package com.nowfloats.AccrossVerticals.domain.ui.ExistingDomain;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.framework.models.firestore.FirestoreManager;
import com.nowfloats.AccrossVerticals.API.APIInterfaces;
import com.nowfloats.AccrossVerticals.API.model.ExistingDomain.ExistingDomainRequest;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;

public class ExistingDomainFragment extends Fragment {

    private ExistingDomainViewModel mViewModel;
    ProgressDialog vmnProgressBar;
    private UserSessionManager session;
    TextView confirmButton, cancleButton;
    RadioButton radioButton1, radioButton2;
    EditText domainName, subdomainDescription;
    String subject = "", message = "";

    public static ExistingDomainFragment newInstance() {
        return new ExistingDomainFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(ExistingDomainViewModel.class);
        return inflater.inflate(R.layout.existing_domain_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vmnProgressBar = new ProgressDialog(requireContext());
        vmnProgressBar.setIndeterminate(true);
        vmnProgressBar.setMessage(getString(R.string.please_wait));
        vmnProgressBar.setCancelable(false);
        session = new UserSessionManager(requireContext(), requireActivity());
        confirmButton = view.findViewById(R.id.confirm_btn);
        cancleButton = view.findViewById(R.id.cancel_button);
        radioButton1 = view.findViewById(R.id.radio_button1);
        radioButton2 = view.findViewById(R.id.radio_button2);
        domainName = view.findViewById(R.id.domain_name);
        subdomainDescription = view.findViewById(R.id.subdomain_description);

        radioButton1.setChecked(true);
        subdomainDescription.setText("I would like to map my existing domain to my\n NowFloats site http://" + session.getFpTag() + ".nowfloats.com\nPlease reach out to me to discuss this in detail.");

        radioButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    radioButton2.setChecked(false);
                    subject = "Make given domain as my Boost website’s web address/URL.[" + session.getFpTag() + "]";
                    if (!domainName.getText().toString().isEmpty()) {
                        message = "I would like to map my existing domain to my\n NowFloats site http://"+ domainName.getText().toString() + " \nPlease reach out to me to discuss this in detail.";
                    }else{
                        message = "";
                    }
                } else {
                    radioButton2.setChecked(true);
                }
            }
        });

        radioButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    radioButton1.setChecked(false);
                    subject = "Point your existing domain to NowFloats website. [" + session.getFpTag() + "]";
                    message = "I would like to map my existing domain to my\n NowFloats site http://" + session.getFpTag() + ".nowfloats.com\nPlease reach out to me to discuss this in detail.";
                    subdomainDescription.setText(message);
                } else {
                    radioButton1.setChecked(true);
                }
            }
        });

        subdomainDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                message = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateExistingDomainDetails();
            }
        });

        cancleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        //setheader
        setHeader(view);
    }

    public void setHeader(View view) {
        LinearLayout backButton;
        TextView title;

        title = view.findViewById(R.id.title);
        backButton = view.findViewById(R.id.back_button);
        title.setText("Use Existing Domain");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });
    }

    void updateExistingDomainDetails() {
        try {
            if (validate()) {
                showProgress();
                ExistingDomainRequest body = new ExistingDomainRequest();
                body.setClientId(Constants.clientId4);
                body.setFPTag(session.getFpTag());
                body.setSubject(subject);
                body.setMesg(message);

                APIInterfaces APICalls = new RestAdapter.Builder()
                        .setEndpoint("https://ria.withfloats.com")
                        .setLogLevel(RestAdapter.LogLevel.FULL)
                        .setLog(new AndroidLog("ggg"))
                        .build()
                        .create(APIInterfaces.class);

                APICalls.addExistingDomainDeatils(Constants.clientId3, session.getFpTag(), body, new Callback<Boolean>() {
                    @Override
                    public void success(Boolean status, Response response) {
                        hideProgress();
                        if (response.getStatus() == 200) {
                            Methods.showSnackBarPositive(requireActivity(), "Request Successfully.");
                            requireActivity().onBackPressed();
                            onDomainAddedOrUpdated(true);
                        } else {
                            Methods.showSnackBarNegative(requireActivity(), getString(R.string.something_went_wrong));
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        hideProgress();
                        Methods.showSnackBarNegative(requireActivity(), getString(R.string.something_went_wrong));
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onDomainAddedOrUpdated(Boolean isAdded) {
        FirestoreManager instance = FirestoreManager.INSTANCE;
        if(instance.getDrScoreData().getMetricdetail()==null) return;
        instance.getDrScoreData().getMetricdetail().setBoolean_add_custom_domain_name_and_ssl(isAdded);
        instance.updateDocument();
    }

    private boolean validate() {
        if(radioButton1.isChecked()){
            if(domainName.getText().toString().isEmpty()){
                Toast.makeText(requireContext(), "Domain Details Field is Empty...", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if(radioButton2.isChecked()){
            if(subdomainDescription.getText().toString().isEmpty()){
                Toast.makeText(requireContext(), "Message Field is Empty...", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
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
}