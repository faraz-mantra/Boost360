package com.boost.cart.ui.freeaddons

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FreeAddonsViewModelFactory(private val application: Application) :
  ViewModelProvider.Factory {
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(FreeAddonsViewModel::class.java)) {
      return FreeAddonsViewModel(application) as T
    }
    throw IllegalArgumentException("Unknown View Model Class")
  }

}