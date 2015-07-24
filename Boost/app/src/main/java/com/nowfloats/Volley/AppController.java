package com.nowfloats.Volley;

import android.content.pm.PackageManager;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.appsflyer.AppsFlyerLib;
import com.crashlytics.android.Crashlytics;
import com.freshdesk.mobihelp.Mobihelp;
import com.freshdesk.mobihelp.MobihelpConfig;
import com.nowfloats.util.Constants;

import java.io.File;
import java.lang.reflect.Method;

import io.fabric.sdk.android.Fabric;

public class AppController extends MultiDexApplication/* implements IAviaryClientCredentials*/ {
	 
    public static final String TAG = AppController.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        AppsFlyerLib.setAppsFlyerKey("drr3ek3vNxVmxJZgtBpfnR");
        try {
            Fabric.with(this, new Crashlytics());
        }catch(Exception e){e.printStackTrace();}

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String deviceId = Settings.Secure.getString(this.getContentResolver(),Settings.Secure.ANDROID_ID);

        Log.d("Device ID","Device ID : "+deviceId);

        /*CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("Roboto-Light.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());*/

        mInstance = this;
        try{
            MobihelpConfig config = new MobihelpConfig("https://nowfloats.freshdesk.com",
                    "nowfloatsboost-1-eb43cfea648e2fd8a088c756519cb4d6",
                    "e13c031f28ba356a76110e8d1e2c4543c84670d5");
            Mobihelp.init(this, config);
        }catch(Exception e){e.printStackTrace();}
    }

    public static synchronized AppController getInstance() {
        return mInstance;
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
        if(pictureDir.exists()){
            String[] children = pictureDir.list();
            for(String s : children){
                if(!s.equals("lib")){
                    deleteDir(new File(pictureDir, s));
                    Log.i("TAG", "File /data/data/com.thinksity/ :"+pictureDir+" , " + s + " DELETED");
                }
            }
        }
    }
    public static boolean deleteDir(File dir) {

        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                Log.d("AppController","Success : "+dir);
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }


    public void deleteCACHE () {
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

}