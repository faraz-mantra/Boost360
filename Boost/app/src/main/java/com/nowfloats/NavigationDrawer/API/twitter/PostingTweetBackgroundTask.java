package com.nowfloats.NavigationDrawer.API.twitter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nowfloats.Login.UserSessionManager;

import oauth.signpost.OAuth;

public class PostingTweetBackgroundTask extends AsyncTask<Void, Void, String> {

	final String TAG = getClass().getName();
	private Activity context;
	private String token,secretToken,tweetMessage;
	ProgressBar pb;
	TextView tv;
	RelativeLayout submit_click,submit,shareScreen,saveScreeen;
	private SharedPreferences prefs;
	UserSessionManager session;
	public PostingTweetBackgroundTask(Activity context,String tweetMessage,ProgressBar pb,
									   TextView tv,RelativeLayout submit_click,RelativeLayout submit,RelativeLayout shareScreen,RelativeLayout saveScreeen) {
		this.context 		= context;
		this.prefs 			= PreferenceManager.getDefaultSharedPreferences(context);
		this.token  		= prefs.getString(OAuth.OAUTH_TOKEN, "");;
		this.secretToken 	= prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");
		this.tweetMessage 	= tweetMessage;
		this.pb				= pb;
		this.tv 			= tv;
		this.submit_click	= submit_click;
		this.submit			= submit;
		this.shareScreen	= shareScreen;
		this.saveScreeen	= saveScreeen;
        session = new UserSessionManager(context,context);
	}

	public PostingTweetBackgroundTask(Activity context, String tweetMessage) {
		this.context = context;
		this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
		this.token = prefs.getString(OAuth.OAUTH_TOKEN, "");
		this.secretToken = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");
		this.tweetMessage = tweetMessage;
        session = new UserSessionManager(context,context);
	}
	@Override
	protected void onPreExecute() {
		if (submit_click != null)
			submit_click.setClickable(false);
		if (submit != null)
			submit.setVisibility(View.GONE);
		if (pb != null)
			pb.setVisibility(View.VISIBLE);
		if (tv != null)
			tv.setVisibility(View.VISIBLE);
	}
	
	@Override
	protected void onPostExecute(String abc) {
		if (submit_click != null)
			submit_click.setClickable(true);
		if (submit != null)
			submit.setVisibility(View.VISIBLE);
		if (pb != null)
			pb.setVisibility(View.GONE);
		if (tv != null)
			tv.setVisibility(View.GONE);
		//Constants.shortUrl = "";
		try{
			if(session.getFPID() == null){
				if(shareScreen != null)
					shareScreen.setVisibility(View.GONE);
				if(saveScreeen != null)
					saveScreeen.setVisibility(View.VISIBLE);
			}else{
				/*if(Constants.app != null)
					Toast.makeText(Constants.app, "Successfully posted the tweet!", 5000).show();
				context.finish();*/
			}
		}catch (Exception e) {
			
		}
		
	}
	@Override
	protected String doInBackground(Void... params) {

		try {
			 TwitterShare.sendTweet(token, secretToken, tweetMessage);
			} catch (Exception e) {
					e.printStackTrace();
		}

		return null;
	}

}