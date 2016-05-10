package com.nowfloats.Twitter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.thinksity.R;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Boost Android on 07/05/2016.
 */
public class PostTweet extends AsyncTask<String, Integer, Void> {
    private final SharedPreferences mSharedPreferences;
    private Context mContext= null;
    private ProgressDialog mPostProgress;


    public PostTweet(Context context){
        mContext =context;
        mSharedPreferences= mContext.getSharedPreferences(TwitterConstants.PREF_NAME, Context.MODE_PRIVATE);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
       /* mPostProgress = new ProgressDialog(mContext);
        mPostProgress.setMessage("Loading...");
        mPostProgress.setCancelable(false);
        mPostProgress.show();*/
    }

    @Override
    protected Void doInBackground(String... params) {
        String status = params[0];
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(mContext.getResources().getString(R.string.twitter_consumer_key));
        builder.setOAuthConsumerSecret(mContext.getResources().getString(R.string.twitter_consumer_secret));

        //SharedPreferences mSharedPreferences = null;
        String access_token = mSharedPreferences.getString(TwitterConstants.PREF_KEY_OAUTH_TOKEN, "");
        String access_token_secret = mSharedPreferences.getString(TwitterConstants.PREF_KEY_OAUTH_SECRET, "");

        AccessToken accessToken = new AccessToken(access_token, access_token_secret);
        Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);
        try {
            if (status.length() < 139) {
                StatusUpdate statusUpdate = new StatusUpdate(status);
                twitter4j.Status response = twitter.updateStatus(statusUpdate);
                Log.d("Status", response.getText());
                //Toast.makeText(mContext, "Your tweet is posted", Toast.LENGTH_SHORT).show();
            }
        } catch (TwitterException e) {
           // Toast.makeText(mContext, "Posting probolm", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
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