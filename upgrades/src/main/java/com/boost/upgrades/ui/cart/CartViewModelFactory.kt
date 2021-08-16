package com.boost.upgrades.ui.cart

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CartViewModelFactory(private val application: Application) :
  ViewModelProvider.Factory {
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
      return CartViewModel(application) as T
    }
    throw IllegalArgumentException("Unknown View Model Class")
  }

}
