package com.nowfloats.facebook.graph

import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.nowfloats.facebook.constants.FacebookGraphPath
import com.nowfloats.facebook.constants.FacebookGraphRequestType
import com.nowfloats.facebook.constants.FacebookGraphRequestType.USER_ACCOUNT
import com.nowfloats.facebook.models.FacebookGraphMeAccountErrorModel
import com.nowfloats.facebook.models.FacebookGraphMeAccountResponse

object FacebookGraphManager {

  enum class ProfilePictureType{
    Small, Normal, Large, Square
  }

  interface GraphRequestUserAccountCallback {
    fun onCompleted(type: FacebookGraphRequestType,
                    facebookGraphMeAccountResponse: FacebookGraphMeAccountResponse?)
  }

  fun getPageProfilePictureUrl(pageId: String, type: ProfilePictureType = ProfilePictureType.Square): String {
    return "https://graph.facebook.com/v6.0/$pageId/picture?type=${type.name.toLowerCase()}"
  }

  fun requestUserAccount(accessToken: AccessToken?, callback: GraphRequestUserAccountCallback) {
    val request = GraphRequest.newGraphPathRequest(accessToken,
            FacebookGraphPath.USER_ACCOUNT) { graphResponse ->

      val response = try {
        Gson().fromJson(graphResponse.rawResponse, FacebookGraphMeAccountResponse::class.java)
      } catch (e: JsonSyntaxException) {
        val error = FacebookGraphMeAccountErrorModel(error = e.localizedMessage)
        FacebookGraphMeAccountResponse(error = error)
      }
      callback.onCompleted(USER_ACCOUNT, response)
    }
    request.executeAsync()
  }
}