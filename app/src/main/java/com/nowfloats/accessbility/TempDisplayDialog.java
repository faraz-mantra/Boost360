package com.nowfloats.accessbility;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.nowfloats.bubble.BubblesService;
import com.nowfloats.customerassistant.SuggestionsActivity;
import com.nowfloats.util.Key_Preferences;
import com.thinksity.R;


/**
 * Created by NowFloats on 4/17/2017.
 */

public class TempDisplayDialog extends AppCompatActivity {

    public final static String IN_APP_DIALOG = "com.nowfloats.accessbility.BubbleInAppDialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BubblesService.FROM from = (BubblesService.FROM) getIntent().getExtras().get(Key_Preferences.DIALOG_FROM);
        Intent it = null;
        switch (from) {
            case WHATSAPP:
                it = new Intent(TempDisplayDialog.this, BubbleDialog.class).
                        setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                break;
            case HOME_ACTIVITY:
                it = new Intent(TempDisplayDialog.this, BubbleInAppDialog.class);

                break;
            case LAUNCHER_HOME_ACTIVITY:
                it = new Intent(TempDisplayDialog.this, SuggestionsActivity.class).
                        setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                break;
            case WHATSAPP_DIALOG:
                break;
        }
        startActivity(it);
//        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
        overridePendingTransition(R.anim.bubble_scale_up, R.anim.bubble_scale_down);
        finish();
    }
}
