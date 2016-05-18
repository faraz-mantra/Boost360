package com.nowfloats.Twitter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import com.thinksity.R;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class MainActivity extends AppCompatActivity implements ITwitterCallbacks, View.OnClickListener {


    //Variables are required to store twitter key and sec
    private String mConsumerKey = null;
    private String mConsumerSecret = null;
    private String mCallbackUrl = null;
    private String mAuthVerifier = null;
    private String mTwitterVerifier = null;
    private Twitter mTwitter = null;
    private RequestToken mRequestToken = null;
    private SharedPreferences mSharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //This is only for demo ,It will be removed in final version
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        //Remove it
        setContentView(R.layout.activity_main);
        initView();
        initTwitterSDK(this);
    }

    private void initView() {
        findViewById(R.id.login_button).setOnClickListener(this);
        findViewById(R.id.twitter_tweet).setOnClickListener(this);
        findViewById(R.id.twitter_logout).setOnClickListener(this);
    }

    //check about aleady authenticated
    protected boolean isAuthenticated() {
        return mSharedPreferences.getBoolean(TwitterConstants.PREF_KEY_TWITTER_LOGIN, false);
    }

    private void initTwitterSDK(Context context) { // it is fixed for user
        mConsumerKey = context.getResources().getString(R.string.twitter_consumer_key);
        mConsumerSecret = context.getResources().getString(R.string.twitter_consumer_secret);
        mAuthVerifier = "oauth_verifier";  // check whether this could be changed or not
        /*If key and secret key are null ,then it not possbile to communicate with twitter*/
        if (TextUtils.isEmpty(mConsumerKey) || TextUtils.isEmpty(mConsumerSecret)) {
            return;
        }
        mSharedPreferences = context.getSharedPreferences(TwitterConstants.PREF_NAME, context.MODE_PRIVATE);
        if (isAuthenticated()) {
            Toast.makeText(getApplicationContext(), "Already you are authorise to use", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "MainActivity else case for verification", Toast.LENGTH_SHORT).show();
            Uri uri = getIntent().getData();
            if (uri != null && uri.toString().startsWith(mCallbackUrl)) {
                String verifier = uri.getQueryParameter(mAuthVerifier);
                try {
                    AccessToken accessToken = mTwitter.getOAuthAccessToken(
                            mRequestToken, verifier);
                    saveTwitterInformation(accessToken);
                    Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                    Log.d("Failed to login ",
                            e.getMessage());
                }
            }
        }
    }

    private void saveTwitterInformation(AccessToken accessToken) {
        {
            long userID = accessToken.getUserId();
            User user;
            try {
                user = mTwitter.showUser(userID);
                String username = user.getName();
                SharedPreferences.Editor e = mSharedPreferences.edit();
                e.putString(TwitterConstants.PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
                e.putString(TwitterConstants.PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
                e.putBoolean(TwitterConstants.PREF_KEY_TWITTER_LOGIN, true);
                e.putString(TwitterConstants.PREF_USER_NAME, username);
                e.commit();

            } catch (TwitterException e1) {
                Log.d("Failed to Save", e1.getMessage());
            }
        }
    }
    public  void logoutFromTwitter() {
        SharedPreferences.Editor e = mSharedPreferences.edit();
        e.remove(TwitterConstants.PREF_KEY_OAUTH_TOKEN);
        e.remove(TwitterConstants.PREF_KEY_OAUTH_SECRET);
        e.remove(TwitterConstants.PREF_KEY_TWITTER_LOGIN);
        e.remove(TwitterConstants.PREF_USER_NAME);
        e.commit();
        CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }

    @Override
    public void startWebAuthentication(String url, RequestToken requestToken) {
        mRequestToken = requestToken;
        final Intent intent = new Intent(this,
                TwitterAuthenticationActivity.class);
        intent.putExtra(TwitterAuthenticationActivity.EXTRA_URL, url);
        startActivityForResult(intent, TwitterConstants.WEBVIEW_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null)
            mTwitterVerifier = data.getExtras().getString(mAuthVerifier);
        AccessToken accessToken;
        try {
            accessToken = mTwitter.getOAuthAccessToken(mRequestToken,mTwitterVerifier);
            long userID = accessToken.getUserId();
            final User user = mTwitter.showUser(userID);
            String username = user.getName();
            saveTwitterInformation(accessToken);
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Exception ", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void showAlertBox() {
        AlertDialog malertDialog = null;
        AlertDialog.Builder mdialogBuilder = null;
        if (mdialogBuilder == null) {
            mdialogBuilder = new AlertDialog.Builder(MainActivity.this);
            mdialogBuilder.setTitle("Alert");
            mdialogBuilder.setMessage("No Network");

            mdialogBuilder.setPositiveButton("Enable",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // launch setting Activity
                            startActivityForResult(new Intent(
                                            android.provider.Settings.ACTION_SETTINGS),
                                    0);
                        }
                    });

            mdialogBuilder.setNegativeButton(android.R.string.no,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setIcon(android.R.drawable.ic_dialog_alert);

            if (malertDialog == null) {
                malertDialog = mdialogBuilder.create();
                malertDialog.show();
            }

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                if (!Utils.isNetworkConnected(this)) {
                    showAlertBox();
                } else {
                    final ConfigurationBuilder builder = new ConfigurationBuilder();
                    builder.setOAuthConsumerKey(mConsumerKey);
                    builder.setOAuthConsumerSecret(mConsumerSecret);
                    final Configuration configuration = builder.build();
                    final TwitterFactory factory = new TwitterFactory(configuration);
                    mTwitter = factory.getInstance();
                    new TokenRequest(this).execute();
                }
                break;
            case R.id.twitter_tweet:
                if (!Utils.isNetworkConnected(this)) {
                    showAlertBox();
                }
                break;
            case R.id.twitter_logout:
                if (!Utils.isNetworkConnected(this)) {
                    showAlertBox();
                } else {
                    logoutFromTwitter();
                }
                break;
        }
    }
}
