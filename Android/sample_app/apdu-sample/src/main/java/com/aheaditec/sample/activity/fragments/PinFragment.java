package com.aheaditec.sample.activity.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.aheaditec.apdu.enums.VerifyCodes;
import com.aheaditec.sample.R;
import com.aheaditec.sample.viewmodel.ApduViewModel;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Custom dialog fragment to choose PIN reference object.
 */

public class PinFragment extends DialogFragment {
    private static final String LISTENER_TAG = "listener";

    private OnPinReferenceSelected listener;
    private ApduViewModel viewModel;

    public static PinFragment newInstance(@NonNull OnPinReferenceSelected listener) {
        PinFragment fragment = new PinFragment();

        Bundle b = new Bundle();
        b.putSerializable(LISTENER_TAG, listener);

        fragment.setArguments(b);
        return fragment;
    }

    @BindView(R.id.radioGroupPin)
    RadioGroup pinReferenceRadioGroup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null || getActivity() == null) {
            throw new IllegalStateException("getArguments() or getActivity() is null.");
        }
        listener = (OnPinReferenceSelected) getArguments().getSerializable(LISTENER_TAG);
        viewModel = ViewModelProviders.of(getActivity()).get(ApduViewModel.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

        dialog.dismiss();
        viewModel.getProgressLiveData().postValue(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pin, container, true);
        ButterKnife.bind(this, view);
        setCancelable(true);
        return view;
    }

    /**
     * Method passes selected PIN reference into viewModel, where the listener was created
     * and continues APDU operation with it.
     */
    @OnClick(R.id.btnPinContinue)
    void onContinueButtonClicked() {
        int selectedObject = pinReferenceRadioGroup.getCheckedRadioButtonId();
        if (selectedObject == -1) {
            Toast.makeText(getActivity(), "You must choose a PIN reference.", Toast.LENGTH_LONG).show();
            return;
        }

        VerifyCodes pinReference;
        switch (selectedObject) {
            case R.id.radioBtnPinPin:
                pinReference = VerifyCodes.PIN;
                break;
            case R.id.radioBtnPinPuk:
                pinReference = VerifyCodes.PUK;
                break;
            case R.id.radioBtnPinQpin:
                pinReference = VerifyCodes.QPIN;
                break;
            default:
                dismiss();
                return;
        }
        listener.onPinReferenceSelected(pinReference);
        viewModel.getProgressLiveData().postValue(true);
        dismiss();
    }

    /**
     * Utility callback which returns selected PIN reference from this fragment.
     */
    public interface OnPinReferenceSelected extends Serializable {
        void onPinReferenceSelected(@NonNull VerifyCodes pinReference);
    }
}
