package com.framework.utils

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.framework.BaseApplication
import com.framework.R
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource


object ExoPlayerUtils {

  val defaultDF = DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true)
  lateinit var player: ExoPlayer
  private var audioProgressHandler = Handler(Looper.getMainLooper())
  private var audioProgressRunnable: Runnable? = null
  var isPlayingChanged: ((Boolean) -> Unit?)? = null
  var playBackStateChanged: ((Int) -> Unit?)? = null
  var fullscreen = false


  //please create new instance of exoplayer if you are using it on different screen or use old instance if on same screen
  fun getInstance() {
    if (this::player.isInitialized.not()){
      val audioAttributes = AudioAttributes.Builder()
        .setUsage(C.USAGE_MEDIA).setContentType(C.CONTENT_TYPE_MUSIC).build()
      val defaultDF = DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true)
      player = ExoPlayer.Builder(BaseApplication.instance)
        .setAudioAttributes(audioAttributes, true)
        .setMediaSourceFactory(DefaultMediaSourceFactory(defaultDF)).build()
      playerListener()
    }

  }

  private fun playerListener() {
    player.addListener(object : Player.Listener {
      override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        playBackStateChanged?.invoke(playbackState)
      }

      override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        if (isPlaying) {
          startTracking()
        } else {
          stopTracking()
        }
        isPlayingChanged?.invoke(isPlaying)

      }

      override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        Toast.makeText(BaseApplication.instance, error.localizedMessage.toString(), Toast.LENGTH_SHORT).show()
      }
    })
  }


  fun startTracking() {
    if (audioProgressRunnable == null) {
      onProgressChanged { }
    }
    audioProgressHandler.postDelayed(audioProgressRunnable!!, 100)
  }


  fun stopTracking() {
    audioProgressRunnable?.let { audioProgressHandler.removeCallbacks(it) }
  }

  fun onProgressChanged(onProgressChanged: (progress: Long) -> Unit) {
    audioProgressRunnable = Runnable {
      onProgressChanged.invoke(player.currentPosition)
      audioProgressHandler.postDelayed(audioProgressRunnable!!, 1000)
    }
  }


  fun play(url: String, id: Int, seek: Long = 0) {
    val mediaItem = MediaItem.Builder().setMediaId(id.toString()).setUri(url).build()
    player.setMediaItem(mediaItem)
    player.prepare()
    player.play()
    player.seekTo(seek)
  }

  fun prepare(url: String) {
    val mediaItem = MediaItem.Builder().setUri(url).build()
    player.setMediaItem(mediaItem)
    player.prepare()
  }

  fun play(){
    try {
      player.play()
    }catch (e:Exception){
      showToast(e.localizedMessage)
    }
  }

  fun pause(){
    try {
      player.pause()
    }catch (e:Exception){
      showToast(e.localizedMessage)
    }
  }

  fun release() {
    player.release()
    stopTracking()
  }

  fun enableFullScreen(activity:AppCompatActivity,playerView:FrameLayout){
    val fullscreenButton = playerView.findViewById<ImageView>(R.id.exo_fullscreen_icon)
    fullscreenButton.setOnClickListener {
      if (fullscreen) {
        fullscreenButton.setImageDrawable(
          ContextCompat.getDrawable(
            activity,
            R.drawable.ic_fullscreen_open
          )
        )
        activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        if (activity.supportActionBar != null) {
          activity.supportActionBar?.show()
        }
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val params = playerView.getLayoutParams()
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height =
          (200 * activity.resources.displayMetrics.density).toInt()
        playerView.layoutParams = params
        fullscreen = false
      } else {
        fullscreenButton.setImageDrawable(
          ContextCompat.getDrawable(
            activity,
            R.drawable.ic_fullscreen_close
          )
        )
        activity.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        if (activity.supportActionBar != null) {
          activity.supportActionBar?.hide()
        }
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        val params = playerView.layoutParams
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.MATCH_PARENT
        playerView.layoutParams = params
        fullscreen = true
      }
    }
  }
}