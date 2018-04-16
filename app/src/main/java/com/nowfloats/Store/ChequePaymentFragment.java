package com.nowfloats.Store;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.nowfloats.Store.Service.OnPaymentOptionClick;
import com.nowfloats.util.Methods;
import com.thinksity.R;

/**
 * Created by Admin on 13-04-2018.
 */

public class ChequePaymentFragment extends Fragment implements View.OnClickListener {

    private Context mContext;
    private TextInputEditText bankNameEt, chequeAmountEt, chequeNumberEt, accountNumberEt,
            ifscCodeEt, paymentDateEt, gstNumberEt;
    private RadioGroup gstRadioGroup;
    private ImageView altChequeImgView, mainChequeImgView;

    public static Fragment getInstance(Bundle b){
        Fragment frag = new ChequePaymentFragment();
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arg = getArguments();
        if (arg != null){

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_cheque_option, container,false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null)
        getActivity().setTitle("Cheque Payment");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isAdded() || getActivity() == null) return;

        bankNameEt = view.findViewById(R.id.editText_bank_name);
        ifscCodeEt = view.findViewById(R.id.editText_ifsc_code);
        accountNumberEt = view.findViewById(R.id.editText_account_number);
        chequeNumberEt = view.findViewById(R.id.editText_cheque_number);
        chequeAmountEt = view.findViewById(R.id.editText_cheque_amount);
        paymentDateEt = view.findViewById(R.id.editText_payment_date);
        gstNumberEt = view.findViewById(R.id.editText_gst_number);
        final TextInputLayout gstLayout = view.findViewById(R.id.inputLayout_gst);
        gstRadioGroup = view.findViewById(R.id.rg_is_gst);
        view.findViewById(R.id.imageView_primary_cheque);
        altChequeImgView = view.findViewById(R.id.imageView_alternate);
        gstRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()){
                    case R.id.radioButton_yes:
                        gstLayout.setVisibility(View.VISIBLE);
                        break;
                    default:
                        gstLayout.setVisibility(View.GONE);
                        break;
                }
            }
        });
        bankNameEt.setFocusable(false);
        bankNameEt.setFocusableInTouchMode(false);
        bankNameEt.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void validateAllFields(){
        if (bankNameEt.getText().toString().trim().length() == 0){
            Methods.showSnackBarNegative(getActivity(), "Enter bank name");
        }else if (ifscCodeEt.getText().toString().trim().length() == 0){
            Methods.showSnackBarNegative(getActivity(), "Enter ifsc code");
        }else if (accountNumberEt.getText().toString().trim().length() == 0){
            Methods.showSnackBarNegative(getActivity(), "Enter account number");
        }else if (chequeAmountEt.getText().toString().trim().length() == 0){
            Methods.showSnackBarNegative(getActivity(), "Enter cheque amount");
        }else if (chequeNumberEt.getText().toString().trim().length() == 0){
            Methods.showSnackBarNegative(getActivity(), "Enter cheque number");
        }else if (paymentDateEt.getText().toString().trim().length() == 0){
            Methods.showSnackBarNegative(getActivity(), "Enter payment date");
        }else  if (gstRadioGroup.getCheckedRadioButtonId() == R.id.radioButton_yes && gstNumberEt.getText().toString().trim().length() == 0){
            Methods.showSnackBarNegative(getActivity(), "Enter GST number");
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.editText_bank_name:
                getBankNames();
                break;
            case R.id.textView_add_cheque:
                ((OnPaymentOptionClick)mContext).onPickImage(PaymentOptionsActivity.PaymentType.CHEQUE, 11);
                break;
            case R.id.textView_add_alt_cheque:
                ((OnPaymentOptionClick)mContext).onPickImage(PaymentOptionsActivity.PaymentType.CHEQUE, 11);
                break;

        }
    }

    private void getBankNames() {

    }

}
