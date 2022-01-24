package com.onboarding.nowfloats.model.channel.statusResponse

import android.widget.LinearLayout
import androidx.core.view.children
import com.framework.base.BaseResponse
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.utils.*
import com.framework.views.customViews.CustomImageView
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class ChannelAccessStatusResponse(
  @SerializedName("channels")
  var channels: ChannelsType? = null,
  @SerializedName("connected_at")
  var connectedAt: String? = null,
  @SerializedName("last_activity_at")
  var lastActivityAt: String? = null,
  @SerializedName("nowfloats_id")
  var nowfloatsId: String? = null,
  @SerializedName("success")
  var success: Boolean? = null,
) : BaseResponse(), Serializable {

  companion object {

    const val CONNECTED_CHANNELS = "connected_channels"
    const val CHANNEL_SHARE_URL = "channel_share_url"

    fun getConnectedChannel(): ArrayList<String> {
      val resp = PreferencesUtils.instance.getData(CONNECTED_CHANNELS, "") ?: ""
      return ArrayList(convertStringToList(resp) ?: ArrayList())
    }

    fun visibleChannels(view: LinearLayout) {
      for (it in view.children) {
        val customImageView = it as? CustomImageView
        val tag = customImageView?.tag
        if (getConnectedChannel().contains(tag)) {
          customImageView?.visible()
        } else {
          customImageView?.gone()
        }
      }
    }

    fun saveDataConnectedChannel(connectedChannels: ArrayList<String>?) {
      PreferencesUtils.instance.saveData(CONNECTED_CHANNELS, convertListObjToString(connectedChannels ?: ArrayList()) ?: "")
    }
  }
}