package com.nowfloats.AccrossVerticals.domain.ui.DomainPurchased;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.nowfloats.AccrossVerticals.domain.DomainEmailActivity;
import com.nowfloats.AccrossVerticals.domain.ui.ExistingDomain.ExistingDomainFragment;
import com.nowfloats.AccrossVerticals.domain.ui.NewDomain.NewDomainFragment;
import com.nowfloats.util.WebEngageController;
import com.thinksity.R;

import static com.framework.webengageconstant.EventLabelKt.EVENT_LABEL_DOMAIN_AND_EMAIL;
import static com.framework.webengageconstant.EventNameKt.CLICKED_ON_BOOK_A_NEW_DOMAIN;
import static com.framework.webengageconstant.EventNameKt.CLICKED_ON_HAVE_AN_EXISTING_DOMAIN;
import static com.framework.webengageconstant.EventNameKt.DOMAIN_AND_EMAIL;
import static com.framework.webengageconstant.EventValueKt.NULL;

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
                WebEngageController.trackEvent(CLICKED_ON_HAVE_AN_EXISTING_DOMAIN, EVENT_LABEL_DOMAIN_AND_EMAIL, NULL);
                ((DomainEmailActivity) requireActivity()).addFragment(new ExistingDomainFragment(), "ExistingDomain");
            }
        });

        newDomainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebEngageController.trackEvent(DOMAIN_AND_EMAIL, CLICKED_ON_BOOK_A_NEW_DOMAIN, NULL);
//                ((DomainEmailActivity) requireActivity()).addFragment(new NewDomainFragment(), "NewDomain");
                Toast.makeText(requireActivity(), "Please log into boost web application to use this feature.", Toast.LENGTH_LONG).show();
            }
        });

    }


    public void setHeader(View view) {
        LinearLayout backButton;
        TextView title;

        title = view.findViewById(R.id.title);
        backButton = view.findViewById(R.id.back_button);
        title.setText(R.string.website_domain);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

}