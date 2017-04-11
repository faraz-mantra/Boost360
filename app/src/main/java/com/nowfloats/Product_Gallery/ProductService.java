package com.nowfloats.Product_Gallery;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.nowfloats.Product_Gallery.Service.FloatingViewService;

/**
 * Created by Admin on 11-04-2017.
 */

public class ProductService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        Log.v("ggg","whindow changed");

            //If the draw over permission is not available open the settings screen
            //to grant the permission.

            Intent intent = new Intent(this, FloatingViewService.class);
            startService(intent);


        Log.v("ggg",event.toString());
    }

    @Override
    public void onInterrupt() {

    }

}
