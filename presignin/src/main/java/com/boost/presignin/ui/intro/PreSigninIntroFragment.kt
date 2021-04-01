package com.boost.presignin.ui.intro

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.VideoView
import androidx.core.view.isVisible
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.databinding.FragmentPreSigninIntroBinding
import com.boost.presignin.model.IntroItem
import com.framework.models.BaseViewModel

class PreSigninIntroFragment : AppBaseFragment<FragmentPreSigninIntroBinding, BaseViewModel>() {


    private val TAG = "IntroFragment"

    private var buffering = true;

    companion object {
        private var INTRO_ITEM = "INTRO_ITEM"
        private var POSITION = "POSITION"

        @JvmStatic
        fun newInstance(introItem: IntroItem, position: Int) =
                PreSigninIntroFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable(INTRO_ITEM, introItem)
                        putInt(POSITION, position)
                    }
                }
    }

    private val introItem by lazy { requireArguments().getSerializable(INTRO_ITEM) as IntroItem }
    private val position by lazy { requireArguments().getInt(POSITION) }
    override fun getLayout(): Int {
        return R.layout.fragment_pre_signin_intro
    }

    override fun onCreateView() {
        super.onCreateView()
        binding?.introItem = introItem;
        binding?.presiginIntroImg?.setImageResource(introItem.imageResource)


        if (position == 0) {
            binding?.boostLogo?.isVisible = true;
            binding?.playPauseLottie?.setAnimation(R.raw.play_pause_lottie);
            binding?.presiginIntroImg?.setOnClickListener {
                binding?.introImgContainer?.isVisible = false;
                binding?.videoViewContainer?.isVisible = true;
                binding?.videoView?.setOnPreparedListener {
                  //  it.setDataSource("https://cdn.withfloats.com/boost/videos/en/intro.mp4")
                }
                binding?.videoView?.setVideoURI(Uri.parse("https://cdn.withfloats.com/boost/videos/en/intro.mp4"));
                binding?.videoView?.setOnInfoListener { p0, p1, p2 ->
                    when (p1) {
                        MediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                            binding?.progressBar?.isVisible = true
                            buffering = true
                        }

                        MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                            binding?.progressBar?.isVisible = false
                            buffering = false
                        }

                        MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {

                        }
                    }
                    true
                }
                binding?.videoView?.setOnClickListener {
                    it as VideoView
                    if (it.isPlaying) {
                        it.pause();
                        binding?.playPauseLottie?.isVisible = true;
                    }
                }
            }
            binding?.playPauseLottie?.setOnClickListener {
                binding?.videoView?.start();
                it.isVisible = false;
            }

        }
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }


    override fun onPause() {
        super.onPause()
        if (position == 0) binding?.videoView?.pause()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onStop() {
        super.onStop()

    }

    override fun onDestroy() {
        super.onDestroy()
    }

}