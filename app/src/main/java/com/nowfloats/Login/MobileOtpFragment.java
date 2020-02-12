package com.nowfloats.Login;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.nowfloats.util.Methods;
import com.thinksity.R;

public class MobileOtpFragment extends Fragment {

    private OnMobileProvidedListener onMobileProvidedListener;
    private CardView cvNextButton;

    public interface OnMobileProvidedListener {
        void onMobileProvided(String mobileNumber);
    }

    private MobileOtpFragment(){}

    public MobileOtpFragment(OnMobileProvidedListener onMobileProvidedListener) {
        this.onMobileProvidedListener = onMobileProvidedListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mobile_otp, container, false);
        EditText etPhoneNumber = v.findViewById(R.id.phoneNumber);
        cvNextButton = v.findViewById(R.id.nextButton);

        v.findViewById(R.id.im_back_button).setOnClickListener(view -> {
            getActivity().onBackPressed();
        });

        cvNextButton.setOnClickListener(view -> {
            String phoneNumber = etPhoneNumber.getText().toString();
            if(!Methods.validPhoneNumber(phoneNumber)) {
                Methods.showSnackBarNegative(getActivity(), "Enter valid number");
                return;
            }
            onMobileProvidedListener.onMobileProvided(phoneNumber);
        });

        etPhoneNumber.requestFocus();

        return v;
    }
}
