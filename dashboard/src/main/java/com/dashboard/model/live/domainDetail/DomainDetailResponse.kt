package com.dashboard.model.live.domainDetail

import com.framework.base.BaseResponse
import com.framework.utils.PreferencesUtils
import com.framework.utils.convertObjToString
import com.framework.utils.convertStringToObj
import com.framework.utils.getData
import java.io.Serializable
import java.util.*

const val DOMAIN_DETAIL_DATA = "DOMAIN_DETAIL_DATA"

data class DomainDetailResponse(
    var domainName: String? = null,
    var domainType: String? = null,
    var activatedOn: String? = null,
    var expiresOn: String? = null,
    var isHasDomain: Boolean = false,
    var isActive: Boolean = false,
    var isExpired: Boolean = false,
    var errorMessage: String? = null,
    var fpTag: String? = null,
    var isPending: Boolean = false,
    var isFailed: Boolean = false,
    var isLinked: Boolean = false,
    var nameServers: ArrayList<String>? = null,
) : BaseResponse(), Serializable {

  fun getDomainDetail(): DomainDetailResponse? {
    return convertStringToObj(PreferencesUtils.instance.getData(DOMAIN_DETAIL_DATA, "") ?: "")
  }

  fun saveDomainDetail() {
    PreferencesUtils.instance.saveDataN(DOMAIN_DETAIL_DATA, convertObjToString(this) ?: "")
  }
}
