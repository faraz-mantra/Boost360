package com.boost.upgrades.interfaces

import android.view.View
import com.boost.upgrades.data.api_model.GetAllFeatures.response.Bundles
import com.boost.upgrades.data.model.FeaturesModel
import com.boost.upgrades.data.model.YoutubeVideoModel

interface HomeListener {
    fun onPackageClicked(item: Bundles?)
    fun onAddFeatureDealItemToCart(item: FeaturesModel?, minMonth: Int)
    fun onPlayYouTubeVideo(videoItem: YoutubeVideoModel)
}