package com.inventoryorder.base.rest

import com.framework.base.BaseLocalService
import com.framework.base.BaseResponse
import com.inventoryorder.rest.TaskCode

open class AppBaseLocalService : BaseLocalService() {

  fun saveToLocal(response: BaseResponse, taskCode: TaskCode) {
    super.saveToLocal(response, taskCode.ordinal)
  }

}