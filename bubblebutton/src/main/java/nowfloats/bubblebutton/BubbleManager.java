package nowfloats.bubblebutton;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by Admin on 13-04-2017.
 */

public class BubbleManager {
    private static BubbleManager instance;
    private WindowManager.LayoutParams bubbleLayoutParams,cancelLayoutParams;
    private Context mContext;
    private WindowManager mWindowManager;

    private BubbleManager(Context context){
        mContext = context;
    }
    private static BubbleManager getInstance(Context context){
        if(instance == null){
            instance = new BubbleManager(context);
        }
        return instance;
    }
    public static class Builder{
        private BubbleManager manager;
        public Builder(Context context){
            manager = getInstance(context);;
        }
        public BubbleManager build(){
            return manager;
        }
    }
    public void show(View view,WindowManager.LayoutParams bubbleLayoutParams){
        addBubbleView(view,bubbleLayoutParams);
    }
    public BubbleManager setLayoutParam(WindowManager.LayoutParams param){
        bubbleLayoutParams = param;
        return this;
    }

    private BubbleManager addBubbleView(View view,WindowManager.LayoutParams bubbleLayoutParams ){
        if(this.bubbleLayoutParams == null){
            this.bubbleLayoutParams = bubbleLayoutParams;
        }
        getWindowManager().addView(view,bubbleLayoutParams);
       return instance;
    }
    public BubbleManager addCancelView(View view,WindowManager.LayoutParams cancelLayoutParams){
        if(this.cancelLayoutParams == null){
            this.cancelLayoutParams = cancelLayoutParams;
        }
        getWindowManager().addView(view,cancelLayoutParams);
        return instance;
    }

    public void removeView(View view){
        if(mWindowManager!=null){
            mWindowManager.removeView(view);
        }
    }

    public void updateBubbleView(View view,WindowManager.LayoutParams bubbleLayoutParams){
        if(mWindowManager!=null){
            mWindowManager.updateViewLayout(view,bubbleLayoutParams);
        }
    }
    private WindowManager getWindowManager(){
        if(mWindowManager == null){
            mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }





}
