package com.boost.marketplace.ui.videos

import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
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
    BaseBottomSheetDialog<BottomSheetHomeVideosBinding, MarketPlaceHomeViewModel>(), VideosListener {

    lateinit var link: String
    private var videoItem: String? = null
    lateinit var videosListAdapter: VideosListAdapter

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

        videoPlayerWebView1?.settings?.javaScriptEnabled = true
        videoPlayerWebView1?.settings?.loadWithOverviewMode = true
        videoPlayerWebView1?.settings?.useWideViewPort = true
        videoPlayerWebView1?.settings?.allowFileAccess = true
        videoPlayerWebView1?.scrollBarStyle = View.SCROLLBARS_OUTSIDE_OVERLAY
        videoPlayerWebView1?.webChromeClient = WebChromeClient()
        videoPlayerWebView1?.webViewClient = WebViewClient()
        val webSettings = videoPlayerWebView1?.settings
        webSettings?.javaScriptCanOpenWindowsAutomatically = true
        webSettings?.setSupportMultipleWindows(true)
        webSettings?.cacheMode = WebSettings.LOAD_DEFAULT
        webSettings?.domStorageEnabled = true

        link = requireArguments().getString("link").toString()
        videoPlayerWebView1.loadUrl("http://www.youtube.com/embed/" + getVideoId(link))
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
        videosListAdapter.addUpdates(list)
        videosListAdapter.notifyDataSetChanged()
    }

    private fun getBundle() {
        this.videoItem = requireArguments().getString("title") ?: ""
        link = ((requireArguments().getString("link"))).toString()
        binding?.ctvVideoTitle?.text = videoItem
        binding?.mainTxt?.text = videoItem
    }

    override fun onPlayYouTubeVideo(videoItem: YoutubeVideoModel) {
        Toast.makeText(context, "Loading", Toast.LENGTH_LONG).show()
        videoPlayerWebView1?.settings?.javaScriptEnabled = true
        videoPlayerWebView1?.settings?.loadWithOverviewMode = true
        videoPlayerWebView1?.settings?.useWideViewPort = true
        videoPlayerWebView1?.settings?.allowFileAccess = true
        videoPlayerWebView1?.scrollBarStyle = View.SCROLLBARS_OUTSIDE_OVERLAY
        videoPlayerWebView1?.webChromeClient = WebChromeClient()
        videoPlayerWebView1?.webViewClient = WebViewClient()
        val webSettings = videoPlayerWebView1?.settings
        webSettings?.javaScriptCanOpenWindowsAutomatically = true
        webSettings?.setSupportMultipleWindows(true)
        webSettings?.cacheMode = WebSettings.LOAD_DEFAULT
        webSettings?.domStorageEnabled = true

        link = requireArguments().getString("link").toString()
        videoPlayerWebView1.loadUrl("http://www.youtube.com/embed/" + getVideoId(link))
        binding?.ctvVideoTitle?.text = videoItem.title
        binding?.mainTxt?.text = videoItem.desc

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

}