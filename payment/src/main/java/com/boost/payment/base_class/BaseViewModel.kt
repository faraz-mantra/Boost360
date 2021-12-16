package com.boost.payment.base_class

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.boost.payment.R
import com.framework.analytics.SentryController
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import retrofit2.HttpException
import java.net.ConnectException
import java.util.regex.Matcher
import java.util.regex.Pattern


enum class ApiStatus { LOADING, ERROR, DONE }

open class BaseViewModel(application: Application) : AndroidViewModel(application) {

  // The internal MutableLiveData that stores the status of the most recent request
  protected val _isError = MutableLiveData<Boolean>()

  // The external immutable LiveData for the request status
  val isError: LiveData<Boolean>
    get() = _isError

  // The external immutable LiveData for the request status
  lateinit var errorMessage: String

  // The external immutable LiveData for the request status
  lateinit var successMessage: String

  // The internal MutableLiveData that stores the status of the most recent request
  protected val _apiStatus = MutableLiveData<ApiStatus>()


  // The external immutable LiveData for the request status
  val apiStatus: LiveData<ApiStatus>
    get() = _apiStatus


  /***************************************** Job and  Coroutine ***********************************************/

  // Create a Coroutine scope using a job to be able to cancel when needed
  private var viewModelJob = Job()

  // the Coroutine runs using the Main (UI) dispatcher
  protected val coroutineScope = CoroutineScope(Dispatchers.IO + viewModelJob)


  override fun onCleared() {
    super.onCleared()
    viewModelJob.cancel()
  }

  /**
   * Set default error status
   *
   */
  fun clearError() {
    _isError.postValue(false)
    errorMessage = ""
  }

  /**
   * Set default error status
   *
   */
  fun clearApiStatus() {
    _apiStatus.postValue(null)
    errorMessage = ""
  }

  protected fun handleNetworkException(e: Exception) {
    when (e) {
      is HttpException -> {
//                parseError(e.response()!!.errorBody()?.string())
        parseError(e.response()!!.errorBody()?.toString())
      }
      is ConnectException -> {
        errorMessage = getApplication<Application>().getString(R.string.no_internet)
        _apiStatus.postValue(ApiStatus.ERROR)
      }
      else -> {
        errorMessage = getApplication<Application>().getString(R.string.exception)
        _apiStatus.postValue(ApiStatus.ERROR)
      }
    }
  }

  private fun parseError(errorString: String?) {
    try {
      val message = JsonParser().parse(errorString)
        .asJsonObject["message"]
        .asString
      errorMessage = message
      _apiStatus.postValue(ApiStatus.ERROR)
    } catch (e: Exception) {
      SentryController.captureException(e)
      Log.i("parseError >>>>", e.toString())
    }
  }

  fun isValidPassword(password: String): Boolean {

    val pattern: Pattern
    val matcher: Matcher
    val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$)$"
    pattern = Pattern.compile(PASSWORD_PATTERN)
    matcher = pattern.matcher(password)

    return matcher.matches()

  }
}