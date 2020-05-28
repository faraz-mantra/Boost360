package com.nowfloats.PreSignUp;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.nowfloats.Login.Login_MainActivity;
import com.nowfloats.util.EventKeysWL;
import com.nowfloats.util.MixPanelController;
import com.thinksity.R;

public class KitsunePreSignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitsune_pre_sign_up);

        findViewById(R.id.ll_kitsune_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MixPanelController.track(EventKeysWL.LOGIN_BUTTON, null);
                Intent intent = new Intent(KitsunePreSignUpActivity.this, Login_MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }
}
