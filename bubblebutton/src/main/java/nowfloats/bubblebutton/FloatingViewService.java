package nowfloats.bubblebutton;

import android.app.Service;
import android.content.Context;
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
import android.widget.Toast;

import nowfloats.bubblebutton.activityDialogs.ProductSuggestions;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by Admin on 11-04-2017.
 */

public class FloatingViewService extends Service implements View.OnTouchListener, View.OnClickListener {
    LinearLayout detailButton,imageButton;
    WindowManager.LayoutParams bubbleParams,cancelParams;
    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;
    View container =null,container_cancel=null,dialog=null;
    ImageView cancelButton;
    boolean isCancel = true;
    private long pressStartTime;
    private int lastAction;
    private WindowManager mWindowManager;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {


            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            stopSelf();
            return;
        }

        container = LayoutInflater.from(this).inflate(R.layout.floating_container,null);
        container_cancel = LayoutInflater.from(this).inflate(R.layout.cancel_button,null);

        cancelParams =getDefaultCancelParam();
        bubbleParams = bubbleDefaultParams();
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        /*bubbleManager = new BubbleManager.Builder(this).build()
                .addCancelView(container_cancel,cancelParams);
        bubbleManager.show(container,bubbleParams);*/

        imageButton = (LinearLayout) container.findViewById(R.id.floating_image);
        detailButton = (LinearLayout) container.findViewById(R.id.floating_view);
        cancelButton = (ImageView) container_cancel.findViewById(R.id.floating_cancel);

        imageButton.setOnTouchListener(this);
        detailButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        mWindowManager.addView(container,bubbleParams);
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
                    initialX = bubbleParams.x;
                    initialY = bubbleParams.y;
                    pressStartTime = System.currentTimeMillis();

                    //get the touch location
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    Log.v("kkk", initialX +" down "+initialY+" "+initialTouchX+" "+initialTouchY);

                    if(!isCancel){
                        //bubbleManager.removeView(container_cancel);
                        mWindowManager.removeView(container_cancel);
                        isCancel = true;
                    }
                    lastAction = event.getAction();
                    return true;
                case MotionEvent.ACTION_UP:
                    int Xdiff = (int) (event.getRawX() - initialTouchX);
                    int Ydiff = (int) (event.getRawY() - initialTouchY);


                    //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                    //So that is click event.
                    long timeDiff = System.currentTimeMillis() - pressStartTime;
                    if (lastAction == MotionEvent.ACTION_DOWN) {

                        if (!isViewCollapsed()) {
                            //When user clicks on the image view of the collapsed layout,
                            //visibility of the collapsed layout will be changed to "View.GONE"
                            //and expanded view will become visible.
                            bubbleParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                            bubbleParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                            //detailButton.setVisibility(View.VISIBLE);
                            Intent i = new Intent(this, ProductSuggestions.class);
                            i.setFlags(FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);

                            resetParams();
                        }else{
                            bubbleParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                            bubbleParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                            //detailButton.setVisibility(View.GONE);
                            resetParams();
                        }

                        //cancelButton.setVisibility(View.GONE);
                    }
                    if(!isCancel){
                        //bubbleManager.removeView(container_cancel);
                        mWindowManager.removeView(container_cancel);
                        isCancel = true;
                    }
                    return true;
                case MotionEvent.ACTION_MOVE:
                    //Calculate the X and Y coordinates of the view.
                    bubbleParams.x = initialX + (int) (- event.getRawX() + initialTouchX);
                    bubbleParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                    Log.v("kkk", initialX +" move "+initialY+" "+initialTouchX+" "+initialTouchY+" "+event.getRawX()+" "+event.getRawY());
                    if(isCancel) {
                        //bubbleManager.addCancelView(container_cancel,cancelParams);
                        mWindowManager.addView(container_cancel,cancelParams);
                        isCancel =false;
                    }
                    lastAction = event.getAction();
                    //Update the layout with new X & Y coordinate
                   // bubbleManager.updateBubbleView(container,bubbleParams);
                    mWindowManager.updateViewLayout(container,bubbleParams);
                    return true;
            }
            return false;
    }
    private WindowManager.LayoutParams getDefaultCancelParam(){
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.RGBA_8888);

        params.gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;

        params.x = 0;
        params.y = 0;
        return params;
    }

    private WindowManager.LayoutParams bubbleDefaultParams(){

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.RGBA_8888);

        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.END;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 0;
        return params;
    }
    private void resetParams(){
        bubbleParams.gravity = Gravity.TOP | Gravity.END;        //Initially view will be added to top-left corner
        bubbleParams.x = 0;
        bubbleParams.y = 0;
        //bubbleManager.updateBubbleView(container,bubbleParams);
        mWindowManager.updateViewLayout(container,bubbleParams);
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
