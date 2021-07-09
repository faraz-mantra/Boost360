package com.appservice.model.razor


import com.framework.base.BaseResponse
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RazorDataResponse(
    @SerializedName("ADDRESS")
    var aDDRESS: String? = null,
    @SerializedName("BANK")
    var bANK: String? = null,
    @SerializedName("BANKCODE")
    var bANKCODE: String? = null,
    @SerializedName("BRANCH")
    var bRANCH: String? = null,
    @SerializedName("CENTRE")
    var cENTRE: String? = null,
    @SerializedName("CITY")
    var cITY: String? = null,
    @SerializedName("CONTACT")
    var cONTACT: String? = null,
    @SerializedName("DISTRICT")
    var dISTRICT: String? = null,
    @SerializedName("IFSC")
    var iFSC: String? = null,
    @SerializedName("IMPS")
    var iMPS: Boolean? = null,
    @SerializedName("MICR")
    var mICR: String? = null,
    @SerializedName("NEFT")
    var nEFT: Boolean? = null,
    @SerializedName("RTGS")
    var rTGS: Boolean? = null,
    @SerializedName("STATE")
    var sTATE: String? = null,
    @SerializedName("UPI")
    var uPI: Boolean? = null
) : BaseResponse(), Serializable