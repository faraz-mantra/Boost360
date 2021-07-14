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
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Admin on 13-04-2018.
 */

public class BankTransferFragment extends ImagesPaymentFragment {

    public static Fragment getInstance(Bundle b) {
        Fragment frag = new BankTransferFragment();
        frag.setArguments(b);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_bank_transfer_option, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public boolean validateAllFields() {
        if (accountNumberEt.getText().toString().trim().length() == 0) {
            showMessage(getString(R.string.enter_account_number));
        } else if (transactionAmountEt.getText().toString().trim().length() == 0) {
            showMessage(getString(R.string.enter_transaction_amount));
        } else if (transactionIdEt.getText().toString().trim().length() == 0) {
            showMessage(getString(R.string.enter_transaction_id));
        } else if (TextUtils.isEmpty(mainImage)) {
            showMessage(getString(R.string.attach_receipt_image));
        } else {

            SimpleDateFormat formatter = new SimpleDateFormat(Methods.YYYY_MM_DD, Locale.ENGLISH);
            Calendar calendar = Calendar.getInstance();
            String s = formatter.format(calendar.getTime());
            paymentDateEt.setText(s);

            paymentMode = InitiateModel.PAYMENT_MODE.NEFT.ordinal();
            return super.validateAllFields();
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null)
            getActivity().setTitle(getString(R.string.bank_transfer_payment));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.textView_add_main) {
            ((OnPaymentOptionClick) mContext).onPickImage(PaymentOptionsActivity.PaymentType.BANK_TRANSFER, 10);
        } else if (id == R.id.textView_add_alt) {
            ((OnPaymentOptionClick) mContext).onPickImage(PaymentOptionsActivity.PaymentType.BANK_TRANSFER, 11);
        } else {
            super.onClick(v);
        }
    }
}
