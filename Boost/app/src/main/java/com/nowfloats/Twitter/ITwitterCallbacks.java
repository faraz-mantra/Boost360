package com.nowfloats.Twitter;

import twitter4j.auth.RequestToken;

/**
 * Created by Boost Android on 07/05/2016.
 */
public interface ITwitterCallbacks {
    public void startWebAuthentication(String url, RequestToken mRequestToken);
}
