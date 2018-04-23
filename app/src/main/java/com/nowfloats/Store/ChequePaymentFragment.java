package com.nowfloats.Store;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nowfloats.Store.Service.OnPaymentOptionClick;
import com.thinksity.R;

/**
 * Created by Admin on 13-04-2018.
 */

public class ChequePaymentFragment extends ImagesPaymentFragment {

    public static Fragment getInstance(Bundle b){
        Fragment frag = new ChequePaymentFragment();
        frag.setArguments(b);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_cheque_option, container,false);
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

    public boolean validateAllFields(){
        if (bankNameEt.getText().toString().trim().length() == 0){
            showMessage("Enter bank name");
        }else if (ifscCodeEt.getText().toString().trim().length() == 0){
            showMessage("Enter ifsc code");
        }else if (accountNumberEt.getText().toString().trim().length() == 0){
            showMessage("Enter account number");
        }else if(transactionAmountEt.getText().toString().trim().length() == 0){
            showMessage("Enter cheque amount");
        }else if (chequeNumberEt.getText().toString().trim().length() == 0){
            showMessage("Enter cheque number");
        }else if(TextUtils.isEmpty(mainImage)){
            showMessage("Please attach cheque image");
        }else{
            return super.validateAllFields();
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textView_add_main:
                ((OnPaymentOptionClick)mContext).onPickImage(PaymentOptionsActivity.PaymentType.CHEQUE, 10);
                break;
            case R.id.textView_add_alt:
                ((OnPaymentOptionClick)mContext).onPickImage(PaymentOptionsActivity.PaymentType.CHEQUE, 11);
                break;
            default:
                super.onClick(v);
        }
    }
}
