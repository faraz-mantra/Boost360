package com.nowfloats.NavigationDrawer.API;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.nowfloats.util.Constants;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

public class Post extends Activity {

	Facebook facebook = new Facebook(Constants.FACEBOOK_API_KEY);

	public static void statusUpdate(PostModel post, Facebook fb,String url, String mesgUrl) {


		if(post.Pages!=null && post.Pages.length()>0)
		{
			try{
				for (int i = 0; i < post.Pages.length(); i++)
				{
				     JSONObject childJSONObject = post.Pages.getJSONObject(i);
				     String id 		= childJSONObject.getString("id");
				     String ac_toc  = childJSONObject.getString("access_token");
				     fb.setAccessToken(ac_toc);
				     Thread.sleep(7000);
				     userStatusUpdate(post,fb,id,url,mesgUrl);

				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		else
		{
			userStatusUpdate(post,fb,"me",url,mesgUrl);
		}

	}

	public static void forFBandPagestatusUpdate(PostModel post, Facebook fb,String url, String mesgUrl) {


		if(post.Pages!=null && post.Pages.length()>0)
		{
			try{
				for (int i = 0; i < post.Pages.length(); i++)
				{
				     JSONObject childJSONObject = post.Pages.getJSONObject(i);
				     String id 		= childJSONObject.getString("id");
				     String ac_toc  = childJSONObject.getString("access_token");
				     fb.setAccessToken(ac_toc);
				     userStatusUpdate(post,fb,id,url,mesgUrl);

				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		else
		{

			userStatusUpdate(post,fb,"me",url,mesgUrl);
		}

	}


	public static void userStatusUpdate(PostModel post, Facebook fb,String id,String url,String mesgUrl)
	{

		AsyncFacebookRunner mAsyncFbRunner = new AsyncFacebookRunner(fb);
		Bundle params = new Bundle();
		//Log.e("NFMANAGE", fb.getAccessToken()+"   nowfloat's Access token");
		params.putString(post.NameKey, post.Message+System.getProperty("line.separator")+"- "+mesgUrl);
		String graphPath = id+"/photos";
		params.putString("url", url);
		

		mAsyncFbRunner.request(graphPath, params, "POST",new RequestListener() {
			
			@Override
			public void onMalformedURLException(MalformedURLException e, Object state) {
				// TODO Auto-generated method stub
				Log.e("NFMANAGE",e.getMessage());
				
			}
			
			@Override
			public void onIOException(IOException e, Object state) {
				// TODO Auto-generated method stub
				Log.e("NFMANAGE",e.getMessage());
			}
			
			@Override
			public void onFileNotFoundException(FileNotFoundException e, Object state) {
				// TODO Auto-generated method stub
				Log.e("NFMANAGE",e.getMessage());
			}
			
			@Override
			public void onFacebookError(FacebookError e, Object state) {
				// TODO Auto-generated method stub
				Log.e("NFMANAGE",e.getMessage());
			}
			
			@Override
			public void onComplete(String response, Object state) {
				// TODO Auto-generated method stub
				Log.e("NFMANAGE",response);
			}
		},null);

	
	}
	
	public static void reqlogout() {
		Post p = new Post();
		p.logout();
	}

	public void logout() {
		AsyncFacebookRunner mAsyncFbRunner = new AsyncFacebookRunner(facebook);
		mAsyncFbRunner.logout(getBaseContext(), new RequestListener() {

			@Override
			public void onComplete(String response, Object state) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onIOException(IOException e, Object state) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onFileNotFoundException(FileNotFoundException e,
					Object state) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onMalformedURLException(MalformedURLException e,
					Object state) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onFacebookError(FacebookError e, Object state) {
				// TODO Auto-generated method stub
				
			}
		});
	}

//	private class response extends BaseRequestListener {
//
//		@Override
//		public void onComplete(String response, Object state) {
//			// TODO Auto-generated method stub
//
//		}
//
//		@Override
//		public void onIOException(IOException e, Object state) {
//			// TODO Auto-generated method stub
//
//		}
//
//		@Override
//		public void onFileNotFoundException(FileNotFoundException e,
//				Object state) {
//			// TODO Auto-generated method stub
//
//		}
//
//		@Override
//		public void onMalformedURLException(MalformedURLException e,
//				Object state) {
//			// TODO Auto-generated method stub
//
//		}
//
//		@Override
//		public void onFacebookError(FacebookError e, Object state) {
//			// TODO Auto-generated method stub
//
//		}
//	}
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        facebook.authorizeCallback(requestCode, resultCode, data);
    }
}
