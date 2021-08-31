package com.appservice.rest.repository

import android.content.Context
import com.appservice.R
import com.appservice.base.rest.AppBaseLocalService
import com.appservice.model.catalog.CatalogTileModel
import com.framework.base.BaseResponse
import io.reactivex.Observable

object CatalogLocalDataSource:AppBaseLocalService() {

  fun getSettingsTiles(context: Context): Observable<BaseResponse> {
    return fromJsonRes(context =context , R.raw.appointment_setting,CatalogTileModel::class.java)
  }
  fun getEcommerceSettingsTiles(context: Context): Observable<BaseResponse> {
    return fromJsonRes(context =context , R.raw.ecommerce_settings,CatalogTileModel::class.java)
  }
}