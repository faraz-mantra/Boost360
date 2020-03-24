package com.nowfloats.twitter

import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import com.twitter.sdk.android.core.identity.TwitterLoginButton

interface TwitterLoginHelper {

  fun getTwitterLoginButton(): TwitterLoginButton?

  fun onTwitterLoginSuccess(result: Result<TwitterSession>?)
  fun onTwitterLoginFailure(exception: TwitterException?)

  /***
   * @see <a href="https://github.com/twitter-archive/twitter-kit-android/wiki/Log-In-with-Twitter">Log In with Twitter</a>
   */
  fun registerTwitterLogin() {
    getTwitterLoginButton()?.callback = object : Callback<TwitterSession>() {
      override fun success(result: Result<TwitterSession>?) {
        onTwitterLoginSuccess(result)
      }

      override fun failure(exception: TwitterException?) {
        onTwitterLoginFailure(exception)
      }

    }
  }
}