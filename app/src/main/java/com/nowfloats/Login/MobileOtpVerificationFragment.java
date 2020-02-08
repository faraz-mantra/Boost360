package com.nowfloats.Login;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.mukesh.OtpView;
import com.thinksity.R;

import io.separ.neural.inputmethod.indic.Constants;

public class MobileOtpVerificationFragment extends Fragment {

    private OnOTPProvidedListener onOTPProvidedListener;
    private CardView nextButton;
    private TextView tvHint;

    private OtpView otpView;

    interface OnOTPProvidedListener {
        void onOTPProvided(String otp);

        String getMobileEntered();
    }

    private MobileOtpVerificationFragment(){}

    public MobileOtpVerificationFragment(OnOTPProvidedListener onOTPProvidedListener) {
        this.onOTPProvidedListener = onOTPProvidedListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mobile_otpverification, container, false);

        nextButton = v.findViewById(R.id.nextButton);
        otpView = v.findViewById(R.id.otp_view);
        tvHint = v.findViewById(R.id.tv_hint_text);

        v.findViewById(R.id.im_back_button).setOnClickListener(view -> {
            getActivity().onBackPressed();
        });


        nextButton.setOnClickListener(view -> {
            String userOtp = otpView.getText().toString();
            onOTPProvidedListener.onOTPProvided(userOtp);
        });

        String mobile = onOTPProvidedListener.getMobileEntered();

        if(!TextUtils.isEmpty(mobile) && mobile.length() == 10) {
            String maskedMobile = mobile.substring(0, 2) + "XXXXXX" + mobile.substring(8, 10);
            tvHint.setText(tvHint.getText() + " "+maskedMobile);
        }

        return v;
    }
}
