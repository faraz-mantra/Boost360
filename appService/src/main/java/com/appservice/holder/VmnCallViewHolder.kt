package com.appservice.holder

import android.view.View
import com.appservice.R
import com.appservice.databinding.SingleItemVmnCallItemV2Binding
import com.appservice.model.VmnCallModel
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.framework.utils.DateUtils.getDate
import java.util.*

class VmnCallViewHolder(binding: SingleItemVmnCallItemV2Binding) : AppBaseRecyclerViewHolder<SingleItemVmnCallItemV2Binding>(binding) {

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val model =item as VmnCallModel
    val totaltime = getTimeFromMilliSeconds(model.callDuration?:0)

    binding.seekBar.setProgress(model.audioPosition)

    if (model.audioPosition == 0 && model
        .audioPosition == 0 && !model.isAudioPlayState
    ) {
      binding.seekBar.setProgress(0)
      binding.tvRecTime.setText("0:00")
      binding.tvEndTime.setText(" / 0:00")

      val currentDuration = 0
      binding.tvPlay.setImageResource(R.drawable.ic_audio_play)
    } else {
      binding.seekBar.setProgress(model.audioPosition)
      binding.seekBar.setMax(model.audioLength)
      binding.tvRecTime.setText(getTimeFromMilliSeconds(model.audioPosition))
      binding.tvEndTime.setText(
        " / " + getTimeFromMilliSeconds(
          model.audioLength
        )
      )
      val currentDuration = model.audioPosition
      if (model.isAudioPlayState) {
        binding.tvPlay.setImageResource(R.drawable.ic_pause_gray)
      } else {
        binding.tvPlay.setImageResource(R.drawable.ic_audio_play)
      }
    }


/*    binding.tvDate.setText(getDate(model.callDateTime))
    binding.tvTime.setText(getDate(model.callDateTime))*/
    binding.divider.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    if (model.callStatus.equals("MISSED", ignoreCase = true)) {
      binding.tvCallType.setText("Missed Call")

      //hide player and line
      binding.playerLayout.setVisibility(View.GONE)
      binding.divider.setVisibility(View.GONE)
    } else {
      binding.playerLayout.setVisibility(View.VISIBLE)
      binding.divider.setVisibility(View.VISIBLE)
      binding.tvCallType.setText("Connected call")
/*
      binding.tvPlay.setOnClickListener(View.OnClickListener { v: View? ->
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
*/
    }
    binding.tvNumber.setText(model.callerNumber)
    binding.llayoutNumber.setOnClickListener(View.OnClickListener {
      /*com.nowfloats.util.Methods.makeCall(
        mContext,
        model.getCallerNumber()
      )*/
    })

    //player listeners


  }

  private fun getTimeFromMilliSeconds(pos: Int): String? {
    val seconds = pos / 1000 % 60
    val minutes = pos / (1000 * 60) % 60
    return String.format(Locale.ENGLISH, "%d:%02d", minutes, seconds)
  }



}
