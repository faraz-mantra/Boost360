package com.framework.NetworkCertificate

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import okhttp3.CertificatePinner

object NetworkCertificate {
     fun certificatePinner(): CertificatePinner{
        val jsonString = FirebaseRemoteConfig.getInstance().getString("network_security_config")
        if(jsonString.isNotEmpty() && jsonString.equals("{}").not()){
            val pinList = Gson().fromJson(jsonString, NetworkCertificateModule::class.java)
            val pinner = CertificatePinner.Builder()
            for(singlePin in pinList) {
                if(!singlePin.domain.isNullOrEmpty() && singlePin.pin.isNullOrEmpty())
                    pinner.add(singlePin.domain,"sha256/"+singlePin.pin)
            }
            return pinner.build()
        }
        return CertificatePinner.Builder().build()
    }
}