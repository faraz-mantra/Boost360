package com.dashboard.base.rest

import com.dashboard.rest.TaskCode
import com.framework.base.BaseLocalService
import com.framework.base.BaseResponse

open class AppBaseLocalService : BaseLocalService() {

  fun saveToLocal(response: BaseResponse, taskCode: TaskCode) {
    super.saveToLocal(response, taskCode.ordinal)
  }

}