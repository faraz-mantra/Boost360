package com.nowfloats.socialConnect;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Set;

/**
 * Created by Admin on 28-08-2017.
 */

public class FacebookHandler{

    private CallbackManager callbackManager;
    private FacebookCallbacks facebookCallbacks;

    public FacebookHandler(Context context){
        facebookCallbacks = (FacebookCallbacks) context;
        FacebookSdk.sdkInitialize(context.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }


    public void getFacebookPermissions(final Object viewReference, List<String> readPermissions, final List<String> publishPermissions) {

        final LoginManager loginManager = LoginManager.getInstance();

        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Set<String> permissions = loginResult.getAccessToken().getPermissions();
                boolean contain = permissions.containsAll(publishPermissions);
                // Log.v("ggg",contain+"permission"+loginResult.getAccessToken().getPermissions());
                if(!contain){
                    logInWithPublishPermissions(loginManager,viewReference, publishPermissions);
                }else {
                    facebookCallbacks.onLoginSuccess(loginResult);
                }
            }

            @Override
            public void onCancel() {
                facebookCallbacks.onCancel();
                LoginManager.getInstance().logOut();
                com.facebook.AccessToken.refreshCurrentAccessTokenAsync();
            }

            @Override
            public void onError(FacebookException error) {
                facebookCallbacks.onError();
                LoginManager.getInstance().logOut();
                com.facebook.AccessToken.refreshCurrentAccessTokenAsync();
            }
        });
        logInWithReadPermissions(loginManager,viewReference, readPermissions);
    }
    public void getFacebookReadPermissions(final Object viewReference, final List<String> readPermissions) {

        final LoginManager loginManager = LoginManager.getInstance();

        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Set<String> permissions = loginResult.getAccessToken().getPermissions();
                boolean contain = permissions.containsAll(readPermissions);
                // Log.v("ggg",contain+"permission"+loginResult.getAccessToken().getPermissions());
                if(contain){
                    facebookCallbacks.onLoginSuccess(loginResult);
                }else{
                    // all permission not given
                }


            }

            @Override
            public void onCancel() {
                facebookCallbacks.onCancel();
                LoginManager.getInstance().logOut();
                com.facebook.AccessToken.refreshCurrentAccessTokenAsync();
            }

            @Override
            public void onError(FacebookException error) {
                facebookCallbacks.onError();
                LoginManager.getInstance().logOut();
                com.facebook.AccessToken.refreshCurrentAccessTokenAsync();
            }
        });
        logInWithReadPermissions(loginManager,viewReference, readPermissions);
    }

    public void getFacebookWritePermissions(final Object viewReference, final List<String> writePermission) {

        final LoginManager loginManager = LoginManager.getInstance();

        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Set<String> permissions = loginResult.getAccessToken().getPermissions();
                boolean contain = permissions.containsAll(writePermission);
                // Log.v("ggg",contain+"permission"+loginResult.getAccessToken().getPermissions());
                if(contain){
                    facebookCallbacks.onLoginSuccess(loginResult);
                }else{
                    // all permission not given
                }


            }

            @Override
            public void onCancel() {
                facebookCallbacks.onCancel();
                LoginManager.getInstance().logOut();
                com.facebook.AccessToken.refreshCurrentAccessTokenAsync();
            }

            @Override
            public void onError(FacebookException error) {
                facebookCallbacks.onError();
                LoginManager.getInstance().logOut();
                com.facebook.AccessToken.refreshCurrentAccessTokenAsync();
            }
        });
        logInWithPublishPermissions(loginManager,viewReference, writePermission);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        callbackManager.onActivityResult(requestCode,resultCode,intent);
    }
    private void getFacebookProfile(final AccessToken accessToken) {
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email");
        GraphRequest meRequest = GraphRequest.newMeRequest(accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try
                        {
                            facebookCallbacks.onProfileConnected(response.getJSONObject());
                            getFacebookPages(accessToken);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        meRequest.setParameters(parameters);
        meRequest.executeAsync();
    }

    private void getFacebookPages(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newGraphPathRequest(
                accessToken,
                "/me/accounts",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        // Insert your code here
                        JSONArray pages = null;
                        try {
                            JSONObject pageMe = response.getJSONObject();
                            pages = pageMe.getJSONArray("data");

                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        facebookCallbacks.onPageConnected(pages);
                    }
                });

        request.executeAsync();
    }

    private void logInWithPublishPermissions(LoginManager manager, Object refrence, List<String> permissions){
        if(refrence instanceof Fragment){
            manager.logInWithPublishPermissions((Fragment) refrence, permissions);
        }else if(refrence instanceof android.support.v4.app.Fragment){
            manager.logInWithPublishPermissions((android.support.v4.app.Fragment) refrence, permissions);
        }else if(refrence instanceof Activity){
            manager.logInWithPublishPermissions((Activity) refrence, permissions);
        }else{
            facebookCallbacks.onError();
        }

    }

    private void logInWithReadPermissions(LoginManager manager, Object refrence, List<String> permissions){
        if(refrence instanceof Fragment){
            manager.logInWithReadPermissions((Fragment) refrence, permissions);
        }else if(refrence instanceof android.support.v4.app.Fragment){
            manager.logInWithReadPermissions((android.support.v4.app.Fragment) refrence, permissions);
        }else if(refrence instanceof Activity){
            manager.logInWithReadPermissions((Activity) refrence, permissions);
        }else{
            facebookCallbacks.onError();
        }

    }

    public interface FacebookCallbacks{
        void onError();
        void onCancel();
        void onLoginSuccess(LoginResult result);
        void onPageConnected(JSONArray pages);
        void onProfileConnected(JSONObject profile);
    }
}
