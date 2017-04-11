package com.nowfloats.Product_Gallery.Service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.thinksity.R;

/**
 * Created by Admin on 11-04-2017.
 */

public class FloatingViewService extends Service implements View.OnTouchListener, View.OnLongClickListener, View.OnClickListener {
    LinearLayout detailButton,imageButton;
    private WindowManager mWindowManager;
    WindowManager.LayoutParams params;
    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;
    View container =null;
    ImageView cancelButton;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {


            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            stopSelf();
            return;
        }
        container = LayoutInflater.from(this).inflate(R.layout.floating_container,null);
        imageButton = (LinearLayout) container.findViewById(R.id.floating_image);
        detailButton = (LinearLayout) container.findViewById(R.id.floating_view);
        cancelButton = (ImageView) container.findViewById(R.id.floating_cancel);
        imageButton.setOnTouchListener(this);
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);

        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.START;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(container, params);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null){
            return Service.START_NOT_STICKY;
        }else {
            return Service.START_REDELIVER_INTENT;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    //remember the initial position.
                    initialX = params.x;
                    initialY = params.y;

                    //get the touch location
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    return true;
                case MotionEvent.ACTION_UP:
                    int Xdiff = (int) (event.getRawX() - initialTouchX);
                    int Ydiff = (int) (event.getRawY() - initialTouchY);


                    //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                    //So that is click event.
                    if (Xdiff < 10 && Ydiff < 10) {

                        if (!isViewCollapsed()) {
                            //When user clicks on the image view of the collapsed layout,
                            //visibility of the collapsed layout will be changed to "View.GONE"
                            //and expanded view will become visible.
                            params.height = WindowManager.LayoutParams.MATCH_PARENT;
                            params.width = WindowManager.LayoutParams.MATCH_PARENT;
                            detailButton.setVisibility(View.VISIBLE);
                            resetParams();
                        }else{
                            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                            detailButton.setVisibility(View.GONE);
                            resetParams();
                        }

                    }
                    return true;
                case MotionEvent.ACTION_MOVE:
                    //Calculate the X and Y coordinates of the view.
                    params.x = initialX + (int) (event.getRawX() - initialTouchX);
                    params.y = initialY + (int) (event.getRawY() - initialTouchY);


                    //Update the layout with new X & Y coordinate
                    mWindowManager.updateViewLayout(container, params);
                    return true;
            }
            return false;
    }

    private void resetParams(){
        params.gravity = Gravity.TOP | Gravity.START;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;
        mWindowManager.updateViewLayout(container, params);
    }

    private boolean isViewCollapsed() {
        return container == null || detailButton.getVisibility() == View.VISIBLE;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("ggg","destroy");
        if (container != null) mWindowManager.removeView(container);
    }

    @Override
    public boolean onLongClick(View v) {
        if(v.getId() == R.id.floating_image){
            cancelButton.setVisibility(View.VISIBLE);
            //container.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.floating_cancel){
            container.setVisibility(View.GONE);
            stopSelf();
        }
    }
}
