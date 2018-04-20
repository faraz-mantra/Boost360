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

import com.thinksity.R;

/**
 * Created by Admin on 13-04-2018.
 */

public class BankTransferFragment extends Fragment implements View.OnClickListener {

    private Context mContext;
    private TextInputEditText bankNameEt, chequeAmountEt, chequeNumberEt, accountNumberEt,
            ifscCodeEt, paymentDateEt, gstNumberEt;
    private RadioGroup gstRadioGroup;
    private ImageView altChequeImgView, mainChequeImgView;
    private int requestCode = 101;

    public static Fragment getInstance(Bundle b){
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
    public void onResume() {
        super.onResume();
        if (getActivity() != null)
            getActivity().setTitle("Bank Transfer Payment");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
        bankNameEt.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == this.requestCode){

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.editText_bank_name:
                //getBankNames();
                break;
            case R.id.textView_add_main:
                break;
            case R.id.textView_add_alt:
                break;

        }
    }
}
