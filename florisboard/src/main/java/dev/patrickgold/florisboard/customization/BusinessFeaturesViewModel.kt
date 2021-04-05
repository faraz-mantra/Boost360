package dev.patrickgold.florisboard.customization

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.patrickgold.florisboard.customization.model.response.CustomerDetails
import dev.patrickgold.florisboard.customization.model.response.Photo
import dev.patrickgold.florisboard.customization.model.response.Product
import dev.patrickgold.florisboard.customization.model.response.Updates
import dev.patrickgold.florisboard.customization.network.BusinessFeatureRepository
import dev.patrickgold.florisboard.customization.network.GetGalleryImagesAsyncTask
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

  fun getUpdates(fpId: String, clientId: String, skipBy: Int, limit: Int) {
    job?.cancel()
    job = CoroutineScope(Dispatchers.IO).launch {
      val updates = BusinessFeatureRepository.getAllUpdates(fpId, clientId, skipBy, limit)
      withContext(Dispatchers.Main) {
        if (updates.isSuccessful) _updates.value = updates.body()
         else _error.value = "Update getting error!"
      }
    }
  }

  private val _products = MutableLiveData<List<Product>>()
  val products: LiveData<List<Product>>
    get() = _products

  fun getProducts(fpTag: String, clientId: String, skipBy: Int, identifierType: String) {
    job?.cancel()
    job = CoroutineScope(Dispatchers.IO).launch {
      val products = BusinessFeatureRepository.getAllProducts(fpTag, clientId, skipBy, identifierType)
      withContext(Dispatchers.Main) {
        if (products.isSuccessful)  _products.value = products.body()
        else _error.value = "Inventory getting error!"
      }
    }
  }

  private val _details = MutableLiveData<CustomerDetails>()
  val details: LiveData<CustomerDetails>
    get() = _details

  fun getDetails(fpTag: String, clientId: String) {
    job?.cancel()
    job = CoroutineScope(Dispatchers.IO).launch {
      val details = BusinessFeatureRepository.getAllDetails(fpTag, clientId)
      withContext(Dispatchers.Main) {
        if (details.isSuccessful)  _details.value = details.body()
        else _error.value = "Detail getting error!"
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