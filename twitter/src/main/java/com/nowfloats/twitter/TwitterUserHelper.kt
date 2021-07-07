package com.nowfloats.twitter

import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.models.User

interface TwitterUserHelper {

  fun getUserDetails(
    session: TwitterSession?,
    onResult: (Result<User>?, TwitterException?) -> Unit
  ) {
    TwitterApiClient(session).accountService.verifyCredentials(
      true, false,
      false
    ).enqueue(
      object : Callback<User>() {
        override fun success(result: Result<User>?) {
          onResult(result, null)
        }

        override fun failure(exception: TwitterException?) {
          onResult(null, exception)
        }
      })
  }

}