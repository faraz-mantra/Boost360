package com.boost.upgrades.interfaces

import android.widget.ImageView
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles

interface CompareListener {
    fun onPackageClicked(item: Bundles?,image: ImageView)
    fun onLearnMoreClicked(item: Bundles?)
}