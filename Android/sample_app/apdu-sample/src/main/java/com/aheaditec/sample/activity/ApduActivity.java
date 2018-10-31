package com.aheaditec.sample.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.util.Pair;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aheaditec.apdu.structures.PinInfoBasic;
import com.aheaditec.wrapper.Reader;
import com.aheaditec.sample.Logger;
import com.aheaditec.sample.listeners.PinEntryListener;
import com.aheaditec.sample.viewmodel.ApduViewModel;
import com.aheaditec.sample.R;
import com.aheaditec.sample.enums.SpinnerOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Basic activity for testing APDU commands with reader.
 */

public class ApduActivity extends BaseActivity {
    private static final String TAG = ApduActivity.class.getSimpleName();
    private static final String READER_TAG = "reader";

    private TypedArray apduOptions;
    private ApduViewModel viewModel;

    private MaterialDialog progressDialog;
    private MaterialDialog pinDialog;
    private MaterialDialog disconnectDialog;

    @BindView(R.id.spinnerApdu)
    Spinner apduSpinner;

    @BindView(R.id.btnApduRun)
    Button runButton;

    @BindView(R.id.txtApduOutput)
    TextView outputTextView;

    @BindView(R.id.txtApduCardState)
    TextView cardStateText;

    @NonNull
    public static Intent getStartIntent(@NonNull Context context,
                                        @NonNull Reader reader) {
        Intent intent = new Intent(context, ApduActivity.class);
        intent.putExtra(READER_TAG, reader);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(TAG, "onCreate - ApduActivity!");

        if (!getIntent().hasExtra(READER_TAG)) {
            throw new IllegalArgumentException("Activity is missing important argument.");
        }
        Reader reader = getIntent().getParcelableExtra(READER_TAG);

        setContentView(R.layout.activity_apdu);
        ButterKnife.bind(this, this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(reader.getNickname());
        }

        initializeModelView();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (pinDialog != null) {
            pinDialog.dismiss();
        }
        if (disconnectDialog != null) {
            disconnectDialog.dismiss();
        }
    }

    private void initializeModelView() {
        viewModel = ViewModelProviders.of(this).get(ApduViewModel.class);

        observerProgressDialog();
        observerOutputData();
        observerState();
        observerDialogs();
        observerPinEntry();
        observerCardState();
    }

    @OnClick(R.id.btnApduBattery)
    public void onBatteryButtonClicked() {
        viewModel.getBatteryLvl();
    }

    @OnClick(R.id.btnApduRun)
    public void onRunApduButtonClicked() {
        viewModel.runApdu(apduOptions, apduSpinner.getSelectedItemPosition());
    }

    @OnClick(R.id.btnApduClear)
    public void onClearOutputButtonClicked() {
        viewModel.clearOutput();
    }

    private void observerProgressDialog() {
        viewModel.getProgressLiveData().observe(this, dialog -> {
            if (dialog != null && dialog) {
                showProgressDialog();
            } else {
                hideProgressDialog();
            }
        });
    }

    private void observerOutputData() {
        viewModel.getOutputLiveData().observe(this, this::addToOutput);
    }

    private void observerState() {
        viewModel.getStateLiveData().observe(this, state -> {
            if (state == null || state.equals(SpinnerOptions.DISCONNECTED)) {
                openDisconnectDialog();
                return;
            }
            setApduOptions(state);
        });
    }

    private void observerDialogs() {
        viewModel.getDialogFragmentLiveData().observe(this, this::showDialog);
    }

    private void observerPinEntry() {
        viewModel.getPinEntryLiveData().observe(this, this::showPasswordDialog);
    }

    private void observerCardState() {
        viewModel.getCardStateLiveData().observe(this, this::setCardState);
    }

    private void setCardState(boolean cardState) {
        if (cardState) {
            cardStateText.setText("Card inserted.");
            runButton.setEnabled(true);
        } else {
            cardStateText.setText("Card removed.");
            runButton.setEnabled(false);
        }
    }

    private void showDialog(@Nullable DialogFragment fragment) {
        if (fragment == null) {
            return;
        }
        fragment.show(getSupportFragmentManager(), "dialog");
        viewModel.getDialogFragmentLiveData().postValue(null);
        viewModel.getProgressLiveData().postValue(false);
    }

    private void addToOutput(@NonNull String msg) {
        outputTextView.setText(msg);
    }

    private void showProgressDialog() {
        progressDialog = new MaterialDialog.Builder(this)
                .cancelable(false)
                .progress(true, 0)
                .content("Please wait...")
                .show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void setApduOptions(SpinnerOptions mode) {
        String[] strings = new String[0];
        switch (mode) {
            case NO_CARD:
                apduOptions = null;
                break;
            case INITIALIZE_LIST:
                apduOptions = getResources().obtainTypedArray(R.array.initializeList);
                strings = getResources().getStringArray(R.array.initializeList);
                break;
            case NO_CONTAINERS_LIST:
                apduOptions = getResources().obtainTypedArray(R.array.noContainersList);
                strings = getResources().getStringArray(R.array.noContainersList);
                break;
            case COMPLETE_LIST:
                apduOptions = getResources().obtainTypedArray(R.array.fullList);
                strings = getResources().getStringArray(R.array.fullList);
                break;
            default:
                Logger.e(TAG, "Unknown mode in setApduOption.");
                break;
        }

        // spinner initialization
        ArrayAdapter<String> apduAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, strings);

        apduAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        apduSpinner.setAdapter(apduAdapter);
    }

    private void openDisconnectDialog() {
        disconnectDialog = new MaterialDialog.Builder(this)
                .content("You have been disconnected from the reader!")
                .neutralText("Go back")
                .onNeutral((dialog, which) -> finish()).cancelable(false)
                .build();
        disconnectDialog.show();
    }

    private void showPasswordDialog(@Nullable Pair<PinInfoBasic, PinEntryListener> pinEntry) {

        if (pinEntry == null || pinEntry.first == null || pinEntry.second == null) {
            if (pinDialog != null && pinDialog.isShowing()) {
                pinDialog.dismiss();
            }
            return;
        }
        PinInfoBasic pinReference = pinEntry.first;
        PinEntryListener listener = pinEntry.second;

        pinDialog = new MaterialDialog.Builder(this)
                .title(pinEntry.first.getPinReference().name() + " entry:")
                .input("Enter your " + pinReference.getPinReference().name() + ", please:",
                        "11111", (dialog, input) -> {
                            // nothing to do
                        })
                .inputRange(pinReference.getPinMinimumLength(), pinReference.getPinMaximumLength())
                .positiveText("Ok")
                .onPositive((dialog, which) -> {
                    listener.onPinEntered(dialog.getInputEditText().getText().toString().getBytes());
                    // sets null to not show this dialog again (in case of configuration change)
                    viewModel.getPinEntryLiveData().postValue(null);
                })
                .cancelable(true)
                .cancelListener(dialogInterface -> viewModel.getPinEntryLiveData().postValue(null))
                .build();

        pinDialog.show();
    }
}
