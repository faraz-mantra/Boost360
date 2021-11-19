package com.framework.exoFullScreen

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SimpleExoPlayer

object MediaPlayer {

  var exoPlayer: ExoPlayer? = null

  fun initialize(context: Context?) {
    if (context != null)
      exoPlayer = ExoPlayer.Builder(context).build();
  }

  fun pausePlayer() {
    exoPlayer?.playWhenReady = false
    exoPlayer?.playbackState
  }

  fun startPlayer() {
    exoPlayer?.playWhenReady = true
    exoPlayer?.playbackState
  }

  fun stopPlayer() {
    exoPlayer?.stop()
  }

}