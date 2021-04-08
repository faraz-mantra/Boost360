package com.inventoryorder.ui.tutorials

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.inventoryorder.R
import com.inventoryorder.base.AppBaseFragment
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.BottomSheetTutorialsOnAppointmentMgmtBinding
import com.inventoryorder.databinding.FragmentAllTutorialsBinding
import com.inventoryorder.databinding.FragmentTutorialDescBinding
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import com.inventoryorder.recyclerView.RecyclerItemClickListener
import com.inventoryorder.ui.tutorials.model.AllTutorialsItem
import com.inventoryorder.ui.tutorials.model.VIDEOSItem
import com.inventoryorder.ui.tutorials.viewmodel.TutorialViewModel

class TutorialVideosBottomSheet : BaseBottomSheetDialog<BottomSheetTutorialsOnAppointmentMgmtBinding, TutorialViewModel>(), Player.EventListener {

  private lateinit var simpleExoplayer: SimpleExoPlayer
  private var playbackPosition: Long = 0
  private var videosItem: VIDEOSItem? = null

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_tutorials_on_appointment_mgmt
  }

  override fun getViewModelClass(): Class<TutorialViewModel> {
    return TutorialViewModel::class.java
  }


  override fun onCreateView() {
    getBundle()
    setOnClickListener(binding?.civBack, binding?.civClose)
    viewModel?.getTutorialsStaffList()?.observeOnce(viewLifecycleOwner, {
      val tutorialPagerAdapter = TutorialPagerAdapter(childFragmentManager, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, it.contents?.allTutorials)
      binding?.viewPagerTutorials?.adapter = tutorialPagerAdapter
      if (it.contents?.allTutorials?.size ?: 0 > 1)
        binding?.tabLayout?.setupWithViewPager(binding?.viewPagerTutorials)
      else binding?.tabLayout?.gone()
    })
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.civBack -> {
        dismiss()
        val learnAboutAppointmentMgmtBottomSheet = LearnHowItWorkBottomSheet()
        learnAboutAppointmentMgmtBottomSheet.show(childFragmentManager, LearnHowItWorkBottomSheet::class.java.name)
      }
      binding?.civClose -> {
        dismiss()
      }
    }
  }

  private fun getBundle() {
    this.videosItem = arguments?.getSerializable(IntentConstant.VIDEO_ITEM.name) as? VIDEOSItem
    binding?.ctvVideoTitle?.text = videosItem?.videoTitle
  }

  override fun onStart() {
    super.onStart()
    initializePlayer()
  }

  override fun onStop() {
    releasePlayer()
    super.onStop()

  }

  override fun onDismiss(dialog: DialogInterface) {
    releasePlayer()
    super.onDismiss(dialog)
  }

  override fun onDetach() {
    releasePlayer()
    super.onDetach()
  }

  override fun onDestroy() {
    releasePlayer()
    super.onDestroy()
  }

  private fun initializePlayer() {
    simpleExoplayer = SimpleExoPlayer.Builder(requireContext()).build()
    preparePlayer(videosItem?.videoUrl.toString())
    binding?.videoView?.player = simpleExoplayer
    simpleExoplayer.seekTo(playbackPosition)
    simpleExoplayer.playWhenReady = true
    simpleExoplayer.addListener(this)
  }

  private val dataSourceFactory: DataSource.Factory by lazy {
    DefaultDataSourceFactory(requireContext(), getString(R.string.app_name))
  }

  private fun buildMediaSource(uri: Uri): MediaSource {
    return ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
  }


  private fun preparePlayer(videoUrl: String) {
    val uri = Uri.parse(videoUrl)
    val mediaSource = buildMediaSource(uri)
    simpleExoplayer.prepare(mediaSource)
  }

  private fun releasePlayer() {
    if (this::simpleExoplayer.isInitialized) {
      playbackPosition = simpleExoplayer.currentPosition
      binding?.videoView?.player?.stop()
      binding?.videoView?.player?.release()
      simpleExoplayer.playWhenReady = false
      simpleExoplayer.stop()
      simpleExoplayer.clearVideoSurface()
      simpleExoplayer.seekTo(0)
      simpleExoplayer.release()
    }
  }

  override fun onPlayerError(error: ExoPlaybackException) {
    // handle error
  }

}

class TutorialPagerAdapter(fm: FragmentManager, behavior: Int, private var allTutorials: ArrayList<AllTutorialsItem>?) : FragmentStatePagerAdapter(fm, behavior) {
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

class FragmentAllTutorials : AppBaseFragment<FragmentAllTutorialsBinding, TutorialViewModel>(), RecyclerItemClickListener {
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
      binding?.rvVideos?.adapter = AppBaseRecyclerViewAdapter(baseActivity, it.contents?.allTutorials!!, this)
    })
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.VIDEO_ITEM_CLICK.ordinal -> {
        val bottomSheetTutorialVideos = TutorialVideosBottomSheet()
        val bundle = Bundle()
        bundle.putSerializable(IntentConstant.VIDEO_ITEM.name, item as? AllTutorialsItem)
        bottomSheetTutorialVideos.arguments = bundle
        bottomSheetTutorialVideos.show(parentFragmentManager, TutorialVideosBottomSheet::class.java.name)
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
    viewModel?.getVideoDetailStaff()?.observeOnce(viewLifecycleOwner, Observer {
      binding?.ctvSteps?.text = it.contentVideo?.tutorialContents?.joinToString("\n")
    })
  }

}