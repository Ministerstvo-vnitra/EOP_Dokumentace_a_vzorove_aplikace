package com.aheaditec.sample.activity;

import static com.aheaditec.sample.enums.ReaderType.EXTRA_NAME;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.aheaditec.sample.R;
import com.aheaditec.sample.adapters.ConnectReadersAdapter;
import com.aheaditec.sample.enums.ReaderType;
import com.aheaditec.sample.viewmodel.ConnectViewModel;
import com.aheaditec.sample.viewmodel.architecture.ReaderViewModelFactory;
import com.aheaditec.wrapper.Reader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Activity used to connect to specified brand of reader (ACS, Gemalto, Feitian).
 */

public class ConnectActivity extends BaseActivity {

    private ReaderType readerType;
    private ConnectViewModel viewModel;
    private MaterialDialog progressDialog;
    private MaterialDialog alertDialog;

    @BindView(R.id.recyclerConnectScan)
    RecyclerView scanRecycler;

    @BindView(R.id.btnConnectScan)
    Button startScan;

    @BindView(R.id.progressConnect)
    ProgressBar progressBar;

    @NonNull
    public static Intent getStartIntent(@NonNull Context context, ReaderType readerType) {
        Intent intent = new Intent(context, ConnectActivity.class);
        intent.putExtra(EXTRA_NAME, readerType);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (!getIntent().hasExtra(EXTRA_NAME)) {
            throw new IllegalArgumentException("Activity is missing important argument.");
        }
        readerType = (ReaderType) getIntent().getSerializableExtra(EXTRA_NAME);
        isBluetoothReader = readerType != ReaderType.USB;
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_connect);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("You are using " + readerType + " plugin");
        }
        if (checkBluetoothAndLocation()) {
            setUpViewModel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (viewModel != null) {
            setReadersToView(viewModel.getScanLiveData().getValue());
        }
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
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    @OnClick(R.id.btnConnectPair)
    public void onPairButtonClicked() {
        startActivity(PairingActivity.getStartIntent(this, readerType));
        viewModel.stopScanning();
        progressBar.setVisibility(View.INVISIBLE);
        startScan.setEnabled(true);
    }

    @OnClick(R.id.btnConnectScan)
    public void onStartScanButtonClicked() {
        viewModel.startScanning();
    }

    private void setUpViewModel() {
        viewModel = ViewModelProviders.of(this, new ReaderViewModelFactory(getApplication(), readerType))
                .get(ConnectViewModel.class);

        observerScanLiveData();
        observerErrorLiveData();
        observerProgressLiveData();
        observerIsConnectedLiveData();
    }

    private void observerIsConnectedLiveData() {
        viewModel.getIsConnectedLiveData().observe(this, reader -> {
            if (reader == null) {
                progressBar.setVisibility(View.INVISIBLE);
                startScan.setVisibility(View.VISIBLE);
                scanRecycler.setVisibility(View.VISIBLE);
            } else {
                startActivity(ApduActivity.getStartIntent(this, reader));
            }
        });
    }

    private void observerErrorLiveData() {
        viewModel.getErrorLiveData().observe(this, this::handleErrorDialog);
    }

    private void observerScanLiveData() {
        viewModel.getScanLiveData().observe(this, this::setReadersToView);
    }

    private void observerProgressLiveData() {
        viewModel.getProgressLiveData().observe(this, progress -> {
            if (progress == null || !progress) {
                hideProgressDialog();
            } else {
                showProgressDialog();
            }
        });
    }

    private void setReadersToView(@Nullable List<Reader> readers) {
        if (readers == null) {
            progressBar.setVisibility(View.INVISIBLE);
            startScan.setEnabled(true);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        startScan.setEnabled(false);
        List<Reader> pairedDevices = viewModel.getPairedDevices();
        List<Reader> temp = new ArrayList<>();
        for (Reader reader : pairedDevices) {
            if (readers.contains(reader)) {
                temp.add(reader);
            }
        }

        scanRecycler.setLayoutManager(new LinearLayoutManager(this));
        scanRecycler.setAdapter(new ConnectReadersAdapter(temp, reader -> {
            viewModel.connectToReader(reader);
            viewModel.stopScanning();
        }));
    }

    private void handleErrorDialog(String msg) {
        if (msg == null) {
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
            return;
        }
        alertDialog = new MaterialDialog.Builder(this)
                .title("Error occurred.")
                .content(msg)
                .cancelable(true)
                .cancelListener(dialogInterface -> viewModel.getErrorLiveData().postValue(null))
                .build();
        alertDialog.show();
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
}
