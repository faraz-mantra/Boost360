package com.nowfloats.accessbility;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;

import com.nowfloats.bubble.BubblesService;


/**
 * Created by Admin on 11-04-2017.
 */

@RequiresApi(api = Build.VERSION_CODES.DONUT)
public class DataAccessbilityService extends AccessibilityService {


    //    public static final String PK_NAME_WHATSAPP = "com.whatsapp";
    public static final String PK_NAME_WHATSAPP = "com.twitter.android";

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.notificationTimeout = 100;
        info.flags = 91;
        info.feedbackType = 16;
        setServiceInfo(info);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (event.getPackageName().toString().equalsIgnoreCase(PK_NAME_WHATSAPP)
                    || (!TextUtils.isEmpty(event.getClassName()) &&
                    event.getClassName().toString().equalsIgnoreCase(BUBBLE_CLASS_NAME))) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    return;
                } else if (!isMyServiceRunning(BubblesService.class)) {
                    Intent intent = new Intent(DataAccessbilityService.this, BubblesService.class);
                    startService(intent);
                }
            } else {
                stopService(new Intent(DataAccessbilityService.this, BubblesService.class));
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
}
