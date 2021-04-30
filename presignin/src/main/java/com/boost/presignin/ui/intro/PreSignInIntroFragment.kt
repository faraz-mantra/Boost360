package com.boost.presignin.ui.intro

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.core.view.isVisible
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.databinding.FragmentPreSigninIntroBinding
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.model.IntroItem
import com.boost.presignin.ui.mobileVerification.MobileVerificationActivity
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.webengageconstant.*

class PreSignInIntroFragment : AppBaseFragment<FragmentPreSigninIntroBinding, BaseViewModel>() {

  private val TAG = "IntroFragment"
  private var buffering = true
  private var videoDuration: Int = 0
  private var timer: CountDownTimer? = null
  private var mediaPlayer: MediaPlayer? = null
  private var mute = false
  private val introItem by lazy { requireArguments().getSerializable(INTRO_ITEM) as IntroItem }
  private val position by lazy { requireArguments().getInt(POSITION) }

  var onSkip: (() -> Unit)? = null
  var playPause: ((b: Boolean) -> Unit)? = null


  companion object {
    private var INTRO_ITEM = "INTRO_ITEM"
    private var POSITION = "POSITION"

    @JvmStatic
    fun newInstance(introItem: IntroItem, position: Int) =
        PreSignInIntroFragment().apply {
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
    binding?.introItem = introItem;
    binding?.presiginIntroImg?.setImageResource(introItem.imageResource)

    if (position == 0) {
      binding?.boostLogo?.visible()
      binding?.presiginIntroImg?.setOnClickListener {
        WebEngageController.trackEvent(PS_INTRO_VIDEO_SPLASH_CLICKED, START_INTRO_VIDEO, NO_EVENT_VALUE)
        playPause?.let { it5 -> it5(true) }
        binding?.introImgContainer?.isVisible = false;
        binding?.videoViewContainer?.isVisible = true;
        binding?.progressBar?.isVisible = true
        binding?.videoView?.setOnPreparedListener {
          mediaPlayer = it
          videoDuration = mediaPlayer?.duration ?: 0
        }
        binding?.videoView?.setVideoPath("https://cdn.withfloats.com/boost/videos/en/intro.mp4")
        binding?.videoView?.start()
        binding?.videoView?.setOnInfoListener { p0, p1, p2 ->
          when (p1) {
            MediaPlayer.MEDIA_INFO_BUFFERING_START -> {
              Log.d(TAG, "onCreateView: MEDIA_INFO_BUFFERING_START")
              binding?.progressBar?.isVisible = true
              buffering = true

            }

            MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
              Log.d(TAG, "onCreateView: MEDIA_INFO_BUFFERING_END")
              binding?.progressBar?.isVisible = false
              binding?.videoViewContainer?.isVisible = true
              buffering = false
            }

            MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {
              Log.d(TAG, "onCreateView: MEDIA_INFO_VIDEO_RENDERING_START")
              binding?.progressBar?.isVisible = false
              binding?.videoViewContainer?.isVisible = true
              setVideoTimerCountDown()
            }
          }
          true
        }
        binding?.videoViewContainer?.setOnClickListener {
          WebEngageController.trackEvent(PS_CLICKED_INTRO_VIDEO_AREA, VIDEO_AREA_CLICKED, NO_EVENT_VALUE)
          if (binding?.videoView?.isPlaying == true) {
            binding?.videoView?.pause()
            binding?.playPauseLottie?.isVisible = true
            timer?.cancel()
          }
        }
      }
      binding?.playPauseLottie?.setOnClickListener {
        binding?.videoView?.start()
        setVideoTimerCountDown()
        it.isVisible = false
      }
    } else binding?.boostLogo?.gone()

    binding?.muteVideo?.setOnClickListener {
      muteUnMute()
    }

    binding?.skipVideo?.setOnClickListener {
//      WebEngageController.trackEvent(PS_CLICKED_INTRO_VIDEO_SKIP, VIDEO_SKIPPED, NO_EVENT_VALUE)
//      onSkip?.let { it1 -> it1() }
      startActivity(Intent(baseActivity, MobileVerificationActivity::class.java))
      baseActivity.finish()
    }
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onPause() {
    super.onPause()
    if (position == 0) {
      if (binding?.videoView?.isPlaying == true) {
        binding?.videoView?.pause()
        binding?.playPauseLottie?.isVisible = true;
        timer?.cancel()
      }
    }
  }


  private fun setVideoTimerCountDown() {
    val duration = mediaPlayer?.duration ?: 0
    val currentTime = mediaPlayer?.currentPosition ?: 0;
    timer = object : CountDownTimer((duration - currentTime).toLong(), 1000) {
      override fun onTick(millisUntilFinished: Long) {
        val videoDuration = (millisUntilFinished / 1000).toInt()
        binding?.videoTime?.post {
          if (videoDuration == 0) {
            timer?.cancel()
            binding?.videoTime?.text = String.format(getString(R.string.intro_video_time), "00")
          } else {
            binding?.videoTime?.text = String.format(getString(R.string.intro_video_time), videoDuration)
          }
        }
      }

      override fun onFinish() {
        Log.e("videoCompleted", "&&&&&&&&&&&&&")
        binding?.introImgContainer?.post {
          binding?.introImgContainer?.isVisible = true
          binding?.videoViewContainer?.isVisible = false
          binding?.progressBar?.isVisible = false
        }
      }
    }
    timer?.start()
  }


  override fun onStop() {
    super.onStop()
    binding?.videoView?.suspend()
  }


  private fun muteUnMute() {
    mute = !mute
    val volume = if (mute) 0.0f else 1.0f
    mediaPlayer?.setVolume(volume, volume)
    binding?.muteIcon?.setImageResource(if (mute) R.drawable.ic_mute else R.drawable.ic_unmute)
    if (mute){
      WebEngageController.trackEvent(PS_CLICKED_MUTE_INTRO_VIDEO, VIDEO_MUTED, NO_EVENT_VALUE)
    }else{
      WebEngageController.trackEvent(PS_CLICKED_UNMUTE_INTRO_VIDEO, VIDEO_UNMUTED, NO_EVENT_VALUE)
    }
  }
}