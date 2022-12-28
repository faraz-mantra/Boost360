package com.nowfloats.AccrossVerticals.domain.ui.NewDomain;

import androidx.lifecycle.ViewModelProviders;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
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
import com.nowfloats.util.Utils;
import com.nowfloats.util.WebEngageController;
import com.thinksity.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;

import static com.framework.webengageconstant.EventLabelKt.EVENT_LABEL_DOMAIN_AND_EMAIL;
import static com.framework.webengageconstant.EventNameKt.CLICKED_ON_CONFIRM_BOOK_A_NEW_DOMAIN;
import static com.framework.webengageconstant.EventValueKt.NO_EVENT_VALUE;

@Deprecated
public class NewDomainFragment extends Fragment {

    ProgressDialog vmnProgressBar;
    String[] domainSupportType = new String[]{};
    private NewDomainViewModel mViewModel;
    private Spinner domainSupportTypeSpinneer;
    private TextView confirm_btn;

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

        vmnProgressBar = new ProgressDialog(requireContext(),R.style.AppCompatAlertDialogStyle);
        vmnProgressBar.setIndeterminate(true);
        vmnProgressBar.setMessage(getString(R.string.please_wait));
        vmnProgressBar.setCancelable(false);

        //setheader
        setHeader(view);

        loadData();


        domainSupportTypeSpinneer = view.findViewById(R.id.domain_support_type);
        confirm_btn = view.findViewById(R.id.confirm_btn);
        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebEngageController.trackEvent(CLICKED_ON_CONFIRM_BOOK_A_NEW_DOMAIN, EVENT_LABEL_DOMAIN_AND_EMAIL, NO_EVENT_VALUE);
            }
        });
//        domainSupportTypeSpinneer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getContext(),domainSupportType[position] , Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
    }

    public void setHeader(View view) {
        LinearLayout backButton;
        TextView title;

        title = view.findViewById(R.id.title);
        backButton = view.findViewById(R.id.back_button);
        title.setText(R.string.get_a_new_domain);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    void loadData() {
        try {
            showProgress();
            APIInterfaces APICalls = new RestAdapter.Builder()
                    .setEndpoint("http://plugin.withfloats.com")
                    .setRequestInterceptor(Utils.getAuthRequestInterceptor())
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .setLog(new AndroidLog("ggg"))
                    .build()
                    .create(APIInterfaces.class);

            APICalls.getDomainSupportType(((DomainEmailActivity) requireActivity()).clientid, new Callback<String[]>() {
                @Override
                public void success(String[] dataList, Response response) {
                    hideProgress();

                    if (response.getStatus() == 200 && dataList != null && dataList.length > 0) {
                        domainSupportType = dataList;
                        ArrayList<String> domainList = new ArrayList<String>(Arrays.asList(dataList));
                        if (domainList.contains(".COM")) {
//                            Log.v("domainSupportType", "contains " +domainList.contains(".COM"));
                            domainList.remove(".COM");
                            domainList.add(0, ".COM");
                        }
                        if (domainList.contains(".CO.ZA")) {
                            domainList.remove(".CO.ZA");
                            domainList.add(".CO.ZA");
                        }
                        if (domainList.contains(".CA")) {
                            domainList.remove(".CA");
                            domainList.add(".CA");
                        }

                        //Creating the ArrayAdapter instance having the country list
//                        ArrayAdapter aa = new ArrayAdapter(getActivity(), R.layout.spinner_item_white_text, domainSupportType);
                        ArrayAdapter aa = new ArrayAdapter(getActivity(), R.layout.spinner_item_white_text, domainList);
                        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        //Setting the ArrayAdapter data on the Spinner
                        domainSupportTypeSpinneer.setAdapter(aa);
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

        } catch (Exception e) {
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