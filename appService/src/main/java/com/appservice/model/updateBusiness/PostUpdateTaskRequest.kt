package com.appservice.model.updateBusiness

import com.framework.base.BaseRequest
import java.io.Serializable

data class PostUpdateTaskRequest(
  var clientId: String? = null,
  var message: String? = null,
  var isPictureMessage: Boolean? = null,
  var merchantId: String? = null,
  var parentId: String? = null,
  var sendToSubscribers: Boolean? = null,
  var socialParameters: String? = null,
  var type:String?=null,
  var tag:ArrayList<String>?=null,
  var extradetails:HashMap<String,String>?=null
) : BaseRequest(), Serializable
