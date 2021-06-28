package com.nowfloats.NavigationDrawer.businessApps;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.nowfloats.CustomWidget.MaterialProgressBar;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.BusinessAppApis;
import com.nowfloats.NavigationDrawer.model.StoreAndGoModel;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.List;

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
    private ArrayList<String> screenShots;
    private PopupWindow popup;
    private View parentView;
    private boolean isOnStopCalled;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_business_app_preview,container,false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session=new UserSessionManager(context,requireActivity());
        //setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }

    public void addAndroidFragment(int id,String bundle,boolean transition){
        if(getActivity() != null && getActivity().isFinishing()) return;
        Fragment frag;
        FragmentTransaction transaction=getChildFragmentManager().beginTransaction();
        switch (id){
            case SHOW_STUDIO:
                frag = BusinessAppStudio.getInstance(ANDROID);
                transaction.add(R.id.card_view_android,frag,"studio").commit();
                break;
            case SHOW_DEVELOPMENT:
                frag = BusinessAppDevelopment.getInstance(ANDROID);
                if(transition)
                    transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);

                transaction.replace(R.id.card_view_android,frag,"development").commit();
                break;
            case SHOW_COMPLETE:
                frag = BusinessAppCompleteFragment.getInstance(ANDROID,bundle);
                transaction.replace(R.id.card_view_android,frag,"complete").commit();
                break;
        }
    }
    public void addIosFragment(int id){
        Fragment frag;
        FragmentTransaction transaction=getChildFragmentManager().beginTransaction();
        switch (id){
            case SHOW_STUDIO:
                frag = BusinessAppStudio.getInstance(IOS);
                transaction.add(R.id.card_view_ios,frag,"studio").commit();
                break;
            case SHOW_DEVELOPMENT:
                frag = BusinessAppDevelopment.getInstance(IOS);
                //transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                transaction.replace(R.id.card_view_ios,frag,"development").commit();
                break;
            case SHOW_COMPLETE:
                frag = BusinessAppCompleteFragment.getInstance(IOS,"");
                transaction.replace(R.id.card_view_ios,frag,"complete").commit();
                break;
        }
    }

    @Override
    public void onStop() {
        isOnStopCalled = true;
        super.onStop();
    }

    private boolean isOnStopCalled(){
        return isOnStopCalled;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isOnStopCalled = false;
        if(!isAdded()) return;
        parentView = view;
        MaterialProgressBar.startProgressBar(getActivity(),"Processing...",false);
        final BusinessAppApis.AppApis apis=BusinessAppApis.getRestAdapter();
        apis.getStatus(Constants.clientId, session.getFPID(), new Callback<JsonObject>() {
            @Override
            public void success(JsonObject s, Response response) {

                if(s == null || !isAdded() || isOnStopCalled() || response.getStatus() != 200){
                    MaterialProgressBar.dismissProgressBar();
                    getActivity().finish();
                    return;
                }
                status = s.get("Status").getAsString();

                if(status == null){
                    MaterialProgressBar.dismissProgressBar();
                    Methods.showSnackBarNegative(getActivity(),getResources().getString(R.string.something_went_wrong_try_again));
                    getActivity().finish();
                }else if(status.equals("0")){
                    MaterialProgressBar.dismissProgressBar();
                    addAndroidFragment(SHOW_DEVELOPMENT,"",false);
                }else if(status.equals("-1")){
                    MaterialProgressBar.dismissProgressBar();
                    addAndroidFragment(SHOW_STUDIO,"",false);
                   /* if(Long.parseLong(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CREATED_ON).split("\\(")[1].split("\\)")[0])/1000 > 1470614400){

                        apis.getGenerate(Constants.clientId, session.getFPID(), new Callback<JsonObject>() {
                            @Override
                            public void success(JsonObject jsonObject, Response response) {
                                MaterialProgressBar.dismissProgressBar();
                                if(jsonObject == null || response.getStatus() != 200){
                                    Methods.showSnackBarNegative(getActivity(),"Problem to start build");
                                    getActivity().finish();
                                    return;
                                }
                                addAndroidFragment(SHOW_DEVELOPMENT,"",false);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                MaterialProgressBar.dismissProgressBar();
                                Methods.showSnackBarNegative(getActivity(),"Problem to start build");
                                getActivity().finish();
                            }
                        });
                    }
                    else{
                        MaterialProgressBar.dismissProgressBar();
                        addAndroidFragment(SHOW_STUDIO,"",false);
                    }*/

                }else if(status.equals("1")){
                    apis.getPublishStatus(Constants.clientId, session.getFPID(), new Callback<List<StoreAndGoModel.PublishStatusModel>>() {
                        @Override
                        public void success(List<StoreAndGoModel.PublishStatusModel> modelList, Response response) {
                            MaterialProgressBar.dismissProgressBar();
                            if(modelList == null || !isAdded()|| modelList.size() == 0 ||response.getStatus() != 200){
                                getActivity().finish();
                                return;
                            }
                            for (StoreAndGoModel.PublishStatusModel model: modelList) {
                                if (model.getKey().equals("Status")) {
                                    if (model.getValue().equals("1")) {
                                        StoreAndGoModel storeAndGoModel = new StoreAndGoModel();
                                        storeAndGoModel.setPublishStatusModelList(modelList);
                                        addAndroidFragment(SHOW_COMPLETE, new Gson().toJson(storeAndGoModel),false);
                                    } else {
                                        addAndroidFragment(SHOW_DEVELOPMENT,"",false);
                                    }
                                    break;
                                }
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            MaterialProgressBar.dismissProgressBar();
                            Methods.showSnackBarNegative((BusinessAppsDetailsActivity)context,getResources().getString(R.string.something_went_wrong_try_again));
                            addAndroidFragment(SHOW_DEVELOPMENT,"",false);
                        }
                    });
                }
            }

            @Override
            public void failure(RetrofitError error) {
                MaterialProgressBar.dismissProgressBar();
                Log.v("ggg",error+"");
                Methods.showSnackBarNegative(getActivity(),getResources().getString(R.string.something_went_wrong_try_again));
                getActivity().finish();
            }
        });

        addIosFragment(SHOW_STUDIO);
    }

    public void showScreenShots(){
        if(screenShots == null || screenShots.size() == 0){
            getScreenShots();
        }else{
            showImageDialog();
        }

    }

    private void showImageDialog() {

       ImageDialogFragment dialog = ImageDialogFragment.getInstance(screenShots);
        dialog.show(getChildFragmentManager(),"dialog");
    }
    public void hideImageDialog(){
        ImageDialogFragment dialog = (ImageDialogFragment) getChildFragmentManager().findFragmentByTag("dialog");
        if(dialog != null && dialog.isVisible()){
            getChildFragmentManager().beginTransaction().remove(dialog).commit();
        }
    }
    private void getScreenShots(){
        MaterialProgressBar.startProgressBar(getActivity(),"Processing...",false);
        final BusinessAppApis.AppApis apis=BusinessAppApis.getRestAdapter();
        apis.getScreenshots(Constants.clientId, session.getFPID(), new Callback<List<StoreAndGoModel.ScreenShotsModel>>() {
            @Override
            public void success(List<StoreAndGoModel.ScreenShotsModel> modelList, Response response) {
                MaterialProgressBar.dismissProgressBar();
                if(modelList == null || modelList.size()== 0 ||response.getStatus() != 200){
                    Methods.showSnackBarNegative(getActivity(),getResources().getString(R.string.something_went_wrong_try_again));
                    return;
                }
                for (StoreAndGoModel.ScreenShotsModel model : modelList){
                    if(model.getKey().equals("screens")){
                        BusinessAppPreview.this.screenShots = (ArrayList<String>) model.getValue();
                        showImageDialog();
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                MaterialProgressBar.dismissProgressBar();
                Methods.showSnackBarNegative(getActivity(),getResources().getString(R.string.something_went_wrong_try_again));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
}
