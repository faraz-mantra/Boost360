package com.onboarding.nowfloats.ui.supportVideo

import android.os.Build
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.pref.APPLICATION_JIO_ID
import com.framework.utils.DateUtils.milliToMinSecFormat
import com.framework.utils.DateUtils.millisecondsToMinutesSeconds
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.material.slider.Slider
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.base.AppBaseActivity
import com.onboarding.nowfloats.constant.IntentConstant
import com.onboarding.nowfloats.databinding.ActivitySupportVideoPlayerBinding
import com.onboarding.nowfloats.extensions.playLeftInAnimation
import com.onboarding.nowfloats.extensions.playRightInAnimation
import com.onboarding.nowfloats.model.supportVideo.FeatureSupportVideoResponse
import com.onboarding.nowfloats.model.supportVideo.FeatureSupportVideoResponse.Companion.getSupportVideoData
import com.onboarding.nowfloats.model.supportVideo.FeatureSupportVideoResponse.Companion.saveSupportVideoData
import com.onboarding.nowfloats.model.supportVideo.FeaturevideoItem
import com.onboarding.nowfloats.viewmodel.SupportVideoViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


class SupportVideoPlayerActivity : AppBaseActivity<ActivitySupportVideoPlayerBinding, SupportVideoViewModel>() {

  private val playbackStateListener: Player.EventListener = playbackStateListener()
  private var exoPlayer: SimpleExoPlayer? = null
  private var supportVideoType: String = ""
  private var currentPosition = 0
  private var currentVideoClock = 0L
  private var currentTotalClock = 0L
  private var isFirstLoad = true
  private var filteredVideos: MutableList<FeaturevideoItem> = mutableListOf()
  private lateinit var allVideoDuration: LongArray
  private var elapsedTimeUpdatesJob: Job? = null

  /*private var arrayOfVideoUrls = arrayListOf(
      "https://cdn.nowfloats.com/manage/assets/Content/videos/vertical/BoostHowToTrackPerformanceAndWebsiteReport.mp4",
      "https://cdn.nowfloats.com/manage/assets/Content/videos/vertical/BoostHowToAddTestimonial.mp4",
      "https://cdn.nowfloats.com/manage/assets/Content/videos/vertical/BoostHowToTrackPerformanceAndWebsiteReport.mp4",
      "https://cdn.nowfloats.com/manage/assets/Content/videos/vertical/BoostHowToTrackPerformanceAndWebsiteReport.mp4"
  )*/

  override fun onCreateView() {
    super.onCreateView()
    if (packageName.equals(APPLICATION_JIO_ID, ignoreCase = true)) {
      showShortToast(getString(R.string.coming_soon))
      finish()
      return
    }
    initUI()
  }

  override fun getLayout(): Int {
    return R.layout.activity_support_video_player
  }

  override fun getViewModelClass(): Class<SupportVideoViewModel> {
    return SupportVideoViewModel::class.java
  }

  private fun initUI() {
    val extras = intent.extras
    supportVideoType = extras?.getString(IntentConstant.SUPPORT_VIDEO_TYPE.name) ?: ""
    if (Build.VERSION.SDK_INT >= 24) initializePlayer() else finishActivity()
    if (supportVideoType.isNotEmpty()) {
      if (isSavedSupportDataAvailableInSharedPref()) {
        populateData()
        getAndSaveFeatureSupportVideos()
      } else {
        getAndSaveFeatureSupportVideos(true)
      }
    } else finishActivity()
    setOnClickListeners()
  }


  private fun isSavedSupportDataAvailableInSharedPref(): Boolean {
    return getSupportVideoData().isNullOrEmpty().not()
  }

  private fun setOnClickListeners() {
    binding?.ivCloseVideo?.setOnClickListener {
      onBackPressed()
    }

    binding?.ivPrev?.setOnClickListener {
      if (currentPosition > 0) {
        binding?.videoView?.playLeftInAnimation()
        binding?.consOverlayPlay?.playLeftInAnimation()
        loadPreviousVideo()
      }
    }

    binding?.ivNext?.setOnClickListener {
      if (currentPosition < filteredVideos.size - 1) {
        binding?.videoView?.playRightInAnimation()
        binding?.consOverlayPlay?.playRightInAnimation()
        isFirstLoad = false
        loadNextVideo()
      }
    }

    binding?.ivPlayBtn?.setOnClickListener {
      playTheVideo()
    }

    binding?.videoView?.videoSurfaceView?.setOnClickListener {
      currentVideoClock = exoPlayer?.currentPosition!!
      currentTotalClock = exoPlayer?.contentDuration!!
      pauseExoPlayer(false)
    }

    binding?.tvVoiceOverLang?.setOnClickListener {
      showShortToast(getString(R.string.coming_soon))
    }

    binding?.seekBarPaused?.setOnTouchListener { _, _ -> true }
  }

  private fun playTheVideo() {
    binding?.consOverlayPlay?.gone()
    exoPlayer?.play()
    binding?.videoView?.useController = true
  }

  private fun pauseExoPlayer(isFirstTimeVideoLoad: Boolean) {
    exoPlayer?.pause()
    binding?.videoView?.useController = false
    if (!isFirstTimeVideoLoad) {
      binding?.tvTimeTotal?.gone()
      binding?.seekBarPaused?.visible()
      binding?.tvElapsedTime?.visible()
    }

    binding?.seekBarPaused?.progress = (currentVideoClock.toFloat().div(currentTotalClock) * 100).toInt()
    binding?.tvElapsedTime?.text = "${millisecondsToMinutesSeconds(currentVideoClock)}/${millisecondsToMinutesSeconds(currentTotalClock)}"
    binding?.consOverlayPlay?.visible()
  }

  private fun loadNextVideo() {
    if (!isFirstLoad) {
      isFirstLoad = false
      currentPosition++
      exoPlayer?.next()
    }

    when {
      filteredVideos.size == 1 -> {
        blurAndDisableNavButtons(isPrevButtonDisable = true, isNextButtonDisable = true)
      }
      currentPosition == filteredVideos.size - 1 -> {
        blurAndDisableNavButtons(isNextButtonDisable = true)
      }
      else -> {
        blurAndDisableNavButtons()
      }
    }

    setupSliderProgresses()
    binding?.videoView?.useController = false
    binding?.tvTimeTotal?.visible()
    binding?.seekBarPaused?.gone()
    binding?.tvElapsedTime?.gone()
    binding?.tvTimeTotal?.text = ""
    binding?.tvVideoTitle?.text = filteredVideos[currentPosition].videotitle
  }

  private fun setupSliderProgresses() {
    for (index in 0 until binding?.linearProgressStatus?.childCount!!) {
      val childAt = binding?.linearProgressStatus?.getChildAt(index) as Slider
      if (isFirstLoad) {
        childAt.value = 0f
      } else {
        if (index < exoPlayer?.currentMediaItemIndex!!) {
          childAt.value = 100f
        } else if (index > exoPlayer?.currentMediaItemIndex!!) {
          childAt.value = 0f
        }
      }
    }
  }

  private fun loadPreviousVideo() {
    currentPosition--
    exoPlayer?.previous()
    if (currentPosition == 0) {
      blurAndDisableNavButtons(isPrevButtonDisable = true)
    } else {
      blurAndDisableNavButtons()
      binding?.ivPrev?.setTintColor(ContextCompat.getColor(this, R.color.white))
      binding?.ivNext?.alpha = 1f
    }

    setupSliderProgresses()
    binding?.videoView?.useController = false
    binding?.tvTimeTotal?.visible()
    binding?.seekBarPaused?.gone()
    binding?.tvElapsedTime?.gone()
    binding?.tvTimeTotal?.text = ""
    binding?.tvVideoTitle?.text = filteredVideos[currentPosition].videotitle
  }

  private fun getAndSaveFeatureSupportVideos(isProgress: Boolean = false) {
    if (isProgress) showProgress()
    viewModel.getSupportVideos().observeOnce(this) {
      val featureVideo = (it as? FeatureSupportVideoResponse)?.data?.firstOrNull()?.featurevideo
      if (it.isSuccess() && featureVideo.isNullOrEmpty().not()) {
        saveSupportVideoData(featureVideo)
        if (isProgress) populateData()
      } else {
        saveSupportVideoData(arrayListOf())
        if (isProgress) {
          showShortToast(getString(R.string.coming_soon))
          finish()
        }
      }
      hideProgress()
    }
  }

  private fun populateData() {
    val featureFirstVideo: FeaturevideoItem? = getSupportVideoData()?.firstOrNull { it.helpsectionidentifier == supportVideoType }
    if (featureFirstVideo != null && featureFirstVideo.videourl?.url?.contains("youtube") == false) {
      filteredVideos = arrayListOf(featureFirstVideo)
      for (item in filteredVideos) {
        exoPlayer?.addMediaItem(item.let { MediaItem.fromUri(it.videourl?.url!!) })
      }
      getVideoDurations()
      initStatusProgressBar()
      exoPlayer?.prepare()
      isFirstLoad = true
      loadNextVideo()
      launchProgressListener()
    } else {
      finishActivity()
      return
    }
  }

  private fun blurAndDisableNavButtons(isPrevButtonDisable: Boolean = false, isNextButtonDisable: Boolean = false) {
    if (isPrevButtonDisable) {
      binding?.ivPrev?.setTintColor(ContextCompat.getColor(this, R.color.black))
      binding?.ivPrev?.alpha = 0.5f
      binding?.ivPrev?.isEnabled = false
    } else {
      binding?.ivPrev?.setTintColor(ContextCompat.getColor(this, R.color.pinkish_grey))
      binding?.ivPrev?.alpha = 1f
      binding?.ivPrev?.isEnabled = true
    }

    if (isNextButtonDisable) {
      binding?.ivNext?.setTintColor(ContextCompat.getColor(this, R.color.black))
      binding?.ivNext?.alpha = 0.5f
      binding?.ivNext?.isEnabled = false
    } else {
      binding?.ivNext?.setTintColor(ContextCompat.getColor(this, R.color.pinkish_grey))
      binding?.ivNext?.alpha = 1f
      binding?.ivNext?.isEnabled = true
    }
  }

  private fun initializePlayer() {
    exoPlayer = SimpleExoPlayer.Builder(this).build().also { exoPlayer ->
      exoPlayer.apply {
        repeatMode = Player.REPEAT_MODE_ONE
        addListener(playbackStateListener)
        playWhenReady = false
        seekTo(0, 0L)
//        prepare()
        binding?.videoView?.player = this
      }
      binding?.videoView?.useController = false
    }
  }

  private fun playbackStateListener() = object : Player.EventListener {

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
      super.onMediaItemTransition(mediaItem, reason)
      onVideoEnded()
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
      val stateString: String = when (playbackState) {
        ExoPlayer.STATE_IDLE -> {
          binding?.videoView?.useController = false
          "ExoPlayer.STATE_IDLE      -"
        }
        ExoPlayer.STATE_BUFFERING -> {
          "ExoPlayer.STATE_BUFFERING -"
        }
        ExoPlayer.STATE_READY -> {
          binding?.videoView?.useController = false
          currentVideoClock = exoPlayer?.currentPosition!!
          currentTotalClock = exoPlayer?.contentDuration!!
          binding?.tvTimeTotal?.text = milliToMinSecFormat(currentTotalClock)
          "ExoPlayer.STATE_READY     -"
        }
        ExoPlayer.STATE_ENDED -> {
          onVideoEnded()
          "ExoPlayer.STATE_ENDED     -"
        }
        else -> "UNKNOWN_STATE             -"
      }
      Log.d(TAG, "changed state to $stateString")
    }
  }

  private fun onVideoEnded() {
    exoPlayer?.pause()
    binding?.consOverlayPlay?.visible()
    binding?.videoView?.useController = false
    binding?.tvTimeTotal?.visible()
    binding?.seekBarPaused?.gone()
    binding?.tvElapsedTime?.gone()
    binding?.seekBarPaused?.progress = 0
  }

  public override fun onPause() {
    pauseExoPlayer(false)
    super.onPause()
  }


  public override fun onStop() {
    pauseExoPlayer(false)
    super.onStop()
  }

  override fun onDestroy() {
    releasePlayer()
    super.onDestroy()
  }

  private fun releasePlayer() {
    exoPlayer?.run {
      release()
      removeListener(playbackStateListener)
    }
    exoPlayer = null
  }

  private fun getVideoDurations() {
    allVideoDuration = LongArray(filteredVideos.size)
    for (index in 0 until filteredVideos.size) {
      allVideoDuration[index] = (filteredVideos[index].videodurationseconds?.toInt()?.times(1000))?.toLong() ?: 0
    }
  }

  private fun initStatusProgressBar() {
    binding?.linearProgressStatus?.weightSum = filteredVideos.size.toFloat()
    for (index in 0 until filteredVideos.size) {
      val slider = Slider(this)
      slider.apply {
        valueFrom = 0f
        valueTo = 100f
        value = 0f
        isTickVisible = false
        thumbRadius = 0
        haloRadius = 0
        trackActiveTintList = ContextCompat.getColorStateList(this@SupportVideoPlayerActivity, R.color.slider_active_color_selector)!!
        trackInactiveTintList = ContextCompat.getColorStateList(this@SupportVideoPlayerActivity, R.color.slider_inactive_color_selector)!!
        setPadding(0, 0, 0, 0)

        val layoutParamsSlider = LinearLayout.LayoutParams(0, 6, 1f)
        layoutParamsSlider.setMargins(6, 0, 6, 0)
        layoutParams = layoutParamsSlider

        binding?.linearProgressStatus?.addView(this)
      }
    }
  }

  private fun videoProgress() = flow {
    while (true) {
      emit((exoPlayer?.currentPosition?.toFloat()?.div(exoPlayer?.duration?.toFloat() ?: 0F)?.times(100))?.toInt() ?: 0)
      delay(1000)
    }
  }.flowOn(Dispatchers.Main)

  private fun launchProgressListener() {
    elapsedTimeUpdatesJob = lifecycleScope.launch {
      withContext(Dispatchers.Main) {
        videoProgress().collect {
          statusProgressUpdate(it)
        }
      }
    }
  }

  private fun statusProgressUpdate(progressPercentage: Int) {
    val childAt = binding?.linearProgressStatus?.getChildAt(exoPlayer?.currentMediaItemIndex!!) as Slider
    childAt.value = progressPercentage.toFloat()
  }

  private fun finishActivity() {
    showShortToast(getString(R.string.please_try_again_later))
    finish()
  }
}