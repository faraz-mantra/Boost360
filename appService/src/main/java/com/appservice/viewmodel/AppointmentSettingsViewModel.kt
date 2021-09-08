package com.appservice.viewmodel

import androidx.lifecycle.LiveData
import com.appservice.appointment.model.*
import com.appservice.rest.repository.BoostNowFloatsRepository
import com.appservice.rest.repository.NowfloatsApiRepository
import com.appservice.rest.repository.WithFloatTwoRepository
import com.appservice.ui.model.ServiceListingRequest
import com.framework.base.BaseResponse
import com.framework.models.BaseViewModel
import com.framework.models.toLiveData

class AppointmentSettingsViewModel : BaseViewModel() {
  //    fun updateCODPreferences(request: RequestCODPreference?) {
//
//    }
//  fun getAppointmentSettingsTiles(context: Context): LiveData<BaseResponse> {
//    return NowfloatsApiRepository.getSettingsTiles(context).toLiveData()
//  }
//
//  fun getEcommerceSettingsTiles(context: Context): LiveData<BaseResponse> {
//    return NowfloatsApiRepository.getSettingsTiles(context).toLiveData()
//  }

  fun getServiceListing(request: ServiceListingRequest): LiveData<BaseResponse> {
    return NowfloatsApiRepository.getServiceListing(request).toLiveData()
  }

  fun getFpDetails(fpId: String, map: Map<String, String>): LiveData<BaseResponse> {
    return WithFloatTwoRepository.getFpDetails(fpId, map).toLiveData()
  }

  fun updateCategoryInfo(request: UpdateInfoRequest): LiveData<BaseResponse> {
    return BoostNowFloatsRepository.updateInfo(request).toLiveData()
  }

  fun getDeliveryDetails(floatingPointId: String?, clientId: String?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.getDeliveryDetails(floatingPointId, clientId).toLiveData()
  }

  fun invoiceSetup(request: InvoiceSetupRequest): LiveData<BaseResponse> {
    return WithFloatTwoRepository.invoiceSetup(request).toLiveData()
  }

  fun getPaymentProfileDetails(floatingPointId: String?, clientId: String?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.getPaymentProfileDetails(floatingPointId, clientId).toLiveData()
  }

  fun setupDelivery(request: DeliverySetup): LiveData<BaseResponse> {
    return WithFloatTwoRepository.setupDelivery(request).toLiveData()
  }

  fun addMerchantUPI(request: UpdateUPIRequest): LiveData<BaseResponse> {
    return WithFloatTwoRepository.addMerchantUPI(request).toLiveData()
  }

  fun addBankAccount(floatingPointId: String?, clientId: String?, request: AddBankAccountRequest): LiveData<BaseResponse> {
    return WithFloatTwoRepository.addBankAccount(floatingPointId, clientId, request).toLiveData()
  }

  fun uploadSignature(request: UploadMerchantSignature): LiveData<BaseResponse> {
    return WithFloatTwoRepository.addMerchantSignature(request = request).toLiveData()
  }

  fun getWareHouseAddress(floatingPointId: String?, clientId: String?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.getWareHouseAddress(floatingPointId, clientId).toLiveData()
  }

  fun addWareHouseAddress(request: RequestAddWareHouseAddress): LiveData<BaseResponse> {
    return WithFloatTwoRepository.addWareHouseAddress(request = request).toLiveData()
  }

  fun getAppointmentCatalogStatus(floatingPointId: String?, clientId: String?): LiveData<BaseResponse> {
    return WithFloatTwoRepository.getAppointmentCatalogStatus(floatingPointId, clientId).toLiveData()
  }

  fun updateProductCategoryVerb(request: ProductCategoryVerbRequest): LiveData<BaseResponse> {
    return WithFloatTwoRepository.updateProductCategoryVerb(request).toLiveData()
  }
}