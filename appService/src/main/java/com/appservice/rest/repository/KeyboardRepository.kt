package com.appservice.rest.repository

import android.content.Context
import com.appservice.base.rest.AppBaseRepository
import com.appservice.rest.TaskCode
import com.appservice.rest.services.KeyboardLocalDataSource
import com.appservice.rest.services.KeyboardRemoteData
import com.framework.base.BaseResponse
import io.reactivex.Observable

object KeyboardRepository : AppBaseRepository<KeyboardRemoteData, KeyboardLocalDataSource>() {

  fun getTabsKeyboard(context: Context): Observable<BaseResponse> {
    return makeLocalRequest(localDataSource.getTabsKeyboard(context), TaskCode.GET_KEYBOARD_TABS)
  }

  override fun getRemoteDataSourceClass(): Class<KeyboardRemoteData> {
    return KeyboardRemoteData::class.java
  }

  override fun getLocalDataSourceInstance(): KeyboardLocalDataSource {
    return KeyboardLocalDataSource
  }
}
