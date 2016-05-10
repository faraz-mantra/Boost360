package com.nowfloats.Twitter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.nowfloats.BusinessProfile.UI.UI.Social_Sharing_Activity;
import com.thinksity.R;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by Boost Android on 07/05/2016.
 */
public class TokenRequest extends AsyncTask<Void, Void, String> {
    private ProgressDialog mPostProgress = null;
    private Context mContext = null;
    private SharedPreferences mSharedPreferences;
    private Twitter mTwitter;
    private RequestToken mRequestToken;
    private ITwitterCallbacks mTwitterCallbacks = null;

    public TokenRequest(Context context){
        mContext = context;
        mSharedPreferences= mContext.getSharedPreferences(TwitterConstants.PREF_NAME, Context.MODE_PRIVATE);
        mTwitterCallbacks= (Social_Sharing_Activity)mContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mPostProgress = new ProgressDialog(mContext);
        mPostProgress.setMessage("Redirecting to twitter, Please wait ...");
        mPostProgress.setCancelable(false);
        mPostProgress.show();
    }

    @Override
    protected String doInBackground(Void... voids) {

        boolean isLoggedIn = mSharedPreferences.getBoolean(TwitterConstants.PREF_KEY_TWITTER_LOGIN, false);
        String url=null;

        if (!isLoggedIn) {
            final ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(mContext.getResources().getString(R.string.twitter_consumer_key));
            builder.setOAuthConsumerSecret(mContext.getResources().getString(R.string.twitter_consumer_secret));
            final Configuration configuration = builder.build();
            final TwitterFactory factory = new TwitterFactory(configuration);
            mTwitter = factory.getInstance();
            try {
                mRequestToken = mTwitter.getOAuthRequestToken(null);
                url = mRequestToken.getAuthenticationURL();
            } catch (TwitterException e) {
                e.printStackTrace();
                Log.d("TOKEN_RAHUL", "FA");
            }

        }

        return url;
    }


    @Override
    protected void onPostExecute(String Result) {
        mTwitterCallbacks.startWebAuthentication(Result,mRequestToken);
        closeProgress();
    }
    private void closeProgress() {
        if (mPostProgress != null && mPostProgress.isShowing()) {
            mPostProgress.dismiss();
            mPostProgress = null;
        }
    }
}
