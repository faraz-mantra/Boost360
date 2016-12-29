package com.nowfloats.NavigationDrawer;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nowfloats.Analytics_Screen.FacebookAnalyticsLogin;
import com.nowfloats.Analytics_Screen.Graph.AnalyticsActivity;
import com.nowfloats.Analytics_Screen.SearchQueries;
import com.nowfloats.Analytics_Screen.ShowWebView;
import com.nowfloats.Analytics_Screen.SubscribersActivity;
import com.nowfloats.Business_Enquiries.Business_Enquiries_Fragment;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.signup.UI.Service.Get_FP_Details_Service;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.MixPanelController;
import com.squareup.otto.Bus;
import com.thinksity.R;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class Analytics_Fragment extends Fragment {
    View rootView = null;
    static public TextView visitCount,subscriberCount,searchQueriesCount, businessEnqCount,facebokImpressions;
    private int noOfSearchQueries = 0;
    public static ProgressBar visits_progressBar,subscriber_progress, search_query_progress, businessEnqProgress;
    UserSessionManager session;
    private Context context;
    private Bus bus;

    @Override
    public void onResume() {
        getFPDetails(getActivity(), session.getFPID(), Constants.clientId, bus);

        MixPanelController.track(EventKeysWL.ANALYTICS_FRAGMENT,null);
        if(!Util.isNullOrEmpty(session.getVisitorsCount()))
        {
            visitCount.setText(session.getVisitorsCount());
        }
        if(!Util.isNullOrEmpty(session.getSubcribersCount()))
        {
            subscriberCount.setText(session.getSubcribersCount());
        }
        if(!Util.isNullOrEmpty(session.getFacebookImpressions())){
            facebokImpressions.setText(session.getFacebookImpressions());
        }
        else
            facebokImpressions.setText("0");
        super.onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new UserSessionManager(getActivity(),getActivity());
        bus = BusProvider.getInstance().getBus();
//        if(Util.isNullOrEmpty(session.getVisitorsCount()) || Util.isNullOrEmpty(session.getSubcribersCount())){
        try {
            //GetVisitorsAndSubscribersCountAsyncTask visit_subcribersCountAsyncTask = new GetVisitorsAndSubscribersCountAsyncTask(getActivity(), session);
            //visit_subcribersCountAsyncTask.execute();
        }catch(Exception e){e.printStackTrace();}
//        }
    }
    private void getFPDetails(Activity activity, String fpId, String clientId, Bus bus) {
        new Get_FP_Details_Service(activity,fpId,clientId,bus);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_analytics, container, false);
        LinearLayout queryLayout = (LinearLayout) rootView.findViewById(R.id.analytics_screen_search_queries);
        queryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MixPanelController.track("SearchQueriesDetailedView",null);
                Intent q = new Intent(getActivity(), SearchQueries.class);
                startActivity(q);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        LinearLayout enqLayOut =(LinearLayout) rootView.findViewById(R.id.analytics_screen_business_enq);
        enqLayOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainFrame,new Business_Enquiries_Fragment())
                        .commit();
            }
        });

        LinearLayout visitsLinearLayout = (LinearLayout) rootView.findViewById(R.id.numberOfVisitsLinearLayout);
        visitsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MixPanelController.track("OverallVisitsDetailedView",null);
                Intent q = new Intent(getActivity(), AnalyticsActivity.class);
                startActivity(q);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        LinearLayout subscribeLinearLayout = (LinearLayout) rootView.findViewById(R.id.subscribers_details);
        subscribeLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SubscribersActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        LinearLayout facebookLayout = (LinearLayout) rootView.findViewById(R.id.facebook_analytics_layout);
        facebookLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
                int status =pref.getInt("fbPageStatus",3);

                if(pref.getBoolean("fbPageShareEnabled",false) && status==1)
                {
                    Intent i = new Intent(getActivity(), ShowWebView.class);
                    startActivity(i);
                }
                else
                {
                    //Log.v("ggg",pref.getBoolean("fbPageShareEnabled",false)+"frag_ana"+status);
                    Intent i = new Intent(getActivity(), FacebookAnalyticsLogin.class);
                    i.putExtra("GetStatus",status);
                    startActivity(i);
                }
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(getResources().getColor(R.color.primaryColor), PorterDuff.Mode.SRC_IN);
        ImageView galleryBack = (ImageView) rootView.findViewById(R.id.pop_up_gallery_img);
        ImageView subsBack = (ImageView) rootView.findViewById(R.id.pop_up_subscribers_img);
        ImageView searchBack = (ImageView) rootView.findViewById(R.id.pop_up_search_img);
        ImageView businessEnqBg = (ImageView) rootView.findViewById(R.id.business_enq_bg);
        ImageView ivFbAnalytics = (ImageView) rootView.findViewById(R.id.iv_fb_page_analytics);


        galleryBack.setColorFilter(porterDuffColorFilter);
        subsBack.setColorFilter(porterDuffColorFilter);
        searchBack.setColorFilter(porterDuffColorFilter);
        businessEnqBg.setColorFilter(porterDuffColorFilter);
        ivFbAnalytics.setColorFilter(porterDuffColorFilter);

        visitCount = (TextView) rootView.findViewById(R.id.analytics_screen_visitor_count);
        subscriberCount = (TextView) rootView.findViewById(R.id.analytics_screen_subscriber_count);
        searchQueriesCount = (TextView) rootView.findViewById(R.id.analytics_screen_search_queries_count);
        businessEnqCount = (TextView) rootView.findViewById(R.id.analytics_screen_business_enq_count);
        facebokImpressions = (TextView) rootView.findViewById(R.id.analytics_screen_updates_count);
        searchQueriesCount.setVisibility(View.INVISIBLE);
        visits_progressBar = (ProgressBar) rootView.findViewById(R.id.visits_progressBar);
        visits_progressBar.setVisibility(View.VISIBLE);
        subscriber_progress = (ProgressBar) rootView.findViewById(R.id.subscriber_progressBar);
        subscriber_progress.setVisibility(View.VISIBLE);
        search_query_progress = (ProgressBar) rootView.findViewById(R.id.search_query_progressBar);
        search_query_progress.setVisibility(View.VISIBLE);
        businessEnqProgress = (ProgressBar) rootView.findViewById(R.id.business_enq_progressBar);
        businessEnqProgress.setVisibility(View.VISIBLE);


        String visittotal  = session.getVisitorsCount();
        String subscribetotal  = session.getSubcribersCount();
        String searchQueryCount = session.getSearchCount();
        String enquiryCount = session.getLatestEnqCount();
//        String Str_noOfSearchQueries = "";

        try {
            if (visittotal!=null && visittotal.length()>0 && !visittotal.contains(",")){
                visittotal = NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(visittotal));
            }
            if (subscribetotal!=null && subscribetotal.length()>0 && !subscribetotal.contains(",")){
                subscribetotal = NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(subscribetotal));
            }
//            Str_noOfSearchQueries = NumberFormat.getNumberInstance(Locale.US).format(noOfSearchQueries);
        }catch(Exception e){
            e.printStackTrace();
        }

        if (visittotal!=null && visittotal.trim().length()>0){
            visits_progressBar.setVisibility(View.GONE);
            visitCount.setVisibility(View.VISIBLE);
            visitCount.setText(visittotal);
        }else{
            visits_progressBar.setVisibility(View.VISIBLE);
            visitCount.setVisibility(View.GONE);
        }

        if (subscribetotal!=null && subscribetotal.trim().length()>0){
            subscriber_progress.setVisibility(View.GONE);
            subscriberCount.setVisibility(View.VISIBLE);
            subscriberCount.setText(subscribetotal);
        }else{
            subscriber_progress.setVisibility(View.VISIBLE);
            subscriberCount.setVisibility(View.GONE);
        }
        if (searchQueryCount!=null && searchQueryCount.trim().length()>0){
            search_query_progress.setVisibility(View.GONE);
            searchQueriesCount.setVisibility(View.VISIBLE);
            searchQueriesCount.setText(searchQueryCount);
        }else{
            search_query_progress.setVisibility(View.VISIBLE);
            searchQueriesCount.setVisibility(View.GONE);
        }
        if(enquiryCount!=null && enquiryCount.trim().length() > 0){
            businessEnqProgress.setVisibility(View.GONE);
            businessEnqCount.setVisibility(View.VISIBLE);
            businessEnqCount.setText(enquiryCount);
        }else {
            businessEnqProgress.setVisibility(View.VISIBLE);
            businessEnqCount.setVisibility(View.GONE);
        }
        Log.i("Visits",""+visittotal);
        Log.i("Subscribes",""+subscribetotal);
       /* if (noOfSearchQueries == 0){
            searchQueriesCount.setText("");
        }else {
            searchQueriesCount.setText(""+Str_noOfSearchQueries);
        }*/

        return rootView;
    }
}
