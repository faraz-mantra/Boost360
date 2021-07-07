package com.nowfloats.signup.UI.UI;


import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.thinksity.R;


public class LoginAndSignUpFragment extends Fragment {

    private Button btnLogin, btnSignUp;
    private OnFragmentInteraction mFragmentInteraction;

    public LoginAndSignUpFragment() {
    }

    public static LoginAndSignUpFragment newInstance() {
        LoginAndSignUpFragment fragment = new LoginAndSignUpFragment();
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFragmentInteraction) {
            mFragmentInteraction = (OnFragmentInteraction) activity;
        } else {
            throw new RuntimeException("Interface not implemented");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login_and_sign_up, container, false);
        btnLogin = (Button) v.findViewById(R.id.btn_login);
        btnSignUp = (Button) v.findViewById(R.id.btn_signup);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentInteraction.OnInteraction("login");
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentInteraction.OnInteraction("signup");
            }
        });

        return v;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentInteraction {
        public void OnInteraction(String fragment);
    }
}
