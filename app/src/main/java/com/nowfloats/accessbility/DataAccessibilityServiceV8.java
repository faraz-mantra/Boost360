package com.nowfloats.accessbility;

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;

import androidx.annotation.RequiresApi;

import android.view.accessibility.AccessibilityEvent;

import com.nowfloats.bubble.BubblesService;
import com.nowfloats.education.koindi.KoinBaseApplication;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

import java.util.List;


/**
 * Created by Admin on 11-04-2017.
 */

@RequiresApi(api = Build.VERSION_CODES.DONUT)
public class DataAccessibilityServiceV8 extends AccessibilityService {


    public static final String PK_NAME_WHATSAPP = KoinBaseApplication.instance.getString(R.string.whatsapp_package);
    public static final String PK_NAME_NOWFLOATS = "com.biz2.nowfloats";
    public static final String PK_NAME_NOWFLOATS_HOME_ACTIVTY = "com.nowfloats";

    private String SUGGESTIONS_CLASS_NAME = "com.nowfloats.swipecard.SuggestionsActivity";
    private String BUBBLE_CLASS_NAME = "com.nowfloats.accessbility.BubbleDialog";

    private SharedPreferences pref;
    private BubblesService.FROM bFrom;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

//        if (!Constants.PACKAGE_NAME.equals(PK_NAME_NOWFLOATS)) {
//            return;
//        }
      /*  pref = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        if (!TextUtils.isEmpty(pref.getString(Key_Preferences.GET_FP_DETAILS_TAG, ""))
                && pref.getBoolean(Key_Preferences.SHOW_WHATS_APP_DIALOG, true)) {
            showWhatsAppDialog();
            pref.edit().putBoolean(Key_Preferences.SHOW_WHATS_APP_DIALOG, false).apply();
            pref.edit().putBoolean(Key_Preferences.IS_BOOST_BUBBLE_ENABLED, true).apply();
        }

        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.notificationTimeout = 100;
        info.flags = 91;
        info.feedbackType = 16;
        setServiceInfo(info);*/
    }

    private void showWhatsAppDialog() {
        MixPanelController.track(MixPanelController.WHATS_APP_DIALOG, null);
        Intent intent = new Intent(this, WhatsAppDialog.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    private void showOverlayDialog() {
        Intent intent = new Intent(this, OverlayDialog.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        if (!Constants.PACKAGE_NAME.equals(PK_NAME_NOWFLOATS)) {
            return;
        }
        /*if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {

            if (!TextUtils.isEmpty(event.getPackageName()) &&
                    (event.getPackageName().toString().equalsIgnoreCase(PK_NAME_WHATSAPP)
                            || (!TextUtils.isEmpty(event.getClassName()) &&
                            event.getClassName().toString().equalsIgnoreCase(BUBBLE_CLASS_NAME)))) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                    showOverlayDialog();
                } else if (!TextUtils.isEmpty(pref.getString(Key_Preferences.GET_FP_DETAILS_TAG, null))
                        && pref.getBoolean(Key_Preferences.IS_BOOST_BUBBLE_ENABLED, false)) {

                    if (bFrom == BubblesService.FROM.LAUNCHER_HOME_ACTIVITY) {
                        stopService(new Intent(DataAccessibilityServiceV8.this, BubblesService.class));
                    }

                    if (!isMyServiceRunning(BubblesService.class)) {
                        Intent intent = new Intent(DataAccessibilityServiceV8.this, BubblesService.class);
                        bFrom = BubblesService.FROM.WHATSAPP;
                        intent.putExtra(Key_Preferences.DIALOG_FROM, BubblesService.FROM.WHATSAPP);
                        startService(intent);
                    }
                }
            }
//            else if (((!TextUtils.isEmpty(event.getPackageName()) &&
//                    event.getPackageName().toString().equalsIgnoreCase(getLauncherPackage())
//                    || (!TextUtils.isEmpty(event.getClassName()) &&
//                    event.getClassName().toString().equalsIgnoreCase(SUGGESTIONS_CLASS_NAME)))
//                    && pref.getBoolean(Key_Preferences.IS_BOOST_BUBBLE_ENABLED, false) &&
//                    !TextUtils.isEmpty(pref.getString(Key_Preferences.GET_FP_DETAILS_TAG, null)))
//                    && pref.getBoolean(Key_Preferences.HAS_SUGGESTIONS, false)) {
//
//                if (bFrom == BubblesService.FROM.WHATSAPP) {
//                    stopService(new Intent(DataAccessibilityServiceV8.this, BubblesService.class));
//                }
//                if (!isMyServiceRunning(BubblesService.class)) {
//
//                    Intent intent = new Intent(DataAccessibilityServiceV8.this, BubblesService.class);
//                    bFrom = BubblesService.FROM.LAUNCHER_HOME_ACTIVITY;
//                    intent.putExtra(Key_Preferences.DIALOG_FROM, BubblesService.FROM.LAUNCHER_HOME_ACTIVITY);
//                    startService(intent);
//                }
//            }
            else {

                if (!TextUtils.isEmpty(event.getClassName()) &&
                        event.getPackageName().toString().equalsIgnoreCase(PK_NAME_NOWFLOATS) &&
                        event.getClassName().toString().contains(PK_NAME_NOWFLOATS_HOME_ACTIVTY)) {
                    stopService(new Intent(DataAccessibilityServiceV8.this, BubblesService.class));
                } else if (!TextUtils.isEmpty(event.getClassName()) && event.getPackageName().toString().equalsIgnoreCase(PK_NAME_NOWFLOATS) &&
                        !event.getClassName().toString().contains(PK_NAME_NOWFLOATS)) {

                } else {
                    stopService(new Intent(DataAccessibilityServiceV8.this, BubblesService.class));
                }
            }
        }*/
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> list = manager.getRunningServices(Integer.MAX_VALUE);
        if (list != null) {
            for (ActivityManager.RunningServiceInfo service : list) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private String getLauncherPackage() {

        Intent intent1 = new Intent(Intent.ACTION_MAIN);
        intent1.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent1, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onDestroy() {

        if (pref == null) {
            pref = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        }

        pref.edit().putBoolean(Key_Preferences.SHOW_WHATS_APP_DIALOG, true).apply();
        pref.edit().putBoolean(Key_Preferences.IS_BOOST_BUBBLE_ENABLED, false).apply();
        super.onDestroy();
    }
}