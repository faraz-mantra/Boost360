package com.boost.marketplace.interfaces

import com.boost.dbcenterapi.upgradeDB.model.YoutubeVideoModel

interface VideosListener {
    fun onPlayYouTubeVideo(videoItem: YoutubeVideoModel)
}