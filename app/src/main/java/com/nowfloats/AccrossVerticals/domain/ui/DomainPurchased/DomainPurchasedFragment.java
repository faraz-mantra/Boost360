package com.nowfloats.AccrossVerticals.domain.ui.DomainPurchased;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nowfloats.AccrossVerticals.domain.DomainEmailActivity;
import com.nowfloats.AccrossVerticals.domain.ui.ExistingDomain.ExistingDomainFragment;
import com.nowfloats.AccrossVerticals.domain.ui.NewDomain.NewDomainFragment;
import com.thinksity.R;

public class DomainPurchasedFragment extends Fragment {

    private DomainPurchasedViewModel mViewModel;
    private TextView existDomainButton, newDomainButton;

    public static DomainPurchasedFragment newInstance() {
        return new DomainPurchasedFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(DomainPurchasedViewModel.class);
        return inflater.inflate(R.layout.domain_purchased_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //setheader
        setHeader(view);

        existDomainButton = view.findViewById(R.id.existdomain_btn_proceed);
        newDomainButton = view.findViewById(R.id.newdomain_btn_proceed);

        existDomainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DomainEmailActivity) requireActivity()).addFragment(new ExistingDomainFragment(), "ExistingDomain");
            }
        });

        newDomainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DomainEmailActivity) requireActivity()).addFragment(new NewDomainFragment(), "NewDomain");
            }
        });

    }


    public void setHeader(View view){
        LinearLayout backButton;
        TextView title;

        title = view.findViewById(R.id.title);
        backButton = view.findViewById(R.id.back_button);
        title.setText("Website Domain");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });
    }

}