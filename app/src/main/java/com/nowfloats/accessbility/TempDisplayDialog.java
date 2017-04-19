package com.nowfloats.accessbility;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.thinksity.R;

/**
 * Created by NowFloats on 4/17/2017.
 */

public class TempDisplayDialog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent it = new Intent(TempDisplayDialog.this, BubbleDialog.class).
                setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(it);
//        overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        finish();
    }
}
