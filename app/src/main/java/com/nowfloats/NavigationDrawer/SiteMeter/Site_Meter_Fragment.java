package com.nowfloats.NavigationDrawer.SiteMeter;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.BusinessProfile.UI.UI.Business_Address_Activity;
import com.nowfloats.BusinessProfile.UI.UI.Business_Hours_Activity;
import com.nowfloats.BusinessProfile.UI.UI.Business_Logo_Activity;
import com.nowfloats.BusinessProfile.UI.UI.Contact_Info_Activity;
import com.nowfloats.BusinessProfile.UI.UI.Edit_Profile_Activity;
import com.nowfloats.BusinessProfile.UI.UI.Social_Sharing_Activity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.DomainApiService;
import com.nowfloats.NavigationDrawer.Create_Message_Activity;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.NavigationDrawer.Home_Fragment_Tab;
import com.nowfloats.Twitter.TwitterConstants;
import com.nowfloats.NavigationDrawer.SidePanelFragment;
import com.nowfloats.NavigationDrawer.model.DomainDetails;
import com.nowfloats.signup.UI.Model.Get_FP_Details_Model;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.ProgressBarAnimation;
import com.nowfloats.util.Utils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import jp.wasabeef.recyclerview.animators.FadeInUpAnimator;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 */
public class Site_Meter_Fragment extends Fragment {
    private static int businessNameWeight = 5;
    private static int businessDescriptionWeight = 10;
    private static int businessCategoryWeight = 5;
    private static int featuredImageWeight = 10;
    private static int phoneWeight = 5;
    private static int emailWeight = 5;
    private static int businessAddressWeight = 10;
    private static int businessHoursWeight = 5;
    private static int businessTimingWeight = 5;
    private static int facebookWeight = 10;
    private static int twitterWeight = 10;
    private static int logoWeight = 5;
    private static int firstUpdatesWeight = 5;
    private static int siteMeterTotalWeight = 0;
    private static boolean fiveUpdatesDone = false;
    private static int onUpdate = 4;
    UserSessionManager session;
    private RecyclerView recyclerView;
    private SiteMeterAdapter adapter;

    private static ProgressBar progressBar;
    private TextView meterReading;
    private ArrayList<SiteMeterModel> siteData = new ArrayList<>();
    public final int domain = 11, phone = 4, category = 2,image =7, businessName=0, description=1,
            social=10, address=5, email=3, post=9, logo=8,businessHours=6;
    private Activity activity;
    private SharedPreferences mSharedPreferences, pref;
    private DomainApiService domainApiService;
    private Bus mBus;
    private ProgressDialog progressDialog;
//    private ScaleInAnimationAdapter scaleAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBus = BusProvider.getInstance().getBus();
        activity = getActivity();
        Methods.isOnline(activity);
        session = new UserSessionManager(activity.getApplicationContext(),activity);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //siteData = loadData();
                //adapter = new SiteMeterAdapter(activity,Site_Meter_Fragment.this,siteData);
            }
        }).start();
        initializePrices();
    }

    HashMap<String, String> hmPrices = new HashMap<String, String>();

    private void showLoader(final String message) {


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(getActivity());
                }
                progressDialog.setMessage(message);
                progressDialog.show();
            }
        });
    }

    private void hideLoader() {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void initializePrices() {
        hmPrices.put(".COM", "680");
        hmPrices.put(".NET", "865");
        hmPrices.put(".CO.IN", "375");
        hmPrices.put(".IN", "490");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        domainApiService = new DomainApiService(mBus);
        return inflater.inflate(R.layout.fragment_site__meter, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = (ProgressBar) view.findViewById(R.id.ProgressBar);
        meterReading = (TextView) view.findViewById(R.id.site_meter_reading);
        recyclerView = (RecyclerView) view.findViewById(R.id.sitemeter_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setItemAnimator(new FadeInUpAnimator());
        pref = getActivity().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        Constants.fbShareEnabled = pref.getBoolean("fbShareEnabled", false);
        Constants.fbPageShareEnabled = pref.getBoolean("fbPageShareEnabled", false);

        mSharedPreferences = getActivity().getSharedPreferences(TwitterConstants.PREF_NAME,Context.MODE_PRIVATE);
        Constants.twitterShareEnabled = mSharedPreferences.getBoolean(TwitterConstants.PREF_KEY_TWITTER_LOGIN, false);
        final LinearLayout progressLayout = (LinearLayout) view.findViewById(R.id.progress_layout);
        progressLayout.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

//                        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
//                        scaleAdapter = new ScaleInAnimationAdapter(alphaAdapter);
//                        scaleAdapter.setFirstOnly(false);
//                        scaleAdapter.setInterpolator(new OvershootInterpolator());
                        siteData = loadData();
                        adapter = new SiteMeterAdapter(activity,Site_Meter_Fragment.this,siteData);
                        siteMeterCalculation();
                        progressLayout.setVisibility(View.GONE);
                    }
                });
            }
        }).start();
    }

    public void shareWebsite() {
        String url = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI);
        if (!Util.isNullOrEmpty(url)) {
            String eol = System.getProperty("line.separator");
            url = "Woohoo! We have a new website. Visit it at "
                    + eol + url.toLowerCase();
        }else{
            String eol = System.getProperty("line.separator");
            url = "Woohoo! We have a new website. Visit it at "
                    + eol + session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG).toLowerCase()
                    + activity.getResources().getString(R.string.tag_for_partners);
        }

        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, url);
        startActivity(Intent.createChooser(intent, "Share with:"));
//        Constants.websiteShared = true;
        session.setWebsiteshare(true);
    }

    private ArrayList<SiteMeterModel> loadData() {

        //Set percentage according to the partners
        //0
        siteData.clear();
        if (!(getResources().getString(R.string.buydomain_percentage).equals("0")))
            siteData.add(new SiteMeterModel(domain,"Buy/Link a Domain","Give your business an identity","+10%",false,12));
        //1
        if (!(getResources().getString(R.string.phoneNumber_percentage).equals("0")))
            siteData.add(new SiteMeterModel(phone,"Phone Number","Help customers to reach you instantly","+5%",false,5));
        //2
        if (!(getResources().getString(R.string.businessCategory_percentage).equals("0")))
            siteData.add(new SiteMeterModel(category,"Business Category","Choose a business category","+5%",false,3));
        //3
        if (!(getResources().getString(R.string.featuredImage_percentage).equals("0")))
            siteData.add(new SiteMeterModel(image,"Featured Image","Add a relevant image","+10%",false,8));
        //4
        if (!(getResources().getString(R.string.businessName_percentage).equals("0")))
            siteData.add(new SiteMeterModel(businessName,"Business Name","Add business name","+5%",false,1));
        //5
        if (!(getResources().getString(R.string.businessdescription_percentage).equals("0")))
            siteData.add(new SiteMeterModel(description,"Business Description","Describe your business","+10%",false,2));
        //6
        if (!(getResources().getString(R.string.social_percentage).equals("0")))
            siteData.add(new SiteMeterModel(social,"Social Share","Connect to Facebook and Twitter","+10%",false,11));
        //7
        siteData.add(new SiteMeterModel(address,"Business Address","Help your customers find you","+10%",false,6));
        //8
        if (!(getResources().getString(R.string.email_percentage).equals("0")))
            siteData.add(new SiteMeterModel(email,"Email","Add your email","+5%",false,4));
        //9
        if (!(getResources().getString(R.string.postUpdate_percentage).equals("0"))){
            int val=HomeActivity.StorebizFloats.size()<5?20-HomeActivity.StorebizFloats.size()*onUpdate:20;
            siteData.add(new SiteMeterModel(post,"Post 5 Updates","Message regularly and relevantly","+"+val+"%",false,10));
        }

        //10
        if (!(getResources().getString(R.string.share_percentage).equals("0"))){
            siteData.add(new SiteMeterModel(logo,"Business Logo","Add a business logo","+5%",false,9));
        }
        if (!(getResources().getString(R.string.business_hours).equals("0")))
            siteData.add(new SiteMeterModel(businessHours,"Business Hours","Display business timings","+5%",false,7));

        Collections.sort(siteData, Collections.<SiteMeterModel>reverseOrder());
        //Collections.reverse(siteData);
        return siteData;
    }

    public void siteMeterCalculation(){
        siteMeterTotalWeight = 0;

        for (int i = 0; i < siteData.size(); i++) {
            if(siteData.get(i).position==domain){
                if(!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI))){
                    siteMeterTotalWeight += 10 ;
                    siteData.get(domain).setStatus(true);
                    siteData.get(domain).setSortChar(1);
                }else {
                    siteData.get(domain).setStatus(false);
                    siteData.get(domain).setSortChar(2);
                }
            }else if(siteData.get(i).position==phone){
                if(!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER))  && !getResources().getString(R.string.phoneNumber_percentage).equals("0")){
                    siteMeterTotalWeight+=phoneWeight;
                    siteData.get(phone).setStatus(true);
                    siteData.get(phone).setSortChar(1);
                }else{
                    siteData.get(phone).setStatus(false);
                    siteData.get(phone).setSortChar(2);
                }
            }else if(siteData.get(i).position==category) {
                if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY)) && !getResources().getString(R.string.businessCategory_percentage).equals("0")) {
                    siteMeterTotalWeight += businessCategoryWeight;
                    siteData.get(category).setStatus(true);
                    siteData.get(category).setSortChar(1);
                } else {
                    siteData.get(category).setStatus(false);
                    siteData.get(category).setSortChar(2);
                }
            }else if(siteData.get(i).position==image) {
                if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_IMAGE_URI)) && !getResources().getString(R.string.featuredImage_percentage).equals("0")) {
                    siteMeterTotalWeight += featuredImageWeight;
                    siteData.get(image).setStatus(true);
                    siteData.get(image).setSortChar(1);
                } else {
                    siteData.get(image).setStatus(false);
                    siteData.get(image).setSortChar(2);
                }
            }else if(siteData.get(i).position==businessName) {
                if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME)) && !getResources().getString(R.string.businessName_percentage).equals("0")) {
                    siteMeterTotalWeight += businessNameWeight;
                    siteData.get(businessName).setStatus(true);
                    siteData.get(businessName).setSortChar(1);
                } else {
                    siteData.get(businessName).setStatus(false);
                    siteData.get(businessName).setSortChar(2);
                }
            }else if(siteData.get(i).position==description) {
                if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_DESCRIPTION)) && !getResources().getString(R.string.businessdescription_percentage).equals("0")) {
                    siteMeterTotalWeight += businessDescriptionWeight;
                    siteData.get(description).setStatus(true);
                    siteData.get(description).setSortChar(1);
                } else {
                    siteData.get(description).setStatus(false);
                    siteData.get(description).setSortChar(2);
                }
            }else if(siteData.get(i).position==social) {
                if (Constants.twitterShareEnabled && pref.getBoolean("fbShareEnabled", false) && pref.getBoolean("fbPageShareEnabled", false)) {
                    siteMeterTotalWeight += twitterWeight;
                    siteData.get(social).setStatus(true);
                    siteData.get(social).setSortChar(1);
                } else {
                    siteData.get(social).setStatus(false);
                    siteData.get(social).setSortChar(2);
                }
            }else if(siteData.get(i).position==address) {
                if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS)) &&
                        !Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.LATITUDE))&&
                        !Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.LONGITUDE))&&
                        !getResources().getString(R.string.address_percentage).equals("0")) {
                    siteMeterTotalWeight += businessAddressWeight;
                    siteData.get(address).setStatus(true);
                    siteData.get(address).setSortChar(1);
                } else {
                    siteData.get(address).setStatus(false);
                    siteData.get(address).setSortChar(2);
                }
            }else if(siteData.get(i).position==email) {
                if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL)) && !getResources().getString(R.string.email_percentage).equals("0")) {
                    siteMeterTotalWeight += emailWeight;
                    siteData.get(email).setStatus(true);
                    siteData.get(email).setSortChar(1);
                } else {
                    siteData.get(email).setStatus(false);
                    siteData.get(email).setSortChar(2);
                }
            }else if (siteData.get(i).position==post) {
                if (HomeActivity.StorebizFloats.size() < 5 && fiveUpdatesDone == false) {
                    siteMeterTotalWeight += (HomeActivity.StorebizFloats.size()*onUpdate);
                    siteData.get(post).setStatus(false);
                    siteData.get(post).setSortChar(2);
                } else {
                    fiveUpdatesDone = true;
                    siteMeterTotalWeight += 20;
                    siteData.get(post).setStatus(true);
                    siteData.get(post).setSortChar(1);
                }
            }else if(siteData.get(i).position==logo) {
                if (!(Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_LogoUrl))) && !getResources().getString(R.string.Logo_percentage).equals("0")) {
                    siteMeterTotalWeight += logoWeight;
                    siteData.get(logo).setStatus(true);
                    siteData.get(logo).setSortChar(1);
                } else {
                    siteData.get(logo).setStatus(false);
                    siteData.get(logo).setSortChar(2);
                }
            }else if(siteData.get(i).position==businessHours) {
                if (session.getBusinessHours()) {
                    siteMeterTotalWeight += businessHoursWeight;
                    siteData.get(businessHours).setStatus(true);
                    siteData.get(businessHours).setSortChar(1);
                } else {
                    siteData.get(businessHours).setStatus(false);
                    siteData.get(businessHours).setSortChar(2);
                }
            }
        }


        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressBar!=null){
//                    progressBar.setProgress(siteMeterTotalWeight);
                    progressBar.setProgress(0);
                    ProgressBarAnimation mProgressAnimation = new ProgressBarAnimation(progressBar, 1000);
                    mProgressAnimation.setProgress(siteMeterTotalWeight);
                }
                if(meterReading!=null)
                    meterReading.setText(siteMeterTotalWeight+"% "+getString(R.string.percent_site_completed));
            }
        });
//        if (scaleAdapter!=null)
//            scaleAdapter.notifyDataSetChanged();
        Collections.sort(siteData);
        recyclerView.setAdapter(adapter);
        if (adapter!=null)
            adapter.notifyDataSetChanged();
        if(recyclerView!=null)
            recyclerView.invalidate();
    }

    @Override
    public void onResume() {
        super.onResume();
        HomeActivity.headerText.setText("Site Health");
        try{
            siteData = loadData();
            adapter = new SiteMeterAdapter(activity,Site_Meter_Fragment.this,siteData);
            siteMeterCalculation();
        }catch(Exception e){e.printStackTrace();}
        MixPanelController.setProperties("SiteHealth", ""+siteMeterTotalWeight);
    }

    public void SiteMeterOnClick(int value) {
        switch (value){
            case logo:
                MixPanelController.track(EventKeysWL.LOGO, null);
                if(Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_LogoUrl))){
                    Intent in = new Intent(activity, Business_Logo_Activity.class);
                    startActivity(in);
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;
            case address:
                MixPanelController.track(EventKeysWL.SITE_SCORE_HELP_YOUR_CUSTOMERS,null);
                if(Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS))||
                        Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.LATITUDE))||
                        Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.LONGITUDE))){
                    Intent in = new Intent(activity, Business_Address_Activity.class);
                    startActivity(in);
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;
            case post:
                MixPanelController.track(EventKeysWL.SITE_SCORE_POST_5_UPDATES,null);
                if(HomeActivity.StorebizFloats.size()<5  && !fiveUpdatesDone){
                    Intent in = new Intent(activity,Create_Message_Activity.class);
                    startActivity(in);
                }
                break;
            case social:
                MixPanelController.track(EventKeysWL.SITE_SCORE_GET_SOCIAL,null);
                if(!(Constants.twitterShareEnabled && pref.getBoolean("fbShareEnabled", false) && pref.getBoolean("fbPageShareEnabled", false))){
                    Intent in = new Intent(activity, Social_Sharing_Activity.class);
                    startActivity(in);
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;
            case email:
                MixPanelController.track(EventKeysWL.SITE_SCORE_ADD_EMAIL,null);
                if(Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL))) {
                    Intent in = new Intent(activity, Contact_Info_Activity.class);
                    startActivity(in);
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;
            case description:
                MixPanelController.track(EventKeysWL.SITE_SCORE_BUSINESS_DESC,null);
                if(Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_DESCRIPTION)))
                {
                    Intent in = new Intent(activity, Edit_Profile_Activity.class);
                    startActivity(in);
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;
            case domain:
                MixPanelController.track(EventKeysWL.SITE_SCORE_GET_YOUR_OWN_IDENTITY, null);
                if (Utils.isNetworkConnected(getActivity())) {
                    showLoader(getString(R.string.please_wait));
                    domainApiService.getDomainDetails(session.getFpTag(), getDomainDetailsParam());
                } else {
                    Methods.showSnackBarNegative(getActivity(), getString(R.string.noInternet));
                }
                break;
            case phone:
                MixPanelController.track(EventKeysWL.SITE_SCORE_PHONE_NUMBER,null);
                if(Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER))){
                    Intent act = new Intent(activity, Contact_Info_Activity.class);
                    startActivity(act);
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;
            case category:
                MixPanelController.track(EventKeysWL.SITE_SCORE_Businesss_CATEGORY,null);
                if(Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY))){
                    Intent in = new Intent(activity, Edit_Profile_Activity.class);
                    startActivity(in);
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;
            case image:
                MixPanelController.track(EventKeysWL.SITE_SCORE_FEATURED_IMAGE,null);
                if(Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_IMAGE_URI))){

                    Intent in = new Intent(activity, Edit_Profile_Activity.class);
                    startActivity(in);
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;
            case businessName:
                MixPanelController.track(EventKeysWL.SITE_SCORE_BUSINESS_NAME,null);
                if(Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME)))
                {
                    Intent in = new Intent(activity, Edit_Profile_Activity.class);
                    startActivity(in);
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;
            case businessHours:
                if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_IMAGE_TIMINGS).equals("TIMINGS")) {
                    Intent businessHoursIntent = new Intent(activity, Business_Hours_Activity.class);
                    startActivity(businessHoursIntent);
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else {
                    new MaterialDialog.Builder(activity)
                            .title(getResources().getString(R.string.features_not_available))
                            .content(getResources().getString(R.string.check_store_for_upgrade_info))
                            .positiveText(getResources().getString(R.string.goto_store))
                            .negativeText(getResources().getString(R.string.cancel))
                            .positiveColorRes(R.color.primaryColor)
                            .negativeColorRes(R.color.light_gray)
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onNegative(MaterialDialog dialog) {
                                    super.onNegative(dialog);
                                    dialog.dismiss();
                                }

                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    super.onPositive(dialog);
//                                                    Constants.showStoreScreen = true ;
                                    Home_Fragment_Tab.viewPager = null;
                                    dialog.dismiss();
//                                                    activity.getSupportFragmentManager().popBackStack();
                                    ((HomeActivity)activity).onClick("Store");
                                }
                            }).show();
                }
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mBus.register(this);
    }

    @Override
    public void onStop() {
        mBus.unregister(this);
        super.onStop();
    }

    /*
     * Domain Details Param
     */
    private HashMap<String, String> getDomainDetailsParam() {
        HashMap<String, String> offersParam = new HashMap<>();
        offersParam.put("clientId", Constants.clientId);
        return offersParam;
    }

    private HashMap<String, String> getDomainAvailabilityParam(String domainType) {
        HashMap<String, String> offersParam = new HashMap<>();
        offersParam.put("clientId", Constants.clientId);
        offersParam.put("domainType", domainType);
        return offersParam;
    }

    private HashMap<String, String> getLinkDomainParam() {
        HashMap<String, String> offersParam = new HashMap<>();
        offersParam.put("authClientId", Constants.clientId);
        offersParam.put("fpTag", session.getFpTag());
        return offersParam;
    }


    /*
    ************************************* DOMAIN BOOKING PROCESS ***********************************
    *
    *
    */

    private static final String DOMAIN_SUCCESS_STATUS = "17";

    /*
     * After getting domain details follow below steps
     *
     * if domain status is not null
     * 1.If processing status is DOMAIN_SUCCESS_STATUS && IsProcessingFailed == false
     *   ---> domain booked.
     *
     * 2.If processing status is not DOMAIN_SUCCESS_STATUS && IsProcessingFailed == false
     *   ---> domain will be activated in 24 hours.
     *
     * 3.If it fails and IsProcessingFailed == true
     *  ---> contact customer care
     *
     */

    @Subscribe
    public void getDomainDetails(DomainDetails domainDetails) {
//        domainDetails = null;
        hideLoader();
        if (domainDetails != null && domainDetails.response) {

            if (domainDetails.getProcessingStatus().equalsIgnoreCase(DOMAIN_SUCCESS_STATUS) &&
                    domainDetails.getIsProcessingFailed().equalsIgnoreCase("true")) {
                showCustomDomain(getString(R.string.domain_booking_successful),
                        String.format(getString(R.string.domain_booking_successful_message), domainDetails.getDomainName()),
                        getString(R.string.ok), null, DialogFrom.DEFAULT);

            } else if (!domainDetails.getProcessingStatus().equalsIgnoreCase(DOMAIN_SUCCESS_STATUS) &&
                    domainDetails.getIsProcessingFailed().equalsIgnoreCase("true")) {

                showCustomDomain(getString(R.string.domain_booking_process),
                        getString(R.string.domain_booking_process_message),
                        getString(R.string.ok), null, DialogFrom.DEFAULT);
            } else {
                showCustomDomain(getString(R.string.buy_a_domain),
                        Html.fromHtml(getString(R.string.drop_us_contact)).toString(),
                        getString(R.string.ok), null, DialogFrom.DEFAULT);
            }
        } else {
            showLoader(getString(R.string.please_wait));
            domainApiService.getDomainFPDetails(session.getFPID(),
                    getDomainDetailsParam());
        }

    }


    private static final String PAYMENT_STATE_SUCCESS = "1";
    private static final String ROOT_ALIAS_URI = "nowfloats";
    private static final String FP_WEB_WIDGET_DOMAIN = "DOMAINPURCHASE";
    private Get_FP_Details_Model get_fp_details_model;

    @Subscribe
    public void setFpDetails(Get_FP_Details_Model get_fp_details_model) {
        hideLoader();
        this.get_fp_details_model = get_fp_details_model;
        if (TextUtils.isEmpty(get_fp_details_model.response)) {

            if (get_fp_details_model.getPaymentState().equalsIgnoreCase(PAYMENT_STATE_SUCCESS)
                    && (TextUtils.isEmpty(get_fp_details_model.getRootAliasUri())
                    || get_fp_details_model.getRootAliasUri().equalsIgnoreCase("null")
                    || get_fp_details_model.getRootAliasUri().contains(ROOT_ALIAS_URI))
                    && get_fp_details_model.getFPWebWidgets() != null
                    && get_fp_details_model.getFPWebWidgets().contains(FP_WEB_WIDGET_DOMAIN)) {

                if(TextUtils.isEmpty(get_fp_details_model.getEmail())
                        || get_fp_details_model.getContacts() == null){
                    showCustomDomain(getString(R.string.domain_detail_required),
                            Html.fromHtml(getString(R.string.please_fill_details_to_proceed)).toString(),
                            getString(R.string.ok), null, DialogFrom.CONTACTS_AND_EMAIL_REQUIRED);

                }else if(get_fp_details_model.getCategory() == null){
                    showCustomDomain(getString(R.string.domain_detail_required),
                            Html.fromHtml(getString(R.string.please_fill_details_to_proceed)).toString(),
                            getString(R.string.ok), null, DialogFrom.CATEGORY_REQUIRED);
                }else if(TextUtils.isEmpty(get_fp_details_model.getAddress())
                        || TextUtils.isEmpty(get_fp_details_model.getLat())
                        || TextUtils.isEmpty(get_fp_details_model.getLng())
                        || get_fp_details_model.getLat().equalsIgnoreCase("0")
                        || get_fp_details_model.getLng().equalsIgnoreCase("0")){
                    showCustomDomain(getString(R.string.domain_detail_required),
                            Html.fromHtml(getString(R.string.please_fill_details_to_proceed)).toString(),
                            getString(R.string.ok), null, DialogFrom.ADDRESS_REQUIRED);
                }
                else{
                    chooseDomain();
                }
            } else {
                showCustomDomain(getString(R.string.buy_a_domain),
                        Html.fromHtml(getString(R.string.drop_us_contact)).toString(),
                        getString(R.string.ok), null, DialogFrom.DEFAULT);
            }

        } else {
            Methods.showSnackBarNegative(getActivity(), get_fp_details_model.response);
        }
    }

    private void chooseDomain() {

        MaterialDialog.Builder builder = new MaterialDialog.Builder(activity)
                .title(getString(R.string.link_a_domain_nf))
                .customView(R.layout.dialog_choose_domain, false)
                .positiveColorRes(R.color.primaryColor);

        if (!activity.isFinishing()) {
            final MaterialDialog materialDialog = builder.show();

            materialDialog.getCustomView().findViewById(R.id.btnBookDomain)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            materialDialog.dismiss();
                            showLoader(getString(R.string.please_wait));
                            domainApiService.getDomainSupportedTypes(getDomainDetailsParam());
                        }
                    });
            materialDialog.getCustomView().findViewById(R.id.btnLinkDomain)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            materialDialog.dismiss();
                            linkDomain();
                        }
                    });
        }
    }

    private ArrayList<String> arrDomainExtensions;

    @Subscribe
    public void getDomainSupportedTypes(ArrayList<String> arrExtensions) {
        hideLoader();
        if (arrExtensions != null && arrExtensions.size() > 0) {
//            arrDomainExtensions = new ArrayList<String>(Arrays.asList(domainSupportedTypes.domainExtension.split(",")));
           /*
               remove below domains as per Rachit
            */
            this.arrDomainExtensions = arrExtensions;
            arrDomainExtensions.remove(".CA");
            arrDomainExtensions.remove(".CO.ZA");
            bookDomain();
        } else {
            Methods.showSnackBarNegative(getActivity(), getString(R.string.domain_details_getting_error));
        }

    }

    private MaterialDialog domainBookDialog = null;

    private void bookDomain() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(activity)
                .title(getString(R.string.book_a_new_domain))
                .customView(R.layout.dialog_book_a_domain, false)
                .positiveColorRes(R.color.primaryColor);


        if (!activity.isFinishing()) {
            domainBookDialog = builder.show();
            View maView = domainBookDialog.getCustomView();
            final EditText edtDomainName = (EditText) maView.findViewById(R.id.edtDomainName);
            final Spinner spDomainTypes = (Spinner) maView.findViewById(R.id.spDomainTypes);
            TextView tvCompanyName = (TextView) maView.findViewById(R.id.tvCompanyName);
            TextView tvTag = (TextView) maView.findViewById(R.id.tvTag);
            TextView tvAddress = (TextView) maView.findViewById(R.id.tvAddress);
            TextView tvCity = (TextView) maView.findViewById(R.id.tvCity);
            final EditText edtZip = (EditText) maView.findViewById(R.id.edtZip);
            TextView tvCountryCode = (TextView) maView.findViewById(R.id.tvCountryCode);
            TextView tvISDCode = (TextView) maView.findViewById(R.id.tvISDCode);
            TextView tvCountry = (TextView) maView.findViewById(R.id.tvCountry);
            TextView tvEmail = (TextView) maView.findViewById(R.id.tvEmail);
            TextView tvPrimaryNumber = (TextView) maView.findViewById(R.id.tvPrimaryNumber);
            final TextView tvPrice = (TextView) maView.findViewById(R.id.tvPrice);
            final TextView tvPriceDef = (TextView) maView.findViewById(R.id.tvPriceDef);
            Button btnActivateDomain = (Button) maView.findViewById(R.id.btnActivateDomain);
            Button btnBack = (Button) maView.findViewById(R.id.btnBack);
//            btnActivateDomain.setEnabled(false);
//            btnActivateDomain.setClickable(false);
            tvPriceDef.setText(String.format(getString(R.string.price_of_domain), arrDomainExtensions.get(0)));
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, arrDomainExtensions);
            spDomainTypes.setAdapter(arrayAdapter);
            spDomainTypes.setSelection(0);
            tvCompanyName.setText(get_fp_details_model.getTag());
            tvTag.setText(get_fp_details_model.getAliasTag());
            tvAddress.setText(get_fp_details_model.getAddress());
            tvCity.setText(get_fp_details_model.getCity());
            if (!TextUtils.isEmpty(get_fp_details_model.getPinCode())) {
                edtZip.setText(get_fp_details_model.getPinCode());
                edtZip.setBackgroundDrawable(null);
                edtZip.setClickable(false);
                edtZip.setEnabled(false);
            }
            tvCountryCode.setText(get_fp_details_model.getLanguageCode());
            tvISDCode.setText(get_fp_details_model.getCountryPhoneCode());
            tvCountry.setText(get_fp_details_model.getCountry());
            tvEmail.setText(get_fp_details_model.getEmail());
            tvPrimaryNumber.setText(get_fp_details_model.getPrimaryNumber());

            btnActivateDomain
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String domainName = edtDomainName.getText().toString();
                            if (TextUtils.isEmpty(domainName)) {
                                Methods.showSnackBarNegative(getActivity(),
                                        getString(R.string.enter_domain_name));
                            } else if (TextUtils.isEmpty(edtZip.getText().toString())) {
                                Methods.showSnackBarNegative(getActivity(),
                                        getString(R.string.enter_zip_code));
                            } else {
                                showLoader(getString(R.string.please_wait));
                                get_fp_details_model.setDomainName(domainName);
                                get_fp_details_model.setDomainType(spDomainTypes.getSelectedItem().toString());
                                get_fp_details_model.setPinCode(edtZip.getText().toString());
                                domainApiService.checkDomainAvailability(domainName,
                                        getDomainAvailabilityParam((String) spDomainTypes.getSelectedItem()));
                            }
                        }
                    });

            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    domainBookDialog.dismiss();
                    chooseDomain();
                }
            });

            spDomainTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    tvPriceDef.setText(String.format(getString(R.string.price_of_domain), arrDomainExtensions.get(position)));
                    if (hmPrices.containsKey(arrDomainExtensions.get(position))) {
                        tvPrice.setText(hmPrices.get(arrDomainExtensions.get(position)));
                    } else {
                        tvPrice.setText("");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    @Subscribe
    public void domainAvailabilityStatus(DomainApiService.DomainAPI domainAPI) {
        hideLoader();
        if (domainAPI == DomainApiService.DomainAPI.CHECK_DOMAIN) {
            Methods.showSnackBarPositive(getActivity(), "Domain is available");
            showCustomDomain(getString(R.string.book_a_new_domain),
                    getString(R.string.are_you_sure_do_you_want_to_book_domain),
                    getString(R.string.yes), getString(R.string.no), DialogFrom.DOMAIN_AVAILABLE);
        } else if (domainAPI == DomainApiService.DomainAPI.LINK_DOMAIN) {
            Methods.showSnackBarPositive(getActivity(), "domain link available");
        } else {

            if (domainBookDialog != null)
                domainBookDialog.dismiss();
            Methods.showSnackBarNegative(getActivity(), getString(R.string.domain_not_available));
        }
    }

    private void linkDomain() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(activity)
                .title(getString(R.string.have_an_existing_domain))
                .customView(R.layout.dialog_link_domain, false)
                .positiveColorRes(R.color.primaryColor);
        if (!activity.isFinishing()) {
            final MaterialDialog materialDialog = builder.show();
            View maView = materialDialog.getCustomView();
            final RadioButton rbPointExisting = (RadioButton) maView.findViewById(R.id.rbPointExisting);
            final RadioButton rbPointNFWeb = (RadioButton) maView.findViewById(R.id.rbPointNFWeb);
            final EditText edtComments = (EditText) maView.findViewById(R.id.edtComments);
            Button btnBack = (Button) maView.findViewById(R.id.btnBack);
            Button btnSubmitRequest = (Button) maView.findViewById(R.id.btnSubmitRequest);

            btnSubmitRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String subject = "";
                    if (rbPointNFWeb.isChecked()) {
                        subject = rbPointNFWeb.getText().toString();
                    } else if (rbPointExisting.isChecked()) {
                        subject = rbPointExisting.getText().toString();
                    }

                    if (TextUtils.isEmpty(subject)) {
                        Methods.showSnackBarNegative(getActivity(),
                                getString(R.string.please_select_subject));
                    } else if (TextUtils.isEmpty(edtComments.getText().toString())) {
                        Methods.showSnackBarNegative(getActivity(),
                                getString(R.string.please_enter_message));
                    } else {
                        materialDialog.dismiss();
                        HashMap<String, String> hashMap = new HashMap<String, String>();
                        hashMap.put("Subject", subject);
                        hashMap.put("Mesg", edtComments.getText().toString());
                        domainApiService.linkDomain(hashMap,
                                getLinkDomainParam());
                    }
                }
            });
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    materialDialog.dismiss();
                    chooseDomain();
                }
            });

        }
    }


    private enum DialogFrom {
        DOMAIN_AVAILABLE,
        CONTACTS_AND_EMAIL_REQUIRED,
        CATEGORY_REQUIRED,
        ADDRESS_REQUIRED,
        DEFAULT
    }

    private void showCustomDomain(String title, String message, String postiveBtn, String negativeBtn,
                                  final DialogFrom dialogFrom) {

        MaterialDialog.Builder builder = new MaterialDialog.Builder(activity)
                .title(title)
                .customView(R.layout.dialog_link_layout, false)
                .positiveText(postiveBtn)
                .negativeText(negativeBtn)
                .positiveColorRes(R.color.primaryColor)
                .negativeColorRes(R.color.primaryColor)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        switch (dialogFrom) {

                            case DOMAIN_AVAILABLE:
                                prepareAndPublishDomain();
                                break;
                            case CONTACTS_AND_EMAIL_REQUIRED:
                                ((SidePanelFragment.OnItemClickListener) activity).
                                        onClick(getResources().getString(R.string.contact__info));
                                break;
                            case CATEGORY_REQUIRED:
                                ((SidePanelFragment.OnItemClickListener) activity).
                                        onClick(getResources().getString(R.string.basic_info));
                                break;
                            case ADDRESS_REQUIRED:
                                ((SidePanelFragment.OnItemClickListener) activity).
                                        onClick(getResources().getString(R.string.business__address));
                            case DEFAULT:

                                break;
                        }
                    }
                    /*
                    ((SidePanelFragment.OnItemClickListener) activity).
                onClick(getResources().getString(R.string.business_profile));
                     */

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);

                        switch (dialogFrom) {

                            case DOMAIN_AVAILABLE:

                                break;
                            case DEFAULT:

                                break;
                        }
                    }
                });

        final MaterialDialog materialDialog = builder.show();
        View maView = materialDialog.getCustomView();

        TextView tvMessage = (TextView) maView.findViewById(R.id.toast_message_to_contact);
        tvMessage.setText(message);
    }

    private void prepareAndPublishDomain() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("clientId", Constants.clientId);
        hashMap.put("domainName", get_fp_details_model.getDomainName());
        hashMap.put("domainType", get_fp_details_model.getDomainType());
        hashMap.put("existingFPTag", session.getFpTag());
        hashMap.put("addressLine1", get_fp_details_model.getAddress());
        hashMap.put("city", get_fp_details_model.getCity());
        hashMap.put("companyName", get_fp_details_model.getTag());
        hashMap.put("contactName", TextUtils.isEmpty(get_fp_details_model.getContactName())?
                session.getFpTag():get_fp_details_model.getContactName());
        hashMap.put("country", get_fp_details_model.getCountry());
        hashMap.put("countryCode", get_fp_details_model.getLanguageCode());
        hashMap.put("email", get_fp_details_model.getEmail());
        hashMap.put("lat", get_fp_details_model.getLat());
        hashMap.put("lng", get_fp_details_model.getLng());
        hashMap.put("phoneISDCode", get_fp_details_model.getCountryPhoneCode());
        if (get_fp_details_model.getCategory() != null && get_fp_details_model.getCategory().size() > 0)
            hashMap.put("primaryCategory", get_fp_details_model.getCategory().get(0));
        else
            hashMap.put("primaryCategory", "");
        hashMap.put("primaryNumber", get_fp_details_model.getPrimaryNumber());
        hashMap.put("regService", "");
        hashMap.put("state", get_fp_details_model.getPaymentState());
        hashMap.put("zip", get_fp_details_model.getPinCode());
        domainApiService.buyDomain(hashMap);
    }

    @Subscribe
    public void domainBookStatus(String content) {
        showCustomDomain(getString(R.string.book_a_new_domain),
                content,
                getString(R.string.ok), null, DialogFrom.DEFAULT);
    }
}