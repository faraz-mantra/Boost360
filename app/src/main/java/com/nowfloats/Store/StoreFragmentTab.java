package com.nowfloats.Store;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.nowfloats.AccountDetails.Model.AccountDetailModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.NavigationDrawer.SlidingTabLayout;
import com.nowfloats.Store.Adapters.StorePagerAdapter;
import com.nowfloats.Store.Model.ActiveWidget;
import com.nowfloats.Store.Model.StoreEvent;
import com.nowfloats.Store.Model.StoreModel;
import com.nowfloats.Store.Service.API_Service;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.QueryMap;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class StoreFragmentTab extends Fragment {
    public static ViewPager viewPager = null;
    StorePagerAdapter tabPagerAdapter;
    SlidingTabLayout tabs;
    UserSessionManager session;
    private Bus bus;
    public Activity activity;
    LinearLayout progressLayout;
    public static ArrayList<StoreModel> activeWidgetModels = new ArrayList<>();
    public static ArrayList<StoreModel> additionalWidgetModels = new ArrayList<>();


    @Override
    public void onResume() {
        super.onResume();
        MixPanelController.track(EventKeysWL.STORE_FRAGMENT, null);
        HomeActivity.headerText.setText("Pricing Plans");
        bus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        session = new UserSessionManager(activity.getApplicationContext(),activity);
        bus = BusProvider.getInstance().getBus();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(activity==null){activity = getActivity();}
                tabPagerAdapter = new StorePagerAdapter(getChildFragmentManager(), activity);
            }
        }).start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_home__fragment__tab, container, false);
        return mainView ;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressLayout = (LinearLayout)view.findViewById(R.id.progress_layout);
        progressLayout.setVisibility(View.VISIBLE);
        tabs = (SlidingTabLayout) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.homeTabViewpager);

        new API_Service(activity, session.getSourceClientId(),session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY),
                session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ACCOUNTMANAGERID),session.getFPID(),bus);
        BoostLog.d("StoreFragmentTab", session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY));
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        viewPager.setAdapter(tabPagerAdapter);
                        tabs.setDistributeEvenly(true);
                        tabs.setCustomTabView(R.layout.tab_text,R.id.tab_textview);
//                      tabs.setSelectedIndicatorColors(getResources().getColor(R.color.white));
                        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                            @Override
                            public int getIndicatorColor(int position) {
                                return getResources().getColor(R.color.white);
                            }
                        });
                        // Setting the ViewPager For the SlidingTabsLayout
                        tabs.setViewPager(viewPager);
                        progressLayout.setVisibility(View.GONE);
                    }
                });
                }catch (InterruptedException e){
                    e.printStackTrace();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();*/
    }

    @Subscribe
    public void getStoreList(StoreEvent response){
        ArrayList<StoreModel> allModels = response.model.AllPackages;
        ArrayList<ActiveWidget> activeIdArray = response.model.ActivePackages;
        ArrayList<StoreModel> additionalPlans = response.model.AllPackages;
        if(allModels!=null && activeIdArray!=null){
            LoadActivePlans(activity,allModels,additionalPlans,activeIdArray);
        }else{
            Methods.showSnackBarNegative(activity,getString(R.string.something_went_wrong));
        }
    }








    private void LoadActivePlans(final Activity activity, ArrayList<StoreModel> allModels, final ArrayList<StoreModel> additionalPlan, ArrayList<ActiveWidget> acIdarray) {
        activeWidgetModels.clear();
        additionalWidgetModels.clear();
        try {
//            AccInfoInterface infoInterface = Constants.restAdapter.create(AccInfoInterface.class);
//            HashMap<String,String> valuadditionalWidgetModelses = new HashMap<>();
//            values.put("clientId", Constants.clientId);
//            values.put("fpId",session.getFPID());
//            infoInterface.getAccDetails(values,new Callback<ArrayList<AccountDetailModel>>() {
//                @Override
//                public void success(ArrayList<AccountDetailModel> accDetail, Response response) {
                    if (acIdarray!=null && acIdarray.size()>0){
                        /*for (int i = 0; i < allModels.size(); i++) {
                            for (int j=0; j < acIdarray.size(); j++){
                                if (allModels.get(i)._id.equals(acIdarray.get(j).ClientProductId)){
                                    activeWidgetModels.add(allModels.get(i));
//                                    additionalWidgetModels.remove(additionalDetails.get(i));
                                }else{
//                                    additionalWidgetModels.add(additionalDetails.get(i));
                                }
                            }
                        }
                        for (int i = 0; i < acIdarray.size(); i++) {
                            for (int j = 0; j < allModels.size(); j++) {
                                if (allModels.get(j)._id.equals(acIdarray.get(i).ClientProductId)){
                                    additionalPlan.remove(allModels.get(j));
                                }
                            }
                        }
                        additionalWidgetModels = additionalPlan;*/
                        for (int i=0; i<allModels.size(); i++){
                            boolean flag = false;
                            for (int j=0; j<acIdarray.size(); j++){
                                if(allModels.get(i)._id.equals(acIdarray.get(j).ClientProductId)){
                                    activeWidgetModels.add(allModels.get(i));
                                    flag = true;
                                }
                            }
                            if(!flag){
                                additionalWidgetModels.add(allModels.get(i));
                            }
                        }
                    }else{
                        additionalWidgetModels = allModels;
//                        Methods.showSnackBarNegative(activity,"Something went wrong");
                    }
//                }
//                @Override
//                public void failure(RetrofitError error) {
//                    Methods.showSnackBarNegative(activity,"Something went wrong");
//                }
//            });

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    viewPager.setAdapter(tabPagerAdapter);
                    tabs.setDistributeEvenly(true);
                    tabs.setCustomTabView(R.layout.tab_text,R.id.tab_textview);
                    //                      tabs.setSelectedIndicatorColors(getResources().getColor(R.color.white));
                    tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                        @Override
                        public int getIndicatorColor(int position) {
                            return getResources().getColor(R.color.white);
                        }
                    });
                    // Setting the ViewPager For the SlidingTabsLayout
                    tabs.setViewPager(viewPager);
                    progressLayout.setVisibility(View.GONE);
                }
            });
            BoostLog.d("Additional WidgetSize:", additionalWidgetModels.size() + "");
            BoostLog.d("Active WidgetSize:", activeWidgetModels.size() + "");
        }catch (Exception e){e.printStackTrace(); Methods.showSnackBarNegative(activity,getString(R.string.something_went_wrong_try_again));}
    }
    public interface AccInfoInterface{
        @GET("/Discover/v1/FloatingPoint/GetPaidWidgetDetailsForFP")
        public void getAccDetails(@QueryMap Map<String,String> map, Callback<ArrayList<AccountDetailModel>> callback);
    }
}