package com.nowfloats.Volley;

import static com.nowfloats.util.BoostLog.VERBOSE;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.appservice.AppServiceApplication;
import com.boost.dbcenterapi.DBCenterAPIApplication;
import com.boost.marketplace.MarketplaceApplication;
import com.boost.presignin.AppPreSignInApplication;
import com.boost.presignup.locale.LocaleManager;
import com.clevertap.android.sdk.CleverTapAPI;
import com.dashboard.AppDashboardApplication;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.festive.poster.FestivePosterApplication;
import com.framework.BaseApplication;
import com.framework.analytics.UserExperiorController;
import com.framework.utils.AppsFlyerUtils;
import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.inventoryorder.BaseOrderApplication;
import com.invitereferrals.invitereferrals.InviteReferralsApi;
import com.invitereferrals.invitereferrals.InviteReferralsApplication;
import com.nowfloats.education.koindi.KoinBaseApplication;
import com.nowfloats.util.Constants;
import com.onboarding.nowfloats.BaseBoardingApplication;
import com.thinksity.R;
import com.webengage.sdk.android.WebEngageActivityLifeCycleCallbacks;
import com.webengage.sdk.android.WebEngageConfig;

import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Method;

import dev.patrickgold.florisboard.ime.core.FlorisApplication;
import zendesk.chat.Chat;
import zendesk.core.AnonymousIdentity;
import zendesk.core.Identity;
import zendesk.core.Zendesk;
import zendesk.support.Support;

public class AppController extends BaseApplication/* implements IAviaryClientCredentials*/ {

    public static final String TAG = AppController.class.getSimpleName();
    private static AppController mInstance;
    private final String APPSFLAYER_DEV_KEY = "8PD2DC7BbVdr7aLnRE8wHY";
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private LocaleManager localeManager;
    CleverTapAPI clevertapDefaultInstance;

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public static String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    /*public class PushNotificationCallbacksImpl implements PushNotificationCallbacks {

        private static final String TAG = "PushNotificationCallbacksImpl";

        @Override
        public PushNotificationData onPushNotificationReceived(Context context, PushNotificationData pushNotificationData) {
            //Log.d(TAG, "Received Push Notification , Experiment Id : " + pushNotificationData.getExperimentId());
            return pushNotificationData;
        }

        @Override
        public void onPushNotificationShown(Context context, PushNotificationData pushNotificationData) {
            //Log.d(TAG, "Push Notification Shown , Experiment Id : " + pushNotificationData.getExperimentId());
        }

        @Override
        public boolean onPushNotificationClicked(Context context, PushNotificationData pushNotificationData) {
            //Log.d(TAG, "User clicked Push Notification , Experiment Id : " + pushNotificationData.getExperimentId());
            return false;
        }

        @Override
        public void onPushNotificationDismissed(Context context, PushNotificationData pushNotificationData) {
            //Log.d(TAG, "Push notification dismissed , Experiment Id : " + pushNotificationData.getExperimentId());
        }

        @Override
        public boolean onPushNotificationActionClicked(Context context, PushNotificationData pushNotificationData, String buttonId) {
            //Log.d(TAG, "User clicked push notification action button , Experiment Id : " + pushNotificationData.getExperimentId() + " button Id : " + buttonId);
            return false;
        }
    }*/
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                Log.d("AppController", "Success : " + dir);
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        SmartLookController.initiateSmartLook(this.getString(R.string.samrt_look_api_key));
        FirebaseApp.initializeApp(this.getApplicationContext());
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        BaseOrderApplication.instance = this;
        BaseOrderApplication.initModule(this);
        BaseBoardingApplication.instance = this;
        BaseBoardingApplication.initModule(this);
        AppServiceApplication.instance = this;
        AppServiceApplication.initModule(this);
        AppDashboardApplication.instance = this;
        AppDashboardApplication.initModule(this);
        AppPreSignInApplication.instance = this;
        AppPreSignInApplication.initModule(this);
        FlorisApplication.instance = this;
        FlorisApplication.initModule(this);
        FestivePosterApplication.instance = this;
        FestivePosterApplication.initModule(this);
        //upgrade module changes
        DBCenterAPIApplication.instance = this;
        DBCenterAPIApplication.initModule(this);
        //marketplace module changes
        MarketplaceApplication.instance = this;
        MarketplaceApplication.initModule(this);
        initWebEngage();
//        initCleverTap();
        UserExperiorController.INSTANCE.startRecording(this);

        //Invite Referral
        InviteReferralsApplication.register(this);
        InviteReferralsApi.getInstance(this).tracking("install", null, 0, null, null,null, new JSONObject());
        //Koin
        KoinBaseApplication.initModule(this);
        //ContextApplication.initSdk(this, this);
        //AppIce SDk
        //ContextApplication.initSdk(getApplicationContext(), this);
        //AppsFlyerLib.setAppsFlyerKey("drr3ek3vNxVmxJZgtBpfnR");

        appsFlyerEvent(this);

        try {
            //Fabric.with(this, new Crashlytics());
        } catch (Exception e) {
            e.printStackTrace();
        }


//        if (!BuildConfig.DEBUG) {
//
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//        }
//        ApxorSDK.initialize(BuildConfig.APXOR_BUNDLED_ID, getApplicationContext());
        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        //Log.d("Device ID","Device ID : "+deviceId);

        /*CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("Roboto-Light.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());*/
//        FacebookSdk.sdkInitialize(getApplicationContext());
        if (!FacebookSdk.isInitialized()) {
            FacebookSdk.sdkInitialize(getApplicationContext());
        }
        registerActivityLifecycleCallbacks(new WebEngageActivityLifeCycleCallbacks(this));
        //WebEngage.registerPushNotificationCallback(new PushNotificationCallbacksImpl());
        AppEventsLogger.activateApp(instance);

        mInstance = this;
        try {
          /*  MobihelpConfig config = new MobihelpConfig("https://nowfloats.freshdesk.com",
                    "nowfloatsboost-1-eb43cfea648e2fd8a088c756519cb4d6",
                    "e13c031f28ba356a76110e8d1e2c4543c84670d5");
            Mobihelp.init(this, config);*/
        } catch (Exception e) {
            e.printStackTrace();
        }

        //TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "open_sans_hebrew_bold.ttf");
    }

    private void appsFlyerEvent(AppController appController) {
        /* Init AppsFlyer SDK */
        AppsFlyerUtils.initAppsFlyer(appController, APPSFLAYER_DEV_KEY);
    }

    void initWebEngage() {
        WebEngageConfig webEngageConfig = new WebEngageConfig.Builder()
                .setWebEngageKey(getString(R.string.webengage_license_code))
                .setDebugMode(false)
                .build();
        registerActivityLifecycleCallbacks(new WebEngageActivityLifeCycleCallbacks(this, webEngageConfig));
    }

//    void initCleverTap() {
//        //Set Debug level for CleverTap
//        CleverTapAPI.setDebugLevel(VERBOSE);
//        clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(getApplicationContext());
//    }

    @SuppressWarnings({"unused"})
    public enum LogLevel {
        OFF(-1),
        INFO(0),
        DEBUG(2),
        VERBOSE(3);

        private final int value;

        LogLevel(final int newValue) {
            value = newValue;
        }

        public int intValue() {
            return value;
        }
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public void clearApplicationData() {
        deleteCACHE();

        // com.nostra13.universalimageloader.core.ImageLoader imageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
//   mImageLoader.clearDiskCache();
//        mImageLoader.clearMemoryCache();

        String dir = Constants.dataDirectory;
        File sdcard = getExternalCacheDir();


        File pictureDir = new File(sdcard, dir);

        // File cache = getCacheDir();
        // File appDir = new File(cache.getParent());
        if (pictureDir.exists()) {
            String[] children = pictureDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(pictureDir, s));
                    Log.i("TAG", "File /data/data/com.thinksity/ :" + pictureDir + " , " + s + " DELETED");
                }
            }
        }
    }

    public void deleteCACHE() {
        PackageManager pm = getPackageManager();
        // Get all methods on the PackageManager

        Method[] methods = pm.getClass().getDeclaredMethods();
        for (Method m : methods) {
            if (m.getName().equals("freeStorage")) {
                // Found the method I want to use
                try {
                    long desiredFreeStorage = 8 * 1024 * 1024 * 1024; // Request for 8GB of free space
                    m.invoke(pm, desiredFreeStorage, null);
                } catch (Exception e) {
                    // Method invocation failed. Could be a permission problem
                }
                break;
            }
        }
    }

    public void addToRequstQueue(Request request) {
        if (mRequestQueue == null) {
            getRequestQueue();
        }
        mRequestQueue.add(request);
    }

    /* Image editing sdk methods*/
    /*@Override
    public String getBillingKey() {
        return "";
    }

    @Override
    public String getClientID() {
        return "08fb6cdc41c94c30ad3fe5067f8c9078";
    }

    @Override
    public String getClientSecret() {
        return "e916fce1-97ca-4dd7-9f95-7a58f6ce42cf";
    }*/

    @Override
    protected void attachBaseContext(Context base) {
        MultiDex.install(this);
        localeManager = new LocaleManager(base);
        Log.e("getLanguage", ">>>>>>>>>>>>>>" + localeManager.getLanguage());
        try {
            localeManager.setNewLocale(base, localeManager.getLanguage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.attachBaseContext(localeManager.setLocale(base));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        localeManager.setLocale(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.d("onConfigurationChanged", "" + newConfig.getLocales().toLanguageTags());
        }
    }

}