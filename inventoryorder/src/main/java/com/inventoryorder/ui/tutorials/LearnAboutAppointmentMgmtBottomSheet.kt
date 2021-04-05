package com.inventoryorder.ui.tutorials

import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.observeOnce
import com.framework.views.customViews.CustomTextView
import com.inventoryorder.BaseOrderApplication
import com.inventoryorder.R
import com.inventoryorder.base.AppBaseFragment
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.databinding.BottomSheetLearnAboutAppointmentMgmtBinding
import com.inventoryorder.databinding.FragmentVideoPagerItemBinding
import com.inventoryorder.ui.tutorials.model.LearnAboutAppointmentMgmt
import com.inventoryorder.ui.tutorials.model.VIDEOSItem
import com.inventoryorder.ui.tutorials.viewmodel.TutorialViewModel
import java.util.*

class LearnAboutAppointmentMgmtBottomSheet : BaseBottomSheetDialog<BottomSheetLearnAboutAppointmentMgmtBinding, TutorialViewModel>() {
    private var data: LearnAboutAppointmentMgmt? = null

    override fun getLayout(): Int {
        return R.layout.bottom_sheet_learn_about_appointment_mgmt
    }

    override fun getViewModelClass(): Class<TutorialViewModel> {
        return TutorialViewModel::class.java
    }

    override fun onCreateView() {
        setOnClickListener(binding?.civClose, binding?.civSpeakHowItWorks, binding?.civSpeakTips, binding?.actionContactSupport, binding?.actionReadFaq)
        binding?.vpVideos?.clipToPadding = false
        binding?.vpVideos?.setPadding(40, 0, 40, 0)
        setData()
    }

    private fun setData() {
        viewModel?.getLearnAppointmentmgmtResponse()?.observeOnce(viewLifecycleOwner, {
            this.data = it
            binding?.ctvHowItWorksContent?.text = data?.contents?.description
            val pagerAdapter = PagerAdapter(childFragmentManager, behavior = FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, data?.contents?.videos)
            binding?.vpVideos?.adapter = pagerAdapter
            binding?.diPagerIndicator?.setViewPager(binding?.vpVideos!!)
            loadTips(data!!)
            textToSpeechEngine


        })
    }


    private val textToSpeechEngine: TextToSpeech by lazy {
        // Pass in context and the listener.
        TextToSpeech(BaseOrderApplication.instance.applicationContext) { status ->
            // set our locale only if init was success.
            if (status == TextToSpeech.SUCCESS) {
                textToSpeechEngine.language = Locale.UK
            }
        }
    }

    private fun loadTips(learnAboutAppointmentMgmt: LearnAboutAppointmentMgmt) {
        learnAboutAppointmentMgmt.tips?.forEach {
            val view = layoutInflater.inflate(R.layout.tips_text_item, binding?.llTipsContainer, false)
            val textTips = view.findViewById<CustomTextView>(R.id.ctv_tips)
            textTips.text = it
            binding?.llTipsContainer?.addView(view)
        }

    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.civSpeakHowItWorks -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // Call Lollipop+ function
                    textToSpeechEngine.speak(data?.contents?.description, TextToSpeech.QUEUE_FLUSH, null, "tts1")
                } else {
                    // Call Legacy function
                    textToSpeechEngine.speak(data?.contents?.description, TextToSpeech.QUEUE_FLUSH, null)
                }

            }
            binding?.civSpeakTips -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // Call Lollipop+ function
                    textToSpeechEngine.speak(data?.tips.toString(), TextToSpeech.QUEUE_FLUSH, null, "tts1")
                } else {
                    // Call Legacy function
                    textToSpeechEngine.speak(data?.tips?.toString(), TextToSpeech.QUEUE_FLUSH, null)
                }

            }
            binding?.actionReadFaq -> {
                dismiss()
                val bottomSheetAppointmentFaq = BottomSheetAppointmentFaq()
                bottomSheetAppointmentFaq.show(parentFragmentManager, BottomSheetAppointmentFaq::class.java.name)
            }
            binding?.actionContactSupport -> {
            }
            binding?.civClose -> {
                dismiss()
            }
        }
    }

}

class PagerAdapter(fm: FragmentManager, behavior: Int, private val videos: List<VIDEOSItem?>?) : FragmentStatePagerAdapter(fm, behavior) {
    override fun getCount(): Int {
        return videos?.size ?: 0
    }

    override fun getItem(position: Int): Fragment {
        return VideoFragment.newInstance(videos?.get(position))
    }

}

class VideoFragment : AppBaseFragment<FragmentVideoPagerItemBinding, TutorialViewModel>() {
    companion object {
        fun newInstance(videosItem: VIDEOSItem?): VideoFragment {
            val args = Bundle()
            args.putSerializable(IntentConstant.VIDEO_ITEM.name, videosItem)
            val fragment = VideoFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_video_pager_item
    }

    override fun getViewModelClass(): Class<TutorialViewModel> {
        return TutorialViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        val videosItem = arguments?.getSerializable(IntentConstant.VIDEO_ITEM.name) as? VIDEOSItem
        setView(videosItem)

    }

    private fun setView(videosItem: VIDEOSItem?) {
        binding?.ctvVideoTitle?.text = videosItem?.videoTitle
    }
}
