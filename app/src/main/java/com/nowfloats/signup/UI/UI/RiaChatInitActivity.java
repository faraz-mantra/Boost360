package com.nowfloats.signup.UI.UI;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.nowfloats.riachatsdk.ChatManager;
import com.thinksity.R;

public class RiaChatInitActivity extends AppCompatActivity
        implements LoginAndSignUpFragment.OnFragmentInteraction {


    private SignUpWithRiaFragment signUpWithRiaFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ria_chat_init);

        SignUpWithRiaFragment fragment = SignUpWithRiaFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public void OnInteraction(String fragmentName) {
        switch (fragmentName) {
            case "signup":
               /* signUpWithRiaFragment = SignUpWithRiaFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, signUpWithRiaFragment)
                        .commit();*/
                signUpWithRiaFragment = null;
                finish();
                break;
            case "back":
                signUpWithRiaFragment = null;
                finish();
                /*LoginAndSignUpFragment loginAndSignUpFragment = LoginAndSignUpFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, loginAndSignUpFragment)
                        .commit();*/
                break;
            case "chatactivity":
                signUpWithRiaFragment = null;
                finish();
                overridePendingTransition(R.anim.ria_fade_in, R.anim.slide_out_up);
                ChatManager.getInstance(this).startChat();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (signUpWithRiaFragment != null) {
            signUpWithRiaFragment.onBackPressed();
        } else {
            super.onBackPressed();
            finish();
        }
    }
}
