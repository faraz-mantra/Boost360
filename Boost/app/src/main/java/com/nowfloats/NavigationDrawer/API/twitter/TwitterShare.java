package com.nowfloats.NavigationDrawer.API.twitter;

import com.nowfloats.util.Constants;

import java.io.File;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class TwitterShare {

	public static void sendTweet(String token, String secretToken, String tweetMessage)
			throws Exception {
		AccessToken a = new AccessToken(token, secretToken);
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(Constants.CONSUMER_KEY,
				Constants.CONSUMER_SECRET);
		twitter.setOAuthAccessToken(a);
		twitter.updateStatus(tweetMessage);
	}
	
	public static void sendImageTweet(String token, String secretToken, String tweetMessage,String path,boolean flag)
			throws Exception {
		AccessToken a = new AccessToken(token, secretToken);
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(Constants.CONSUMER_KEY,
				Constants.CONSUMER_SECRET);
		twitter.setOAuthAccessToken(a);
		
		//twitter.updateStatus(tweetMessage);
		StatusUpdate update = new StatusUpdate(tweetMessage);
		if(flag)
			{	File file = new File(path);
				update.setMedia(file);
			}
		twitter.updateStatus(update);
	}
}
