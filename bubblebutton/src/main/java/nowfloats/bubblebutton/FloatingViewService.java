package nowfloats.bubblebutton;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import nowfloats.bubblebutton.adapter.ProductSuggestionAdapter;

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
    boolean isDialog =false;
    private WindowManager mWindowManager;
    private RecyclerView recyclerView;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.v("kkk","overlay permission required");

        container = LayoutInflater.from(this).inflate(R.layout.floating_container,null);
        detailButton = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.dialog_products,null);
        container_cancel = LayoutInflater.from(this).inflate(R.layout.cancel_button,null);

        cancelParams =getDefaultCancelParam();
        bubbleParams = bubbleDefaultParams();
        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        /*bubbleManager = new BubbleManager.Builder(this).build()
                .addCancelView(container_cancel,cancelParams);
        bubbleManager.show(container,bubbleParams);*/

        imageButton = (LinearLayout) container.findViewById(R.id.floating_image);
        recyclerView = (RecyclerView) detailButton.findViewById(R.id.list);
        cancelButton = (ImageView) container_cancel.findViewById(R.id.floating_cancel);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(new ProductSuggestionAdapter(this));

        imageButton.setOnTouchListener(this);
        detailButton.setOnClickListener(this);
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

                        if (!isDialog) {
                            //When user clicks on the image view of the collapsed layout,
                            //visibility of the collapsed layout will be changed to "View.GONE"
                            //and expanded view will become visible.
                            /*bubbleParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                            bubbleParams.width = WindowManager.LayoutParams.WRAP_CONTENT;*/
                            resetParams();
                            mWindowManager.addView(detailButton,detailsParams());
                        }else{
//                            bubbleParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
//                            bubbleParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                            resetParams();
                            mWindowManager.removeView(detailButton);
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
                    Log.v("ppp1", event.getRawX()+" "+event.getRawY());
                    if(isCancel) {
                        //bubbleManager.addCancelView(container_cancel,cancelParams);
                        mWindowManager.addView(container_cancel,cancelParams);
                        isCancel =false;
                    }
                    lastAction = event.getAction();
                    //Update the layout with new X & Y coordinate
                   // bubbleManager.updateBubbleView(container,bubbleParams);
                    mWindowManager.updateViewLayout(container,bubbleParams);

                    if(checkForOverlap()){

                        stopSelf();
                    }
                    return true;
            }
            return false;
    }

    private boolean checkForOverlap() {

        int[] outPos1=new int[2];
        int[] outPos2=new int[2];
        imageButton.getLocationOnScreen(outPos1);
        cancelButton.getLocationOnScreen(outPos2);
        Log.v("pppp",outPos1[0]+" "+outPos1[1]+" "+outPos2[0]+" "+outPos2[1]);
        if(outPos1[0]> outPos2[0] && outPos1[0]-outPos2[0]<=15 && outPos1[1]>outPos2[1]&& outPos1[1]-outPos2[1]<=15
                && (outPos1[0]+imageButton.getWidth())<=(outPos2[0]+cancelButton.getWidth())
                && (outPos1[1]+imageButton.getHeight())<=(outPos2[1]+cancelButton.getHeight())){
            Log.v("pppp","stop service");
            return true;
        }
        return false;
    }

    private WindowManager.LayoutParams getDefaultCancelParam(){
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
    private WindowManager.LayoutParams detailsParams(){

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.RGBA_8888);

        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.END;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 200;
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

        if (container_cancel != null) mWindowManager.removeView(container_cancel);
    }

    @Override
    public void onClick(View v) {

    }

}
