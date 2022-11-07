package com.boost.marketplace.interfaces

import com.boost.dbcenterapi.upgradeDB.model.FeaturesModel

interface MyAddonsListener {
  fun onFreeAddonsClicked(item: FeaturesModel)
  fun onPaidAddonsClicked(item: FeaturesModel)
}