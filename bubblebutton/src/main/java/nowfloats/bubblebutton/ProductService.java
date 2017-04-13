package nowfloats.bubblebutton;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

/**
 * Created by Admin on 11-04-2017.
 */

public class ProductService extends AccessibilityService {


    public static final String PK_NAME_WHATSAAPP = "com.whatsapp";
    boolean isWhatsApp, isHomeScreen;

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

        if(!TextUtils.isEmpty(event.getPackageName())){
            if(event.getPackageName().toString().contains(PK_NAME_WHATSAAPP)){

                if(!isWhatsApp) {
                Intent intent = new Intent(this, FloatingViewService.class);
                startService(intent);
                    Log.v("ggg ", "window start whats app");
                    isWhatsApp = true;
                }
                if(isHomeScreen) {
                    isHomeScreen =false;
                    Log.v("ggg ", "window close home screen");
                }
            }else if(event.getPackageName().toString().contains(currentHomePackage)){

                if(isWhatsApp) {
                    stopService(new Intent(this, FloatingViewService.class));
                    isWhatsApp =false;
                    Log.v("ggg ", "window close whatsapp");
                }
                if(!isHomeScreen) {
                    Log.v("ggg ", currentHomePackage);
                    Log.v("ggg ", "window home screen");
                    isHomeScreen =true;
                }
            }else{
                if(isHomeScreen) {
                    isHomeScreen =false;
                    Log.v("ggg ", "window close home screen");
                }

                if(isWhatsApp) {
                    isWhatsApp =false;
                    Log.v("ggg ", "window close whatsapp");
                }
            }
        }

        /*


       /* if(event.getPackageName().toString().contains(currentHomePackage)){
            if(!isHomeScreen) {
                Log.v("ggg ", currentHomePackage);
                Log.v("ggg ", "window home screen");
                isHomeScreen =true;
            }
        }else{
            if(isHomeScreen) {
                isHomeScreen =false;
                Log.v("ggg ", "window close home screen");
            }
        }*/
    }

    @Override
    public void onInterrupt() {

    }

}
