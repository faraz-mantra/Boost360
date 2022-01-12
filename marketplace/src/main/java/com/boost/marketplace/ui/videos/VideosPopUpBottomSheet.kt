package com.boost.marketplace.ui.videos

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.View
import android.webkit.*
import android.widget.ImageView
import android.widget.Toast
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.PartnerZone
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.PromoBanners
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.upgradeDB.model.YoutubeVideoModel
import com.boost.marketplace.R
import com.boost.marketplace.databinding.BottomSheetVideosBinding
import com.boost.marketplace.interfaces.HomeListener
import com.boost.marketplace.ui.home.MarketPlaceHomeViewModel
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.pref.UserSessionManager
import com.framework.pref.getAccessTokenAuth
import kotlinx.android.synthetic.main.bottom_sheet_videos.*


class VideosPopUpBottomSheet : BaseBottomSheetDialog<BottomSheetVideosBinding, MarketPlaceHomeViewModel>(),HomeListener//,YouTubePlayer.OnInitializedListener
{

//  lateinit var videosListAdapter: VideosListAdapter
  var clientid: String = "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21"
//
//  var videoURL =
//    "https://media.geeksforgeeks.org/wp-content/uploads/20201217163353/Screenrecorder-2020-12-17-16-32-03-350.mp4"
//
//  var api_key = "AIzaSyBnZoW90SuwbPAarwxUfbeDyGyWr67RKfQ"

  lateinit var link: String
  private var playbackPosition: Long = 0
  private var videoItem: String?=null
  private var viewClose: View? = null


  override fun getLayout(): Int {
    return R.layout.bottom_sheet_videos
  }

  override fun getViewModelClass(): Class<MarketPlaceHomeViewModel> {
    return MarketPlaceHomeViewModel::class.java
  }

  @SuppressLint("SetJavaScriptEnabled")
  override fun onCreateView() {
    dialog.behavior.isDraggable = false
    getBundle()


    webview?.settings?.javaScriptEnabled = true
    webview?.settings?.loadWithOverviewMode = true
    webview?.settings?.useWideViewPort = true
    webview?.settings?.allowFileAccess = true
    webview?.scrollBarStyle = View.SCROLLBARS_OUTSIDE_OVERLAY
    webview?.webChromeClient = WebChromeClient()
    val webSettings = webview?.settings
    webSettings?.javaScriptCanOpenWindowsAutomatically = true
    webSettings?.setSupportMultipleWindows(true)
    webSettings?.cacheMode = WebSettings.LOAD_DEFAULT
    webSettings?.domStorageEnabled = true


    if (link != null) {
      webview.settings.javaScriptEnabled = true
      webview.webViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
         binding?.progressBar?.visible()

          return false
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
          super.onPageStarted(view, url, favicon)
          binding?.progressBar?.visible()

        }

        override fun onPageFinished(view: WebView?, url: String?) {
          super.onPageFinished(view, url)
          binding?.progressBar?.gone()
        }

        override fun onReceivedError(
          view: WebView?,
          request: WebResourceRequest?,
          error: WebResourceError?
        ) {
          binding?.progressBar?.gone()
          super.onReceivedError(view, request, error)
        }
      }
    //  webview.loadUrl(link!!)
      val links: List<String> = link!!.split('/')
      webview.loadUrl("http://www.youtube.com/embed/" + links.get(links.size - 1) + "?autoplay=1&vq=small")
    } else {
      Toast.makeText(requireContext(), "Link is Empty!!", Toast.LENGTH_LONG).show()
//      (activity as UpgradeActivity).popFragmentFromBackStack()
    }

//    web_addons_back.setOnClickListener {
////      (activity as UpgradeActivity).popFragmentFromBackStack()
//    }

    //  ytPlayer.initialize("AIzaSyBnZoW90SuwbPAarwxUfbeDyGyWr67RKfQ", this)

 //   viewModel?.setApplicationLifecycle(Application(), this)
 //   initMVVM()

//    binding?.ytPlayer?.initialize(api_key, OnInitializedListener() {
//      @Override
//      override fun  onInitializationSuccess(
//          com.google.android.youtube.player.YouTubePlayer.Provider() provider,
//          YouTubePlayer youTubePlayer, boolean b)
//        {
//          youTubePlayer.loadVideo("HzeK7g8cD0Y");
//          youTubePlayer.play();
//        }
//
//        @Override
//        override fun onInitializationFailure(YouTubePlayer.Provider provider,
//          YouTubeInitializationResult
//                  youTubeInitializationResult)
//        {
//          Toast.makeText(getApplicationContext(), "Video player Failed", Toast.LENGTH_SHORT).show();
//        }
//      })

//    loadData()
  //  initializeVideosRecycler()

  //  val videouri: Uri = Uri.parse(videoURL)

//    lifecycle.addObserver(binding!!.youtubePlayerView)
//
//    binding?.youtubePlayerView?.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
//      override fun onReady(@NonNull youTubePlayer: YouTubePlayer) {
//        val videoId = "https://www.youtube.com/watch?v=S0Q4gqBUs7c"
//        youTubePlayer.loadVideo(videoId, 0f)
//      }
//    })
  }

  private fun getBundle() {

    this.videoItem = requireArguments().getString("title") ?: ""
    link = requireArguments().getString("link") ?: ""
    binding?.ctvVideoTitle?.text = videoItem


  }
//
//  override fun onPause() {
//    super.onPause()
//    MediaPlayer.pausePlayer()
//  }
//
//  override fun onStart() {
//    super.onStart()
//    initializePlayer()
//  }
//
//  private fun initializePlayer() {
////    val videouri: Uri = Uri.parse(videoURL)
////    MediaPlayer.initialize(context)
////    viewClose = MediaPlayer.exoPlayer?.preparePlayer(binding?.videoView!!, AppCompatActivity())
////    MediaPlayer.exoPlayer?.setSource(
////      playbackPosition,
////      requireContext(),
////      videoURL
////    )
////    MediaPlayer.startPlayer()
//  }
//
//  override fun onStop() {
//    super.onStop()
//    MediaPlayer.stopPlayer()
//  }
//
//  override fun onDismiss(dialog: DialogInterface) {
//    super.onDismiss(dialog)
//    MediaPlayer.stopPlayer()
//  }
//
//  override fun onDetach() {
//    super.onDetach()
//    MediaPlayer.stopPlayer()
//  }
//
//  override fun onDestroy() {
//    super.onDestroy()
//    MediaPlayer.stopPlayer()
//  }

  private fun loadData() {
    viewModel?.loadUpdates(
      getAccessToken(),
      requireArguments().getString("fpid")?:"" ,
      "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21",
      requireArguments().getString("experienceCode")?:"",
      requireArguments().getString("fpTag")?:""
    )

  }

  fun getAccessToken(): String {
    return UserSessionManager(this.requireContext()).getAccessTokenAuth()?.barrierToken() ?: ""
  }

//  private fun initMVVM() {
//    viewModel?.getYoutubeVideoDetails()?.observe(this, androidx.lifecycle.Observer {
//      Log.e("getYoutubeVideoDetails", it.toString())
//      updateVideosViewPager(it)
//    })
//
//    videosListAdapter = VideosListAdapter(ArrayList(),this)
//
//
//  }

//  fun updateVideosViewPager(list: List<YoutubeVideoModel>) {
//    val link: List<String> = list.get(0).youtube_link!!.split('/')
////        videoPlayerWebView.getSettings().setJavaScriptEnabled(true)
//////    videoPlayerWebView.getSettings().setPluginState(WebSettings.PluginState.ON)
////        videoPlayerWebView.setWebViewClient(WebViewClient())
////        videoPlayerWebView.loadUrl("http://www.youtube.com/embed/" + link.get(link.size - 1) + "?autoplay=1&vq=small")
//    videosListAdapter.addUpdates(list)
//    videosListAdapter.notifyDataSetChanged()
////        video_sub_title.text = list.get(0).title
////        video_sub_desc.text = list.get(0).desc
//  }

//  private fun initializeVideosRecycler() {
//    val gridLayoutManager = LinearLayoutManager(this.context)
//    gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
//    videos_pop_up_recycler_view.apply {
//      layoutManager = gridLayoutManager
//      videos_pop_up_recycler_view.adapter = videosListAdapter
//    }
//
//  }


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

//  override fun onInitializationSuccess(
//    p0: YouTubePlayer.Provider?,
//    p1: YouTubePlayer?,
//    p2: Boolean
//  ) {
//    if (!p2) {
//      p1?.cueVideo("Evfe8GEn33w")
//    }
//  }
//
//  override fun onInitializationFailure(
//    p0: YouTubePlayer.Provider?,
//    p1: YouTubeInitializationResult?
//  ) {
//    Toast.makeText(getApplicationContext(), "Video player Failed", Toast.LENGTH_SHORT).show();
//  }


}