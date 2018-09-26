package com.nowfloats.SellerProfileV2.Fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thinksity.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnAddressSelectedListener} interface
 * to handle interaction events.
 * Use the {@link SellerPofileV2OtherDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SellerPofileV2OtherDetailsFragment extends Fragment {


    private OnAddressSelectedListener mListener;

    public SellerPofileV2OtherDetailsFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static SellerPofileV2OtherDetailsFragment newInstance(OnAddressSelectedListener onAddressSelectedListener) {
        SellerPofileV2OtherDetailsFragment fragment = new SellerPofileV2OtherDetailsFragment();
        fragment.mListener = onAddressSelectedListener;
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_seller_profile_v2_address, container, false);
    }

    public interface OnAddressSelectedListener {
        // TODO: Update argument type and name
        void onAddressSelected(Uri uri);
    }
}
