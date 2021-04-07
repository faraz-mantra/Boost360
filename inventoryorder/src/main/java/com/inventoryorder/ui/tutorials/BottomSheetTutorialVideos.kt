package com.inventoryorder.ui.tutorials

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.framework.base.BaseBottomSheetDialog
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

class BottomSheetTutorialVideos : BaseBottomSheetDialog<BottomSheetTutorialsOnAppointmentMgmtBinding, TutorialViewModel>(), Player.EventListener {
    override fun getLayout(): Int {
        return R.layout.bottom_sheet_tutorials_on_appointment_mgmt
    }

    override fun getViewModelClass(): Class<TutorialViewModel> {
        return TutorialViewModel::class.java
    }

    private var videosItem: VIDEOSItem? = null
    override fun onCreateView() {
        getBundle()
        setOnClickListener(binding?.civBack, binding?.civClose)
        val tutorialPagerAdapter = TutorialPagerAdapter(childFragmentManager, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        viewModel?.getTutorialsList()?.observeOnce(viewLifecycleOwner, {
            binding?.viewPagerTutorials?.adapter = tutorialPagerAdapter
            binding?.tabLayout?.setupWithViewPager(binding?.viewPagerTutorials)
        })
        initializePlayer()

    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.civBack -> {
                dismiss()
                val learnAboutAppointmentMgmtBottomSheet = LearnAboutAppointmentMgmtBottomSheet()
                learnAboutAppointmentMgmtBottomSheet.show(childFragmentManager, LearnAboutAppointmentMgmtBottomSheet::class.java.name)
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

    private fun getVideoUrl(): String? {
        return videosItem?.videoUrl
    }

    private lateinit var simpleExoplayer: SimpleExoPlayer
    private var playbackPosition: Long = 0


    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onStop() {
        releasePlayer()
        super.onStop()

    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    override fun onDetach() {
        releasePlayer()
        super.onDetach()
    }

    private fun initializePlayer() {
        simpleExoplayer = SimpleExoPlayer.Builder(requireParentFragment().requireContext()).build()
        preparePlayer(videosItem?.videoUrl.toString())
        binding?.videoView?.player = simpleExoplayer
        simpleExoplayer.seekTo(playbackPosition)
        simpleExoplayer.playWhenReady = true
        simpleExoplayer.addListener(this)
    }

    private val dataSourceFactory: DataSource.Factory by lazy {
        DefaultDataSourceFactory(requireActivity(), getString(R.string.app_name))
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        return ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri)
    }


    private fun preparePlayer(videoUrl: String) {
        val uri = Uri.parse(videoUrl)
        val mediaSource = buildMediaSource(uri)
        simpleExoplayer.prepare(mediaSource)
    }

    private fun releasePlayer() {
        playbackPosition = simpleExoplayer.currentPosition
        simpleExoplayer.stop()
        simpleExoplayer.clearVideoSurface();
        binding?.videoView?.player?.stop()
        binding?.videoView?.player?.release()
        simpleExoplayer.release()
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        // handle error
    }


}

class TutorialPagerAdapter(fm: FragmentManager, behavior: Int) : FragmentStatePagerAdapter(fm, behavior) {
    override fun getCount(): Int {
        return 2
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
        viewModel?.getTutorialsList()?.observeOnce(viewLifecycleOwner, {
            binding?.rvVideos?.adapter = AppBaseRecyclerViewAdapter(baseActivity, it.contents?.allTutorials!!, this)
        })
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        when (actionType) {
            RecyclerViewActionType.VIDEO_ITEM_CLICK.ordinal -> {
                val bottomSheetTutorialVideos = BottomSheetTutorialVideos()
                val bundle = Bundle()
                bundle.putSerializable(IntentConstant.VIDEO_ITEM.name, item as? AllTutorialsItem)
                bottomSheetTutorialVideos.arguments = bundle
                bottomSheetTutorialVideos.show(parentFragmentManager, BottomSheetTutorialVideos::class.java.name)
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
        binding?.ctvSteps?.text = "Step 1. Click on ‘Add A New Order’.\n" +
                "Step 2. Fill in the customer details.\n" +
                "Step 3. Select products from your product list.\n" +
                "Step 4. Review your order. Verify the payment options.\n" +
                "Step 5. Done."
    }

}