package com.nowfloats.accessbility;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.nowfloats.bubble.BubblesService;

import java.util.List;


/**
 * Created by Admin on 11-04-2017.
 */

@RequiresApi(api = Build.VERSION_CODES.DONUT)
public class DataAccessbilityService extends AccessibilityService {


    public static final String PK_NAME_WHATSAAPP = "com.twitter.android";
    public static final String PK_NAME_NOWFLOATS= "com.biz2.nowfloats";
    public static final String PK_GOOGLE = "com.google";
    public static final String PK_ANDROID = "com.android";
    public static final String PK_BOOST = "com.biz2.nowfloats";


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        info.notificationTimeout = 100;
        info.flags = 91;
        info.feedbackType = 16;
        setServiceInfo(info);
        Log.e("onServiceConnected", "onServiceConnectedonServiceConnectedonServiceConnected");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        Log.e("event.getPackageName()",event.getPackageName().toString());
//        if (isWhatsAppRunning()) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                return;
//            } else {
//                Intent intent = new Intent(DataAccessbilityService.this, BubblesService.class);
//                startService(intent);
//            }
//        } else {
//            stopService(new Intent(DataAccessbilityService.this, BubblesService.class));
//        }

    }

    private boolean isWhatsAppRunning() {
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        String packageName = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            List<ActivityManager.RunningAppProcessInfo> taskInfo =  am.getRunningAppProcesses();
            if (taskInfo != null && taskInfo.size() > 0) {
                packageName = taskInfo.get(0).pkgList[0];
                if(packageName.equalsIgnoreCase(PK_NAME_NOWFLOATS)){
//                    Log.e("className",taskInfo.get(0).getClass());
                }
            }
        }else{

            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(10);

            if (taskInfo != null && taskInfo.size() > 0) {

                ComponentName componentInfo = taskInfo.get(0).topActivity;
                packageName = componentInfo.getPackageName();
            }
        }
        Log.e("packageName",packageName);
        return packageName.contains(PK_NAME_WHATSAAPP);
    }

    @Override
    public void onInterrupt() {

    }
}
