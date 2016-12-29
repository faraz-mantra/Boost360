package com.nowfloats.NavigationDrawer.API.twitter;


import com.nowfloats.util.Constants;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class TwitterUtils {

	public static boolean isAuthenticated(String tok,String sec) {

		try{
			AccessToken a = new AccessToken(tok, sec);
			Twitter twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
			twitter.setOAuthAccessToken(a);
				return true;
			} catch (Exception e) {
				return false;
			}
	}
}
