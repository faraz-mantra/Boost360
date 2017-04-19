package com.nowfloats.accessbility;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.nowfloats.bubble.BubblesService;
import com.thinksity.R;

/**
 * Created by Admin on 19-04-2017.
 */

public class WhatsAppDialog extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_show_whats_app);
        initialize();
    }
    private void initialize() {
        getWindow().setGravity(Gravity.CENTER_VERTICAL);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        TextView whatsApp = (TextView) findViewById(R.id.whatsapp_button);
        whatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(WhatsAppDialog.this, BubblesService.class));
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.whatsapp");
                startActivity(launchIntent);
                finish();
            }
        });
    }
}
