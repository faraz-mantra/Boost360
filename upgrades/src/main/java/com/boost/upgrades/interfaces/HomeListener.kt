package com.boost.upgrades.interfaces

import android.widget.ImageView
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.PartnerZone
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.PromoBanners
import com.boost.dbcenterapi.upgradeDB.model.*

interface HomeListener {
    fun onPackageClicked(item: Bundles?)
    fun onPromoBannerClicked(item: PromoBanners?)
    fun onShowHidePromoBannerIndicator(status: Boolean)
    fun onPartnerZoneClicked(item: PartnerZone?)
    fun onShowHidePartnerZoneIndicator(status: Boolean)
    fun onAddFeatureDealItemToCart(item: FeaturesModel?, minMonth: Int)
    fun onAddonsCategoryClicked(categoryType: String)
    fun onPlayYouTubeVideo(videoItem: YoutubeVideoModel)
    fun onPackageAddToCart(item: Bundles?,image: ImageView)
}