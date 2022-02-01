package com.boost.marketplace.ui.videos

import android.content.DialogInterface
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.PartnerZone
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.PromoBanners
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.upgradeDB.model.YoutubeVideoModel
import com.boost.marketplace.R
import com.boost.marketplace.adapter.VideosListAdapter
import com.boost.marketplace.databinding.BottomSheetVideosBinding
import com.boost.marketplace.interfaces.HomeListener
import com.boost.marketplace.ui.home.MarketPlaceHomeViewModel
import com.framework.base.BaseBottomSheetDialog
import com.framework.exoFullScreen.MediaPlayer
import com.google.android.exoplayer2.MediaItem
import kotlinx.android.synthetic.main.bottom_sheet_videos.*


class VideosPopUpBottomSheet :
    BaseBottomSheetDialog<BottomSheetVideosBinding, MarketPlaceHomeViewModel>(), HomeListener {

    lateinit var link: String
    private var videoItem: String? = null
    lateinit var videosListAdapter: VideosListAdapter


    override fun getLayout(): Int {
        return R.layout.bottom_sheet_videos
    }

    override fun getViewModelClass(): Class<MarketPlaceHomeViewModel> {
        return MarketPlaceHomeViewModel::class.java
    }

    override fun onCreateView() {
        dialog!!.window!!.setBackgroundDrawableResource(com.boost.cart.R.color.fullscreen_color)
        dialog.behavior.isDraggable = true
        viewModel = ViewModelProviders.of(baseActivity).get(MarketPlaceHomeViewModel::class.java)
        getBundle()
        initView()
        initMvvm()
    }

    private fun initView() {
        videosListAdapter = VideosListAdapter(ArrayList(), this)
         viewModel?.GetHelp()
        initializeVideosRecycler()
    }

    private fun initializeVideosRecycler() {
        val gridLayoutManager = LinearLayoutManager(context)
        gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
        videos_pop_up_recycler_view.apply {
            layoutManager = gridLayoutManager
            videos_pop_up_recycler_view.adapter = videosListAdapter
        }

    }

    private fun initMvvm() {
        viewModel?.getYoutubeVideoDetails()?.observe(this, androidx.lifecycle.Observer {
            Log.e("getYoutubeVideoDetails", it.toString())
            updateVideosViewPager(it)
        })
    }

    fun updateVideosViewPager(list: List<YoutubeVideoModel>) {
        val link: List<String> = list.get(0).youtube_link!!.split('/')
//        videoPlayerWebView.getSettings().setJavaScriptEnabled(true)
////    videoPlayerWebView.getSettings().setPluginState(WebSettings.PluginState.ON)
//        videoPlayerWebView.setWebViewClient(WebViewClient())
//        videoPlayerWebView.loadUrl("http://www.youtube.com/embed/" + link.get(link.size - 1) + "?autoplay=1&vq=small")
        videosListAdapter.addUpdates(list)
        videosListAdapter.notifyDataSetChanged()
//        video_sub_title.text = list.get(0).title
//        video_sub_desc.text = list.get(0).desc
    }

    private fun getBundle() {
        this.videoItem = requireArguments().getString("title") ?: ""
        link = requireArguments().getString("link") ?: ""
        binding?.ctvVideoTitle?.text = videoItem
        binding?.mainTxt?.text=videoItem
    }

    private fun initializePlayer() {
        MediaPlayer.initialize(baseActivity)
        val player=MediaPlayer.exoPlayer
        binding?.playerView?.player = player
        player?.setMediaItem(MediaItem.fromUri(Uri.parse(link)))
        player?.prepare()
        player?.play()
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

    override fun onPackageClicked(item: Bundles?) {
        TODO("Not yet implemented")
    }

    override fun onPromoBannerClicked(item: PromoBanners?) {
        TODO("Not yet implemented")
    }

    override fun onShowHidePromoBannerIndicator(status: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onPartnerZoneClicked(item: PartnerZone?) {
        TODO("Not yet implemented")
    }

    override fun onShowHidePartnerZoneIndicator(status: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onAddFeatureDealItemToCart(item: FeaturesModel?, minMonth: Int) {
        TODO("Not yet implemented")
    }

    override fun onAddonsCategoryClicked(categoryType: String) {
        TODO("Not yet implemented")
    }

    override fun onPlayYouTubeVideo(videoItem: YoutubeVideoModel) {
        Toast.makeText(context,"Loading",Toast.LENGTH_LONG).show()
        link= videoItem.youtube_link.toString()
        MediaPlayer.releasePlayer()
        initializePlayer()
        binding?.ctvVideoTitle?.text = videoItem.title
        binding?.mainTxt?.text= videoItem.desc

    }

    override fun onPackageAddToCart(item: Bundles?, image: ImageView) {
        TODO("Not yet implemented")
    }

}