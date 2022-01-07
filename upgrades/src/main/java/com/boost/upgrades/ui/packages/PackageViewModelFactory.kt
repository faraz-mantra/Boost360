package com.boost.upgrades.ui.packages

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PackageViewModelFactory(private val application: Application) :
  ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(PackageViewModel::class.java)) {
      return PackageViewModel(application) as T
    }
    throw IllegalArgumentException("Unknown View Model Class")
  }

}
