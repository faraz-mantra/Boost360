package dev.patrickgold.florisboard.customization.model.response

import com.framework.base.BaseResponse
import com.framework.utils.*

const val MERCHANT_SUMMARY_RESPONSE = "MERCHANT_SUMMARY_RESPONSE"

data class MerchantSummaryResponse(
    val Entity: List<Map<String, Int>>,
    val ErrorList: List<Any>,
    val OperationStatus: Boolean,
    val ReferenceId: Any
) : BaseResponse(){

    fun saveMerchantSummary() {
        PreferencesUtils.instance.saveData(MERCHANT_SUMMARY_RESPONSE, convertObjToString(this))
    }

    companion object{
        fun getMerchantSummaryWebsite(): MerchantSummaryResponse? {
            return convertStringToObj(PreferencesUtils.instance.getData(MERCHANT_SUMMARY_RESPONSE, "") ?: "")
        }
    }

    fun getCount(key:String): Int? {
        Entity.forEach {
            if (it[key]!=null){
                return it[key]
            }
        }
        return -1
    }
}




