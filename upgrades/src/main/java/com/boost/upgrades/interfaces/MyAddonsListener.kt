package com.boost.upgrades.interfaces

import android.view.View
import com.boost.upgrades.data.model.UpdatesModel

interface MyAddonsListener {
    fun onFreeAddonsClicked(v: View?)
    fun onPaidAddonsClicked(v: View?)
}