package nowfloats.bubblebutton;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import nowfloats.bubblebutton.bubble.BubblesService;

/**
 * Created by Admin on 11-04-2017.
 */

public class ProductService extends AccessibilityService {


    public static final String PK_NAME_WHATSAAPP = "com.whatsapp";
    public static final String PK_GOOGLE ="com.google";
    public static final String  PK_ANDROID="com.android";
    public static final String  PK_BOOST="com.biz2.nowfloats";

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

        Intent intent1 = new Intent(Intent.ACTION_MAIN);
        intent1.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent1, PackageManager.MATCH_DEFAULT_ONLY);
        String currentHomePackage = resolveInfo.activityInfo.packageName;
        Log.v("ggg",event.toString());
        if(!TextUtils.isEmpty(event.getPackageName())){
            if(event.getPackageName().toString().equalsIgnoreCase(PK_NAME_WHATSAAPP)){

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    return;
                }else{
                    startService(new Intent(ProductService.this,BubblesService.class));
                }
            }else{
                //stopService(new Intent(ProductService.this,BubblesService.class));
            }
        }

    }

    @Override
    public void onInterrupt() {

    }

}
