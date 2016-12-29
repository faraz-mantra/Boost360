package com.nowfloats.NavigationDrawer.API.twitter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.nowfloats.util.Constants;
import com.thinksity.R;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Boost Android on 06/05/2016.
 */
public class PostTweet extends AsyncTask<String, Integer, Void> {
    private Context mContext = null;
    private ProgressDialog mPostProgress;
    private String mConsumerKey = null;
    private String mConsumerSecret = null;

    public PostTweet(Context context){
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mPostProgress = new ProgressDialog(mContext);
        mPostProgress.setMessage("Loading...");
        mPostProgress.setCancelable(false);
        mPostProgress.show();
        mConsumerKey = mContext.getResources().getString(R.string.twitter_consumer_key);
        mConsumerSecret = mContext.getResources().getString(R.string.twitter_consumer_secret);
    }

    @Override
    protected Void doInBackground(String... params) {

        String status = params[0];
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(mConsumerKey);
        builder.setOAuthConsumerSecret(mConsumerSecret);

        SharedPreferences mSharedPreferences = null;
        mSharedPreferences =
                mContext.getSharedPreferences(Constants.PREF_NAME, 0);
        String access_token = mSharedPreferences.getString(
                Constants.PREF_KEY_OAUTH_TOKEN, "");
        String access_token_secret = mSharedPreferences.getString(
                Constants.PREF_KEY_OAUTH_SECRET, "");
        Log.d("Async", "Consumer Key in Post Process : "
                + access_token);
        Log.d("Async", "Consumer Secreat Key in post Process : "
                + access_token_secret);

        AccessToken accessToken = new AccessToken(access_token,
                access_token_secret);
        Twitter twitter = new TwitterFactory(builder.build())
                .getInstance(accessToken);
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
    private void closeProgress() {
        if (mPostProgress != null && mPostProgress.isShowing()) {
            mPostProgress.dismiss();
            mPostProgress = null;
        }
    }
}