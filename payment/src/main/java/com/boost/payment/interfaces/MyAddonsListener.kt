package com.boost.payment.interfaces

import android.view.View

interface MyAddonsListener {
  fun onFreeAddonsClicked(v: View?)
  fun onPaidAddonsClicked(v: View?)
}