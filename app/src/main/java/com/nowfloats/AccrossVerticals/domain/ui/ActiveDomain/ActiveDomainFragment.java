package com.nowfloats.AccrossVerticals.domain.ui.ActiveDomain;

import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nowfloats.AccrossVerticals.API.APIInterfaces;
import com.nowfloats.AccrossVerticals.API.model.GetDomain.GetDomainData;
import com.nowfloats.AccrossVerticals.Testimonials.TestimonialsFeedbackActivity;
import com.nowfloats.AccrossVerticals.domain.DomainEmailActivity;
import com.nowfloats.AccrossVerticals.domain.adapter.EmailAdapter;
import com.nowfloats.AccrossVerticals.domain.ui.DomainPurchased.DomainPurchasedFragment;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.AccrossVerticals.domain.interfaces.ActiveDomainListener;
import com.nowfloats.AccrossVerticals.domain.ui.Popup.AddEmailPopUpFragment;
import com.nowfloats.AccrossVerticals.domain.ui.Popup.EditEmailPopUpFragment;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;


public class ActiveDomainFragment extends Fragment implements ActiveDomainListener {

    private ActiveDomainViewModel mViewModel;
    private ProgressDialog vmnProgressBar;
    private UserSessionManager session;
    private EmailAdapter emailAdapter;
    private RecyclerView recyclerView;
    private ImageView addNewEmailButton, domainStatus;
    private TextView domainName, activeOnValue, expiryOnValue;
    private AddEmailPopUpFragment addEmailPopUpFragment = new AddEmailPopUpFragment();
    private EditEmailPopUpFragment editEmailPopUpFragment = new EditEmailPopUpFragment();
    GetDomainData data = null;

    public static ActiveDomainFragment newInstance() {
        return new ActiveDomainFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        session = new UserSessionManager(getContext(), requireActivity());
        mViewModel = ViewModelProviders.of(this).get(ActiveDomainViewModel.class);

        return inflater.inflate(R.layout.active_domain_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vmnProgressBar = new ProgressDialog(requireContext());
        vmnProgressBar.setIndeterminate(true);
        vmnProgressBar.setMessage(getString(R.string.please_wait));
        vmnProgressBar.setCancelable(false);

        domainName = view.findViewById(R.id.domain_name);
        domainStatus = view.findViewById(R.id.domain_status);
        activeOnValue = view.findViewById(R.id.booked_on_value);
        expiryOnValue = view.findViewById(R.id.expired_on_value);

        addNewEmailButton = view.findViewById(R.id.add_new_email_button);
        addNewEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEmailPopUpFragment.show(getActivity().getSupportFragmentManager(), "ADD_EMAIL_POPUP_FRAGMENT");
            }
        });
        emailAdapter = new EmailAdapter(new ArrayList(), this);
        recyclerView = (RecyclerView) view.findViewById(R.id.email_recycler);
        initializeRecycler();

        //setheader
        setHeader(view);

        loadData();
    }

    private void initializeRecycler() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(emailAdapter);
    }

    @Override
    public void onEmailItemClicked(String value) {
        Toast.makeText(getContext(), value, Toast.LENGTH_LONG).show();
        editEmailPopUpFragment.show(getActivity().getSupportFragmentManager(), "EDIT_EMAIL_POPUP_FRAGMENT");
    }

    private void showProgress() {
        if (!vmnProgressBar.isShowing()) {
            vmnProgressBar.show();
        }
    }

    private void hideProgress() {
        if (vmnProgressBar.isShowing()) {
            vmnProgressBar.dismiss();
        }
    }

    public void setHeader(View view) {
        LinearLayout backButton;
        TextView title;

        title = view.findViewById(R.id.title);
        backButton = view.findViewById(R.id.back_button);
        title.setText("Website Domain");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private void loadData() {
        try {
            showProgress();
            APIInterfaces APICalls = new RestAdapter.Builder()
                    .setEndpoint("http://plugin.withfloats.com")
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setLog(new AndroidLog("ggg"))
                    .build()
                    .create(APIInterfaces.class);

            APICalls.getDomainDetails(session.getFpTag(), Constants.clientId3, new Callback<GetDomainData>() {
                @Override
                public void success(GetDomainData domainData, Response response) {
                    hideProgress();
                    if (domainData == null || response.getStatus() != 200) {
                        Toast.makeText(getContext(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    data = domainData;
                    updateView();

                }

                @Override
                public void failure(RetrofitError error) {
                    hideProgress();
                    Methods.showSnackBarNegative(getActivity(), getString(R.string.something_went_wrong));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateView() {
        domainName.setText(data.getDomainName() + data.getDomainType());
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String activeDate = data.getActivatedOn();
        if (activeDate.contains("/Date")) {
            activeDate = activeDate.replace("/Date(", "").replace(")/", "");
            Date date = new Date(Long.parseLong(activeDate));
            activeOnValue.setText(sdf.format(date));
        }else{
            activeOnValue.setText("null");
        }
        String expiryDate = data.getExpiresOn();
        if (expiryDate.contains("/Date")) {
            expiryDate = activeDate.replace("/Date(", "").replace(")/", "");
            Date date = new Date(Long.parseLong(expiryDate));
            expiryOnValue.setText(sdf.format(date));
        }else {
            expiryOnValue.setText("null");
        }

        if (data.getIsActive()) {
            domainStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_domain_active_status));
        } else{
            domainStatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_domain_expired_status));
        }
    }
}