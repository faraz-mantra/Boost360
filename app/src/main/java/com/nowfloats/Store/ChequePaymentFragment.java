package com.nowfloats.Store;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
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
            showMessage(getString(R.string.enter_bank_name));
        } else if (ifscCodeEt.getText().toString().trim().length() == 0) {
            showMessage(getString(R.string.enter_ifsc_code));
        } else if (accountNumberEt.getText().toString().trim().length() == 0) {
            showMessage(getString(R.string.enter_account_no));
        } else if (transactionAmountEt.getText().toString().trim().length() == 0) {
            showMessage(getString(R.string.enter_cheque_amount));
        } else if (chequeNumberEt.getText().toString().trim().length() == 0) {
            showMessage(getString(R.string.enter_cheque_no));
        } else if (TextUtils.isEmpty(mainImage)) {
            showMessage(getString(R.string.please_attach_cheque_imaeg));
        } else if (TextUtils.isEmpty(paymentDateEt.getText().toString())) {
            showMessage(getString(R.string.please_enter_cheque_date));
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
        int id = v.getId();
        if (id == R.id.textView_add_main) {
            ((OnPaymentOptionClick) mContext).onPickImage(PaymentOptionsActivity.PaymentType.CHEQUE, 10);
        } else if (id == R.id.textView_add_alt) {
            ((OnPaymentOptionClick) mContext).onPickImage(PaymentOptionsActivity.PaymentType.CHEQUE, 11);
        } else {
            super.onClick(v);
        }
    }
}
