package com.nowfloats.NavigationDrawer.SiteMeter;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nowfloats.BusinessProfile.UI.UI.BusinessHoursActivity;
import com.nowfloats.BusinessProfile.UI.UI.Business_Address_Activity;
import com.nowfloats.BusinessProfile.UI.UI.Business_Logo_Activity;
import com.nowfloats.BusinessProfile.UI.UI.ContactInformationActivity;
import com.nowfloats.BusinessProfile.UI.UI.Edit_Profile_Activity;
import com.nowfloats.BusinessProfile.UI.UI.Social_Sharing_Activity;
import com.nowfloats.CustomWidget.roboto_lt_24_212121;
import com.nowfloats.CustomWidget.roboto_md_60_212121;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.DomainApiService;
import com.nowfloats.NavigationDrawer.Create_Message_Activity;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.NavigationDrawer.Home_Fragment_Tab;
import com.nowfloats.NavigationDrawer.SidePanelFragment;
import com.nowfloats.NavigationDrawer.model.DomainDetails;
import com.nowfloats.NavigationDrawer.model.EmailBookingModel;
import com.nowfloats.Store.NewPricingPlansActivity;
import com.nowfloats.AccrossVerticals.domain.DomainDetailsActivity;
import com.nowfloats.helper.DigitalChannelUtil;
import com.nowfloats.on_boarding.OnBoardingApiCalls;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.twitter.TwitterConnection;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.ProgressBarAnimation;
import com.nowfloats.util.Utils;
import com.nowfloats.util.WebEngageController;
import com.thinksity.BuildConfig;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import jp.wasabeef.recyclerview.animators.FadeInUpAnimator;

import static com.framework.webengageconstant.EventLabelKt.SITE_HEALTH;
import static com.framework.webengageconstant.EventNameKt.CLICKED_ON_SITE_HEALTH;

/**
 * A simple {@link Fragment} subclass.
 */
public class Site_Meter_Fragment extends Fragment implements DomainApiService.DomainCallback {
    public final static int businessNameWeight = 5;
    public final static int businessDescriptionWeight = 10;
    public final static int businessCategoryWeight = 5;
    public final static int featuredImageWeight = 10;
    public final static int phoneWeight = 5;
    public final static int emailWeight = 5;
    public final static int businessAddressWeight = 10;
    public final static int businessHoursWeight = 5;
    public final static int businessTimingWeight = 5;
    public final static int facebookWeight = 10;
    public final static int twitterWeight = 10;
    public final static int logoWeight = 5;
    public final static int firstUpdatesWeight = 5;
    public final static int onUpdate = 4;
    private final static int LIGHT_HOUSE_EXPIRED = -1, DEMO = 0, DEMO_EXPIRED = -2;
    private static final String DOMAIN_SUCCESS_STATUS = "17";
    public final int domain = 11, phone = 4, category = 2, image = 7, businessName = 0, description = 1,
            social = 10, address = 5, email = 3, post = 9, logo = 8, businessHours = 6;
    int siteMeterTotalWeight = 0;
    boolean fiveUpdatesDone = false;
    UserSessionManager session;
    private RecyclerView recyclerView;
    private SiteMeterAdapter adapter;
    private ProgressBar progressBar;
    private TextView meterReading;
    private ArrayList<SiteMeterModel> siteData = new ArrayList<>();
    private Activity activity;
    private SharedPreferences mSharedPreferences, pref;
    private DomainApiService domainApiService;
    private ProgressDialog progressDialog;
    private boolean isAlreadyCalled = false, isDomainDetailsAvali;
    //private ScaleInAnimationAdapter scaleAdapter;
    private Integer storebizFloats;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        Methods.isOnline(activity);
        session = new UserSessionManager(activity.getApplicationContext(), activity);
    }

    private void showLoader(final String message) {

        if (getActivity() == null) return;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog == null) {
                    progressDialog = new ProgressDialog(requireActivity());
                    progressDialog.setCanceledOnTouchOutside(false);
                }
                progressDialog.setMessage(message);
                progressDialog.show();
            }
        });
    }

    private void hideLoader() {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        domainApiService = new DomainApiService(this);
        if (getArguments() != null) {
            storebizFloats = getArguments().getInt("StorebizFloats", 0);
        }

        return inflater.inflate(R.layout.fragment_site__meter, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() == null || !isAdded()) return;
        progressBar = (ProgressBar) view.findViewById(R.id.ProgressBar);
        meterReading = (TextView) view.findViewById(R.id.site_meter_reading);
        recyclerView = (RecyclerView) view.findViewById(R.id.sitemeter_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setItemAnimator(new FadeInUpAnimator());
        pref = getActivity().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        Constants.fbShareEnabled = pref.getBoolean("fbShareEnabled", false);
        Constants.fbPageShareEnabled = pref.getBoolean("fbPageShareEnabled", false);

        mSharedPreferences = getActivity().getSharedPreferences(TwitterConnection.PREF_NAME, Context.MODE_PRIVATE);
        Constants.twitterShareEnabled = mSharedPreferences.getBoolean(TwitterConnection.PREF_KEY_TWITTER_LOGIN, false);
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
                        adapter = new SiteMeterAdapter(activity, Site_Meter_Fragment.this, siteData);
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
            url = getString(R.string.woohoo_we_have_a_new_website_visit_it_at)
                    + eol + url.toLowerCase();
        } else {
            String eol = System.getProperty("line.separator");
            url = getString(R.string.woohoo_we_have_a_new_website_visit_it_at)
                    + eol + session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG).toLowerCase()
                    + activity.getResources().getString(R.string.tag_for_partners);
        }

        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, url);
        startActivity(Intent.createChooser(intent, getString(R.string.share_with)));
//        Constants.websiteShared = true;
        session.setWebsiteshare(true);
    }

    private ArrayList<SiteMeterModel> loadData() {

        //Set percentage according to the partners
        if (!isAdded()) return siteData;
        //0
        siteData.clear();
        if (!(getResources().getString(R.string.buydomain_percentage).equals("0")))
            siteData.add(new SiteMeterModel(domain, getString(R.string.buy_link_a_domain), getString(R.string.give_your_business_an_identity), getString(R.string.plus_ten_percent), false, 12));
        //1
        if (!(getResources().getString(R.string.phoneNumber_percentage).equals("0")))
            siteData.add(new SiteMeterModel(phone, getString(R.string.phone_number_), getString(R.string.help_customers_to_reach_you), getString(R.string.plus_five_percent), false, 5));
        //2
        if (!(getResources().getString(R.string.businessCategory_percentage).equals("0")))
            siteData.add(new SiteMeterModel(category, getString(R.string.business_category_), getString(R.string.choose_a_business_category), getString(R.string.plus_five_percent), false, 3));
        //3
        if (!(getResources().getString(R.string.featuredImage_percentage).equals("0")))
            siteData.add(new SiteMeterModel(image, getString(R.string.featured_image), getString(R.string.add_a_relevent_name), getString(R.string.plus_ten_percent), false, 8));
        //4
        if (!(getResources().getString(R.string.businessName_percentage).equals("0")))
            siteData.add(new SiteMeterModel(businessName, getString(R.string.business_name_), getString(R.string.add_business_name), getString(R.string.plus_five_percent), false, 1));
        //5
        if (!(getResources().getString(R.string.businessdescription_percentage).equals("0")))
            siteData.add(new SiteMeterModel(description, getString(R.string.business_description), getString(R.string.describe_you_business), getString(R.string.plus_ten_percent), false, 2));
        //6
        if (!(getResources().getString(R.string.social_percentage).equals("0")))
            siteData.add(new SiteMeterModel(social, getString(R.string.social_share), getString(R.string.connect_to_fb_twitter), getString(R.string.plus_ten_percent), false, 11));
        //7
        siteData.add(new SiteMeterModel(address, getString(R.string.business_address), getString(R.string.help_customer_to_find_you), getString(R.string.plus_ten_percent), false, 6));
        //8
        if (!(getResources().getString(R.string.email_percentage).equals("0")))
            siteData.add(new SiteMeterModel(email, getString(R.string.email_), getString(R.string.add_your_email), getString(R.string.plus_five_percent), false, 4));
        //9
        if (!(getResources().getString(R.string.postUpdate_percentage).equals("0"))) {
            int val = getStorebizFloats() < 5 ? 20 - getStorebizFloats() * onUpdate : 20;
            siteData.add(new SiteMeterModel(post, getString(R.string.post_five_updates), getString(R.string.message_regularly_and_relatively), "+" + val + "%", false, 10));
        }

        //10
        if (!(getResources().getString(R.string.share_percentage).equals("0"))) {
            siteData.add(new SiteMeterModel(logo, getString(R.string.business_logo), getString(R.string.add_business_logo), getString(R.string.plus_five_percent), false, 9));
        }
        if (!(getResources().getString(R.string.business_hours).equals("0")))
            siteData.add(new SiteMeterModel(businessHours, getString(R.string.business_hours_), getString(R.string.display_business_timings), getString(R.string.plus_five_percent), false, 7));

        Collections.sort(siteData, Collections.<SiteMeterModel>reverseOrder());
        //Collections.reverse(siteData);
        return siteData;
    }

    public void siteMeterCalculation() {
        siteMeterTotalWeight = 0;
        if (siteData == null || !isAdded()) return;
        for (int i = 0; i < siteData.size(); i++) {
            if (siteData.get(i).position == domain) {
                if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI))) {
                    siteMeterTotalWeight += 10;
                    siteData.get(domain).setStatus(true);
                    siteData.get(domain).setSortChar(1);
                } else {
                    siteData.get(domain).setStatus(false);
                    siteData.get(domain).setSortChar(2);
                }
            } else if (siteData.get(i).position == phone) {
                if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER)) && !getResources().getString(R.string.phoneNumber_percentage).equals("0")) {
                    siteMeterTotalWeight += phoneWeight;
                    siteData.get(phone).setStatus(true);
                    siteData.get(phone).setSortChar(1);
                } else {
                    siteData.get(phone).setStatus(false);
                    siteData.get(phone).setSortChar(2);
                }
            } else if (siteData.get(i).position == category) {
                if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY)) && !getResources().getString(R.string.businessCategory_percentage).equals("0")) {
                    siteMeterTotalWeight += businessCategoryWeight;
                    siteData.get(category).setStatus(true);
                    siteData.get(category).setSortChar(1);
                } else {
                    siteData.get(category).setStatus(false);
                    siteData.get(category).setSortChar(2);
                }
            } else if (siteData.get(i).position == image) {
                if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_IMAGE_URI)) && !getResources().getString(R.string.featuredImage_percentage).equals("0")) {
                    siteMeterTotalWeight += featuredImageWeight;
                    siteData.get(image).setStatus(true);
                    siteData.get(image).setSortChar(1);
                } else {
                    siteData.get(image).setStatus(false);
                    siteData.get(image).setSortChar(2);
                }
            } else if (siteData.get(i).position == businessName) {
                if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME)) && !getResources().getString(R.string.businessName_percentage).equals("0")) {
                    siteMeterTotalWeight += businessNameWeight;
                    siteData.get(businessName).setStatus(true);
                    siteData.get(businessName).setSortChar(1);
                } else {
                    siteData.get(businessName).setStatus(false);
                    siteData.get(businessName).setSortChar(2);
                }
            } else if (siteData.get(i).position == description) {
                if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_DESCRIPTION)) && !getResources().getString(R.string.businessdescription_percentage).equals("0")) {
                    siteMeterTotalWeight += businessDescriptionWeight;
                    siteData.get(description).setStatus(true);
                    siteData.get(description).setSortChar(1);
                } else {
                    siteData.get(description).setStatus(false);
                    siteData.get(description).setSortChar(2);
                }
            } else if (siteData.get(i).position == social) {
                if (Constants.twitterShareEnabled && pref.getBoolean("fbShareEnabled", false) && pref.getBoolean("fbPageShareEnabled", false)) {
                    siteMeterTotalWeight += twitterWeight;
                    siteData.get(social).setStatus(true);
                    siteData.get(social).setSortChar(1);
                } else {
                    siteData.get(social).setStatus(false);
                    siteData.get(social).setSortChar(2);
                }
            } else if (siteData.get(i).position == address) {
                if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS)) &&
                        !Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.LATITUDE)) &&
                        !Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.LONGITUDE)) &&
                        !getResources().getString(R.string.address_percentage).equals("0")) {
                    siteMeterTotalWeight += businessAddressWeight;
                    siteData.get(address).setStatus(true);
                    siteData.get(address).setSortChar(1);
                } else {
                    siteData.get(address).setStatus(false);
                    siteData.get(address).setSortChar(2);
                }
            } else if (siteData.get(i).position == email) {
                if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL)) && !getResources().getString(R.string.email_percentage).equals("0")) {
                    siteMeterTotalWeight += emailWeight;
                    siteData.get(email).setStatus(true);
                    siteData.get(email).setSortChar(1);
                } else {
                    siteData.get(email).setStatus(false);
                    siteData.get(email).setSortChar(2);
                }
            } else if (siteData.get(i).position == post) {
                if (getStorebizFloats() < 5 && fiveUpdatesDone == false) {
                    siteMeterTotalWeight += (getStorebizFloats() * onUpdate);
                    siteData.get(post).setStatus(false);
                    siteData.get(post).setSortChar(2);
                } else {
                    fiveUpdatesDone = true;
                    siteMeterTotalWeight += 20;
                    siteData.get(post).setStatus(true);
                    siteData.get(post).setSortChar(1);
                }
            } else if (siteData.get(i).position == logo) {
                if (!(Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_LogoUrl))) && !getResources().getString(R.string.Logo_percentage).equals("0")) {
                    siteMeterTotalWeight += logoWeight;
                    siteData.get(logo).setStatus(true);
                    siteData.get(logo).setSortChar(1);
                } else {
                    siteData.get(logo).setStatus(false);
                    siteData.get(logo).setSortChar(2);
                }
            } else if (siteData.get(i).position == businessHours) {
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

        WebEngageController.trackEvent(CLICKED_ON_SITE_HEALTH, SITE_HEALTH, String.valueOf(siteMeterTotalWeight));

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressBar != null) {
//                    progressBar.setProgress(siteMeterTotalWeight);
                    progressBar.setProgress(0);
                    ProgressBarAnimation mProgressAnimation = new ProgressBarAnimation(progressBar, 1000);
                    mProgressAnimation.setProgress(siteMeterTotalWeight);
                }
                if (meterReading != null)
                    meterReading.setText(siteMeterTotalWeight + "% " + getString(R.string.percent_site_completed));
            }
        });
//        if (scaleAdapter!=null)
//            scaleAdapter.notifyDataSetChanged();
        Collections.sort(siteData);
        recyclerView.setAdapter(adapter);
        if (adapter != null)
            adapter.notifyDataSetChanged();
        if (recyclerView != null)
            recyclerView.invalidate();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (activity instanceof HomeActivity && HomeActivity.headerText != null) {
            HomeActivity.headerText.setText("Site Health");
        }
        try {
            siteData = loadData();
            adapter = new SiteMeterAdapter(activity, Site_Meter_Fragment.this, siteData);
            siteMeterCalculation();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!session.getOnBoardingStatus() && session.getSiteHealth() != siteMeterTotalWeight) {
            session.setSiteHealth(siteMeterTotalWeight);
            OnBoardingApiCalls.updateData(session.getFpTag(), String.format("site_health:%s", siteMeterTotalWeight));
        }

        MixPanelController.setProperties("SiteHealth", "" + siteMeterTotalWeight);
    }

    public void SiteMeterOnClick(int value) {
        switch (value) {
            case logo:
                MixPanelController.track(EventKeysWL.LOGO, null);
                if (Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_LogoUrl))) {
                    Intent in = new Intent(activity, Business_Logo_Activity.class);
                    startActivity(in);
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;
            case address:
                MixPanelController.track(EventKeysWL.SITE_SCORE_HELP_YOUR_CUSTOMERS, null);
                if (Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS)) ||
                        Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.LATITUDE)) ||
                        Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.LONGITUDE))) {
                    Intent in = new Intent(activity, Business_Address_Activity.class);
                    startActivity(in);
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;
            case post:
                MixPanelController.track(EventKeysWL.SITE_SCORE_POST_5_UPDATES, null);
                if (getStorebizFloats() < 5 && !fiveUpdatesDone) {
                    Intent in = new Intent(activity, Create_Message_Activity.class);
                    startActivity(in);
                }
                break;
            case social:
//                MixPanelController.track(EventKeysWL.SITE_SCORE_GET_SOCIAL, null);
//                if (!(Constants.twitterShareEnabled && pref.getBoolean("fbShareEnabled", false) && pref.getBoolean("fbPageShareEnabled", false))) {
//                    Intent in = new Intent(activity, Social_Sharing_Activity.class);
//                    startActivity(in);
//                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                if (activity instanceof HomeActivity)
                    ((HomeActivity) activity).onClick(getString(R.string.title_activity_social__sharing_));
                else {
                    DigitalChannelUtil.startDigitalChannel(activity, session);
//                    Toast.makeText(activity, getString(R.string.something_went_wrong_), Toast.LENGTH_SHORT).show();
                }
                    /*Intent in = new Intent(activity, Social_Sharing_Activity.class);
                    startActivity(in);
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);*/
//                }
                break;
            case email:
                MixPanelController.track(EventKeysWL.SITE_SCORE_ADD_EMAIL, null);
                if (Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL))) {
                    Intent in = new Intent(activity, ContactInformationActivity.class);
                    startActivity(in);
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;
            case description:
                MixPanelController.track(EventKeysWL.SITE_SCORE_BUSINESS_DESC, null);
                if (Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_DESCRIPTION))) {
                    Intent in = new Intent(activity, Edit_Profile_Activity.class);
                    startActivity(in);
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;
            case domain:
                MixPanelController.track(EventKeysWL.SITE_SCORE_GET_YOUR_OWN_IDENTITY, null);
                isAlreadyCalled = false;
                if (!BuildConfig.APPLICATION_ID.equals("com.biz2.nowfloats")) {
                    MaterialDialog.Builder builder = new MaterialDialog.Builder(activity)
                            .title("Get A Domain")
                            .customView(R.layout.dialog_link_layout, false)
                            .positiveText(getString(R.string.ok))
                            .positiveColorRes(R.color.primaryColor)
                            .callback(new MaterialDialog.ButtonCallback() {
                                @Override
                                public void onPositive(MaterialDialog dialog) {
                                    super.onPositive(dialog);
                                }

                            });
                    if (!activity.isFinishing()) {
                        builder.show();
                    }
                } else if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equalsIgnoreCase("0")) {
                    showExpiryDialog(DEMO);
                } else if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE).equalsIgnoreCase("-1") &&
                        session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTLEVEL).equalsIgnoreCase("0")) {
                    showExpiryDialog(DEMO_EXPIRED);
                } else if (Utils.isNetworkConnected(getActivity())) {
                    showLoader(getString(R.string.please_wait));
                    domainApiService.getDomainDetails(activity, session.getFpTag(), getDomainDetailsParam());
                } else {
                    Methods.showSnackBarNegative(getActivity(), getString(R.string.noInternet));
                }

                break;
            case phone:
                MixPanelController.track(EventKeysWL.SITE_SCORE_PHONE_NUMBER, null);
                if (Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER))) {
                    Intent act = new Intent(activity, ContactInformationActivity.class);
                    startActivity(act);
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;
            case category:
                MixPanelController.track(EventKeysWL.SITE_SCORE_Businesss_CATEGORY, null);
                if (Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY))) {
                    Intent in = new Intent(activity, Edit_Profile_Activity.class);
                    startActivity(in);
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;
            case image:
                MixPanelController.track(EventKeysWL.SITE_SCORE_FEATURED_IMAGE, null);
                if (Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_IMAGE_URI))) {

                    Intent in = new Intent(activity, Edit_Profile_Activity.class);
                    startActivity(in);
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;
            case businessName:
                MixPanelController.track(EventKeysWL.SITE_SCORE_BUSINESS_NAME, null);
                if (Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME))) {
                    Intent in = new Intent(activity, Edit_Profile_Activity.class);
                    startActivity(in);
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
                break;
            case businessHours:
                if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_WIDGET_IMAGE_TIMINGS).equals("TIMINGS")) {
                    Intent businessHoursIntent = new Intent(activity, BusinessHoursActivity.class);
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
                                    Intent i = new Intent(activity, NewPricingPlansActivity.class);
                                    startActivity(i);
                                }
                            }).show();
                }
                break;
        }
    }

    private void showExpiryDialog(int showDialog) {

        String callUsButtonText, cancelButtonText, dialogTitle, dialogMessage;
        int dialogImage, dialogImageBgColor;

        switch (showDialog) {
            case LIGHT_HOUSE_EXPIRED:
                callUsButtonText = getString(R.string.buy_in_capital);
                cancelButtonText = getString(R.string.later_in_capital);
                dialogTitle = getString(R.string.renew_light_house_plan);
                dialogMessage = getString(R.string.light_house_plan_expired_some_features_visible);
                dialogImage = R.drawable.androidexpiryxxxhdpi;
                dialogImageBgColor = Color.parseColor("#ff0010");
                break;
            case DEMO:
                dialogImage = R.drawable.androidexpiryxxxhdpi;
                dialogImageBgColor = Color.parseColor("#ff0010");
                callUsButtonText = getString(R.string.buy_in_capital);
                cancelButtonText = getString(R.string.later_in_capital);
                dialogTitle = getString(R.string.buy_light_house_plan);
                dialogMessage = getString(R.string.buy_light_house);
                break;
            case DEMO_EXPIRED:
                dialogImage = R.drawable.androidexpiryxxxhdpi;
                dialogImageBgColor = Color.parseColor("#ff0010");
                callUsButtonText = getString(R.string.buy_in_capital);
                cancelButtonText = getString(R.string.later_in_capital);
                dialogTitle = getString(R.string.buy_light_house_plan);
                dialogMessage = getString(R.string.demo_plan_expired);
                break;
            default:
                return;
        }

        MaterialDialog mExpireDailog = new MaterialDialog.Builder(requireActivity())
                .customView(R.layout.pop_up_restrict_post_message, false)
                .backgroundColorRes(R.color.white)
                .positiveText(callUsButtonText)
                .negativeText(cancelButtonText)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent(activity, NewPricingPlansActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                })
                .show();

        View view = mExpireDailog.getCustomView();

        roboto_md_60_212121 title = (roboto_md_60_212121) view.findViewById(R.id.textView1);
        title.setText(dialogTitle);

        ImageView expireImage = (ImageView) view.findViewById(R.id.img_warning);
        expireImage.setBackgroundColor(dialogImageBgColor);
        expireImage.setImageDrawable(ContextCompat.getDrawable(activity, dialogImage));

        roboto_lt_24_212121 message = (roboto_lt_24_212121) view.findViewById(R.id.pop_up_create_message_body);
        message.setText(Methods.fromHtml(dialogMessage));
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {

        super.onStop();
    }


    /*
     ************************************* DOMAIN BOOKING PROCESS ***********************************
     *
     *
     */

    /*
     * Domain Details Param
     */
    private HashMap<String, String> getDomainDetailsParam() {
        HashMap<String, String> offersParam = new HashMap<>();
        offersParam.put("clientId", Constants.clientId);
        return offersParam;
    }

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

    @Override
    public void getDomainDetails(DomainDetails domainDetails) {
//        domainDetails = null;
        hideLoader();

        if (!isAlreadyCalled) {
            if (domainDetails == null) {
                Methods.showSnackBarNegative(activity, getString(R.string.something_went_wrong));
            } else if (domainDetails.isFailed()) {
                showCustomDialog(getString(R.string.domain_booking_failed),
                        Methods.fromHtml(TextUtils.isEmpty(domainDetails.getErrorMessage()) ?
                                getString(R.string.drop_us_contact) : domainDetails.getErrorMessage()).toString(),
                        getString(R.string.ok), null, DialogFrom.DEFAULT);
            } else if (domainDetails.isPending()) {

                showCustomDialog(getString(R.string.domain_booking_process),
                        getString(R.string.domain_booking_process_message),
                        getString(R.string.ok), null, DialogFrom.DEFAULT);
            } else {
                showDomainDetails();
            }
        }

    }

    @Override
    public void emailBookingStatus(ArrayList<EmailBookingModel.EmailBookingStatus> bookingStatuses) {

    }

    @Override
    public void getEmailBookingList(ArrayList<String> ids, String error) {

    }

    @Override
    public void getDomainSupportedTypes(ArrayList<String> arrExtensions) {

    }

    @Override
    public void domainAvailabilityStatus(String domainName, String domainType, DomainApiService.DomainAPI domainAPI) {

    }

    @Override
    public void domainBookStatus(String response) {

    }

    /* @Subscribe
     public void getStoreList(StoreEvent response) {
         ArrayList<StoreModel> allModels = response.model.AllPackages;
         ArrayList<StoreModel> activeIdArray = response.model.ActivePackages;
         ArrayList<StoreModel> additionalPlans = response.model.AllPackages;
         if (allModels != null && activeIdArray != null) {
             long storeExpiryDays = 0;
             for (StoreModel storeModel : activeIdArray) {
                 float validity = storeModel.TotalMonthsValidity;
                 Calendar cal = Calendar.getInstance();
                 int year = cal.get(Calendar.YEAR);
                 int month = cal.get(Calendar.MONTH);
                 int day = cal.get(Calendar.DAY_OF_MONTH);
                 cal.setTimeInMillis(Long.parseLong(storeModel.ToBeActivatedOn.replace("/Date(", "").replace(")/", "")));
                 cal.add(Calendar.MONTH, (int) validity);
                 cal.add(Calendar.DATE, (int) ((validity - Math.floor((double) validity)) * 30));

                 long tempExpiryDays = cal.getTimeInMillis();
                 if (tempExpiryDays > storeExpiryDays) {
                     storeExpiryDays = tempExpiryDays;
                     domainYears = 0;
                     if(cal.get(Calendar.YEAR)>= year){
                         domainYears = cal.get(Calendar.YEAR)-year;
                         if(cal.get(Calendar.MONTH)>month){
                             domainYears+=1;
                         }else if(cal.get(Calendar.MONTH) == month){
                             if(cal.get(Calendar.DAY_OF_MONTH)>day){
                                 domainYears+=1;
                             }
                         }
                     }
                 }
             }
             domainApiService.getDomainFPDetails(session.getFPID(), getDomainDetailsParam());

         } else {
             hideLoader();
             Methods.showSnackBarNegative(getActivity(), getString(R.string.something_went_wrong));
         }
     }*/

    private void showDomainDetails() {

        Intent domainIntent = new Intent(activity, DomainDetailsActivity.class);
        domainIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(domainIntent);
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void showCustomDialog(String title, String message, String postiveBtn, String negativeBtn,
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
                                MixPanelController.track(MixPanelController.DOMAIN_SEARCH, null);
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

    public int getStorebizFloats() {
        if (storebizFloats != null)
            return storebizFloats;
        else return HomeActivity.StorebizFloats.size();
    }

    private enum DialogFrom {
        DOMAIN_AVAILABLE,
        CONTACTS_AND_EMAIL_REQUIRED,
        CATEGORY_REQUIRED,
        ADDRESS_REQUIRED,
        DEFAULT
    }
}