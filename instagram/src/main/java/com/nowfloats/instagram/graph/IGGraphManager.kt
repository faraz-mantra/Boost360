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

    interface GraphRequestIGUserCallback {
        fun onCompleted(
            response:IGUserResponse?
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


    fun requestIGUserDetails(igId:String, accessToken: AccessToken?, callback: GraphRequestIGUserCallback) {
        val request = GraphRequest.newGraphPathRequest(
            accessToken,
            igId
        ) { graphResponse ->

            callback.onCompleted(convertStringToObj<IGUserResponse>(graphResponse.rawResponse))

        }
        val parameters = Bundle()
        parameters.putString("fields", "username")
        request.parameters = parameters
        request.executeAsync()
    }
}