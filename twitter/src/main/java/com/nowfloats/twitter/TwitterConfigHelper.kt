package com.nowfloats.twitter

import android.content.Context
import android.util.Log
import com.twitter.sdk.android.core.DefaultLogger
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterConfig


object TwitterConfigHelper {

  var debug = false

  fun initialize(context: Context) {
    val config = TwitterConfig.Builder(context)
      .logger(DefaultLogger(Log.DEBUG))
      .twitterAuthConfig(
        TwitterAuthConfig(
          context.getString(R.string.twitter_consumer_key),
          context.getString(R.string.twitter_consumer_secret)
        )
      )
      .debug(debug)
      .build()
    Twitter.initialize(config)
  }

  fun debug(enable: Boolean) {
    this.debug = enable
  }

}