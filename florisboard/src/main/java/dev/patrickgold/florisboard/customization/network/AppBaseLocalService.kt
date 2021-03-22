package dev.patrickgold.florisboard.customization.network

import com.framework.base.BaseLocalService
import com.framework.base.BaseResponse

open class AppBaseLocalService : BaseLocalService() {

    fun saveToLocal(response: BaseResponse, taskCode: TaskCode) {
        super.saveToLocal(response, taskCode.ordinal)
    }

}