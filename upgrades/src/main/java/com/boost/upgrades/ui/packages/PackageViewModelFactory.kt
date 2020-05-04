package com.boost.upgrades.ui.packages

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.biz2.nowfloats.boost.updates.persistance.local.AppDatabase
import com.boost.upgrades.data.model.FeaturesModel
import com.boost.upgrades.ui.home.HomeViewModel
import io.reactivex.disposables.CompositeDisposable
import java.lang.IllegalArgumentException

class PackageViewModelFactory(private val application: Application) :
        ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(PackageViewModel::class.java)){
            return PackageViewModel (application) as T
        }
        throw IllegalArgumentException("Unknown View Model Class")
    }

}
