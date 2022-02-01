package com.boost.marketplace.ui.videos

import HelpVideosViewPagerAdapter
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.PartnerZone
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.PromoBanners
import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel
import com.boost.dbcenterapi.upgradeDB.model.YoutubeVideoModel
import com.boost.marketplace.Adapters.HelpSectionAdapter
import com.boost.marketplace.R
import com.boost.marketplace.databinding.BottomSheetHelpVideosBinding
import com.boost.marketplace.interfaces.HomeListener
import com.boost.marketplace.ui.home.MarketPlaceHomeViewModel
import com.framework.base.BaseBottomSheetDialog
import kotlinx.android.synthetic.main.bottom_sheet_help_videos.*

class HelpVideosBottomSheet : BaseBottomSheetDialog<BottomSheetHelpVideosBinding, MarketPlaceHomeViewModel>(),
  HomeListener
{

  lateinit var helpSectionAdapter: HelpSectionAdapter
  var clientid: String = "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21"

  var list = ArrayList<String>()
  var currentPos = 0
  var initialLoad = true
  lateinit var link: String
  private var playbackPosition: Long = 0
  private var videoItem: String?=null
  private var viewClose: View? = null
  private var viewPager2: ViewPager2? = null


  override fun getLayout(): Int {
    return R.layout.bottom_sheet_help_videos
  }

  override fun getViewModelClass(): Class<MarketPlaceHomeViewModel> {
    return MarketPlaceHomeViewModel::class.java
  }


  override fun onCreateView() {
    dialog.behavior.isDraggable = true
    getBundle()
//    currentPos = requireArguments().getInt("position")
//    list = requireArguments().getStringArrayList("list") as ArrayList<String>
//    imagePreviewAdapter = ImagePreviewAdapter(list)
 //   helpSectionAdapter = HelpSectionAdapter(ArrayList())
  //  initializetipsrecycler()
   // initializeViewPager()

   // viewPager2 = findViewById(R.id.viewpager)

    val helpVideosViewPagerAdapter = HelpVideosViewPagerAdapter(requireContext())
    preview_video.setAdapter(helpVideosViewPagerAdapter)
    packageIndicator2?.setViewPager2(preview_video)

  }

//  private fun initializeViewPager() {
//    preview_pager.adapter = helpSectionAdapter
//    preview_pager.offscreenPageLimit = list.size
//    preview_pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//      override fun onPageSelected(position: Int) {
//        super.onPageSelected(position)
//        if (!initialLoad) {
//          currentPosition(position)
//        }
//      }
//    })
//  }
//
//  override fun onResume() {
//    super.onResume()
//    preview_pager.postDelayed(Runnable {
//      initialLoad = false
//      preview_pager.setCurrentItem(currentPos)
//      if (currentPos == 0) {
//        currentPosition(0)
//      }
//    }, 100)
//  }

//  fun currentPosition(pos: Int) {
//    currentPos = pos
//    preview_current_position.setText((pos + 1).toString() + "/" + list.size)
//  }

  private fun initializetipsrecycler() {
    val gridLayoutManager = LinearLayoutManager(requireContext())
    gridLayoutManager.orientation = LinearLayoutManager.VERTICAL
    tips_recycler.apply {
      layoutManager = gridLayoutManager
      tips_recycler.adapter = helpSectionAdapter

    }

  }

  private fun getBundle() {
    this.videoItem = requireArguments().getString("title") ?: ""
    link = requireArguments().getString("link") ?: ""
//    binding?.ctvVideoTitle?.text = videoItem
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