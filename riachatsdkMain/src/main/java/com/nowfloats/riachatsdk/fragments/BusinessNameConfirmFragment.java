package com.nowfloats.riachatsdk.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nowfloats.riachatsdk.R;
import com.nowfloats.riachatsdk.interfaces.IConfirmationCallback;
import com.nowfloats.riachatsdk.utils.Constants;

public class BusinessNameConfirmFragment extends Fragment {
    private static final String ARG_BIZ_NAME = "business_name";

    private IConfirmationCallback mCallBack;
    private String mBizName;


    public BusinessNameConfirmFragment() {
    }

    public static BusinessNameConfirmFragment newInstance(String param1) {
        BusinessNameConfirmFragment fragment = new BusinessNameConfirmFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BIZ_NAME, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof IConfirmationCallback){
            mCallBack = (IConfirmationCallback) activity;
        }else {
            throw new RuntimeException("Not Instance of IConfirmationCallback");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBizName = getArguments().getString(ARG_BIZ_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_business_name_confirm, container, false);

        ((TextView) v.findViewById(R.id.tv_confirmation_text)).setText(mBizName);
        v.findViewById(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.onPositiveResponse(Constants.ConfirmationType.BIZ_NAME, mBizName);
            }
        });

        v.findViewById(R.id.tv_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.onNegativeResponse(Constants.ConfirmationType.BIZ_NAME);
            }
        });

        return v;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

}
