package com.nowfloats.NavigationDrawer.businessApps;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonObject;
import com.nowfloats.CustomWidget.MaterialProgressBar;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.BusinessAppApis;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Abhi on 12/12/2016.
 */

public class BusinessAppPreview extends Fragment {
    private static final String ANDROID = "android",IOS="ios";
    Context context;
    public final static int SHOW_STUDIO=0,SHOW_DEVELOPMENT=1,SHOW_COMPLETE=2;
    private UserSessionManager session;
    String status=null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_business_app_preview,container,false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session=new UserSessionManager(context,getActivity());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }

    public void addAndroidFragment(int id){
        Fragment frag;
        FragmentTransaction transaction=getChildFragmentManager().beginTransaction();
        switch (id){
            case SHOW_STUDIO:
                frag = BusinessAppStudio.getInstance(ANDROID);
                transaction.add(R.id.card_view_android,frag,"studio");
                break;
            case SHOW_DEVELOPMENT:
                frag = BusinessAppDevelopment.getInstance(ANDROID);
                //transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                transaction.replace(R.id.card_view_android,frag,"development");
                break;
            case SHOW_COMPLETE:
                frag = BusinessAppCompleteFragment.getInstance(ANDROID);
                transaction.replace(R.id.card_view_android,frag,"complete");
                break;
            default:
                break;
        }
        transaction.commit();
    }
    public void addIosFragment(int id){
        Fragment frag;
        FragmentTransaction transaction=getChildFragmentManager().beginTransaction();
        switch (id){
            case SHOW_STUDIO:
                frag = BusinessAppStudio.getInstance(IOS);
                transaction.add(R.id.card_view_ios,frag,"studio");
                break;
            case SHOW_DEVELOPMENT:
                frag = BusinessAppDevelopment.getInstance(IOS);
                //transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                transaction.replace(R.id.card_view_ios,frag,"development");
                break;
            case SHOW_COMPLETE:
                frag = BusinessAppCompleteFragment.getInstance(IOS);
                transaction.replace(R.id.card_view_ios,frag,"complete");
                break;
            default:
                break;
        }
        transaction.commit();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialProgressBar.startProgressBar(getActivity(),"Processing...",false);
        final BusinessAppApis.AppApis apis=BusinessAppApis.getRestAdapter();
        apis.getStatus(Constants.clientId, session.getFPID(), new Callback<JsonObject>() {
            @Override
            public void success(JsonObject s, Response response) {

                if(s == null || response.getStatus() != 200){
                    MaterialProgressBar.dismissProgressBar();
                    return;
                }
                status = s.get("Status").getAsString();
                String message = s.get("Message").getAsString();
                Log.v("ggg",status);
                if(status == null){
                    MaterialProgressBar.dismissProgressBar();
                    Methods.showSnackBarNegative(getActivity(),"Problem to start build");
                }else if(status.equals("0")){
                    MaterialProgressBar.dismissProgressBar();
                    addAndroidFragment(SHOW_DEVELOPMENT);
                }else if(status.equals("-1")){

                    if(Long.parseLong(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CREATED_ON).split("\\(")[1].split("\\)")[0])/1000 > 1470614400){

                        apis.getGenerate(Constants.clientId, session.getFPID(), new Callback<JsonObject>() {
                            @Override
                            public void success(JsonObject jsonObject, Response response) {
                                MaterialProgressBar.dismissProgressBar();
                                if(jsonObject == null || response.getStatus() != 200){
                                    Methods.showSnackBarNegative(getActivity(),"Problem to start build");
                                    return;
                                }
                                addAndroidFragment(SHOW_DEVELOPMENT);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                MaterialProgressBar.dismissProgressBar();
                                Methods.showSnackBarNegative(getActivity(),"Problem to start build");
                            }
                        });
                    }
                    else{
                        MaterialProgressBar.dismissProgressBar();
                        addAndroidFragment(SHOW_STUDIO);
                    }

                }else if(status.equals("1")){
                    apis.getPublishStatus(Constants.clientId, session.getFPID(), new Callback<JsonObject>() {
                        @Override
                        public void success(JsonObject jsonObject, Response response) {
                            if(jsonObject == null || response.getStatus() != 200){
                                MaterialProgressBar.dismissProgressBar();
                                Methods.showSnackBarNegative(getActivity(),"Problem to start build");
                                return;
                            }
                            String status = jsonObject.get("Status").getAsString();
                            MaterialProgressBar.dismissProgressBar();
                            if(status!= null && status.equals("1")){
                                addAndroidFragment(SHOW_COMPLETE);
                            }else {
                                addAndroidFragment(SHOW_DEVELOPMENT);
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            MaterialProgressBar.dismissProgressBar();
                            Methods.showSnackBarNegative((BusinessAppsActivity)context,getString(R.string.something_went_wrong));
                            addAndroidFragment(SHOW_DEVELOPMENT);
                        }
                    });
                }
            }

            @Override
            public void failure(RetrofitError error) {
                MaterialProgressBar.dismissProgressBar();
                Log.v("ggg",error+"");
                Methods.showSnackBarNegative(getActivity(),"Problem to start build");
            }
        });

        addIosFragment(SHOW_STUDIO);
    }
}
