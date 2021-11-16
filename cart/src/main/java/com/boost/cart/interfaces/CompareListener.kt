package com.boost.cart.interfaces

import android.view.View
import android.widget.ImageView
import com.boost.cart.data.api_model.GetAllFeatures.response.Bundles
import com.boost.cart.data.api_model.GetAllFeatures.response.PartnerZone
import com.boost.cart.data.api_model.GetAllFeatures.response.PromoBanners
import com.framework.upgradeDB.model.*

interface CompareListener {
    fun onPackageClicked(item: Bundles?,image: ImageView)
    fun onLearnMoreClicked(item: Bundles?)
}