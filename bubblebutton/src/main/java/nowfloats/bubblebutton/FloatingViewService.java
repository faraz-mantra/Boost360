package nowfloats.bubblebutton;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by Admin on 11-04-2017.
 */

public class FloatingViewService extends Service implements View.OnTouchListener, View.OnClickListener {
    LinearLayout detailButton,imageButton;
    private WindowManager mWindowManager;
    WindowManager.LayoutParams params;
    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;
    View container =null,container_cancel=null;
    ImageView cancelButton;
    boolean isCancel = true;
    private long pressStartTime;

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
        LinearLayout linearLayout = new LinearLayout(this){

            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {

                if(event.getKeyCode() == KeyEvent.KEYCODE_BACK)
                {

                }
                return  true;
            }
        };

        container = LayoutInflater.from(this).inflate(R.layout.floating_container,null);
        container_cancel = LayoutInflater.from(this).inflate(R.layout.cancel_button,null);

        imageButton = (LinearLayout) container.findViewById(R.id.floating_image);
        detailButton = (LinearLayout) container.findViewById(R.id.floating_view);
        cancelButton = (ImageView) container_cancel.findViewById(R.id.floating_cancel);

        imageButton.setOnTouchListener(this);
        detailButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        params = getContainerParam();

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(container, params);

    }

    private WindowManager.LayoutParams getCancelParam(){
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.RGBA_8888);

        params.gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;

        params.x = 0;
        params.y = -500;
        return params;
    }

    private WindowManager.LayoutParams getContainerParam(){
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PRIORITY_PHONE,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.RGBA_8888);

        DisplayMetrics matrics = getResources().getDisplayMetrics();
        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.START;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;
        return params;
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
                    pressStartTime = System.currentTimeMillis();

                    //get the touch location
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    if(!isCancel){
                        mWindowManager.removeViewImmediate(container_cancel);
                        isCancel = true;
                    }
                    return true;
                case MotionEvent.ACTION_UP:
                    int Xdiff = (int) (event.getRawX() - initialTouchX);
                    int Ydiff = (int) (event.getRawY() - initialTouchY);


                    //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                    //So that is click event.
                    long timeDiff = System.currentTimeMillis() - pressStartTime;
                    if (Xdiff < 5 && Ydiff < 5 && timeDiff< 1000) {

                        if (!isViewCollapsed()) {
                            //When user clicks on the image view of the collapsed layout,
                            //visibility of the collapsed layout will be changed to "View.GONE"
                            //and expanded view will become visible.
                            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                            params.width = WindowManager.LayoutParams.MATCH_PARENT;
                            detailButton.setVisibility(View.VISIBLE);
                            resetParams();
                        }else{
                            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                            params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                            detailButton.setVisibility(View.GONE);
                            resetParams();
                        }
                        //cancelButton.setVisibility(View.GONE);
                    }
                    if(!isCancel){
                        mWindowManager.removeViewImmediate(container_cancel);
                        isCancel = true;
                    }
                    return true;
                case MotionEvent.ACTION_MOVE:
                    //Calculate the X and Y coordinates of the view.
                    params.x = initialX + (int) (event.getRawX() - initialTouchX);
                    params.y = initialY + (int) (event.getRawY() - initialTouchY);

                    if(isCancel) {
                        mWindowManager.addView(container_cancel, getCancelParam());
                        isCancel =false;
                    }
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
    public void onClick(View v) {
        if(v.getId() == R.id.floating_cancel){
            stopSelf();
        }else if(v.getId() == R.id.floating_view){
            Toast.makeText(this, "view clicked", Toast.LENGTH_SHORT).show();
        }
    }

}
