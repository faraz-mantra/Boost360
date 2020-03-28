package com.nowfloats.twitter

import android.app.Activity
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import com.twitter.sdk.android.core.identity.TwitterAuthClient
import com.twitter.sdk.android.core.identity.TwitterLoginButton

interface TwitterLoginHelper {

  fun onTwitterLoginSuccess(result: Result<TwitterSession>?)
  fun onTwitterLoginFailure(exception: TwitterException?)

  /***
   * @see <a href="https://github.com/twitter-archive/twitter-kit-android/wiki/Log-In-with-Twitter">Log In with Twitter</a>
   */
  fun loginWithTwitter(activity: Activity, authClient: TwitterAuthClient) {
    authClient.authorize(activity, object : Callback<TwitterSession>() {
      override fun success(result: Result<TwitterSession>?) {
        onTwitterLoginSuccess(result)
      }

      override fun failure(exception: TwitterException?) {
        onTwitterLoginFailure(exception)
      }

    })
  }
}