package com.nowfloats.NavigationDrawer;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.gson.JsonObject;
import com.nowfloats.Analytics_Screen.API.CallTrackerApis;
import com.nowfloats.Analytics_Screen.Graph.SiteViewsAnalytics;
import com.nowfloats.Analytics_Screen.Graph.api.AnalyticsFetch;
import com.nowfloats.Analytics_Screen.Graph.fragments.UniqueVisitorsFragment;
import com.nowfloats.Analytics_Screen.Graph.model.VisitsModel;
import com.nowfloats.Analytics_Screen.OrderSummaryActivity;
import com.nowfloats.Analytics_Screen.RevenueSummaryActivity;
import com.nowfloats.Analytics_Screen.SearchQueriesActivity;
import com.nowfloats.Analytics_Screen.SearchRankingActivity;
import com.nowfloats.Analytics_Screen.SocialAnalytics;
import com.nowfloats.Analytics_Screen.SubscribersActivity;
import com.nowfloats.Analytics_Screen.VmnCallCardsActivity;
import com.nowfloats.Business_Enquiries.BusinessEnquiryActivity;
import com.nowfloats.CustomWidget.VerticalTextView;
import com.nowfloats.CustomWidget.roboto_lt_24_212121;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.model.CoordinateList;
import com.nowfloats.NavigationDrawer.model.CoordinatesSet;
import com.nowfloats.NavigationDrawer.model.RiaCardModel;
import com.nowfloats.NavigationDrawer.model.RiaNodeDataModel;
import com.nowfloats.NavigationDrawer.model.Section;
import com.nowfloats.manageinventory.interfaces.WebActionCallInterface;
import com.nowfloats.manageinventory.models.SellerSummary;
import com.nowfloats.on_boarding.OnBoardingManager;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.RiaEventLogger;
import com.nowfloats.util.Utils;
import com.nowfloats.util.WebEngageController;
import com.nowfloats.widget.WidgetKey;
import com.squareup.otto.Bus;
import com.thinksity.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.framework.webengageconstant.EventLabelKt.EVENT_LABEL_NULL;
import static com.framework.webengageconstant.EventLabelKt.SEARCH_RANKING;
import static com.framework.webengageconstant.EventNameKt.ROI_SUMMARY_ADDRESS_VIEWS;
import static com.framework.webengageconstant.EventNameKt.SALES_ANALYTICS;
import static com.framework.webengageconstant.EventNameKt.SEARCH_ANALYTICS;
import static com.framework.webengageconstant.EventNameKt.SOCIAL_ANALYTICS_DETAILS_FROM_HOME;
import static com.framework.webengageconstant.EventNameKt.SUBSCRIBERS;
import static com.framework.webengageconstant.EventNameKt.WEBSITE_VISITS_CHART_DURATION_CHANGED;
import static com.framework.webengageconstant.EventNameKt.WILDFIRE_ANALYTICS;
import static com.nowfloats.Analytics_Screen.Graph.SiteViewsAnalytics.VISITS_TYPE;

/**
 * A simple {@link Fragment} subclass.
 */
public class Analytics_Fragment extends Fragment {
    private static final String BUTTON_TYPE_DEEP_LINK = "DeepLink";
    private static final String BUTTON_TYPE_NEXT_NODE = "NextNode";
    private static final String BUTTON_TYPE_EXIT = "None";
    private static final String BUTTON_TYPE_OPEN_URL = "OpenUrl";
    private static final String BAR = "Bar";
    private static final String LINE = "Line";
    public static TextView visitCount, mapVisitsCount, visitorsCount, subscriberCount, vmnTotalCallCount, vmnTotalCustomerCount,
            searchQueriesCount, businessEnqCount, facebokImpressions, tvOrdersCount;
    public static ProgressBar visits_progressBar, map_progressbar, visitors_progressBar, vmnProgressBar, vmnCustomerProgressBar,
            subscriber_progress, search_query_progress, businessEnqProgress, pbOrders;
    private static ImageView rupeeSymbol;
    private final int noOfSearchQueries = 0;
    View rootView = null;
    UserSessionManager session;
    CardView cvRiaCard, vmnCallCard, cvCustomerAppointment;
    Button btnRiaCardLeft, btnRiaCrdRight, btnSingleResponse;
    TextView tvRiaCardHeader;
    //LinearLayout mLockLayout;
    RiaCardDeepLinkListener mRiaCardDeepLinkListener;
    LinearLayout llRiaCardSections;
    roboto_lt_24_212121 customerAppointmentTitle;
    RiaCardResponseListener mListener = null;
    LinearLayout llTwoButtons, llSingleButtonLayout;
    SharedPreferences pref;
    OnBoardingManager onBoardingManager;
    private Context context;
    private Bus bus;
    private String mButtonId;
    private String mNextNodeId;
    private String vmnTotalCalls;

    public static String getNumberFormat(String value) {
        try {
            return NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(value));
        } catch (Exception e) {
            return value;
        }
    }

    @Override
    public void onResume() {

        //Log.d("FCM Token", FirebaseInstanceId.getInstance().getToken());
        //getFPDetails(getActivity(), session.getFPID(), Constants.clientId, bus);
        //enableLockScreen();

        MixPanelController.track(EventKeysWL.ANALYTICS_FRAGMENT, null);
        if (!Util.isNullOrEmpty(session.getVisitorsCount())) {
            visitorsCount.setText(getNumberFormat(session.getVisitorsCount()));
        }
        if (!Util.isNullOrEmpty(session.getVisitsCount())) {
            visitCount.setText(getNumberFormat(session.getVisitsCount()));
        }
        if (!Util.isNullOrEmpty(session.getSubcribersCount())) {
            subscriberCount.setText(session.getSubcribersCount());
        }
        if (!Util.isNullOrEmpty(session.getEnquiryCount())) {
            businessEnqCount.setText(getNumberFormat(session.getEnquiryCount()));
        }
        if (!Util.isNullOrEmpty(session.getOrderCount())) {
            tvOrdersCount.setText(getNumberFormat(session.getOrderCount()));
        }
        if (!Util.isNullOrEmpty(session.getMapVisitsCount())) {
            mapVisitsCount.setText(session.getMapVisitsCount());
        }
        if (!Util.isNullOrEmpty(session.getFacebookImpressions())) {
            facebokImpressions.setText(getNumberFormat(session.getFacebookImpressions()));
        } else {
            facebokImpressions.setText("0");
        }

        if (mListener != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (getActivity() != null && mButtonId != null && mNextNodeId != null) {
                        if (RiaEventLogger.isLastEventCompleted) {
                            mListener.onResponse(mButtonId, mNextNodeId);
                        }

                    } else if (mNextNodeId == null) {
                        if (RiaEventLogger.isLastEventCompleted) {
                            cvRiaCard.setVisibility(View.GONE);
                            bus.post(new ArrayList<RiaCardModel>());
                            RiaEventLogger.lastEventStatus = false;
                        }
                    }
                }
            }, 200);
        }
        super.onResume();
    }

//    private void enableLockScreen() {
//
//        if (!pref.getBoolean(Key_Preferences.ON_BOARDING_STATUS, false)
//                && (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equals("1") ||
//                session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equals("0"))) {
//            //onBoardingManager.getOnBoardingData(session.getFpTag());
//            mLockLayout.setVisibility(View.VISIBLE);
//        } else {
//            mLockLayout.setVisibility(View.GONE);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mRiaCardDeepLinkListener = (RiaCardDeepLinkListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        this.context = context;
//        WebEngageController.trackEvent("HOME SCREEN", "Clicked on reports", null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        session = new UserSessionManager(getActivity(), getActivity());
        bus = BusProvider.getInstance().getBus();
        pref = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        onBoardingManager = new OnBoardingManager(getContext());
//        if(Util.isNullOrEmpty(session.getVisitorsCount()) || Util.isNullOrEmpty(session.getSubcribersCount())){
        try {
            //GetVisitorsAndSubscribersCountAsyncTask visit_subcribersCountAsyncTask = new GetVisitorsAndSubscribersCountAsyncTask(getActivity(), session);
            //visit_subcribersCountAsyncTask.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        WebEngageController.trackEvent("HOME SCREEN", "Clicked on reports", null);
//        }
    }

    private void getMapVisitsCount() {

        HashMap<String, String> map = new HashMap<>();
        map.put("batchType", UniqueVisitorsFragment.BatchType.yy.name());
        map.put("startDate", Methods.getFormattedDate(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CREATED_ON), UniqueVisitorsFragment.pattern));
        map.put("endDate", Methods.getFormattedDate(Calendar.getInstance().getTimeInMillis(), UniqueVisitorsFragment.pattern));
        map.put("clientId", Constants.clientId);
        map.put("scope", session.getISEnterprise().equals("true") ? "Enterprise" : "Store");
        Constants.restAdapter.create(AnalyticsFetch.FetchDetails.class).getMapVisits(session.getFpTag(), map, new Callback<VisitsModel>() {
            @Override
            public void success(VisitsModel visitsModel, Response response) {
                int totalCount = 0;
                if (visitsModel != null) {
                    for (VisitsModel.UniqueVisitsList data : visitsModel.getUniqueVisitsList()) {
                        totalCount += data.getDataCount();
                    }
                }
                session.setMapVisitsCount(String.valueOf(totalCount));
                if (isAdded() && getActivity() != null) {
                    map_progressbar.setVisibility(View.GONE);
                    mapVisitsCount.setVisibility(View.VISIBLE);
                    mapVisitsCount.setText(String.valueOf(totalCount));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (isAdded() && getActivity() != null) {
                    map_progressbar.setVisibility(View.GONE);
                }
            }
        });
    }

//    private void setAnalyticsLockScreen() {
//        ((ImageView) mLockLayout.findViewById(R.id.image1)).setImageResource(R.drawable.ic_lock);
//        TextView actionButton = mLockLayout.findViewById(R.id.btn_action);
//        TextView title = mLockLayout.findViewById(R.id.main_text1);
//        title.setText("SEO DISABLED");
//        TextView message = mLockLayout.findViewById(R.id.message_text2);
//        title.setTextColor(ContextCompat.getColor(context, R.color.white));
//        message.setTextColor(ContextCompat.getColor(context, R.color.white));
//        message.setText("Complete onboarding to unlock analytics ");
//        actionButton.setText("Resume Onboarding");
//        actionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBoardingManager.getOnBoardingData(session.getFpTag(), mLockLayout);
//            }
//        });
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_analytics, container, false);
        LinearLayout queryLayout = rootView.findViewById(R.id.analytics_screen_search_queries);
        llTwoButtons = rootView.findViewById(R.id.ll_twobuttons);
        llSingleButtonLayout = rootView.findViewById(R.id.ll_single_button);
        btnSingleResponse = rootView.findViewById(R.id.btnSingleResponse);
        vmnCallCard = rootView.findViewById(R.id.card_view_vmn_call);
        cvCustomerAppointment = rootView.findViewById(R.id.card_view_customer_appointment);
        customerAppointmentTitle = rootView.findViewById(R.id.customer_appointment_title);
        //mLockLayout = rootView.findViewById(R.id.lock_analytics);
        //setAnalyticsLockScreen();
        queryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MixPanelController.track("SearchQueriesDetailedView", null);
                Intent q = new Intent(getActivity(), SearchQueriesActivity.class);
                startActivity(q);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        LinearLayout enqLayOut = rootView.findViewById(R.id.analytics_screen_business_enq);
        enqLayOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, BusinessEnquiryActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        LinearLayout visitsLinearLayout = rootView.findViewById(R.id.numberOfVisitsLinearLayout);
        visitsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebEngageController.trackEvent(WEBSITE_VISITS_CHART_DURATION_CHANGED, EVENT_LABEL_NULL, session.getFpTag());
                MixPanelController.track("OverallVisitsDetailedView", null);
                Intent q = new Intent(getActivity(), SiteViewsAnalytics.class);
                q.putExtra(VISITS_TYPE, SiteViewsAnalytics.VisitsType.TOTAL);
                startActivity(q);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        LinearLayout visitorsLinearLayout = rootView.findViewById(R.id.numberOfVisitorsLinearLayout);
        visitorsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MixPanelController.track("UniqueVisitsDetailedView", null);
                Intent q = new Intent(getActivity(), SiteViewsAnalytics.class);
                q.putExtra(VISITS_TYPE, SiteViewsAnalytics.VisitsType.UNIQUE);
                startActivity(q);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        LinearLayout mapVisitsLinearLayout = rootView.findViewById(R.id.mapVisitsLinearLayout);
        mapVisitsLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebEngageController.trackEvent(ROI_SUMMARY_ADDRESS_VIEWS, EVENT_LABEL_NULL, session.getFpTag());
                MixPanelController.track("MapVisitsDetailedView", null);
                Intent q = new Intent(getActivity(), SiteViewsAnalytics.class);
                q.putExtra(VISITS_TYPE, SiteViewsAnalytics.VisitsType.MAP_VISITS);
                startActivity(q);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        if (session.getISEnterprise().equals("true")) {
            rootView.findViewById(R.id.map_card).setVisibility(View.GONE);
        }
        LinearLayout subscribeLinearLayout = rootView.findViewById(R.id.subscribers_details);
        subscribeLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addSubscription();
                WebEngageController.trackEvent(ROI_SUMMARY_ADDRESS_VIEWS, EVENT_LABEL_NULL, session.getFpTag());

//                Intent i = new Intent(getActivity(), SubscribersActivity.class);
//                startActivity(i);
//                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        LinearLayout facebookLayout = rootView.findViewById(R.id.facebook_analytics_layout);
        facebookLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if (Constants.PACKAGE_NAME.equals("com.digitalseoz")) {
                    Toast.makeText(context, "This feature is coming soon", Toast.LENGTH_LONG).show();
                } else {*/
                WebEngageController.trackEvent(SOCIAL_ANALYTICS_DETAILS_FROM_HOME, EVENT_LABEL_NULL, session.getFpTag());
                int status = pref.getInt("fbPageStatus", 0);

                Intent i = new Intent(getActivity(), SocialAnalytics.class);
                i.putExtra("GetStatus", status);
                startActivity(i);

                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                // }
            }
        });

        LinearLayout orderLayout = rootView.findViewById(R.id.order_analytics_layout);
        orderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebEngageController.trackEvent(SALES_ANALYTICS, EVENT_LABEL_NULL, session.getFpTag());
//                Intent i = new Intent(getActivity(), OrderAnalyticsActivity.class);
                Intent i = new Intent(getActivity(), RevenueSummaryActivity.class);
                startActivity(i);

                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        LinearLayout llSearchRanking = rootView.findViewById(R.id.analytics_screen_search_ranking);
        llSearchRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebEngageController.trackEvent(SEARCH_ANALYTICS, SEARCH_RANKING, session.getFpTag());
                Intent i = new Intent(getActivity(), SearchRankingActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        CardView wildfireAnalytics = rootView.findViewById(R.id.card_view_wildfire);
        wildfireAnalytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebEngageController.trackEvent(WILDFIRE_ANALYTICS, EVENT_LABEL_NULL, session.getFpTag());
                Intent i = new Intent(getActivity(), WildFireAdsActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });


        cvRiaCard = rootView.findViewById(R.id.cvRiaCard);
        btnRiaCardLeft = rootView.findViewById(R.id.btnRiaResponse1);
        btnRiaCrdRight = rootView.findViewById(R.id.btnRiaResponse2);
        tvRiaCardHeader = rootView.findViewById(R.id.tvRiaCardHeader);
        llRiaCardSections = rootView.findViewById(R.id.llRiaCardSections);

//        PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(ContextCompat.getColor(context, R.color.primaryColor), PorterDuff.Mode.SRC_IN);
//        ImageView galleryBack = (ImageView) rootView.findViewById(R.id.pop_up_gallery_img);
//        ImageView mapBack = (ImageView) rootView.findViewById(R.id.map_visits_img);
//        ImageView subsBack = (ImageView) rootView.findViewById(R.id.pop_up_subscribers_img);
//        ImageView ivFbAnalytics = (ImageView) rootView.findViewById(R.id.iv_fb_page_analytics);
//        ImageView visitorsBack = (ImageView) rootView.findViewById(R.id.visitors_image_bg);
//        ImageView wildfireBack = (ImageView) rootView.findViewById(R.id.img_wildfire_back);
//        ImageView orderAnalyticsBack = rootView.findViewById(R.id.iv_order_analytics);


        /*galleryBack.setColorFilter(porterDuffColorFilter);
        mapBack.setColorFilter(porterDuffColorFilter);
        subsBack.setColorFilter(porterDuffColorFilter);
        searchBack.setColorFilter(porterDuffColorFilter);
        businessEnqBg.setColorFilter(porterDuffColorFilter);
        ivFbAnalytics.setColorFilter(porterDuffColorFilter);
        vmnCallBack.setColorFilter(porterDuffColorFilter);
        visitorsBack.setColorFilter(porterDuffColorFilter);
        searchRankBack.setColorFilter(porterDuffColorFilter);
        wildfireBack.setColorFilter(porterDuffColorFilter);
        orderAnalyticsBack.setColorFilter(porterDuffColorFilter);*/


        visitCount = rootView.findViewById(R.id.analytics_screen_visitor_count);
        mapVisitsCount = rootView.findViewById(R.id.analytics_screen_map_count);
        visitorsCount = rootView.findViewById(R.id.visitors_count);
        subscriberCount = rootView.findViewById(R.id.analytics_screen_subscriber_count);
        searchQueriesCount = rootView.findViewById(R.id.analytics_screen_search_queries_count);
        vmnTotalCallCount = rootView.findViewById(R.id.analytics_screen_vmn_tracker_count);
        vmnTotalCustomerCount = rootView.findViewById(R.id.customer_appointment_total_count);
        businessEnqCount = rootView.findViewById(R.id.analytics_screen_business_enq_count);
        tvOrdersCount = rootView.findViewById(R.id.orders_count);
        rupeeSymbol = rootView.findViewById(R.id.iv_rupee_symbol);
        facebokImpressions = rootView.findViewById(R.id.analytics_screen_updates_count);
        searchQueriesCount.setVisibility(View.INVISIBLE);
        visits_progressBar = rootView.findViewById(R.id.visits_progressBar);
        visits_progressBar.setVisibility(View.VISIBLE);
        map_progressbar = rootView.findViewById(R.id.map_progressBar);
        map_progressbar.setVisibility(View.VISIBLE);
        visitors_progressBar = rootView.findViewById(R.id.visitors_progressBar);
        visitors_progressBar.setVisibility(View.VISIBLE);
        subscriber_progress = rootView.findViewById(R.id.subscriber_progressBar);
        subscriber_progress.setVisibility(View.VISIBLE);
        vmnProgressBar = rootView.findViewById(R.id.vmn_progressbar);
        vmnCustomerProgressBar = rootView.findViewById(R.id.customer_appointment_progressbar);
        search_query_progress = rootView.findViewById(R.id.search_query_progressBar);
        search_query_progress.setVisibility(View.GONE);
        businessEnqProgress = rootView.findViewById(R.id.business_enq_progressBar);
        businessEnqProgress.setVisibility(View.VISIBLE);
        pbOrders = rootView.findViewById(R.id.order_progressBar);
        pbOrders.setVisibility(View.GONE);


        String visittotal = session.getVisitsCount();
        String visitortotal = session.getVisitorsCount();
        String subscribetotal = session.getSubcribersCount();
        String searchQueryCount = session.getSearchCount();
        String enquiryCount = session.getEnquiryCount();
        String orderCount = session.getOrderCount();
        String mapCount = session.getMapVisitsCount();
        setVmnTotalCallCount();
        setVmnTotalCustomerCount();         //calling customer Appointment
        customerAppointmentTitle.setText(Utils.getCustomerAppointmentTaxonomyFromServiceCode(session.getFP_AppExperienceCode()));
//        String Str_noOfSearchQueries = "";

        try {
            if (visittotal != null && visittotal.length() > 0 && !visittotal.contains(",")) {
                visittotal = NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(visittotal));
            }
            if (subscribetotal != null && subscribetotal.length() > 0 && !subscribetotal.contains(",")) {
                subscribetotal = NumberFormat.getNumberInstance(Locale.US).format(Integer.parseInt(subscribetotal));
            }
//            Str_noOfSearchQueries = NumberFormat.getNumberInstance(Locale.US).format(noOfSearchQueries);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (visittotal != null && visittotal.trim().length() > 0) {
            visits_progressBar.setVisibility(View.GONE);
            visitCount.setVisibility(View.VISIBLE);
            visitCount.setText(getNumberFormat(visittotal));
        } else {
            visits_progressBar.setVisibility(View.VISIBLE);
            visitCount.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(mapCount)) {
            map_progressbar.setVisibility(View.GONE);
            mapVisitsCount.setVisibility(View.VISIBLE);
            mapVisitsCount.setText(mapCount);
        } else {
            map_progressbar.setVisibility(View.VISIBLE);
            mapVisitsCount.setVisibility(View.GONE);
        }
        if (visitortotal != null && visitortotal.trim().length() > 0) {
            visitors_progressBar.setVisibility(View.GONE);
            visitorsCount.setVisibility(View.VISIBLE);
            visitorsCount.setText(getNumberFormat(visitortotal));
        } else {
            visitors_progressBar.setVisibility(View.VISIBLE);
            visitorsCount.setVisibility(View.GONE);
        }

        if (subscribetotal != null && subscribetotal.trim().length() > 0) {
            subscriber_progress.setVisibility(View.GONE);
            subscriberCount.setVisibility(View.VISIBLE);
            subscriberCount.setText(subscribetotal);
        } else {
            subscriber_progress.setVisibility(View.VISIBLE);
            subscriberCount.setVisibility(View.GONE);
        }
        if (searchQueryCount != null && searchQueryCount.trim().length() > 0) {
            search_query_progress.setVisibility(View.GONE);
            searchQueriesCount.setVisibility(View.VISIBLE);
            searchQueriesCount.setText(getNumberFormat(searchQueryCount));
        } else {
            search_query_progress.setVisibility(View.GONE);
            searchQueriesCount.setVisibility(View.GONE);
        }
        if (enquiryCount != null && enquiryCount.trim().length() > 0) {
            businessEnqProgress.setVisibility(View.GONE);
            businessEnqCount.setVisibility(View.VISIBLE);
            businessEnqCount.setText(getNumberFormat(enquiryCount));
        } else {
            businessEnqProgress.setVisibility(View.VISIBLE);
            businessEnqCount.setVisibility(View.GONE);
        }

        if (orderCount != null && orderCount.trim().length() > 0) {
            pbOrders.setVisibility(View.GONE);
            tvOrdersCount.setVisibility(View.VISIBLE);
            rupeeSymbol.setVisibility(View.VISIBLE);
            tvOrdersCount.setText(getNumberFormat(orderCount));
        } else {
//            pbOrders.setVisibility(View.VISIBLE);
            tvOrdersCount.setVisibility(View.GONE);
            rupeeSymbol.setVisibility(View.GONE);
        }

//        final boolean isVmnEnable = "VMN".equals(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NAME_1)) ||
//                "VMN".equals(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ALTERNATE_NAME_3)) ||
//                "VMN".equals(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NAME));
//        String paymentState = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE);
//        if (!Constants.PACKAGE_NAME.equals("com.biz2.nowfloats")) {
//            vmnCallCard.setVisibility(View.GONE);
//        }
//
//        else
//        {
//            setVmnTotalCallCount();
//            vmnCallCard.setVisibility(View.VISIBLE);
//        }

//        else if (isVmnEnable) {
//            setVmnTotalCallCount();
//            vmnCallCard.setVisibility(View.VISIBLE);
//        } else if ((!TextUtils.isEmpty(paymentState) && "1".equalsIgnoreCase(paymentState))) {
//            vmnCallCard.setVisibility(View.VISIBLE);
//            //request
//        }

        vmnCallCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (vmnTotalCallCount.getVisibility() == View.VISIBLE) {
                    Intent i = new Intent(getActivity(), VmnCallCardsActivity.class);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    Toast.makeText(context, getString(R.string.please_wait), Toast.LENGTH_SHORT).show();
                }

//                if (isVmnEnable) {
//                    if (vmnTotalCallCount.getVisibility() == View.VISIBLE) {
//                        Intent i = new Intent(getActivity(), VmnCallCardsActivity.class);
//                        startActivity(i);
//                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                    } else {
//                        Toast.makeText(context, getString(R.string.please_wait), Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Intent i = new Intent(getActivity(), VmnNumberRequestActivity.class);
//                    startActivity(i);
//                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                }

            }
        });

        cvCustomerAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vmnTotalCustomerCount.getVisibility() == View.VISIBLE) {
//                    Intent i = new Intent(getActivity(), VmnCallCardsActivity.class);
                    Intent i = new Intent(getActivity(), OrderSummaryActivity.class);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    Toast.makeText(context, getString(R.string.please_wait), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Disabling the below function as RiaChatSDK has been disabled from v5.3.7 onwards
        //initRiaCard();


        if (!session.getISEnterprise().equalsIgnoreCase("true")) {
            getMapVisitsCount();
        }

        //enableLockScreen();
        return rootView;
    }

//    private void initRiaCard() {
//        Map<String, String> query = new HashMap<>();
//        query.put("fpTag", session.getFpTag());
//        try {
//            query.put("appVersion", getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName);
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        query.put("deviceId", DeviceDetails.getDeviceId(getActivity()));
//        query.put("libVersion", DeviceDetails.getLibVersionName());
//        query.put("osVersion", DeviceDetails.getAndroidVersion());
//        query.put("osTimeZone", DeviceDetails.getTimeZone());
//        query.put("osCountry", DeviceDetails.getCountry());
//        query.put("osLanguage", DeviceDetails.getLanguage());
//        query.put("deviceBrand", DeviceDetails.getBrand());
//        query.put("deviceModel", DeviceDetails.getDeviceModel());
//        query.put("screenWidth", DeviceDetails.getScreenWidth(getActivity()) + "");
//        query.put("screenHeight", DeviceDetails.getScreenHeight(getActivity()) + "");
//
//
//        RiaNetworkInterface networkInterface = Constants.riaMemoryRestAdapter.create(RiaNetworkInterface.class);
//        networkInterface.getRiaCards(query, new Callback<ArrayList<RiaCardModel>>() {
//            @Override
//            public void success(ArrayList<RiaCardModel> riaCardModels, Response response) {
//                if (riaCardModels != null && getActivity() != null && riaCardModels.size() > 0) {
//                    cvRiaCard.setVisibility(View.VISIBLE);
//                    drawRiaCards(riaCardModels);
//                    bus.post(riaCardModels);
//                }
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                error.printStackTrace();
//            }
//        });
//    }


    private void drawRiaCards(final List<RiaCardModel> riaCardModels) {
        RiaCardModel rootCard = riaCardModels.get(0);
        mListener = new RiaCardResponseListener() {
            @Override
            public void onResponse(String buttonId, String NextNodeId) {

                for (RiaCardModel riaCard : riaCardModels) {
                    if (riaCard.getId().equals(NextNodeId)) {
                        drawSingleRiaCard(riaCard, this);
                        break;
                    }
                }
            }
        };
        drawSingleRiaCard(rootCard, mListener);

    }

    private void drawSingleRiaCard(final RiaCardModel rootCard, final RiaCardResponseListener listener) {
        llTwoButtons.setVisibility(View.GONE);
        llSingleButtonLayout.setVisibility(View.GONE);
        tvRiaCardHeader.setText(rootCard.getHeaderText());
        RiaEventLogger.isLastEventCompleted = false;
        if (rootCard.getButtons() == null || rootCard.getButtons().size() == 0) {

        } else if (rootCard.getButtons() != null && rootCard.getButtons().size() == 1) {
            llSingleButtonLayout.setVisibility(View.VISIBLE);
            final com.nowfloats.NavigationDrawer.model.Button btnSingle = rootCard.getButtons().get(0);
            btnSingleResponse.setText(btnSingle.getButtonText());
            btnSingleResponse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (btnSingle.getButtonType().equals(BUTTON_TYPE_DEEP_LINK) && btnSingle.getDeepLinkUrl() != null) {

                        mRiaCardDeepLinkListener.onDeepLink(btnSingle.getDeepLinkUrl(), true,
                                new RiaNodeDataModel(rootCard.getId(), btnSingle.getId(),
                                        btnSingle.getButtonText()));
                        mButtonId = btnSingle.getId();
                        mNextNodeId = btnSingle.getNextNodeId();

                    } else if (btnSingle.getButtonType().equals(BUTTON_TYPE_NEXT_NODE) && btnSingle.getNextNodeId() != null) {
                        listener.onResponse(btnSingle.getId(), btnSingle.getNextNodeId());
                    } else if (btnSingle.getButtonType().equals(BUTTON_TYPE_EXIT)) {
                        cvRiaCard.setVisibility(View.GONE);
                        bus.post(new ArrayList<RiaCardModel>());
                    } else if (btnSingle.getButtonType().equals(BUTTON_TYPE_OPEN_URL)) {
                        Intent intent = new Intent(getActivity(), RiaWebViewActivity.class);
                        intent.putExtra(RiaWebViewActivity.RIA_WEB_CONTENT_URL, btnSingle.getUrl());
                        intent.putExtra(RiaWebViewActivity.RIA_NODE_DATA, new RiaNodeDataModel(rootCard.getId(), btnSingle.getId(),
                                btnSingle.getButtonText()));
                        mButtonId = btnSingle.getId();
                        mNextNodeId = btnSingle.getNextNodeId();
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                    RiaEventLogger.getInstance().logClickEvent(session.getFpTag(), rootCard.getId(),
                            btnSingle.getId(), btnSingle.getButtonText());
                }
            });
        } else {
            llTwoButtons.setVisibility(View.VISIBLE);
            final com.nowfloats.NavigationDrawer.model.Button btnLeft = rootCard.getButtons().get(0);
            final com.nowfloats.NavigationDrawer.model.Button btnRight = rootCard.getButtons().get(1);
            btnRiaCardLeft.setText(btnLeft.getButtonText());
            btnRiaCardLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (btnLeft.getButtonType().equals(BUTTON_TYPE_DEEP_LINK) && btnLeft.getDeepLinkUrl() != null) {

                        mRiaCardDeepLinkListener.onDeepLink(btnLeft.getDeepLinkUrl(), true,
                                new RiaNodeDataModel(rootCard.getId(), btnLeft.getId(),
                                        btnLeft.getButtonText()));
                        mButtonId = btnLeft.getId();
                        mNextNodeId = btnLeft.getNextNodeId();

                    } else if (btnLeft.getButtonType().equals(BUTTON_TYPE_NEXT_NODE) && btnLeft.getNextNodeId() != null) {
                        listener.onResponse(btnLeft.getId(), btnLeft.getNextNodeId());
                    } else if (btnLeft.getButtonType().equals(BUTTON_TYPE_EXIT)) {
                        cvRiaCard.setVisibility(View.GONE);
                        bus.post(new ArrayList<RiaCardModel>());
                    } else if (btnLeft.getButtonType().equals(BUTTON_TYPE_OPEN_URL)) {
                        Intent intent = new Intent(getActivity(), RiaWebViewActivity.class);
                        intent.putExtra(RiaWebViewActivity.RIA_WEB_CONTENT_URL, btnLeft.getUrl());
                        intent.putExtra(RiaWebViewActivity.RIA_NODE_DATA, new RiaNodeDataModel(rootCard.getId(), btnLeft.getId(),
                                btnLeft.getButtonText()));
                        mButtonId = btnLeft.getId();
                        mNextNodeId = btnLeft.getNextNodeId();
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                    RiaEventLogger.getInstance().logClickEvent(session.getFpTag(), rootCard.getId(),
                            btnLeft.getId(), btnLeft.getButtonText());
                }
            });
            btnRiaCrdRight.setText(btnRight.getButtonText());
            btnRiaCrdRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (btnRight.getButtonType().equals(BUTTON_TYPE_DEEP_LINK) && btnRight.getDeepLinkUrl() != null) {

                        mRiaCardDeepLinkListener.onDeepLink(btnRight.getDeepLinkUrl(), true,
                                new RiaNodeDataModel(rootCard.getId(), btnRight.getId(),
                                        btnRight.getButtonText()));
                        mButtonId = btnLeft.getId();
                        mNextNodeId = btnLeft.getNextNodeId();

                    } else if (btnRight.getButtonType().equals(BUTTON_TYPE_NEXT_NODE) && btnRight.getNextNodeId() != null) {
                        listener.onResponse(btnRight.getId(), btnRight.getNextNodeId());
                    } else if (btnRight.getButtonType().equals(BUTTON_TYPE_EXIT)) {
                        RiaEventLogger.lastEventStatus = true;
                        cvRiaCard.setVisibility(View.GONE);
                        bus.post(new ArrayList<RiaCardModel>());
                    } else if (btnRight.getButtonType().equals(BUTTON_TYPE_OPEN_URL)) {
                        Intent intent = new Intent(getActivity(), RiaWebViewActivity.class);
                        intent.putExtra(RiaWebViewActivity.RIA_WEB_CONTENT_URL, btnRight.getUrl());
                        intent.putExtra(RiaWebViewActivity.RIA_NODE_DATA, new RiaNodeDataModel(rootCard.getId(), btnRight.getId(),
                                btnRight.getButtonText()));
                        mButtonId = btnRight.getId();
                        mNextNodeId = btnRight.getNextNodeId();
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                    RiaEventLogger.getInstance().logClickEvent(session.getFpTag(), rootCard.getId(),
                            btnRight.getId(), btnRight.getButtonText());
                }
            });
        }

        drawSectionsForCard(rootCard.getSections(), llRiaCardSections);
        RiaEventLogger.getInstance().logViewEvent(session.getFpTag(), rootCard.getId());
        cvRiaCard.setAnimation(inFromRightAnimation());
        cvRiaCard.animate();
    }

    private void drawSectionsForCard(List<Section> sections, LinearLayout llRiaCardSections) {
        if (llRiaCardSections == null) {
            return;
        }
        llRiaCardSections.removeAllViews();
        for (int i = 0; i < sections.size(); i++) {
            Section widget = sections.get(i);
            switch (SectionType.valueOf(widget.getSectionType())) {
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
        if (getActivity() == null) return;
        TextView tv = new TextView(getActivity());
        tv.setText(Methods.fromHtml(widget.getText()));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 0);
        tv.setLayoutParams(lp);
        llRiaCardSections.addView(tv);
    }

    private void attachGraph(Section widget, LinearLayout llRiaCardSections) {
        if (getActivity() == null) return;
        LinearLayout graph = new LinearLayout(getActivity());
        graph.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        graph.setGravity(Gravity.CENTER_VERTICAL);
        VerticalTextView yAxisName = new VerticalTextView(getActivity(), null);
        yAxisName.setText(widget.getY().getLabel());
        yAxisName.setTextSize(dpToPx(4));
        yAxisName.setRotation(180);
        graph.addView(yAxisName);
        if (!(widget.getY().getAxisType().equals("Integer") || widget.getY().getAxisType().equals("Double"))) {
            cvRiaCard.setVisibility(View.GONE);
            bus.post(new ArrayList<RiaCardModel>());
            return;
        }
        if (widget.getGraphType().equals(BAR)) {
            BarChart barChart = new BarChart(getActivity());
            barChart.getAxisLeft().setDrawGridLines(false);
            barChart.getAxisRight().setDrawGridLines(false);
            barChart.getXAxis().setDrawGridLines(false);
            barChart.setDescription(null);
            barChart.getLegend().setEnabled(false);
            barChart.setPadding(dpToPx(-5), dpToPx(-5), dpToPx(-5), dpToPx(-5));
            barChart.getAxisLeft().setAxisMinValue(0);
            barChart.getAxisLeft().setSpaceBottom(0);
//            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            final ArrayList<String> xVals = new ArrayList<>();
            List<BarEntry> dataEntry = new ArrayList<>();
            for (CoordinatesSet coordinateSet : widget.getCoordinatesSet()) {
                int i = 0;
                for (CoordinateList coordinate : coordinateSet.getCoordinateList()) {
                    dataEntry.add(new BarEntry(i, Float.parseFloat(coordinate.getY()), ""));
                    xVals.add(coordinate.getX());
                    i++;
                }
                BarDataSet dataSet = new BarDataSet(dataEntry, null);
                //dataSet.setDrawFilled(true);
                dataSet.setColor(ContextCompat.getColor(getActivity(), R.color.primary));
                dataSet.setValueTextSize(10);
                dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
//                dataSets.add(dataSet);
            }
            if (widget.getCoordinatesSet().size() <= 1) {
                barChart.getAxisRight().setEnabled(false);
            }
            barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            BarDataSet set = new BarDataSet(dataEntry, "BarDataSet");
            BarData barDataMain = new BarData(set);

            barDataMain.setValueFormatter(new GraphValueFormatter());
            barChart.setData(barDataMain);

            XAxis xAxis = barChart.getXAxis();
            xAxis.setDrawGridLines(false);
            xAxis.setGranularity(1f); // only intervals of 1 day
            xAxis.setLabelCount(7);
            xAxis.setGranularityEnabled(true);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

            xAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return xVals.get((int) value);
                }
            });

            barChart.invalidate();
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(200));
            graph.addView(barChart, lp);
            llRiaCardSections.addView(graph);
            TextView xAxisNames = new TextView(getActivity());
            xAxisNames.setText(widget.getX().getLabel());
            xAxisNames.setTextSize(dpToPx(4));
            xAxisNames.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            xAxisNames.setGravity(Gravity.CENTER_HORIZONTAL);
            llRiaCardSections.addView(xAxisNames);

        } else if (widget.getGraphType().equals(LINE)) {
            LineChart lineChart = new LineChart(getActivity());
            lineChart.getAxisLeft().setDrawGridLines(false);
            lineChart.getAxisRight().setDrawGridLines(false);
            lineChart.getXAxis().setDrawGridLines(false);
            lineChart.setPadding(dpToPx(-5), dpToPx(-5), dpToPx(-5), dpToPx(-5));
            lineChart.setDescription(null);
            lineChart.getLegend().setEnabled(false);
            lineChart.getAxisLeft().setAxisMinValue(0);
            lineChart.getAxisLeft().setSpaceBottom(0);
//            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            final ArrayList<String> xVals = new ArrayList<>();
            List<Entry> dataEntry = new ArrayList<>();
            for (CoordinatesSet coordinateSet : widget.getCoordinatesSet()) {
                int i = 0;
                for (CoordinateList coordinate : coordinateSet.getCoordinateList()) {
                    dataEntry.add(new Entry(Float.parseFloat(coordinate.getY()), i));
                    xVals.add(coordinate.getX());
                    i++;
                }
                LineDataSet dataSet = new LineDataSet(dataEntry, null);
                //dataSet.setFillColor(Color.parseColor("#33ffb900"));
                dataSet.setValueTextSize(10);
                dataSet.setDrawFilled(true);
                dataSet.setColor(ContextCompat.getColor(getActivity(), R.color.primaryColor));
                dataSet.setCircleColor(ContextCompat.getColor(getActivity(), R.color.primaryColor));
                dataSet.setCircleColorHole(ContextCompat.getColor(getActivity(), R.color.primaryColor));
                dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
//                dataSets.add(dataSet);
            }
            if (widget.getCoordinatesSet().size() <= 1) {
                lineChart.getAxisRight().setEnabled(false);
            }
            lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            LineDataSet set = new LineDataSet(dataEntry, "");
            LineData lineDataMain = new LineData(set);


            lineDataMain.setValueFormatter(new GraphValueFormatter());
            lineChart.setData(lineDataMain);

            XAxis xAxis = lineChart.getXAxis();
            xAxis.setValueFormatter(new IAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    return xVals.get((int) value);
                }
            });

            lineChart.invalidate();
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(200));
            graph.addView(lineChart, lp);
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
        inFromRight.setDuration(100);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }

    private Animation outToLeftAnimation() {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(250);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }

    private void attachImage(Section widget, LinearLayout llRiaCardSections) {
        if (getActivity() == null) return;
        ImageView iv = new ImageView(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(150));
        lp.setMargins(0, 0, 0, dpToPx(15));
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        iv.setLayoutParams(lp);
        Glide.with(getActivity())
                .load(widget.getUrl())
                .apply(new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.default_product_image))
                .into(iv);
        //Glide.with(getActivity()).load(widget.getUrl()).placeholder(R.drawable.image_placeholder).into(iv);
        llRiaCardSections.addView(iv);
    }

    private void setVmnTotalCallCount() {
        vmnProgressBar.setVisibility(View.VISIBLE);
        CallTrackerApis trackerApis = Constants.restAdapter.create(CallTrackerApis.class);
        String type = session.getISEnterprise().equals("true") ? "MULTI" : "SINGLE";

        trackerApis.getVmnSummary(Constants.clientId, session.getFPID(), type, new Callback<JsonObject>() {
            @Override
            public void success(JsonObject jsonObject, Response response) {
                vmnProgressBar.setVisibility(View.GONE);
                vmnTotalCallCount.setVisibility(View.VISIBLE);
                if (jsonObject == null || response.getStatus() != 200) {
                    return;
                }
                if (jsonObject.has("TotalCalls")) {
                    vmnTotalCalls = jsonObject.get("TotalCalls").getAsString();
                    vmnTotalCallCount.setText(getNumberFormat(vmnTotalCalls));
                    session.setVmnCallsCount(vmnTotalCalls);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                vmnProgressBar.setVisibility(View.GONE);
                vmnTotalCallCount.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setVmnTotalCustomerCount() {
        vmnCustomerProgressBar.setVisibility(View.VISIBLE);

        WebActionCallInterface callInterface = Constants.apAdapter.create(WebActionCallInterface.class);
        callInterface.getRevenueSummary(session.getFpTag(), new retrofit.Callback<SellerSummary>() {

            @Override
            public void success(SellerSummary sellerSummary, retrofit.client.Response response) {
                vmnCustomerProgressBar.setVisibility(View.GONE);
                vmnTotalCustomerCount.setVisibility(View.VISIBLE);
                if (sellerSummary == null || response.getStatus() != 200) {
                    return;
                }
                vmnTotalCustomerCount.setText(getNumberFormat(sellerSummary.getData().getTotalOrders().toString()));
                if (sellerSummary.getData().getTotalNetAmount() > 0) {
                    pbOrders.setVisibility(View.GONE);
                    tvOrdersCount.setVisibility(View.VISIBLE);
                    rupeeSymbol.setVisibility(View.VISIBLE);
                    tvOrdersCount.setText(getNumberFormat(sellerSummary.getData().getTotalNetAmount().toString()));
                } else {
//                  pbOrders.setVisibility(View.VISIBLE);
                    tvOrdersCount.setVisibility(View.GONE);
                    rupeeSymbol.setVisibility(View.GONE);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                vmnCustomerProgressBar.setVisibility(View.GONE);
                vmnTotalCustomerCount.setVisibility(View.VISIBLE);
            }
        });
    }

    private void openSubscriberActivity() {
        Intent i = new Intent(getActivity(), SubscribersActivity.class);
        startActivity(i);
        getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void addSubscription() {
        /**
         * If not new pricing plan
         */
        if (!WidgetKey.isNewPricingPlan) {
            WebEngageController.trackEvent(SUBSCRIBERS, EVENT_LABEL_NULL, session.getFpTag());
            openSubscriberActivity();
        } else {
            String value = WidgetKey.getPropertyValue(WidgetKey.WIDGET_SUBSCRIPTION, WidgetKey.WIDGET_PROPERTY_SUBSCRIPTION);

            if (value.equals(WidgetKey.WidgetValue.FEATURE_NOT_AVAILABLE.getValue())) {
                Toast.makeText(getContext(), getString(R.string.message_feature_not_available), Toast.LENGTH_LONG).show();
            } else {
                WebEngageController.trackEvent(SUBSCRIBERS, EVENT_LABEL_NULL, session.getFpTag());
                openSubscriberActivity();
            }
        }
    }

    private enum SectionType {
        Text, Graph, Image
    }


    public interface RiaCardResponseListener {
        void onResponse(String buttonId, String NextNodeId);
    }

    public interface RiaCardDeepLinkListener {
        void onDeepLink(String deepLinkUrl, boolean isFromRia, RiaNodeDataModel riaNodeData);
    }

    public class GraphValueFormatter implements IValueFormatter {
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return Math.round(value) + "";
        }

    }
}