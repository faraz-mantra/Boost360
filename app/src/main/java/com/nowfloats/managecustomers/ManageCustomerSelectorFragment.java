package com.nowfloats.managecustomers;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thinksity.R;

/**
 * Created by Admin on 27-06-2017.
 */

public class ManageCustomerSelectorFragment extends Fragment {

    float elevation = 0f;
    public static Fragment getInstanse(int pos) {
        Fragment frag = new ManageCustomerSelectorFragment();
        Bundle b = new Bundle();
        b.putInt("pos", pos);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

            int pos = getArguments().getInt("pos");

            if (pos != 0) {
                elevation = 6.0f;
            }

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_customers_v1_item, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isAdded()) return;
        view.bringToFront();
        CardView cvManageCustomer = (CardView) view.findViewById(R.id.cvManageCustomer);
        cvManageCustomer.setElevation(elevation);

    }
}
