package dev.patrickgold.florisboard.customization

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.framework.utils.NetworkUtils
import com.google.gson.Gson
import com.onboarding.nowfloats.model.channel.statusResponse.ChannelAccessStatusResponse
import com.onboarding.nowfloats.model.profile.MerchantProfileResponse
import com.onboarding.nowfloats.rest.response.channel.ChannelWhatsappResponse
import dev.patrickgold.florisboard.customization.model.response.*
import dev.patrickgold.florisboard.customization.model.response.shareUser.ShareUserDetailResponse
import dev.patrickgold.florisboard.customization.model.response.staff.GetStaffListingRequest
import dev.patrickgold.florisboard.customization.model.response.staff.StaffResult
import dev.patrickgold.florisboard.customization.network.GetGalleryImagesAsyncTask
import dev.patrickgold.florisboard.customization.network.repository.*
import dev.patrickgold.florisboard.customization.util.Constants
import dev.patrickgold.florisboard.customization.util.PrefConstants
import dev.patrickgold.florisboard.customization.util.SharedPrefUtil
import kotlinx.coroutines.*
import timber.log.Timber


class BusinessFeaturesViewModel(context: Context) {

  private val TAG = "BusinessFeaturesViewMod"
  var job: Job? = null
  val sharedPref = SharedPrefUtil.fromBoostPref().getsBoostPref(context)

  private val _error = MutableLiveData<String>()
  val error: LiveData<String>
    get() = _error

  private val _updates = MutableLiveData<Updates>()
  val updates: LiveData<Updates>
    get() = _updates

  private val _merchantSummary = MutableLiveData<MerchantSummaryResponse>()
  val merchantSummary: LiveData<MerchantSummaryResponse>
    get() = _merchantSummary

  fun getMerchantSummary(clientId: String?, fpTag: String?) {
    val prefMerchant = MerchantSummaryResponse.getMerchantSummaryWebsite()
    prefMerchant?.let {
      _merchantSummary.postValue(it)
    }
    try {

      CoroutineScope(Dispatchers.IO).launch {
        val response = BusinessFeatureRepository.getMerchantSummary(clientId, fpTag)

        if (response.isSuccessful && response.body() != null) {
          _merchantSummary.postValue(response.body())
          response.body()?.saveMerchantSummary()
        } else {
          if (response.code() == Constants.UNAUTHORIZED_STATUS_CODE) {
            _error.value = Constants.TOKEN_EXPIRED_MESSAGE
          } else {
            _error.value = "Summary getting error!"
          }
        }
      }
    } catch (e: Exception) {
      Log.e(TAG, "getMerchantData: " + e.localizedMessage)
      if (prefMerchant == null) {
        _error.postValue(e.localizedMessage)
      }
    }

  }

  fun getUpdates(fpId: String?, clientId: String, skipBy: Int, limit: Int) {
    val prefUpdates = sharedPref.updateList
    if (prefUpdates != null && skipBy == 0) _updates.postValue(prefUpdates)
    job = CoroutineScope(Dispatchers.IO).launch {
      try {
        val updates = BusinessFeatureRepository.getAllUpdates(fpId, clientId, skipBy, limit)
        withContext(Dispatchers.Main) {
          if (updates.isSuccessful) {
            val data: Updates = updates.body() ?: Updates()
            if (skipBy == 0) sharedPref.save(PrefConstants.PREF_UPDATES, Gson().toJson(data))
            _updates.value = data
          } else {
            if (updates.code() == Constants.UNAUTHORIZED_STATUS_CODE) {
              _error.value = Constants.TOKEN_EXPIRED_MESSAGE
            } else _error.value = "Business Update getting error!"
          }
        }
      } catch (e: Exception) {
        Log.e(TAG, "getUpdates: " + e.localizedMessage)
        if (prefUpdates == null) _error.postValue(e.localizedMessage)
      }
    }
  }

  private val _products = MutableLiveData<ProductResponse>()
  val products: LiveData<ProductResponse>
    get() = _products

  fun getProducts(fpTag: String?, clientId: String, skipBy: Int, identifierType: String) {
    val prefProducts = sharedPref.productList
    if (prefProducts != null && skipBy == 0) _products.postValue(prefProducts)
    job = CoroutineScope(Dispatchers.IO).launch {
      try {
        val products = BusinessFeatureRepository.getAllProducts(fpTag, clientId, skipBy, identifierType)
        withContext(Dispatchers.Main) {
          Log.i(TAG, "getProducts: " + products.code())
          if (products.isSuccessful) {
            val data: ProductResponse = products.body() ?: ProductResponse()
            if (skipBy == 0) sharedPref.save(PrefConstants.PREF_PRODUCTS, Gson().toJson(data))
            _products.postValue(data)
          } else {
            if (products.code() == Constants.UNAUTHORIZED_STATUS_CODE) {
              _error.value = Constants.TOKEN_EXPIRED_MESSAGE
            } else _error.value = "Inventory getting error!"
          }
        }
      } catch (e: Exception) {
        if (prefProducts == null) _error.postValue(e.localizedMessage)
      }
    }
  }

  private val _details = MutableLiveData<CustomerDetails>()
  val details: LiveData<CustomerDetails>
    get() = _details

  fun getDetails(fpTag: String?, clientId: String) {
    Log.i(TAG, "getDetails: called")

    CoroutineScope(Dispatchers.IO).launch {
      try {
        val details = BusinessFeatureRepository.getAllDetails(fpTag, clientId)
        Log.i(TAG, "getDetails: " + details.code())
        withContext(Dispatchers.Main) {
          Log.i(TAG, "getDetails: response")

          if (details.isSuccessful) {
            _details.value = details.body()
            Log.i(TAG, "getDetails: success")
          } else {
            if (details.code() == Constants.UNAUTHORIZED_STATUS_CODE) {
              _error.value = Constants.TOKEN_EXPIRED_MESSAGE
            } else {
              _error.value = "Detail getting error!"
            }
          }
        }
      } catch (e: Exception) {
        _error.postValue(e.localizedMessage)
      }

    }
  }

  private val _channelStatus = MutableLiveData<ChannelAccessStatusResponse>()
  val channelStatusData: LiveData<ChannelAccessStatusResponse>
    get() = _channelStatus

  fun getChannelsAccessTokenStatus(fpId: String?) {
    Log.i(TAG, "getChannelAccessTokenStatus: called")
    if (NetworkUtils.isNetworkConnected()) {
      job = CoroutineScope(Dispatchers.IO).launch {
        try {
          val channelStatus = NfxFloatRepository.getChannelsStatus(fpId)
          withContext(Dispatchers.Main) {
            Log.i(TAG, "getChannelsAccessTokenStatus: response ")
            if (channelStatus.isSuccessful) _channelStatus.value = channelStatus.body()
            else {
              if (channelStatus.code() == Constants.UNAUTHORIZED_STATUS_CODE) {
                _error.value = Constants.TOKEN_EXPIRED_MESSAGE
              } else {
                _error.value = "Inventory getting error!"
              }
              _channelStatus.value = ChannelAccessStatusResponse(success = false)
            }
          }
        } catch (e: Exception) {
          _error.postValue(e.localizedMessage)
        }
      }
    }


  }

  private val _channelWhatsApp = MutableLiveData<ChannelWhatsappResponse>()
  val channelWhatsAppData: LiveData<ChannelWhatsappResponse>
    get() = _channelWhatsApp

  fun getWhatsAppBusiness(auth: String, request: String?) {
    job = CoroutineScope(Dispatchers.IO).launch {
      try {
        val channelWhatsApp = WebActionBoostRepository.getWhatsAppBusiness(auth, request)
        withContext(Dispatchers.Main) {
          if (channelWhatsApp.isSuccessful) _channelWhatsApp.value = channelWhatsApp.body()
          else _channelWhatsApp.value = ChannelWhatsappResponse()
        }
      } catch (e: Exception) {
        _error.postValue(e.localizedMessage)
      }

    }
  }

  private val _shareUserDetail = MutableLiveData<ShareUserDetailResponse>()
  val shareUserDetailData: LiveData<ShareUserDetailResponse>
    get() = _shareUserDetail

  fun getBoostVisitingMessage(context: Context) {
    job = CoroutineScope(Dispatchers.IO).launch {
      try {
        val shareUserDetail = WebActionBoostRepository.getBoostVisitingMessage(context) as? ShareUserDetailResponse
        withContext(Dispatchers.Main) {
          if (shareUserDetail != null && shareUserDetail.status == 200) _shareUserDetail.value = shareUserDetail!!
          else _shareUserDetail.value = ShareUserDetailResponse()
        }
      } catch (e: Exception) {
        _error.postValue(e.localizedMessage)
      }
    }
  }

  private val _merchantProfile = MutableLiveData<MerchantProfileResponse>()
  val merchantProfileData: LiveData<MerchantProfileResponse>
    get() = _merchantProfile

  fun getMerchantProfile(floatingId: String?) {
    val prefBCards = sharedPref.businessCardList
    if (prefBCards != null) {
      _merchantProfile.postValue(prefBCards)
    }
    job = CoroutineScope(Dispatchers.IO).launch {
      try {
        val merchantProfile = BoostFloatRepository.getMerchantProfile(floatingId)
        withContext(Dispatchers.Main) {
          if (merchantProfile.isSuccessful) {
            _merchantProfile.value = merchantProfile.body()
            sharedPref.save(PrefConstants.PREF_BUSINESS_CARD, Gson().toJson(merchantProfile.body()))
          } else {
            if (merchantProfile.code() == Constants.UNAUTHORIZED_STATUS_CODE) {
              _error.value = Constants.TOKEN_EXPIRED_MESSAGE
            } else _error.value = "Inventory getting error!"
            _merchantProfile.value = MerchantProfileResponse()
          }
        }
      } catch (e: Exception) {
        if (prefBCards == null) _error.postValue(e.localizedMessage)
      }
    }
  }

  private val _photo = MutableLiveData<List<Photo>>()
  val photos: LiveData<List<Photo>>
    get() = _photo

  fun getPhotos(fpId: String) {
    val prefPhotos = sharedPref.photoList
    if (prefPhotos != null) _photo.postValue(prefPhotos)
    job = CoroutineScope(Dispatchers.IO).launch {
      try {
        BusinessFeatureRepository.getAllImageList(object : GetGalleryImagesAsyncTask.GetGalleryImagesInterface {
          override fun imagesReceived(listImage: ArrayList<String>) {
            Timber.i("Images Received: $fpId")
            CoroutineScope(Dispatchers.Main).launch {
              val response = listImage.map { url -> Photo().apply { imageUri = url } }
              _photo.value = response
              sharedPref.save(PrefConstants.PREF_PHOTOS, Gson().toJson(response))
            }
          }

          override fun onFailed(code: Int?) {
            if (code == Constants.UNAUTHORIZED_STATUS_CODE) {
              _error.value = Constants.TOKEN_EXPIRED_MESSAGE
            } else _error.value = "Getting photo error"
          }
        }, fpId)
      } catch (e: Exception) {
        if (prefPhotos == null) _error.postValue(e.localizedMessage)
      }
    }
  }

  private val _staff = MutableLiveData<StaffResult>()
  val staff: LiveData<StaffResult>
    get() = _staff

  fun getStaffList(request: GetStaffListingRequest?) {
    val prefStaff = sharedPref.staffList
    if (prefStaff != null && request?.filterBy?.offset == 0) _staff.postValue(prefStaff)
    job = CoroutineScope(Dispatchers.IO).launch {
      try {
        val staffResponse = NowFloatRepository.fetchStaffList(request)
        withContext(Dispatchers.Main) {
          if (staffResponse.isSuccessful) {
            val result = staffResponse.body()?.result ?: StaffResult()
            if (request?.filterBy?.offset == 0) sharedPref.save(PrefConstants.PREF_STAFF, Gson().toJson(result))
            _staff.value = result
          } else {
            if (staffResponse.code() == Constants.UNAUTHORIZED_STATUS_CODE) {
              _error.value = Constants.TOKEN_EXPIRED_MESSAGE
            } else _error.value = "Staff getting error!"
          }
        }
      } catch (e: Exception) {
        if (prefStaff == null) _error.postValue(e.localizedMessage)
      }
    }
  }
}