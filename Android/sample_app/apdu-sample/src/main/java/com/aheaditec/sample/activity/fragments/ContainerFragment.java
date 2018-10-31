package com.aheaditec.sample.activity.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.aheaditec.apdu.structures.ContainerBasic;
import com.aheaditec.sample.R;
import com.aheaditec.sample.viewmodel.ApduViewModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Custom dialog fragment for choosing a container to sign data, get public key
 * or certificate.
 */

public class ContainerFragment extends DialogFragment {
    private static final String LISTENER_TAG = "listener";

    private ApduViewModel viewModel;
    private OnContainerSelected listener;
    private List<ContainerBasic> containers;

    public static ContainerFragment newInstance(@NonNull OnContainerSelected listener) {
        ContainerFragment fragment = new ContainerFragment();

        Bundle b = new Bundle();
        b.putSerializable(LISTENER_TAG, listener);

        fragment.setArguments(b);
        return fragment;
    }

    @BindView(R.id.spinnerContainers)
    Spinner containerSpinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null || getActivity() == null) {
            throw new IllegalStateException("getArguments() or getActivity() is null");
        }
        listener = (OnContainerSelected) getArguments().getSerializable(LISTENER_TAG);
        viewModel = ViewModelProviders.of(getActivity()).get(ApduViewModel.class);

        observerContainerLiveData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_containers, container, true);
        ButterKnife.bind(this, view);
        setCancelable(false);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @OnClick(R.id.btnContainersCancel)
    void onCancelButtonClicked() {
        dismiss();
        // this dialog was closed, prevent of opening it again
        viewModel.getDialogFragmentLiveData().postValue(null);
        viewModel.getProgressLiveData().postValue(false);
    }

    /**
     * Method passes selected container into viewModel, where the listener was created
     * and continues APDU operation with selected container.
     */
    @OnClick(R.id.btnContainersContinue)
    void onContinueButtonClicked() {
        listener.onContainerSelected(containers.get(containerSpinner.getSelectedItemPosition()));
        dismiss();
        // this dialog was closed, prevent of opening it again
        viewModel.getDialogFragmentLiveData().postValue(null);
    }

    /**
     * Observer for List<ContainerBasic> containers.
     */
    private void observerContainerLiveData() {
        viewModel.getContainersLiveData().observe(this, this::updateSpinner);
    }

    /**
     * Updates spinner with container names get from the observer.
     */
    private void updateSpinner(List<ContainerBasic> result) {
        this.containers = result;
        if (containers == null) {
            containers = new ArrayList<>();
        }

        String[] strings = new String[containers.size()];
        for (int i = 0; i < containers.size(); i++) {
            strings[i] = containers.get(i).getContainerName();
        }

        if (getActivity() == null) {
            throw new IllegalStateException("getActivity() is null.");
        }
        ArrayAdapter<String> apduAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, strings);
        apduAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        containerSpinner.setAdapter(apduAdapter);
    }

    /**
     * Utility callback which returns selected container from this fragment.
     */
    public interface OnContainerSelected extends Serializable {
        void onContainerSelected(@NonNull ContainerBasic container);
    }
}
