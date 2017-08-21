package com.nowfloats.twitter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.nowfloats.util.Constants;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

/**
 * Created by Admin on 31-05-2017.
 */

public class TwitterConnection {
    public final static String PREF_NAME = "NFBoostTwitterPref";
    public static final String PREF_KEY_TWITTER_LOGIN = "is_twitter_loggedin";
    public static final int WEBVIEW_REQUEST_CODE = 100;
    public static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    public static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    public static final String PREF_USER_NAME = "twitter_user_name";
    private Context mContext;

    private TwitterResult mTwitterResult;


    private TwitterAuthClient client;

    public TwitterConnection(Context context, TwitterResult twitterResult){
        TwitterConfig config;
        config = new TwitterConfig.Builder(context)
                .twitterAuthConfig(new TwitterAuthConfig(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET))
                .debug(!Constants.APK_MODE_RELEASE)
                .build();
        Twitter.initialize(config);
        mContext = context;
        mTwitterResult = twitterResult;

    }
    
    public void authorize(){
        client = new TwitterAuthClient();
        client.authorize((Activity)mContext, new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                mTwitterResult.onTwitterConnected(result);
            }

            @Override
            public void failure(TwitterException e) {
                mTwitterResult.onTwitterConnected(null);
                e.printStackTrace();
            }
        });

    }

    public interface TwitterResult{
        void onTwitterConnected(Result<TwitterSession> result);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        client.onActivityResult(requestCode,resultCode,data);
    }
}
