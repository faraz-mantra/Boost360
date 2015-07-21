package com.nowfloats.NavigationDrawer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.demach.konotor.Konotor;
import com.mixpanel.android.mpmetrics.GCMReceiver;
import com.nineoldandroids.animation.Animator;
import com.nowfloats.Analytics_Screen.SearchQueries;
import com.nowfloats.BusinessProfile.UI.UI.Business_Address_Activity;
import com.nowfloats.BusinessProfile.UI.UI.Business_Hours_Activity;
import com.nowfloats.BusinessProfile.UI.UI.Business_Logo_Activity;
import com.nowfloats.BusinessProfile.UI.UI.Business_Profile_Fragment_V2;
import com.nowfloats.BusinessProfile.UI.UI.Contact_Info_Activity;
import com.nowfloats.BusinessProfile.UI.UI.Settings_Fragment;
import com.nowfloats.BusinessProfile.UI.UI.Social_Sharing_Activity;
import com.nowfloats.Business_Enquiries.Business_Enquiries_Fragment;
import com.nowfloats.Image_Gallery.Image_Gallery_Fragment;
import com.nowfloats.Login.Model.FloatsMessageModel;
import com.nowfloats.Login.Ria_Register;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.App_Update_Async_Task;
import com.nowfloats.NavigationDrawer.API.DeepLinkInterface;
import com.nowfloats.NavigationDrawer.SiteMeter.Site_Meter_Fragment;
import com.nowfloats.Product_Gallery.Product_Gallery_Fragment;
import com.nowfloats.Store.DomainLookup;
import com.nowfloats.Store.Store_Fragment;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.DefaultArtifactVersion;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HomeActivity extends AppCompatActivity implements  SidePanelFragment.OnItemClickListener,DeepLinkInterface {
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private Fragment fragmentNavigationDrawer;
    SidePanelFragment drawerFragment;
    private LinearLayout leftPanelLayout;
    Home_Fragment_Tab homeFragment;
    Business_Profile_Fragment_V2 businessFragment;
    Site_Meter_Fragment siteMeterFragment ;
    Settings_Fragment settingsFragment;
    Business_Enquiries_Fragment businessEnquiriesFragment;
    Image_Gallery_Fragment imageGalleryFragment ;
    Product_Gallery_Fragment productGalleryFragment ;
    Store_Fragment storeFragment;
    UserSessionManager session;
    Typeface robotoMedium ;
    Typeface robotoLight ;
    public static TextView headerText;
    public static ImageView plusAddButton;
    public static ImageView  shareButton;
    public String deepLinkUrl = null;
    public static ArrayList<FloatsMessageModel> StorebizFloats = new ArrayList<FloatsMessageModel>();
    private boolean showLookupDomain = false ;
    private int clickCnt = 0;
    public static Activity activity;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_v3);

        AppsFlyerLib.sendTracking(getApplicationContext());
        Log.d("HomeActivity ONcreate","onCreate");
        session = new UserSessionManager(getApplicationContext(),HomeActivity.this);
        activity = HomeActivity.this;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Methods.isOnline(HomeActivity.this);

        deepLinkUrl = GCMReceiver.deeplinkUrl;
        session = new UserSessionManager(getApplicationContext(),HomeActivity.this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                String Konotor_APP_ID = "3d54f920-9547-48ad-9f60-040713f4e5f2";
                String Konotor_APP_KEY = "dd48efe5-7c5f-45e8-9b3e-db558dd6bb5e";
                Konotor.getInstance(getApplicationContext())
                        .withNoGcmRegistration(true)
                        .withUserName(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CONTACTNAME))
                        .withIdentifier(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG))// optional name by which to display the user
                                // optional metadata for your user
                        .withUserEmail(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL)) 		// optional email address of the user
                        .init(Konotor_APP_ID,Konotor_APP_KEY);

//                String regid = gcm.register(K.ANDROID_PROJECT_SENDER_ID);


                Konotor.getInstance(getApplicationContext())
                        .withUserMeta("Country", session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY))
                        .withUserMeta("Category", session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY))
                        .withUserMeta("Business Name",session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME))
                        .withUserMeta("Payment Level",""+session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTLEVEL))
                        .update();

                Konotor.getInstance(getApplicationContext())
                        .withSupportName("Ria")
                                // optional custom name for the support person
                        .withFeedbackScreenTitle("Customer Support") 	// optional title to display when asking for feedback
                        .withNoAudioRecording(false) // optional - to disable voice messaging
                        .withNoPictureMessaging(true) // optional - to disable sending images from camera/gallery
                        .withUsesCustomSupportImage(true) // optional - set to true to use a different image to represenent the app on the chat screen. Replace konotor_support_image.png with your desired image
                        .withUsesCustomNotificationImage(true) // optional - set to true to use a different notification icon from your default app icon. Replace konotor_chat.png with your desired icon
                        .withWelcomeMessage("Welcome to NowFloats Boost! If you have any queries, please leave a message below. Also, you can reach out to us at "+getString(R.string.contact_us_number))		// optional custom welcome message for your app
                        .init(Konotor_APP_ID,Konotor_APP_KEY);

            }
        }).start();

        if (getIntent().hasExtra("message")){
            StorebizFloats = getIntent().getExtras().getParcelableArrayList("message");
        }

        AppsFlyerLib.registerConversionListener(this, new AppsFlyerConversionListener() {
            public String campaign = "";

            @Override
                    public void onInstallConversionDataLoaded(Map<String, String> stringStringMap) {
                        for (String attrName : stringStringMap.keySet()){

                            String status = stringStringMap.get("af_status");
                            String source = stringStringMap.get("media_source");
                            campaign = stringStringMap.get("campaign");

                            MixPanelController.setProperties("InstallType", status);
                            MixPanelController.setProperties("AcquisitionSource", source);
                            MixPanelController.setProperties("CampaignName", campaign);

                            Log.d("AppsFlyerTest","attribute: "+attrName+" = "+stringStringMap.get(attrName));
                            Log.d("AppsFlyerTest","attribute: "+attrName+" = "+stringStringMap.get(attrName));

                        }

                if(campaign!=null && campaign.trim().length()>0 && campaign.contains("domain"))
                {
                    if(!session.getIsFreeDomainDisplayed().equals("true")) {
                        session.storeIsFreeDomainDisplayed("true");
                        showLookupDomain = true;
                    }
                }
            }
                    @Override
                    public void onInstallConversionFailure(String s) {}
                    @Override
                    public void onAppOpenAttribution(Map<String, String> stringStringMap) {}
                    @Override
                    public void onAttributionFailure(String s) {}
                });

        MixPanelController.sendMixPanelProperties(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME),
                session.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL),
                session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG),
                session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CREATED_ON));

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        homeFragment = new Home_Fragment_Tab();
        businessFragment = new Business_Profile_Fragment_V2();
        settingsFragment = new Settings_Fragment();
        businessEnquiriesFragment = new Business_Enquiries_Fragment();
        imageGalleryFragment = new Image_Gallery_Fragment();
        productGalleryFragment = new Product_Gallery_Fragment();
        storeFragment = new Store_Fragment();
        siteMeterFragment = new Site_Meter_Fragment();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PackageInfo info = getPackageManager().getPackageInfo(
                            "com.thinksity",
                            PackageManager.GET_SIGNATURES);
                    for (Signature signature : info.signatures) {
                        MessageDigest md = MessageDigest.getInstance("SHA");
                        md.update(signature.toByteArray());
                        Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                    }
                } catch (PackageManager.NameNotFoundException e) {

                } catch (NoSuchAlgorithmException e) {

                }

            }
        }).start();


        new Thread(new Runnable() {
            @Override
            public void run() {
                SetMixPanelProperties();
            }
        }).start();

        robotoMedium = Typeface.createFromAsset(getAssets(),"Roboto-Medium.ttf");
        robotoLight = Typeface.createFromAsset(getAssets(),"Roboto-Light.ttf");

        Intent loginIntent = getIntent();
        boolean displayOnBoardingScreens = loginIntent.getBooleanExtra("fromLogin",false);
        Constants.fromLogin = displayOnBoardingScreens ;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    final boolean chk =get_VersionUpdate();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (chk) appUpdateAlertDilog(HomeActivity.this);
                        }
                    });
                }catch(Exception e){e.printStackTrace();}
            }
        }).start();

        if(Constants.fromLogin) {
            showOnBoardingScreens();
            // Constants.fromLogin = false ;
        }

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        headerText = (TextView) toolbar.findViewById(R.id.titleTextView);
        headerText.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
        headerText.setSelected(true);
        headerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Fragment myFragment = (Fragment)getSupportFragmentManager().findFragmentByTag("homeFragment");
                    if (myFragment != null && myFragment.isVisible()){
                        headerText.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
                        headerText.setSelected(true);
                        headerText.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                        headerText.setSingleLine(true);
                    }
                }catch(Exception e){e.printStackTrace();}
            }
        });
        /*This button is used in the image gallery*/
        plusAddButton = (ImageView) toolbar.findViewById(R.id.image_gallery_add_image_button);
        shareButton = (ImageView) toolbar.findViewById(R.id.business_profile_share_button);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        drawerFragment = (SidePanelFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);
        mDrawerLayout.closeDrawer(Gravity.LEFT);

        setTitle(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFrame, homeFragment, "homeFragment");
        ft.commit();
//        deepLinkUrl = "update";
        DeepLinkPage(deepLinkUrl);
    }

    public static void setGCMId(String id){
        new Ria_Register(activity,Constants.clientId,"ANDROID",id);
    }

    public void DeepLinkPage(String url) {
        Log.d("Deep Link URL","Deep Link URL : "+url);
        if(!Util.isNullOrEmpty(url)){
            if(url.contains(getResources().getString(R.string.deeplink_update)) || url.contains(getResources().getString(R.string.deeplink_featuredimage))){
//                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                ft.replace(R.id.mainFrame,homeFragment, "homeFragment").commit();

                Intent queries = new Intent(HomeActivity.this, Create_Message_Activity.class);
                startActivity(queries);
            }
            else  if(url.contains(getResources().getString(R.string.deeplink_upgrade))){
                final String appPackageName = HomeActivity.this.getPackageName(); // getPackageName() from Context or Activity object
                try {
                    HomeActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    HomeActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
            else if(url.contains(getResources().getString(R.string.deeplink_analytics))){
                Constants.deepLinkAnalytics = true ;
            }
            else if(url.contains(getResources().getString(R.string.deeplink_bizenquiry)) || url.contains("enquiries")){
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainFrame,businessEnquiriesFragment)
                    .commit();
            }
            else  if(url.contains("store") || url.contains(getResources().getString(R.string.deeplink_store)) ||
                    url.contains(getResources().getString(R.string.deeplink_propack)) ||
                    url.contains(getResources().getString(R.string.deeplink_nfstoreseo)) ||
                    url.contains(getResources().getString(R.string.deeplink_nfstorettb)) ||
                    url.contains(getResources().getString(R.string.deeplink_nfstorebiztiming)) ||
                    url.contains(getResources().getString(R.string.deeplink_nfstoreimage)) ||
                    url.contains(getResources().getString(R.string.deeplink_nfstoreimage))){

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainFrame, storeFragment)
                        .commit();
            }
            else if(url.contains(getResources().getString(R.string.deeplink_searchqueries))){
                Intent queries = new Intent(HomeActivity.this, SearchQueries.class);
                startActivity(queries);
            }
            else if(url.contains(getResources().getString(R.string.deeplink_setings))){
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainFrame, settingsFragment)
                        .commit();
            }
            else if(url.contains(getResources().getString(R.string.deeplink_socailsharing))){
                Intent queries = new Intent(HomeActivity.this, Social_Sharing_Activity.class);
                startActivity(queries);
            }
            else if(url.contains(getResources().getString(R.string.deeplink_profile))){
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainFrame,
                businessFragment,"Profile").commit();
            }
            else if(url.contains(getResources().getString(R.string.deeplink_contact))){
                Intent queries = new Intent(HomeActivity.this, Contact_Info_Activity.class);
                startActivity(queries);
            }
            else if(url.contains(getResources().getString(R.string.deeplink_bizaddress))){
                Intent queries = new Intent(HomeActivity.this, Business_Address_Activity.class);
                startActivity(queries);
            }
            else if(url.contains(getResources().getString(R.string.deeplink_bizhours))){
                 Intent queries = new Intent(HomeActivity.this, Business_Hours_Activity.class);
                 startActivity(queries);
            }
            else if(url.contains(getResources().getString(R.string.deeplink_bizlogo))){
                Intent queries = new Intent(HomeActivity.this, Business_Logo_Activity.class);
                startActivity(queries);
            }
            else if(url.contains(getResources().getString(R.string.deeplink_nfstoreDomainTTBCombo))){
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainFrame, businessEnquiriesFragment)
                    .commit();
            }
            else if(url.contains(getResources().getString(R.string.deeplink_imageGallery))){
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                  ft.replace(R.id.mainFrame,imageGalleryFragment).
                          commit();
            }
            else if(url.contains(getResources().getString(R.string.deeplink_ProductGallery))){
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainFrame,productGalleryFragment).
                        commit();
            }else if(url.contains(getResources().getString(R.string.deeplink_gplaces))){//TODO
             }
        }deepLinkUrl = null; GCMReceiver.deeplinkUrl = null;
    }

    private void SetMixPanelProperties() {
// TODO Auto-generated method stub
        try {
            JSONObject store = new JSONObject();
//            if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CONTACTNAME))){
                //store.put("name", Constants.ContactName);
//            }
            store.put("Business Name", session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
            store.put("Tag", session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG));
            store.put("Primary contact", session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER));
            if(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI)==null || session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI).equals("null")){
                store.put("Domain", "False");
            }
            else{
                store.put("Domain", "True");
            }
            store.put("FpId", session.getFPID());
            MixPanelController.identify(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG), store,session.getFPID());
        }catch(JSONException e){
            e.printStackTrace();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        Constants.fromLogin = false ;
    }

    @Override
    protected void onDestroy() {
       MixPanelController.flushMixPanel(MixPanelController.mainActivity);
       super.onDestroy();
    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        Methods.isOnline(HomeActivity.this);
    }
    public void appUpdateAlertDilog(final Activity mContext) {
        new MaterialDialog.Builder(mContext)
                .title("App update available")
                .content("You are using an old version of NowFloats Boost. Update the app to get new features and enhancements")
                .positiveText("Update")
                .negativeText("Remind me Later")
                .positiveColorRes(R.color.primaryColor)
                .negativeColorRes(R.color.primaryColor)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        final String appPackageName = mContext.getPackageName(); // getPackageName() from Context or Activity object
                        try {
                            dialog.dismiss();
                            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            dialog.dismiss();
                            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }

                })
                .show();
    }
    private void isAppUpdatedNeeded() {
        if(session.get_FIRST_TIME() == true)
        {
            String curVersion;
            try {
                curVersion = (this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName);
                App_Update_Async_Task obj=new App_Update_Async_Task(this,String.valueOf(curVersion));
                obj.execute();
            }
            catch (PackageManager.NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            session.store_FIRST_TIME(false);
            session.store_SHOW_POP_UP_TIME(System.currentTimeMillis());
        }
        else if(System.currentTimeMillis() - session.get_SHOW_POP_UP_TIME() > (3600000  * 24) ) {
            session.store_FIRST_TIME(true);
        }
        //SharedPreferences settings = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);;
    }

    private void showOnBoardingScreens() {
        MixPanelController.track(EventKeysWL.WELCOME_SCREEN_1, null);
        final Dialog dialog = new Dialog(HomeActivity.this,android.R.style.Theme_Translucent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.onboarding_popup_one);
        dialog.show();

        final CardView cardView = (CardView)dialog.findViewById(R.id.card_view);
        final ImageView imageView = (ImageView)dialog.findViewById(R.id.welcome_popup_icon);
        final TextView showNextButton = (TextView) dialog.findViewById(R.id.loginButton);
        final TextView titleTextView = (TextView) dialog.findViewById(R.id.onboarding_ScreenOne_Welcome);
        final TextView descTextView = (TextView) dialog.findViewById(R.id.onboarding_ScreenOne_desc_TextView);

        titleTextView.setTypeface(robotoMedium);
        descTextView.setTypeface(robotoMedium);
        titleTextView.setText(getResources().getString(R.string.onboarding_screen_1_title));
        descTextView.setText(getResources().getString(R.string.onboarding_screen_1_desc));
        imageView.setBackgroundColor(getResources().getColor(R.color.primaryColor));

        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            YoYo.with(Techniques.ZoomIn).duration(1000).playOn(cardView);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();

        clickCnt = 0;
        showNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickCnt == 0){
                    MixPanelController.track(EventKeysWL.WELCOME_SCREEN_2, null);
                    titleTextView.setText(getResources().getString(R.string.onboarding_screen_2_title));
                    descTextView.setText(Html.fromHtml("<font>Update your website </font>" + "<font color='#FFB900'>twice a week. </font>"+"<font>Just press the plus button at the bottom and simply tell your customers about your products, services or offers </font>"));
                    showNextButton.setText("Got it,Next");
                    imageView.setImageResource(R.drawable.onboarding_popup_two_image);
                    imageView.setBackgroundColor(getResources().getColor(R.color.primaryColor));
                    clickCnt++;
                }else if(clickCnt == 1){
                    MixPanelController.track(EventKeysWL.WELCOME_SCREEN_3, null);
                    titleTextView.setText(getResources().getString(R.string.onboarding_screen_3_title));
                    descTextView.setText(Html.fromHtml("<font>People around your business are searching for stuff you sell, so keep your updates </font>" + "<font color='#FFB900'>relevant and fresh.</font>" + "<font>This will you lead to attract more customers and generate more revenue.</font>"));
                    showNextButton.setText("Great, Thanks");
                    imageView.setImageResource(R.drawable.onboarding_popup_three);
                    imageView.setBackgroundColor(getResources().getColor(R.color.white));
                    clickCnt++;
                }else {
                    clickCnt = 0;
                    if(showLookupDomain)
                    {
                        showDomainLookUpDialog();
                    }else {
                        try {
                            YoYo.with(Techniques.ZoomOut).withListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {}
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    if (dialog != null)
                                        dialog.dismiss();
                                }
                                @Override
                                public void onAnimationCancel(Animator animation) {}
                                @Override
                                public void onAnimationRepeat(Animator animation) {}
                            }).duration(1000).playOn(cardView);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private void showDomainLookUpDialog() {

        final String purchased = "Purchased";

        new MaterialDialog.Builder(HomeActivity.this)

                .iconRes(R.drawable.domain_lookup_icon)
                .title("Get a free .com! ")
                .content("  Voila! You are now eligible to get a free domain. Request a .com/.net of your choice and we will integrate it to your website within 24 hours.")
                .positiveText("Request Now")

                .positiveColorRes(R.color.primaryColor)
                .negativeColorRes(R.color.primaryColor)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                          dialog.dismiss();

                        Intent intent = new Intent(HomeActivity.this,DomainLookup.class);
                                intent.putExtra("key",purchased);
                                startActivity(intent);
                    }

                })

                .show();


    }


    @Override
    protected void onPause() {
        super.onPause();
        if(CardAdapter_V3.pd != null)
        CardAdapter_V3.pd.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("HomeActivity", "onResume");
        Methods.isOnline(HomeActivity.this);
        com.facebook.AppEventsLogger.activateApp(HomeActivity.this, getResources().getString(R.string.facebook_app_id));

        if(session.getISEnterprise().equals("true"))
            headerText.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
        else
            setTitle(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
        plusAddButton.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("HomeActivity","onStart");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(CardAdapter_V3.pd != null)
        CardAdapter_V3.pd.dismiss();
        Log.d("","");
    }

    public void getStoredImage(String imagePath) {
        //  Log.d("Image Path"," Stored Image Path : "+imagePath);
        File cacheDir = Util.getCacheFolder(this);
        File cacheFile = new File(cacheDir, imagePath);
        InputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(cacheFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        // bitmapOptions.inSampleSize=scale;
        bitmapOptions.inJustDecodeBounds=false;
        Bitmap wallpaperBitmap = BitmapFactory.decodeStream(fileInputStream, null, bitmapOptions);
        //ImageView imageView = (ImageView) this.findViewById(R.id.preview);
        //imageView.setImageBitmap(wallpaperBitmap);
    }

    @Override
    public void recreate() {
        super.recreate();
    }

    @Override
    public void onClick(String nextScreen) {
        Methods.isOnline(HomeActivity.this);
        mDrawerLayout.closeDrawer(Gravity.LEFT);
        shareButton.setVisibility(View.GONE);
        if (nextScreen.equals("Business Profile"))
        {
            //Intent businessProfileIntent = new Intent(HomeActivity.this, BusinessProfile_HomeActivity.class);
            //startActivity(businessProfileIntent)
            // ;
            //slidingTabLayout.setVisibility(View.GONE);
            shareButton.setVisibility(View.VISIBLE);
            plusAddButton.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, businessFragment,"Profile")
            .addToBackStack(null)
            .commit();
            // getSupportFragmentManager().beginTransaction().
            //        replace(R.id.mainFrame, businessFragment).commit();

//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.mainFrame, businessFragment)
//                            // Add this transaction to the back stack
//                    .addToBackStack("Profile")
//                    .commit();
        }else if (nextScreen.equals("Image Gallery"))
        {
            // Intent imageGalleryIntent = new Intent(HomeActivity.this, Image_Gallery_MainActivity.class);
            // startActivity(imageGalleryIntent);
            getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame,imageGalleryFragment).
            commit();
        }else if (nextScreen.equals("Product Gallery"))
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame,productGalleryFragment).commit();
        }else if (nextScreen.equals("Site Meter"))
        {
            // Intent imageGalleryIntent = new Intent(HomeActivity.this, Image_Gallery_MainActivity.class);
            // startActivity(imageGalleryIntent);
            getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame,siteMeterFragment).addToBackStack(null).commit();
        }else if(nextScreen.equals("Home")) {
           headerText.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
            setTitle(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
            plusAddButton.setVisibility(View.GONE);
            /*
            if(Home_Fragment_Tab.viewPager!=null && Constants.showStoreScreen){
                Constants.showStoreScreen=false;
                Home_Fragment_Tab.viewPager.setCurrentItem(2,true);
            }*/
            getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame,homeFragment, "homeFragment").commit();
            //   getSupportFragmentManager().beginTransaction().
            //           replace(R.id.mainFrame, homeFragment).addToBackStack("Home").commit();
        }else if(nextScreen.equals("Chat"))
        {
            Konotor.getInstance(getApplicationContext()).launchFeedbackScreen(HomeActivity.this);
        }else  if(nextScreen.equals("Call"))
        {
            Intent call = new Intent(Intent.ACTION_DIAL);
            String callString = "tel:"+getString(R.string.contact_us_number);
            call.setData(Uri.parse(callString));
            startActivity(call);
        }else if(nextScreen.equals("Share"))
        {
            String url = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI);
            if (!Util.isNullOrEmpty(url)) {
                String eol = System.getProperty("line.separator");
                url = "Woohoo! We have a new website. Visit it at "
                        + eol + url.toLowerCase();
            }
            else{
                String eol = System.getProperty("line.separator");
                url = "Woohoo! We have a new website. Visit it at "
                        + eol + session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG).toLowerCase()
                        + getResources().getString(R.string.tag_for_partners);
            }
//            pref = getSharedPreferences(
//                    Constants.PREF_NAME, Activity.MODE_PRIVATE);
//            prefsEditor = pref.edit();
            shareWebsite(url);
        }else if(nextScreen.equals("Business Enquiries"))
        {
            // ft.remove(homeFragment);

            //  ft.add(R.id.mainFrame,businessEnquiriesFragment);
            //  ft.commit();
            plusAddButton.setVisibility(View.GONE);

            getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, businessEnquiriesFragment).commit();
        }else if(nextScreen.equals("Settings"))
        {
            //ft.replace(R.id.homeTabViewpager, settingsFragment);
            //ft.commit();
            plusAddButton.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, settingsFragment).commit();
        }else if(nextScreen.equals("Store")){
            shareButton.setVisibility(View.VISIBLE);
            plusAddButton.setVisibility(View.GONE);
            getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, storeFragment).commit();
        }
    }

    public void shareWebsite(String text) {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(intent, "Share with:"));
//        prefsEditor.putBoolean("shareWebsite", true);
//        prefsEditor.commit();
//        Constants.websiteShared = true;
        session.setWebsiteshare(true);
//        final String[] INTENT_FILTER = new String[] {
//                "com.twitter.android",
//                "com.facebook.katana"
//        };
//
//        List<Intent> targetShareIntents=new ArrayList<Intent>();
//        Intent shareIntent=new Intent();
//        shareIntent.setAction(Intent.ACTION_SEND);
//        shareIntent.setType("text/plain");
//        List<ResolveInfo> resInfos=getPackageManager().queryIntentActivities(shareIntent, 0);
//        if(!resInfos.isEmpty()){
//            System.out.println("Have package");
//            for(ResolveInfo resInfo : resInfos){
//                String packageName=resInfo.activityInfo.packageName;
//                Log.i("Package Name", packageName);
//                if(packageName.contains("com.twitter.android") || packageName.contains("com.facebook.katana") || packageName.contains("com.instagram.android")  ){
//                    Intent intent=new Intent();
//                    intent.setComponent(new ComponentName(packageName, resInfo.activityInfo.name));
//                    intent.setAction(Intent.ACTION_SEND);
//                    intent.setType("text/plain");
//                    intent.putExtra(Intent.EXTRA_TEXT, "Text");
//                    intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
//                    intent.setPackage(packageName);
//                    targetShareIntents.add(intent);
//                }
//            }
//            if(!targetShareIntents.isEmpty()){
//                System.out.println("Have Intent");
//                Intent chooserIntent=Intent.createChooser(targetShareIntents.remove(0), "Choose app to share");
//                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetShareIntents.toArray(new Parcelable[]{}));
//                startActivity(chooserIntent);
//            }else{
//                System.out.println("Do not Have Intent");
//                //showDialaog(this);
//            }
//        }
    }
    private boolean get_VersionUpdate() {
        try {
            String new_version = Jsoup.connect("https://play.google.com/store/apps/details?id=" + getPackageName() + "&hl=it")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .select("div[itemprop=softwareVersion]")
                    .first()
                    .ownText();
            return newer_version_available(getPackageManager().getPackageInfo(getPackageName(),0).versionName, new_version);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean newer_version_available(String local_version_string, String online_version_string){
        DefaultArtifactVersion local_version_mvn = new DefaultArtifactVersion(local_version_string);
        DefaultArtifactVersion online_version_mvn = new DefaultArtifactVersion(online_version_string);
        return local_version_mvn.compareTo(online_version_mvn) == -1 && !local_version_string.equals(new String());
    }

    @Override
    public void deepLink(String url) {
        DeepLinkPage(url);
    }
}
