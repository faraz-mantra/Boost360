package com.nowfloats.Login;

import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.mukesh.OtpView;
import com.thinksity.R;

public class MobileOtpVerificationFragment extends Fragment {

    private final String LOG_TAG = "BOOST";

    private OnOTPProvidedListener onOTPProvidedListener;
    private CardView nextButton;
    private TextView tvHint, tvResend;
    private View tvResendUnderline;

    private OtpView otpView;

    public interface OnOTPProvidedListener {
        void onOTPProvided(String otp);
        void onResend(String phoneNumber);

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
            String maskedMobile = mobile.substring(0, 2) + getString(R.string.xxxxxxx) + mobile.substring(8, 10);
            tvHint.setText(tvHint.getText() + " "+maskedMobile);
        }

        startOTPResendOperation(v);
        return v;
    }

    private void startOTPResendOperation(View v) {

        tvResend = v.findViewById(R.id.resend_tv);
        tvResendUnderline = v.findViewById(R.id.resend_underline);
        tvResendUnderline.setVisibility(View.GONE);

        new Handler().postDelayed(() -> {
            String resendValue = getString(R.string.didnt_get_the_code);
            SpannableString resendString = new SpannableString(resendValue);
            resendString.setSpan(new UnderlineSpan(),resendValue.length() - 6, resendValue.length(),0);
            tvResend.setText(resendString);
//            tvResendUnderline.setVisibility(View.VISIBLE);
        }, 30000);


        tvResend.setOnClickListener(view -> {
            onOTPProvidedListener.onResend(onOTPProvidedListener.getMobileEntered());
        });


    }
}
