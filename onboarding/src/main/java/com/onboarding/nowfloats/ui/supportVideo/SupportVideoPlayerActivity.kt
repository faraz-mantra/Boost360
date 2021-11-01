package com.onboarding.nowfloats.ui.supportVideo

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.MotionEvent
import android.view.View.OnTouchListener
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.base.AppBaseActivity
import com.onboarding.nowfloats.databinding.ActivitySupportVideoPlayerBinding
import com.onboarding.nowfloats.viewmodel.SupportVideoViewModel
import jp.shts.android.storiesprogressview.StoriesProgressView


class SupportVideoPlayerActivity :
    AppBaseActivity<ActivitySupportVideoPlayerBinding, SupportVideoViewModel>(),
    StoriesProgressView.StoriesListener {

    private var exoPlayer: SimpleExoPlayer? = null
    var pressTime = 0L
    var limit = 500L
    private var counter = 0
    private var arrayOfVideoUrls = arrayListOf<String>()
    private var playWhenReadyLocal = true
    private var currentWindow = 0
    private var playbackPosition = 0L

    private fun initializePlayer() {
        arrayOfVideoUrls = arrayListOf(
            "https://cdn.nowfloats.com/manage/assets/Content/videos/vertical/BoostHowToTrackPerformanceAndWebsiteReport.mp4",
            "https://cdn.nowfloats.com/manage/assets/Content/videos/vertical/BoostHowToAddTestimonial.mp4"
        )
        exoPlayer = SimpleExoPlayer.Builder(this)
            .build()
            .also { exoPlayer ->
                binding?.videoView?.player = exoPlayer
                val mediaItem = MediaItem.fromUri(arrayOfVideoUrls[0])
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.playWhenReady = playWhenReadyLocal
                exoPlayer.seekTo(currentWindow, playbackPosition)
                exoPlayer.prepare()
            }
    }

    // on below line we are creating a new method for adding touch listener
    private val onTouchListener = OnTouchListener { v, event -> // inside on touch method we are
        // getting action on below line.
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

                // on action down when we press our screen
                // the story will pause for specific time.
                pressTime = System.currentTimeMillis()

                // on below line we are pausing our indicator.
                binding?.statusBarView?.storiesProgressView!!.pause()
                return@OnTouchListener false
            }
            MotionEvent.ACTION_UP -> {

                // in action up case when user do not touches
                // screen this method will skip to next image.
                val now = System.currentTimeMillis()

                // on below line we are resuming our progress bar for status.
                binding?.statusBarView?.storiesProgressView!!.resume()

                // on below line we are returning if the limit < now - presstime
                return@OnTouchListener limit < now - pressTime
            }
        }
        false
    }

    override fun onCreateView() {
        super.onCreateView()
        initUI()
    }

    private fun initUI() {
        getAndSaveFeatureSupportVideos()
        // on below line we are initializing our variables.

        // on below line we are setting the total count for our stories.

        // on below line we are setting the total count for our stories.
        binding?.statusBarView?.storiesProgressView?.setStoriesCount(arrayOfVideoUrls.size)

        // on below line we are setting story duration for each story.

        // on below line we are setting story duration for each story.
        binding?.statusBarView?.storiesProgressView?.setStoryDuration(3000L)

        // on below line we are calling a method for set
        // on story listener and passing context to it.

        // on below line we are calling a method for set
        // on story listener and passing context to it.
        binding?.statusBarView?.storiesProgressView?.setStoriesListener(this)

        // below line is use to start stories progress bar.

        // below line is use to start stories progress bar.
        binding?.statusBarView?.storiesProgressView?.startStories(counter)

        // initializing our image view.

        // on below line we are setting image to our image view.

        // on below line we are setting image to our image view.
        binding?.statusBarView?.image?.setImageResource(R.drawable.exo_edit_mode_logo)

        // below is the view for going to the previous story.
        // initializing our previous view.

        // below is the view for going to the previous story.

        // adding on click listener for our reverse view.

        // adding on click listener for our reverse view.
        binding?.statusBarView?.reverse?.setOnClickListener { // inside on click we are
            // reversing our progress view.
            binding?.statusBarView?.storiesProgressView?.reverse()
        }

        // on below line we are calling a set on touch
        // listener method to move towards previous image.

        // on below line we are calling a set on touch
        // listener method to move towards previous image.
        binding?.statusBarView?.reverse?.setOnTouchListener(onTouchListener)

        // on below line we are initializing
        // view to skip a specific story.

        // on below line we are initializing
        // view to skip a specific story.
        binding?.statusBarView?.skip?.setOnClickListener {
            // inside on click we are
            // skipping the story progress view.
            // inside on click we are
            // reversing our progress view.
            binding?.statusBarView?.storiesProgressView?.skip()
        }
        // on below line we are calling a set on touch
        // listener method to move to next story.
        // on below line we are calling a set on touch
        // listener method to move to next story.
        binding?.statusBarView?.skip?.setOnTouchListener(onTouchListener)

    }


    override fun getLayout(): Int {
        return R.layout.activity_support_video_player
    }

    override fun getViewModelClass(): Class<SupportVideoViewModel> {
        return SupportVideoViewModel::class.java
    }

    override fun onNext() {
        binding?.statusBarView?.image?.setImageResource(R.drawable.exo_edit_mode_logo)
    }

    override fun onPrev() {
        binding?.statusBarView?.image?.setImageResource(R.drawable.exo_edit_mode_logo)
    }

    override fun onComplete() {
        onBackPressed()
    }

    private fun getAndSaveFeatureSupportVideos() {
        viewModel.getSupportVideos().observeForever {
            if (it.isSuccess()) {
                Log.i("kjsbcjk", it.toString())
            }
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
            playbackPosition = this.currentPosition
            currentWindow = this.currentWindowIndex
            playWhenReadyLocal = this.playWhenReady
            release()
        }
        exoPlayer = null
    }

}

fun startVideoActivity(activity: Activity) {
    val intent = Intent(activity, SupportVideoPlayerActivity::class.java)
    activity.startActivity(intent)
}