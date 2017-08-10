package com.nowfloats.accessbility;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.thinksity.R;

/**
 * Created by Admin on 19-04-2017.
 */

public class SAMDialog extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_sam);
        initialize();
    }

    private void initialize() {

        TextView whatsApp = (TextView) findViewById(R.id.whatsapp_button);

    }
}
