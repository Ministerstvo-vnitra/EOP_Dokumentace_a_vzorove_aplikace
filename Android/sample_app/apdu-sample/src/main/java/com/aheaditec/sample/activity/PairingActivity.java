package com.aheaditec.sample.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aheaditec.wrapper.Reader;
import com.aheaditec.sample.Logger;
import com.aheaditec.sample.enums.ReaderType;
import com.aheaditec.sample.viewmodel.PairingViewModel;
import com.aheaditec.sample.R;
import com.aheaditec.sample.adapters.PairedReadersAdapter;
import com.aheaditec.sample.adapters.ScanReadersAdapter;
import com.aheaditec.sample.viewmodel.architecture.ReaderViewModelFactory;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.aheaditec.sample.enums.ReaderType.EXTRA_NAME;


/**
 * Basic activity for pairing with devices.
 */

public class PairingActivity extends BaseActivity {
    private static final String TAG = PairingActivity.class.getSimpleName();

    private PairingViewModel viewModel;
    private MaterialDialog progressDialog;
    private MaterialDialog pairingDone;
    private MaterialDialog pairingDialog;
    private MaterialDialog renameDialog;
    private AlertDialog errorDialog;

    @NonNull
    public static Intent getStartIntent(@NonNull Context context, ReaderType readerType) {
        Intent intent = new Intent(context, PairingActivity.class);
        intent.putExtra(EXTRA_NAME, readerType);
        return intent;
    }

    @BindView(R.id.recyclerPairingPaired)
    RecyclerView pairedDevicesRecycler;

    @BindView(R.id.recyclerPairingAvailable)
    RecyclerView scannedDevicesRecycler;

    @BindView(R.id.progressPairing)
    ProgressBar scanProgressBar;

    @BindView(R.id.btnPairingScan)
    Button scanButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(TAG, "onCreate - PairingActivity");

        if (!getIntent().hasExtra(EXTRA_NAME)) {
            throw new IllegalArgumentException("Activity is missing important argument.");
        }
        ReaderType readerType = (ReaderType) getIntent().getSerializableExtra(EXTRA_NAME);

        setContentView(R.layout.activity_pairing);
        ButterKnife.bind(this, this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("You are using " + readerType + " plugin");
        }

        initializePairing(readerType);
        viewModel.initPairing();
        scanProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (pairingDone != null) {
            pairingDone.dismiss();
        }
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (pairingDialog != null) {
            pairingDialog.dismiss();
        }
        if (errorDialog != null) {
            errorDialog.dismiss();
        }
        if (renameDialog != null) {
            renameDialog.dismiss();
        }
    }

    private void initializePairing(@NonNull ReaderType readerType) {
        viewModel = ViewModelProviders.of(this, new ReaderViewModelFactory(this.getApplication(), readerType)).get(PairingViewModel.class);

        observerPairedReaders();
        observerSearchLiveData();
        observerProgressDialog();
        observerScanProgressDialog();
        observerPairingDialog();
        observerPairingDoneDialog();
        observerErrorDialog();
        observerRenameDialog();
    }

    @OnClick(R.id.btnPairingScan)
    public void startPairing() {
        viewModel.startScan();
    }

    private void observerSearchLiveData() {
        viewModel.getScanLiveData().observe(this, readers -> {
            if (readers == null) {
                readers = new ArrayList<>();
            }
            updateScanList(readers);
        });
    }

    private void observerPairedReaders() {
        viewModel.getPairedReadersLiveData().observe(this, readers -> {
            if (readers == null) {
                readers = new ArrayList<>();
            }
            updatePairedList(readers);
        });
    }

    private void observerProgressDialog() {
        viewModel.getProgressBarLiveData().observe(this, dialog -> {
            if (dialog != null && dialog) {
                startProgress();
            } else {
                stopProgress();
            }
        });
    }

    private void observerPairingDoneDialog() {
        viewModel.getPairingDoneLiveData().observe(this, dialog -> {
            if (dialog != null && dialog) {
                showPairingDoneDialog();
            }
        });
    }

    private void observerScanProgressDialog() {
        viewModel.getScanProgressBarLiveData().observe(this, dialog -> {
            if (dialog != null && dialog) {
                scanProgressBar.setVisibility(View.VISIBLE);
                scanButton.setEnabled(false);
            } else {
                scanProgressBar.setVisibility(View.INVISIBLE);
                scanButton.setEnabled(true);
            }
        });
    }

    private void observerPairingDialog() {
        viewModel.getPairingDialogLiveData().observe(this, reader -> {
            if (reader != null) {
                showPairingDialog(reader);
            }
        });
    }

    private void observerErrorDialog() {
        viewModel.getErrorLiveData().observe(this, error -> {
            if (error != null) {
                showAlertDialog(error.getMessage());
            }
        });
    }

    private void observerRenameDialog() {
        viewModel.getRenameDialogLiveData().observe(this, dialog -> {
            if (dialog != null) {
                showRenameDialog(dialog);
            }
        });
    }

    private void updatePairedList(@NonNull List<Reader> readers) {

        pairedDevicesRecycler.setLayoutManager(new LinearLayoutManager(this));
        pairedDevicesRecycler.setAdapter(new PairedReadersAdapter(readers, new PairedReadersAdapter.ViewClickAdapter() {
            @Override
            public void onUnpairClick(Reader reader) {
                viewModel.unpairReader(reader);
            }

            @Override
            public void onRenameClick(Reader reader) {
                viewModel.showRenameDialog(reader);
            }
        }));
    }

    private void updateScanList(@NonNull List<Reader> readers) {
        scannedDevicesRecycler.setLayoutManager(new LinearLayoutManager(this));
        scannedDevicesRecycler.setAdapter(new ScanReadersAdapter(readers, reader -> viewModel.pairWithReaderDialog(reader)));
    }

    private void startProgress() {
        progressDialog = new MaterialDialog.Builder(this)
                .cancelable(true)
                .progress(true, 0)
                .content("Please wait...")
                .show();
    }

    private void stopProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    private void showPairingDialog(@NonNull Reader reader) {
        pairingDialog = new MaterialDialog.Builder(this)
                .title("Pairing with reader")
                .content("Pair with reader " + reader.getNickname())
                .positiveText("Pair")
                .onPositive((dialog, which) -> {
                    viewModel.stopScan();
                    viewModel.pairWithReader(reader);
                })
                .negativeText("Cancel")
                .onNegative((dialog, which) -> viewModel.pairWithReaderDialog(null))
                .cancelable(false)
                .build();
        pairingDialog.show();
    }

    private void showAlertDialog(@NonNull String msg) {
        errorDialog = new AlertDialog.Builder(this)
                .setMessage(msg)
                .setTitle("ApduSDK error!")
                .setCancelable(true)
                .setOnCancelListener(dialog -> viewModel.errorDialogClosed())
                .setNeutralButton("Ok", (dialog, which) -> viewModel.errorDialogClosed())
                .create();
        errorDialog.show();
    }

    private void showRenameDialog(@NonNull Reader reader) {
        renameDialog = new MaterialDialog.Builder(this)
                .title("Change name of " + reader.getNickname() + ":")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("new name", null, false, (dialog, input) ->
                        viewModel.renameReader(reader, input.toString()))
                .inputRange(1, 10)
                .cancelable(true)
                .build();
        renameDialog.show();
    }

    private void showPairingDoneDialog() {
        pairingDone = new MaterialDialog.Builder(this)
                .content("Reader successfully paired.")
                .cancelable(true)
                .cancelListener(dialogInterface -> viewModel.pairDoneDialogClosed())
                .positiveText("Ok")
                .onPositive((dialog, which) -> viewModel.pairDoneDialogClosed())
                .build();

        pairingDone.show();
    }
}
