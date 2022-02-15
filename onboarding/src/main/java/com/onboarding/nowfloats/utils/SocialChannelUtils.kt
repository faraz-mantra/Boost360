package com.onboarding.nowfloats.utils

import com.framework.BaseApplication
import com.framework.pref.UserSessionManager
import com.onboarding.nowfloats.model.channel.statusResponse.CHANNEL_STATUS_SUCCESS
import com.onboarding.nowfloats.model.channel.statusResponse.ChannelsType

object SocialChannelUtils {

    fun storeSocialStatus(channelsAccessToken: ChannelsType?) {
        val userSessionManager = UserSessionManager(BaseApplication.instance)
        userSessionManager?.isIGActive = channelsAccessToken?.instagram?.status
            .equals(CHANNEL_STATUS_SUCCESS, true) == true
        userSessionManager?.isTwitterActive = channelsAccessToken?.twitter?.status
            .equals(CHANNEL_STATUS_SUCCESS, true) == true
        userSessionManager?.isFBPageActive = channelsAccessToken?.facebookpage?.status
            .equals(CHANNEL_STATUS_SUCCESS, true) == true
        userSessionManager?.isFBUserActive = channelsAccessToken?.facebookusertimeline?.status
            .equals(CHANNEL_STATUS_SUCCESS, true) == true
    }
}