package com.nowfloats.instagram.graph

import android.os.Bundle
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.framework.utils.convertStringToObj
import com.nowfloats.instagram.models.IGFBPageLinkedResponse


object IGGraphManager {

    interface GraphRequestIGAccountCallback {
        fun onCompleted(
            response:IGFBPageLinkedResponse?
        )
    }

    fun requestIGAccount(pageId:String, accessToken: AccessToken?, callback: GraphRequestIGAccountCallback) {
        val request = GraphRequest.newGraphPathRequest(
            accessToken,
            pageId
        ) { graphResponse ->
            callback.onCompleted(convertStringToObj<IGFBPageLinkedResponse>(graphResponse.rawResponse))

        }
        val parameters = Bundle()
        parameters.putString("fields", "instagram_business_account")
        request.parameters = parameters
        request.executeAsync()
    }
}