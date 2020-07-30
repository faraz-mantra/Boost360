package com.nowfloats.NavigationDrawer;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.anachat.chatsdk.AnaChatBuilder;
import com.anachat.chatsdk.AnaCore;
import com.anachat.chatsdk.internal.database.PreferencesManager;
import com.android.inputmethod.latin.utils.JniUtils;
import com.appservice.model.StatusKyc;
import com.boost.upgrades.UpgradeActivity;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.inventoryorder.constant.FragmentType;
import com.inventoryorder.constant.IntentConstant;
import com.inventoryorder.model.PreferenceData;
import com.nineoldandroids.animation.Animator;
import com.nowfloats.Analytics_Screen.Graph.AnalyticsActivity;
import com.nowfloats.Analytics_Screen.Graph.fragments.UniqueVisitorsFragment;
import com.nowfloats.Analytics_Screen.SearchQueriesActivity;
import com.nowfloats.Analytics_Screen.ShowVmnCallActivity;
import com.nowfloats.Analytics_Screen.SubscribersActivity;
import com.nowfloats.Analytics_Screen.VmnCallCardsActivity;
import com.nowfloats.Analytics_Screen.model.NfxGetTokensResponse;
import com.nowfloats.BusinessProfile.UI.UI.BusinessHoursActivity;
import com.nowfloats.BusinessProfile.UI.UI.Business_Address_Activity;
import com.nowfloats.BusinessProfile.UI.UI.Business_Logo_Activity;
import com.nowfloats.BusinessProfile.UI.UI.Business_Profile_Fragment_V2;
import com.nowfloats.BusinessProfile.UI.UI.ContactInformationActivity;
import com.nowfloats.BusinessProfile.UI.UI.Edit_Profile_Activity;
import com.nowfloats.BusinessProfile.UI.UI.SocialSharingFragment;
import com.nowfloats.Business_Enquiries.BusinessEnquiryActivity;
import com.nowfloats.CustomPage.CreateCustomPageActivity;
import com.nowfloats.CustomPage.CustomPageActivity;
import com.nowfloats.CustomPage.CustomPageAdapter;
import com.nowfloats.CustomPage.CustomPageDeleteInterface;
import com.nowfloats.CustomPage.CustomPageFragment;
import com.nowfloats.CustomWidget.roboto_lt_24_212121;
import com.nowfloats.CustomWidget.roboto_md_60_212121;
import com.nowfloats.FacebookLeads.FacebookLeadsFragment;
import com.nowfloats.Image_Gallery.ImageGalleryActivity;
import com.nowfloats.Login.API_Login;
import com.nowfloats.Login.Login_Interface;
import com.nowfloats.Login.Model.FloatsMessageModel;
import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.NavigationDrawer.API.App_Update_Async_Task;
import com.nowfloats.NavigationDrawer.API.DeepLinkInterface;
import com.nowfloats.NavigationDrawer.API.GetVisitorsAndSubscribersCountAsyncTask;
import com.nowfloats.NavigationDrawer.SiteMeter.Site_Meter_Fragment;
import com.nowfloats.NavigationDrawer.businessApps.BusinessAppsDetailsActivity;
import com.nowfloats.NavigationDrawer.businessApps.BusinessAppsFragment;
import com.nowfloats.NavigationDrawer.model.RiaNodeDataModel;
import com.nowfloats.ProductGallery.ProductGalleryActivity;
import com.nowfloats.ProductGallery.Product_Detail_Activity_V45;
import com.nowfloats.SiteAppearance.SiteAppearanceActivity;
import com.nowfloats.Store.AddOnFragment;
import com.nowfloats.Store.DomainLookup;
import com.nowfloats.Store.FlavourFivePlansActivity;
import com.nowfloats.Store.NewPricingPlansActivity;
import com.nowfloats.Store.UpgradesFragment;
import com.nowfloats.bubble.CustomerAssistantService;
import com.nowfloats.customerassistant.ThirdPartyQueriesActivity;
import com.nowfloats.enablekeyboard.KeyboardFragment;
import com.nowfloats.managecustomers.ManageCustomerFragment;
import com.nowfloats.manageinventory.ManageInboxFragment;
import com.nowfloats.manageinventory.ManageInventoryFragment;
import com.nowfloats.manageinventory.SellerAnalyticsActivity;
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
import com.nowfloats.util.WebEngageController;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.thinksity.BuildConfig;
import com.thinksity.R;
import com.webengage.sdk.android.WebEngage;
import com.zopim.android.sdk.api.ZopimChat;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import zendesk.core.AnonymousIdentity;
import zendesk.core.Identity;
import zendesk.core.Zendesk;
import zendesk.support.Support;

import static com.appservice.ui.paymentgateway.PaymentGatewayContainerActivityKt.startFragmentPaymentActivityNew;
import static com.inventoryorder.ui.FragmentContainerOrderActivityKt.startFragmentActivityNew;
import static com.nowfloats.NavigationDrawer.businessApps.BusinessAppsFragment.BIZ_APP_DEMO;
import static com.nowfloats.NavigationDrawer.businessApps.BusinessAppsFragment.BIZ_APP_DEMO_REMOVE;
import static com.nowfloats.NavigationDrawer.businessApps.BusinessAppsFragment.BIZ_APP_PAID;
import static com.nowfloats.manageinventory.ManageInventoryFragment.getExperienceType;

//import com.nfx.leadmessages.ReadMessages;


public class HomeActivity extends AppCompatActivity implements SidePanelFragment.OnItemClickListener
        , DeepLinkInterface, CustomPageDeleteInterface, Home_Main_Fragment.OnRenewPlanClickListener,
        CardAdapter_V3.Permission, OffersFragment.OnRenewPlanClickListener, Analytics_Fragment.RiaCardDeepLinkListener,
        API_Login.API_Login_Interface {


    private Toolbar toolbar;
    private SharedPreferences pref = null;
    private DrawerLayout mDrawerLayout;
    SidePanelFragment drawerFragment;
    Home_Fragment_Tab homeFragment;
    KeyboardFragment keyboardFragment;
    FacebookLeadsFragment facebookLeadsFragment;
    Business_Profile_Fragment_V2 businessFragment;
    ManageCustomerFragment manageCustomerFragment;
    ManageInventoryFragment manageInventoryFragment;
    ManageInboxFragment manageInboxFragment;
    UpgradesFragment upgradesFragment;
    AddOnFragment addOnFragment;
    AboutFragment aboutFragment;
    ManageContentFragment manageContentFragment;
    AccountSettingsFragment accountSettingsFragment;
    UniqueVisitorsFragment uniqueVisitorsFragment;
    Site_Meter_Fragment siteMeterFragment;
    SocialSharingFragment socialSharingFragment;
    HelpAndSupportFragment helpAndSupportFragment;
    UserSessionManager session;
    Typeface robotoMedium;
    Typeface robotoLight;
    MaterialDialog mExpireDialog;
    public static TextView headerText;
    public static ImageView plusAddButton;
    public static ImageView shareButton;
    private boolean isExpiredCheck = false;
    public static ArrayList<FloatsMessageModel> StorebizFloats = new ArrayList<FloatsMessageModel>();
    private boolean showLookupDomain = false;
    private int clickCnt = 0;
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
    private String mDeepLinkUrl, mPayload;
    private String TAG = HomeActivity.class.getSimpleName();
    /*private String[] permission = new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS
            , Manifest.permission.READ_PHONE_STATE};*/
    private final static int READ_MESSAGES_ID = 221;
    //private ArrayList<AccountDetailModel> accountDetailsModel = new ArrayList<>();

    private ProgressDialog progressDialog;

    private boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private static boolean newer_version_available(String local_version_string, String
            online_version_string) {
        DefaultArtifactVersion local_version_mvn = new DefaultArtifactVersion(local_version_string);
        DefaultArtifactVersion online_version_mvn = new DefaultArtifactVersion(online_version_string);
        return local_version_mvn.compareTo(online_version_mvn) == -1 && !local_version_string.equals("");
    }


    /*private void getPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(this, ReadMessages.class);
            startService(intent);
            // start the service to send data to firebase
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            requestPermissions(permission, READ_MESSAGES_ID);


        }
    }*/


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == READ_MESSAGES_ID) {

            /*List<Integer> intList = new ArrayList<Integer>();
            for (int i : grantResults) {
                intList.add(i);
            }
            if (!intList.contains(PackageManager.PERMISSION_DENIED)) {
                Intent intent = new Intent(this, ReadMessages.class);
                startService(intent);
            }*/
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JniUtils.loadNativeLibrary();
        pref = getSharedPreferences(Constants.PREF_NAME, Activity.MODE_PRIVATE);
        BoostLog.d("HomeActivity ONcreate", "onCreate");
        bus = BusProvider.getInstance().getBus();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
//        GCMIntentService.setHomeActivity(HomeActivity.this);
        Methods.isOnline(HomeActivity.this);

        session = new UserSessionManager(getApplicationContext(), HomeActivity.this);
        Log.d("WEBSITE_ID", "ID : " + session.getFPID());


        WebEngageController.initiateUserLogin(session.getUserProfileId());
        WebEngageController.setUserContactInfoProperties(session);
        WebEngageController.setFPTag(session.getFpTag());

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();
                WebEngage.get().setRegistrationID(token);
            }
        });

        WebEngageController.trackEvent("HOME", "pageview", null);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            if (bundle.containsKey("url")) {
                mDeepLinkUrl = bundle.getString("url");
            }
            if (bundle.containsKey("payload")) {
                mPayload = bundle.getString("payload");
            }
        }

        if (bundle != null && bundle.containsKey("Username")) {

        } else {
            createView();
        }

        initialiseZendeskSupportSdk();
        //WidgetKey.getWidgets(session, this);
    }

    public void DeepLinkPage(String url, boolean isFromRia) {
        BoostLog.d("Deep Link URL", "Deep Link URL : " + url);
        Constants.GCM_Msg = false;
        if (!Util.isNullOrEmpty(url)) {
            if (!isFromRia) {
                MixPanelController.track("$app_open", null);
            }
            if (url.contains(getString(R.string.keyboard))) {

                getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, keyboardFragment, "Keyboard")
                        .commit();
            } else if (url.contains(getString(R.string.facebook_chat))) {

            } else if (url.contains("thirdPartyQueries")) {
                Intent intent = new Intent(this, ThirdPartyQueriesActivity.class);
                startActivity(intent);
            } else if (url.contains(getString(R.string.facebook_chat_main))) {

            } else if (url.contains(getString(R.string.deeplink_manage_customer))) {
                getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, manageCustomerFragment, "ManageCustomers")
                        .commit();
            } else if (url.contains(getString(R.string.feedback_chat))) {
                getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, helpAndSupportFragment).commit();
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
            } else if (url.contains("keyboardSettings")) {
                getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, keyboardFragment, "Keyboard")
                        .commit();
                //navigationView.getMenu().getItem(1).setChecked(true);
            } else if (url.contains("addCustomPage")) {
                Intent createCustomPage = new Intent(HomeActivity.this, CreateCustomPageActivity.class);
                if (isFromRia && mRiaNodeDataModel != null) {
                    createCustomPage.putExtra(Constants.RIA_NODE_DATA, mRiaNodeDataModel);
                }
                startActivity(createCustomPage);
            } else if (url.contains("myorders")) {
                Intent listOrder = new Intent(HomeActivity.this, SellerAnalyticsActivity.class);
                startActivity(listOrder);
            } else if (url.contains("myorderdetail")) {
                Bundle bundle = new Bundle();
                PreferenceData data = new PreferenceData(Constants.clientId_ORDER, session.getUserProfileId(), Constants.WA_KEY, session.getFpTag());
                bundle.putSerializable(IntentConstant.PREFERENCE_DATA.name(), data);
                bundle.putString(IntentConstant.EXPERIENCE_CODE.name(), session.getFP_AppExperienceCode());
                bundle.putString(IntentConstant.ORDER_ID.name(), mPayload);
                int experienceType = getExperienceType(session.getFP_AppExperienceCode());

                if (experienceType == 1) startFragmentActivityNew(this, FragmentType.APPOINTMENT_DETAIL_VIEW, bundle, false);
                else if (experienceType == 3) startFragmentActivityNew(this, FragmentType.ORDER_DETAIL_VIEW, bundle, false);
//                Intent orderDetail = new Intent(HomeActivity.this, OrderDetailsActivity.class);
//                orderDetail.putExtra("orderId", mPayload);
//                startActivity(orderDetail);
            } else if (url.contains(getResources().getString(R.string.deeplink_upgrade))) {
                final String appPackageName = HomeActivity.this.getPackageName(); // getPackageName() from Context or Activity object
                try {
                    HomeActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    HomeActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            } else if (url.contains(getResources().getString(R.string.deeplink_analytics))) {
                headerText.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
                setTitle(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainFrame, homeFragment)
                        .commit();
                getSupportFragmentManager().executePendingTransactions();
                Constants.deepLinkAnalytics = true;
                homeFragment.setFragmentTab(1);
            } else if (url.contains(getResources().getString(R.string.deeplink_bizenquiry)) || url.contains("enquiries")) {
                Intent queries = new Intent(HomeActivity.this, BusinessEnquiryActivity.class);
                startActivity(queries);
            } else if (url.contains(getString(R.string.deep_link_call_tracker))) {
                Intent callLogs = new Intent(HomeActivity.this, ShowVmnCallActivity.class);
                startActivity(callLogs);
            } else if (url.contains("store") || url.contains(getResources().getString(R.string.deeplink_store)) ||
                    url.contains(getResources().getString(R.string.deeplink_propack)) ||
                    url.contains(getResources().getString(R.string.deeplink_nfstoreseo)) ||
                    url.contains(getResources().getString(R.string.deeplink_nfstorettb)) ||
                    url.contains(getResources().getString(R.string.deeplink_nfstorebiztiming)) ||
                    url.contains(getResources().getString(R.string.deeplink_nfstoreimage)) ||
                    url.contains(getResources().getString(R.string.deeplink_nfstoreimage))) {
                Intent i = new Intent(this, NewPricingPlansActivity.class);
                startActivity(i);
            } else if (url.contains(getResources().getString(R.string.deeplink_searchqueries))) {
                Intent queries = new Intent(HomeActivity.this, SearchQueriesActivity.class);
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
            } else if (url.contains("visits") || url.contains("viewgraph")) {
                Intent accountInfo = new Intent(HomeActivity.this, AnalyticsActivity.class);
                accountInfo.putExtra("table_name", Constants.VISITS_TABLE);
                startActivity(accountInfo);
            } else if (url.contains(getResources().getString(R.string.deeplink_business_app))) {
                startBusinessApp();
            } else if (url.contains(getResources().getString(R.string.deeplink_socailsharing))) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainFrame, socialSharingFragment, "socialSharingFragment").commit();
//                startActivity(new Intent(activity, Social_Sharing_Activity.class));
            } else if (url.contains("notification")) {
                homeFragment.setFragmentTab(2);
            } else if (url.contains(getResources().getString(R.string.deeplink_profile))) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainFrame, businessFragment, "Profile").commit();
            } else if (url.contains(getResources().getString(R.string.deeplink_contact))) {
                Intent queries = new Intent(HomeActivity.this, ContactInformationActivity.class);
                startActivity(queries);
            } else if (url.contains(getResources().getString(R.string.deeplink_bizaddress)) || url.contains("address")) {
                Intent queries = new Intent(HomeActivity.this, Business_Address_Activity.class);
                startActivity(queries);
            } else if (url.contains(getResources().getString(R.string.deeplink_bizhours)) || url.contains("hours")) {
                Intent queries = new Intent(HomeActivity.this, BusinessHoursActivity.class);
                startActivity(queries);
            } else if (url.contains(getResources().getString(R.string.deeplink_bizlogo)) || url.contains("logo")) {
                Intent queries = new Intent(HomeActivity.this, Business_Logo_Activity.class);
                startActivity(queries);
            } else if (url.contains(getResources().getString(R.string.deeplink_nfstoreDomainTTBCombo)) || url.contains("bookdomain")) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainFrame, businessFragment)
                        .commit();
            } else if (url.contains("sitemeter")) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainFrame, siteMeterFragment)
                        .commit();
            } else if (url.contains(getResources().getString(R.string.deeplink_imageGallery)) ||
                    url.contains("imagegallery") || url.contains("imagegallery")) {
                Intent i = new Intent(this, ImageGalleryActivity.class);
                startActivity(i);
            } else if (url.contains(getResources().getString(R.string.deeplink_ProductGallery))) {
                Intent i = new Intent(this, ProductGalleryActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            } else if (url.contains("assuredPurchase")) {
                getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, manageInventoryFragment, "ManageInventory")
                        .commit();
            } else if (url.contains(getResources().getString(R.string.deeplink_gplaces))) {//TODO
            } else if (url.contains("accSetting")) {
                MixPanelController.track(EventKeysWL.SIDE_PANEL_ACCOUNT_SETTINGS, null);
                getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, accountSettingsFragment, "accountSettingsFragment")
                        .commit();
            } else if (url.contains("uniqueVisitor")) {
                Intent accountInfo = new Intent(HomeActivity.this, AnalyticsActivity.class);
                accountInfo.putExtra("table_name", Constants.VISITORS_TABLE);
                startActivity(accountInfo);
            } else if (url.contains(getResources().getString(R.string.addon_marketplace))) {
                initiateAddonMarketplace();
            }

        }
        mDeepLinkUrl = null;
    }

    private String getCountryCode() {
        String[] string_array = getResources().getStringArray(R.array.CountryCodes);
        for (String country_phone : string_array) {
            String[] Codes = country_phone.split(",");
            if (Codes[0].equalsIgnoreCase(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRYPHONECODE))) {
                return Codes[1];
            }
        }
        return "";
    }

    private void setMixPanelProperties() {

// TODO Auto-generated method stub
        try {
            JSONObject store = new JSONObject();
//            if (!Util.isNullOrEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CONTACTNAME))){
            //store.put("name", Constants.ContactName);
//            }
            store.put("Business Name", session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
            store.put("Tag", session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG));
            store.put("Primary contact", session.getFPDetails(Key_Preferences.MAIN_PRIMARY_CONTACT_NUM));
            store.put("$phone", session.getFPDetails(Key_Preferences.MAIN_PRIMARY_CONTACT_NUM));
            store.put("$email", session.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL));
            store.put("$city", session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CITY));
            store.put("$country_code", getCountryCode());
            if (TextUtils.isEmpty(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI)) || session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI).equals("null")) {
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
        if (am.getRunningTasks(1) != null && am.getRunningTasks(1).size() > 0) {
            ComponentName componentName = am.getRunningTasks(1).get(0).topActivity;
            if (!componentName.getPackageName().equalsIgnoreCase(getApplicationContext().getPackageName())) {
                sendBroadcast(new Intent(CustomerAssistantService.ACTION_ADD_BUBBLE));
            } else {
                sendBroadcast(new Intent(CustomerAssistantService.ACTION_REMOVE_BUBBLE));
            }
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

//    @Override
//    public void onBackPressed() {
//        // super.onBackPressed();
////        Methods.isOnline(HomeActivity.this);
//        BoostLog.i("back---", "" + backChk);
//        if (backChk) {
//            finish();
//        }
//        if (!backChk) {
//            start_backclick();
//            backChk = true;
//            Methods.showSnackBar(HomeActivity.this, getString(R.string.click_again_to_exit));
//        }
//    }

    private void initialiseZendeskSupportSdk() {
        try {
            Zendesk.INSTANCE.init(HomeActivity.this,
                    "https://boost360.zendesk.com",
                    "684341b544a77a2a73f91bd3bb2bc77141d4fc427decda49",
                    "mobile_sdk_client_6c56562cfec5c64c7857");

//            Identity identity = new AnonymousIdentity();

            Identity identity = new AnonymousIdentity.Builder()
                    .withNameIdentifier(session.getFpTag())
                    .withEmailIdentifier(session.getFPEmail())
                    .build();

            Zendesk.INSTANCE.setIdentity(identity);

            Support.INSTANCE.init(Zendesk.INSTANCE);

            ZopimChat.init("MJwgUJn9SKy2m9ooxsQgJSeTSR5hU3A5");
        } catch (Exception e) {

        }
    }


//    private void start_backclick() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(4000);
//                    backChk = false;
//                    BoostLog.i("INSIDE---", "" + backChk);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//    }

    public void appUpdateAlertDialog(final Activity mContext) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(mContext)
                .title(getString(R.string.app_update_available))
                .content(getString(R.string.update_nowfloats_app))
                .positiveText(getString(R.string.update))
                .positiveColorRes(R.color.primaryColor)
                .cancelable(false)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        dialog.dismiss();
                        final String appPackageName = mContext.getPackageName(); // getPackageName() from Context or Activity object
                        try {

                            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
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

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.mainFrame);

        if (!(fragment instanceof Home_Fragment_Tab)) {
            loadFragment(homeFragment, "homeFragment");
            return;
        }

        //Log.d("homeFragment", "" + ((Home_Fragment_Tab)fragment).getCurrentItem());
        //homeFragment.setFragmentTab(0);

        if (((Home_Fragment_Tab) fragment).getCurrentItem() != 0) {
            homeFragment.setFragmentTab(0);
            return;
        }

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
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
        if (pref == null || prefsEditor == null) {
            pref = getSharedPreferences(Constants.PREF_NAME, Activity.MODE_PRIVATE);
            prefsEditor = pref.edit();
        }

        Boolean isPaymentSuccess = pref.getBoolean("Last_payment_status", false);
        Set<String> keys = pref.getStringSet("Last_Purchase_Order_Feature_Keys", null);
        if (keys != null && isPaymentSuccess) {
            ArrayList<String> keys2 = new ArrayList<>();
            keys2.addAll(keys);
            Toast.makeText(HomeActivity.this, "Refreshing your business dashboard with the digital add-ons you just purchased.", Toast.LENGTH_LONG).show();
            for (int i = 0; i < keys2.size(); i++) {
                if (!Constants.StoreWidgets.contains(keys2.get(i))) {
                    Constants.StoreWidgets.add(keys2.get(i));
                }
            }
        }
        prefsEditor.remove("Last_Purchase_Order_Feature_Keys");
        prefsEditor.remove("Last_payment_status");
        prefsEditor.apply();

        if (!isCalled) {
            navigateView();
        }
        //DeepLinkPage(mDeepLinkUrl, false);
        //mDeepLinkUrl = null;

//        if (pref.getBoolean(Key_Preferences.HAS_SUGGESTIONS, false)) {
        checkCustomerAssistantService();
//        }
    }

    //    private void getCustomerAssistantSuggestions() {
//        CustomerAssistantApi suggestionsApi = new CustomerAssistantApi(bus);
//        if (Utils.isNetworkConnected(this)) {
//            String appVersion = "";
//
//            try {
//                appVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            HashMap<String, String> offersParam = new HashMap<>();
//            offersParam.put("fpId", session.getFPID());
//            suggestionsApi.getMessages(offersParam, session.getFPID(), appVersion);
//        }
//    }
//
//    @Subscribe
//    public void processSmsData(SMSSuggestions smsSuggestions) {
//
//        if (smsSuggestions != null && smsSuggestions.getSuggestionList() != null
//                && smsSuggestions.getSuggestionList().size() > 0) {
//            pref.edit().putBoolean(Key_Preferences.HAS_SUGGESTIONS, true).apply();
//            checkCustomerAssistantService();
//        } else {
//            stopService(new Intent(this, CustomerAssistantService.class));
//        }
//    }
//
    private void checkCustomerAssistantService() {
//        pref.edit().putBoolean(Key_Preferences.HAS_SUGGESTIONS, true).commit();
        if (pref.getBoolean(Key_Preferences.HAS_SUGGESTIONS, false)) {

            if (Methods.hasOverlayPerm(HomeActivity.this)) {

                if (!Methods.isMyServiceRunning(this, CustomerAssistantService.class)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(new Intent(this, CustomerAssistantService.class));
                    } else {
                        Intent bubbleIntent = new Intent(this, CustomerAssistantService.class);
                        startService(bubbleIntent);
                    }
                }
            }
        }
        sendBroadcast(new Intent(CustomerAssistantService.ACTION_REMOVE_BUBBLE));
    }

    private void showOnBoardingScreens() {
        MixPanelController.track(EventKeysWL.WELCOME_SCREEN_1, null);
        final Dialog dialog = new Dialog(HomeActivity.this, android.R.style.Theme_Translucent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.onboarding_popup_one);
        dialog.show();

        final CardView cardView = dialog.findViewById(R.id.card_view);
        final ImageView imageView = dialog.findViewById(R.id.welcome_popup_icon);
        final TextView showNextButton = dialog.findViewById(R.id.loginButton);
        final TextView titleTextView = dialog.findViewById(R.id.onboarding_ScreenOne_Welcome);
        final TextView descTextView = dialog.findViewById(R.id.onboarding_ScreenOne_desc_TextView);

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

    @Override
    protected void onStart() {
        super.onStart();
        bus.register(this);
        BoostLog.d("HomeActivity", "onStart");
        isExpiredCheck = true;
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

        roboto_md_60_212121 title = view.findViewById(R.id.textView1);
        title.setText(dialogTitle);

        ImageView expireImage = view.findViewById(R.id.img_warning);
        expireImage.setBackgroundColor(dialogImageBgColor);
        expireImage.setImageDrawable(ContextCompat.getDrawable(this, dialogImage));

        roboto_lt_24_212121 message = view.findViewById(R.id.pop_up_create_message_body);
        message.setText(Methods.fromHtml(dialogMessage));
    }

    @Override
    public void onClick(final String nextScreen) {
        Methods.isOnline(HomeActivity.this);
        mDrawerLayout.closeDrawer(GravityCompat.START);
        shareButton.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (nextScreen.equals(getString(R.string.payment_gateway))) {
                    Bundle bundle = new Bundle();
                    bundle.putString(com.appservice.constant.IntentConstant.CLIENT_ID.name(), Constants.clientId);
                    bundle.putString(com.appservice.constant.IntentConstant.USER_PROFILE_ID.name(), session.getUserProfileId());
                    bundle.putString(com.appservice.constant.IntentConstant.FP_ID.name(), session.getFPID());
                    bundle.putString(com.appservice.constant.IntentConstant.FP_TAG.name(), session.getFpTag());
                    bundle.putString(com.appservice.constant.IntentConstant.EXPERIENCE_CODE.name(), session.getFP_AppExperienceCode());
                    bundle.putString(com.appservice.constant.IntentConstant.FP_LOGO.name(), session.getFPLogo());
                    bundle.putString(com.appservice.constant.IntentConstant.FP_USER_EMAIL.name(), session.getFPEmail());
                    bundle.putString(com.appservice.constant.IntentConstant.FP_USER_NUMBER.name(), session.getFPPrimaryContactNumber());
                    if (Constants.StoreWidgets.contains(StatusKyc.CUSTOM_PAYMENTGATEWAY.name())) {
                        bundle.putBoolean(com.appservice.constant.IntentConstant.CUSTOM_PAYMENT_GATEWAY.name(), true);
                    } else {
                        bundle.putBoolean(com.appservice.constant.IntentConstant.CUSTOM_PAYMENT_GATEWAY.name(), false);
                    }
                    startFragmentPaymentActivityNew(HomeActivity.this, com.appservice.constant.FragmentType.PAYMENT_GATEWAY, bundle, false);
                } else if (nextScreen.equals(getString(R.string.keyboard))) {
                    WebEngageController.trackEvent("NAV - BIZ_KEYBOARD", "BIZ_KEYBOARD", session.getFpTag());
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, keyboardFragment, "Keyboard")
                            .commit();
                } else if (nextScreen.equals(getString(R.string.facebook_leads))) {
                    WebEngageController.trackEvent("NAV - ONLINE_ADVERTISING", "ONLINE_ADVERTISING", session.getFpTag());
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, facebookLeadsFragment, "FacebookLeadAds")
                            .commit();
                } else if (nextScreen.equals(getString(R.string.business_profile))) {
                    shareButton.setVisibility(View.VISIBLE);
                    plusAddButton.setVisibility(View.GONE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, businessFragment, "Profile")
                            .commit();
                } else if (nextScreen.equals(getString(R.string.manage_customers))) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, manageCustomerFragment, "ManageCustomers")
                            .commit();
                } else if (nextScreen.equals("wildfire")) {
                    FragmentManager manager = getSupportFragmentManager();
                    Fragment frag = manager.findFragmentByTag("wildfireFrag");
                    if (frag == null) {
                        frag = new WildFireFragment();
                    }
                    manager.beginTransaction().replace(R.id.mainFrame, frag, "wildfireFrag")
                            .commit();
                } else if (nextScreen.equals("dictate")) {
                    FragmentManager manager = getSupportFragmentManager();
                    Fragment frag = manager.findFragmentByTag("DictateFrag");
                    if (frag == null) {
                        frag = new DictateFragment();
                    }
                    manager.beginTransaction().replace(R.id.mainFrame, frag, "DictateFrag")
                            .commit();
                } else if (nextScreen.equals(getResources().getString(R.string.my_business_apps))) {
                    startBusinessApp();
                } else if (nextScreen.equals(getResources().getString(R.string.side_panel_site_appearance))) {
                    Intent i = new Intent(HomeActivity.this, SiteAppearanceActivity.class);
                    startActivity(i);
                } else if (nextScreen.equals(getString(R.string.image_gallery))) {
                    // Intent imageGalleryIntent = new Intent(HomeActivity.this, Image_Gallery_MainActivity.class);
                    // startActivity(imageGalleryIntent);
                    Intent i = new Intent(HomeActivity.this, ImageGalleryActivity.class);
                    startActivity(i);
                } else if (nextScreen.equals(getString(R.string.product_gallery))) {

                    Intent i = new Intent(HomeActivity.this, ProductGalleryActivity.class);
                    startActivity(i);

                } else if (nextScreen.equals(getString(R.string.site__meter))) {
                    WebEngageController.trackEvent("NAV - SITE_HEALTH", "SITE_HEALTH", null);
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, siteMeterFragment).commit();
                } else if (nextScreen.equals(getString(R.string.deeplink_analytics))) {
                    DeepLinkPage(getString(R.string.deeplink_analytics), false);
                } else if (nextScreen.equals(getString(R.string.home)) || nextScreen.equals(getString(R.string.update))) {

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

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.mainFrame, homeFragment, "homeFragment")
                            .commit();
                    getSupportFragmentManager().executePendingTransactions();
                    homeFragment.setFragmentTab(0);
                    /*if (callMethod && Constants.PACKAGE_NAME.equals("com.biz2.nowfloats")) {

                        homeFragment.checkOverlay(Home_Fragment_Tab.DrawOverLay.FromHome);
                    }*/
                    //   getSupportFragmentManager().beginTransaction().
                    //           replace(R.id.mainFrame, homeFragment).addToBackStack("Home").commit();
                } else if (nextScreen.equals(getString(R.string.chat))) {
                    if (Constants.PACKAGE_NAME.equals("com.biz2.nowfloats") || BuildConfig.APPLICATION_ID.equals("com.redtim")) {
                        MixPanelController.track(MixPanelController.HELP_AND_SUPPORT_CHAT, null);
                        WebEngageController.trackEvent("CONTACT NF", "null", session.getFpTag());

                        PreferencesManager.getsInstance(HomeActivity.this).setUserName(session.getFpTag());

                        new AnaChatBuilder(HomeActivity.this)
                                .setBusinessId(Constants.ANA_BUSINESS_ID)
                                .setBaseUrl(Constants.ANA_CHAT_API_URL)
                                .setFlowId(Constants.ANA_BUSINESS_ID)
                                .setThemeColor(R.color.primary)
                                .setToolBarDescription("Available")
                                .setToolBarTittle(getString(R.string.support_name) + " Chat")
                                .setToolBarLogo(R.drawable.ria)
                                .start();
                    }

                } else if (nextScreen.equals(getString(R.string.call)) || nextScreen.equals(getString(R.string.help_and_support))) {

                    WebEngageController.trackEvent("NAV - SUPPORT", "SUPPORT", null);
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, helpAndSupportFragment).commit();

                } else if (nextScreen.equals(getString(R.string.share))) {
                    shareWebsite();
                } else if (nextScreen.equalsIgnoreCase("Store")) {
                    shareButton.setVisibility(View.GONE);
                    plusAddButton.setVisibility(View.GONE);
                    Intent i = new Intent(HomeActivity.this, NewPricingPlansActivity.class);
                    startActivity(i);

                } else if (nextScreen.equals("csp")) {
                    Intent i = new Intent(HomeActivity.this, CustomPageActivity.class);
                    startActivity(i);
//            Intent in = new Intent(HomeActivity.this, CustomPageFragment.class);
//            startActivity(in);
                } else if (nextScreen.equals(getString(R.string.contact__info))) {
                    Intent contactIntent = new Intent(HomeActivity.this, ContactInformationActivity.class);
                    startActivity(contactIntent);
                } else if (nextScreen.equals(getString(R.string.basic_info))) {
                    Intent basicInfoIntent = new Intent(HomeActivity.this, Edit_Profile_Activity.class);
                    startActivity(basicInfoIntent);
                } else if (nextScreen.equals(getString(R.string.business__address))) {
                    Intent businessAddressIntent = new Intent(HomeActivity.this, Business_Address_Activity.class);
                    startActivity(businessAddressIntent);
                } else if (nextScreen.equals(getString(R.string.title_activity_social__sharing_)) || nextScreen.equals(getString(R.string.content_sharing_settings))) {
                    WebEngageController.trackEvent("NAV - CONTENT_SHARING_SETTINGS", "CONTENT_SHARING_SETTINGS", null);
                    /*Intent socialSharingIntent = new Intent(HomeActivity.this, Social_Sharing_Activity.class);
                    startActivity(socialSharingIntent);*/
//                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, socialSharingFragment, "socialSharingFragment").commit();
                    try {
                        Bundle bundle = new Bundle();
                        Intent channelIntent = new Intent(HomeActivity.this, Class.forName("com.onboarding.nowfloats.ui.updateChannel.ContainerUpdateChannelActivity"));
                        String rootAlisasURI = session.getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI);
                        String normalURI = "http://" + session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG).toLowerCase() + getString(R.string.tag_for_partners);
                        session.setHeader(Constants.WA_KEY);
                        bundle.putString(UserSessionManager.KEY_FP_ID, session.getFPID());
                        bundle.putString(Key_Preferences.GET_FP_DETAILS_TAG, session.getFpTag());
                        bundle.putString(Key_Preferences.GET_FP_EXPERIENCE_CODE, session.getFP_AppExperienceCode());
                        bundle.putBoolean("IsUpdate", true);
                        if (rootAlisasURI != null && !rootAlisasURI.isEmpty()) bundle.putString("website_url", rootAlisasURI);
                        else bundle.putString("website_url", normalURI);
                        channelIntent.putExtras(bundle);
                        channelIntent.putExtra("FRAGMENT_TYPE", "MY_DIGITAL_CHANNEL");
                        startActivity(channelIntent);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } else if (nextScreen.equals(getString(R.string.manage_inventory))) {
                    WebEngageController.trackEvent("NAV - ORDERS", "ORDERS", null);
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, manageInventoryFragment, "ManageInventory")
                            .commit();
//                    Intent socialSharingIntent = new Intent(HomeActivity.this, ManageInventoryActivity.class);
//                    startActivity(socialSharingIntent);
                } else if (nextScreen.equals(getString(R.string.manage_inbox))) {
                    WebEngageController.trackEvent("NAV - ENQUIRIES", "ENQUIRIES", null);
//                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, manageInboxFragment, "ManageInbox")
//                            .commit();
                    Intent queries = new Intent(HomeActivity.this, BusinessEnquiryActivity.class);
                    startActivity(queries);
                } else if (nextScreen.equals(getString(R.string.manage_customer_calls))) {
                    WebEngageController.trackEvent("NAV - CALLS", "CALLS", null);
                    Intent i = new Intent(HomeActivity.this, VmnCallCardsActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else if (nextScreen.equals(getString(R.string.upgrades))) {
                    MixPanelController.track(EventKeysWL.SIDE_PANEL_UPGRADE, null);
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, upgradesFragment, "upgradesFragment")
                            .commit();
                } else if (nextScreen.equals(getString(R.string.add_ons))) {
                    MixPanelController.track(EventKeysWL.SIDE_PANEL_ADD_ONS, null);
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, addOnFragment, "addOnFragment")
                            .commit();
                } else if (nextScreen.equals(getString(R.string.about))) {
                    WebEngageController.trackEvent("NAV - ABOUT_BOOST", "ABOUT_BOOST", null);
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, aboutFragment, "aboutFragment")
                            .commit();
                } else if (nextScreen.equals(getString(R.string.manage_content))) {
                    WebEngageController.trackEvent("NAV - MANAGE_CONTENT", "MANAGE_CONTENT", null);
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, manageContentFragment, "manageContentFragment")
                            .commit();
                } else if (nextScreen.equals(getString(R.string.account_settings))) {
                    WebEngageController.trackEvent("NAV - ACCOUNT_SETTINGS", "ACCOUNT_SETTINGS", null);
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, accountSettingsFragment, "accountSettingsFragment")
                            .commit();
                } else if (nextScreen.equals(getString(R.string.addon_marketplace))) {
                    WebEngageController.trackEvent("NAV - ADDONS_MARKETPLACE", "ADDONS_MARKETPLACE", null);
                    initiateAddonMarketplace();
                } else if (nextScreen.equals(getString(R.string.subscriptions))) {
                    WebEngageController.trackEvent("NAV - SUBSCRIPTIONS", "SUBSCRIPTIONS", null);
                    Intent subscribers = new Intent(HomeActivity.this, SubscribersActivity.class);
                    startActivity(subscribers);
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
            Intent i = new Intent(this, BusinessAppsDetailsActivity.class);
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
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, getString(R.string.share_with)));
        session.setWebsiteshare(true);
    }

    private boolean get_VersionUpdate() {
//        if (!Methods.isOnline(this) || BuildConfig.DEBUG) {
//            return false;
//        }
        try {
            String new_version = Jsoup.connect("https://play.google.com/store/apps/details?id=" + getPackageName() + "&hl=it")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .body().getElementsByClass("xyOfqd").select(".hAyfc")
                    .get(3).child(1).child(0).child(0).ownText();
            return newer_version_available(getPackageManager().getPackageInfo(getPackageName(), 0).versionName, new_version);
        } catch (Exception e) {
            return false;
        }
    }

    private void initiateAddonMarketplace() {
        Intent intent = new Intent(HomeActivity.this, UpgradeActivity.class);
        intent.putExtra("expCode", session.getFP_AppExperienceCode());
        intent.putExtra("fpName", session.getFPName());
        intent.putExtra("fpid", session.getFPID());
        intent.putExtra("loginid", session.getUserProfileId());
        if (session.getFPEmail() != null) {
            intent.putExtra("email", session.getFPEmail());
        } else {
            intent.putExtra("email", "ria@nowfloats.com");
        }
        if (session.getFPPrimaryContactNumber() != null) {
            intent.putExtra("mobileNo", session.getFPPrimaryContactNumber());
        } else {
            intent.putExtra("mobileNo", "9160004303");
        }
        intent.putExtra("profileUrl", session.getFPLogo());
        startActivity(intent);
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
        if (mExpireDialog != null && !mExpireDialog.isCancelled()) {
            mExpireDialog.dismiss();
        }
        try {
            Intent intent;
            if (BuildConfig.APPLICATION_ID.equalsIgnoreCase("com.capture")) {
                intent = new Intent(this, FlavourFivePlansActivity.class);
            } else {
                intent = new Intent(this, NewPricingPlansActivity.class);
            }
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } catch (Exception e) {
            e.printStackTrace();
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

//        if (bundle != null && bundle.containsKey("Username")) {
//
//            progressDialog = ProgressDialog.show(HomeActivity.this, "", getString(R.string.loading));
//            progressDialog.setCancelable(false);
//
//            API_Login apiLogin = new API_Login(HomeActivity.this, session, bus);
//            apiLogin.authenticate(bundle.getString("Username"), bundle.getString("Password"), Constants.clientId);
//
//        }
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
        if (FirebaseInstanceId.getInstance().getToken() != null) {
            AnaCore.saveFcmToken(this, FirebaseInstanceId.getInstance().getToken());
            AnaCore.registerUser(this, session.getFpTag(), Constants.ANA_BUSINESS_ID, Constants.ANA_CHAT_API_URL);
        }
        getNfxTokenData();
        BoostLog.d(TAG, "In on CreateView");
        MixPanelController.sendMixPanelProperties(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME),
                session.getFPDetails(Key_Preferences.GET_FP_DETAILS_EMAIL),
                session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG),
                session.getFPDetails(Key_Preferences.GET_FP_DETAILS_CREATED_ON));

        mDrawerLayout = findViewById(R.id.drawer_layout);
        homeFragment = new Home_Fragment_Tab();
        businessFragment = new Business_Profile_Fragment_V2();
        manageCustomerFragment = new ManageCustomerFragment();
        keyboardFragment = new KeyboardFragment();
        facebookLeadsFragment = new FacebookLeadsFragment();
        manageInventoryFragment = new ManageInventoryFragment();
        manageInboxFragment = new ManageInboxFragment();
        upgradesFragment = new UpgradesFragment();
        addOnFragment = new AddOnFragment();
        aboutFragment = new AboutFragment();
        manageContentFragment = new ManageContentFragment();
        accountSettingsFragment = new AccountSettingsFragment();
        uniqueVisitorsFragment = new UniqueVisitorsFragment();
        socialSharingFragment = new SocialSharingFragment();
        siteMeterFragment = new Site_Meter_Fragment();
        customPageActivity = new CustomPageFragment();
        helpAndSupportFragment = new HelpAndSupportFragment();
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    PackageInfo info = getPackageManager().getPackageInfo(BuildConfig.APPLICATION_ID,
//                            PackageManager.GET_SIGNATURES);
//                    for (Signature signature : info.signatures) {
//                        MessageDigest md = MessageDigest.getInstance("SHA");
//                        md.update(signature.toByteArray());
//                        BoostLog.v("ggg KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//                    }
//                } catch (PackageManager.NameNotFoundException e) {
//
//                } catch (NoSuchAlgorithmException e) {
//
//                }
//
//            }
//        }).start();


        new Thread(new Runnable() {
            @Override
            public void run() {
                setMixPanelProperties();
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
                            if (chk) appUpdateAlertDialog(HomeActivity.this);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        if (Constants.PACKAGE_NAME.equals("com.biz2.nowfloats")) {
            WebEngageController.setUserContactInfoProperties(session);
        }


        toolbar = findViewById(R.id.app_bar);
        headerText = toolbar.findViewById(R.id.titleTextView);
        headerText.setText(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
        headerText.setSelected(true);
        headerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Fragment myFragment = getSupportFragmentManager().findFragmentByTag("homeFragment");
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
        plusAddButton = toolbar.findViewById(R.id.image_gallery_add_image_button);
        shareButton = toolbar.findViewById(R.id.business_profile_share_button);
        HomeActivity.shareButton.setImageResource(R.drawable.share_with_apps);
        PorterDuffColorFilter whiteLabelFilter_pop_ip = new PorterDuffColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN);
        HomeActivity.shareButton.setColorFilter(whiteLabelFilter_pop_ip);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        drawerFragment = (SidePanelFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, findViewById(R.id.drawer_layout), toolbar);
        mDrawerLayout.closeDrawer(Gravity.LEFT);

//        ChatFragment.chatModels.add(new ChatModel("New Message",true,Methods.getCurrentTime()));
//        ChatFragment.chatModels.add(new ChatModel("Next Message",false,Methods.getCurrentTime()));
//        ChatFragment.chatModels.add(new ChatModel("New Message",true,Methods.getCurrentTime()));
//        ChatFragment.chatModels.add(new ChatModel("Next Message",false,Methods.getCurrentTime()));
        setTitle(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME));
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.mainFrame, homeFragment, "homeFragment");
//        ft.commit();

        loadFragment(homeFragment, "homeFragment");
        deepLink(mDeepLinkUrl);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BoostLog.d("cek", "home selected");
                if (drawerFragment.mDrawerToggle.isDrawerIndicatorEnabled()) {
                    ((DrawerLayout) findViewById(R.id.drawer_layout)).openDrawer(GravityCompat.START);
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

//        if (pref.getBoolean(Key_Preferences.HAS_SUGGESTIONS, false)) {
        checkCustomerAssistantService();
//        } else {
//            getCustomerAssistantSuggestions();
//        }

        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            String action = intent.getAction();
            String data = intent.getDataString();
            BoostLog.d("Data: ", data + "  " + action);
            if (session.checkLogin()) {
                deepLink(data.substring(data.lastIndexOf("/") + 1));
            } else {
                finish();
            }
        }
    }


    private void loadFragment(Fragment fragment, String title) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFrame, fragment, title);
        ft.commit();
    }

    @Subscribe
    public void nfxCallback(NfxGetTokensResponse response) {
        if (BuildConfig.APPLICATION_ID.equals("com.biz2.nowfloats")) {
            SharedPreferences smsPref = getSharedPreferences(com.nfx.leadmessages.Constants.SHARED_PREF, Context.MODE_PRIVATE);
            smsPref.edit().putString(com.nfx.leadmessages.Constants.FP_ID, session.getFPID()).apply();
            //getPermissions();
        }

    }

    private void getNfxTokenData() {
        Get_FP_Details_Service.newNfxTokenDetails(this, session.getFPID(), bus);
        Get_FP_Details_Service.autoPull(this, session.getFPID());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean canDrawOverlaysUsingReflection(Context context) {

        try {

            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            Class clazz = AppOpsManager.class;
            Method dispatchMethod = clazz.getMethod("checkOp", int.class, int.class, String.class);
//AppOpsManager.OP_SYSTEM_ALERT_WINDOW = 24
            int mode = (Integer) dispatchMethod.invoke(manager, new Object[]{24, Binder.getCallingUid(), context.getApplicationContext().getPackageName()});

            return AppOpsManager.MODE_ALLOWED == mode;

        } catch (Exception e) {
            return false;
        }

    }
}
