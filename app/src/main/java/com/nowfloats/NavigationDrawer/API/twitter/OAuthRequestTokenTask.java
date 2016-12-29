package com.nowfloats.NavigationDrawer.API.twitter;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import com.nowfloats.util.Constants;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;


public class OAuthRequestTokenTask extends AsyncTask<Void, Void, String> {

	final String TAG = getClass().getName();
	private Activity context;
	private OAuthProvider provider;
	private OAuthConsumer consumer;
	ProgressDialog pd;

	public OAuthRequestTokenTask(Activity context, OAuthConsumer consumer,OAuthProvider provider) {
		this.context = context;
		this.consumer = consumer;
		this.provider = provider;
	}
	@Override
	protected void onPreExecute() {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pd= ProgressDialog.show(context, null, "Taking you to twitter authentication...");
            }
        });
	}
	
	@Override
	protected void onPostExecute(String abc) {
        try {
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (pd != null)
                        pd.dismiss();
                }
            });
        }catch (Exception e){e.printStackTrace();}
	}
	@Override
	protected String doInBackground(Void... params) {

		try {
			provider.setOAuth10a(true);
			final String url = provider.retrieveRequestToken(consumer, Constants.OAUTH_CALLBACK_URL);
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url))
					.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
							| Intent.FLAG_ACTIVITY_NO_HISTORY
							| Intent.FLAG_FROM_BACKGROUND);
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}