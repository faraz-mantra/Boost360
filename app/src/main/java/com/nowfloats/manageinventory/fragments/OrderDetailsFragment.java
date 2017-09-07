package com.nowfloats.manageinventory.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nowfloats.manageinventory.models.OrderModel;
import com.thinksity.R;

public class OrderDetailsFragment extends DialogFragment {

    private OrderModel mOrderDetails;

    public static OrderDetailsFragment newInstance(OrderModel orderModel) {
        OrderDetailsFragment fragment = new OrderDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("order", orderModel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mOrderDetails = getArguments().getParcelable("order");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        getUserAndProducts();
        return inflater.inflate(R.layout.fragment_order_details, container, false);
    }

    private void getUserAndProducts() {


    }

}
