package com.inventoryorder.base.rest

import com.framework.base.BaseLocalService
import com.framework.base.BaseResponse
import com.inventoryorder.rest.Taskcode

open class AppBaseLocalService : BaseLocalService() {

  fun saveToLocal(response: BaseResponse, taskcode: Taskcode) {
    super.saveToLocal(response, taskcode.ordinal)
  }

}