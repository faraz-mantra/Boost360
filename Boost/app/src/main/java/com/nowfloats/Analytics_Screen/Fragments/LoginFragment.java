package com.nowfloats.Analytics_Screen.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.thinksity.R;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Abhi on 11/25/2016.
 */

public class LoginFragment extends Fragment {
    private CallbackManager callbackManager;

    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private List<String> readPermission=Arrays.asList("user_friends","read_insights","pages_show_list");
    private List<String> publishPermission=Arrays.asList("manage_pages","publish_pages","publish_actions");
    private Context context;

    public static Fragment getInstance(int i){
        LoginFragment frag=new LoginFragment();
        Bundle bundle=new Bundle();
        bundle.putInt("LoginText",i);
        frag.setArguments(bundle);
        return frag;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_facebook_connect, container, false);
    }

    private int repeat=0;
    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();
            displayMessage(accessToken.getToken()+" "+ accessToken.getExpires()+" "+accessToken.getPermissions());
            boolean read=accessToken.getPermissions().containsAll(readPermission);
            boolean publish=accessToken.getPermissions().containsAll(publishPermission);
            if(!read || !publish)
            {
                if(!read)
                {
                    LoginManager manager = LoginManager.getInstance();
                    manager.logInWithReadPermissions(LoginFragment.this, readPermission);
                    manager.registerCallback(callbackManager, callback);
                }
                else{
                    LoginManager manager = LoginManager.getInstance();
                    manager.logInWithPublishPermissions(LoginFragment.this, publishPermission);
                    manager.registerCallback(callbackManager, callback);
                }
            }
        }
        @Override
        public void onCancel()
        {
            //show dialog these are require permission
            displayMessage("cancel");
        }

        @Override
        public void onError(FacebookException e) {
            e.printStackTrace();
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        Log.v("ggg","dfdfdf");
        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker= new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                if(newToken!=null)
                Log.v("ggg","new "+newToken.getToken());
                if(oldToken!=null)
                Log.v("ggg"," old "+oldToken.getToken());
            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                //Log.v("ggg",newToken.getToken()+" old"+newProfile.getToken());
            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button button= (Button) view.findViewById(R.id.facebook_login);
        TextView message= (TextView) view.findViewById(R.id.facebook_analytics_connect_text1);
        if(getArguments()!=null && getArguments().getInt("LoginText")==2){
            message.setText(getResources().getString(R.string.session_expired));
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager manager=LoginManager.getInstance();
                manager.logInWithReadPermissions(LoginFragment.this,readPermission );
                manager.registerCallback(callbackManager,callback);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    private void displayMessage(String  profile){
        if(profile != null){
            Log.v("ggg",profile);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }
    public interface facebookLoggedIn{
        void fetch(String token);
    }
}
