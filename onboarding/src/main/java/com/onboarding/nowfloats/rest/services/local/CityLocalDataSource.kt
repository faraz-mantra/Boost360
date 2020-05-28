package com.onboarding.nowfloats.rest.services.local

import android.content.Context
import com.framework.base.BaseResponse
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.base.rest.AppBaseLocalService
import com.onboarding.nowfloats.rest.response.ResponseDataCity
import io.reactivex.Observable


object CityLocalDataSource : AppBaseLocalService() {

  fun getCities(context: Context): Observable<BaseResponse> {
    return fromJsonRes(context, R.raw.cities, ResponseDataCity::class.java)
  }
}