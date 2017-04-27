package com.nowfloats.accessbility;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.nowfloats.bubble.BubblesService;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

/**
 * Created by Admin on 26-04-2017.
 */

public class WhatsAppBubbleCloseDialog extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_whatsapp_bubble_close);
        initialize();
    }
    private void initialize() {
        getWindow().setGravity(Gravity.CENTER_VERTICAL);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        TextView whatsApp = (TextView) findViewById(R.id.boost_button);
        whatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(WhatsAppBubbleCloseDialog.this, BubblesService.class));
                MixPanelController.track(MixPanelController.WHATSAPP_TO_BOOST,null);
                try {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage(DataAccessbilityServiceV2.PK_NAME_NOWFLOATS);
                    startActivity(launchIntent);
                }catch(Exception e){
                    e.printStackTrace();
                    Toast.makeText(WhatsAppBubbleCloseDialog.this, "Problem to open Boost", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }
}
