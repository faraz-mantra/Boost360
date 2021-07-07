package com.nowfloats.accessbility;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.nowfloats.util.Constants;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

/**
 * Created by Admin on 19-04-2017.
 */

public class BubbleInAppDialog extends AppCompatActivity {
    public static final String ACTION_KILL_DIALOG = "nowfloats.bubblebutton.bubble.ACTION_KILL_DIALOG";
    private KillListener killListener;
    private Button permissionBtn;
    private SharedPreferences pref = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getSharedPreferences(Constants.PREF_NAME, Activity.MODE_PRIVATE);
        setContentView(R.layout.dialog_in_app_bubble);
        /*if (pref.getBoolean(Key_Preferences.HAS_BUBBLE_SHARE_PRODUCTS, false)) {
            setContentView(R.layout.dialog_in_app_bubble);
        } else {
            setContentView(R.layout.dialog_in_app_add_products);
        }*/
        MixPanelController.track(MixPanelController.BUBBLE_IN_APP_DIALOG, null);
        initialize();
    }

    private void initialize() {

        killListener = new KillListener();
        getWindow().setGravity(Gravity.CENTER_VERTICAL);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);


        permissionBtn = (Button) findViewById(R.id.permission_btn);
        permissionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (pref.getBoolean(Key_Preferences.HAS_BUBBLE_SHARE_PRODUCTS, false)) {
                MixPanelController.track(MixPanelController.BUBBLE_IN_APP_DIALOG_CLICKED, null);
                showCustomToastView();
                Intent i = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(i);
                finish();
                //} else {
                   /* startActivity(new Intent(BubbleInAppDialog.this,ProductGalleryActivity.class));
                    stopService(new Intent(BubbleInAppDialog.this, BubblesService.class));
                    finish();*/
                // }
            }
        });
    }

    private void showCustomToastView() {

        Toast mToast = new Toast(BubbleInAppDialog.this);
        mToast.setView(getLayoutInflater().inflate(R.layout.accessbility_toast, null));
        mToast.setDuration(Toast.LENGTH_LONG);
        mToast.setGravity(Gravity.CENTER_VERTICAL, getResources().getDisplayMetrics().widthPixels, 0);
        mToast.show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_KILL_DIALOG);
        registerReceiver(killListener, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(killListener);
    }

    private class KillListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    }
}
