package com.boost.cart.ui.features

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class ViewAllFeaturesViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(ViewAllFeaturesViewModel::class.java)) {
      return ViewAllFeaturesViewModel(application) as T
    }
    throw IllegalArgumentException("Unknown View Model Class")
  }

}
