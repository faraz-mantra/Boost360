package com.nowfloats.Store;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nowfloats.Store.Model.InitiateModel;
import com.nowfloats.Store.Service.OnPaymentOptionClick;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Admin on 13-04-2018.
 */

public class ChequePaymentFragment extends ImagesPaymentFragment {

    public static Fragment getInstance(Bundle b) {
        Fragment frag = new ChequePaymentFragment();
        frag.setArguments(b);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_cheque_option, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null)
            getActivity().setTitle("Cheque Payment");
    }

    public boolean validateAllFields() {
        if (bankNameEt.getText().toString().trim().length() == 0) {
            showMessage("Enter bank name");
        } else if (ifscCodeEt.getText().toString().trim().length() == 0) {
            showMessage("Enter ifsc code");
        } else if (accountNumberEt.getText().toString().trim().length() == 0) {
            showMessage("Enter account number");
        } else if (transactionAmountEt.getText().toString().trim().length() == 0) {
            showMessage("Enter cheque amount");
        } else if (chequeNumberEt.getText().toString().trim().length() == 0) {
            showMessage("Enter cheque number");
        } else if (TextUtils.isEmpty(mainImage)) {
            showMessage("Please attach cheque image");
        } else if (TextUtils.isEmpty(paymentDateEt.getText().toString())) {
            showMessage("Please enter cheque date");
        } else {
            paymentMode = InitiateModel.PAYMENT_MODE.CHEQUE.ordinal();
            try {
                if (Methods.getDateDifference(new Date(), new SimpleDateFormat(Methods.YYYY_MM_DD).
                        parse(paymentDateEt.getText().toString())) > 0) {
                    paymentMode = InitiateModel.PAYMENT_MODE.PDC.ordinal();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return super.validateAllFields();
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView_add_main:
                ((OnPaymentOptionClick) mContext).onPickImage(PaymentOptionsActivity.PaymentType.CHEQUE, 10);
                break;
            case R.id.textView_add_alt:
                ((OnPaymentOptionClick) mContext).onPickImage(PaymentOptionsActivity.PaymentType.CHEQUE, 11);
                break;
            default:
                super.onClick(v);
        }
    }
}
