package com.nowfloats.Twitter;

import android.content.Intent;

import twitter4j.auth.RequestToken;

/**
 * Created by Boost Android on 07/05/2016.
 */
public interface ITwitterCallbacks {
    public void startWebAuthentication(String url, RequestToken mRequestToken);
    public void returnToken(Intent data);
}
