package com.nowfloats.signup.UI.UI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.thinksity.R;

public class RiaChatInitActivity extends AppCompatActivity
        implements LoginAndSignUpFragment.OnFragmentInteraction{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ria_chat_init);

        LoginAndSignUpFragment fragment = LoginAndSignUpFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public void OnInteraction(String fragmentName) {
        switch (fragmentName){
            case "signup":
                SignUpWithRiaFragment fragment = SignUpWithRiaFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
        }
    }
}
