package com.boost.upgrades.ui.removeaddons

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.boost.upgrades.ui.myaddons.MyAddonsViewModel


class RemoveAddonsViewModelFactory(private val application: Application) :
  ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(RemoveAddonsViewModel::class.java)) {
      return RemoveAddonsViewModel(application) as T
    }
    throw IllegalArgumentException("Unknown View Model Class")
  }

}
