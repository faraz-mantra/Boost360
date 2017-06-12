package com.nowfloats.riachatsdk.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nowfloats.riachatsdk.R;
import com.nowfloats.riachatsdk.interfaces.IConfirmationCallback;
import com.nowfloats.riachatsdk.utils.Constants;
import com.nowfloats.riachatsdk.utils.Utils;


public class AddressCardFragment extends Fragment {
    private static final String PARAM_ADDR = "address";
    private static final String PARAM_LAT = "lattitude";
    private static final String PARAM_LONG = "longitude";

    private String mAddress;
    private String mLatitiude;
    private String mLongitude;

    private IConfirmationCallback mCallback;

    public AddressCardFragment() {
    }

    public static AddressCardFragment newInstance(String address, String lattitude, String longitude) {
        AddressCardFragment fragment = new AddressCardFragment();
        Bundle args = new Bundle();
        args.putString(PARAM_ADDR, address);
        args.putString(PARAM_LAT, lattitude);
        args.putString(PARAM_LONG, longitude);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof IConfirmationCallback){
            mCallback = (IConfirmationCallback) activity;
        }else {
            throw new RuntimeException("Callback not implemented");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAddress = getArguments().getString(PARAM_ADDR);
            mLatitiude = getArguments().getString(PARAM_LAT);
            mLongitude = getArguments().getString(PARAM_LONG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_address_card, container, false);
        final String url = Utils.getMapUrlFromLocation(mLatitiude, mLongitude);
        ImageView ivMap = (ImageView) v.findViewById(R.id.iv_map_data);
        ((TextView) v.findViewById(R.id.tv_address_text)).setText(mAddress);
        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.default_product_image)
                .error(R.drawable.default_product_image)
                .into(ivMap);
        v.findViewById(R.id.tv_addr_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onPositiveResponse(Constants.ConfirmationType.ADDRESS_ENTRY, mAddress, url);
            }
        });

        v.findViewById(R.id.tv_addr_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onNegativeResponse(Constants.ConfirmationType.ADDRESS_ENTRY);
            }
        });
        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
