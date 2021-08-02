package com.boost.upgrades.interfaces

import android.view.View
import android.widget.ImageView
import com.boost.upgrades.data.api_model.GetAllFeatures.response.Bundles
import com.boost.upgrades.data.api_model.GetAllFeatures.response.PartnerZone
import com.boost.upgrades.data.api_model.GetAllFeatures.response.PromoBanners
import com.boost.upgrades.data.model.FeaturesModel
import com.boost.upgrades.data.model.YoutubeVideoModel

interface CompareListener {
    fun onPackageClicked(item: Bundles?,image: ImageView)
    fun onLearnMoreClicked(item: Bundles?)
}