package com.nowfloats.AccrossVerticals.domain.ui.NewDomain;

import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nowfloats.AccrossVerticals.API.APIInterfaces;
import com.nowfloats.AccrossVerticals.domain.DomainEmailActivity;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;

public class NewDomainFragment extends Fragment {

    private NewDomainViewModel mViewModel;
    private Spinner domainSupportTypeSpinneer;
    ProgressDialog vmnProgressBar;
    String[] domainSupportType = new String[]{};

    public static NewDomainFragment newInstance() {
        return new NewDomainFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(NewDomainViewModel.class);
        return inflater.inflate(R.layout.new_domain_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vmnProgressBar = new ProgressDialog(requireContext());
        vmnProgressBar.setIndeterminate(true);
        vmnProgressBar.setMessage(getString(R.string.please_wait));
        vmnProgressBar.setCancelable(false);

        //setheader
        setHeader(view);

        loadData();


        domainSupportTypeSpinneer = view.findViewById(R.id.domain_support_type);
        domainSupportTypeSpinneer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(requireContext(),domainSupportType[position] , Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void setHeader(View view){
        LinearLayout backButton;
        TextView title;

        title = view.findViewById(R.id.title);
        backButton = view.findViewById(R.id.back_button);
        title.setText("Get a New Domain");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });
    }

    void loadData(){
        try {
            showProgress();
            APIInterfaces APICalls = new RestAdapter.Builder()
                    .setEndpoint("http://plugin.withfloats.com")
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setLog(new AndroidLog("ggg"))
                    .build()
                    .create(APIInterfaces.class);

            APICalls.getDomainSupportType(((DomainEmailActivity) requireActivity()).clientid, new Callback<String[]>() {
                @Override
                public void success(String[] dataList, Response response) {
                    hideProgress();

                    if(response.getStatus() == 200 && dataList != null && dataList.length > 0 ) {
                        domainSupportType = dataList;

                        //Creating the ArrayAdapter instance having the country list
                        ArrayAdapter aa = new ArrayAdapter(requireActivity(), R.layout.spinner_item_white_text, domainSupportType);
                        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        //Setting the ArrayAdapter data on the Spinner
                        domainSupportTypeSpinneer.setAdapter(aa);
                    }else{
                        Methods.showSnackBarNegative(requireActivity(), getString(R.string.something_went_wrong));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    hideProgress();
                    Methods.showSnackBarNegative(requireActivity(), getString(R.string.something_went_wrong));
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
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