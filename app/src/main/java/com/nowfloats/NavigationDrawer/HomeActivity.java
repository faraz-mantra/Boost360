package com.nowfloats.NavigationDrawer;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.freshdesk.hotline.Hotline;
import com.freshdesk.hotline.HotlineConfig;
import com.freshdesk.hotline.HotlineUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.nfx.leadmessages.ReadMessages;
import com.nineoldandroids.animation.Animator;
import com.nowfloats.AccountDetails.AccountInfoActivity;
import com.nowfloats.Analytics_Screen.Graph.AnalyticsActivity;
import com.nowfloats.Analytics_Screen.SearchQueries;
import com.nowfloats.Analytics_Screen.SubscribersActivity;
import com.nowfloats.Analytics_Screen.model.NfxGetTokensResponse;
import com.nowfloats.BusinessProfile.UI.UI.Business_Address_Activity;
import com.nowfloats.BusinessProfile.UI.UI.Business_Hours_Activity;
import com.nowfloats.BusinessProfile.UI.UI.Business_Logo_Activity;
import com.nowfloats.BusinessProfile.UI.UI.Business_Profile_Fragment_V2;
import com.nowfloats.BusinessProfile.UI.UI.Contact_Info_Activity;
import com.nowfloats.BusinessProfile.UI.UI.Edit_Profile_Activity;
import com.nowfloats.BusinessProfile.UI.UI.Settings_Fragment;
import com.nowfloats.BusinessProfile.UI.UI.SocialSharingFragment;
import com.nowfloats.BusinessProfile.UI.UI.Social_Sharing_Activity;
import com.nowfloats.Business_Enquiries.Business_Enquiries_Fragment;
import com.nowfloats.CustomPage.CreateCustomPageActivity;
import com.nowfloats.CustomPage.CustomPageAdapter;
import com.nowfloats.CustomPage.CustomPageDeleteInterface;
import com.nowfloats.CustomPage.CustomPageFragment;
import com.nowfloats.CustomWidget.roboto_lt_24_212121;
import com.nowfloats.CustomWidget.roboto_md_60_212121;
import com.nowfloats.Image_Gallery.Image_Gallery_Fragment;
import com.nowfloats.Login.API_Login;
import com.nowfloats.Login.Login_Interface;
import com.nowfloats.Login.Model.FloatsMessageModel;
import com.nowfloats.Login.Ria_Register;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.App_Update_Async_Task;
import com.nowfloats.NavigationDrawer.API.DeepLinkInterface;
import com.nowfloats.NavigationDrawer.API.GetVisitorsAndSubscribersCountAsyncTask;
import com.nowfloats.NavigationDrawer.Chat.ChatFragment;
import com.nowfloats.NavigationDrawer.SiteMeter.Site_Meter_Fragment;
import com.nowfloats.NavigationDrawer.businessApps.BusinessAppsActivity;
import com.nowfloats.NavigationDrawer.businessApps.BusinessAppsFragment;
import com.nowfloats.NavigationDrawer.model.RiaNodeDataModel;
import com.nowfloats.Product_Gallery.Product_Detail_Activity_V45;
import com.nowfloats.Product_Gallery.Product_Gallery_Fragment;
import com.nowfloats.RiaFCM.RiaFirebaseMessagingService;
import com.nowfloats.SiteAppearance.SiteAppearanceFragment;
import com.nowfloats.Store.DomainLookup;
import com.nowfloats.Store.Model.StoreEvent;
import com.nowfloats.Store.Model.StoreModel;
import com.nowfloats.Store.StoreFragmentTab;
import com.nowfloats.bubble.CustomerAssistantService;
import com.nowfloats.customerassistant.models.SMSSuggestions;
import com.nowfloats.customerassistant.service.CustomerAssistantApi;
import com.nowfloats.managecustomers.FacebookChatDetailActivity;
import com.nowfloats.managecustomers.ManageCustomerFragment;
import com.nowfloats.manageinventory.ManageInventoryFragment;
import com.nowfloats.signup.UI.Model.Get_FP_Details_Event;
import com.nowfloats.signup.UI.Service.Get_FP_Details_Service;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.BoostLog;
import com.nowfloats.util.BusProvider;
import com.nowfloats.util.Constants;
import com.nowfloats.util.DefaultArtifactVersion;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.Methods;
import com.nowfloats.util.MixPanelController;
import com.nowfloats.util.Utils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.BuildConfig;
import com.thinksity.R;
import com.thinksity.Specific;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.nowfloats.NavigationDrawer.businessApps.BusinessAppsFragment.BIZ_APP_DEMO;
import static com.nowfloats.NavigationDrawer.businessApps.BusinessAppsFragment.BIZ_APP_DEMO_REMOVE;
import static com.nowfloats.NavigationDrawer.businessApps.BusinessAppsFragment.BIZ_APP_PAID;


public class HomeActivity extends AppCompatActivity implements SidePanelFragment.OnItemClickListener
        , DeepLinkInterface, CustomPageDeleteInterface, Home_Main_Fragment.OnRenewPlanClickListener,
        CardAdapter_V3.Permission, OffersFragment.OnRenewPlanClickListener, Analytics_Fragment.RiaCardDeepLinkListener,
        API_Login.API_Login_Interface {


    private Toolbar toolbar;
    private SharedPreferences pref = null;
    private DrawerLayout mDrawerLayout;
    private Fragment fragmentNavigationDrawer;
    SidePanelFragment drawerFragment;
    private LinearLayout leftPanelLayout;
    Home_Fragment_Tab homeFragment;
    Business_Profile_Fragment_V2 businessFragment;
    ManageCustomerFragment manageCustomerFragment;
    ManageInventoryFragment manageInventoryFragment;
    Site_Meter_Fragment siteMeterFragment;
    Settings_Fragment settingsFragment;
    Business_Enquiries_Fragment businessEnquiriesFragment;
    Image_Gallery_Fragment imageGalleryFragment;
    SiteAppearanceFragment mSiteAppearanceFragement;
    Product_Gallery_Fragment productGalleryFragment;
    ChatFragment chatFragment;
    StoreFragmentTab storeFragment;
    SocialSharingFragment socialSharingFragment;
    HelpAndSupportFragment helpAndSupportFragment;
    UserSessionManager session;
    Typeface robotoMedium;
    Typeface robotoLight;
    MaterialDialog mExpireDailog;
    public static TextView headerText;
    public static ImageView plusAddButton;
    public static ImageView shareButton;
    public String deepLinkUrl = null;
    private boolean isExpiredCheck = false;
    public static ArrayList<FloatsMessageModel> StorebizFloats = new ArrayList<FloatsMessageModel>();
    private boolean showLookupDomain = false;
    private int clickCnt = 0;
    public static Activity activity;
    public static String FPID;
    public CustomPageFragment customPageActivity;
    private boolean backChk = false;
    private final int LIGHT_HOUSE_EXPIRE = 0;
    private final int WILD_FIRE_EXPIRE = 1;
    private final int DEMO_EXPIRE = 3;
    private static final int DEMO_DAYS_LEFT = 4;
    private static final int LIGHT_HOUSE_DAYS_LEFT = 5;
    private static final int WILD_FIRE_PURCHASE = 2;
    SharedPreferences.Editor prefsEditor;
    private boolean isShownExpireDialog = false;
    private RiaNodeDataModel mRiaNodeDataModel;
    private String mDeepLinkUrl;
    private String TAG = HomeActivity.class.getSimpleName();
    private String[] permission = new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS
            , Manifest.permission.READ_PHONE_STATE};
    private final static int READ_MESSAGES_ID = 221;
    //private ArrayList<AccountDetailModel> accountDetailsModel = new ArrayList<>();

    private ProgressDialog progressDialog;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getSharedPreferences(Constants.PREF_NAME, Activity.MODE_PRIVATE);
        BoostLog.d("HomeActivity ONcreate", "onCreate");
        bus = BusProvider.getInstance().getBus();
        activity = HomeActivity.this;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
//        GCMIntentService.setHomeActivity(HomeActivity.this);
        Methods.isOnline(HomeActivity.this);

        session = new UserSessionManager(getApplicationContext(), HomeActivity.this);
        setHotlineUser();


        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("url")) {
            mDeepLinkUrl = bundle.getString("url");
        }
        if (bundle != null && bundle.containsKey("Username")) {
        } else {
            createView();
        }

    }

    private void getPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
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
        HotlineConfig hlConfig = new HotlineConfig("f3e79ba0-6b2e-4793-aaeb-e226b43473fb", "a2cc59f2-d2d1-4a8f-a27a-5586a1defd6d");

        hlConfig.setVoiceMessagingEnabled(true);
        hlConfig.setCameraCaptureEnabled(true);
        hlConfig.setPictureMessagingEnabled(true);

        Hotline.getInstance(this).init(hlConfig);

        HotlineUser hlUser = Hotline.getInstance(this).getUser();
        hlUser.setName(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
        hlUser.setEmail(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL));
        hlUser.setPhone(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRYPHONECODE),
                session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PRIMARY_NUMBER));
        Hotline.getInstance(this).updateUser(hlUser);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == READ_MESSAGES_ID) {

            List<Integer> intList = new ArrayList<Integer>();
            for (int i : grantResults) {
                intList.add(i);
            }
            if (!intList.contains(PackageManager.PERMISSION_DENIED)) {
                Intent intent = new Intent(this, ReadMessages.class);
                startService(intent);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public static void setGCMId(String id) {
        new Ria_Register(activity, Constants.clientId, "ANDROID", id);
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
            emailValidation.post_RegisterRia(params, new Callback<String>() {
                @Override
                public void success(String s, Response response) {
                    Log.i("GCM local ", "reg success" + params.toString());
                    Log.d("Response", "Response : " + s);
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.i("GCM local ", "reg FAILed" + params.toString());
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
        BoostLog.d("Deep Link URL", "Deep Link URL : " + url);

        Constants.GCM_Msg = false;
        if (!Util.isNullOrEmpty(url)) {
            if (url.contains(getString(R.string.facebook_chat))) {
                Intent intent = new Intent(this, FacebookChatDetailActivity.class);
                intent.putExtras(getIntent());
                startActivity(intent);
            } else if (url.contains("facebookpage")) {
                Methods.likeUsFacebook(this, "/reviews/");
            } else if (url.contains(getResources().getString(R.string.deeplink_update))) {
//                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//                ft.replace(R.id.mainFrame,homeFragment, "homeFragment").commit();

                Intent createUpdate = new Intent(HomeActivity.this, Create_Message_Activity.class);
                if (isFromRia && mRiaNodeDataModel != null) {
                    createUpdate.putExtra(Constants.RIA_NODE_DATA, mRiaNodeDataModel);
                }
                startActivity(createUpdate);
            } else if (url.contains(getResources().getString(R.string.deeplink_featuredimage))) {
                Intent featureImage = new Intent(HomeActivity.this, Edit_Profile_Activity.class);
                if (isFromRia && mRiaNodeDataModel != null) {
                    featureImage.putExtra(Constants.RIA_NODE_DATA, mRiaNodeDataModel);
                }
                startActivity(featureImage);
            } else if (url.contains("addProduct")) {
                Intent productActivity = new Intent(HomeActivity.this, Product_Detail_Activity_V45.class);
                productActivity.putExtra("new", "");
                if (isFromRia && mRiaNodeDataModel != null) {
                    productActivity.putExtra(Constants.RIA_NODE_DATA, mRiaNodeDataModel);
                }
                startActivity(productActivity);
            } else if (url.contains("addCustomPage")) {
                Intent createCustomPage = new Intent(HomeActivity.this, CreateCustomPageActivity.class);
                if (isFromRia && mRiaNodeDataModel != null) {
                    createCustomPage.putExtra(Constants.RIA_NODE_DATA, mRiaNodeDataModel);
                }
                startActivity(createCustomPage);
            } else if (url.contains(getResources().getString(R.string.deeplink_upgrade))) {
                final String appPackageName = HomeActivity.this.getPackageName(); // getPackageName() from Context or Activity object
                try {
                    HomeActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    HomeActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            } else if (url.contains(getResources().getString(R.string.deeplink_analytics))) {
                Constants.deepLinkAnalytics = true;
                homeFragment.setFragmentTab(1);
            } else if (url.contains(getResources().getString(R.string.deeplink_bizenquiry)) || url.contains("enquiries")) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainFrame, businessEnquiriesFragment)
                        .commit();
            } else if (url.contains("store") || url.contains(getResources().getString(R.string.deeplink_store)) ||
                    url.contains(getResources().getString(R.string.deeplink_propack)) ||
                    url.contains(getResources().getString(R.string.deeplink_nfstoreseo)) ||
                    url.contains(getResources().getString(R.string.deeplink_nfstorettb)) ||
                    url.contains(getResources().getString(R.string.deeplink_nfstorebiztiming)) ||
                    url.contains(getResources().getString(R.string.deeplink_nfstoreimage)) ||
                    url.contains(getResources().getString(R.string.deeplink_nfstoreimage))) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainFrame, storeFragment)
                        .commit();
            } else if (url.contains(getResources().getString(R.string.deeplink_searchqueries))) {
                Intent queries = new Intent(HomeActivity.this, SearchQueries.class);
                startActivity(queries);
            } else if (url.contains("blog")) {

                if (!Util.isNullOrEmpty(url)) {
                    url = "http://" + session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI);
                } else {
                    url = "http://" + session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG).toLowerCase()
                            + getResources().getString(R.string.tag_for_partners);
                }
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } else if (url.contains("subscribers")) {
                Intent subscribers = new Intent(HomeActivity.this, SubscribersActivity.class);
                startActivity(subscribers);
            } else if (url.contains("share")) {
                shareWebsite();
            } else if (url.contains("accountstatus")) {
                Intent accountInfo = new Intent(HomeActivity.this, AccountInfoActivity.class);
                startActivity(accountInfo);
            } else if (url.contains("visits") || url.contains("viewgraph")) {
                Intent accountInfo = new Intent(HomeActivity.this, AnalyticsActivity.class);
                accountInfo.putExtra("table_name", Constants.VISITS_TABLE);
                startActivity(accountInfo);
            } else if (url.contains(getResources().getString(R.string.deeplink_setings))) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainFrame, settingsFragment)
                        .commit();
            } else if (url.contains(getResources().getString(R.string.deeplink_business_app))) {
                startBusinessApp();
            } else if (url.contains(getResources().getString(R.string.deeplink_socailsharing))) {
                Intent queries = new Intent(HomeActivity.this, Social_Sharing_Activity.class);
                startActivity(queries);
            } else if (url.contains("notification")) {
                homeFragment.setFragmentTab(2);
            } else if (url.contains(getResources().getString(R.string.deeplink_profile))) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainFrame, businessFragment, "Profile").commit();
            } else if (url.contains(getResources().getString(R.string.deeplink_contact))) {
                Intent queries = new Intent(HomeActivity.this, Contact_Info_Activity.class);
                startActivity(queries);
            } else if (url.contains(getResources().getString(R.string.deeplink_bizaddress)) || url.contains("address")) {
                Intent queries = new Intent(HomeActivity.this, Business_Address_Activity.class);
                startActivity(queries);
            } else if (url.contains(getResources().getString(R.string.deeplink_bizhours)) || url.contains("hours")) {
                Intent queries = new Intent(HomeActivity.this, Business_Hours_Activity.class);
                startActivity(queries);
            } else if (url.contains(getResources().getString(R.string.deeplink_bizlogo)) || url.contains("logo")) {
                Intent queries = new Intent(HomeActivity.this, Business_Logo_Activity.class);
                startActivity(queries);
            } else if (url.contains(getResources().getString(R.string.deeplink_nfstoreDomainTTBCombo)) || url.contains("bookdomain")) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainFrame, businessEnquiriesFragment)
                        .commit();
            } else if (url.contains("sitemeter")) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainFrame, siteMeterFragment)
                        .commit();
            } else if (url.contains(getResources().getString(R.string.deeplink_imageGallery)) ||
                    url.contains("imagegallery") || url.contains("imagegallery")) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainFrame, imageGalleryFragment).
                        commit();
            } else if (url.contains(getResources().getString(R.string.deeplink_ProductGallery))) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainFrame, productGalleryFragment).
                        commit();
            } else if (url.contains("chatWindow")) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainFrame, chatFragment, "chatFragment").commit();
            } else if (url.contains(getResources().getString(R.string.deeplink_gplaces))) {//TODO
            }

        }
        deepLinkUrl = null;
    }

    private String getCountryCode(){
        String[] string_array = getResources().getStringArray(R.array.CountryCodes);
        for(String country_phone : string_array) {
            String[] Codes = country_phone.split(",");
            if(Codes[0].equalsIgnoreCase(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRYPHONECODE))){
                return Codes[1];
            }
        }
        return "";
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
            store.put("Primary contact", session.getFPDetails(Key_Preferences.MAIN_PRIMARY_CONTACT_NUM));
            store.put("$phone",session.getFPDetails(Key_Preferences.MAIN_PRIMARY_CONTACT_NUM));
            store.put("$email",session.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL));
            store.put("$city",session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CITY));
            store.put("$country_code",getCountryCode());
            if (session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI) == null || session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI).equals("null")) {
                store.put("Domain", "False");
            } else {
                store.put("Domain", "True");
            }
            store.put("FpId", session.getFPID());
            MixPanelController.identify(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG), store, session.getFPID());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        bus.unregister(this);
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName componentName = am.getRunningTasks(1).get(0).topActivity;
        if (!componentName.getPackageName().equalsIgnoreCase(getApplicationContext().getPackageName())) {
            sendBroadcast(new Intent(CustomerAssistantService.ACTION_ADD_BUBBLE));
        }

        Constants.fromLogin = false;
        isExpiredCheck = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MixPanelController.flushMixPanel(MixPanelController.mainActivity);
        BoostLog.d(TAG, "In onDestroy");
        prefsEditor = pref.edit();
        prefsEditor.putBoolean("EXPIRE_DIALOG", false);
        prefsEditor.apply();

    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
//        Methods.isOnline(HomeActivity.this);
        BoostLog.i("back---", "" + backChk);
        if (backChk) {
            finish();
        }
        if (!backChk) {
            start_backclick();
            backChk = true;
            Methods.showSnackBar(HomeActivity.this, getString(R.string.click_again_to_exit));
        }
    }

    private void start_backclick() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                    backChk = false;
                    BoostLog.i("INSIDE---", "" + backChk);
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
                .cancelable(false)
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
        if (!isFinishing()) {
            builder.show();
        }

    }

    private void isAppUpdatedNeeded() {
        if (session.get_FIRST_TIME()) {
            String curVersion;
            try {
                curVersion = (this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName);
                App_Update_Async_Task obj = new App_Update_Async_Task(this, String.valueOf(curVersion));
                obj.execute();
            } catch (PackageManager.NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            session.store_FIRST_TIME(false);
            session.store_SHOW_POP_UP_TIME(System.currentTimeMillis());
        } else if (System.currentTimeMillis() - session.get_SHOW_POP_UP_TIME() > (3600000 * 24)) {
            session.store_FIRST_TIME(true);
        }
        //SharedPreferences settings = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);;
    }

    private void showOnBoardingScreens() {
        MixPanelController.track(EventKeysWL.WELCOME_SCREEN_1, null);
        final Dialog dialog = new Dialog(HomeActivity.this, android.R.style.Theme_Translucent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.onboarding_popup_one);
        dialog.show();

        final CardView cardView = (CardView) dialog.findViewById(R.id.card_view);
        final ImageView imageView = (ImageView) dialog.findViewById(R.id.welcome_popup_icon);
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
                if (clickCnt == 0) {
                    MixPanelController.track(EventKeysWL.WELCOME_SCREEN_2, null);
                    titleTextView.setText(getResources().getString(R.string.onboarding_screen_2_title));
                    descTextView.setText(Methods.fromHtml(getString(R.string.update_website_twice_week)));
                    showNextButton.setText(getString(R.string.got_it_next));
                    imageView.setImageResource(R.drawable.onboarding_popup_two_image);
                    imageView.setBackgroundColor(getResources().getColor(R.color.primaryColor));
                    clickCnt++;
                } else if (clickCnt == 1) {
                    MixPanelController.track(EventKeysWL.WELCOME_SCREEN_3, null);
                    titleTextView.setText(getResources().getString(R.string.onboarding_screen_3_title));
                    descTextView.setText(Html.fromHtml(getString(R.string.people_are_searching_keep_your_updates)));
                    showNextButton.setText(getString(R.string.greate_thanks));
                    imageView.setImageResource(R.drawable.onboarding_popup_three);
                    PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(ContextCompat.getColor(HomeActivity.this, R.color.primaryColor), PorterDuff.Mode.SRC_IN);
                    imageView.setColorFilter(colorFilter);
                    imageView.setBackgroundColor(getResources().getColor(R.color.white));
                    clickCnt++;
                } else {
                    clickCnt = 0;
                    if (showLookupDomain) {
                        showDomainLookUpDialog();
                    } else {
                        try {
                            YoYo.with(Techniques.ZoomOut).withListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    if (dialog != null)
                                        dialog.dismiss();
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {
                                }
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

                        Intent intent = new Intent(HomeActivity.this, DomainLookup.class);
                        intent.putExtra("key", purchased);
                        startActivity(intent);
                    }

                })

                .show();


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (CardAdapter_V3.pd != null)
            CardAdapter_V3.pd.dismiss();
    }


    private Bus bus;

    @Override
    protected void onResume() {
        super.onResume();
        BoostLog.d("HomeActivity", "onResume");
        Methods.isOnline(HomeActivity.this);
        //com.facebook.AppEventsLogger.activateApp(HomeActivity.this, getResources().getString(R.string.facebook_app_id));

        if (session != null) {

            if (session.getISEnterprise().equals("true"))
                headerText.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
            else
                setTitle(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
            if (plusAddButton != null)
                plusAddButton.setVisibility(View.GONE);

        }

        if (!isCalled) {
            navigateView();
        }
        //DeepLinkPage(mDeepLinkUrl, false);
        //mDeepLinkUrl = null;

        if (pref.getBoolean(Key_Preferences.HAS_SUGGESTIONS, false)) {
            checkCustomerAssistantService();
        }
    }

    private void getCustomerAssistantSuggestions() {
        CustomerAssistantApi suggestionsApi = new CustomerAssistantApi(bus);
        if (Utils.isNetworkConnected(this)) {
            HashMap<String, String> offersParam = new HashMap<>();
            offersParam.put("fpId", session.getFPID());
            suggestionsApi.getMessages(offersParam);
        }
    }

    @Subscribe
    public void processSmsData(SMSSuggestions smsSuggestions) {

        if (smsSuggestions != null && smsSuggestions.getSuggestionList() != null
                && smsSuggestions.getSuggestionList().size() > 0) {
            pref.edit().putBoolean(Key_Preferences.HAS_SUGGESTIONS, true).apply();
            checkCustomerAssistantService();
        } else {
            stopService(new Intent(this, CustomerAssistantService.class));
        }
    }

    private void checkCustomerAssistantService() {
        if (Methods.hasOverlayPerm(HomeActivity.this)) {

            if (!Methods.isMyServiceRunning(this, CustomerAssistantService.class)) {
                Intent bubbleIntent = new Intent(this, CustomerAssistantService.class);
                startService(bubbleIntent);
            }
        }
        sendBroadcast(new Intent(CustomerAssistantService.ACTION_REMOVE_BUBBLE));
    }

    private void checkExpiry1() {
        if (Constants.PACKAGE_NAME.equals("com.kitsune.biz")) {
            return;
        }
        String paymentState = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE);
        String paymentLevel = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTLEVEL);
        if (TextUtils.isEmpty(paymentState) || TextUtils.isEmpty(paymentLevel)) {
            return;
        }
        Calendar calendar = Calendar.getInstance();
        Long currentTime = calendar.getTimeInMillis();

        switch (Integer.parseInt(paymentState)) {

            case -1:
                if (Integer.parseInt(paymentLevel) > 10) {
                    showDialog1(LIGHT_HOUSE_EXPIRE, -1);
                } else {
                    showDialog1(DEMO_EXPIRE, -1);
                }
                break;
            /*case 0:
                String fpCreatedDate = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CREATED_ON);
                if(fpCreatedDate.contains("/Date")){
                    fpCreatedDate = fpCreatedDate.replace("/Date(", "").replace(")/", "");
                }
                float days = ((currentTime-Long.valueOf(fpCreatedDate))/(float)(1000*60*60*24));

                if( days <= 7){
                    //seven days dialog ervery day
                    showDialog1(DEMO_DAYS_LEFT,days);
                }else if(days< 30){
                    //once a week
                    long prev = pref.getLong("expire_dialog",-1);
                    if((currentTime-prev)/(60*60*24*1000) >= 7) {
                        showDialog1(DEMO_DAYS_LEFT,days);
                    }
                }
                break;
            case 1:
                if(!BuildConfig.APPLICATION_ID.equals("com.kitsune.biz")){
                    if (checkExpiry() && !session.isSiteAppearanceShown()) {
                        showSiteVisibilityDialog();
                    }
                }
                getAccountDetails();
                break;*/
        }

    }

    private void showDialog1(int showDialog, float days) {

        String callUsButtonText, cancelButtonText, dialogTitle, dialogMessage;
        int dialogImage, dialogImageBgColor;

        switch (showDialog) {
            case LIGHT_HOUSE_EXPIRE:
                callUsButtonText = getString(R.string.buy_in_capital);
                cancelButtonText = getString(R.string.later_in_capital);
                dialogTitle = getString(R.string.renew_light_house_plan);
                dialogMessage = getString(R.string.light_house_plan_expired_some_features_visible);
                dialogImage = R.drawable.androidexpiryxxxhdpi;
                dialogImageBgColor = Color.parseColor("#ff0010");
                break;
            case WILD_FIRE_EXPIRE:
                dialogTitle = getString(R.string.renew_wildfire_plan);
                dialogMessage = getString(R.string.continue_auto_promoting_on_google);
                callUsButtonText = getString(R.string.renew_in_capital);
                cancelButtonText = getString(R.string.ignore_in_capital);
                dialogImage = R.drawable.wild_fire_expire;
                dialogImageBgColor = Color.parseColor("#ffffff");
                break;
            case DEMO_EXPIRE:
                dialogImage = R.drawable.androidexpiryxxxhdpi;
                dialogImageBgColor = Color.parseColor("#ff0010");
                callUsButtonText = getString(R.string.buy_in_capital);
                cancelButtonText = getString(R.string.later_in_capital);
                dialogTitle = getString(R.string.buy_light_house_plan);
                dialogMessage = getString(R.string.demo_plan_expired);
                break;
            case DEMO_DAYS_LEFT:
                dialogImage = R.drawable.androidexpiryxxxhdpi;
                dialogImageBgColor = Color.parseColor("#ff0010");
                callUsButtonText = getString(R.string.buy_in_capital);
                cancelButtonText = getString(R.string.later_in_capital);
                dialogTitle = getString(R.string.buy_light_house_plan);
                if (days < 1) {
                    dialogMessage = String.format(getString(R.string.demo_plan_will_expire), "< 1");
                } else {
                    dialogMessage = String.format(getString(R.string.demo_plan_will_expire), (int) Math.floor(days) + " ");
                }
                break;
            case LIGHT_HOUSE_DAYS_LEFT:
                callUsButtonText = getString(R.string.buy_in_capital);
                cancelButtonText = getString(R.string.later_in_capital);
                dialogTitle = getString(R.string.renew_light_house_plan);

                if (days < 1) {
                    dialogMessage = String.format(getString(R.string.light_house_pla_will_expire), "< 1");
                } else {
                    dialogMessage = String.format(getString(R.string.light_house_pla_will_expire), (int) Math.floor(days) + " ");
                }

                dialogImage = R.drawable.androidexpiryxxxhdpi;
                dialogImageBgColor = Color.parseColor("#ff0010");
                break;
            default:
                return;
        }
        pref.edit().putLong("expire_dialog", Calendar.getInstance().getTimeInMillis()).apply();

        MaterialDialog mExpireDailog = new MaterialDialog.Builder(this)
                .customView(R.layout.pop_up_restrict_post_message, false)
                .backgroundColorRes(R.color.white)
                .positiveText(callUsButtonText)
                .negativeText(cancelButtonText)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                       /* prefsEditor = pref.edit();
                        prefsEditor.putBoolean("EXPIRE_DIALOG", true);
                        prefsEditor.putBoolean("IGNORE_CLICKED", true);
                        prefsEditor.apply();*/
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        openStore();
                        /*prefsEditor = pref.edit();
                        prefsEditor.putBoolean("EXPIRE_DIALOG", true);
                        prefsEditor.apply();*/
                    }
                })
                .show();

        View view = mExpireDailog.getCustomView();

        roboto_md_60_212121 title = (roboto_md_60_212121) view.findViewById(R.id.textView1);
        title.setText(dialogTitle);

        ImageView expireImage = (ImageView) view.findViewById(R.id.img_warning);
        expireImage.setBackgroundColor(dialogImageBgColor);
        expireImage.setImageDrawable(ContextCompat.getDrawable(this, dialogImage));

        roboto_lt_24_212121 message = (roboto_lt_24_212121) view.findViewById(R.id.pop_up_create_message_body);
        message.setText(Methods.fromHtml(dialogMessage));
    }
   /* private void checkExpire(){
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

    private void showFacebookReviewDialog(){
        Calendar calendar = Calendar.getInstance();
        final Long current = calendar.getTimeInMillis();
        String paymentState = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE);
        long prev = pref.getLong(Key_Preferences.SHOW_FACEBOOK_REVIEW,-1);
        if(!paymentState.equals("1") || (current-prev)/(1000*60*60*24)<=7){
            return;
        }
        new MaterialDialog.Builder(this)
                .title("Review")
                .content("How are you liking our product? If you think, we have added value to your business, please rate us!")
                .negativeText("Later")
                .negativeColorRes(R.color.primary_color)
                .positiveColorRes(R.color.primary_color)
                .positiveText("Review")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        pref.edit().putLong(Key_Preferences.SHOW_FACEBOOK_REVIEW,current).apply();
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        MixPanelController.track(MixPanelController.FACEBOOK_REVIEW,null);
                        Methods.likeUsFacebook(HomeActivity.this,"");
                        pref.edit().putLong(Key_Preferences.SHOW_FACEBOOK_REVIEW,Long.MAX_VALUE).apply();
                    }
                }).show();

    }
    private void getAccountDetails(){
        AccountInfoActivity.AccInfoInterface infoInterface = Constants.restAdapter.create(AccountInfoActivity.AccInfoInterface.class);
        HashMap<String,String> values = new HashMap<>();
        values.put("clientId", Constants.clientId);
        values.put("fpId",session.getFPID());
        infoInterface.getAccDetails(values,new Callback<ArrayList<AccountDetailModel>>() {
            @Override
            public void success(ArrayList<AccountDetailModel> accountDetailModels, Response response) {
                int count =0;
                if(accountDetailModels == null){
                    return;
                }
                Collections.sort(accountDetailModels, new Comparator<AccountDetailModel>() {
                    @Override
                    public int compare(AccountDetailModel o1, AccountDetailModel o2) {
                        return o2.ToBeActivatedOn.compareToIgnoreCase(o1.ToBeActivatedOn);
                    }
                });
                Calendar calendar = Calendar.getInstance();
                Long current = calendar.getTimeInMillis();
                boolean showWildFire = true,flag = true;
                long prevShown = pref.getLong("expire_dialog",-1);
                for (AccountDetailModel model : accountDetailModels){
                    if(model.purchasedPackageDetails.packType == 0) {
                        count++;

                        if(flag) {
                            flag = false;
                            String Sdate = model.ToBeActivatedOn;
                            if (Sdate.contains("/Date")) {
                                Sdate = Sdate.replace("/Date(", "").replace(")/", "");
                            }
                            long date = Long.valueOf(Sdate);
                            calendar.setTimeInMillis(date);
                            calendar.add(Calendar.MONTH, Integer.parseInt(model.totalMonthsValidity));

                            float days = ((calendar.getTimeInMillis()-current) / (float)(1000 * 60 * 60 * 24));

                            if(days<=0){
                                showDialog1(LIGHT_HOUSE_EXPIRE, -1);
                            } else if (days <= 7) {
                                //seven days dialog ervery day
                                showDialog1(LIGHT_HOUSE_DAYS_LEFT, days);
                                showWildFire = false;
                            } else if (days < 30) {
                                //once a week
                                if ((current - prevShown) / (60 * 60 * 24 * 1000) >= 7) {
                                    showDialog1(LIGHT_HOUSE_DAYS_LEFT, days);
                                    showWildFire = false;
                                }
                            }
                        }
                    }
                }

               *//* if(showWildFire && (current-prevShown)/(60*60*24*1000)>=7) {
                    showDialog1(WILD_FIRE_EXPIRE,-1);
                }*//*

                if (count > 1) {
                   // showFacebookReviewDialog();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }*/
/*
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
        prefsEditor.apply();
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
                            prefsEditor.apply();
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
    }*/

    /*private void enableKitsune() {
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
    }*/

    @Override
    protected void onStart() {
        super.onStart();
        bus.register(this);
        BoostLog.d("HomeActivity", "onStart");
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
        if (CardAdapter_V3.pd != null)
            CardAdapter_V3.pd.dismiss();
        BoostLog.d("", "");

        Fragment sociaSharingFragment = getSupportFragmentManager().findFragmentByTag("socialSharingFragment");
        if (sociaSharingFragment != null) {
            ((SocialSharingFragment) sociaSharingFragment).onSocialSharingResult(requestCode, resultCode, data);
        }
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
        bitmapOptions.inJustDecodeBounds = false;
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
        mDrawerLayout.closeDrawer(Gravity.START);
        shareButton.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (nextScreen.equals(getString(R.string.business_profile))) {
                    //Intent businessProfileIntent = new Intent(HomeActivity.this, BusinessProfile_HomeActivity.class);
                    //startActivity(businessProfileIntent)
                    // ;
                    //slidingTabLayout.setVisibility(View.GONE);
                    shareButton.setVisibility(View.VISIBLE);
                    plusAddButton.setVisibility(View.GONE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, businessFragment, "Profile")
                            .addToBackStack(null)
                            .commit();
                    // getSupportFragmentManager().beginTransaction().
                    //        replace(R.id.mainFrame, businessFragment).commit();

//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.mainFrame, businessFragment)
//                            // Add this transaction to the back stack
//                    .addToBackStack("Profile")
//                    .commit();
                } else if (nextScreen.equals(getString(R.string.manage_customers))) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, manageCustomerFragment, "ManageCustomers")
                            .addToBackStack(null)
                            .commit();
                } else if (nextScreen.equals(getResources().getString(R.string.my_business_apps))) {
                    startBusinessApp();
                } else if (nextScreen.equals(getResources().getString(R.string.side_panel_site_appearance))) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, mSiteAppearanceFragement).
                            commit();
                } else if (nextScreen.equals(getString(R.string.image_gallery))) {
                    // Intent imageGalleryIntent = new Intent(HomeActivity.this, Image_Gallery_MainActivity.class);
                    // startActivity(imageGalleryIntent);
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, imageGalleryFragment).
                            commit();
                } else if (nextScreen.equals(getString(R.string.product_gallery))) {

                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, productGalleryFragment).commit();

                } else if (nextScreen.equals(getString(R.string.site__meter))) {
                    // Intent imageGalleryIntent = new Intent(HomeActivity.this, Image_Gallery_MainActivity.class);
                    // startActivity(imageGalleryIntent);
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, siteMeterFragment).addToBackStack(null).commit();
                } else if (nextScreen.equals(getString(R.string.home))) {
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
                    if (myFragment != null && myFragment.isVisible() && session.getBubbleTime() == -1) {
                        callMethod = true;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, homeFragment, "homeFragment").commit();
                    if (callMethod && Constants.PACKAGE_NAME.equals("com.biz2.nowfloats")) {

                        homeFragment.checkOverlay(Home_Fragment_Tab.DrawOverLay.FromHome);
                    }
                    //   getSupportFragmentManager().beginTransaction().
                    //           replace(R.id.mainFrame, homeFragment).addToBackStack("Home").commit();
                } else if (nextScreen.equals(getString(R.string.chat))) {
                    //getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame,hotlineFragment, "chatfragment").commit();
                    //Konotor.getInstance(getApplicationContext()).launchFeedbackScreen(HomeActivity.this);
                    Hotline.showConversations(HomeActivity.this);
                    //Konotor.getInstance(getApplicationContext()).launchFeedbackScreen(HomeActivity.this);
                } else if (nextScreen.equals(getString(R.string.call))) {
                    String paymentState = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE);
                    if (!Constants.PACKAGE_NAME.equals("com.biz2.nowfloats") || paymentState == null || !paymentState.equals("1")) {
                        Intent call = new Intent(Intent.ACTION_DIAL);
                        String callString = "tel:" + getString(R.string.contact_us_number);
                        call.setData(Uri.parse(callString));
                        startActivity(call);
                    } else {
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, helpAndSupportFragment).commit();
                    }
                } else if (nextScreen.equals(getString(R.string.share))) {
                    shareWebsite();
                } else if (nextScreen.equals("Settings")) {
                    //ft.replace(R.id.homeTabViewpager, settingsFragment);
                    //ft.commit();
                    plusAddButton.setVisibility(View.GONE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, settingsFragment).commit();
                } else if (nextScreen.equalsIgnoreCase("Store")) {
                    shareButton.setVisibility(View.GONE);
                    plusAddButton.setVisibility(View.GONE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, storeFragment).commit();
                } else if (nextScreen.equals("csp")) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, customPageActivity).commit();
//            Intent in = new Intent(HomeActivity.this, CustomPageFragment.class);
//            startActivity(in);
                } else if (nextScreen.equals(getString(R.string.contact__info))) {
                    Intent contactIntent = new Intent(HomeActivity.this, Contact_Info_Activity.class);
                    startActivity(contactIntent);
                } else if (nextScreen.equals(getString(R.string.basic_info))) {
                    Intent basicInfoIntent = new Intent(HomeActivity.this, Edit_Profile_Activity.class);
                    startActivity(basicInfoIntent);
                } else if (nextScreen.equals(getString(R.string.business__address))) {
                    Intent businessAddressIntent = new Intent(HomeActivity.this, Business_Address_Activity.class);
                    startActivity(businessAddressIntent);
                } else if (nextScreen.equals(getString(R.string.title_activity_social__sharing_))) {
                    MixPanelController.track(EventKeysWL.SOCIAL_SHARING, null);
                    /*Intent socialSharingIntent = new Intent(HomeActivity.this, Social_Sharing_Activity.class);
                    startActivity(socialSharingIntent);*/
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, socialSharingFragment, "socialSharingFragment").commit();


                } else if (nextScreen.equals(getString(R.string.manage_inventory))) {
                    MixPanelController.track(EventKeysWL.MANAGE_INVENTORY, null);
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, manageInventoryFragment, "ManageCustomers")
                            .addToBackStack(null)
                            .commit();
//                    Intent socialSharingIntent = new Intent(HomeActivity.this, ManageInventoryActivity.class);
//                    startActivity(socialSharingIntent);
                }
            }
        }, 200);

    }

    private void startBusinessApp() {
        int businessAppStatus = pref.getInt(Key_Preferences.ABOUT_BUSINESS_APP, BIZ_APP_DEMO);
        if (businessAppStatus == BIZ_APP_DEMO) {
            getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, new BusinessAppsFragment()).commit();
        } else {
            if (businessAppStatus == BIZ_APP_PAID) {
                pref.edit().putInt(Key_Preferences.ABOUT_BUSINESS_APP, BIZ_APP_DEMO_REMOVE).apply();
            }
            Intent i = new Intent(this, BusinessAppsActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    public void shareWebsite() {
        String url = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI);
        if (!Util.isNullOrEmpty(url)) {
            String eol = System.getProperty("line.separator");
            url = getString(R.string.visit_to_new_website)
                    + eol + url.toLowerCase();
        } else {
            String eol = System.getProperty("line.separator");
            url = getString(R.string.visit_to_new_website)
                    + eol + session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG).toLowerCase()
                    + getResources().getString(R.string.tag_for_partners);
        }
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, url);
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
            return newer_version_available(getPackageManager().getPackageInfo(getPackageName(), 0).versionName, new_version);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean newer_version_available(String local_version_string, String online_version_string) {
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
        if (chk) {
            drawerFragment.mDrawerToggle.setDrawerIndicatorEnabled(false);
            drawerFragment.mDrawerToggle.setHomeAsUpIndicator(R.drawable.icon_action_back);
        }
        if (!chk) {
            drawerFragment.mDrawerToggle.setDrawerIndicatorEnabled(!chk);
        }
//        if (!chk){
//            drawerFragment.mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic);
//        }
    }

    @Override
    public void onRenewPlanSelected() {
        prefsEditor = pref.edit();
        prefsEditor.putBoolean("EXPIRE_DIALOG", false);
        prefsEditor.apply();
        //checkExpire();
    }

    private void openStore() {
        if (mExpireDailog != null && !mExpireDailog.isCancelled()) {
            mExpireDailog.dismiss();
        }
        try {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putBoolean(StoreFragmentTab.IS_FROM_WILD_FIRE_MINI, false);
            storeFragment.setArguments(bundle);
            ft.replace(R.id.mainFrame, storeFragment)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Subscribe
    public void getStoreList(StoreEvent response) {
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
        for (int i = 0; i < allModels.size(); i++) {
            if (mExpireDailog != null && mExpireDailog.isShowing()) {
                break;
            }
            String temp = allModels.get(i).Name;
            if (temp != null && !temp.isEmpty() && (temp.contains("NowFloats WildFire") || temp.contains("NF WildFire"))) {

                String date = allModels.get(i).CreatedOn;
                float totalMonthsValidity = allModels.get(i).TotalMonthsValidity;
                int remainingDay = verifyTime(date.substring(date.indexOf("(") + 1, date.indexOf(")")), totalMonthsValidity);
                if (remainingDay > 0 && remainingDay < 7) {
                    prefsEditor = pref.edit();
                    prefsEditor.putInt("Days_remain", remainingDay);
                    prefsEditor.apply();
                } else if (remainingDay < 0) {
                    prefsEditor = pref.edit();
                    prefsEditor.putInt("Days_remain", -1);
                    prefsEditor.apply();
                } else {
                    return;
                }
                //renewPlanDialog(WILD_FIRE_EXPIRE);
                //showWildFire();
                return;
            }
        }
        //Log.v("ggg","not showing");
        //showWildFire();
    }

    private int verifyTime(String unixtime, float months) {
        Long createdunixtime = Long.parseLong(unixtime);
        Calendar cal = Calendar.getInstance();
        Date currdate = cal.getTime();
        cal.setTimeInMillis(createdunixtime);
        cal.add(Calendar.MONTH, (int) months);
        cal.add(Calendar.DATE, (int) ((months - (int) months) * 30));
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
        prefsEditor.putBoolean("EXPIRE_DIALOG", true);
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
                if (!ignoreclicked) {
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
                } else {
                    dialogShowFlag = false;
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

        if (dialogShowFlag) {
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

    private void showWildFire() {

        Calendar calendar = Calendar.getInstance();
        long oldTime = pref.getLong("wildFireMinitime", -1);
        long newTime = calendar.getTimeInMillis();
        long diff = 7 * 24 * 60 * 60 * 1000;
        //Log.v("ggg",String.valueOf(diff)+" "+String.valueOf(newTime-oldTime));
        if (oldTime != -1 && ((newTime - oldTime) < diff)) {
            return;
        }
        prefsEditor = pref.edit();
        prefsEditor.putLong("wildFireMinitime", newTime).apply();
        String paymentState = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTSTATE);
        String paymentLevel = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_PAYMENTLEVEL);
        if (Integer.valueOf(paymentState) > 0 && Integer.valueOf(paymentLevel) >= 10) {
            View view = getLayoutInflater().inflate(R.layout.pop_up_restrict_post_message, null);
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
                    .customView(view, true)
                    .backgroundColorRes(R.color.white)
                    .positiveText("BUY")
                    .negativeText("LATER")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog mExpireDailog) {
                            super.onPositive(mExpireDailog);
                            openStore();
                            MixPanelController.track(Key_Preferences.EVENT_WILDFIRE_BUY, null);
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


    private boolean isCalled = false;

    private void navigateView() {

        isCalled = true;

        Bundle bundle = getIntent().getExtras();

        if (bundle != null && bundle.containsKey("Username")) {

            progressDialog = ProgressDialog.show(HomeActivity.this, "", getString(R.string.loading));
            progressDialog.setCancelable(false);

            API_Login apiLogin = new API_Login(HomeActivity.this, session, bus);
            apiLogin.authenticate(bundle.getString("Username"), bundle.getString("Password"), Specific.clientId2);

        }
    }

    @Override
    public void authenticationStatus(String value) {
        if (value.equals("Success")) {

            Date date = new Date(System.currentTimeMillis());
            String dateString = date.toString();

            MixPanelController.setProperties("LastLoginDate", dateString);
            MixPanelController.setProperties("LoggedIn", "True");

            getFPDetails(HomeActivity.this, session.getFPID(), Constants.clientId, bus);
            HomeActivity.registerChat(session.getFPID());
        } else {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
    }


    @Override
    public void authenticationFailure(String value) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }

        finish();
    }


    private void getFPDetails(Activity activity, String fpId, String clientId, Bus bus) {
        new Get_FP_Details_Service(activity, fpId, clientId, bus);
    }

    @Subscribe
    public void post_getFPDetails(Get_FP_Details_Event response) {
        Bundle bundle = getIntent().getExtras();

        if (bundle != null && bundle.containsKey("Username")) {
            GetVisitorsAndSubscribersCountAsyncTask visit_subcribersCountAsyncTask = new GetVisitorsAndSubscribersCountAsyncTask(HomeActivity.this, session);
            visit_subcribersCountAsyncTask.execute();
        }

        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }

        createView();

    }

    @Subscribe
    public void getResponse(Response response) {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        createView();
    }

    @Subscribe
    public void getError(RetrofitError retrofitError) {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
        createView();
    }

    private void createView() {

        setContentView(R.layout.activity_home_v3);
        BoostLog.d(TAG, "In on CreateView");
        deepLinkUrl = RiaFirebaseMessagingService.deepLinkUrl;
        FPID = session.getFPID();
        MixPanelController.sendMixPanelProperties(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME),
                session.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL),
                session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG),
                session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CREATED_ON));

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        homeFragment = new Home_Fragment_Tab();
        businessFragment = new Business_Profile_Fragment_V2();
        manageCustomerFragment = new ManageCustomerFragment();
        manageInventoryFragment = new ManageInventoryFragment();
        settingsFragment = new Settings_Fragment();
        businessEnquiriesFragment = new Business_Enquiries_Fragment();
        imageGalleryFragment = new Image_Gallery_Fragment();
        mSiteAppearanceFragement = new SiteAppearanceFragment();
        productGalleryFragment = new Product_Gallery_Fragment();
        chatFragment = new ChatFragment();
        storeFragment = new StoreFragmentTab();
        socialSharingFragment = new SocialSharingFragment();
        siteMeterFragment = new Site_Meter_Fragment();
        customPageActivity = new CustomPageFragment();
        helpAndSupportFragment = new HelpAndSupportFragment();

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

        robotoMedium = Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf");
        robotoLight = Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf");

        Intent loginIntent = getIntent();
        boolean displayOnBoardingScreens = loginIntent.getBooleanExtra("fromLogin", false);
        Constants.fromLogin = displayOnBoardingScreens;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final boolean chk = get_VersionUpdate();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (chk) appUpdateAlertDilog(HomeActivity.this);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        if (Constants.fromLogin) {
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
                try {
                    Fragment myFragment = (Fragment) getSupportFragmentManager().findFragmentByTag("homeFragment");
                    if (myFragment != null && myFragment.isVisible()) {
                        headerText.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
                        headerText.setSelected(true);
                        headerText.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                        headerText.setSingleLine(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        /*This button is used in the image gallery*/
        plusAddButton = (ImageView) toolbar.findViewById(R.id.image_gallery_add_image_button);
        shareButton = (ImageView) toolbar.findViewById(R.id.business_profile_share_button);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        drawerFragment = (SidePanelFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
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
        deepLink(deepLinkUrl);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BoostLog.d("cek", "home selected");
                if (drawerFragment.mDrawerToggle.isDrawerIndicatorEnabled()) {
                    ((DrawerLayout) findViewById(R.id.drawer_layout)).openDrawer(Gravity.START);
                } else {
                    try {
                        drawerFragment.mDrawerToggle.setDrawerIndicatorEnabled(true);
                        headerText.setText(getString(R.string.custom_pages));
                        shareButton.setVisibility(View.GONE);
                        CustomPageFragment.customPageDeleteCheck = false;
                        CustomPageAdapter.deleteCheck = false;
                        CustomPageFragment.posList = new ArrayList<String>();
                        if (CustomPageFragment.custompageAdapter != null) {
                            CustomPageFragment.custompageAdapter.updateSelection(0);
                            CustomPageFragment.custompageAdapter.notifyDataSetChanged();
                        }
                        if (CustomPageFragment.recyclerView != null)
                            CustomPageFragment.recyclerView.invalidate();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        if (pref.getBoolean(Key_Preferences.HAS_SUGGESTIONS, false)) {
            checkCustomerAssistantService();
        } else {
            getCustomerAssistantSuggestions();
        }


        checkExpiry1();

        Intent intent = getIntent();
        if (intent != null && intent.getData() != null)

        {
            String action = intent.getAction();
            String data = intent.getDataString();
            BoostLog.d("Data: ", data.toString() + "  " + action);
            if (session.checkLogin()) {
                deepLink(data.substring(data.lastIndexOf("/") + 1));
            } else {
                finish();
            }
        }
        getNfxTokenData();
    }
    @Subscribe
    public void nfxCallback(NfxGetTokensResponse response){
        if (BuildConfig.APPLICATION_ID.equals("com.biz2.nowfloats")) {
            SharedPreferences smsPref = getSharedPreferences(com.nfx.leadmessages.Constants.SHARED_PREF, Context.MODE_PRIVATE);
            smsPref.edit().putString(com.nfx.leadmessages.Constants.FP_ID, FPID).apply();
            getPermissions();
        }

    }
    private void getNfxTokenData(){
        Get_FP_Details_Service.newNfxTokenDetails(this,session.getFPID(),bus);
        Get_FP_Details_Service.autoPull(this,session.getFPID());
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean canDrawOverlaysUsingReflection(Context context) {

        try {

            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            Class clazz = AppOpsManager.class;
            Method dispatchMethod = clazz.getMethod("checkOp", new Class[]{int.class, int.class, String.class});
//AppOpsManager.OP_SYSTEM_ALERT_WINDOW = 24
            int mode = (Integer) dispatchMethod.invoke(manager, new Object[]{24, Binder.getCallingUid(), context.getApplicationContext().getPackageName()});

            return AppOpsManager.MODE_ALLOWED == mode;

        } catch (Exception e) {
            return false;
        }

    }
}
