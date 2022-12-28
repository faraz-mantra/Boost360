package com.appservice.model.domainBooking

import com.framework.base.BaseResponse
import com.framework.utils.*
import com.google.gson.annotations.SerializedName
import java.io.Serializable

const val DOMAIN_DETAILS_DATA = "DOMAIN_DETAILS_DATA"

data class DomainDetailsResponse(

    @field:SerializedName("fpTag")
    val fpTag: String? = null,

    @field:SerializedName("isFailed")
    val isFailed: Boolean? = null,

    @field:SerializedName("domainType")
    val domainType: Any? = null,

    @field:SerializedName("ExpiresOn")
    val expiresOn: String? = null,

    @field:SerializedName("isActive")
    val isActive: Boolean? = null,

    @field:SerializedName("isPending")
    val isPending: Boolean? = null,

    @field:SerializedName("isLinked")
    val isLinked: Boolean? = null,

    @field:SerializedName("domainName")
    val domainName: String? = null,

    @field:SerializedName("hasDomain")
    val hasDomain: Boolean? = null,

    @field:SerializedName("ErrorMessage")
    val errorMessage: Any? = null,

    @field:SerializedName("isExpired")
    val isExpired: Boolean? = null,

    @field:SerializedName("ActivatedOn")
    val activatedOn: String? = null,

    @field:SerializedName("NameServers")
    val nameServers: Any? = null

) : BaseResponse(), Serializable{

    companion object {
        fun saveDomainDetailsData(domainDetails: DomainDetailsResponse?) {
            PreferencesUtils.instance.saveData(DOMAIN_DETAILS_DATA, convertObjToString(domainDetails ?: DomainDetailsResponse()) ?: "")
        }

        fun getDomainDetailsData(): DomainDetailsResponse? {
            return convertStringToObj(PreferencesUtils.instance.getData(DOMAIN_DETAILS_DATA, "") ?: "")
        }

        fun isDomainAvailable() : Boolean {
            val domainDetailsData = getDomainDetailsData()
            return domainDetailsData?.domainName != null && domainDetailsData.domainName.isNotEmpty()
        }
    }
}
