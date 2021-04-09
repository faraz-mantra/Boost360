package com.framework.exoFullScreen

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.framework.R
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

@SuppressLint("SourceLockedOrientationActivity")
fun SimpleExoPlayer.preparePlayer(playerView: PlayerView, context: AppCompatActivity, forceLandscape: Boolean = false): View? {
  try {
    val playerViewFullscreen = PlayerView(context)
    val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    playerViewFullscreen.layoutParams = layoutParams
    playerViewFullscreen.visibility = View.GONE
    playerViewFullscreen.setBackgroundColor(Color.BLACK)
    (playerView.rootView as ViewGroup).apply { addView(playerViewFullscreen, childCount) }
    val fullScreenButton: ImageView = playerView.findViewById(R.id.exo_fullscreen_icon)
    val normalScreenButton: ImageView = playerViewFullscreen.findViewById(R.id.exo_fullscreen_icon)
    fullScreenButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_fullscreen_open))
    normalScreenButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_fullscreen_close))
    fullScreenButton.setOnClickListener {
      context.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
      context.supportActionBar?.hide()
      if (forceLandscape)
        context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
      playerView.visibility = View.GONE
      playerViewFullscreen.visibility = View.VISIBLE
      PlayerView.switchTargetView(this@preparePlayer, playerView, playerViewFullscreen)
    }
    normalScreenButton.setOnClickListener {
      closeExoplayer(context, forceLandscape, playerView, playerViewFullscreen)
    }
    playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
    playerView.player = this@preparePlayer
    return normalScreenButton
  } catch (e: Exception) {
    e.printStackTrace()
  }
  return null
}

private fun SimpleExoPlayer.closeExoplayer(context: AppCompatActivity, forceLandscape: Boolean, playerView: PlayerView, playerViewFullscreen: PlayerView) {
  val normalScreenButton: ImageView = playerViewFullscreen.findViewById(R.id.exo_fullscreen_icon)
  context.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
  context.supportActionBar?.show()
  if (forceLandscape)
    context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
  normalScreenButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_fullscreen_close))
  playerView.visibility = View.VISIBLE
  playerViewFullscreen.visibility = View.GONE
  PlayerView.switchTargetView(this, playerViewFullscreen, playerView)
}

fun SimpleExoPlayer.setSource(playbackPosition: Long, context: Context, url: String) {
  val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(context, Util.getUserAgent(context, "app"))
  val videoSource: MediaSource =
      if (url.endsWith("m3u8") || url.endsWith("m3u"))
        HlsMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(url))
      else
        ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(url))
  this.prepare(videoSource)
  this.seekTo(playbackPosition)
  this.playWhenReady = true
}