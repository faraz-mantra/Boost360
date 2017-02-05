package com.nowfloats.NavigationDrawer;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.nowfloats.Analytics_Screen.FacebookAnalyticsLogin;
import com.nowfloats.Analytics_Screen.Graph.AnalyticsActivity;
import com.nowfloats.Analytics_Screen.SearchQueries;
import com.nowfloats.Analytics_Screen.ShowWebView;
import com.nowfloats.Analytics_Screen.SubscribersActivity;
import com.nowfloats.Business_Enquiries.BusinessEnquiryActivity;
import com.nowfloats.CustomWidget.VerticalTextView;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.RiaNetworkInterface;
import com.nowfloats.NavigationDrawer.model.CoordinateList;
import com.nowfloats.NavigationDrawer.model.CoordinatesSet;
import com.nowfloats.NavigationDrawer.model.RiaCardModel;
import com.nowfloats.NavigationDrawer.model.Section;
import com.nowfloats.signup.UI.Service.Get_FP_Details_Service;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.MixPanelController;
import com.squareup.otto.Bus;
import com.squareup.picasso.Picasso;
import com.thinksity.R;

import java.security.spec.ECField;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.android.AndroidLog;
import retrofit.client.Response;

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
    CardView cvRiaCard;
    Button btnRiaCardLeft, btnRiaCrdRight;
    TextView tvRiaCardHeader;
    RiaCardDeepLinkListener mRiaCardDeepLinkListener;
    private static final String BUTTON_TYPE_DEEP_LINK = "DeepLink";
    private static final String BUTTON_TYPE_NEXT_NODE = "NextNode";
    LinearLayout llRiaCardSections;
    private enum SectionType{
        Text, Graph, Image
    }
    private static final String BAR = "Bar";
    private static final String LINE = "Line";

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
        try {
            mRiaCardDeepLinkListener = (RiaCardDeepLinkListener) context;
        }catch (ClassCastException e){
            e.printStackTrace();
        }
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
               Intent i = new Intent(context, BusinessEnquiryActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
        cvRiaCard = (CardView) rootView.findViewById(R.id.cvRiaCard);
        btnRiaCardLeft = (Button) rootView.findViewById(R.id.btnRiaResponse1);
        btnRiaCrdRight = (Button) rootView.findViewById(R.id.btnRiaResponse2);
        tvRiaCardHeader = (TextView) rootView.findViewById(R.id.tvRiaCardHeader);
        llRiaCardSections = (LinearLayout) rootView.findViewById(R.id.llRiaCardSections);

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
        initRiaCard();

        return rootView;
    }

    private void initRiaCard() {
        RestAdapter riaCardAdapter = new RestAdapter.Builder().setEndpoint(Constants.RIA_API_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new AndroidLog("Retrofit Response"))
                .build();

        RiaNetworkInterface networkInterface = riaCardAdapter.create(RiaNetworkInterface.class);
        networkInterface.getRiaCards(session.getFpTag(), new Callback<ArrayList<RiaCardModel>>() {
            @Override
            public void success(ArrayList<RiaCardModel> riaCardModels, Response response) {
                if(riaCardModels!=null){
                    cvRiaCard.setVisibility(View.VISIBLE);
                    drawRiaCards(riaCardModels);
                    Log.d("Analytics Fragment", "Can reach this");
                }else {
                    Log.d("Analytics Fragment", "Can't reach this");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                Log.d("Analytics Fragment", "Can't reach this");
            }
        });
    }


    private void drawRiaCards(final List<RiaCardModel> riaCardModels) {
        RiaCardModel rootCard = riaCardModels.get(0);
        RiaCardResponseListener listener = new RiaCardResponseListener() {
            @Override
            public void onResponse(String buttonId, String NextNodeId) {
                Log.d("NextNodeId", NextNodeId);
                for(RiaCardModel riaCard: riaCardModels){
                    if(riaCard.getId().equals(NextNodeId)){
                        drawSingleRiaCard(riaCard, this);
                        Log.d("NextNodeId", "This is getting called");
                        break;
                    }
                }
            }
        };
        //cvRiaCard.setAnimation(outToLeftAnimation());
        //cvRiaCard.animate();
        drawSingleRiaCard(rootCard, listener);

    }

    private void drawSingleRiaCard(RiaCardModel rootCard, final RiaCardResponseListener listener) {
        tvRiaCardHeader.setText(rootCard.getHeaderText());
        //TODO: Handle for buttons null case
        final com.nowfloats.NavigationDrawer.model.Button btnLeft = rootCard.getButtons().get(0);
        final com.nowfloats.NavigationDrawer.model.Button btnRight = rootCard.getButtons().get(1);
        btnRiaCardLeft.setText(btnLeft.getButtonText());
        btnRiaCardLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnLeft.getButtonType().equals(BUTTON_TYPE_DEEP_LINK) && btnLeft.getDeepLinkUrl()!=null){
                    //TODO: do the deep link
                    mRiaCardDeepLinkListener.onDeepLink(btnLeft.getDeepLinkUrl());
                }else if(btnLeft.getButtonType().equals(BUTTON_TYPE_NEXT_NODE) && btnLeft.getNextNodeId()!=null){
                    listener.onResponse(btnLeft.getId(), btnLeft.getNextNodeId());
                }
            }
        });
        btnRiaCrdRight.setText(btnRight.getButtonText());
        btnRiaCrdRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnRight.getButtonType().equals(BUTTON_TYPE_DEEP_LINK) && btnRight.getDeepLinkUrl()!=null){
                    //TODO: do the deep link
                    mRiaCardDeepLinkListener.onDeepLink(btnRight.getDeepLinkUrl());
                }else if(btnRight.getButtonType().equals(BUTTON_TYPE_NEXT_NODE) && btnRight.getNextNodeId()!=null){
                    listener.onResponse(btnRight.getId(), btnRight.getNextNodeId());
                }
            }
        });

        drawSectionsForCard(rootCard.getSections(), llRiaCardSections);
        cvRiaCard.setAnimation(inFromRightAnimation());
        cvRiaCard.animate();
    }

    private void drawSectionsForCard(List<Section> sections, LinearLayout llRiaCardSections) {
        if(llRiaCardSections==null){
            return;
        }
        llRiaCardSections.removeAllViews();
        for(int i=0; i<sections.size(); i++){
            Section widget = sections.get(i);
            switch (SectionType.valueOf(widget.getSectionType())){
                case Text:
                    attachText(widget, llRiaCardSections);
                    break;
                case Graph:
                    attachGraph(widget, llRiaCardSections);
                    break;
                case Image:
                    attachImage(widget, llRiaCardSections);
                    break;
            }
        }
    }

    private void attachText(Section widget, LinearLayout llRiaCardSections) {
        TextView tv = new TextView(getActivity());
        tv.setText(widget.getText());
        tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        llRiaCardSections.addView(tv);
    }

    private void attachGraph(Section widget, LinearLayout llRiaCardSections) {
        //TODO: Do it properly after getting confirmed data
        LinearLayout graph = new LinearLayout(getActivity());
        graph.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        graph.setGravity(Gravity.CENTER_VERTICAL);
        VerticalTextView yAxisName = new VerticalTextView(getActivity(), null);
        yAxisName.setText(widget.getY().getLabel());
        yAxisName.setTextSize(dpToPx(4));
        graph.addView(yAxisName);
        if(!(widget.getY().getAxisType().equals("Integer") || widget.getY().getAxisType().equals("Double"))){
            cvRiaCard.setVisibility(View.GONE);
            return;
        }
        if(widget.getGraphType().equals(BAR)){
            BarChart barChart = new BarChart(getActivity());
            barChart.getAxisLeft().setDrawGridLines(false);
            barChart.getXAxis().setDrawGridLines(false);
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            ArrayList<String> xVals = new ArrayList<>();
            for(CoordinatesSet coordinateSet : widget.getCoordinatesSet()){
                List<BarEntry> dataEntry = new ArrayList<>();
                int i = coordinateSet.getCoordinateList().size()-1;
                for(CoordinateList coordinate: coordinateSet.getCoordinateList()){
                    dataEntry.add(new BarEntry(Float.parseFloat(coordinate.getY()), i));
                    try {
                        String splitData[] = (coordinate.getX() + "").split("-");
                        xVals.add(splitData[0] + "-W " + splitData[1]);
                    }catch (Exception e){

                    }
                    i--;
                }
                BarDataSet dataSet = new BarDataSet(dataEntry, coordinateSet.getLegendName());
                //dataSet.setDrawFilled(true);
                dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                dataSets.add(dataSet);
            }
            barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            Collections.reverse(xVals);
            BarData lineDataMain = new BarData(xVals, dataSets);
            barChart.setData(lineDataMain);
            barChart.invalidate();
            barChart.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(200)));
            graph.addView(barChart);
            llRiaCardSections.addView(graph);
            TextView xAxisNames = new TextView(getActivity());
            xAxisNames.setText(widget.getX().getLabel());
            xAxisNames.setTextSize(dpToPx(4));
            xAxisNames.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            xAxisNames.setGravity(Gravity.CENTER_HORIZONTAL);
            llRiaCardSections.addView(xAxisNames);

        }else if(widget.getGraphType().equals(LINE)){
            LineChart lineChart = new LineChart(getActivity());
            lineChart.getAxisLeft().setDrawGridLines(false);
            lineChart.getXAxis().setDrawGridLines(false);
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            ArrayList<String> xVals = new ArrayList<>();
            for(CoordinatesSet coordinateSet : widget.getCoordinatesSet()){
                List<Entry> dataEntry = new ArrayList<>();
                int i = coordinateSet.getCoordinateList().size()-1;
                for(CoordinateList coordinate: coordinateSet.getCoordinateList()){
                    dataEntry.add(new Entry(Float.parseFloat(coordinate.getY()), i));
                    try {
                        String splitData[] = (coordinate.getX() + "").split("-");
                        xVals.add("W-" + splitData[1] + " " + splitData[0].substring(2, 4));
                    }catch (Exception e){

                    }
                    i--;
                }
                LineDataSet dataSet = new LineDataSet(dataEntry, coordinateSet.getLegendName());
                dataSet.setDrawFilled(true);
                dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                dataSets.add(dataSet);
            }
            lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            Collections.reverse(xVals);
            LineData lineDataMain = new LineData(xVals, dataSets);
            lineChart.setData(lineDataMain);
            lineChart.invalidate();
            lineChart.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(200)));
            graph.addView(lineChart);
            llRiaCardSections.addView(graph);
            TextView xAxisNames = new TextView(getActivity());
            xAxisNames.setText(widget.getX().getLabel());
            xAxisNames.setTextSize(dpToPx(4));
            xAxisNames.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            xAxisNames.setGravity(Gravity.CENTER_HORIZONTAL);
            llRiaCardSections.addView(xAxisNames);

        }
    }

    private int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private Animation inFromRightAnimation() {

        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(250);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    private void attachImage(Section widget, LinearLayout llRiaCardSections) {
        ImageView iv = new ImageView(getActivity());
        iv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 150));
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Picasso.with(getActivity()).load(widget.getUrl()).into(iv);
        llRiaCardSections.addView(iv);
    }

    public interface RiaCardResponseListener{
        void onResponse(String buttonId, String NextNodeId);
    }

    public interface RiaCardDeepLinkListener{
        void onDeepLink(String deepLinkUrl);
    }
}
