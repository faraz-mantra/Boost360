package com.nowfloats.NavigationDrawer;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.Adapter.TextExpandableAdapter;
import com.nowfloats.Store.Model.MailModel;
import com.nowfloats.Store.Service.StoreInterface;
import com.nowfloats.Store.TopUpDialog;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Admin on 20-12-2017.
 */

public class WildFireFragment extends Fragment implements View.OnClickListener {

    private Context mContext;
    private ProgressDialog progressDialog;
    private TopUpDialog mTopUpDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.please_wait));
        MixPanelController.track(MixPanelController.WILDFIRE_CLICK,null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wildfire,container,false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (!isAdded())  return;
        showDefaultPage(view);
    }

    private void showDefaultPage(View view){

        TextView wildfireDefinitionTv = view.findViewById(R.id.wildfire_definition);
        view.findViewById(R.id.llayout_wildfire).setOnClickListener(this);
        view.findViewById(R.id.llayout_know_more).setOnClickListener(this);
        wildfireDefinitionTv.setText(Methods.fromHtml(getString(R.string.wildfire_definition)));
        ArrayList<ArrayList<String>> childList = new ArrayList<>(3);
        ArrayList<String> parentList =new ArrayList<>(Arrays.asList( getResources().getStringArray(R.array.wildfire_parents)));
        childList.add(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.wildfire_parent_0))));
        childList.add(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.wildfire_parent_1))));
        childList.add(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.wildfire_parent_2))));

        ExpandableListView expandableListView = view.findViewById(R.id.info_exlv);
        expandableListView.setAdapter(new TextExpandableAdapter(mContext,childList,parentList));
        expandableListView.expandGroup(0);
        hideProgress();
    }

    private void showProgress(){
        if (!progressDialog.isShowing()){
            progressDialog.show();
        }
    }
    private void hideProgress(){
        if (progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (mContext instanceof HomeActivity && HomeActivity.headerText != null) {
            HomeActivity.headerText.setText("WildFire");
        }
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.llayout_wildfire) {
            if (getActivity() != null && mTopUpDialog == null) {
                mTopUpDialog = new TopUpDialog(getActivity());
            }
            mTopUpDialog.getTopUpPricing(TopUpDialog.TopUpType.WildFire.name());
        } else if (id == R.id.llayout_know_more) {
            sendEmailForWildFire();
        }
    }
    private void sendEmailForWildFire(){
        showProgress();
        MixPanelController.track(MixPanelController.REQUEST_FOR_WILDFIRE_PLAN,null);
        UserSessionManager manager = new UserSessionManager(mContext,getActivity());
        ArrayList<String> emailsList = new ArrayList<String>(2);
        emailsList.add(getString(R.string.pranav_email));
        emailsList.add(getString(R.string.wildfire_email));
        MailModel model = new MailModel(Constants.clientId,
               getString(R.string.client_with_fp_tag)+manager.getFpTag()+getString(R.string.has_requested_a_meeting),
                 getString(R.string.wildfire_meeting_is_requested)+manager.getFpTag(),
                 emailsList);
        StoreInterface anInterface = Constants.restAdapter.create(StoreInterface.class);
        anInterface.mail(model, new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                hideProgress();
                if (response.getStatus() == 200 && !TextUtils.isEmpty(s)){
                    Methods.materialDialog(getActivity(),getString(R.string.request_for_wildfire_plan),getString(R.string.your_meeting_request_has_been_sent));
                }else{
                    Methods.showSnackBarNegative(getActivity(),getString(R.string.something_went_wrong_try_again));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgress();
                Methods.showSnackBarNegative(getActivity(),getString(R.string.server_error));
            }
        });

    }

}
