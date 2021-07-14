package com.boost.presignup.datamodel

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.boost.presignup.R
import com.boost.presignup.utils.AppConstants.Companion.ENABLE_BOTTOM_VIEW
import com.boost.presignup.utils.AppConstants.Companion.SINGLE_LANGUAGE_BUTTON_VIEW
import com.luminaire.apolloar.livedata.SingleLiveEvent

open class SharedViewModel(application: Application) : AndroidViewModel(application) {
  var eventHandler = EventHandler(this)

  private val _changeLocaleLag = SingleLiveEvent<Int>()

  val changeLocaleLag: LiveData<Int>
    get() = _changeLocaleLag

  private val _initialLoad = SingleLiveEvent<Boolean>()

  val initialLoadStatus: LiveData<Boolean>
    get() = _initialLoad

  private val _navigation = SingleLiveEvent<String>()

  val navigation: LiveData<String>
    get() = _navigation

  private val _languageSelected = SingleLiveEvent<String>()

  val languageSelected: SingleLiveEvent<String>
    get() = _languageSelected


  private val _inActivityUpdateButtonStyle = SingleLiveEvent<Int>()

  val inActivityUpdateButtonStyle: LiveData<Int>
    get() = _inActivityUpdateButtonStyle

  fun postValueToUpdateButtonStyle(it: Int?) {
    _inActivityUpdateButtonStyle.postValue(it)
  }

  fun setSelectedLanguage(lang: String) {
    _languageSelected.value = lang
  }

  fun initialLoad(status: Boolean) {
    _initialLoad.postValue(status)
  }


  fun enableBottomView() {
    _navigation.postValue(ENABLE_BOTTOM_VIEW)
  }

  fun SingleLanguageButtonView() {
    _navigation.postValue(SINGLE_LANGUAGE_BUTTON_VIEW)
  }

  fun LanguageSelection(lang: String) {
    _languageSelected.value = lang
  }


  class EventHandler(var sharedViewModel: SharedViewModel) {

    fun onCreateAccountClick(view: View) {
      Log.e("onCreateAccountClick", ">>>>>")
    }

    fun onLoginClick(view: View) {
      Log.e("onLoginClick", ">>>>>")
    }

    fun onDropDownSelected(view: View) {

    }

    fun onSetUpEnglishVideoClick(view: View) {
      sharedViewModel._changeLocaleLag.postValue(R.string.english)
    }

    fun onSetUpHindiVideoClick(view: View) {
      sharedViewModel._changeLocaleLag.postValue(R.string.hindi)
    }

    fun onSetUpKannadaVideoClick(view: View) {
      sharedViewModel._changeLocaleLag.postValue(R.string.kannada)
    }

    fun onSetUpTeluguVideoClick(view: View) {
      sharedViewModel._changeLocaleLag.postValue(R.string.telugu)
    }

    fun onSetUpMalayalamVideoClick(view: View) {
      sharedViewModel._changeLocaleLag.postValue(R.string.malayalam)
    }

    fun onSetUpTamilVideoClick(view: View) {
      sharedViewModel._changeLocaleLag.postValue(R.string.tamil)
    }

    fun onSetUpMaratiVideoClick(view: View) {
      sharedViewModel._changeLocaleLag.postValue(R.string.marathi)
    }
  }
}