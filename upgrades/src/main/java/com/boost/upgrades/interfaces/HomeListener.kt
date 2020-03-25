package com.boost.upgrades.interfaces

import android.view.View
import com.boost.upgrades.data.model.UpdatesModel

interface HomeListener {
    fun onPackageClicked(v: View?)
    fun onAddonsClicked(v: View?)
}