package dev.patrickgold.florisboard.customization

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.onboarding.nowfloats.model.channel.statusResponse.ChannelAccessStatusResponse
import com.onboarding.nowfloats.model.profile.MerchantProfileResponse
import com.onboarding.nowfloats.rest.response.channel.ChannelWhatsappResponse
import dev.patrickgold.florisboard.customization.model.response.CustomerDetails
import dev.patrickgold.florisboard.customization.model.response.Photo
import dev.patrickgold.florisboard.customization.model.response.Product
import dev.patrickgold.florisboard.customization.model.response.Updates
import dev.patrickgold.florisboard.customization.model.response.shareUser.ShareUserDetailResponse
import dev.patrickgold.florisboard.customization.network.repository.BusinessFeatureRepository
import dev.patrickgold.florisboard.customization.network.GetGalleryImagesAsyncTask
import dev.patrickgold.florisboard.customization.network.repository.BoostFloatRepository
import dev.patrickgold.florisboard.customization.network.repository.NfxFloatRepository
import dev.patrickgold.florisboard.customization.network.repository.WebActionBoostRepository
import dev.patrickgold.florisboard.customization.util.Constants
import kotlinx.coroutines.*
import timber.log.Timber


class BusinessFeaturesViewModel {

  var job: Job? = null

  private val _error = MutableLiveData<String>()
  val error: LiveData<String>
    get() = _error

  private val _updates = MutableLiveData<Updates>()
  val updates: LiveData<Updates>
    get() = _updates

  fun getUpdates(fpId: String?, clientId: String, skipBy: Int, limit: Int) {
    job?.cancel()
    job = CoroutineScope(Dispatchers.IO).launch {
      val updates = BusinessFeatureRepository.getAllUpdates(fpId, clientId, skipBy, limit)
      withContext(Dispatchers.Main) {
        if (updates.isSuccessful) _updates.value = updates.body()
        else _error.value = "Business Update getting error!"
      }
    }
  }

  private val _products = MutableLiveData<List<Product>>()
  val products: LiveData<List<Product>>
    get() = _products

  fun getProducts(fpTag: String?, clientId: String, skipBy: Int, identifierType: String) {
    job?.cancel()
    job = CoroutineScope(Dispatchers.IO).launch {
      val products = BusinessFeatureRepository.getAllProducts(fpTag, clientId, skipBy, identifierType)
      withContext(Dispatchers.Main) {
        if (products.isSuccessful) _products.value = products.body()
        else _error.value = "Inventory getting error!"
      }
    }
  }

  private val _details = MutableLiveData<CustomerDetails>()
  val details: LiveData<CustomerDetails>
    get() = _details

  fun getDetails(fpTag: String?, clientId: String) {
    job?.cancel()
    job = CoroutineScope(Dispatchers.IO).launch {
      val details = BusinessFeatureRepository.getAllDetails(fpTag, clientId)
      withContext(Dispatchers.Main) {
        if (details.isSuccessful) _details.value = details.body()
        else _error.value = "Detail getting error!"
      }
    }
  }

  private val _channelStatus = MutableLiveData<ChannelAccessStatusResponse>()
  val channelStatusData: LiveData<ChannelAccessStatusResponse>
    get() = _channelStatus

  fun getChannelsAccessTokenStatus(fpId: String?) {
    job?.cancel()
    job = CoroutineScope(Dispatchers.IO).launch {
      val channelStatus = NfxFloatRepository.getChannelsStatus(fpId)
      withContext(Dispatchers.Main) {
        if (channelStatus.isSuccessful) _channelStatus.value = channelStatus.body()
        else _channelStatus.value = ChannelAccessStatusResponse(success = false)
      }
    }
  }

  private val _channelWhatsApp = MutableLiveData<ChannelWhatsappResponse>()
  val channelWhatsAppData: LiveData<ChannelWhatsappResponse>
    get() = _channelWhatsApp

  fun getWhatsAppBusiness(auth: String, request: String?) {
    job?.cancel()
    job = CoroutineScope(Dispatchers.IO).launch {
      val channelWhatsApp = WebActionBoostRepository.getWhatsAppBusiness(auth, request)
      withContext(Dispatchers.Main) {
        if (channelWhatsApp.isSuccessful) _channelWhatsApp.value = channelWhatsApp.body()
        else _channelWhatsApp.value = ChannelWhatsappResponse()
      }
    }
  }

  private val _shareUserDetail = MutableLiveData<ShareUserDetailResponse>()
  val shareUserDetailData: LiveData<ShareUserDetailResponse>
    get() = _shareUserDetail

  fun getBoostVisitingMessage(context: Context) {
    job?.cancel()
    job = CoroutineScope(Dispatchers.IO).launch {
      val shareUserDetail = WebActionBoostRepository.getBoostVisitingMessage(context) as? ShareUserDetailResponse
      withContext(Dispatchers.Main) {
        if (shareUserDetail != null && shareUserDetail.status == 200) _shareUserDetail.value = shareUserDetail!!
        else _shareUserDetail.value = ShareUserDetailResponse()
      }
    }
  }

  private val _merchantProfile = MutableLiveData<MerchantProfileResponse>()
  val merchantProfileData: LiveData<MerchantProfileResponse>
    get() = _merchantProfile

  fun getMerchantProfile(floatingId: String?) {
    job?.cancel()
    job = CoroutineScope(Dispatchers.IO).launch {
      val merchantProfile = BoostFloatRepository.getMerchantProfile(floatingId)
      withContext(Dispatchers.Main) {
        if (merchantProfile.isSuccessful) _merchantProfile.value = merchantProfile.body()
        else _merchantProfile.value = MerchantProfileResponse()
      }
    }
  }

  private val _photo = MutableLiveData<List<Photo>>()
  val photos: LiveData<List<Photo>>
    get() = _photo

  fun getPhotos(fpId: String) {
    job?.cancel()
    job = CoroutineScope(Dispatchers.IO).launch {
      BusinessFeatureRepository.getAllImageList(object : GetGalleryImagesAsyncTask.GetGalleryImagesInterface {
        override fun imagesReceived() {
          Timber.i("Images Received")
          CoroutineScope(Dispatchers.Main).launch {
            _photo.value = Constants.storeSecondaryImages.map { url ->
              Photo().apply {
                imageUri = url
              }
            }
          }
        }
      }, fpId)
    }
  }
}