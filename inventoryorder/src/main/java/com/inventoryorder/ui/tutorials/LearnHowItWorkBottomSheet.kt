package com.inventoryorder.ui.tutorials

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.bumptech.glide.Glide
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.observeOnce
import com.framework.views.customViews.CustomTextView
import com.inventoryorder.BaseOrderApplication
import com.inventoryorder.R
import com.inventoryorder.base.AppBaseFragment
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.databinding.BottomSheetLearnHowItWorksBinding
import com.inventoryorder.databinding.FragmentVideoPagerItemBinding
import com.inventoryorder.ui.tutorials.model.LearnAboutAppointmentMgmt
import com.inventoryorder.ui.tutorials.model.VIDEOSItem
import com.inventoryorder.ui.tutorials.viewmodel.TutorialViewModel
import java.util.*

class LearnHowItWorkBottomSheet : BaseBottomSheetDialog<BottomSheetLearnHowItWorksBinding, TutorialViewModel>() {

  private var data: LearnAboutAppointmentMgmt? = null

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_learn_how_it_works
  }

  override fun getViewModelClass(): Class<TutorialViewModel> {
    return TutorialViewModel::class.java
  }

  override fun onDetach() {
    super.onDetach()
    textToSpeechEngine.stop()

  }

  override fun onPause() {
    super.onPause()
    textToSpeechEngine.stop()
  }

  override fun onCreateView() {
    setOnClickListener(binding?.civClose, binding?.civSpeakHowItWorks, binding?.civSpeakTips, binding?.actionContactSupport, binding?.actionReadFaq)
    binding?.vpVideos?.clipToPadding = false
    binding?.vpVideos?.setPadding(40, 0, 40, 0)
    setData()
    textToSpeechEngine
  }


  private fun setData() {
    viewModel?.getLearnStaffResponse()?.observeOnce(viewLifecycleOwner, {
      this.data = it
      binding?.ctvHowItWorksContent?.text = data?.contents?.description
      val pagerAdapter = PagerAdapter(childFragmentManager, behavior = FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, data?.contents?.videos)
      binding?.vpVideos?.adapter = pagerAdapter
      binding?.diPagerIndicator?.setViewPager(binding?.vpVideos!!)
      loadTips(data!!)
      binding?.ctvVideosHeading?.text = "${if (data?.contents?.videos?.size ?: 0 > 1) "VIDEOS" else "VIDEO"} (${data?.contents?.videos?.size})"
    })
  }


  private val textToSpeechEngine: TextToSpeech by lazy {
    // Pass in context and the listener.
    TextToSpeech(BaseOrderApplication.instance.applicationContext) { status ->
      // set our locale only if init was success.
      if (status == TextToSpeech.SUCCESS) {
        textToSpeechEngine.language = Locale.UK
        textToSpeechEngine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
          override fun onStart(utteranceId: String?) {
          }

          override fun onDone(utteranceId: String?) {
            binding?.civSpeakHowItWorks?.setTintColor(Color.parseColor("#e1e1e1"))
            binding?.civSpeakTips?.setTintColor(Color.parseColor("#e1e1e1"))

          }

          override fun onError(utteranceId: String?) {
          }

        })

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
        if (textToSpeechEngine.isSpeaking) {
          binding?.civSpeakHowItWorks?.setTintColor(Color.parseColor("#e1e1e1"))
          textToSpeechEngine.stop()
        } else {
          binding?.civSpeakHowItWorks?.setTintColor(Color.parseColor("#ffb900"))
          speak(data?.contents?.description.toString())
        }
      }
      binding?.civSpeakTips -> {
        if (textToSpeechEngine.isSpeaking) {
          textToSpeechEngine.stop()
          binding?.civSpeakTips?.setTintColor(Color.parseColor("#e1e1e1"))
        } else {
          binding?.civSpeakTips?.setTintColor(Color.parseColor("#ffb900"))
          speak(data?.tips.toString())
        }

      }
      binding?.actionReadFaq -> {
        dismiss()
        val bottomSheetAppointmentFaq = FAQsReadBottomSheet()
        bottomSheetAppointmentFaq.show(parentFragmentManager, FAQsReadBottomSheet::class.java.name)
      }
      binding?.actionContactSupport -> {
      }
      binding?.civClose -> {
        dismiss()
      }
    }
  }

  private fun speak(string: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      // Call Lollipop+ function
      textToSpeechEngine.speak(string, TextToSpeech.QUEUE_FLUSH, null, "tts1")
    } else {
      // Call Legacy function
      textToSpeechEngine.speak(string, TextToSpeech.QUEUE_FLUSH, null)
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
    binding?.root?.setOnClickListener { openTutorialBottomSheet(videosItem) }
  }

  private fun openTutorialBottomSheet(videosItem: VIDEOSItem?) {
    val tutorialVideosBottomSheet = TutorialVideosBottomSheet()
    val bundle = Bundle()
    bundle.putSerializable(IntentConstant.VIDEO_ITEM.name, videosItem)
    tutorialVideosBottomSheet.arguments = bundle
    tutorialVideosBottomSheet.isCancelable = false
    tutorialVideosBottomSheet.show(parentFragmentManager, TutorialVideosBottomSheet::class.java.name)
  }

  private fun setView(videosItem: VIDEOSItem?) {
    binding?.ctvVideoTitle?.text = videosItem?.videoTitle
    binding?.ctvVideoDuration?.text = videosItem?.videoLength
//    Glide.with(baseActivity).load(videosItem?.videoUrl).apply(RequestOptions()).thumbnail(0.1f).into(binding?.videoThumbnails!!)
    Glide.with(baseActivity).load(videosItem?.videoThumbnails).into(binding?.videoThumbnails!!)
  }
}
