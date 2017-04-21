package com.nowfloats.NavigationDrawer;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.freshdesk.hotline.Hotline;
import com.freshdesk.hotline.HotlineConfig;
import com.freshdesk.hotline.HotlineUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.mixpanel.android.mpmetrics.GCMReceiver;
import com.nfx.leadmessages.ReadMessages;
import com.nineoldandroids.animation.Animator;
import com.nowfloats.AccountDetails.AccountInfoActivity;
import com.nowfloats.Analytics_Screen.Graph.AnalyticsActivity;
import com.nowfloats.Analytics_Screen.SearchQueries;
import com.nowfloats.Analytics_Screen.SubscribersActivity;
import com.nowfloats.BusinessProfile.UI.UI.Business_Address_Activity;
import com.nowfloats.BusinessProfile.UI.UI.Business_Hours_Activity;
import com.nowfloats.BusinessProfile.UI.UI.Business_Logo_Activity;
import com.nowfloats.BusinessProfile.UI.UI.Business_Profile_Fragment_V2;
import com.nowfloats.BusinessProfile.UI.UI.Contact_Info_Activity;
import com.nowfloats.BusinessProfile.UI.UI.Edit_Profile_Activity;
import com.nowfloats.BusinessProfile.UI.UI.Settings_Fragment;
import com.nowfloats.BusinessProfile.UI.UI.Social_Sharing_Activity;
import com.nowfloats.Business_Enquiries.Business_Enquiries_Fragment;
import com.nowfloats.CustomPage.CreateCustomPageActivity;
import com.nowfloats.CustomPage.CustomPageActivity;
import com.nowfloats.CustomPage.CustomPageAdapter;
import com.nowfloats.CustomPage.CustomPageDeleteInterface;
import com.nowfloats.CustomWidget.roboto_lt_24_212121;
import com.nowfloats.CustomWidget.roboto_md_60_212121;
import com.nowfloats.Image_Gallery.Image_Gallery_Fragment;
import com.nowfloats.Login.Login_Interface;
import com.nowfloats.Login.Model.FloatsMessageModel;
import com.nowfloats.Login.Ria_Register;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.App_Update_Async_Task;
import com.nowfloats.NavigationDrawer.API.DeepLinkInterface;
import com.nowfloats.NavigationDrawer.API.KitsuneApi;
import com.nowfloats.NavigationDrawer.Chat.ChatFragment;
import com.nowfloats.NavigationDrawer.SiteMeter.Site_Meter_Fragment;
import com.nowfloats.NavigationDrawer.model.RiaNodeDataModel;
import com.nowfloats.Product_Gallery.Product_Detail_Activity_V45;
import com.nowfloats.Product_Gallery.Product_Gallery_Fragment;
import com.nowfloats.RiaFCM.RiaFirebaseMessagingService;
import com.nowfloats.SiteAppearance.SiteAppearanceFragment;
import com.nowfloats.Store.DomainLookup;
import com.nowfloats.Store.Model.StoreEvent;
import com.nowfloats.Store.Model.StoreModel;
import com.nowfloats.Store.Service.API_Service;
import com.nowfloats.Store.StoreFragmentTab;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.DefaultArtifactVersion;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.BuildConfig;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HomeActivity extends AppCompatActivity implements  SidePanelFragment.OnItemClickListener
        ,DeepLinkInterface,CustomPageDeleteInterface,Home_Main_Fragment.OnRenewPlanClickListener,
        CardAdapter_V3.Permission, OffersFragment.OnRenewPlanClickListener, Analytics_Fragment.RiaCardDeepLinkListener {

    private Toolbar toolbar;
    private SharedPreferences pref = null;
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
    SiteAppearanceFragment mSiteAppearanceFragement;
    Product_Gallery_Fragment productGalleryFragment ;
    ChatFragment chatFragment;
    StoreFragmentTab storeFragment;
    UserSessionManager session;
    Typeface robotoMedium ;
    Typeface robotoLight ;
    MaterialDialog mExpireDailog;
    public static TextView headerText;
    public static ImageView plusAddButton;
    public static ImageView  shareButton;
    public String deepLinkUrl = null;
    private boolean isExpiredCheck = false;
    public static ArrayList<FloatsMessageModel> StorebizFloats = new ArrayList<FloatsMessageModel>();
    private boolean showLookupDomain = false ;
    private int clickCnt = 0;
    public static Activity activity;
    public static String FPID;
    public CustomPageActivity customPageActivity;
    private boolean backChk = false;
    private final int LIGHT_HOUSE_EXPIRE = 0;
    private final int WILD_FIRE_EXPIRE = 1;
    private final int DEMO_EXPIRE = 3;
    private static final int WILD_FIRE_PURCHASE = 2;
    SharedPreferences.Editor prefsEditor;
    private boolean isShownExpireDialog = false;
    private RiaNodeDataModel mRiaNodeDataModel;

    private String TAG = HomeActivity.class.getSimpleName();
    private String[] permission = new String[]{Manifest.permission.READ_SMS,Manifest.permission.RECEIVE_SMS
    ,Manifest.permission.READ_PHONE_STATE, Settings.ACTION_ACCESSIBILITY_SETTINGS};
    private final static int READ_MESSAGES_ID=221;

    @Override
    protected void attachBaseContext(Context newBase) {
       super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_v3);
        pref = getSharedPreferences(Constants.PREF_NAME,Activity.MODE_PRIVATE);
        AppsFlyerLib.sendTracking(getApplicationContext());
        BoostLog.d("HomeActivity ONcreate","onCreate");
        bus = BusProvider.getInstance().getBus();
        activity = HomeActivity.this;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
//        GCMIntentService.setHomeActivity(HomeActivity.this);
        Methods.isOnline(HomeActivity.this);
        session = new UserSessionManager(getApplicationContext(),HomeActivity.this);
        setHotlineUser();

        BoostLog.d(TAG, "In on CreateView");
        deepLinkUrl = RiaFirebaseMessagingService.deepLinkUrl;
        FPID = session.getFPID();
        if(BuildConfig.APPLICATION_ID.equals("com.biz2.nowfloats")) {
            SharedPreferences smsPref = getSharedPreferences(com.nfx.leadmessages.Constants.SHARED_PREF, Context.MODE_PRIVATE);
            smsPref.edit().putString(com.nfx.leadmessages.Constants.FP_ID, FPID).apply();
            getPermissions();
        }
        /*if (getIntent().hasExtra("message")){
            StorebizFloats = getIntent().getExtras().getParcelableArrayList("message");
        }*/

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

                            BoostLog.d("AppsFlyerTest","attribute: "+attrName+" = "+stringStringMap.get(attrName));
                            BoostLog.d("AppsFlyerTest","attribute: "+attrName+" = "+stringStringMap.get(attrName));

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
        mSiteAppearanceFragement = new SiteAppearanceFragment();
        productGalleryFragment = new Product_Gallery_Fragment();
        chatFragment = new ChatFragment();
        storeFragment = new StoreFragmentTab();
        siteMeterFragment = new Site_Meter_Fragment();
        customPageActivity = new CustomPageActivity();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PackageInfo info = getPackageManager().getPackageInfo(BuildConfig.APPLICATION_ID,
                            PackageManager.GET_SIGNATURES);
                    for (Signature signature : info.signatures) {
                        MessageDigest md = MessageDigest.getInstance("SHA");
                        md.update(signature.toByteArray());
                        BoostLog.v("ggg KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
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

//        ChatFragment.chatModels.add(new ChatModel("New Message",true,Methods.getCurrentTime()));
//        ChatFragment.chatModels.add(new ChatModel("Next Message",false,Methods.getCurrentTime()));
//        ChatFragment.chatModels.add(new ChatModel("New Message",true,Methods.getCurrentTime()));
//        ChatFragment.chatModels.add(new ChatModel("Next Message",false,Methods.getCurrentTime()));
        setTitle(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFrame, homeFragment, "homeFragment");
        ft.commit();
        //DeepLinkPage(deepLinkUrl);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BoostLog.d("cek", "home selected");
                if (drawerFragment.mDrawerToggle.isDrawerIndicatorEnabled()){
                    ((DrawerLayout) findViewById(R.id.drawer_layout)).openDrawer(Gravity.LEFT);
                } else {
                    try{
                        drawerFragment.mDrawerToggle.setDrawerIndicatorEnabled(true);
                        headerText.setText(getString(R.string.custom_pages));
                        shareButton.setVisibility(View.GONE);
                        CustomPageActivity.customPageDeleteCheck = false;
                        CustomPageAdapter.deleteCheck = false;
                        CustomPageActivity.posList = new ArrayList<String>();
                        if (CustomPageActivity.custompageAdapter!=null){
                            CustomPageActivity.custompageAdapter.updateSelection(0);
                            CustomPageActivity.custompageAdapter.notifyDataSetChanged();
                        }
                        if (CustomPageActivity.recyclerView!=null)
                            CustomPageActivity.recyclerView.invalidate();
                    }catch(Exception e){e.printStackTrace();}
                }
            }
        });
        //registerChat();
        checkExpire();

        Intent intent = getIntent();
        if(intent!=null && intent.getData()!=null){
            String action = intent.getAction();
            String data = intent.getDataString();
            BoostLog.d("Data: ", data.toString() + "  "+  action);
            if(session.checkLogin()){
                deepLink(data.substring(data.lastIndexOf("/") + 1));
            }else {
                finish();
            }
        }

    }
    private void getPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(this, ReadMessages.class);
            startService(intent);
            // start the service to send data to firebase
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // if user deny the permissions
           /* if(shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS)||
                    shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)){

                Snackbar.make(parent_layout, com.nfx.leadmessages.R.string.required_permission_to_show, Snackbar.LENGTH_INDEFINITE)
                        .setAction(com.nfx.leadmessages.R.string.enable, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                startActivity(intent);
                            }
                        })  // action text on the right side of snackbar
                        .setActionTextColor(ContextCompat.getColor(this,android.R.color.holo_green_light))
                        .show();
            }
            else{*/
            requestPermissions(permission, READ_MESSAGES_ID);
            // }

        }
    }
    private void setHotlineUser() {
        HotlineConfig hlConfig=new HotlineConfig("f3e79ba0-6b2e-4793-aaeb-e226b43473fb","a2cc59f2-d2d1-4a8f-a27a-5586a1defd6d");

        hlConfig.setVoiceMessagingEnabled(true);
        hlConfig.setCameraCaptureEnabled(true);
        hlConfig.setPictureMessagingEnabled(true);

        Hotline.getInstance(this).init(hlConfig);

        HotlineUser hlUser=Hotline.getInstance(this).getUser();
        hlUser.setName(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
        hlUser.setEmail(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL));
        hlUser.setPhone(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRYPHONECODE),
                session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER));
        Hotline.getInstance(this).updateUser(hlUser);

    }

    public static void setGCMId(String id){
        new Ria_Register(activity,Constants.clientId,"ANDROID",id);
        //registerChat(FPID,id);
        /*SdkConfig config = new SdkConfig();
        config.setGcmSenderId(id);
        config.setAnalyticsTrackingAllowedState(true);
        config.setDebuggingStateAllowed(true);*/
    }

    public static void registerChat(String userId) {
        BoostLog.d("HomeActivity", "This is getting Called");
        //String reg = session.getFPDetails(Key_Preferences.FCM_TOKEN);
        try {
            final HashMap<String, String> params = new HashMap<String, String>();
            params.put("Channel", FirebaseInstanceId.getInstance().getToken());
            params.put("UserId", userId);
            params.put("DeviceType", "ANDROID");
            params.put("clientId", Constants.clientId);

            Log.i("Ria_Register GCM id--", "API call Started");

            Login_Interface emailValidation = Constants.restAdapter.create(Login_Interface.class);
            emailValidation.post_RegisterRia(params,new Callback<String>() {
                @Override
                public void success(String s, Response response) {
                    Log.i("GCM local ","reg success" + params.toString());
                    Log.d("Response","Response : "+s);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.i("GCM local ","reg FAILed" + params.toString());
                }
            });
        } catch (Exception e) {
            Log.i("Ria_Register ", "API Exception:" + e);
            e.printStackTrace();
        }
        //new Ria_Register(activity,Constants.clientId,"ANDROID",reg);
        /*try{
            Login_Interface chat = Constants.chatRestAdapter.create(Login_Interface.class);
            chat.chat(fpid,reg,new Callback<ChatRegResponse>() {
                @Override
                public void success(ChatRegResponse s, Response response) {
                    BoostLog.i("GCM chat ", "reg success");
                    BoostLog.d("Response","Response : "+s.Status);
                }

                @Override
                public void failure(RetrofitError error) {
                    BoostLog.i("GCM chat ","reg FAILed");
                }
            });
        }catch(Exception e){
            BoostLog.i("GCM chat ","reg exp");
            e.printStackTrace();
        }*/
    }

    public void DeepLinkPage(String url, boolean isFromRia) {
        BoostLog.d("Deep Link URL","Deep Link URL : "+url);

        Constants.GCM_Msg = false;
        if(!Util.isNullOrEmpty(url)){

            if(url.contains("facebookpage")){
                Methods.likeUsFacebook(this,"/reviews/");
            }else if(url.contains(getResources().getString(R.string.deeplink_update))){
//                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                ft.replace(R.id.mainFrame,homeFragment, "homeFragment").commit();

                Intent createUpdate = new Intent(HomeActivity.this, Create_Message_Activity.class);
                if(isFromRia && mRiaNodeDataModel!=null) {
                    createUpdate.putExtra(Constants.RIA_NODE_DATA, mRiaNodeDataModel);
                }
                startActivity(createUpdate);
            }else if(url.contains(getResources().getString(R.string.deeplink_featuredimage))){
                Intent featureImage = new Intent(HomeActivity.this, Edit_Profile_Activity.class);
                if(isFromRia && mRiaNodeDataModel!=null) {
                    featureImage.putExtra(Constants.RIA_NODE_DATA, mRiaNodeDataModel);
                }
                startActivity(featureImage);
            }else if(url.contains("addProduct")){
                Intent productActivity = new Intent(HomeActivity.this, Product_Detail_Activity_V45.class);
                productActivity.putExtra("new", "");
                if(isFromRia && mRiaNodeDataModel!=null) {
                    productActivity.putExtra(Constants.RIA_NODE_DATA, mRiaNodeDataModel);
                }
                startActivity(productActivity);
            }else if(url.contains("addCustomPage")){
                Intent createCustomPage = new Intent(HomeActivity.this, CreateCustomPageActivity.class);
                if(isFromRia && mRiaNodeDataModel!=null) {
                    createCustomPage.putExtra(Constants.RIA_NODE_DATA, mRiaNodeDataModel);
                }
                startActivity(createCustomPage);
            }else if(url.contains(getResources().getString(R.string.deeplink_upgrade))){
                final String appPackageName = HomeActivity.this.getPackageName(); // getPackageName() from Context or Activity object
                try {
                    HomeActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    HomeActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
            else if(url.contains(getResources().getString(R.string.deeplink_analytics))){
                Constants.deepLinkAnalytics = true ;
                homeFragment.setFragmentTab(1);
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
            }else if(url.contains("blog")){
                String url1 = "";
                if (!Util.isNullOrEmpty(url)) {
                    url = "http://" + session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI);
                } else {
                    url = "http://" + session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG).toLowerCase()
                            + getResources().getString(R.string.tag_for_partners);
                }
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
            else if(url.contains("subscribers")){
                Intent subscribers = new Intent(HomeActivity.this, SubscribersActivity.class);
                startActivity(subscribers);
            }else if(url.contains("subscribers")){
                String url2 = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI);
                if (!Util.isNullOrEmpty(url)) {
                    String eol = System.getProperty("line.separator");
                    url2 = getString(R.string.visit_to_new_website)
                            + eol + url.toLowerCase();
                }
                else{
                    String eol = System.getProperty("line.separator");
                    url2 = getString(R.string.visit_to_new_website)
                            + eol + session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG).toLowerCase()
                            + getResources().getString(R.string.tag_for_partners);
                }
//            pref = getSharedPreferences(
//                    Constants.PREF_NAME, Activity.MODE_PRIVATE);
//            prefsEditor = pref.edit();
                shareWebsite(url2);
            }
            else if(url.contains("accountstatus")){
                Intent accountInfo = new Intent(HomeActivity.this, AccountInfoActivity.class);
                startActivity(accountInfo);
            }
            else if(url.contains("visits") || url.contains("viewgraph")){
                Intent accountInfo = new Intent(HomeActivity.this, AnalyticsActivity.class);
                accountInfo.putExtra("table_name",Constants.VISITS_TABLE);
                startActivity(accountInfo);
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
            else if(url.contains("notification")){
                homeFragment.setFragmentTab(2);
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
            else if(url.contains(getResources().getString(R.string.deeplink_bizaddress))||url.contains("address")){
                Intent queries = new Intent(HomeActivity.this, Business_Address_Activity.class);
                startActivity(queries);
            }
            else if(url.contains(getResources().getString(R.string.deeplink_bizhours))||url.contains("hours")){
                 Intent queries = new Intent(HomeActivity.this, Business_Hours_Activity.class);
                 startActivity(queries);
            }
            else if(url.contains(getResources().getString(R.string.deeplink_bizlogo))||url.contains("logo")){
                Intent queries = new Intent(HomeActivity.this, Business_Logo_Activity.class);
                startActivity(queries);
            }
            else if(url.contains(getResources().getString(R.string.deeplink_nfstoreDomainTTBCombo))|| url.contains("bookdomain")){
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainFrame, businessEnquiriesFragment)
                    .commit();
            }
            else if(url.contains("sitemeter")){
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainFrame, siteMeterFragment)
                        .commit();
            }
            else if(url.contains(getResources().getString(R.string.deeplink_imageGallery))||
                    url.contains("imagegallery") || url.contains("imagegallery")){
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                  ft.replace(R.id.mainFrame,imageGalleryFragment).
                          commit();
            }
            else if(url.contains(getResources().getString(R.string.deeplink_ProductGallery))){
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainFrame,productGalleryFragment).
                        commit();
            }else if(url.contains("chatWindow")){
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainFrame,chatFragment,"chatFragment").commit();
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
        isExpiredCheck = false;
    }

    @Override
    protected void onDestroy() {
       super.onDestroy();
        MixPanelController.flushMixPanel(MixPanelController.mainActivity);
        BoostLog.d(TAG, "In onDestroy");
        prefsEditor = pref.edit();
        prefsEditor.putBoolean("EXPIRE_DIALOG",false);
        prefsEditor.commit();

    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
//        Methods.isOnline(HomeActivity.this);
        BoostLog.i("back---",""+backChk);
        if(backChk){finish();}
        if (!backChk){
            start_backclick();
            backChk = true;
            Methods.showSnackBar(HomeActivity.this,getString(R.string.click_again_to_exit));
        }
    }

    private void start_backclick() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                    backChk = false;
                    BoostLog.i("INSIDE---",""+backChk);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void appUpdateAlertDilog(final Activity mContext) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext)
                .title(getString(R.string.app_update_available))
                .content(getString(R.string.update_nowfloats_app))
                .positiveText(getString(R.string.update))
                .negativeText(getString(R.string.remind_me_later))
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
                            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }

                });
                if(!isFinishing()) {
                    builder.show();
                }

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
                    descTextView.setText(Html.fromHtml(getString(R.string.update_website_twice_week)));
                    showNextButton.setText(getString(R.string.got_it_next));
                    imageView.setImageResource(R.drawable.onboarding_popup_two_image);
                    imageView.setBackgroundColor(getResources().getColor(R.color.primaryColor));
                    clickCnt++;
                }else if(clickCnt == 1){
                    MixPanelController.track(EventKeysWL.WELCOME_SCREEN_3, null);
                    titleTextView.setText(getResources().getString(R.string.onboarding_screen_3_title));
                    descTextView.setText(Html.fromHtml(getString(R.string.people_are_searching_keep_your_updates)));
                    showNextButton.setText(getString(R.string.greate_thanks));
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
                .title(getString(R.string.get_free_com))
                .content(getString(R.string.you_are_eligible_to_free_domain))
                .positiveText(getString(R.string.request_now))

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
        bus.unregister(this);
        if(CardAdapter_V3.pd != null)
        CardAdapter_V3.pd.dismiss();
    }
    private Bus bus;
    @Override
    protected void onResume() {
        super.onResume();
        BoostLog.d("HomeActivity", "onResume");
        Methods.isOnline(HomeActivity.this);
        //com.facebook.AppEventsLogger.activateApp(HomeActivity.this, getResources().getString(R.string.facebook_app_id));
        bus.register(this);
        if(session.getISEnterprise().equals("true"))
            headerText.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
        else
            setTitle(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
        plusAddButton.setVisibility(View.GONE);
        if(Constants.GCM_Msg){
            DeepLinkPage(RiaFirebaseMessagingService.deepLinkUrl, false);
            Constants.GCM_Msg = false;
        }

    }
    private void checkExpire(){
        isExpiredCheck = pref.getBoolean("EXPIRE_DIALOG",false);
        if(!isExpiredCheck) {
            String paymentState = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE);
            String paymentLevel = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTLEVEL);
            if (paymentState.equals("-1")) {
                try {
                    if (Integer.parseInt(paymentLevel) > 10) {
                        //LH expire
                        if(BuildConfig.APPLICATION_ID.equals("com.kitsune.biz")){
                            renewKitsune(LIGHT_HOUSE_EXPIRE);
                        }else {
                            renewPlanDialog(LIGHT_HOUSE_EXPIRE);
                        }
                    } else{
                        //Demo expire

                        if(BuildConfig.APPLICATION_ID.equals("com.kitsune.biz")){
                            renewKitsune(DEMO_EXPIRE);
                        }else {
                            renewPlanDialog(DEMO_EXPIRE);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            else {
                // LH is active ,check for wildfire
                if(!BuildConfig.APPLICATION_ID.equals("com.kitsune.biz")){
                    if (checkExpiry() && !session.isSiteAppearanceShown()) {
                        showSiteVisibilityDialog();
                    }
                    new API_Service(activity, session.getSourceClientId(), session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY),
                            session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ACCOUNTMANAGERID), session.getFPID(), bus);
                }
            }
        }
    }

    private void renewKitsune(int expiryType) {
        String dialogTitle = null;
        String dialogMessage = null;
        String callUsButtonText = "";
        String cancelButtonText = null;
        int dialogImage = 0;
        int dialogImageBgColor = 0;
        int days;
        prefsEditor = pref.edit();
        prefsEditor.putBoolean("EXPIRE_DIALOG",true);
        prefsEditor.commit();
        boolean dialogShowFlag = true;
        switch (expiryType) {
            case LIGHT_HOUSE_EXPIRE:
                dialogTitle = getString(R.string.kitsune_renew_dialog_title);
                dialogMessage = getString(R.string.kitsune_renew_dialog_body);
                dialogImage = R.drawable.androidexpiryxxxhdpi;
                dialogImageBgColor = Color.parseColor("#ff0010");
                break;
            case DEMO_EXPIRE:
                dialogImage = R.drawable.androidexpiryxxxhdpi;
                dialogImageBgColor = Color.parseColor("#ff0010");
                dialogTitle = getString(R.string.kitsune_demo_expire_dialog_title);
                dialogMessage = getString(R.string.kitsune_demo_expire_dialog_body);
                break;
            default:
                callUsButtonText = "";
                cancelButtonText = "";
                dialogTitle = "";
                dialogMessage = "";
                dialogImage = -1;
                break;
        }

        if(dialogShowFlag) {
            mExpireDailog = new MaterialDialog.Builder(this)
                    .customView(R.layout.pop_up_restrict_post_message, false)
                    .backgroundColorRes(R.color.white)
                    .positiveText("OK")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog mExpireDailog) {
                            super.onPositive(mExpireDailog);
                            //openStore();
                            mExpireDailog.dismiss();
                            prefsEditor = pref.edit();
                            prefsEditor.putBoolean("EXPIRE_DIALOG", true);
                            prefsEditor.commit();
                        }

                        @Override
                        public void onNegative(MaterialDialog mExpireDailog) {
                            super.onNegative(mExpireDailog);
                            mExpireDailog.dismiss();
                            prefsEditor = pref.edit();
                            prefsEditor.putBoolean("EXPIRE_DIALOG", true);
                            prefsEditor.putBoolean("IGNORE_CLICKED", true);
                            prefsEditor.commit();
                        }
                    }).show();

            mExpireDailog.setCancelable(true);
            View view = mExpireDailog.getCustomView();

            roboto_md_60_212121 title = (roboto_md_60_212121) view.findViewById(R.id.textView1);
            title.setText(dialogTitle);

            ImageView expireImage = (ImageView) view.findViewById(R.id.img_warning);
            expireImage.setBackgroundColor(dialogImageBgColor);
            expireImage.setImageDrawable(getResources().getDrawable(dialogImage));

            roboto_lt_24_212121 message = (roboto_lt_24_212121) view.findViewById(R.id.pop_up_create_message_body);
            message.setText(dialogMessage);
        }

    }

    private void showSiteVisibilityDialog() {
        session.setSiteAppearanceShown(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.site_appearance_dialog, null);
        final Dialog dialog = builder.setView(v).create();
        v.findViewById(R.id.btn_enable_kitsune).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                enableKitsune();
            }
        });
        v.findViewById(R.id.btn_later_kitsune).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private void enableKitsune() {
        final ProgressDialog pg = ProgressDialog.show(this,"" , getString(R.string.wait_for_new_look));
        new KitsuneApi(session.getFpTag()).setResultListener(new KitsuneApi.ResultListener() {
            @Override
            public void onResult(String response, boolean isError) {
                pg.dismiss();
                if(response.equals("true") && !isError){
                    Methods.showSnackBarPositive(HomeActivity.this, getString(R.string.your_website_appearance_changed));
                    session.storeFpWebTempalteType("6");
                }
                else {
                    Methods.showSnackBarNegative(HomeActivity.this, getString(R.string.can_not_change_appearance));
                }
            }
        }).enableKitsune();
    }

    private boolean checkExpiry() {
        boolean flag = false;
        String strExpiryTime = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_EXPIRY_DATE);
        long expiryTime = -1;
        try {
            if (strExpiryTime != null) {
                expiryTime = Long.parseLong(strExpiryTime.split("\\(")[1].split("\\)")[0]);
            }
            if (expiryTime != -1 && ((expiryTime - System.currentTimeMillis()) / 86400000 >= 180) && !session.getWebTemplateType().equals("6")) {
                flag = true;
            }
        }catch (Exception e){
            flag = false;
        }
        return flag;
    }

    @Override
    protected void onStart() {
        super.onStart();
        BoostLog.d("HomeActivity","onStart");
        isExpiredCheck = true;
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
        BoostLog.d("","");
    }

    public void getStoredImage(String imagePath) {
        //  BoostLog.d("Image Path"," Stored Image Path : "+imagePath);
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
    public void onClick(final String nextScreen) {
        Methods.isOnline(HomeActivity.this);
        mDrawerLayout.closeDrawer(Gravity.LEFT);
        shareButton.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (nextScreen.equals(getString(R.string.business_profile)))
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
                }else if(nextScreen.equals(getResources().getString(R.string.side_panel_site_appearance))){
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame,mSiteAppearanceFragement).
                            commit();
                }else if (nextScreen.equals(getString(R.string.image_gallery)))
                {
                    // Intent imageGalleryIntent = new Intent(HomeActivity.this, Image_Gallery_MainActivity.class);
                    // startActivity(imageGalleryIntent);
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame,imageGalleryFragment).
                            commit();
                }else if (nextScreen.equals(getString(R.string.product_gallery)))
                {
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame,productGalleryFragment).commit();
                }else if (nextScreen.equals(getString(R.string.site__meter)))
                {
                    // Intent imageGalleryIntent = new Intent(HomeActivity.this, Image_Gallery_MainActivity.class);
                    // startActivity(imageGalleryIntent);
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame,siteMeterFragment).addToBackStack(null).commit();
                }else if(nextScreen.equals(getString(R.string.home))) {
                    headerText.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
                    setTitle(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
                    plusAddButton.setVisibility(View.GONE);
            /*
            if(Home_Fragment_Tab.viewPager!=null && Constants.showStoreScreen){
                Constants.showStoreScreen=false;
                Home_Fragment_Tab.viewPager.setCurrentItem(2,true);
            }*/
                    boolean callMethod = false;
                    Home_Fragment_Tab myFragment = (Home_Fragment_Tab) getSupportFragmentManager().findFragmentByTag("homeFragment");
                    if (myFragment != null && myFragment.isVisible()
                            && session.isBoostBubbleEnabled()) {
                        callMethod = true;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame,homeFragment, "homeFragment").commit();
                    if(callMethod){
                        homeFragment.checkOverlay();
                    }
                    //   getSupportFragmentManager().beginTransaction().
                    //           replace(R.id.mainFrame, homeFragment).addToBackStack("Home").commit();
                }else if(nextScreen.equals(getString(R.string.chat)))
                {
                    //getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame,hotlineFragment, "chatfragment").commit();
                    //Konotor.getInstance(getApplicationContext()).launchFeedbackScreen(HomeActivity.this);
                    Hotline.showConversations(HomeActivity.this);
                    //Konotor.getInstance(getApplicationContext()).launchFeedbackScreen(HomeActivity.this);
                }else  if(nextScreen.equals(getString(R.string.call)))
                {
                    String paymentState = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE);
                    if(paymentState==null || paymentState.equals("-1") || paymentState.equals("0")) {
                        Intent call = new Intent(Intent.ACTION_DIAL);
                        String callString = "tel:" + getString(R.string.contact_us_number);
                        call.setData(Uri.parse(callString));
                        startActivity(call);
                    }else {
                        Intent supportIntent =  new Intent(HomeActivity.this, HelpAndSupportActivity.class);
                        startActivity(supportIntent);
                    }
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }else if(nextScreen.equals(getString(R.string.share)))
                {
                    String url = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI);
                    if (!Util.isNullOrEmpty(url)) {
                        String eol = System.getProperty("line.separator");
                        url = getString(R.string.visit_to_new_website)
                                + eol + url.toLowerCase();
                    }
                    else{
                        String eol = System.getProperty("line.separator");
                        url = getString(R.string.visit_to_new_website)
                                + eol + session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG).toLowerCase()
                                + getResources().getString(R.string.tag_for_partners);
                    }
//            pref = getSharedPreferences(
//                    Constants.PREF_NAME, Activity.MODE_PRIVATE);
//            prefsEditor = pref.edit();
                    shareWebsite(url);
                }/*else if(nextScreen.equals(getString(R.string.business_enquiries_title)))
                {
                    // ft.remove(homeFragment);

                    //  ft.add(R.id.mainFrame,businessEnquiriesFragment);
                    //  ft.commit();
                    plusAddButton.setVisibility(View.GONE);

                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, businessEnquiriesFragment).commit();
                }*/else if(nextScreen.equals("Settings"))
                {
                    //ft.replace(R.id.homeTabViewpager, settingsFragment);
                    //ft.commit();
                    plusAddButton.setVisibility(View.GONE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, settingsFragment).commit();
                }else if(nextScreen.equalsIgnoreCase("Store")){
                    shareButton.setVisibility(View.GONE);
                    plusAddButton.setVisibility(View.GONE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, storeFragment).commit();
                }else if(nextScreen.equals("csp")){
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, customPageActivity).commit();
//            Intent in = new Intent(HomeActivity.this, CustomPageActivity.class);
//            startActivity(in);
                }
            }
        }, 200);

    }

    public void shareWebsite(String text) {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(intent, getString(R.string.share_with)));
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
//                BoostLog.i("Package Name", packageName);
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
        DeepLinkPage(url, false);
    }

    @Override
    public void DeletePageTrigger(int position, boolean chk, View view) {
//        if(chk) getSupportActionBar().setDisplayHomeAsUpEnabled(chk);
//        if(!chk) getSupportActionBar().setDisplayShowHomeEnabled(!chk);
        if(chk){
            drawerFragment.mDrawerToggle.setDrawerIndicatorEnabled(false);
            drawerFragment.mDrawerToggle.setHomeAsUpIndicator(R.drawable.icon_action_back);}
        if(!chk){ drawerFragment.mDrawerToggle.setDrawerIndicatorEnabled(!chk); }
//        if (!chk){
//            drawerFragment.mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic);
//        }
    }

    @Override
    public void onRenewPlanSelected() {
        prefsEditor = pref.edit();
        prefsEditor.putBoolean("EXPIRE_DIALOG",false);
        prefsEditor.commit();
        checkExpire();
    }

    private void openStore(){
        if(mExpireDailog!=null && !mExpireDailog.isCancelled()){
            mExpireDailog.dismiss();
        }
        try {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putBoolean(StoreFragmentTab.IS_FROM_WILD_FIRE_MINI, true);
            storeFragment.setArguments(bundle);
            ft.replace(R.id.mainFrame, storeFragment)
                    .commit();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Subscribe
    public void getStoreList(StoreEvent response){
        ArrayList<StoreModel> allModels = response.model.AllPackages;
        ArrayList<StoreModel> activeIdArray = response.model.ActivePackages;
        if (!isShownExpireDialog) {
            if (allModels != null && activeIdArray != null) {
                printPlan(activeIdArray);
                isShownExpireDialog = true;
            }
            
            // TODO: 06-06-2016 need to handle multilple wildfire expire cases with support of api team.
        }
    }



    private void printPlan(ArrayList<StoreModel> allModels) {
        //Log.v("ggg","plans");
        for(int i=0;i<allModels.size();i++){
            if(mExpireDailog!=null && mExpireDailog.isShowing()){
                break;
            }
            String temp = allModels.get(i).Name;
            if(temp!=null && !temp.isEmpty() && (temp.contains("NowFloats WildFire")||temp.contains("NF WildFire"))){

                String date = allModels.get(i).CreatedOn;
                float totalMonthsValidity = allModels.get(i).TotalMonthsValidity;
                int remainingDay = verifyTime(date.substring(date.indexOf("(")+1,date.indexOf(")")),totalMonthsValidity);
                //Log.v("ggg",remainingDay+" days");
                if(remainingDay>0 && remainingDay<7){
                    prefsEditor = pref.edit();
                    prefsEditor.putInt("Days_remain", remainingDay);
                    prefsEditor.apply();
                }else if(remainingDay<0){
                    prefsEditor = pref.edit();
                    prefsEditor.putInt("Days_remain", -1);
                    prefsEditor.apply();
                }else{
                    return;
                }
                renewPlanDialog(WILD_FIRE_EXPIRE);
                //showWildFire();
                return;
            }
        }
        //Log.v("ggg","not showing");
        //showWildFire();
    }

    private int verifyTime(String unixtime, float months)
    {
        Long createdunixtime = Long.parseLong(unixtime);
        Calendar cal = Calendar.getInstance();
        Date currdate = cal.getTime();
        cal.setTimeInMillis(createdunixtime);
        cal.add(Calendar.MONTH, (int)months);
        cal.add(Calendar.DATE, (int)((months-(int)months)*30));
        Date dateaftersixmonths = cal.getTime();
        long diff = dateaftersixmonths.getTime() - currdate.getTime();
        int diffInDays = (int) ((diff) / (1000 * 60 * 60 * 24));
        return diffInDays;
    }

    private void renewPlanDialog(final int expireAccount) {
        String dialogTitle = null;
        String dialogMessage = null;
        String callUsButtonText = "";
        String cancelButtonText = null;
        int dialogImage = 0;
        int dialogImageBgColor = 0;
        int days;
        prefsEditor = pref.edit();
        prefsEditor.putBoolean("EXPIRE_DIALOG",true);
        prefsEditor.commit();
        boolean dialogShowFlag = true;
        switch (expireAccount) {
            case LIGHT_HOUSE_EXPIRE:
                callUsButtonText = getString(R.string.buy_in_capital);
                cancelButtonText = getString(R.string.later_in_capital);
                dialogTitle = getString(R.string.renew_light_house_plan);
                dialogMessage = getString(R.string.light_house_plan_expired_some_features_visible);
                dialogImage = R.drawable.androidexpiryxxxhdpi;
                dialogImageBgColor = Color.parseColor("#ff0010");
                break;
            case WILD_FIRE_EXPIRE:
                boolean ignoreclicked = pref.getBoolean("IGNORE_CLICKED", false);
                BoostLog.d("ILUD Boolean Vals: ", String.valueOf(ignoreclicked) + "   " + String.valueOf(dialogShowFlag));
                if(!ignoreclicked) {
                    days = pref.getInt("Days_remain", 0);
                    if (days <= 0) {
                        dialogTitle = getString(R.string.renew_wildfire_plan);
                        dialogMessage = getString(R.string.continue_auto_promoting_on_google);
                    } else {
                        dialogTitle = getString(R.string.wildfire_will_expire) + days + getString(R.string.days);
                        dialogMessage = getString(R.string.continue_auto_promoting_on_google);
                    }
                    callUsButtonText = getString(R.string.renew_in_capital);
                    cancelButtonText = getString(R.string.ignore_in_capital);
                    dialogImage = R.drawable.wild_fire_expire;
                    dialogImageBgColor = Color.parseColor("#ffffff");
                }else {
                    dialogShowFlag= false;
                }
                break;
            case DEMO_EXPIRE:
                dialogImage = R.drawable.androidexpiryxxxhdpi;
                dialogImageBgColor = Color.parseColor("#ff0010");
                callUsButtonText = getString(R.string.buy_in_capital);
                cancelButtonText = getString(R.string.later_in_capital);
                dialogTitle = getString(R.string.buy_light_house_plan);
                dialogMessage = getString(R.string.demo_plan_expired);
                break;
            default:
                callUsButtonText = "";
                cancelButtonText = "";
                dialogTitle = "";
                dialogMessage = "";
                dialogImage = -1;
                break;
        }

        if(dialogShowFlag) {
            mExpireDailog = new MaterialDialog.Builder(this)
                    .customView(R.layout.pop_up_restrict_post_message, false)
                    .backgroundColorRes(R.color.white)
                    .positiveText(callUsButtonText)
                    .negativeText(cancelButtonText)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog mExpireDailog) {
                            super.onPositive(mExpireDailog);
                            openStore();
                            mExpireDailog.dismiss();
                            prefsEditor = pref.edit();
                            prefsEditor.putBoolean("EXPIRE_DIALOG", true);
                            prefsEditor.commit();
                        }

                        @Override
                        public void onNegative(MaterialDialog mExpireDailog) {
                            super.onNegative(mExpireDailog);
                            mExpireDailog.dismiss();
                            prefsEditor = pref.edit();
                            prefsEditor.putBoolean("EXPIRE_DIALOG", true);
                            prefsEditor.putBoolean("IGNORE_CLICKED", true);
                            prefsEditor.commit();
                        }
                    }).show();

            mExpireDailog.setCancelable(true);
            View view = mExpireDailog.getCustomView();

            roboto_md_60_212121 title = (roboto_md_60_212121) view.findViewById(R.id.textView1);
            title.setText(dialogTitle);

            ImageView expireImage = (ImageView) view.findViewById(R.id.img_warning);
            expireImage.setBackgroundColor(dialogImageBgColor);
            expireImage.setImageDrawable(getResources().getDrawable(dialogImage));

            roboto_lt_24_212121 message = (roboto_lt_24_212121) view.findViewById(R.id.pop_up_create_message_body);
            message.setText(dialogMessage);
        }
    }
    private void showWildFire(){

        Calendar calendar = Calendar.getInstance();
        long oldTime = pref.getLong("wildFireMinitime",-1);
        long newTime = calendar.getTimeInMillis();
        long diff = 7*24*60*60*1000;
        //Log.v("ggg",String.valueOf(diff)+" "+String.valueOf(newTime-oldTime));
        if(oldTime != -1 && ((newTime-oldTime) < diff)){
            return;
        }
        prefsEditor  = pref.edit();
        prefsEditor.putLong("wildFireMinitime",newTime).apply();
        String paymentState = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE);
        String paymentLevel = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTLEVEL);
        if(Integer.valueOf(paymentState)>0 && Integer.valueOf(paymentLevel)>=10 ){
            View view = getLayoutInflater().inflate(R.layout.pop_up_restrict_post_message,null);
            ImageView image = (ImageView) view.findViewById(R.id.img_warning);
            ViewGroup.LayoutParams lp = image.getLayoutParams();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            image.setLayoutParams(lp);
            TextView title = (TextView) view.findViewById(R.id.textView1);
            TextView description = (TextView) view.findViewById(R.id.pop_up_create_message_body);
            title.setText("Connect with your customers instantly!");
            description.setText("The all new WildFire Mini Plan lets you reach out to your customers," +
                    "track all thier phone calls and get good leads. Start your 45 day plan now!");
            image.setImageResource(R.drawable.wildfire);
            new MaterialDialog.Builder(this)
                    .customView(view,true)
                    .backgroundColorRes(R.color.white)
                    .positiveText("BUY")
                    .negativeText("LATER")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog mExpireDailog) {
                            super.onPositive(mExpireDailog);
                            openStore();
                            MixPanelController.track(Key_Preferences.EVENT_WILDFIRE_BUY,null);
                            mExpireDailog.dismiss();
                        }

                        @Override
                        public void onNegative(MaterialDialog mExpireDailog) {
                            super.onNegative(mExpireDailog);
                            mExpireDailog.dismiss();
                        }
                    }).show();
        }
    }
    @Override
    public void getPermission() {
        BoostLog.d("Yeah:Permission ", "I am getting called");
    }

    @Override
    public void onDeepLink(String deepLinkUrl, boolean isFromRia, RiaNodeDataModel nodeDataModel) {
        mRiaNodeDataModel = nodeDataModel;
        DeepLinkPage(deepLinkUrl, isFromRia);
    }

 /*   public void wildFireStatus(){
        RestAdapter adapter =new RestAdapter.Builder()
                .setEndpoint("http://wmt.withfloats.com")
                .build();
        WildFire wildfire = adapter.create(WildFire.class);
        wildfire.getStatus(Constants.clientId,);
    }
    public interface WildFire{
        @GET("/testingapi/api/v1/account/accountstatus")
        void getStatus(@Query("clientId") String clientId,@Query("accountId") String Callback<String> response);
    }*/
}

