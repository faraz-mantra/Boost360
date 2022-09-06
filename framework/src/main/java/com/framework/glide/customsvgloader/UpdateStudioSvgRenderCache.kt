package com.framework.glide.customsvgloader

import android.content.Context
import com.bumptech.glide.Glide
import com.framework.R
import com.framework.constants.PosterKeys
import com.framework.pref.UserSessionManager
import com.framework.pref.getDomainName
import com.framework.utils.PreferencesKey
import com.framework.utils.application
import com.framework.utils.fetchString
import com.framework.utils.toBase64

class UpdateStudioSvgRenderCache:SvgRenderCacheUtil() {

    private  val TAG = "FestivePosterSvgRenderC"

    val session = UserSessionManager(application())
    val userWebsite = session.getDomainName(true)?:""
    val userPhone = session.userPrimaryMobile?:""
    val profileUrl =
        session.getFPDetails(PreferencesKey.GET_FP_DETAILS_LogoUrl.name)
    val base64 = Glide.with(application()).asBitmap().load(profileUrl).submit().get().toBase64()


    companion object {
        var instance=UpdateStudioSvgRenderCache()

        suspend fun refresh(){
            instance=UpdateStudioSvgRenderCache()
        }
    }
    fun replace(
        svgString: String?,
        context: Context,
    ): String? {

        var result =svgString

        result= result?.replace("Lorumipsum.com",userWebsite)
        result= result?.replace("loreuminpsum.com",userWebsite)
        result= result?.replace("loreoispum.com",userWebsite)
        result= result?.replace("+91 9999999999",userPhone)
        val imgStart = result?.indexOf("data:image/png;base64,")?:result?.indexOf("data:image/jpeg;base64,")
        imgStart?:return result
        val imgEnd = result?.indexOf("/>",imgStart)
        imgEnd?:return result
        result= result?.replaceRange(imgStart,imgEnd,"data:image/jpeg;base64,$base64\"")

        return result
    }

    private fun getDefaultReplaceVal(name: String?): String? {
        return when(name){
            PosterKeys.business_email->{
                fetchString(R.string.your_email_address)
            }
            PosterKeys.business_name->{
                fetchString(R.string.your_business_name)
            }
            PosterKeys.user_name->{
                fetchString(R.string.your_name)
            }
            PosterKeys.user_contact->{
                fetchString(R.string.your_mobile_number)
            }
            else ->""

        }
    }


}