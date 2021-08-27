package dev.patrickgold.florisboard.customization.network.service

import com.framework.base.BaseLocalService
import com.framework.base.BaseResponse
import dev.patrickgold.florisboard.customization.network.TaskCode

open class AppBaseLocalService : BaseLocalService() {

  fun saveToLocal(response: BaseResponse, taskCode: TaskCode) {
    super.saveToLocal(response, taskCode.ordinal)
  }
}