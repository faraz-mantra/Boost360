package com.nowfloats.NavigationDrawer.SiteMeter;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nowfloats.BusinessProfile.UI.UI.Business_Address_Activity;
import com.nowfloats.BusinessProfile.UI.UI.Contact_Info_Activity;
import com.nowfloats.BusinessProfile.UI.UI.Edit_Profile_Activity;
import com.nowfloats.BusinessProfile.UI.UI.Social_Sharing_Activity;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.Create_Message_Activity;
import com.nowfloats.NavigationDrawer.HomeActivity;
import com.nowfloats.NavigationDrawer.SidePanelFragment;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.ProgressBarAnimation;
import com.thinksity.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
    private static int businessTimingWeight = 5;
    private static int facebookWeight = 10;
    private static int twitterWeight = 10;
    private static int shareWebsiteWeight = 5;
    private static int firstUpdatesWeight = 5;
    private static int siteMeterTotalWeight = 0;
    private static boolean fiveUpdatesDone = false;
    private static int onUpdate = 5;
    UserSessionManager session;
    private RecyclerView recyclerView;
    private SiteMeterAdapter adapter;

    private static ProgressBar progressBar;
    private TextView meterReading;
    private ArrayList<SiteMeterModel> siteData = new ArrayList<>();
    public final int domain = 0, phone = 1, category = 2,image =3, businessName=4, description=5, social=6, address=7, email=8, post=9, share=10;
    private Activity activity;
//    private ScaleInAnimationAdapter scaleAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        Methods.isOnline(activity);
        session = new UserSessionManager(activity.getApplicationContext(),activity);
        new Thread(new Runnable() {
            @Override
            public void run() {
                siteData = loadData();
                adapter = new SiteMeterAdapter(activity,Site_Meter_Fragment.this,siteData);
            }
        }).start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_site__meter, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
                        progressBar = (ProgressBar) view.findViewById(R.id.ProgressBar);
                        meterReading = (TextView) view.findViewById(R.id.site_meter_reading);
                        recyclerView = (RecyclerView) view.findViewById(R.id.sitemeter_recycler_view);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                        recyclerView.setItemAnimator(new FadeInUpAnimator());
//                        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
//                        scaleAdapter = new ScaleInAnimationAdapter(alphaAdapter);
//                        scaleAdapter.setFirstOnly(false);
//                        scaleAdapter.setInterpolator(new OvershootInterpolator());
                        recyclerView.setAdapter(adapter);
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
        ArrayList<SiteMeterModel> siteData =new ArrayList<>();
        //Set percentage according to the partners
        //0
        if (!(getResources().getString(R.string.buydomain_percentage).equals("0")))
            siteData.add(new SiteMeterModel(domain,"Get your own identity","Buy a .com","+10%",false,1));
        //1
        if (!(getResources().getString(R.string.phoneNumber_percentage).equals("0")))
            siteData.add(new SiteMeterModel(phone,"Phone number","Help customers reach you instantly","+5%",false,2));
        //2
        if (!(getResources().getString(R.string.businessCategory_percentage).equals("0")))
            siteData.add(new SiteMeterModel(category,"Business Category","Choose a Business category","+5%",false,3));
        //3
        if (!(getResources().getString(R.string.featuredImage_percentage).equals("0")))
            siteData.add(new SiteMeterModel(image,"Featured Photo","Add relevant image","+10%",false,4));
        //4
        if (!(getResources().getString(R.string.businessName_percentage).equals("0")))
            siteData.add(new SiteMeterModel(businessName,"Business Name","Add Business name","+5%",false,5));
        //5
        if (!(getResources().getString(R.string.businessdescription_percentage).equals("0")))
            siteData.add(new SiteMeterModel(description,"Business Description","Describe your business","+10%",false,6));
        //6
        if (!(getResources().getString(R.string.social_percentage).equals("0")))
            siteData.add(new SiteMeterModel(social,"Get Social","Connect to Facebook/Twitter","+10%",false,7));
        //7
        siteData.add(new SiteMeterModel(address,"Help your customers find you","Add address","+10%",false,8));
        //8
        if (!(getResources().getString(R.string.email_percentage).equals("0")))
            siteData.add(new SiteMeterModel(email,"Add Email","Add email","+5%",false,9));
        //9
        if (!(getResources().getString(R.string.postUpdate_percentage).equals("0")))
            siteData.add(new SiteMeterModel(post,"Post 5 Updates","Message regularly and relevantly","+25%",false,10));
        //10
        if (!(getResources().getString(R.string.share_percentage).equals("0")))
            siteData.add(new SiteMeterModel(share,"Share","Promote your website","+5%",false,11));
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
                if (Constants.twitterShareEnabled == false && Util.isNullOrEmpty(session.getFacebookAccessToken()) && !getResources().getString(R.string.social_percentage).equals("0")) {
                    siteData.get(social).setStatus(false);
                    siteData.get(social).setSortChar(2);
                } else {
                    siteMeterTotalWeight += twitterWeight;
                    siteData.get(social).setStatus(true);
                    siteData.get(social).setSortChar(1);
                }
            }else if(siteData.get(i).position==address) {
                if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS)) && !getResources().getString(R.string.address_percentage).equals("0")) {
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
                    siteMeterTotalWeight += onUpdate;
                    siteData.get(post).setStatus(false);
                    siteData.get(post).setSortChar(2);
                } else {
                    fiveUpdatesDone = true;
                    siteMeterTotalWeight += 25;
                    siteData.get(post).setStatus(true);
                    siteData.get(post).setSortChar(1);
                }
            }else if(siteData.get(i).position==share) {
                if (session.getWebsiteshare()) {
                    siteMeterTotalWeight += businessNameWeight;
                    siteData.get(share).setStatus(true);
                    siteData.get(share).setSortChar(1);
                } else {
                    siteData.get(share).setStatus(false);
                    siteData.get(share).setSortChar(2);
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
                    meterReading.setText(siteMeterTotalWeight+"% site completed");
                }
        });
//        if (scaleAdapter!=null)
//            scaleAdapter.notifyDataSetChanged();
//        Collections.sort(siteData);
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
            siteMeterCalculation();
        }catch(Exception e){e.printStackTrace();}
        MixPanelController.setProperties("SiteHealth", ""+siteMeterTotalWeight);
    }

    public void SiteMeterOnClick(int value) {
        switch (value){
            case share:
                MixPanelController.track(EventKeysWL.SITE_SCORE_SHARE, null);
                Constants.fromSiteMeter_Share = true ;
                shareWebsite();
                break;
            case address:
                MixPanelController.track(EventKeysWL.SITE_SCORE_HELP_YOUR_CUSTOMERS,null);
                if(Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS))){
                    Intent in = new Intent(activity, Business_Address_Activity.class);
                    startActivity(in);
                }
                break;
            case post:
                MixPanelController.track(EventKeysWL.SITE_SCORE_POST_5_UPDATES,null);
                if(HomeActivity.StorebizFloats.size()<5  && fiveUpdatesDone == false){
                    Intent in = new Intent(activity,Create_Message_Activity.class);
                    startActivity(in);
                }
            break;
            case social:
                MixPanelController.track(EventKeysWL.SITE_SCORE_GET_SOCIAL,null);
                if(Constants.twitterShareEnabled == false && Util.isNullOrEmpty(Constants.FACEBOOK_USER_ID)){
                    Intent in = new Intent(activity, Social_Sharing_Activity.class);
                    startActivity(in);
                }
            break;
            case email:
                MixPanelController.track(EventKeysWL.SITE_SCORE_ADD_EMAIL,null);
                if(Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL))) {
                    Intent in = new Intent(activity, Edit_Profile_Activity.class);
                    startActivity(in);
                }
                break;
            case description:
                MixPanelController.track(EventKeysWL.SITE_SCORE_BUSINESS_DESC,null);
                if(Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_DESCRIPTION)))
                {
                    Intent in = new Intent(activity, Edit_Profile_Activity.class);
                    startActivity(in);
                }
                break;
            case domain:
                MixPanelController.track(EventKeysWL.SITE_SCORE_GET_YOUR_OWN_IDENTITY,null);
                ((SidePanelFragment.OnItemClickListener) activity).onClick("Store");
                break;
            case phone:
                MixPanelController.track(EventKeysWL.SITE_SCORE_PHONE_NUMBER,null);
                if(Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER))){
                    Intent act = new Intent(activity, Contact_Info_Activity.class);
                    startActivity(act);}
                break;
            case category:
                MixPanelController.track(EventKeysWL.SITE_SCORE_Businesss_CATEGORY,null);
                if(Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY))){
                    Intent in = new Intent(activity, Edit_Profile_Activity.class);
                    startActivity(in);}
                break;
            case image:
                MixPanelController.track(EventKeysWL.SITE_SCORE_FEATURED_IMAGE,null);
                if(Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_IMAGE_URI))){

                    Intent in = new Intent(activity, Edit_Profile_Activity.class);
                    startActivity(in);
                }
                break;
            case businessName:
                MixPanelController.track(EventKeysWL.SITE_SCORE_BUSINESS_NAME,null);
                if(Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME)))
                {
                    Intent in = new Intent(activity, Edit_Profile_Activity.class);
                    startActivity(in);
                }
                break;
        }
    }
}