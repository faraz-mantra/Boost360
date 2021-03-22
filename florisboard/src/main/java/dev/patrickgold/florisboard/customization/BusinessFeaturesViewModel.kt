package dev.patrickgold.florisboard.customization

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.patrickgold.florisboard.customization.model.response.CustomerDetails
import dev.patrickgold.florisboard.customization.model.response.Product
import dev.patrickgold.florisboard.customization.model.response.Updates
import dev.patrickgold.florisboard.customization.network.BusinessFeatureRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BusinessFeaturesViewModel {

    private val _updates = MutableLiveData<Updates>()
    val updates: LiveData<Updates>
        get() = _updates

    fun getUpdates(fpId: String, clientId: String, skipBy: Int, limit: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val updates = BusinessFeatureRepository.getAllUpdates(fpId, clientId, skipBy, limit)
            withContext(Dispatchers.Main) {
                _updates.value = updates
            }
        }
    }

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>>
        get() = _products

    fun getProducts(fpTag: String, clientId: String, skipBy: Int, identifierType: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val products = BusinessFeatureRepository.getAllProducts(fpTag, clientId, skipBy, identifierType)
            withContext(Dispatchers.Main) {
                _products.value = products
            }
        }
    }

    private val _details = MutableLiveData<CustomerDetails>()
    val details: LiveData<CustomerDetails>
        get() = _details

    fun getDetails(fpTag: String, clientId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val details = BusinessFeatureRepository.getAllDetails(fpTag, clientId)
            withContext(Dispatchers.Main) {
                _details.value = details
            }
        }
    }
}