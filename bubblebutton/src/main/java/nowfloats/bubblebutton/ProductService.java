package nowfloats.bubblebutton;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

/**
 * Created by Admin on 11-04-2017.
 */

public class ProductService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {


        if(event.getPackageName().toString().contains(PK_NAME_WHATSAAPP)){
            Intent intent = new Intent(this, FloatingViewService.class);
            startService(intent);
            Log.v("ggg ","window start whats app");
        }else{
            stopService(new Intent(this, FloatingViewService.class));
            Log.v("ggg ","window close whatsapp");
        }

        Intent intent1 = new Intent(Intent.ACTION_MAIN);
        intent1.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = getPackageManager().resolveActivity(intent1, PackageManager.MATCH_DEFAULT_ONLY);
        String currentHomePackage = resolveInfo.activityInfo.packageName;

        Log.v("ggg ",currentHomePackage);
        if(event.getPackageName().toString().contains(currentHomePackage)){
            Log.v("ggg ","window home screen");
        }else{
            Log.v("ggg ","window close home screen");
        }
        Log.v("ggg",event.toString());
    }

    public static final String PK_NAME_WHATSAAPP = "";

    @Override
    public void onInterrupt() {

    }

}
