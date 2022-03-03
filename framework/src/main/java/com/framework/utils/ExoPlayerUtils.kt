package com.framework.utils

import com.framework.BaseApplication
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource

object ExoPlayerUtils {

    val defaultDF = DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true)
    var player = ExoPlayer.Builder(BaseApplication.instance)
        .setMediaSourceFactory(DefaultMediaSourceFactory(defaultDF)).build()

    fun addListener(isPlayingChanged:(isPlaying:Boolean)->Unit){
        player.addListener(object :Player.Listener{
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                isPlayingChanged.invoke(isPlaying)
            }
        })
    }
}