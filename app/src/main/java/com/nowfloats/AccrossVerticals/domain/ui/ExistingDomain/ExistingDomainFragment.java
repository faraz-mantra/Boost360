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

import com.framework.firebaseUtils.firestore.FirestoreManager;
import com.nowfloats.AccrossVerticals.API.APIInterfaces;
import com.nowfloats.AccrossVerticals.API.model.ExistingDomain.ExistingDomainRequest;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.nowfloats.util.Utils;
import com.thinksity.R;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;

@Deprecated
public class ExistingDomainFragment extends Fragment {

    ProgressDialog vmnProgressBar;
    TextView confirmButton, cancleButton;
    RadioButton radioButton1, radioButton2;
    EditText domainName, subdomainDescription;
    String subject = "", message = "";
    private ExistingDomainViewModel mViewModel;
    private UserSessionManager session;

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
        session = new UserSessionManager(getContext(), requireActivity());
        confirmButton = view.findViewById(R.id.confirm_btn);
        cancleButton = view.findViewById(R.id.cancel_button);
        radioButton1 = view.findViewById(R.id.radio_button1);
        radioButton2 = view.findViewById(R.id.radio_button2);
        domainName = view.findViewById(R.id.domain_name);
        subdomainDescription = view.findViewById(R.id.subdomain_description);

        radioButton1.setChecked(true);
        subdomainDescription.setText(getString(R.string.i_would_like_to_map_my_existing_domain) + session.getFpTag() + getString(R.string.please_reach_out_to_me));

        radioButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    radioButton2.setChecked(false);
                    subject = getString(R.string.make_my_domain_as_my_boost) + session.getFpTag() + "]";
                    if (!domainName.getText().toString().isEmpty()) {
                        message = getString(R.string.i_would_like_to_map_my_existing_domain_to_) + domainName.getText().toString() + getString(R.string.please_reach_out_to_me_discuss_in_details);
                    } else {
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
                    subject = getString(R.string.point_your_existing_domain_to_nowfloats) + session.getFpTag() + "]";
                    message = getString(R.string.i_would_like_to_map_my_existing_domain_to_my) + session.getFpTag() + getString(R.string.please_reach_out_and_discuss_more_about_it);
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
                getActivity().onBackPressed();
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
                getActivity().onBackPressed();
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
                        .setRequestInterceptor(Utils.getAuthRequestInterceptor())
                        .setLogLevel(RestAdapter.LogLevel.FULL)
                        .setLog(new AndroidLog("ggg"))
                        .build()
                        .create(APIInterfaces.class);

                APICalls.addExistingDomainDeatils(Constants.clientId3, session.getFpTag(), body, new Callback<Boolean>() {
                    @Override
                    public void success(Boolean status, Response response) {
                        hideProgress();
                        if (response.getStatus() == 200) {
                            Methods.showSnackBarPositive(getActivity(), getString(R.string.request_successfully));
                            getActivity().onBackPressed();
                            onDomainAddedOrUpdated(true);
                        } else {
                            Methods.showSnackBarNegative(getActivity(), getString(R.string.something_went_wrong));
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        hideProgress();
                        Methods.showSnackBarNegative(getActivity(), getString(R.string.something_went_wrong));
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onDomainAddedOrUpdated(Boolean isAdded) {
        FirestoreManager instance = FirestoreManager.INSTANCE;
        if (instance.getDrScoreData() != null && instance.getDrScoreData().getMetricdetail() != null) {
            instance.getDrScoreData().getMetricdetail().setBoolean_add_custom_domain_name_and_ssl(isAdded);
            instance.updateDocument();
        }
    }

    private boolean validate() {
        if (radioButton1.isChecked()) {
            if (domainName.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), getString(R.string.domain_details_field_is_empty), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (radioButton2.isChecked()) {
            if (subdomainDescription.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), getString(R.string.message_field_is_empty), Toast.LENGTH_SHORT).show();
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