package com.appservice.holder

import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.media.MediaPlayer.OnPreparedListener
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.appservice.R
import com.appservice.databinding.SingleItemVmnCallItemV2Binding
import com.appservice.model.VmnCallModel
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import java.util.*

class VmnCallViewHolder(binding: SingleItemVmnCallItemV2Binding) : AppBaseRecyclerViewHolder<SingleItemVmnCallItemV2Binding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val totaltime = getTimeFromMilliSeconds(model.getCallDuration())
    val model =item as VmnCallModel

    binding.seekBar.setProgress(model.getAudioPosition())

    if (model.getAudioPosition() == 0 && model
        .getAudioLength() == 0 && !model.isAudioPlayState()
    ) {
      holder.seekBar.setProgress(0)
      holder.audioStartTime.setText("0:00")
      holder.audioEndTime.setText(" / 0:00")

                  if(model.getCallRecordingUri() != null && !model.getCallRecordingUri().equals("None")){

       Uri uri = Uri.parse(model.getCallRecordingUri());
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                int millSecond = Integer.parseInt(durationStr);

                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    Log.v("getAudioLength2", " "+ model.getCallRecordingUri());
                    mediaPlayer.setDataSource(model.getCallRecordingUri());
//                    mediaPlayer.prepare();
                    mediaPlayer.prepareAsync();
                    Log.v("getAudioLength3", " "+ getTimeFromMilliSeconds(mediaPlayer.getDuration()));
                    holder.audioEndTime.setText(" / "+ getTimeFromMilliSeconds(mediaPlayer.getDuration()));
//                    Log.v("getAudioLength3", " "+ mediaPlayer.getDuration());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }holder.currentDuration = 0
      holder.playPauseButton.setImageResource(R.drawable.ic_audio_play)
    } else {
      holder.seekBar.setProgress(model.getAudioPosition())
      holder.seekBar.setMax(model.getAudioLength())
      holder.audioStartTime.setText(getTimeFromMilliSeconds(model.getAudioPosition()))
      holder.audioEndTime.setText(
        " / " + getTimeFromMilliSeconds(
          model.getAudioLength()
        )
      )
      holder.currentDuration = model.getAudioPosition()
      if (model.isAudioPlayState()) {
        holder.playPauseButton.setImageResource(R.drawable.ic_pause_gray)
      } else {
        holder.playPauseButton.setImageResource(R.drawable.ic_audio_play)
      }
    }


    val childModel: com.nowfloats.Analytics_Screen.model.VmnCallModel = model
    holder.date.setText(getDate(com.nowfloats.util.Methods.getDateFormat(childModel.getCallDateTime())))
    holder.time.setText(getDate(com.nowfloats.util.Methods.getTimeFormat(childModel.getCallDateTime())))
    holder.divider.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    if (childModel.getCallStatus().equals("MISSED", ignoreCase = true)) {
      holder.callType.setText("Missed Call")

      //hide player and line
      holder.playerLayout.setVisibility(View.GONE)
      holder.divider.setVisibility(View.GONE)
    } else {
      holder.playerLayout.setVisibility(View.VISIBLE)
      holder.divider.setVisibility(View.VISIBLE)
      holder.callType.setText("Connected call")
      holder.playPauseButton.setOnClickListener(View.OnClickListener { v: View? ->
        if (!holder.mediaPlayer.isPlaying()) {
          // This block is triggered if media is not playing.
          if (mAllowAudioPlay.allowAudioPlay()) {
            mAllowAudioPlay.toggleAllowAudioPlayFlag(false) // Block other audios from playing.
            holder.playPauseButton.setImageResource(R.drawable.ic_pause_gray)
            currentPlay = holder.getAdapterPosition()
            if (!TextUtils.isEmpty(model.getCallRecordingUri())) {
              for (i in mList.indices) {
                mList.get(i).setAudioPlayState(i == position)
              }
              if (holder.currentDuration > 0) {
                holder.start()
                holder.handler.postDelayed(holder.updateSeekBar, 1000)
              } else {
                try {
                  if (mList.size > position) {
                    val callModel: com.nowfloats.Analytics_Screen.model.VmnCallModel =
                      model
                    if (callModel != null) {
                      holder.mediaPlayer.setDataSource(callModel.getCallRecordingUri())
                      holder.mediaPlayer.prepareAsync() // might take long! (for buffering, etc)
                    }
                  }
                } catch (e: Exception) {
                  if (e.localizedMessage != null) Log.v(
                    "AUDIO_EXCEPTION",
                    e.localizedMessage
                  ) else if (e.message != null) Log.v(
                    "AUDIO_EXCEPTION",
                    e.message!!
                  )
                }
              }
            } else {
              Toast.makeText(mContext, "Can't get recording url", Toast.LENGTH_SHORT).show()
            }
          } else {
            Toast.makeText(
              mContext,
              "You can only play one audio clip at a time.",
              Toast.LENGTH_SHORT
            ).show()
          }
        } else {
          holder.pause()
          holder.playPauseButton.setImageResource(R.drawable.ic_audio_play)
          mAllowAudioPlay.toggleAllowAudioPlayFlag(true) // Allow other audios to play.
        }
      })
    }
    holder.number.setText(model.getCallerNumber())
    holder.mainLayout.setOnClickListener(View.OnClickListener {
      com.nowfloats.util.Methods.makeCall(
        mContext,
        model.getCallerNumber()
      )
    })

    //player listeners

    //player listeners
    holder.mediaPlayer.setOnCompletionListener(OnCompletionListener {
      holder.handler.removeCallbacks(holder.updateSeekBar)
      holder.mediaPlayer.reset()
      holder.playPauseButton.setImageResource(R.drawable.ic_audio_play)
      holder.seekBar.setProgress(0)
      holder.audioStartTime.setText("0:00")
      holder.audioEndTime.setText(" / 0:00")
      holder.currentDuration = 0
      model.setAudioPlayState(false)
      mAllowAudioPlay.toggleAllowAudioPlayFlag(true) // Allow other audios to play.
    })

    holder.mediaPlayer.setOnPreparedListener(OnPreparedListener { mp: MediaPlayer ->
      holder.audioEndTime.setText(" / " + getTimeFromMilliSeconds(mp.duration))
      holder.seekBar.setMax(mp.duration)
      holder.start()
      holder.handler.postDelayed(holder.updateSeekBar, 1000)

      //set audio length
      model.setAudioLength(mp.duration)

      //set audio play state
      model.setAudioPlayState(true)
    })

    holder.updateSeekBar = Runnable {
      if (holder.mediaPlayer == null) {
        return@Runnable
      }
      holder.currentDuration = holder.mediaPlayer.getCurrentPosition()
      //int seekBarPos = seekBarPos(duration);
      val time: String = getTimeFromMilliSeconds(holder.currentDuration)
      Log.v("ggg", holder.currentDuration.toString() + " " + time)
      holder.seekBar.setProgress(holder.currentDuration)
      holder.audioStartTime.setText(time)
      if (holder.currentDuration == holder.mediaPlayer.getDuration() || !holder.isPlaying()) {
        return@Runnable
      }
      holder.handler.postDelayed(holder.updateSeekBar, 1000)
    }
  }


}
