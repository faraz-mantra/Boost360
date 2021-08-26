package dev.patrickgold.florisboard.customization.network.service

import android.content.Context
import com.framework.base.BaseResponse
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.customization.model.response.shareUser.ShareUserDetailResponse

open class FlorisBoardLocalDataSource : AppBaseLocalService() {

 suspend fun getBoostVisitingMessage(context: Context): BaseResponse {
    return fromJsonResNew(context, R.raw.visiting_card_data_share, ShareUserDetailResponse::class.java)
  }
}