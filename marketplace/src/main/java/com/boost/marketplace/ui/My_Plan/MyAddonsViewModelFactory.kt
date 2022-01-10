package com.boost.marketplace.ui.My_Plan

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MyAddonsViewModelFactory() :
  ViewModelProvider.Factory {
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(MyCurrentPlanViewModel::class.java)) {
      return MyCurrentPlanViewModel() as T
    }
    throw IllegalArgumentException("Unknown View Model Class")
  }

}