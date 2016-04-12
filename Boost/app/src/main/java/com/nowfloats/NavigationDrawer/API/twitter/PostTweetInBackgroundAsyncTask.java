package com.nowfloats.NavigationDrawer.API.twitter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.nowfloats.Login.UserSessionManager;
import com.nowfloats.test.com.nowfloatsui.buisness.util.Util;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Key_Preferences;

import oauth.signpost.OAuth;


public final class PostTweetInBackgroundAsyncTask extends
		AsyncTask<Void, String, String> {

	private Activity appContext = null;
	ProgressDialog pd = null;
	String Username = null, Password = null;
	private SharedPreferences prefs = null;
	String responseMessage = "";
	Boolean success = false;
	String clientIdConcatedWithQoutes = "\"" + Constants.clientId + "\"";
	int size = 0;
	SharedPreferences.Editor prefsEditor;
	private String token,secretToken;
	String shareText = "";
	String id = "";
    UserSessionManager session;
	public PostTweetInBackgroundAsyncTask(Activity context, String shareText,String id,UserSessionManager session) {
		this.appContext = context;
		this.shareText 	= shareText;
		this.id 		= id;
        this.session = session;
		prefs 								= appContext.getSharedPreferences(Constants.PREF_NAME,Activity.MODE_PRIVATE);
	}

	public PostTweetInBackgroundAsyncTask(Activity context,UserSessionManager session) {
		this.appContext = context;
		prefs 								= appContext.getSharedPreferences(Constants.PREF_NAME,Activity.MODE_PRIVATE);
        this.session = session;
	}

	@Override
	protected void onPreExecute() {

	}

	@Override
	protected void onPostExecute(String result) {

	}
	@Override
	protected String doInBackground(Void... params) {
		String response = "",tweetMessage = "";
		String shortUrl = Util.shortUrl(session.getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG) + ".nowfloats.com/bizFloat/" + id);
		if (!Util.isNullOrEmpty(shareText) && !Util.isNullOrEmpty(shortUrl)){
			int len = shareText.length();

			int mlen = 140-(shortUrl.length()+8);
			int tlen = Math.min(len, mlen);
			String separator = " ... ";
			if(tlen!=mlen)
			{
				separator = " - ";
			}
			tweetMessage = shareText.substring(0, tlen)+separator+shortUrl;
		}
		else
		{
			tweetMessage = "Checkout ... "+shortUrl;
		}
		
		if(!Util.isNullOrEmpty(tweetMessage)){
			try {
				if(prefs != null ){
					Constants.TWITTER_TOK 				= prefs.getString(OAuth.OAUTH_TOKEN, "");
					Constants.TWITTER_SEC 				= prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");
				}
				if(!Util.isNullOrEmpty(Constants.TWITTER_TOK) && !Util.isNullOrEmpty(Constants.TWITTER_SEC))
					TwitterShare.sendTweet(Constants.TWITTER_TOK, Constants.TWITTER_SEC, tweetMessage);
				else
					Log.d("ILUD TOKEN", "Token seems to be null");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	
}
