package com.nowfloats.NavigationDrawer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.WildFireApis;
import com.nowfloats.NavigationDrawer.Adapter.TextExpandableAdapter;
import com.nowfloats.NavigationDrawer.Adapter.WildFireAdapter;
import com.nowfloats.NavigationDrawer.model.WildFireDataModel;
import com.nowfloats.NavigationDrawer.model.WildFireKeyStatsModel;
import com.nowfloats.Store.NewPricingPlansActivity;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.Arrays;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.nowfloats.NavigationDrawer.HomeActivity.headerText;

/**
 * Created by Admin on 29-11-2017.
 */

public class WildFireFragment extends Fragment implements View.OnClickListener,WildFireAdapter.ItemClick{

    private Context mContext;
    private ProgressDialog progressDialog;
    private ArrayList<WildFireKeyStatsModel> wildFireList;

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
        super.onViewCreated(view, savedInstanceState);
        if (!isAdded()){
            return;
        }
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.please_wait));
        UserSessionManager manager = new UserSessionManager(mContext,getActivity());
        getWildFireData(manager.getFPDetails(Key_Preferences.EXTERNAL_SOURCE_ID),view);
    }

    private void showKeywordsList(View view){

        if (wildFireList != null && wildFireList.size()>0){
            ConstraintLayout defaultLayout = view.findViewById(R.id.default_layout);
            defaultLayout.setVisibility(View.GONE);
            RecyclerView keywordRv = view.findViewById(R.id.keyword_list);
            keywordRv.setHasFixedSize(true);
            keywordRv.setVisibility(View.VISIBLE);
            keywordRv.setLayoutManager(new LinearLayoutManager(mContext));
            keywordRv.setAdapter(new WildFireAdapter(this,mContext,wildFireList));
        }else{
            showDefaultPage(view);
        }
    }
    private void showDefaultPage(View view){
        TextView wildfireDefinitionTv = view.findViewById(R.id.wildfire_definition);
        view.findViewById(R.id.tv_wildfire).setOnClickListener(this);
        view.findViewById(R.id.tv_know_more).setOnClickListener(this);
        wildfireDefinitionTv.setText(Methods.fromHtml(getString(R.string.wildfire_definition)));
        ArrayList<ArrayList<String>> childList = new ArrayList<>(3);
        ArrayList<String> parentList =new ArrayList<>(Arrays.asList( mContext.getResources().getStringArray(R.array.wildfire_parents)));
        childList.add(new ArrayList<>(Arrays.asList(mContext.getResources().getStringArray(R.array.wildfire_parent_0))));
        childList.add(new ArrayList<>(Arrays.asList(mContext.getResources().getStringArray(R.array.wildfire_parent_1))));
        childList.add(new ArrayList<>(Arrays.asList(mContext.getResources().getStringArray(R.array.wildfire_parent_2))));

        ExpandableListView expandableListView = view.findViewById(R.id.info_exlv);
        expandableListView.setAdapter(new TextExpandableAdapter(mContext,childList,parentList));
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
    private void getKeyWordStats(String accId, final View view){
        WildFireApis apis = WildFireApis.adapter.create(WildFireApis.class);
        apis.getKeyWordsStats(Constants.clientId, accId, new Callback<ArrayList<WildFireKeyStatsModel>>() {
            @Override
            public void success(ArrayList<WildFireKeyStatsModel> wildFireKeyStatsModels, Response response) {
                hideProgress();
                if (wildFireKeyStatsModels != null){
                    wildFireList = wildFireKeyStatsModels;
                }
                showKeywordsList(view);
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgress();
                showKeywordsList(view);
            }
        });
    }

    private void getWildFireData(String sourceId, final View view){
        showProgress();
        WildFireApis apis = Constants.restAdapter.create(WildFireApis.class);
        apis.getWildFireData(sourceId, Constants.clientId, new Callback<WildFireDataModel>() {
            @Override
            public void success(WildFireDataModel wildFireDataModel, Response response) {
                if (wildFireDataModel != null){
                    getKeyWordStats(wildFireDataModel.getId(),view);
                }else{
                    hideProgress();
                   showDefaultPage(view);
                    // show default page
                }
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgress();
                showDefaultPage(view);
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        if (headerText != null){
            headerText.setText("WildFire");
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_wildfire:
                startActivity(new Intent(mContext, NewPricingPlansActivity.class));
                break;
            case R.id.tv_know_more:

                break;
        }
    }

    @Override
    public void onItemSelected(int data) {
        //startActivity(new );
    }
}
