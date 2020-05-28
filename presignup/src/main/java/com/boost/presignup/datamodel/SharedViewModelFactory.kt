package com.boost.presignup.datamodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

//class SharedViewModelFactory<T>(val creator: () -> T) : ViewModelProvider.Factory {
//    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        return creator() as T
//    }
//}

class SharedViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
            return SharedViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}