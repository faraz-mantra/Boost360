package com.boost.upgrades.interfaces

import android.view.View
import com.boost.upgrades.data.api_model.GetAllFeatures.response.Bundles
import com.boost.upgrades.data.api_model.GetAllFeatures.response.PartnerZone
import com.boost.upgrades.data.api_model.GetAllFeatures.response.PromoBanners
import com.framework.upgradeDB.model.*

interface CompareBackListener {
  fun backComparePress()
}