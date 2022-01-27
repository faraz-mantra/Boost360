package com.boost.marketplace.interfaces

import android.view.View

interface MyAddonsListener {
  fun onFreeAddonsClicked(v: View?)
  fun onPaidAddonsClicked(v: View?)
}