package com.boost.presignup.ui

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.boost.presignup.R
import com.boost.presignup.datamodel.SharedViewModel
import com.boost.presignup.locale.LocaleManager
import kotlinx.android.synthetic.main.fragment_main.view.*


class MainFragment : Fragment() {


    lateinit var root: View
    private lateinit var viewModel: SharedViewModel
    var videoDuration: Int = 0
    var videoCurrentPosition: Int = 0
    lateinit var timer: CountDownTimer
    lateinit var videoURLs: MutableList<String>
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

        videoURLs = mutableListOf<String>(
            "https://cdn.withfloats.com/boost/videos/en/intro.mp4",
            "https://cdn.withfloats.com/boost/videos/hi/intro.mp4",
            "https://cdn.withfloats.com/boost/videos/te/intro.mp4",
            "https://cdn.withfloats.com/boost/videos/ta/intro.mp4",
            "https://cdn.withfloats.com/boost/boost_01.mp4",
            "https://cdn.withfloats.com/boost/boost_01.mp4",
            "https://cdn.withfloats.com/boost/boost_01.mp4"
        )
        if (::localeManager.isInitialized && !localeManager.getLanguage()!!.isEmpty()) {
            val langType = localeManager.getLanguage()
            when (langType) {
                "en" -> {
                    setVideoURL(videoURLs.get(0))
                }
                "hi" -> {
                    setVideoURL(videoURLs.get(1))
                }
                "te" -> {
                    setVideoURL(videoURLs.get(2))
                }
                "ta" -> {
                    setVideoURL(videoURLs.get(3))
                }
            }
        } else {
            setVideoURL(videoURLs.get(0))
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
                } else {
                    setVolume(0)
                    muteState = true
                    root.mute_icon.setImageResource(R.drawable.ic_mute)
                }
            }
        }

        root.video_layout.setOnTouchListener (object : View.OnTouchListener {
            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN ->{
                       initialDownX = event.x
                    }
                    MotionEvent.ACTION_UP -> {
                        initialUpX = event.x
                        val deltaX = initialUpX - initialDownX
                        if (Math.abs(deltaX) > MIN_DISTANCE){
                            // Left to Right swipe action
                            if (initialUpX > initialDownX) {
                                Log.e("Swipe >>>>>>>>","Left to Right swipe [Next]")
                            } else {
                                Log.e("Swipe >>>>>>>>","Right to Left swipe [Previous]")
                                navToIntroScreen()
                            }
                        }else{
                            if(root.videoPlayer.isPlaying){
                                root.play_pause_lottie.visibility = View.VISIBLE
                                root.play_pause_lottie.playAnimation()
                                videoCurrentPosition = root.videoPlayer.getCurrentPosition(); //stopPosition is an int
                                videoDuration = videoDuration - videoCurrentPosition
                                root.videoPlayer.pause()
                                if (::timer.isInitialized) {
                                    timer.cancel()
                                }
                                if(::checkvideo.isInitialized){
                                    checkvideo.cancel()
                                }
                                }else{
                                root.play_pause_lottie.visibility = View.GONE
                                root.videoPlayer.seekTo(videoCurrentPosition);
                                root.videoPlayer.start()
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
        root.replay_intro.visibility = View.VISIBLE
        root.videoPlayer.visibility = View.VISIBLE
        root.video_loading.visibility = View.VISIBLE
        root.videoPlayer.start()
        viewModel.enableBottomView()
        checkVideoPlaying()
//        }


        root.skip_video.setOnClickListener {
            navToIntroScreen()
        }
        root.videoPlayer.setOnCompletionListener {
            Log.e("Video completed", "$$$$$$$$$$$$$$$$$$$")
            navToIntroScreen()
        }
//        root.videoPlayer.start()

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
        root.video_time.setText(initialTime + " " + getString(R.string.seconds))
    }

    fun checkVideoPlaying() {
        checkvideo = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
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
                    setVideoURL(videoURLs.get(0))
//                    root.videoPlayer.setVideoURI(Uri.parse(videoURLs.get(0)))
                    videoDuration = 0
                    if (::timer.isInitialized) {
                        timer.cancel()
                    }
                    root.video_time.setText(initialTime + " " + getString(R.string.seconds))
                    root.video_loading.visibility = View.VISIBLE
                    root.videoPlayer.start()
                }
                R.string.hindi -> {
                    setVideoURL(videoURLs.get(1))
//                    root.videoPlayer.setVideoURI(Uri.parse(videoURLs.get(1)))
                    videoDuration = 0
                    if (::timer.isInitialized) {
                        timer.cancel()
                    }
                    root.video_time.setText(initialTime + " " + getString(R.string.seconds))
                    root.video_loading.visibility = View.VISIBLE
                    root.videoPlayer.start()

                }
                R.string.kannada -> {
                    setVideoURL(videoURLs.get(2))
//                    root.videoPlayer.setVideoURI(Uri.parse(videoURLs.get(2)))
                    videoDuration = 0
                    if (::timer.isInitialized) {
                        timer.cancel()
                    }
                    root.video_time.setText(initialTime + " " + getString(R.string.seconds))
                    root.video_loading.visibility = View.VISIBLE
                    root.videoPlayer.start()

                }
                R.string.telugu -> {
                    setVideoURL(videoURLs.get(3))
//                    root.videoPlayer.setVideoURI(Uri.parse(videoURLs.get(3)))
                    videoDuration = 0
                    if (::timer.isInitialized) {
                        timer.cancel()
                    }
                    root.video_time.setText(initialTime + " " + getString(R.string.seconds))
                    root.video_loading.visibility = View.VISIBLE
                    root.videoPlayer.start()

                }
                R.string.malayalam -> {
                    setVideoURL(videoURLs.get(4))
//                    root.videoPlayer.setVideoURI(Uri.parse(videoURLs.get(4)))
                    videoDuration = 0
                    if (::timer.isInitialized) {
                        timer.cancel()
                    }
                    root.video_time.setText(initialTime + " " + getString(R.string.seconds))
                    root.video_loading.visibility = View.VISIBLE
                    root.videoPlayer.start()

                }
                R.string.tamil -> {
                    setVideoURL(videoURLs.get(5))
//                    root.videoPlayer.setVideoURI(Uri.parse(videoURLs.get(5)))
                    videoDuration = 0
                    if (::timer.isInitialized) {
                        timer.cancel()
                    }
                    root.video_time.setText(initialTime + " " + getString(R.string.seconds))
                    root.video_loading.visibility = View.VISIBLE
                    root.videoPlayer.start()

                }
                R.string.marathi -> {
                    setVideoURL(videoURLs.get(6))
//                    root.videoPlayer.setVideoURI(Uri.parse(videoURLs.get(6)))
                    videoDuration = 0
                    if (::timer.isInitialized) {
                        timer.cancel()
                    }
                    root.video_time.setText(initialTime + " " + getString(R.string.seconds))
                    root.video_loading.visibility = View.VISIBLE
                    root.videoPlayer.start()
                }
            }
        })
    }

    fun navToIntroScreen() {
        root.video_layout.visibility = View.GONE
        findNavController().navigate(R.id.action_videoPlayerFragment_to_introFragment)
        root.videoPlayer.suspend()
        if (::timer.isInitialized) {
            timer.cancel()
        }
        videoDuration = 0
    }

    fun setVideoURL(url: String) {
        root.videoPlayer.setVideoURI(Uri.parse(url))
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
