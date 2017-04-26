package com.nowfloats.accessbility;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.nowfloats.bubble.BubblesService;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;
import com.nowfloats.util.MixPanelController;


/**
 * Created by Admin on 11-04-2017.
 */

@RequiresApi(api = Build.VERSION_CODES.DONUT)
public class DataAccessbilityServiceV1 extends AccessibilityService {


    public static final String PK_NAME_WHATSAPP = "com.whatsapp";
    public static final String PK_NAME_NOWFLOATS = "com.biz2.nowfloats";

    //    public static final String PK_NAME_WHATSAPP = "com.twitter.android";
    private SharedPreferences pref;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        pref = getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        if(!TextUtils.isEmpty(pref.getString(Key_Preferences.GET_FP_DETAILS_TAG,""))
        && pref.getBoolean(Key_Preferences.SHOW_WHATS_APP_DIALOG,true)) {
            showWhatsAppDialog();
            pref.edit().putBoolean(Key_Preferences.SHOW_WHATS_APP_DIALOG,false).apply();
            pref.edit().putBoolean(Key_Preferences.IS_BOOST_BUBBLE_ENABLED,true).apply();
        }
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.notificationTimeout = 100;
        info.flags = 91;
        info.feedbackType = 16;
        setServiceInfo(info);

    }

    private void showWhatsAppDialog() {
        MixPanelController.track(MixPanelController.WHATS_APP_DIALOG,null);
        Intent intent = new Intent(this,WhatsAppDialog.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    private void showOverlayDialog() {
        Intent intent = new Intent(this,OverlayDialog.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            Log.v("ggg",event.toString());
            if (event.getPackageName().toString().equalsIgnoreCase(PK_NAME_WHATSAPP)
                    || (!TextUtils.isEmpty(event.getClassName()) && (
                    event.getClassName().toString().equalsIgnoreCase(BUBBLE_CLASS_NAME)))) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
//                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
                    showOverlayDialog();
                } else if (!isMyServiceRunning(BubblesService.class) &&
                        !TextUtils.isEmpty(pref.getString(Key_Preferences.GET_FP_DETAILS_TAG, null))
                        && pref.getBoolean(Key_Preferences.IS_BOOST_BUBBLE_ENABLED,false)) {
                    Intent intent = new Intent(DataAccessbilityServiceV1.this, BubblesService.class);
                    startService(intent);
                }
            } else {

                if(!TextUtils.isEmpty(event.getClassName()) && event.getPackageName().toString().equalsIgnoreCase(PK_NAME_NOWFLOATS) &&
                !event.getClassName().toString().contains(PK_NAME_NOWFLOATS)){

                }else{
                    Log.v("ggg1","closed");
                    stopService(new Intent(DataAccessbilityServiceV1.this, BubblesService.class));
                }

            }
        }
    }

    private String BUBBLE_CLASS_NAME = "com.nowfloats.accessbility.BubbleDialog";

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        pref.edit().putBoolean(Key_Preferences.SHOW_WHATS_APP_DIALOG,true).apply();
        pref.edit().putBoolean(Key_Preferences.IS_BOOST_BUBBLE_ENABLED,false).apply();
    }
}
