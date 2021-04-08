package com.nowfloats.NotificationCenter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dashboard.utils.DeepLinkUtil;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.DeepLinkInterface;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.NavigationDrawer.Home_Fragment_Tab;
import com.nowfloats.NavigationDrawer.model.AlertCountEvent;
import com.nowfloats.NotificationCenter.Model.AlertModel;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.squareup.otto.Bus;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by guru on 19-06-2015.
 */
public class NotificationFragment extends Fragment implements DeepLinkInterface {
    Activity activity;
    UserSessionManager session;
    public static RecyclerView recyclerView;
    private NotificationAdapter adapter;
    Bus bus;
    private LinearLayout emptylayout, progress_layout;
    NotificationInterface alertInterface;
    private boolean mIsAlertShown = false;
    private int readAlertsCount, unReadAlertCount;
    private boolean stop;
    private DeepLinkUtil deepLinkUtil;
    private ArrayList<AlertModel> alertModelsList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        bus = BusProvider.getInstance().getBus();
        session = new UserSessionManager(activity.getApplicationContext(), activity);
        alertInterface = Constants.restAdapter.create(NotificationInterface.class);
        deepLinkUtil = new DeepLinkUtil((AppCompatActivity) activity, new com.framework.pref.UserSessionManager(activity));
    }

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        bus.unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alert, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mIsAlertShown) {
            alertModelsList.clear();
            adapter.notifyDataSetChanged();
            mIsAlertShown = false;
            readAlertsCount = 0;
            unReadAlertCount = 0;
            stop = false;
        }

        getAlertCount(session, alertInterface, bus);
        MixPanelController.track("Alerts", null);
        recyclerView = (RecyclerView) view.findViewById(R.id.alert_recycler_view);
        recyclerView.setHasFixedSize(true);
        emptylayout = (LinearLayout) view.findViewById(R.id.emptyalertlayout);
        emptylayout.setVisibility(View.GONE);
        progress_layout = (LinearLayout) view.findViewById(R.id.progress_layout);
        final LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        adapter = new NotificationAdapter(activity, alertModelsList, alertInterface, session, bus, this);
        recyclerView.setAdapter(adapter);
        //get alert Values
        if (!mIsAlertShown) {
            loadAlerts();
        } else if (alertModelsList.size() == 0) {
            emptylayout.setVisibility(View.VISIBLE);
        }
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int totalItemCount = mLinearLayoutManager.getItemCount();
                int lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
                if (lastVisibleItem >= totalItemCount - 2 && !stop) {

                    if (Integer.parseInt(Home_Fragment_Tab.alertCountVal) > totalItemCount) {
                        loadAlerts();
                    } else {
                        moreAlerts();
                    }

                }
            }
        });
    }

    private void moreAlerts() {
        stop = true;

        String offset = String.valueOf(readAlertsCount);
        readAlertsCount += 10;

        progress_layout.setVisibility(View.VISIBLE);
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("clientId", Constants.clientId);
            params.put("fpId", session.getFPID());
            params.put("isRead", "true");
            params.put("offset", offset);
            params.put("limit", "10");

            alertInterface.getAlerts(params, new Callback<ArrayList<AlertModel>>() {
                @Override
                public void success(ArrayList<AlertModel> alertModels, Response response) {

                    progress_layout.setVisibility(View.GONE);
                    if (alertModels == null || alertModels.size() == 0) {
                        if (alertModelsList.size() == 0) {
                            emptylayout.setVisibility(View.VISIBLE);
                        }
                    } else {
                        for (int i = 0; i < alertModels.size(); i++) {

                            alertModelsList.add(alertModels.get(i));
                        }
                        adapter.notifyDataSetChanged();
                        if (alertModels.size() >= 10) {
                            stop = false;
                        }
                    }

//                    Collections.sort(alertModelsList, new AlterDateComparator());
//                    adapter.notifyDataSetChanged();

                }

                @Override
                public void failure(RetrofitError error) {
                    stop = false;
                    if (alertModelsList.size() == 0) {
                        emptylayout.setVisibility(View.VISIBLE);
                    }
                    Log.i("ggg", "" + error.getMessage());
                    if (isAdded()) {
                        Methods.showSnackBarNegative(activity, getString(R.string.something_went_wrong_try_again));
                    }
                    progress_layout.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            Log.i("ggg", "API Exception:" + e);
            Methods.showSnackBarNegative(activity, getString(R.string.something_went_wrong_try_again));
            e.printStackTrace();
            progress_layout.setVisibility(View.GONE);
        }
    }

    @Override
    public void deepLink(String url) {
        if (getActivity() instanceof HomeActivity) {
            ((HomeActivity) getActivity()).deepLink(url);
        } else if (deepLinkUtil != null) {
            deepLinkUtil.deepLinkPage(url, "", false);
        }
    }


    public class AlterDateComparator implements Comparator<AlertModel> {
        public int compare(AlertModel left, AlertModel right) {
            return Methods.getFormattedDate(right.CreatedOn).compareTo(Methods.getFormattedDate(left.CreatedOn));
        }

    }

    public void loadAlerts() {
        String offset = String.valueOf(unReadAlertCount);
        unReadAlertCount += 10;

        stop = true;
        progress_layout.setVisibility(View.VISIBLE);
        mIsAlertShown = true;
        try {
            Map<String, String> params = new HashMap<String, String>();
            params.put("clientId", Constants.clientId);
            params.put("fpId", session.getFPID());
            params.put("isRead", "false");
            params.put("offset", offset);
            params.put("limit", "10");

            alertInterface.getAlerts(params, new Callback<ArrayList<AlertModel>>() {
                @Override
                public void success(ArrayList<AlertModel> alertModels, Response response) {
                    progress_layout.setVisibility(View.GONE);
                    if (alertModels == null) {
                        emptylayout.setVisibility(View.VISIBLE);
                        return;
                    } else if (alertModels.size() > 0) {
                        alertModelsList.addAll(alertModels);
                        /*for (int i = 0; i < alertModels.size(); i++) {
                            alertModelsList.add(alertModels.get(i));
                        }*/
                        adapter.notifyDataSetChanged();
                    }
                    stop = false;
                    if (alertModels.size() < 10) {
                        moreAlerts();
                    }

                }

                @Override
                public void failure(RetrofitError error) {
                    stop = false;
                    if (alertModelsList.size() == 0) {
                        emptylayout.setVisibility(View.VISIBLE);
                    }
                    if (getActivity() != null) {
                        Methods.showSnackBarNegative(getActivity(), getString(R.string.something_went_wrong_try_again));
                    }
                    progress_layout.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {

            Methods.showSnackBarNegative(activity, getString(R.string.something_went_wrong_try_again));
            e.printStackTrace();
            progress_layout.setVisibility(View.GONE);
        }
    }

    public static void getAlertCount(final UserSessionManager session, NotificationInterface notificationInterface, final Bus bus) {

        try {
            HashMap<String, String> map = new HashMap<>();
            map.put("fpId", session.getFPID());
            map.put("clientId", Constants.clientId);
            map.put("isRead", "false");
            notificationInterface.getAlertCount(map, new Callback<String>() {
                @Override
                public void success(String s, Response response) {
                    Home_Fragment_Tab.alertCountVal = s;
                    bus.post(new AlertCountEvent(s));
                    MixPanelController.setProperties("AlertCount", s);
                }

                @Override
                public void failure(RetrofitError error) {
                    Home_Fragment_Tab.alertCountVal = "0";
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            bus.post(new AlertCountEvent("0"));
        }
    }
}