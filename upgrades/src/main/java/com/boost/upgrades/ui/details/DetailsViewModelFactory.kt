package com.boost.upgrades.ui.details

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.boost.upgrades.ui.features.ViewAllFeaturesViewModel
import java.lang.IllegalArgumentException

class DetailsViewModelFactory(private val application: Application) :
  ViewModelProvider.Factory {
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
      return DetailsViewModel(application) as T
    }
    throw IllegalArgumentException("Unknown View Model Class")
  }

}