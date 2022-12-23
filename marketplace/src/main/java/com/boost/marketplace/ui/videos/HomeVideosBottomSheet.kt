package com.boost.marketplace.ui.videos

import android.content.pm.ActivityInfo
import android.os.Build
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.dbcenterapi.upgradeDB.model.YoutubeVideoModel
import com.boost.marketplace.R
import com.boost.marketplace.adapter.VideosListAdapter
import com.boost.marketplace.databinding.BottomSheetHomeVideosBinding
import com.boost.marketplace.interfaces.VideosListener
import com.boost.marketplace.ui.home.MarketPlaceHomeViewModel
import com.framework.base.BaseBottomSheetDialog
import kotlinx.android.synthetic.main.bottom_sheet_home_videos.*
import kotlinx.android.synthetic.main.bottom_sheet_videos.videos_pop_up_recycler_view
import java.util.regex.Matcher
import java.util.regex.Pattern


class HomeVideosBottomSheet :
    BaseBottomSheetDialog<BottomSheetHomeVideosBinding, MarketPlaceHomeViewModel>(),VideosListener {

    lateinit var link: String
    private var videoItem: String? = null
    lateinit var videosListAdapter: VideosListAdapter
    var videoCount : Int = 0
    var position1 : Int = 0
    var size : Int = 0
    var webView: VideoEnabledWebView? = null
    var webChromeClient: VideoEnabledWebChromeClient? = null
    var youtubeList: List<YoutubeVideoModel> = arrayListOf()

    override fun getLayout(): Int {
        return R.layout.bottom_sheet_home_videos
    }

    override fun getViewModelClass(): Class<MarketPlaceHomeViewModel> {
        return MarketPlaceHomeViewModel::class.java
    }

    override fun onCreateView() {
        dialog.window!!.setBackgroundDrawableResource(com.boost.cart.R.color.fullscreen_color)
        dialog.behavior.isDraggable = true
        viewModel = ViewModelProviders.of(baseActivity).get(MarketPlaceHomeViewModel::class.java)
        getBundle()
        initView()
        initMvvm()

        val loadingView: View = layoutInflater.inflate(R.layout.view_loading_video, null)
        webChromeClient = VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, webView)

        webChromeClient!!.setOnToggledFullscreen(object : VideoEnabledWebChromeClient.ToggledFullscreenCallback{
            override fun toggledFullscreen(fullscreen: Boolean) {
                // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
                // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
                if (fullscreen) {
                    val attrs: WindowManager.LayoutParams = requireActivity().getWindow().getAttributes()
                    attrs.flags = attrs.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
                    attrs.flags = attrs.flags or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    requireActivity().getWindow().setAttributes(attrs)
                    if (Build.VERSION.SDK_INT >= 14) {
                        requireActivity().getWindow().getDecorView()
                            .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE)
                    }
                    header.visibility = View.GONE
                    requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR)
                } else {
                    val attrs: WindowManager.LayoutParams = requireActivity().getWindow().getAttributes()
                    attrs.flags = attrs.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
                    attrs.flags =
                        attrs.flags and WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON.inv()
                    requireActivity().getWindow().setAttributes(attrs)
                    if (Build.VERSION.SDK_INT >= 14) {
                        requireActivity().getWindow().getDecorView()
                            .setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE)
                    }
                    header.visibility = View.VISIBLE
                    requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                }
            }

        })


//        videoPlayerWebView1?.settings?.javaScriptEnabled = true
//        videoPlayerWebView1?.settings?.loadWithOverviewMode = true
//        videoPlayerWebView1?.settings?.useWideViewPort = true
//        videoPlayerWebView1?.settings?.allowFileAccess = true
//        videoPlayerWebView1?.scrollBarStyle = View.SCROLLBARS_OUTSIDE_OVERLAY
//        videoPlayerWebView1?.webChromeClient = WebChromeClient()
//        videoPlayerWebView1?.webViewClient = WebViewClient()
//        val webSettings = videoPlayerWebView1?.settings
//        webSettings?.javaScriptCanOpenWindowsAutomatically = true
//        webSettings?.setSupportMultipleWindows(true)
//        webSettings?.cacheMode = WebSettings.LOAD_DEFAULT
//        webSettings?.domStorageEnabled = true
//
//        link = requireArguments().getString("link").toString()
//        binding?.videoCount?.text= "playing $position1 of $size "
//        videoPlayerWebView1.loadUrl("http://www.youtube.com/embed/" + getVideoId(link))
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
            youtubeList = it
            updateVideosViewPager(it)
            onPlayYouTubeVideo(it[0], 0)
        })
    }

    fun updateVideosViewPager(list: List<YoutubeVideoModel>) {
        videosListAdapter.addUpdates(list)
        videosListAdapter.notifyDataSetChanged()
    }

    private fun getBundle() {
        this.videoItem = requireArguments().getString("title") ?: ""
        position1 = requireArguments().getInt("position")+1
        size = requireArguments().getInt("size")
        link = ((requireArguments().getString("link"))).toString()
        binding?.ctvVideoTitle?.text = videoItem
        binding?.mainTxt?.text = videoItem
    }

    override fun onPlayYouTubeVideo(videoItem: YoutubeVideoModel, position: Int) {
        Toast.makeText(context, "Loading", Toast.LENGTH_SHORT).show()
        videoPlayerWebView1?.settings?.javaScriptEnabled = true
        videoPlayerWebView1?.settings?.loadWithOverviewMode = true
        videoPlayerWebView1?.settings?.useWideViewPort = true
        videoPlayerWebView1?.settings?.allowFileAccess = true
        videoPlayerWebView1?.scrollBarStyle = View.SCROLLBARS_OUTSIDE_OVERLAY
//        videoPlayerWebView1?.webChromeClient = WebChromeClient()
        videoPlayerWebView1?.webChromeClient = webChromeClient
//        videoPlayerWebView1?.webViewClient = WebViewClient()
        videoPlayerWebView1?.webViewClient = InsideWebViewClient()
        val webSettings = videoPlayerWebView1?.settings
        webSettings?.javaScriptCanOpenWindowsAutomatically = true
        webSettings?.setSupportMultipleWindows(true)
        webSettings?.cacheMode = WebSettings.LOAD_DEFAULT
        webSettings?.domStorageEnabled = true

        link = videoItem.youtube_link.toString()
        videoPlayerWebView1.loadUrl("http://www.youtube.com/embed/" + getVideoId(link))
        binding?.mainTxt?.text = videoItem.title
        binding?.ctvVideoTitle?.text = videoItem.title
        videoCount = videosListAdapter.itemCount
        val position1 : Int = position+1
        binding?.videoCount?.text= "playing $position1 of $videoCount "

    }

    fun getVideoId(videoUrl: String): String {
        var videoId = ""
        val regex = "http(?:s)?:\\/\\/(?:m.)?(?:www\\.)?youtu(?:\\.be\\/|be\\.com\\/(?:watch\\?(?:feature=youtu.be\\&)?v=|v\\/|embed\\/|user\\/(?:[\\w#]+\\/)+))([^&#?\\n]+)"
        val pattern: Pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
        val matcher: Matcher = pattern.matcher(videoUrl)
        if (matcher.find()) {
            videoId = matcher.group(1)
        }
        return videoId
    }

    private class InsideWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }
    }

}