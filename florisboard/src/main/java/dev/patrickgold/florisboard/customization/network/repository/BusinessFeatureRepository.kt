package dev.patrickgold.florisboard.customization.network.repository

import com.framework.base.BaseResponse
import dev.patrickgold.florisboard.customization.model.response.*
import dev.patrickgold.florisboard.customization.network.GetGalleryImagesAsyncTask
import dev.patrickgold.florisboard.customization.network.client.BusinessFeatureApiClient
import dev.patrickgold.florisboard.customization.network.service.AppBaseLocalService
import dev.patrickgold.florisboard.customization.network.service.BusinessFeaturesRemoteData
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.Retrofit
import java.util.*

object BusinessFeatureRepository : AppBaseRepository<BusinessFeaturesRemoteData, AppBaseLocalService>() {

  suspend fun getAllUpdates(fpId: String?, clientId: String, skipBy: Int, limit: Int): Response<Updates> {
    val queries: MutableMap<String, String> = HashMap()
    queries["fpId"] = fpId?:""
    queries["clientId"] = clientId
    queries["skipBy"] = skipBy.toString() + ""
    queries["limit"] = limit.toString() + ""
    return remoteDataSource.getAllUpdates(queries)
  }

  suspend fun getAllDetails(fpTag: String?, clientId: String): Response<CustomerDetails> {
    val queries: MutableMap<String, String> = HashMap()
    queries["clientId"] = clientId
    return remoteDataSource.getAllDetails(fpTag, queries)
  }

  suspend fun getAllProducts(fpTag: String?, clientId: String, skipBy: Int, identifierType: String): Response<ProductResponse> {
    val queries: MutableMap<String, String> = HashMap()
    queries["fpTag"] = fpTag ?: ""
    queries["clientId"] = clientId
    queries["skipBy"] = skipBy.toString() + ""
    queries["identifierType"] = identifierType
    return remoteDataSource.getAllProducts(queries)
  }

  fun getAllImageList(listener: GetGalleryImagesAsyncTask.GetGalleryImagesInterface, fpId: String) {
    val gallery = GetGalleryImagesAsyncTask()
    gallery.setGalleryInterfaceListener(listener, fpId)
    gallery.execute()
  }

  suspend fun getMerchantSummary(clientId: String?,fpTag:String?): Response<MerchantSummaryResponse> {
    return remoteDataSource.getMerchantSummary(clientId,fpTag)
  }

  override fun getRemoteDataSourceClass(): Class<BusinessFeaturesRemoteData> = BusinessFeaturesRemoteData::class.java

  override fun getLocalDataSourceInstance(): AppBaseLocalService = AppBaseLocalService()

  override fun getApiClient(): Retrofit = BusinessFeatureApiClient.shared.retrofit
}
