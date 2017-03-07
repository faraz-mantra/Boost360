package com.nowfloats.NotificationCenter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.Home_Fragment_Tab;
import com.nowfloats.NavigationDrawer.model.AlertCountEvent;
import com.nowfloats.NotificationCenter.Model.AlertModel;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.squareup.otto.Bus;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.recyclerview.animators.FadeInUpAnimator;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by guru on 19-06-2015.
 */
public class NotificationFragment extends Fragment{
    Activity activity;
    UserSessionManager session;
    public static RecyclerView recyclerView;
    private NotificationAdapter adapter;
    Bus bus;
    private LinearLayout emptylayout,progress_layout;
    NotificationInterface alertInterface;
    private boolean userScrolled = false;
    private int prevCount = 0;
    private String mIsRead = "false";
    private boolean mIsAlertShown = false;
    private int alertsCount = 0;
    private boolean mShouldRequestMore = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        bus = BusProvider.getInstance().getBus();
        session = new UserSessionManager(activity.getApplicationContext(),activity);
        alertInterface = Constants.restAdapter.create(NotificationInterface.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alert, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getAlertCount(session,alertInterface,bus);
        MixPanelController.track("Alerts", null);
        recyclerView = (RecyclerView) view.findViewById(R.id.alert_recycler_view);
        recyclerView.setHasFixedSize(true);
        emptylayout = (LinearLayout) view.findViewById(R.id.emptyalertlayout);
        emptylayout.setVisibility(View.GONE);
        progress_layout = (LinearLayout) view.findViewById(R.id.progress_layout);
        progress_layout.setVisibility(View.VISIBLE);
        final LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setItemAnimator(new FadeInUpAnimator());
        //get alert Values
        loadAlerts();
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                super.onScrollStateChanged(recyclerView, scrollState);
                if(scrollState == RecyclerView.SCROLL_STATE_DRAGGING){
                    userScrolled = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = recyclerView.getChildCount();
                int totalItemCount = mLinearLayoutManager.getItemCount();
                int firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

                /*if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    // End has been reached

                    // Do something
                    current_page++;
                }*/

                int lastInScreen = firstVisibleItem + visibleItemCount;
                if((userScrolled) && (lastInScreen == totalItemCount) && (prevCount!=totalItemCount) && mShouldRequestMore){
                    userScrolled=false;
                    prevCount = totalItemCount;
                    moreAlerts("" + alertsCount);
                }
            }
        });
    }

    private void moreAlerts(String offset) {
        progress_layout.setVisibility(View.VISIBLE);
        alertsCount += 10;
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("clientId", Constants.clientId);
            params.put("fpId", session.getFPID());
            params.put("isRead", "true");
            params.put("offset", offset);
            params.put("limit", "10");

            alertInterface.getAlerts(params,new Callback<ArrayList<AlertModel>>() {
                @Override
                public void success(ArrayList<AlertModel> alertModels, Response response) {
                    if (alertModels!=null && alertModels.size()<10){
                        mShouldRequestMore = false;
                    }
                    progress_layout.setVisibility(View.GONE);
                    if ((alertModels==null || alertModels.size()==0) && !mIsAlertShown){
                        emptylayout.setVisibility(View.VISIBLE);
                    }else{
                        mIsAlertShown = true;
                        emptylayout.setVisibility(View.GONE);
                    }
                    if(adapter!=null) {
                        adapter.addAlerts(alertModels);
                        adapter.notifyDataSetChanged();
                    }else{
                        adapter = new NotificationAdapter(getActivity(),alertModels,alertInterface,session,bus);
                        recyclerView.setAdapter(adapter);
                    }
                    progress_layout.setVisibility(View.GONE);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.i("loadAlerts failure",""+error.getMessage());
                    Methods.showSnackBarNegative(activity,getString(R.string.something_went_wrong_try_again));
                    progress_layout.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            Log.i("Alert data","API Exception:"+e);
            Methods.showSnackBarNegative(activity,getString(R.string.something_went_wrong_try_again));
            e.printStackTrace();
            progress_layout.setVisibility(View.GONE);
        }
    }



    public void loadAlerts() {
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("clientId", Constants.clientId);
            params.put("fpId", session.getFPID());
            params.put("isRead", "false");
            params.put("offset", "0");
            params.put("limit", Home_Fragment_Tab.alertCountVal);

            alertInterface.getAlerts(params,new Callback<ArrayList<AlertModel>>() {
                @Override
                public void success(ArrayList<AlertModel> alertModels, Response response) {
                    Log.i("Alerts Success","");
                    if (alertModels!=null && alertModels.size() > 0 && getActivity()!=null){
                        mIsAlertShown = true;
                        adapter = new NotificationAdapter(getActivity(),alertModels,alertInterface,session,bus);
                        recyclerView.setAdapter(adapter);
                    }
                    moreAlerts("0");
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.i("loadAlerts failure",""+error.getMessage());
                    if(getActivity()!=null) {
                        Methods.showSnackBarNegative(getActivity(), getString(R.string.something_went_wrong_try_again));
                    }
                    progress_layout.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            Log.i("Alert data","API Exception:"+e);
            Methods.showSnackBarNegative(activity,getString(R.string.something_went_wrong_try_again));
            e.printStackTrace();
            progress_layout.setVisibility(View.GONE);
        }
    }

    public static void getAlertCount(UserSessionManager session,NotificationInterface notificationInterface,final Bus bus){
        try{
        HashMap<String,String> map = new HashMap<>();
        map.put("fpId",session.getFPID());
        map.put("clientId", Constants.clientId);
        map.put("isRead","false");
        notificationInterface.getAlertCount(map,new Callback<String>() {
            @Override
            public void success(String s, Response response) {
                Home_Fragment_Tab.alertCountVal = s;
                bus.post(new AlertCountEvent(s));
                BoostLog.d("AlertCount-", s);
                MixPanelController.track("AlertCount-"+s, null);
            }

            @Override
            public void failure(RetrofitError error) {
                Home_Fragment_Tab.alertCountVal = "0";
            }
        });
        }catch(Exception e){e.printStackTrace();
            bus.post(new AlertCountEvent("0"));}
    }
}