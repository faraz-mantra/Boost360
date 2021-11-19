package com.boost.presignin.ui.intro

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.databinding.FragmentPreSigninIntroBinding
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.model.IntroItem
import com.framework.analytics.SentryController
import com.framework.models.BaseViewModel
import com.framework.webengageconstant.*
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer


class PreSignInIntroFragment : AppBaseFragment<FragmentPreSigninIntroBinding, BaseViewModel>() {

  private val TAG = "IntroFragment"
  private var buffering = true
  private var videoDuration: Long = 0
  private var player:SimpleExoPlayer?=null
  private var timer: com.boost.presignin.timer.CountDownTimer? = null
  private var mute = false
  private val introItem by lazy { requireArguments().getSerializable(INTRO_ITEM) as IntroItem }
  private val position by lazy { requireArguments().getInt(POSITION) }

  var onSkip: (() -> Unit)? = null
  var playPause: ((b: Boolean) -> Unit)? = null

  companion object {
    private var INTRO_ITEM = "INTRO_ITEM"
    private var POSITION = "POSITION"

    @JvmStatic
    fun newInstance(introItem: IntroItem, position: Int) = PreSignInIntroFragment().apply {
      arguments = Bundle().apply {
        putSerializable(INTRO_ITEM, introItem)
        putInt(POSITION, position)
      }
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_pre_signin_intro
  }

  override fun onCreateView() {
    super.onCreateView()

    player = SimpleExoPlayer.Builder(requireContext()).build()
    binding?.videoView?.player = player

    binding?.introItem = introItem;
    introItem.imageResource?.let { binding?.presiginIntroImg?.setImageResource(it) }

    if (position == 0) {
      binding?.presiginIntroImg?.setOnClickListener {
        WebEngageController.trackEvent(
          PS_INTRO_VIDEO_SPLASH_CLICKED,
          START_INTRO_VIDEO,
          NO_EVENT_VALUE
        )
        binding?.videoViewContainer?.isVisible = true;
        binding?.introImgContainer?.isVisible = false;
        binding?.progressBar?.isVisible = true
        val mediaItem= MediaItem.fromUri(getString(R.string.intro_video_url))
// Set the media item to be played.
// Set the media item to be played.
        player?.setMediaItem(mediaItem)
// Prepare the player.
// Prepare the player.
        player?.prepare()
// Start the playback.
// Start the playback.
        player?.play()
        playPause?.let { it5 -> it5(true) }

        player?.addListener(object : Player.Listener {
          override fun onPlaybackStateChanged(state: Int) {
            Log.i(TAG, "onPlaybackStateChanged: "+state)
            when(state){
              Player.STATE_READY->{
                videoDuration = player?.duration?:0
                Log.i(TAG, "video duration: "+videoDuration)
                binding?.progressBar?.isVisible = false
                binding?.videoViewContainer?.isVisible = true
                setVideoTimerCountDown(videoDuration,player?.currentPosition?:0)
                val seconds = (videoDuration.minus(player?.currentPosition?:0)).div(1000).toInt()
                Log.i(TAG, "seconds: "+seconds)
                binding?.videoTime?.text =
                    getString(R.string.intro_video_time, seconds.toString())

              }
              Player.STATE_IDLE->{
                Log.i(TAG, "buffering: ")
                buffering = true
                binding?.progressBar?.isVisible = true
                buffering = true
                timer?.pause()
              }
              Player.STATE_ENDED->{
                binding?.introImgContainer?.post {
                  binding?.introImgContainer?.isVisible = true
                  binding?.videoViewContainer?.isVisible = false
                  binding?.progressBar?.isVisible = false
                }
              }
            }
            super.onPlaybackStateChanged(state)
          }
        })

        /*binding?.videoView?.setOnPreparedListener {
          mediaPlayer = it
          videoDuration = mediaPlayer?.duration ?: 0
        }
        binding?.videoView?.setVideoPath("https://cdn.nowfloats.com/jioonline/android/videos/JioOnlineHighResolution.mp4")
        binding?.videoView?.start()
        binding?.videoView?.setOnInfoListener { p0, p1, p2 ->
          when (p1) {
            MediaPlayer.MEDIA_INFO_BUFFERING_START -> {
              Log.d(TAG, "onCreateView: MEDIA_INFO_BUFFERING_START")
              binding?.progressBar?.isVisible = true
              buffering = true
              timer?.pause()
            }

            MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
              Log.d(TAG, "onCreateView: MEDIA_INFO_BUFFERING_END")
              binding?.progressBar?.isVisible = false
              binding?.videoViewContainer?.isVisible = true
              buffering = false
              timer?.resume()
            }

            MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {
              Log.d(TAG, "onCreateView: MEDIA_INFO_VIDEO_RENDERING_START")
              binding?.progressBar?.isVisible = false
              binding?.videoViewContainer?.isVisible = true
              setVideoTimerCountDown()
            }
          }
          true
        }*/
        binding?.videoViewContainer?.setOnClickListener {
          WebEngageController.trackEvent(
            PS_CLICKED_INTRO_VIDEO_AREA,
            VIDEO_AREA_CLICKED,
            NO_EVENT_VALUE
          )
          if (player?.isPlaying == true) {
            player?.pause()
            binding?.playPauseLottie?.isVisible = true
            timer?.pause()
          }
        }
      }
      binding?.playPauseLottie?.setOnClickListener {
        player?.play()
        playPause?.let { it5 -> it5(true) }

        if (timer?.isStarted == true){
          timer?.resume()
        }else{
          timer?.start()
        }
        it.isVisible = false
      }
    }

    binding?.muteVideo?.setOnClickListener {
      muteUnMute()
    }

    binding?.skipVideo?.setOnClickListener {
      WebEngageController.trackEvent(PS_CLICKED_INTRO_VIDEO_SKIP, VIDEO_SKIPPED, NO_EVENT_VALUE)
      binding?.playPauseLottie?.isVisible = true
      try {
        player?.stop()
      } catch (e: Exception) {
        Log.e("SKIP_VIDEO_SUSPEND", e.localizedMessage)
        SentryController.captureException(e)

      }
      timer?.cancel()
      binding?.playPauseLottie?.isVisible = false
      binding?.introImgContainer?.isVisible = true
      binding?.videoViewContainer?.isVisible = false
      binding?.progressBar?.isVisible = false
      (requireActivity() as? IntroActivity)?.slideNextPage()
    }
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  private fun setVideoTimerCountDown(videoDuration: Long, currentPosition: Long) {
    try {

      timer =
        object : com.boost.presignin.timer.CountDownTimer((videoDuration - currentPosition).toLong(), 1000) {
          override fun onTick(millisUntilFinished: Long) {
            val videoDuration = (millisUntilFinished / 1000).toInt()
            Log.i(TAG, "onTick: "+videoDuration)
            binding?.videoTime?.post {
              if (videoDuration == 0) {
                timer?.cancel()
                binding?.videoTime?.text =
                  String.format(getString(R.string.intro_video_time), "00")
              } else {
                binding?.videoTime?.text =
                  String.format(getString(R.string.intro_video_time), videoDuration)
              }
            }
          }

          override fun onFinish() {
            Log.e("videoCompleted", "&&&&&&&&&&&&&")

          }
        }
      if (player?.isPlaying == true){
        timer?.start()
      }else{

      }
    } catch (e: Exception) {
      Log.e("TimerCountDown", e.localizedMessage)
      SentryController.captureException(e)

    }
  }

  fun setTimerText(seconds:Int){
    Log.i(TAG, "setTimerText: "+seconds)
    if (seconds == 0) {
      timer?.cancel()
      binding?.videoTime?.text =
        String.format(getString(R.string.intro_video_time), "00")
    } else {
      binding?.videoTime?.text =
        String.format(getString(R.string.intro_video_time), videoDuration)
    }
  }

  override fun onPause() {
    super.onPause()
    Log.i(TAG, "onPause: ")
    if (position == 0) {
        player?.pause()
        if (binding?.introImgContainer?.isVisible == false){
          binding?.playPauseLottie?.isVisible = true;
        }
    }
    timer?.pause()
  }

  override fun onStop() {
    super.onStop()
    Log.i(TAG, "onStop: ")
    //timer?.cancel()
   // player?.release()
  }

  override fun onDestroy() {
    super.onDestroy()
    player?.release()
  }

  private fun muteUnMute() {
    mute = !mute
    val volume = if (mute) 0.0f else 1.0f
    player?.volume = volume
    binding?.muteIcon?.setImageResource(if (mute) R.drawable.ic_mute else R.drawable.ic_unmute)
    if (mute) {
      WebEngageController.trackEvent(PS_CLICKED_MUTE_INTRO_VIDEO, VIDEO_MUTED, NO_EVENT_VALUE)
    } else {
      WebEngageController.trackEvent(PS_CLICKED_UNMUTE_INTRO_VIDEO, VIDEO_UNMUTED, NO_EVENT_VALUE)
    }
  }
}