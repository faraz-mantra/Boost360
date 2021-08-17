package com.nowfloats.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.util.Log;

import com.nowfloats.NavigationDrawer.Create_Message_Activity;
import com.nowfloats.PreSignUp.SplashScreen_Activity;
import com.thinksity.R;

import java.util.Arrays;

/**
 * Created by Admin on 11-01-2018.
 */

public class ShortcutHelper {

    private Context mContext;
    private ShortcutManager shortcutManager;
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1 && BuildConfig.APPLICATION_ID.equalsIgnoreCase("com.biz2.nowfloats")) {
            ShortcutHelper.get(this).publishShortcuts();
        }*/
    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    private  ShortcutHelper(Context context){
        mContext = context;
        shortcutManager = context.getSystemService(ShortcutManager.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public static ShortcutHelper get(Context context){
        return new ShortcutHelper(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public void showShortcuts(){
        if (shortcutManager.getDynamicShortcuts().size()>0){
            for (ShortcutInfo info :shortcutManager.getDynamicShortcuts()){
                Log.v("shortcuts",info.getId());
            }
        }else{
            publishShortcuts();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public void publishShortcuts(){
        Intent i = new Intent(mContext, SplashScreen_Activity.class);
        i.putExtra("url","update");
        i.putExtra("from","shortcut_app");
        i.setAction(Create_Message_Activity.SHORTCUT_ID);
        ShortcutInfo shortcut = new ShortcutInfo.Builder(mContext, "create_update")
                .setShortLabel("Add Update")
                .setLongLabel("Post an Update")
                .setIcon(Icon.createWithResource(mContext, R.drawable.ria))
                .setIntent(i)
                .build();

        shortcutManager.setDynamicShortcuts(Arrays.asList(shortcut));
    }

    public void updateShortcuts(){

    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public void removeShortcuts(){
        shortcutManager.removeAllDynamicShortcuts();
    }

}
