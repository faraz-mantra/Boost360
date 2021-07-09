package com.onboarding.nowfloats.model.channel

enum class ChannelType {
    G_SEARCH, FB_PAGE, G_MAPS, FB_SHOP, WAB, T_FEED, G_BUSINESS;

    companion object {
        fun from(findValue: String): ChannelType = values().first { it.name == findValue }
    }
}