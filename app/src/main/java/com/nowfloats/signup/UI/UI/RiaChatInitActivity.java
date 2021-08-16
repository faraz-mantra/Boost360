package com.nowfloats.signup.UI.UI;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

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
                startActivity(new Intent(RiaChatInitActivity.this, PreSignUpActivity.class));
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
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
//                ChatManager.getInstance(this).startChat(ChatManager.ChatType.CREATE_WEBSITE);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (signUpWithRiaFragment != null) {
            signUpWithRiaFragment.onBackPressed();
        } else {
            startActivity(new Intent(RiaChatInitActivity.this, PreSignUpActivity.class));
            finish();
            super.onBackPressed();
        }
    }
}
