package com.boost.presignup.ui

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.boost.presignup.R
import com.boost.presignup.datamodel.SharedViewModel
import com.boost.presignup.locale.LocaleManager
import com.boost.presignup.utils.WebEngageController
import com.framework.webengageconstant.*
import kotlinx.android.synthetic.main.fragment_main.view.*


class MainFragment : Fragment() {


    lateinit var root: View
    private lateinit var viewModel: SharedViewModel
    var videoDuration: Int = 0
    var videoCurrentPosition: Int = 0
    lateinit var timer: CountDownTimer
    lateinit var videoURLs: MutableList<String>
    lateinit var splashImageResources: MutableList<Int>
    lateinit var checkvideo: CountDownTimer
    val initialTime = "00"
    lateinit var localeManager: LocaleManager
    var initialDownX: Float = 0.0f
    var initialUpX: Float = 0.0f
    val MIN_DISTANCE = 200
    var muteState = false
    lateinit var mediaPlayer: MediaPlayer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_main, container, false)

        localeManager = LocaleManager(requireContext())

//        root.animation_view.setAnimation(R.raw.splash_globe)
//        root.animation_view.addAnimatorListener(object : Animator.AnimatorListener {
//            override fun onAnimationRepeat(animation: Animator?) {
//                Log.d("onAnimationRepeat", "")
//            }
//
//            override fun onAnimationEnd(animation: Animator?) {
//                Log.d("onAnimationEnd", "")
////                findNavController().navigate(R.id.action_splashFragment_to_videoPlayerFragment)
//                root.animation_view.visibility = View.GONE
//                root.replay_intro.visibility = View.VISIBLE
//                root.videoPlayer.visibility = View.VISIBLE
//                root.video_loading.visibility = View.VISIBLE
//                root.videoPlayer.start()
//                viewModel.enableBottomView()
//                viewModel.initialLoad(false)
//                checkVideoPlaying()
//            }
//
//            override fun onAnimationCancel(animation: Animator?) {
//                Log.d("onAnimationCancel", "")
//            }
//
//            override fun onAnimationStart(animation: Animator?) {
//                Log.d("onAnimationStart", "")
//            }
//
//        })

        //NOTE: Both videoURLs and splashImageResources should always be of the same length.
        //each videoURL has the corresponding SplashImageResource stored in the corresponding index of the other array
        videoURLs = mutableListOf<String>(
            "https://cdn.nowfloats.com/jioonline/android/videos/JioOnlineHighResolution.mp4",
            "https://cdn.nowfloats.com/jioonline/android/videos/JioOnlineHighResolution.mp4",
            "https://cdn.nowfloats.com/jioonline/android/videos/JioOnlineHighResolution.mp4",
            "https://cdn.nowfloats.com/jioonline/android/videos/JioOnlineHighResolution.mp4",
            "https://cdn.withfloats.com/boost/boost_01.mp4",
            "https://cdn.withfloats.com/boost/boost_01.mp4",
            "https://cdn.withfloats.com/boost/boost_01.mp4"
        )
        splashImageResources = mutableListOf<Int>(
                R.drawable.intro_video_splash_en,
                R.drawable.intro_video_splash_hi,
                R.drawable.intro_video_splash_telegu,
                R.drawable.intro_video_splash_tamil,
                R.drawable.intro_video_splash_en,
                R.drawable.intro_video_splash_en,
                R.drawable.intro_video_splash_en
        )

        if (::localeManager.isInitialized && !localeManager.getLanguage()!!.isEmpty()) {
            val langType = localeManager.getLanguage()
            when (langType) {
                "en" -> {
                    setVideoURL(videoURLs.get(0), splashImageResources.get(0))
                }
                "hi" -> {
                    setVideoURL(videoURLs.get(1), splashImageResources.get(1))
                }
                "te" -> {
                    setVideoURL(videoURLs.get(2), splashImageResources.get(2))
                }
                "ta" -> {
                    setVideoURL(videoURLs.get(3), splashImageResources.get(3))
                }
            }
        } else {
            setVideoURL(videoURLs.get(0), splashImageResources.get(0))
        }

        val onInfoToPlayStateListener = MediaPlayer.OnInfoListener { mp, what, extra ->
            when (what) {
                MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {
                    if (::timer.isInitialized) {
                        timer.cancel()
                    }
                    root.video_loading.visibility = View.GONE
                    if (videoDuration < 1) {
                        videoDuration = mp.duration
                        //checkvideo playing timer
                        if (!root.videoPlayer.isPlaying && ::checkvideo.isInitialized) {
                            checkvideo.cancel()
                        }
                    }else{
                        videoDuration = mp.duration - mp.currentPosition
                    }
                    setVideoTimerCountDown()
                }
                MediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                    if (::timer.isInitialized) {
                        timer.cancel()
                    }
                    root.video_loading.visibility = View.VISIBLE
                    root.video_time.setText(getString(R.string.loading))
//                    if (videoDuration < 1) {
//                        videoDuration = mp.duration
//                    }else{
//                        videoDuration = mp.duration - mp.currentPosition
//                    }
//                    timer.cancel()
//                    setVideoTimerCountDown()
                }
                MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                    if (::timer.isInitialized) {
                        timer.cancel()
                    }
                    root.video_loading.visibility = View.GONE
//                    if (videoDuration < 1) {
//                        videoDuration = mp.duration
//                    }else{
//                        videoDuration = mp.duration - mp.currentPosition
//                    }
                    setVideoTimerCountDown()
                }
            }
            true
        }
        root.videoPlayer!!.setOnInfoListener(onInfoToPlayStateListener)
        root.videoPlayer!!.setOnPreparedListener {
            mediaPlayer = it
        }
        root.play_pause_lottie.setAnimation(R.raw.play_pause_lottie)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        root.mute_video.setOnClickListener {
            if(root.videoPlayer.isPlaying) {
                if (muteState) {
                    setVolume(100)
                    muteState = false
                    root.mute_icon.setImageResource(R.drawable.ic_unmute)
                    WebEngageController.trackEvent(PS_CLICKED_UNMUTE_INTRO_VIDEO, VIDEO_UNMUTED, NO_EVENT_VALUE)
                } else {
                    setVolume(0)
                    muteState = true
                    root.mute_icon.setImageResource(R.drawable.ic_mute)
                    WebEngageController.trackEvent(PS_CLICKED_MUTE_INTRO_VIDEO, VIDEO_MUTED, NO_EVENT_VALUE)
                }
            }
        }

        root.video_layout.setOnTouchListener(object : View.OnTouchListener {
            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialDownX = event.x
                    }
                    MotionEvent.ACTION_UP -> {
                        initialUpX = event.x
                        val deltaX = initialUpX - initialDownX
                        if (Math.abs(deltaX) > MIN_DISTANCE) {
                            // Left to Right swipe action
                            if (initialUpX > initialDownX) {
                                Log.e("Swipe >>>>>>>>", "Left to Right swipe [Next]")
                            } else {
                                Log.e("Swipe >>>>>>>>", "Right to Left swipe [Previous]")
                                navToIntroScreen()
                                WebEngageController.trackEvent(PS_SWIPED_ON_THE_INTRO_VIDEO, VIDEO_AREA_SWIPED, NO_EVENT_VALUE)
                            }
                        } else {
                            WebEngageController.trackEvent(PS_CLICKED_INTRO_VIDEO_AREA, VIDEO_AREA_CLICKED, NO_EVENT_VALUE)
                            if (root.videoPlayer.isPlaying) {
                                root.play_pause_lottie.visibility = View.VISIBLE
                                root.play_pause_lottie.playAnimation()
                                videoCurrentPosition = root.videoPlayer.getCurrentPosition(); //stopPosition is an int
                                videoDuration = videoDuration - videoCurrentPosition
                                root.videoPlayer.pause()
                                if (::timer.isInitialized) {
                                    timer.cancel()
                                }
                                if (::checkvideo.isInitialized) {
                                    checkvideo.cancel()
                                }
                            } else {
                                root.play_pause_lottie.visibility = View.GONE
                                root.videoPlayer.seekTo(videoCurrentPosition);
                                root.videoPlayer.start()
                                WebEngageController.trackEvent(PS_INTRO_VIDEO_STARTED, VIDEO_STARTED, NO_EVENT_VALUE)
                                setVideoTimerCountDown()
                            }
                        }
                    }
                }

                return true
            }
        })
        activity?.let {
            viewModel = ViewModelProviders.of(it).get(SharedViewModel::class.java)
        }

//        if (viewModel.initialLoadStatus.value == null || viewModel.initialLoadStatus.value!!) {
//            viewModel.initialLoad(true)
//            root.animation_view.playAnimation()
//        }else{
//            root.animation_view.visibility = View.GONE

        root.skip_video.setOnClickListener {
            WebEngageController.trackEvent(PS_CLICKED_INTRO_VIDEO_SKIP, VIDEO_SKIPPED, NO_EVENT_VALUE)
            navToIntroScreen()
        }

        root.videoPlayer.setOnCompletionListener {
            Log.e("Video completed", "$$$$$$$$$$$$$$$$$$$")
            navToIntroScreen()
        }

        root.introVideoSplash.setOnClickListener{
            WebEngageController.trackEvent(PS_INTRO_VIDEO_SPLASH_CLICKED, START_INTRO_VIDEO, NO_EVENT_VALUE)
            root.play_pause_lottie.visibility = View.GONE
            WebEngageController.trackEvent(PS_INTRO_VIDEO_STARTED, VIDEO_STARTED, NO_EVENT_VALUE)
            root.videoPlayer.start()
            root.videoPlayer.seekTo(0)
            root.introVideoSplash.visibility = View.GONE
            root.replay_intro.visibility = View.VISIBLE
            root.videoPlayer.visibility = View.VISIBLE
            root.video_loading.visibility = View.VISIBLE
            checkVideoPlaying()
        }

        resetVideo()
//        root.videoPlayer.start()
        viewModel.enableBottomView()
        setUpMvvm()
    }

    override fun onStop() {
        super.onStop()
        root.videoPlayer.suspend()
        if (::timer.isInitialized) {
            timer.cancel()
        }
        if (::checkvideo.isInitialized) {
            checkvideo.cancel()
        }
    }

    override fun onResume() {
        super.onResume()
        videoDuration = 0
        if (::timer.isInitialized) {
            timer.cancel()
        }
        resetVideo()
    }

    fun resetVideo(){
        root.introVideoSplash.visibility = View.VISIBLE
        root.replay_intro.visibility = View.GONE
        root.videoPlayer.visibility = View.GONE
        root.video_loading.visibility = View.GONE

        root.video_time.setText(initialTime + " " + getString(R.string.seconds))
    }

    fun checkVideoPlaying() {
        checkvideo = object : CountDownTimer(6000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                WebEngageController.trackEvent(PS_INTRO_VIDEO_FAILED_TO_START, SIX_THOUSANDS, NO_EVENT_VALUE)
                Log.e("Video not playing", "###################")
                if (!root.videoPlayer.isPlaying) {
                    navToIntroScreen()
                }
            }
        }.start()
    }

    fun setVideoTimerCountDown() {
        timer = object : CountDownTimer(videoDuration.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val videoTime = millisUntilFinished / 1000
                if (videoDuration == 0) {
                    root.video_time.setText(initialTime + " " + getString(R.string.seconds))
                } else {
                    root.video_time.setText(videoTime.toString() + " " + getString(R.string.seconds))
                }
            }

            override fun onFinish() {
                Log.e("videoCompleted", "&&&&&&&&&&&&&")
            }
        }
        timer.start()
    }

    private fun setUpMvvm() {
        viewModel.navigation.observe(viewLifecycleOwner, Observer {
            Log.e(it, ">>>>>>>>>>>>>>44")
        })


        viewModel.changeLocaleLag.observe(viewLifecycleOwner, Observer {
            Log.e(it.toString(), ">>>>>>")
            root.videoPlayer.suspend()
            viewModel.postValueToUpdateButtonStyle(it)
            when (it) {
                R.string.english -> {
                    setVideoURL(videoURLs.get(0), splashImageResources.get(0))
//                    root.videoPlayer.setVideoURI(Uri.parse(videoURLs.get(0)))
                    videoDuration = 0
                    if (::timer.isInitialized) {
                        timer.cancel()
                    }
                    root.video_time.setText(initialTime + " " + getString(R.string.seconds))
                    root.video_loading.visibility = View.VISIBLE
                    root.videoPlayer.start()
                    WebEngageController.trackEvent(PS_INTRO_VIDEO_STARTED_ENGLISH, VIDEO_STARTED, NO_EVENT_VALUE)
                }
                R.string.hindi -> {
                    setVideoURL(videoURLs.get(1), splashImageResources.get(1))
                    videoDuration = 0
                    if (::timer.isInitialized) {
                        timer.cancel()
                    }
                    root.video_time.setText(initialTime + " " + getString(R.string.seconds))
                    root.video_loading.visibility = View.VISIBLE
                    root.videoPlayer.start()
                    WebEngageController.trackEvent(PS_INTRO_VIDEO_STARTED_HINDI, VIDEO_STARTED, NO_EVENT_VALUE)
                }
                R.string.kannada -> {
                    setVideoURL(videoURLs.get(2), splashImageResources.get(2))
//                    root.videoPlayer.setVideoURI(Uri.parse(videoURLs.get(2)))
                    videoDuration = 0
                    if (::timer.isInitialized) {
                        timer.cancel()
                    }
                    root.video_time.setText(initialTime + " " + getString(R.string.seconds))
                    root.video_loading.visibility = View.VISIBLE
                    root.videoPlayer.start()
                    WebEngageController.trackEvent(PS_INTRO_VIDEO_STARTED_KANNADA, VIDEO_STARTED, NO_EVENT_VALUE)
                }
                R.string.telugu -> {
                    setVideoURL(videoURLs.get(3), splashImageResources.get(3))
//                    root.videoPlayer.setVideoURI(Uri.parse(videoURLs.get(3)))
                    videoDuration = 0
                    if (::timer.isInitialized) {
                        timer.cancel()
                    }
                    root.video_time.setText(initialTime + " " + getString(R.string.seconds))
                    root.video_loading.visibility = View.VISIBLE
                    root.videoPlayer.start()
                    WebEngageController.trackEvent(PS_INTRO_VIDEO_STARTED_TELUGU, VIDEO_STARTED, NO_EVENT_VALUE)
                }
                R.string.malayalam -> {
                    setVideoURL(videoURLs.get(4), splashImageResources.get(4))
//                    root.videoPlayer.setVideoURI(Uri.parse(videoURLs.get(4)))
                    videoDuration = 0
                    if (::timer.isInitialized) {
                        timer.cancel()
                    }
                    root.video_time.setText(initialTime + " " + getString(R.string.seconds))
                    root.video_loading.visibility = View.VISIBLE
                    root.videoPlayer.start()
                    WebEngageController.trackEvent(PS_INTRO_VIDEO_STARTED_MALAYALAM, VIDEO_STARTED, NO_EVENT_VALUE)
                }
                R.string.tamil -> {
                    setVideoURL(videoURLs.get(5), splashImageResources.get(5))
//                    root.videoPlayer.setVideoURI(Uri.parse(videoURLs.get(5)))
                    videoDuration = 0
                    if (::timer.isInitialized) {
                        timer.cancel()
                    }
                    root.video_time.setText(initialTime + " " + getString(R.string.seconds))
                    root.video_loading.visibility = View.VISIBLE
                    root.videoPlayer.start()
                    WebEngageController.trackEvent(PS_INTRO_VIDEO_STARTED_TAMIL, VIDEO_STARTED, NO_EVENT_VALUE)
                }
                R.string.marathi -> {
                    setVideoURL(videoURLs.get(6), splashImageResources.get(6))
//                    root.videoPlayer.setVideoURI(Uri.parse(videoURLs.get(6)))
                    videoDuration = 0
                    if (::timer.isInitialized) {
                        timer.cancel()
                    }
                    root.video_time.setText(initialTime + " " + getString(R.string.seconds))
                    root.video_loading.visibility = View.VISIBLE
                    root.videoPlayer.start()
                    WebEngageController.trackEvent(PS_INTRO_VIDEO_STARTED_MARATHI, VIDEO_STARTED, NO_EVENT_VALUE)
                }
            }
        })
    }

    fun navToIntroScreen() {
        try {
            root.video_layout.visibility = View.GONE
            findNavController().navigate(R.id.action_videoPlayerFragment_to_introFragment)
            root.videoPlayer.suspend()
            if (::timer.isInitialized) {
                timer.cancel()
            }
            videoDuration = 0
        } catch (e: Exception) {
            root.video_layout.visibility = View.VISIBLE
        }
    }

    fun setVideoURL(videoUrl: String, splashImageUrlResource: Int) {
        root.videoPlayer.setVideoURI(Uri.parse(videoUrl))
        root.introVideoSplash.setImageResource(splashImageUrlResource)
    }

    private fun setVolume(amount: Int) {
        val max = 100
        val numerator: Double =
            if (max - amount > 0) Math.log(max - amount.toDouble()) else 0.0
        val volume =
            (1 - numerator / Math.log(max.toDouble())).toFloat()
        mediaPlayer.setVolume(volume, volume)
    }
}
