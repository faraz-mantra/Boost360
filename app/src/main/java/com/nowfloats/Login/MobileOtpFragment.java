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
    private View mobileOtpBottomView;

    public interface OnMobileProvidedListener {
        void onMobileProvided(String mobileNumber);
    }

    private MobileOtpFragment() {
    }

    public MobileOtpFragment(OnMobileProvidedListener onMobileProvidedListener) {
        this.onMobileProvidedListener = onMobileProvidedListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mobile_otp, container, false);
        EditText etPhoneNumber = v.findViewById(R.id.phoneNumber);
        cvNextButton = v.findViewById(R.id.nextButton);
        mobileOtpBottomView = v.findViewById(R.id.mobile_otp_bottom_view);

        v.findViewById(R.id.im_back_button).setOnClickListener(view -> {
            getActivity().onBackPressed();
        });

        cvNextButton.setOnClickListener(view -> {
            Methods.hideKeyboard(getContext());
            String phoneNumber = etPhoneNumber.getText().toString();
            if (!Methods.validPhoneNumber(phoneNumber)) {
                Methods.showSnackBarNegative(getActivity(), getString(R.string.enter_a_valid_number));
                return;
            }
            onMobileProvidedListener.onMobileProvided(phoneNumber);
        });

        etPhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ViewGroup.LayoutParams lp = mobileOtpBottomView.getLayoutParams();
                if (hasFocus) {
                    lp.height = 5;
                } else {
                    lp.height = 2;
                }
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                mobileOtpBottomView.setLayoutParams(lp);
            }
        });

        etPhoneNumber.requestFocus();

        return v;
    }
}
