package com.onboarding.nowfloats.ui.supportVideo

import android.os.Build
import android.util.Log
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.utils.DateUtils.milliToMinSecFormat
import com.framework.utils.DateUtils.millisecondsToMinutesSeconds
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
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
import jp.shts.android.storiesprogressview.StoriesProgressView
import android.view.MotionEvent

import android.view.View.OnTouchListener





class SupportVideoPlayerActivity :
    AppBaseActivity<ActivitySupportVideoPlayerBinding, SupportVideoViewModel>(),
    StoriesProgressView.StoriesListener {

    private val playbackStateListener: Player.EventListener = playbackStateListener()
    private var exoPlayer: SimpleExoPlayer? = null
    private var supportVideoType: String = ""
    private var isVideoPaused = true
    private var currentPosition = 0
    private var currentVideoClock = 0L
    private var currentTotalClock = 0L
    private lateinit var filteredVideos: List<FeaturevideoItem?>

    /*private var arrayOfVideoUrls = arrayListOf(
        "https://cdn.nowfloats.com/manage/assets/Content/videos/vertical/BoostHowToTrackPerformanceAndWebsiteReport.mp4",
        "https://cdn.nowfloats.com/manage/assets/Content/videos/vertical/BoostHowToAddTestimonial.mp4",
        "https://cdn.nowfloats.com/manage/assets/Content/videos/vertical/BoostHowToTrackPerformanceAndWebsiteReport.mp4"
    )*/

    override fun onCreateView() {
        super.onCreateView()
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
        if (extras?.containsKey(IntentConstant.SUPPORT_VIDEO_TYPE.name)!!) {
            supportVideoType = extras.getString(IntentConstant.SUPPORT_VIDEO_TYPE.name)!!
            getAndSaveFeatureSupportVideos()
        }
        binding?.storiesProgressView?.setStoriesListener(this)
        setUpProgressBar()
        setOnClickListeners()
    }

    private fun setUpProgressBar(duration: Long = 0L, counterL: Int = 0) {
        binding?.storiesProgressView?.setStoryDuration(duration)
        //binding?.storiesProgressView?.startStories(counterL)
    }

    private fun setOnClickListeners() {
        binding?.ivCloseVideo?.setOnClickListener {
            onBackPressed()
        }

        binding?.ivPrev?.setOnClickListener {
            if (currentPosition > 0) {
                binding?.videoView!!.playLeftInAnimation()
                binding?.consOverlayPlay?.playLeftInAnimation()
                loadPreviousVideo()
            }
        }

        binding?.ivNext?.setOnClickListener {
            if (currentPosition < filteredVideos.size - 1) {
                binding?.videoView!!.playRightInAnimation()
                binding?.consOverlayPlay?.playRightInAnimation()
                loadNextVideo(false)
            }
        }

        binding?.ivPlayBtn?.setOnClickListener {
            playTheVideo()
        }

        binding?.videoView?.videoSurfaceView?.setOnClickListener {
            currentVideoClock = exoPlayer?.currentPosition!!
            currentTotalClock = exoPlayer?.contentDuration!!
            pauseExoPlayer(false)
            isVideoPaused = true
        }

        binding?.tvVoiceOverLang?.setOnClickListener {
            showShortToast(getString(R.string.coming_soon))
        }

        binding?.seekBarPaused?.setOnTouchListener { _, _ -> true }
    }

    private fun playTheVideo() {
        binding?.consOverlayPlay?.gone()
        exoPlayer?.play()
        isVideoPaused = false
    }

    private fun pauseExoPlayer(isFirstTimeVideoLoad: Boolean) {
        exoPlayer?.pause()
        if (!isFirstTimeVideoLoad) {
            binding?.tvTimeTotal?.gone()
            binding?.seekBarPaused?.visible()
            binding?.tvElapsedTime?.visible()
        }

        binding?.seekBarPaused?.progress = (currentVideoClock.toFloat().div(currentTotalClock) * 100).toInt()
        binding?.tvElapsedTime?.text = "${millisecondsToMinutesSeconds(currentVideoClock)}/${
            millisecondsToMinutesSeconds(currentTotalClock)
        }"
        binding?.consOverlayPlay?.visible()
    }

    private fun loadNextVideo(isFirstLoad:Boolean) {
        if (!isFirstLoad)
          currentPosition++
        exoPlayer?.next()
        binding?.tvTimeTotal?.visible()
        binding?.seekBarPaused?.gone()
        binding?.tvElapsedTime?.gone()
        binding?.tvVideoTitle?.text = filteredVideos[currentPosition]?.videotitle
        //pauseExoPlayer(true)
    }

    private fun loadPreviousVideo() {
        currentPosition--
        exoPlayer?.previous()
        binding?.tvTimeTotal?.visible()
        binding?.seekBarPaused?.gone()
        binding?.tvElapsedTime?.gone()
        binding?.tvVideoTitle?.text = filteredVideos[currentPosition]?.videotitle
        //pauseExoPlayer(true)
    }

    private fun getAndSaveFeatureSupportVideos() {
        showProgress()
        viewModel.getSupportVideos().observeForever {
            val featureSupportVideoResponse = it as? FeatureSupportVideoResponse
            if (it.isSuccess() && featureSupportVideoResponse != null) {
                saveSupportVideoData(featureSupportVideoResponse)
                populateData()
            }
            hideProgress()
        }
    }

    private fun populateData() {
        val featureVideos = getSupportVideoData()?.data?.first()?.featurevideo
        filteredVideos = featureVideos?.filter { filter -> filter?.helpsectionidentifier == supportVideoType }!!
        for (item in filteredVideos!!)
            exoPlayer?.addMediaItem(item?.videourl?.url?.let { MediaItem.fromUri(it) }!!)
        binding?.storiesProgressView?.setStoriesCount(filteredVideos.size)
        exoPlayer?.prepare()
        loadNextVideo(true)
    }

    override fun onNext() {
    }

    override fun onPrev() {
    }

    override fun onComplete() {
        binding?.consOverlayPlay?.visible()
    }

    private fun initializePlayer() {
        exoPlayer = SimpleExoPlayer.Builder(this)
            .build()
            .also { exoPlayer ->
                exoPlayer.addListener(playbackStateListener)
                binding?.videoView?.player = exoPlayer
                exoPlayer.playWhenReady = false
                exoPlayer.seekTo(0, 0L)
                exoPlayer.prepare()
            }
    }

    private fun playbackStateListener() = object : Player.EventListener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            val stateString: String = when (playbackState) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                ExoPlayer.STATE_BUFFERING -> {
                    "ExoPlayer.STATE_BUFFERING -"
                }
                ExoPlayer.STATE_READY -> {
                    currentVideoClock = exoPlayer?.currentPosition!!
                    currentTotalClock = exoPlayer?.contentDuration!!
                    binding?.tvTimeTotal?.text = milliToMinSecFormat(currentTotalClock)
                    setUpProgressBar(exoPlayer?.contentDuration!!, currentPosition)
                    "ExoPlayer.STATE_READY     -"
                }
                ExoPlayer.STATE_ENDED -> {
                    exoPlayer?.pause()
                    binding?.consOverlayPlay?.visible()
                    binding?.tvTimeTotal?.visible()
                    binding?.seekBarPaused?.gone()
                    binding?.tvElapsedTime?.gone()
                    binding?.seekBarPaused?.progress = 0
                    "ExoPlayer.STATE_ENDED     -"
                }
                else -> "UNKNOWN_STATE             -"
            }
            Log.d(TAG, "changed state to $stateString")
        }
    }

    public override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= 24) {
            initializePlayer()
        }
    }

    public override fun onResume() {
        super.onResume()
        if ((Build.VERSION.SDK_INT < 24 || exoPlayer == null)) {
            initializePlayer()
        }
    }

    public override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT < 24) {
            releasePlayer()
        }
    }


    public override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        exoPlayer?.run {
            release()
            removeListener(playbackStateListener)
        }
        exoPlayer = null
    }
}