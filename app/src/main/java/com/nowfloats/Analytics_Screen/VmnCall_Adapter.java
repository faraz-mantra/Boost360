package com.nowfloats.Analytics_Screen;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.all.All;
import com.nowfloats.Analytics_Screen.model.VmnCallModel;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

@Deprecated
interface AllowAudioPlay {
    boolean allowAudioPlay();

    void toggleAllowAudioPlayFlag(boolean setValue);
}

/**
 * Created by Admin on 23-06-2017.
 */
@Deprecated
public class VmnCall_Adapter extends RecyclerView.Adapter<VmnCall_Adapter.MyHolder> {

    NotificationManagerCompat notificationManager;
    private ArrayList<VmnCallModel> mList;
    private int currentPlay = -1;
    private Context mContext;
    private NotificationCompat.Builder mBuilder;
    private AllowAudioPlay mAllowAudioPlay;

    VmnCall_Adapter(Context context, ArrayList<VmnCallModel> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.single_item_vmn_call_item, parent, false);
        return new MyHolder(convertView);
    }

    public void updateList(ArrayList<VmnCallModel> listItems) {
        this.mList = listItems;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final MyHolder holder, int position) {

        holder.totaltime = getTimeFromMilliSeconds(mList.get(position).getCallDuration());
        holder.mediaData = mList.get(position);

        holder.seekBar.setProgress(mList.get(position).getAudioPosition());

        if (mList.get(position).getAudioPosition() == 0 && mList.get(position).getAudioLength() == 0 && !mList.get(position).isAudioPlayState()) {
            holder.seekBar.setProgress(0);
            holder.audioStartTime.setText("0:00");
            holder.audioEndTime.setText(" / 0:00");

            /*            if(mList.get(position).getCallRecordingUri() != null && !mList.get(position).getCallRecordingUri().equals("None")){
             *//* Uri uri = Uri.parse(mList.get(position).getCallRecordingUri());
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                int millSecond = Integer.parseInt(durationStr);*//*
                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    Log.v("getAudioLength2", " "+ mList.get(position).getCallRecordingUri());
                    mediaPlayer.setDataSource(mList.get(position).getCallRecordingUri());
//                    mediaPlayer.prepare();
                    mediaPlayer.prepareAsync();
                    Log.v("getAudioLength3", " "+ getTimeFromMilliSeconds(mediaPlayer.getDuration()));
                    holder.audioEndTime.setText(" / "+ getTimeFromMilliSeconds(mediaPlayer.getDuration()));
//                    Log.v("getAudioLength3", " "+ mediaPlayer.getDuration());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }*/

            holder.currentDuration = 0;
            holder.playPauseButton.setImageResource(R.drawable.ic_audio_play);
        } else {
            holder.seekBar.setProgress(mList.get(position).getAudioPosition());
            holder.seekBar.setMax(mList.get(position).getAudioLength());
            holder.audioStartTime.setText(getTimeFromMilliSeconds(mList.get(position).getAudioPosition()));
            holder.audioEndTime.setText(" / " + getTimeFromMilliSeconds(mList.get(position).getAudioLength()));
            holder.currentDuration = mList.get(position).getAudioPosition();
            if (mList.get(position).isAudioPlayState()) {
                holder.playPauseButton.setImageResource(R.drawable.ic_pause_gray);
            } else {
                holder.playPauseButton.setImageResource(R.drawable.ic_audio_play);
            }
        }


        final VmnCallModel childModel = mList.get(position);
        holder.date.setText(getDate(Methods.getDateFormat(childModel.getCallDateTime())));
        holder.time.setText(getDate(Methods.getTimeFormat(childModel.getCallDateTime())));
        holder.divider.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        if (childModel.getCallStatus().equalsIgnoreCase("MISSED")) {
            holder.callType.setText("Missed Call");

            //hide player and line
            holder.playerLayout.setVisibility(View.GONE);
            holder.divider.setVisibility(View.GONE);
        } else {
            holder.playerLayout.setVisibility(View.VISIBLE);
            holder.divider.setVisibility(View.VISIBLE);
            holder.callType.setText("Connected call");
            holder.playPauseButton.setOnClickListener(v -> {
                if (!holder.mediaPlayer.isPlaying()) {
                    // This block is triggered if media is not playing.
                    if (mAllowAudioPlay.allowAudioPlay()) {
                        mAllowAudioPlay.toggleAllowAudioPlayFlag(false); // Block other audios from playing.
                        holder.playPauseButton.setImageResource(R.drawable.ic_pause_gray);
                        currentPlay = holder.getAdapterPosition();
                        if (!TextUtils.isEmpty(mList.get(position).getCallRecordingUri())) {
                            for (int i = 0; i < mList.size(); i++) {
                                mList.get(i).setAudioPlayState(i == position);
                            }
                            if (holder.currentDuration > 0) {
                                holder.start();
                                holder.handler.postDelayed(holder.updateSeekBar, 1000);
                            } else {
                                try {
                                    if (mList.size() > position) {
                                        VmnCallModel callModel = mList.get(position);
                                        if (callModel != null) {
                                            holder.mediaPlayer.setDataSource(callModel.getCallRecordingUri());
                                            holder.mediaPlayer.prepareAsync(); // might take long! (for buffering, etc)
                                        }
                                    }
                                } catch (Exception e) {
                                    if (e.getLocalizedMessage() != null)
                                        Log.v("AUDIO_EXCEPTION", e.getLocalizedMessage());
                                    else if (e.getMessage() != null)
                                        Log.v("AUDIO_EXCEPTION", e.getMessage());
                                }
                            }
                        } else {
                            Toast.makeText(mContext, "Can't get recording url", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mContext, "You can only play one audio clip at a time.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    holder.pause();
                    holder.playPauseButton.setImageResource(R.drawable.ic_audio_play);
                    mAllowAudioPlay.toggleAllowAudioPlayFlag(true); // Allow other audios to play.
                }
            });
        }
        holder.number.setText(mList.get(position).getCallerNumber());
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Methods.makeCall(mContext, mList.get(position).getCallerNumber());
            }
        });

        //player listeners
        holder.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                holder.handler.removeCallbacks(holder.updateSeekBar);
                holder.mediaPlayer.reset();
                holder.playPauseButton.setImageResource(R.drawable.ic_audio_play);
                holder.seekBar.setProgress(0);
                holder.audioStartTime.setText("0:00");
                holder.audioEndTime.setText(" / 0:00");
                holder.currentDuration = 0;
                mList.get(position).setAudioPlayState(false);
                mAllowAudioPlay.toggleAllowAudioPlayFlag(true); // Allow other audios to play.
            }
        });

        holder.mediaPlayer.setOnPreparedListener(mp -> {
            holder.audioEndTime.setText(" / " + getTimeFromMilliSeconds(mp.getDuration()));
            holder.seekBar.setMax(mp.getDuration());
            holder.start();
            holder.handler.postDelayed(holder.updateSeekBar, 1000);

            //set audio length
            mList.get(position).setAudioLength(mp.getDuration());

            //set audio play state
            mList.get(position).setAudioPlayState(true);
        });

        holder.updateSeekBar = new Runnable() {
            @Override
            public void run() {
                if (holder.mediaPlayer == null) {
                    return;
                }

                holder.currentDuration = holder.mediaPlayer.getCurrentPosition();
                //int seekBarPos = seekBarPos(duration);
                String time = getTimeFromMilliSeconds(holder.currentDuration);
                Log.v("ggg", holder.currentDuration + " " + time);
                holder.seekBar.setProgress(holder.currentDuration);
                holder.audioStartTime.setText(time);
                if (holder.currentDuration == holder.mediaPlayer.getDuration() || !holder.isPlaying()) {
                    return;
                }

                holder.handler.postDelayed(holder.updateSeekBar, 1000);
            }
        };
    }

    public void setAllowAudioPlay(AllowAudioPlay allowAudioPlay) {
        mAllowAudioPlay = allowAudioPlay;
    }

    private String getDate(String date) {
      /*  int comaIndex = DATE.indexOf(",");
        String subYear = DATE.substring(comaIndex,DATE.lastIndexOf(" at"));*/
        return date.replaceAll(",.*?at", /*"'"+subYear.substring(subYear.length()-2)*/ "");
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private String getTimeFromMilliSeconds(int pos) {
        int seconds = (pos / 1000) % 60;
        int minutes = ((pos / (1000 * 60)) % 60);
        return String.format(Locale.ENGLISH, "%d:%02d", minutes, seconds);
    }

    class MyHolder extends RecyclerView.ViewHolder {

        TextView number, date, time, callType, audioStartTime, audioEndTime;
        ImageView playPauseButton;
        View divider;
        LinearLayout playerLayout, mainLayout;
        SeekBar seekBar;
        boolean audioPlayState = false;
        String totaltime;
        int currentDuration = 0;
        Runnable updateSeekBar;
        private MediaPlayer mediaPlayer;
        private VmnCallModel mediaData;
        private Handler handler;

        public MyHolder(View itemView) {
            super(itemView);
            handler = new Handler();
            number = (TextView) itemView.findViewById(R.id.tv_number);
            date = (TextView) itemView.findViewById(R.id.tv_date);
            time = (TextView) itemView.findViewById(R.id.tv_time);
            playPauseButton = itemView.findViewById(R.id.tv_play);
            divider = itemView.findViewById(R.id.divider);
            callType = itemView.findViewById(R.id.tv_call_type);
            playerLayout = itemView.findViewById(R.id.player_layout);
            mainLayout = itemView.findViewById(R.id.llayout_number);
            seekBar = (SeekBar) itemView.findViewById(R.id.seekBar);
            audioStartTime = itemView.findViewById(R.id.tv_rec_time);
            audioEndTime = itemView.findViewById(R.id.tv_end_time);
            mediaPlayer = new MediaPlayer();
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        mediaPlayer.seekTo(progress);
                        mList.get(getAdapterPosition()).setAudioPosition(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }

        private void pause() {
            mediaPlayer.pause();
            audioPlayState = false;
        }

        private void start() {
            audioPlayState = true;
            mediaPlayer.start();
            if (currentDuration != 0) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        }

        public void releaseResources() {
            if (mediaPlayer != null) {
                mediaPlayer.reset();
            }

        }

        private boolean isPlaying() {
            return mediaPlayer.isPlaying();
        }
    }

}