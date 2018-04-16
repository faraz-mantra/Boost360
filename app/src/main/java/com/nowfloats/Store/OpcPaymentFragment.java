package com.nowfloats.Store;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thinksity.R;

/**
 * Created by Admin on 13-04-2018.
 */

public class OpcPaymentFragment extends Fragment implements View.OnClickListener {

    public static Fragment getInstance(Bundle b){
        Fragment frag = new OpcPaymentFragment();
        frag.setArguments(b);
        return frag;
    }
    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null)
            getActivity().setTitle("OPC Payment");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_opc_option, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.textView_apply).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textView_apply:
                // check for opc
                break;
        }
    }
}
