package com.nowfloats.NavigationDrawer.API.twitter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.nowfloats.NavigationDrawer.Create_Message_Activity;
import com.nowfloats.util.Constants;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;


public class PrepareRequestTokenActivity extends Activity {

	final String TAG = getClass().getName();
	String tweetData = "",color = null;
	private OAuthConsumer consumer;
	private OAuthProvider provider;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
				Bundle b = getIntent().getExtras();
				if(b!=null)
				{
					tweetData =  b.getString("tweetData");
					color = b.getString("sharingFrom");
				}
			this.consumer = new CommonsHttpOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
			this.provider = new CommonsHttpOAuthProvider(Constants.REQUEST_URL, Constants.ACCESS_URL, Constants.AUTHORIZE_URL);
		} 
		catch (Exception e) 
		{
			
		}
		new OAuthRequestTokenTask(PrepareRequestTokenActivity.this, consumer, provider).execute();
	}

	@Override
	public void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME,
				Activity.MODE_PRIVATE);
		final Uri uri = intent.getData();
		if ((uri.toString()).contains("denied")) {
			finish();
		} else if((uri.toString()).contains("oauth_token") && (uri.toString()).contains("oauth_verifier")){
			new RetrieveAccessTokenTask(this, consumer, provider, prefs)
					.execute(uri);
			
		}
	}

	public class RetrieveAccessTokenTask extends AsyncTask<Uri, Void, Void> {

		private Activity context;
		private OAuthProvider provider;
		private OAuthConsumer consumer;
		private SharedPreferences prefs;

		public RetrieveAccessTokenTask(Activity context, OAuthConsumer consumer,OAuthProvider provider,
				SharedPreferences prefs) {
			this.context = context;
			this.consumer = consumer;
			this.provider = provider;
			this.prefs = prefs;
		}

		@Override
		protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				if(context != null)
					context.finish();
		}

		@Override
		protected Void doInBackground(Uri... params) {
			final Uri uri = params[0];
			final String oauth_verifier = uri.getQueryParameter(OAuth.OAUTH_VERIFIER);

			try {
				provider.retrieveAccessToken(consumer, oauth_verifier);
				
				final Editor edit = prefs.edit();
				edit.putString(OAuth.OAUTH_TOKEN, consumer.getToken());
				edit.putString(OAuth.OAUTH_TOKEN_SECRET,
						consumer.getTokenSecret());
				AccessToken accessToken = new AccessToken(consumer.getToken(),
						consumer.getTokenSecret());
					
			          Twitter twitter = new TwitterFactory().getInstance();
			          twitter.setOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);

			          twitter.setOAuthAccessToken(accessToken);
			          long userID = accessToken.getUserId();
			          User user = twitter.showUser(userID);
			          String name = user.getScreenName();
			          edit.putString("TWITUName", name);
			          edit.putBoolean("twitterShareEnabled", true);
			          edit.commit();
                Create_Message_Activity.isFirstTimeTwitter = true;
				Constants.TWITTER_TOK = prefs.getString(OAuth.OAUTH_TOKEN, "");
				Constants.TWITTER_SEC = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");
				Constants.twitterShareEnabled = true;
				consumer.setTokenWithSecret(Constants.TWITTER_TOK, Constants.TWITTER_SEC);
				

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

}
