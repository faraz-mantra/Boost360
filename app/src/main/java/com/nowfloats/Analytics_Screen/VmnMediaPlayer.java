package com.nowfloats.Analytics_Screen;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.util.Log;

import com.nowfloats.Analytics_Screen.Search_Query_Adapter.VmnCallAdapter;

import java.io.IOException;

/**
 * Created by Admin on 29-04-2017.
 */

public class VmnMediaPlayer {
    private static MediaPlayer mediaPlayer;
    private Context mContext;
    private String playingUrl;
    private boolean  playOrPause;
    private static VmnMediaPlayer vmnMediaPlayer;

    private VmnMediaPlayer(Context context) {
        mContext = context;
    }

    public static VmnMediaPlayer getInstance(Context context){
        if(vmnMediaPlayer == null){
            vmnMediaPlayer = new VmnMediaPlayer(context);
        }
        return vmnMediaPlayer;
    }
    public void setUpPlayer(VmnCallAdapter.ConnectToVmnPlayer implementer){
        if(mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setWakeMode(mContext, PowerManager.PARTIAL_WAKE_LOCK);
            mediaPlayer.setOnCompletionListener(implementer);
            mediaPlayer.setOnPreparedListener(implementer);
            mediaPlayer.setOnErrorListener(implementer);
        }
    }

    public void reset(){
        mediaPlayer.reset();
    }

    public void seekTo(int i){
        mediaPlayer.seekTo(seekToDuration(i));
    }

    private int seekToDuration(int i){
        int totalDuration = mediaPlayer.getDuration();
        return (i*totalDuration)/100;
    }

    public int getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration(){
        return  mediaPlayer.getDuration();
    }

    public String getUrl(){
        return playingUrl;
    }

    public void setDataUrl(String url){
        Log.v("ggg",url);
        playingUrl = url;
        if(playOrPause){
            Log.v("ggg","isplay");
            stop();
        }
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync(); // might take long! (for buffering, etc)
        } catch (IOException e) {
            Log.v("ggg",e.getMessage());
        }
    }

    public void pause(){
        playOrPause = true;
        mediaPlayer.pause();
    }
    public void start(){
        playOrPause = true;
        mediaPlayer.start();
    }

    public void stop(){
        playOrPause = false;
        mediaPlayer.stop();
        mediaPlayer.reset();
    }

    public boolean isPlaying(){

        return mediaPlayer != null && mediaPlayer.isPlaying();

    }

    public void release(){
        if(mediaPlayer != null){
            Log.v("ggg","released");
            mediaPlayer.release();
            mediaPlayer = null;
            playingUrl = null;
            playOrPause = false;
        }
    }
}
