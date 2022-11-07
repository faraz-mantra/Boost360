package com.boost.marketplace.interfaces

import android.widget.ImageView
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles

interface PacksV3listener {
    fun onPackageClickedV3(item: Bundles?, image: ImageView?)
}