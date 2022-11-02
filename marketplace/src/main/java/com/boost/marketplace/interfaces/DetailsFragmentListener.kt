package com.boost.marketplace.interfaces

import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles

interface DetailsFragmentListener {

    fun imagePreviewPosition(list: ArrayList<String>, pos: Int)
    fun onPackageClicked(item: Bundles?)
    fun itemAddedToCart(status: Boolean)
    fun goToCart()
}