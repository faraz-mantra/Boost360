package com.boost.marketplace.ui.videos

import android.app.Application
import android.content.DialogInterface
import android.util.Log
import android.view.View
import android.widget.ImageView
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
import com.framework.exoFullScreen.preparePlayer
import com.framework.exoFullScreen.setSource
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.bottom_sheet_videos.*


class VideosPopUpBottomSheet :
    BaseBottomSheetDialog<BottomSheetVideosBinding, MarketPlaceHomeViewModel>(), HomeListener {

    lateinit var link: String
    private var playbackPosition: Long = 0
    private var videoItem: String? = null
    private var viewClose: View? = null
    lateinit var videosListAdapter: VideosListAdapter
    lateinit var singleVideoDetails: YoutubeVideoModel


    override fun getLayout(): Int {
        return R.layout.bottom_sheet_videos
    }

    override fun getViewModelClass(): Class<MarketPlaceHomeViewModel> {
        return MarketPlaceHomeViewModel::class.java
    }

    companion object {
        fun newInstance() = VideosPopUpBottomSheet()
    }


    override fun onCreateView() {
        dialog.behavior.isDraggable = true
        viewModel?.setApplicationLifecycle(Application(), this)
        viewModel = ViewModelProviders.of(this).get(MarketPlaceHomeViewModel::class.java)
        viewModel?.GetHelp()

        getBundle()
        initView()
        initMvvm()

        singleVideoDetails = Gson().fromJson<YoutubeVideoModel>(
            requireArguments().getString("singleVideoDetails"),
            object : TypeToken<YoutubeVideoModel>() {}.type
        )


//    webview?.settings?.javaScriptEnabled = true
//    webview?.settings?.loadWithOverviewMode = true
//    webview?.settings?.useWideViewPort = true
//    webview?.settings?.allowFileAccess = true
//    webview?.scrollBarStyle = View.SCROLLBARS_OUTSIDE_OVERLAY
//    webview?.webChromeClient = WebChromeClient()
//    val webSettings = webview?.settings
//    webSettings?.javaScriptCanOpenWindowsAutomatically = true
//    webSettings?.setSupportMultipleWindows(true)
//    webSettings?.cacheMode = WebSettings.LOAD_DEFAULT
//    webSettings?.domStorageEnabled = true


//    if (link != null) {
//      webview.settings.javaScriptEnabled = true
//      webview.webViewClient = object : WebViewClient() {
//        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
//         binding?.progressBar?.visible()
//
//          return false
//        }
//
//        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
//          super.onPageStarted(view, url, favicon)
//          binding?.progressBar?.visible()
//
//        }
//
//        override fun onPageFinished(view: WebView?, url: String?) {
//          super.onPageFinished(view, url)
//          binding?.progressBar?.gone()
//        }
//
//        override fun onReceivedError(
//          view: WebView?,
//          request: WebResourceRequest?,
//          error: WebResourceError?
//        ) {
//          binding?.progressBar?.gone()
//          super.onReceivedError(view, request, error)
//        }
//      }
//      webview.loadUrl(link!!)
//   //   val links: List<String> = link!!.split('/')
//   //   webview.loadUrl("http://www.youtube.com/embed/" + links.get(links.size - 1) + "?autoplay=1&vq=small")
//    } else {
//      Toast.makeText(requireContext(), "Link is Empty!!", Toast.LENGTH_LONG).show()
////      (activity as UpgradeActivity).popFragmentFromBackStack()
//    }
    }

    private fun initView() {
        videosListAdapter = VideosListAdapter(ArrayList(), this)
        // viewModel?.GetHelp()
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


    }

    private fun initializePlayer() {

        MediaPlayer.initialize(baseActivity)
        viewClose = MediaPlayer.exoPlayer?.preparePlayer(playerView, baseActivity, false)
        MediaPlayer.exoPlayer?.setSource(playbackPosition, baseActivity, link)
        MediaPlayer.startPlayer()
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
        TODO("Not yet implemented")
    }

    override fun onPackageAddToCart(item: Bundles?, image: ImageView) {
        TODO("Not yet implemented")
    }

}