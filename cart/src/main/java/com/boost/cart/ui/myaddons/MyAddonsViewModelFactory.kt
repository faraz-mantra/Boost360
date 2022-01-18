package com.boost.cart.ui.myaddons

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MyAddonsViewModelFactory(private val application: Application) :
  ViewModelProvider.Factory {
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(MyAddonsViewModel::class.java)) {
      return MyAddonsViewModel(application) as T
    }
    throw IllegalArgumentException("Unknown View Model Class")
  }

}