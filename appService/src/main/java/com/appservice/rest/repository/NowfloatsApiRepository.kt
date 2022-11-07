package com.appservice.rest.repository

import com.appservice.base.rest.AppBaseRepository
import com.appservice.model.serviceProduct.CatalogProduct
import com.appservice.model.serviceProduct.delete.DeleteProductRequest
import com.appservice.model.serviceProduct.service.ServiceListingRequest
import com.appservice.model.serviceProduct.update.ProductUpdate
import com.appservice.model.servicev1.DeleteSecondaryImageRequest
import com.appservice.model.servicev1.DeleteServiceRequest
import com.appservice.model.servicev1.ServiceModelV1
import com.appservice.model.servicev1.UploadImageRequest
import com.appservice.model.testimonial.AddTestimonialImageRequest
import com.appservice.model.testimonial.AddUpdateTestimonialRequest
import com.appservice.model.testimonial.ListTestimonialRequest
import com.appservice.model.updateBusiness.pastupdates.CategoryUi
import com.appservice.model.updateBusiness.pastupdates.GetCategoryResponse
import com.appservice.model.updateBusiness.pastupdates.asDomainModels
import com.appservice.rest.TaskCode
import com.appservice.rest.apiClients.NowfloatsApiClient
import com.appservice.rest.services.NowfloatsRemoteData
import com.framework.base.BaseResponse
import com.framework.utils.getResponse
import com.inventoryorder.rest.repositories.NowFloatsRepository
import io.reactivex.Observable
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Retrofit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object NowfloatsApiRepository : AppBaseRepository<NowfloatsRemoteData, CatalogLocalDataSource>() {

  fun createService(request: ServiceModelV1?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.createService(request), TaskCode.POST_CREATE_SERVICE)
  }

//  fun getSettingsTiles(context: Context): Observable<BaseResponse> {
//    return makeLocalRequest(localDataSource.getSettingsTiles(context =context ),TaskCode.GET_CATALOG_SETTINGS_TILES)
//  }

//  fun getEcommerceSettingsTiles(context: Context): Observable<BaseResponse> {
//    return makeLocalRequest(localDataSource.getSettingsTiles(context =context ),TaskCode.GET_CATALOG_SETTINGS_TILES)
//  }

  fun updateService(request: ServiceModelV1?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.updateService(request), TaskCode.POST_UPDATE_SERVICE)
  }

  fun addPrimaryImage(request: UploadImageRequest?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.addImage(request), TaskCode.ADD_SERVICE_PRIMARY_IMAGE_V1)
  }

  fun addSecondaryImage(request: UploadImageRequest?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.addImage(request), TaskCode.ADD_SERVICE_SECONDARY_IMAGE_V1)
  }

  fun getServiceDetail(serviceId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getServiceDetails(serviceId), TaskCode.GET_SERVICE_DETAILS)
  }

  fun deleteService(request: DeleteServiceRequest?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.deleteService(request), TaskCode.DELETE_SERVICE)
  }

  fun addUpdateImageProductService(clientId: String?, requestType: String?, requestId: String?, totalChunks: Int?, currentChunkNumber: Int?, productId: String?, requestBody: RequestBody?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.addUpdateImageProductService(clientId, requestType, requestId, totalChunks, currentChunkNumber, productId, requestBody), TaskCode.ADD_UPDATE_IMAGE_PRODUCT_SERVICE)
  }

  fun getNotificationCount(clientId: String?, fpId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getNotificationCount(clientId, fpId), TaskCode.GET_NOTIFICATION)
  }

  override fun getRemoteDataSourceClass(): Class<NowfloatsRemoteData> {
    return NowfloatsRemoteData::class.java
  }

  override fun getLocalDataSourceInstance(): CatalogLocalDataSource {
    return CatalogLocalDataSource
  }

  override fun getApiClient(): Retrofit {
    return NowfloatsApiClient.shared.retrofit
  }

  fun createProduct(request: CatalogProduct?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.createProduct(request = request), TaskCode.POST_CREATE_PRODUCT)
  }

  fun updateProduct(request: ProductUpdate?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.updateProduct(request), TaskCode.POST_UPDATE_PRODUCT)
  }

  fun deleteProduct(request: DeleteProductRequest?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.deleteProduct(request), TaskCode.POST_UPDATE_PRODUCT)
  }

  fun addUpdateImageProduct(clientId: String?, requestType: String?, requestId: String?, totalChunks: Int?, currentChunkNumber: Int?, productId: String?, requestBody: RequestBody?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.addUpdateImageProduct(clientId, requestType, requestId, totalChunks, currentChunkNumber, productId, requestBody), TaskCode.ADD_UPDATE_IMAGE_PRODUCT_SERVICE)
  }

  fun deleteSecondaryImage(request: DeleteSecondaryImageRequest?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.deleteSecondaryImage(request), TaskCode.DELETE_SECONDARY_IMAGE)
  }

  fun getServiceListing(request: ServiceListingRequest): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getServiceListing(request), TaskCode.GET_SERVICE_LISTING)
  }

  fun getServiceSearchListing(fpTag: String?, fpId: String?, searchString: String?, offset: Int? = 0, limit: Int? = 0): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getServiceSearchListing(fpTag, fpId, searchString, offset, limit), TaskCode.GET_SERVICE_LISTING)
  }

  //TODO Testimonial
  fun getTestimonialList(request: ListTestimonialRequest?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getTestimonialList(request), TaskCode.GET_TESTIMONIAL_LIST)
  }

  fun getTestimonialDetail(testimonialId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.getTestimonialDetail(testimonialId), TaskCode.GET_TESTIMONIAL_DETAIL)
  }

  fun addTestimonial(request: AddUpdateTestimonialRequest?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.addTestimonial(request), TaskCode.ADD_TESTIMONIAL)
  }

  fun updateTestimonial(request: AddUpdateTestimonialRequest?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.updateTestimonial(request), TaskCode.UPDATE_TESTIMONIAL)
  }

  fun updateTestimonialImage(request: AddTestimonialImageRequest?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.updateTestimonialImage(request), TaskCode.ADD_IMAGE_TESTIMONIAL)
  }

  fun deleteTestimonial(testimonialId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.deleteTestimonial(getRequestTestimonial(testimonialId)), TaskCode.DELETE_TESTIMONIAL)
  }

  fun deleteTestimonialImage(testimonialId: String?): Observable<BaseResponse> {
    return makeRemoteRequest(remoteDataSource.deleteTestimonialImage(getRequestTestimonial(testimonialId)), TaskCode.DELETE_IMAGE_TESTIMONIAL)
  }

  private fun getRequestTestimonial(testimonialId: String?): MutableMap<String, String> {
    return mutableMapOf<String, String>().apply { put("testimonialId", testimonialId ?: "") }

  }

  fun getCategories(): Observable<BaseResponse> {
    return makeRemoteRequest(
      remoteDataSource.getUpdatesCategories(),
      TaskCode.GET_UPDATES_CATS
    )
  }

  suspend fun getCategoriesUI()=
    suspendCoroutine<List<CategoryUi>>{ cont->
      getCategories().getResponse {
        if (it.isSuccess()){
          val response = it as? GetCategoryResponse
          cont.resume(response!!.Result.asDomainModels())

        }else{
          cont.resumeWithException(Exception())
        }
      }
    }
}
