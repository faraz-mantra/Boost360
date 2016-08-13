package com.nowfloats.NavigationDrawer;


import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nowfloats.Analytics_Screen.Graph.VisitsPage;
import com.nowfloats.Analytics_Screen.SearchQueries;
import com.nowfloats.Analytics_Screen.SubscribersActivity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.GetVisitorsAndSubscribersCountAsyncTask;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class Analytics_Fragment extends Fragment {
    View rootView = null;
    static public TextView visitCount,subscriberCount,searchQueriesCount;
    private int noOfSearchQueries = 0;
    public static ProgressBar visits_progressBar,subscriber_progress;
    UserSessionManager session;

    @Override
    public void onResume() {
        MixPanelController.track(EventKeysWL.ANALYTICS_FRAGMENT,null);
        if(!Util.isNullOrEmpty(session.getVisitorsCount()))
        {
            visitCount.setText(session.getVisitorsCount());
        }
        if(!Util.isNullOrEmpty(session.getSubcribersCount()))
        {
            subscriberCount.setText(session.getSubcribersCount());
        }
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new UserSessionManager(getActivity(),getActivity());
//        if(Util.isNullOrEmpty(session.getVisitorsCount()) || Util.isNullOrEmpty(session.getSubcribersCount())){
        try {
            GetVisitorsAndSubscribersCountAsyncTask visit_subcribersCountAsyncTask = new GetVisitorsAndSubscribersCountAsyncTask(getActivity(), session);
            visit_subcribersCountAsyncTask.execute();
        }catch(Exception e){e.printStackTrace();}
//        }
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

        LinearLayout visitsLinearLayout = (LinearLayout) rootView.findViewById(R.id.numberOfVisitsLinearLayout);
        visitsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MixPanelController.track("OverallVisitsDetailedView",null);
                Intent q = new Intent(getActivity(), VisitsPage.class);
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

        PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(getResources().getColor(R.color.primaryColor), PorterDuff.Mode.SRC_IN);
        ImageView galleryBack = (ImageView) rootView.findViewById(R.id.pop_up_gallery_img);
        ImageView subsBack = (ImageView) rootView.findViewById(R.id.pop_up_subscribers_img);
        ImageView searchBack = (ImageView) rootView.findViewById(R.id.pop_up_search_img);

        galleryBack.setColorFilter(porterDuffColorFilter);
        subsBack.setColorFilter(porterDuffColorFilter);
        searchBack.setColorFilter(porterDuffColorFilter);

        visitCount = (TextView) rootView.findViewById(R.id.analytics_screen_visitor_count);
        subscriberCount = (TextView) rootView.findViewById(R.id.analytics_screen_subscriber_count);
        searchQueriesCount = (TextView) rootView.findViewById(R.id.analytics_screen_search_queries_count);
        searchQueriesCount.setVisibility(View.INVISIBLE);
        visits_progressBar = (ProgressBar) rootView.findViewById(R.id.visits_progressBar);
        visits_progressBar.setVisibility(View.VISIBLE);
        subscriber_progress = (ProgressBar) rootView.findViewById(R.id.subscriber_progressBar);
        subscriber_progress.setVisibility(View.VISIBLE);


        String visittotal  = session.getVisitorsCount();
        String subscribetotal  = session.getSubcribersCount();
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
