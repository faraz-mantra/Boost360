package com.nowfloats.facebook.graph

import android.os.Bundle
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.nowfloats.facebook.constants.FacebookGraphPath
import com.nowfloats.facebook.constants.FacebookGraphRequestType
import com.nowfloats.facebook.models.BaseFacebookGraphResponse
import com.nowfloats.facebook.models.userDetails.FacebookGraphUserDetailsResponse
import com.nowfloats.facebook.models.userPages.FacebookGraphUserPagesErrorModel
import com.nowfloats.facebook.models.userPages.FacebookGraphUserPagesResponse

object FacebookGraphManager {

  enum class ProfilePictureType {
    Small, Normal, Large, Square
  }

  interface GraphRequestUserAccountCallback {
    fun onCompleted(
      type: FacebookGraphRequestType,
      facebookGraphResponse: BaseFacebookGraphResponse?
    )
  }

  fun getProfilePictureUrl(
    id: String,
    type: ProfilePictureType = ProfilePictureType.Square
  ): String {
    return "https://graph.facebook.com/v6.0/$id/picture?type=${type.name.toLowerCase()}"
  }

  fun requestUserPages(accessToken: AccessToken?, callback: GraphRequestUserAccountCallback) {
    val request = GraphRequest.newGraphPathRequest(
      accessToken,
      FacebookGraphPath.USER_PAGES
    ) { graphResponse ->

      val response = try {
        Gson().fromJson(graphResponse.rawResponse, FacebookGraphUserPagesResponse::class.java)
      } catch (e: Exception) {
        val error = FacebookGraphUserPagesErrorModel(error = e.localizedMessage)
        FacebookGraphUserPagesResponse(error = error)
      }
      callback.onCompleted(FacebookGraphRequestType.USER_PAGES, response)
    }
    request.executeAsync()
  }

  fun requestIGAccount(pageId:String,accessToken: AccessToken?) {
    val request = GraphRequest.newGraphPathRequest(
      accessToken,
      pageId
    ) { graphResponse ->

      graphResponse.rawResponse

    }
    val parameters = Bundle()
    parameters.putString("fields", "instagram_business_account")
    request.parameters = parameters
    request.executeAsync()
  }


  fun requestUserPublicDetails(
    accessToken: AccessToken?,
    userId: String,
    callback: GraphRequestUserAccountCallback
  ) {
    val request = GraphRequest.newGraphPathRequest(accessToken, userId) { graphResponse ->
      val response = try {
        Gson().fromJson(graphResponse.rawResponse, FacebookGraphUserDetailsResponse::class.java)
      } catch (e: JsonSyntaxException) {
        FacebookGraphUserDetailsResponse(error = e.localizedMessage)
      }
      callback.onCompleted(FacebookGraphRequestType.USER_DETAILS, response)
    }
    request.executeAsync()
  }

  fun sdasd() {

  }
}