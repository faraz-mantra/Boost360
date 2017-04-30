package com.nowfloats.Analytics_Screen;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.util.Log;
import android.widget.ImageView;

import com.nowfloats.Analytics_Screen.Search_Query_Adapter.VmnCallAdapter;
import com.thinksity.R;

import java.io.IOException;
import java.util.Locale;

/**
 * Created by Admin on 29-04-2017.
 */

public class VmnMediaPlayer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {
    private static MediaPlayer mediaPlayer;
    private Context mContext;
    private String playingUrl;
    private VmnCallAdapter.MyChildHolder childHolder;
    private ImageView prevplayToPause;
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
    public void setUpPlayer(String url){
        if(mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setWakeMode(mContext, PowerManager.PARTIAL_WAKE_LOCK);
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setScreenOnWhilePlaying(true);
        }
        playingUrl = url;
        setDataUrl(url);
    }
    public void setHolder(VmnCallAdapter.MyChildHolder holder){
        setPrevPlayToPause();
        childHolder = holder;
        prevplayToPause = childHolder.playButton;
    }
    Runnable updateProgressBar = new Runnable() {
        @Override
        public void run() {
            int duration=mediaPlayer.getCurrentPosition();
            int seekBarPos = duration*100 / mediaPlayer.getDuration();

            String time=getTimeFromMilliSeconds((long) duration);
            childHolder.progressBar.setProgress(seekBarPos);
            childHolder.recCurrPoint.setText(time);
        }
    };
    public void seekTo(int i){
        mediaPlayer.seekTo(i);
    }
    public int getProgress(){
        return mediaPlayer.getCurrentPosition();
    }
    public void setPrevPlayToPause(){
        if(prevplayToPause == null){
            Log.v("ggg","first recording play");
            return;
        }
        prevplayToPause.setImageResource(R.drawable.ic_play_light);
    }
    public String getUrl(){
        return playingUrl;
    }
    private void setDataUrl(String url){
        Log.v("ggg",url);
        if(mediaPlayer.isPlaying()){
            Log.v("ggg","isplay");
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync(); // might take long! (for buffering, etc)
        } catch (IOException e) {
            Log.v("ggg",e.getMessage());
        }
    }

    public void pause(){
        mediaPlayer.pause();
    }
    public void start(){
        mediaPlayer.start();
        seekTo(childHolder.progressBar.getProgress());
    }
    public boolean isPlaying(){

        return mediaPlayer != null && mediaPlayer.isPlaying();

    }
    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.v("ggg","play");
        start();
    }

    public String getTimeFromMilliSeconds(Long pos) {
        int seconds = (int) (pos / 1000) % 60 ;
        int minutes = (int) ((pos / (1000*60)) % 60);
        return String.format(Locale.ENGLISH,"%02d:%02d", minutes,seconds);
    }
}
