package com.appservice.rest.services

import android.content.Context
import com.appservice.R
import com.appservice.base.rest.AppBaseLocalService
import com.appservice.model.keyboard.KeyboardTabsResponse
import com.framework.base.BaseResponse
import io.reactivex.Observable

object KeyboardLocalDataSource : AppBaseLocalService() {

  fun getTabsKeyboard(context: Context): Observable<BaseResponse> {
    return fromJsonRes(context, R.raw.keyboard_tabs_data, KeyboardTabsResponse::class.java)
  }
}