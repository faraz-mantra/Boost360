package com.boost.marketplace.interfaces

import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel

interface AddonsListener {
    fun onAddonsClicked(item: FeaturesModel)
}