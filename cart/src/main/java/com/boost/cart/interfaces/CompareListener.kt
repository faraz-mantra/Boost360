package com.boost.cart.interfaces

import android.widget.ImageView
import com.boost.cart.data.api_model.GetAllFeatures.response.Bundles

interface CompareListener {
    fun onPackageClicked(item: Bundles?,image: ImageView)
    fun onLearnMoreClicked(item: Bundles?)
}