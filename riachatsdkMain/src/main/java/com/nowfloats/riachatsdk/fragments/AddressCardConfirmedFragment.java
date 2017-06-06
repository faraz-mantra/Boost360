package com.nowfloats.riachatsdk.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nowfloats.riachatsdk.R;


public class AddressCardConfirmedFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ADDR = "address";
    private static final String MAP_URL = "mapurl";

    // TODO: Rename and change types of parameters
    private String mAddress;
    private String mMapUrl;

    public AddressCardConfirmedFragment() {
        // Required empty public constructor
    }

    public static AddressCardConfirmedFragment newInstance(String address, String mapUrl) {
        AddressCardConfirmedFragment fragment = new AddressCardConfirmedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ADDR, address);
        args.putString(MAP_URL, mapUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAddress = getArguments().getString(ARG_ADDR);
            mMapUrl = getArguments().getString(MAP_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_address_card_confirmed, container, false);
        ((TextView) v.findViewById(R.id.tv_confirmation_text)).setText(mAddress);
        ImageView ivMap = (ImageView) v.findViewById(R.id.iv_map_data);
        Glide.with(this)
                .load(mMapUrl)
                .placeholder(R.drawable.default_product_image)
                .error(R.drawable.default_product_image)
                .into(ivMap);
        return  v;
    }
}
