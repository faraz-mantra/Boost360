package com.boost.upgrades.ui.historydetails

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException


class HistoryDetailsViewModelFactory(private val application: Application) :
  ViewModelProvider.Factory {
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(HistoryDetailsViewModel::class.java)) {
      return HistoryDetailsViewModel(application) as T
    }
    throw IllegalArgumentException("Unknown View Model Class")
  }

}