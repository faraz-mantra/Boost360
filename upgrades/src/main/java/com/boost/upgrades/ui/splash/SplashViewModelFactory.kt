package com.boost.upgrades.ui.splash

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SplashViewModelFactory(private val application: Application) :
  ViewModelProvider.Factory {
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
      return SplashViewModel(application) as T
    }
    throw IllegalArgumentException("Unknown View Model Class")
  }

}
