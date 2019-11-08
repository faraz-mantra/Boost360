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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by Admin on 28-08-2017.
 */

public class FacebookHandler{

    private CallbackManager callbackManager;
    private FacebookCallbacks facebookCallbacks;

    public FacebookHandler(FacebookCallbacks callbacks,Context context){
        facebookCallbacks = callbacks;
        FacebookSdk.sdkInitialize(context.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }


    public void getFacebookPermissions(final Object viewReference, final List<String> readPermissions, final List<String> publishPermissions) {

        final LoginManager loginManager = LoginManager.getInstance();

        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Set<String> permissions = loginResult.getAccessToken().getPermissions();
                boolean readContain = permissions.containsAll(publishPermissions);
                boolean publishContain = permissions.containsAll(publishPermissions);
                // Log.v("ggg",contain+"permission"+loginResult.getAccessToken().getPermissions());
                if(!publishContain){
                    logInWithPublishPermissions(loginManager,viewReference, publishPermissions);
                }else{
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
                    facebookCallbacks.onAllPermissionNotGiven(permissions);
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
                   facebookCallbacks.onAllPermissionNotGiven(permissions);
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
    public void getFacebookProfile(final AccessToken accessToken) {
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email");
        GraphRequest meRequest = GraphRequest.newMeRequest(accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try
                        {
                            facebookCallbacks.onProfileConnected(response.getJSONObject(), accessToken);
                            getFacebookPages(accessToken);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        meRequest.setParameters(parameters);
        meRequest.executeAsync();
    }

    public void getFacebookPages(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newGraphPathRequest(
                accessToken,
                "/me/accounts",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        // Insert your code here
                        JSONArray pages = null;
                        ArrayList<String> pagesNameList = null;
                        try {
                            JSONObject pageMe = response.getJSONObject();
                            pages = pageMe.getJSONArray("data");
                            if (pages != null)
                            {
                                int size = pages.length();
                                pagesNameList = new ArrayList<String>(size);
                                for (int i = 0; i < size; i++) {
                                    pagesNameList.add(i, (String) ((JSONObject) pages.get(i)).get("name"));
                                }
                            }

                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }finally {
                            facebookCallbacks.onProfilePages(pages,pagesNameList);
                        }

                    }
                });

        request.executeAsync();
    }

    private void logInWithPublishPermissions(LoginManager manager, Object reference, List<String> permissions){
        if(reference instanceof Fragment){
            manager.logInWithPublishPermissions((Fragment) reference, permissions);
        }else if(reference instanceof androidx.fragment.app.Fragment){
            manager.logInWithPublishPermissions((androidx.fragment.app.Fragment) reference, permissions);
        }else if(reference instanceof Activity){
            manager.logInWithPublishPermissions((Activity) reference, permissions);
        }else{
            facebookCallbacks.onError();
        }

    }

    private void logInWithReadPermissions(LoginManager manager, Object reference, List<String> permissions){
        if(reference instanceof Fragment){
            manager.logInWithReadPermissions((Fragment) reference, permissions);
        }else if(reference instanceof androidx.fragment.app.Fragment){
            manager.logInWithReadPermissions((androidx.fragment.app.Fragment) reference, permissions);
        }else if(reference instanceof Activity){
            manager.logInWithReadPermissions((Activity) reference, permissions);
        }else{
            facebookCallbacks.onError();
        }

    }

    public interface FacebookCallbacks{
        void onError();
        void onCancel();
        void onAllPermissionNotGiven(Collection<String> givenPermissions);
        void onLoginSuccess(LoginResult result);
        void onProfilePages(JSONArray pages, ArrayList<String> pagesName);
        void onProfileConnected(JSONObject profile,AccessToken token);

    }
}
