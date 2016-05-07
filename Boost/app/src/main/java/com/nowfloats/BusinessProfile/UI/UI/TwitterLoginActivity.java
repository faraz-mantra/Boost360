package com.nowfloats.BusinessProfile.UI.UI;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nowfloats.util.Constants;
import com.nowfloats.util.Utils;
import com.thinksity.R;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterLoginActivity extends AppCompatActivity{
    public static final int WEBVIEW_REQUEST_CODE = 100;

    private ProgressDialog mPostProgress = null;
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
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_twitter_login);
        //boolean isLogin = getIntent().getBooleanExtra("Login",false);
        //initViews();
        initSDK();
        loginToTwitter();

    }

   /* private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().show();
    }*/


    public  void logoutFromTwitter() {
        SharedPreferences.Editor e = mSharedPreferences.edit();
        e.remove(Constants.PREF_KEY_OAUTH_TOKEN);
        e.remove(Constants.PREF_KEY_OAUTH_SECRET);
        e.remove(Constants.PREF_KEY_TWITTER_LOGIN);
        e.remove(Constants.PREF_USER_NAME);
        e.commit();

        CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();

      /*  mtweet_layout.setVisibility(View.GONE);
        mtwitter_login_layout.setVisibility(View.VISIBLE);
        twitter_logout_btn.setVisibility(View.GONE);*/
    }


    private void closeProgress() {
        if (mPostProgress != null && mPostProgress.isShowing()) {
            mPostProgress.dismiss();
            mPostProgress = null;
        }
    }

    ///TwitterAuthentication
    public void initSDK() {
        mConsumerKey = getResources().getString(R.string.twitter_consumer_key);
        mConsumerSecret = getResources().getString(R.string.twitter_consumer_secret);
        mAuthVerifier = "oauth_verifier";
        if (TextUtils.isEmpty(mConsumerKey)|| TextUtils.isEmpty(mConsumerSecret)) {
            return;
        }
        mSharedPreferences = getSharedPreferences(Constants.PREF_NAME, 0);
        if (isAuthenticated()) {
            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
            //hide login button here and show tweet
            //Rahul enable the twitter check box
        } else {
            //Rahul go for url verification
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

    protected boolean isAuthenticated() {
        return mSharedPreferences.getBoolean(Constants.PREF_KEY_TWITTER_LOGIN, false);
    }

    private void saveTwitterInformation(AccessToken accessToken) {
        long userID = accessToken.getUserId();
        User user;
        try {
            user = mTwitter.showUser(userID);
            String username = user.getName();
            SharedPreferences.Editor e = mSharedPreferences.edit();
            e.putString(Constants.PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
            e.putString(Constants.PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
            e.putBoolean(Constants.PREF_KEY_TWITTER_LOGIN, true);
            e.putString(Constants.PREF_USER_NAME, username);
            e.putBoolean("twitterShareEnabled",true);
            e.commit();

        } catch (TwitterException e1) {
            Log.d("Failed to Save", e1.getMessage());
        }
    }

    private void loginToTwitter() {
        new TokenRequest().execute();
    }/*{
        boolean isLoggedIn = mSharedPreferences.getBoolean(
                PREF_KEY_TWITTER_LOGIN, false);

        if (!isLoggedIn) {
            final ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(mConsumerKey);
            builder.setOAuthConsumerSecret(mConsumerSecret);

            final Configuration configuration = builder.build();
            final TwitterFactory factory = new TwitterFactory(configuration);
            mTwitter = factory.getInstance();
            try {
                mRequestToken = mTwitter.getOAuthRequestToken(mCallbackUrl);
                startWebAuthentication();
            } catch (TwitterException e) {
                e.printStackTrace();
                Log.d("FA", "FA");
            }
        }
    }*/

    protected void startWebAuthentication() {
        final Intent intent = new Intent(TwitterLoginActivity.this,
                TwitterAuthenticationActivity.class);
        intent.putExtra(TwitterAuthenticationActivity.EXTRA_URL,
                mRequestToken.getAuthenticationURL());
        startActivityForResult(intent, WEBVIEW_REQUEST_CODE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null)
            mTwitterVerifier = intent.getExtras().getString(mAuthVerifier);

        AccessToken accessToken;
        try {
            accessToken = mTwitter.getOAuthAccessToken(mRequestToken,
                    mTwitterVerifier);
            long userID = accessToken.getUserId();
            final User user = mTwitter.showUser(userID);
            String username = user.getName();
            saveTwitterInformation(accessToken);
        } catch (Exception e) {
            Toast.makeText(TwitterLoginActivity.this, "Exception ", Toast.LENGTH_SHORT).show();
        }finally {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null)
            mTwitterVerifier = data.getExtras().getString(mAuthVerifier);

        AccessToken accessToken;
        try {
            accessToken = mTwitter.getOAuthAccessToken(mRequestToken,
                    mTwitterVerifier);
            long userID = accessToken.getUserId();
            final User user = mTwitter.showUser(userID);
            String username = user.getName();
            saveTwitterInformation(accessToken);
        } catch (Exception e) {
            Toast.makeText(TwitterLoginActivity.this, "Exception ", Toast.LENGTH_SHORT).show();
        }finally {
            finish();
        }
    }

    private void showAlertBox() {

        AlertDialog malertDialog = null;
        AlertDialog.Builder mdialogBuilder = null;
        if (mdialogBuilder == null) {
            mdialogBuilder = new AlertDialog.Builder(TwitterLoginActivity.this);

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

    class TokenRequest extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mPostProgress = new ProgressDialog(TwitterLoginActivity.this);
            mPostProgress.setMessage("Redirecting to twitter, Please wait ...");
            mPostProgress.setCancelable(false);
            mPostProgress.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            boolean isLoggedIn = mSharedPreferences.getBoolean(
                    Constants.PREF_KEY_TWITTER_LOGIN, false);

            if (!isLoggedIn) {
                final ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey(mConsumerKey);
                builder.setOAuthConsumerSecret(mConsumerSecret);

                final Configuration configuration = builder.build();
                final TwitterFactory factory = new TwitterFactory(configuration);
                mTwitter = factory.getInstance();
                try {
                    mRequestToken = mTwitter.getOAuthRequestToken(mCallbackUrl);
                } catch (TwitterException e) {
                    e.printStackTrace();
                    Log.d("FA", "FA");
                }
                startWebAuthentication();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void Result) {
           // mEditText.setText("");
            closeProgress();
        }
    }

    public class PostTweet extends AsyncTask<String, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mPostProgress = new ProgressDialog(TwitterLoginActivity.this);
            mPostProgress.setMessage("Loading...");
            mPostProgress.setCancelable(false);
            mPostProgress.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            String status = params[0];
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(mConsumerKey);
            builder.setOAuthConsumerSecret(mConsumerSecret);

            SharedPreferences mSharedPreferences = null;
            mSharedPreferences =getSharedPreferences(Constants.PREF_NAME, 0);
            String access_token = mSharedPreferences.getString(Constants.PREF_KEY_OAUTH_TOKEN, "");
            String access_token_secret = mSharedPreferences.getString(Constants.PREF_KEY_OAUTH_SECRET, "");
            Log.d("Async", "Consumer Key in Post Process : " + access_token);
            Log.d("Async", "Consumer Secreat Key in post Process : "+ access_token_secret);

            AccessToken accessToken = new AccessToken(access_token, access_token_secret);
            Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);
            try {
                if (status.length() < 139) {
                    StatusUpdate statusUpdate = new StatusUpdate(status);
                    twitter4j.Status response = twitter.updateStatus(statusUpdate);
                    Log.d("Status", response.getText());
                }
            } catch (TwitterException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void Result) {
            closeProgress();
        }
    }
}