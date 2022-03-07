package com.framework.utils

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.framework.BaseApplication
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource

object ExoPlayerUtils {

    val defaultDF = DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true)
    lateinit var player :ExoPlayer
    private var audioProgressHandler= Handler(Looper.getMainLooper())
    private var audioProgressRunnable: Runnable?=null
    var isPlayingChanged: ((Boolean) -> Unit?)? =null
    var playBackStateChanged: ((Int) -> Unit?)? =null
    //please create new instance of exoplayer if you are using it on different screen or use old instance if on same screen
    fun newInstance(){
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.CONTENT_TYPE_MUSIC)
            .build()
        val defaultDF = DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true)
        player = ExoPlayer.Builder(BaseApplication.instance)
            .setAudioAttributes(audioAttributes,true)
            .setMediaSourceFactory(DefaultMediaSourceFactory(defaultDF)).build()

    playerListener()
    }

    private fun playerListener() {
        player.addListener(object :Player.Listener{
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                playBackStateChanged?.invoke(playbackState)
            }
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                if (isPlaying){
                    startTracking()
                }else{
                    stopTracking()
                }
                isPlayingChanged?.invoke(isPlaying)

            }

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                Toast.makeText(BaseApplication.instance, error.localizedMessage.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }


    fun startTracking(){
        if (audioProgressRunnable==null){
            onProgressChanged {  }
        }
        audioProgressHandler.postDelayed(audioProgressRunnable!!, 100)
    }


    fun stopTracking(){
        audioProgressRunnable?.let { audioProgressHandler.removeCallbacks(it) }
    }

    fun onProgressChanged(onProgressChanged:(progress:Long)->Unit){
        audioProgressRunnable = Runnable {
            onProgressChanged.invoke(player.currentPosition)
            audioProgressHandler.postDelayed(audioProgressRunnable!!, 1000)
        }
    }


    fun play(url:String,id: Int,seek:Long=0){
        val mediaItem = MediaItem.Builder().setMediaId(id.toString()).setUri(url)
            .build()
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
        player.seekTo(seek)

    }

    fun release(){
        player.release()
        stopTracking()
    }
}