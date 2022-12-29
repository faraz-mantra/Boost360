package com.appservice.constant

import com.framework.BuildConfig

object URLs {

    //val BUILD_MY_LOGO_BY_LOGOTRON_URL_LIVE ="https://www.buildmylogo.co/aff.php?aff=300&promocode=Boost360FreeLogo"
    val BUILD_MY_LOGO_BY_LOGOTRON_URL_LIVE ="https://www.buildmylogo.co/index.php?aff=300&promocode=Boost360FreeLogo"
    val BUILD_MY_LOGO_BY_LOGOTRON_URL_DEMO ="http://157.230.188.91/index.php?aff=300&promocode=Boost360FreeLogo"

    fun getLogotronUrl(logoUrl:(String)->Unit) {
            logoUrl.invoke(if (BuildConfig.BUILD_TYPE == "qa" || BuildConfig.BUILD_TYPE == "debug")
                BUILD_MY_LOGO_BY_LOGOTRON_URL_DEMO
            else
                BUILD_MY_LOGO_BY_LOGOTRON_URL_LIVE)
            return
    }

}