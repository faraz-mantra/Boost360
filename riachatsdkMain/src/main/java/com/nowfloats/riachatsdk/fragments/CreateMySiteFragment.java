package com.nowfloats.riachatsdk.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nowfloats.riachatsdk.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NowFloats on 27-03-2017 by Romio Ranjan Jena.
 */

public class CreateMySiteFragment extends DialogFragment {

    private static final String ARG_MAP_DATA = "map_data";

    private OnResultReceive mResultListener;

    private Map<String, String> mDataMap = new HashMap<>();


    public static CreateMySiteFragment newInstance(HashMap<String, String> mDataMap) {

        CreateMySiteFragment fragment = new CreateMySiteFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARG_MAP_DATA, mDataMap);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDataMap = (Map<String, String>) getArguments().get(ARG_MAP_DATA);
        }

    }

    public void setResultListener(OnResultReceive onResultReceive) {
        mResultListener = onResultReceive;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.chat_submit_form_layout, container, false);
        setCancelable(false);

        TextView tvBusinessName = (TextView) v.findViewById(R.id.tvBusinessName);
        TextView tvPhoneNumber = (TextView) v.findViewById(R.id.tvPhoneNumber);
        TextView tvCategory = (TextView) v.findViewById(R.id.tvCategory);
        TextView tvWebsiteURL = (TextView) v.findViewById(R.id.tvWebsiteURL);
        TextView tvAddress = (TextView) v.findViewById(R.id.tvAddress);
        TextView tvEmailAddress = (TextView) v.findViewById(R.id.tvEmailAddress);


        tvBusinessName.setText(mDataMap.get("[~BUSINESS_NAME]"));
        tvPhoneNumber.setText(mDataMap.get("[~PHONE]"));
        tvCategory.setText(mDataMap.get("[~BUSINESS_CATEGORY]"));
        tvAddress.setText(mDataMap.get("[~STREET_ADDRESS]") + "," + mDataMap.get("[~CITY]") + "," + mDataMap.get("[~COUNTRY]"));
        tvWebsiteURL.setText(mDataMap.get("[~WEBSITE_URL]"));
        tvEmailAddress.setText(mDataMap.get("[~EMAIL]"));

        Button btnCreateWebsite = (Button) v.findViewById(R.id.btnCreateWebsite);
        btnCreateWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResultListener.OnResult();
            }
        });

        try {
            getDialog().getWindow().setBackgroundDrawableResource(R.drawable.place_pick_dialog_bg);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return v;
    }

    public interface OnResultReceive {
        void OnResult();
    }

}
