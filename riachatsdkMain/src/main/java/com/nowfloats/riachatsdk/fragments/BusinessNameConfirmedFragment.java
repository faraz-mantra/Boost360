package com.nowfloats.riachatsdk.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nowfloats.riachatsdk.R;
import com.nowfloats.riachatsdk.interfaces.IConfirmationCallbackInterface;

public class BusinessNameConfirmedFragment extends Fragment {

    private static final String ARG_BIZ_NAME = "business_name";
    private IConfirmationCallbackInterface mCallBack;
    private String mBizName;


    public BusinessNameConfirmedFragment() {
    }

    public static BusinessNameConfirmedFragment newInstance(String param1) {
        BusinessNameConfirmedFragment fragment = new BusinessNameConfirmedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BIZ_NAME, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof IConfirmationCallbackInterface){
            mCallBack = (IConfirmationCallbackInterface) activity;
        }else {
            throw new RuntimeException("Not Instance of IConfirmationCallbackInterface");
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
        View v =  inflater.inflate(R.layout.fragment_business_name_confirmed, container, false);

        ((TextView) v.findViewById(R.id.tv_confirmed_business_text)).setText(mBizName);


        return v;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }
}
