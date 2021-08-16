package com.inventoryorder.ui.tutorials

import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import com.framework.base.BaseBottomSheetDialog
import com.framework.exoFullScreen.MediaPlayer
import com.framework.exoFullScreen.preparePlayer
import com.framework.exoFullScreen.setSource
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.inventoryorder.R
import com.inventoryorder.base.AppBaseFragment
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.BottomSheetTutorialsOnStaffProfileBinding
import com.inventoryorder.databinding.FragmentAllTutorialsBinding
import com.inventoryorder.databinding.FragmentTutorialDescBinding
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import com.inventoryorder.recyclerView.RecyclerItemClickListener
import com.inventoryorder.ui.tutorials.model.AllTutorialsItem
import com.inventoryorder.ui.tutorials.model.VIDEOSItem
import com.inventoryorder.ui.tutorials.viewmodel.TutorialViewModel

class TutorialVideosBottomSheet :
  BaseBottomSheetDialog<BottomSheetTutorialsOnStaffProfileBinding, TutorialViewModel>() {

  private var playbackPosition: Long = 0
  private var videosItem: VIDEOSItem? = null
  private var viewClose: View? = null

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_tutorials_on_staff_profile
  }

  override fun getViewModelClass(): Class<TutorialViewModel> {
    return TutorialViewModel::class.java
  }

  override fun onCreateView() {
    getBundle()
    setupBackPressListener()
    setOnClickListener(binding?.civBack, binding?.civClose)
    viewModel?.getTutorialsStaffList()?.observeOnce(viewLifecycleOwner, {
      val tutorialPagerAdapter = TutorialPagerAdapter(
        childFragmentManager,
        FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
        it.contents?.allTutorials
      )
      binding?.viewPagerTutorials?.adapter = tutorialPagerAdapter
      if (it.contents?.allTutorials?.size ?: 0 > 1) binding?.tabLayout?.setupWithViewPager(binding?.viewPagerTutorials)
      else binding?.tabLayout?.gone()
    })
  }

  private fun getBundle() {
    this.videosItem = arguments?.getSerializable(IntentConstant.VIDEO_ITEM.name) as? VIDEOSItem
    binding?.ctvVideoTitle?.text = videosItem?.videoTitle
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.civBack, binding?.civClose -> dismiss()
    }
  }

  override fun isDraggable(): Boolean? {
    return false
  }

  private fun setupBackPressListener() {
    this.view?.isFocusableInTouchMode = true
    this.view?.requestFocus()
    this.view?.setOnKeyListener { _, keyCode, _ ->
      if (keyCode == KeyEvent.KEYCODE_BACK) {
        viewClose?.post { viewClose?.performClick() }
        true
      } else false
    }
  }

  override fun onPause() {
    super.onPause()
    MediaPlayer.pausePlayer()
  }

  override fun onStart() {
    super.onStart()
    initializePlayer()
  }

  override fun onStop() {
    super.onStop()
    MediaPlayer.stopPlayer()
  }

  override fun onDismiss(dialog: DialogInterface) {
    super.onDismiss(dialog)
    MediaPlayer.stopPlayer()
  }

  override fun onDetach() {
    super.onDetach()
    MediaPlayer.stopPlayer()
  }

  override fun onDestroy() {
    super.onDestroy()
    MediaPlayer.stopPlayer()
  }

  private fun initializePlayer() {
    MediaPlayer.initialize(baseActivity)
    viewClose = MediaPlayer.exoPlayer?.preparePlayer(binding?.playerView!!, baseActivity)
    MediaPlayer.exoPlayer?.setSource(
      playbackPosition,
      baseActivity,
      videosItem?.videoUrl.toString()
    )
    MediaPlayer.startPlayer()
  }
}

class TutorialPagerAdapter(
  fm: FragmentManager,
  behavior: Int,
  private var allTutorials: ArrayList<AllTutorialsItem>?
) : FragmentStatePagerAdapter(fm, behavior) {
  override fun getCount(): Int {
    return if (allTutorials?.size ?: 0 <= 1) 1
    else 2
  }

  override fun getItem(position: Int): Fragment {
    return when (position) {
      0 -> FragmentTutorialDesc.newInstance()
      1 -> FragmentAllTutorials.newInstance()
      else -> FragmentTutorialDesc.newInstance()
    }

  }

  override fun getPageTitle(position: Int): CharSequence? {
    return when (position) {
      0 -> "Tutorial contents"
      1 -> "All tutorials"
      else -> ""
    }
  }

}

class FragmentAllTutorials : AppBaseFragment<FragmentAllTutorialsBinding, TutorialViewModel>(),
  RecyclerItemClickListener {
  companion object {
    fun newInstance(): FragmentAllTutorials {
      return FragmentAllTutorials()
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_all_tutorials
  }

  override fun getViewModelClass(): Class<TutorialViewModel> {
    return TutorialViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    viewModel?.getTutorialsStaffList()?.observeOnce(viewLifecycleOwner, {
      binding?.rvVideos?.adapter =
        AppBaseRecyclerViewAdapter(baseActivity, it.contents?.allTutorials!!, this)
    })
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.VIDEO_ITEM_CLICK.ordinal -> {
        val bottomSheetTutorialVideos = TutorialVideosBottomSheet()
        val bundle = Bundle()
        bundle.putSerializable(IntentConstant.VIDEO_ITEM.name, item as? AllTutorialsItem)
        bottomSheetTutorialVideos.arguments = bundle
        bottomSheetTutorialVideos.isCancelable = false
        bottomSheetTutorialVideos.show(
          parentFragmentManager,
          TutorialVideosBottomSheet::class.java.name
        )
      }
    }
  }

}

class FragmentTutorialDesc : AppBaseFragment<FragmentTutorialDescBinding, TutorialViewModel>() {
  companion object {
    fun newInstance(): FragmentTutorialDesc {
      return FragmentTutorialDesc()
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_tutorial_desc
  }

  override fun getViewModelClass(): Class<TutorialViewModel> {
    return TutorialViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    viewModel?.getVideoDetailStaff()?.observeOnce(viewLifecycleOwner, {
      binding?.ctvSteps?.text = it.contentVideo?.tutorialContents?.joinToString("\n")
    })
  }

}