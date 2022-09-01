package com.boost.marketplace.interfaces

import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.Bundles
import com.boost.dbcenterapi.upgradeDB.model.CartModel

interface PacksV3FooterListener {
    fun onSelectedPack(itemList:Bundles, itemList1: List<CartModel>?)
}