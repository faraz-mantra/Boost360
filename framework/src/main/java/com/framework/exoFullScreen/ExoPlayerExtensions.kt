package com.framework.exoFullScreen

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.framework.R
import com.framework.utils.getNavigationBarHeight
import com.google.android.exoplayer2.C
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
    fullScreenButton.setOnClickListener { fullScreenExoPlayer(context, forceLandscape, playerView, playerViewFullscreen) }
    normalScreenButton.setOnClickListener { fullScreenCloseExoplayer(context, forceLandscape, normalScreenButton, playerView, playerViewFullscreen) }
    playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
    playerView.player = this@preparePlayer
    return normalScreenButton
  } catch (e: Exception) {
    e.printStackTrace()
  }
  return null
}

private fun SimpleExoPlayer.fullScreenExoPlayer(context: AppCompatActivity, forceLandscape: Boolean, playerView: PlayerView, playerViewFullscreen: PlayerView) {
  context.window.decorView.apply {
    systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
        or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
        or View.SYSTEM_UI_FLAG_IMMERSIVE)
  }
  context.supportActionBar?.hide()
  playerViewFullscreen.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
  val controlExo: LinearLayout? = playerViewFullscreen.findViewById(R.id.control_exo)
  controlExo?.setPadding(0,0,0,context.getNavigationBarHeight())
  this.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
  if (forceLandscape) context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
  playerView.visibility = View.GONE
  playerViewFullscreen.visibility = View.VISIBLE
  PlayerView.switchTargetView(this, playerView, playerViewFullscreen)
}

private fun SimpleExoPlayer.fullScreenCloseExoplayer(context: AppCompatActivity, forceLandscape: Boolean, normalScreenButton: ImageView, playerView: PlayerView, playerViewFullscreen: PlayerView) {
  context.window.decorView.apply { systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE }
  context.supportActionBar?.show()
  if (forceLandscape) context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
  normalScreenButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_fullscreen_close))
  playerView.visibility = View.VISIBLE
  playerViewFullscreen.visibility = View.GONE
  PlayerView.switchTargetView(this, playerViewFullscreen, playerView)
}

fun SimpleExoPlayer.setSource(playbackPosition: Long, context: Context, url: String) {
  val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(context, Util.getUserAgent(context, "Boost"))
  val videoSource: MediaSource =
      if (url.endsWith("m3u8") || url.endsWith("m3u")) HlsMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(url))
      else ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(url))
  this.prepare(videoSource)
  this.seekTo(playbackPosition)
  this.playWhenReady = true
}

