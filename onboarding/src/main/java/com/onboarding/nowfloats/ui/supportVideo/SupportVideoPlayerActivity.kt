package com.onboarding.nowfloats.ui.supportVideo

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.utils.DateUtils.millisecondsToMinutesSeconds
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.base.AppBaseActivity
import com.onboarding.nowfloats.databinding.ActivitySupportVideoPlayerBinding
import com.onboarding.nowfloats.viewmodel.SupportVideoViewModel
import jp.shts.android.storiesprogressview.StoriesProgressView
import android.view.animation.AccelerateInterpolator

import android.view.animation.Animation

import android.view.animation.TranslateAnimation
import com.onboarding.nowfloats.model.supportVideo.FeatureSupportVideoResponse
import com.google.android.exoplayer2.ExoPlaybackException

import com.google.android.exoplayer2.Timeline





class SupportVideoPlayerActivity :
    AppBaseActivity<ActivitySupportVideoPlayerBinding, SupportVideoViewModel>(),
    StoriesProgressView.StoriesListener {

    private val playbackStateListener: Player.EventListener = playbackStateListener()
    private var exoPlayer: SimpleExoPlayer? = null
    private var isVideoPaused = true
    private var currentPosition = 0
    private var currentVideoClock = 0L
    private var currentTotalClock = 0L
    private var arrayOfVideoUrls = arrayListOf(
        "https://cdn.nowfloats.com/manage/assets/Content/videos/vertical/BoostHowToTrackPerformanceAndWebsiteReport.mp4",
        "https://cdn.nowfloats.com/manage/assets/Content/videos/vertical/BoostHowToAddTestimonial.mp4",
        "https://cdn.nowfloats.com/manage/assets/Content/videos/vertical/BoostHowToTrackPerformanceAndWebsiteReport.mp4"
    )

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

    private fun initializePlayer() {
        exoPlayer = SimpleExoPlayer.Builder(this)
            .build()
            .also { exoPlayer ->
                exoPlayer.addListener(playbackStateListener)
                binding?.videoView?.player = exoPlayer
                for (item in arrayOfVideoUrls)
                    exoPlayer.addMediaItem(MediaItem.fromUri(item))

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
                    //binding?.storiesProgressView?.resume()
                    currentVideoClock = exoPlayer?.currentPosition!!
                    currentTotalClock = exoPlayer?.contentDuration!!
                    setUpProgressBar(exoPlayer?.contentDuration!!, currentPosition)

                    "ExoPlayer.STATE_READY     -"
                }
                ExoPlayer.STATE_ENDED -> {
                    binding?.storiesProgressView?.pause()
                    exoPlayer?.pause()
                    binding?.consOverlayPlay?.visible()
                    "ExoPlayer.STATE_ENDED     -"
                }
                else -> "UNKNOWN_STATE             -"
            }
            Log.d(TAG, "changed state to $stateString")
        }
    }

    private fun initUI() {
        binding?.storiesProgressView?.setStoriesCount(arrayOfVideoUrls.size)
        binding?.storiesProgressView?.setStoriesListener(this)
        getAndSaveFeatureSupportVideos()
        setUpProgressBar()
        setOnClickListeners()
        loadNextVideo()
    }

    private fun setUpProgressBar(duration: Long = 0L, counterL: Int = 0) {
        binding?.storiesProgressView?.setStoryDuration(duration)
        binding?.storiesProgressView?.startStories(counterL)
        binding?.storiesProgressView?.pause()
    }

    private fun setOnClickListeners() {
        binding?.ivCloseVideo?.setOnClickListener {
            onBackPressed()
        }

        binding?.ivPrev?.setOnClickListener {
            if (currentPosition > 0) {
                playLeftInAnimation(binding?.videoView!!)
                loadPreviousVideo()
            }
        }

        binding?.ivNext?.setOnClickListener {
            if (currentPosition < arrayOfVideoUrls.size - 1) {
                playRightInAnimation(binding?.videoView!!)
                binding?.storiesProgressView?.skip()
                loadNextVideo()
            }
        }

        binding?.ivPlayBtn?.setOnClickListener {
            playTheVideo()
        }

        binding?.videoView?.videoSurfaceView?.setOnClickListener {
            currentVideoClock = exoPlayer?.currentPosition!!
            currentTotalClock = exoPlayer?.contentDuration!!
            pauseExoPlayer()
            isVideoPaused = true
        }

        binding?.tvVoiceOverLang?.setOnClickListener {
            showShortToast(getString(R.string.coming_soon))
        }
    }

    private fun playTheVideo() {
        binding?.consOverlayPlay?.gone()
        binding?.storiesProgressView?.resume()
        exoPlayer?.play()
        isVideoPaused = false
    }

    private fun pauseExoPlayer(){
        exoPlayer?.pause()
        //binding?.storiesProgressView?.pause()
        binding?.seekBarPaused?.progress = ((currentVideoClock / currentTotalClock) * 100).toInt()
        binding?.consOverlayPlay?.visible()
        binding?.tvElapsedTime?.text = "${millisecondsToMinutesSeconds(currentVideoClock)}/${millisecondsToMinutesSeconds(currentTotalClock)}"
    }

    private fun loadNextVideo() {
        currentPosition++
        exoPlayer?.next()
        //pauseExoPlayer()
        //binding?.storiesProgressView?.pause()
    }

    private fun loadPreviousVideo() {
        currentPosition--
        exoPlayer?.previous()
        //pauseExoPlayer()
        binding?.storiesProgressView?.reverse()
        //binding?.storiesProgressView?.pause()
    }

    private fun getAndSaveFeatureSupportVideos() {
        showProgress()
        viewModel.getSupportVideos().observeForever {
            if (it.isSuccess()) {
                val featureSupportVideoResponse =  it as? FeatureSupportVideoResponse
                val featureVideos = featureSupportVideoResponse?.data?.first()?.featurevideo
                val filteredVideos =
                    featureVideos?.filter { filter -> filter?.helpsectionidentifier == "feature.TESTIMONIALS" }
                filteredVideos?.first()?.videourl?.url
            }
            hideProgress()
        }
    }

    override fun onNext() {
        binding?.storiesProgressView?.pause()
        /* //binding?.storiesProgressView?.pause()
         if (currentPosition < arrayOfVideoUrls.size - 1) {
             currentPosition++
             loadNextVideo()
         }*/
    }

    override fun onPrev() {
        binding?.storiesProgressView?.pause()
        /* //binding?.storiesProgressView?.pause()
         if (currentPosition > 0) {
             currentPosition--
             loadPreviousVideo()
         }*/
    }

    override fun onComplete() {
        binding?.consOverlayPlay?.visible()
        //exoPlayer?.stop()
        //binding?.storiesProgressView?.pause()
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

    private fun playRightInAnimation(view: View) {
        val inFromRight: Animation = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, +1.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f
        )
        inFromRight.duration = 500
        inFromRight.interpolator = AccelerateInterpolator()
        view.startAnimation(inFromRight)
    }

    private fun playLeftInAnimation(view: View) {
        val inFromLeft: Animation = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, -1.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f
        )
        inFromLeft.duration = 500
        inFromLeft.interpolator = AccelerateInterpolator()
        view.startAnimation(inFromLeft)
    }

}

fun startVideoActivity(activity: Activity) {
    val intent = Intent(activity, SupportVideoPlayerActivity::class.java)
    activity.startActivity(intent)
}