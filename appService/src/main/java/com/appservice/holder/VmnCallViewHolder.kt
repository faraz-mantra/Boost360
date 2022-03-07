package com.appservice.holder

import android.R.color
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Build
import android.view.View
import android.widget.SeekBar
import com.appservice.R
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.SingleItemVmnCallItemV2Binding
import com.appservice.model.VmnCallModel
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.framework.BaseApplication
import com.framework.utils.DateUtils.getDate
import com.framework.utils.ExoPlayerUtils.player
import com.framework.utils.makeCall
import java.util.*


class VmnCallViewHolder(binding: SingleItemVmnCallItemV2Binding) : AppBaseRecyclerViewHolder<SingleItemVmnCallItemV2Binding>(binding) {


  init {
    binding.seekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
      override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        if (p2){
          if (player.currentPosition< player.duration){
            player.seekTo(p1.toLong())
          }

        }
      }
      override fun onStartTrackingTouch(p0: SeekBar?) {
      }

      override fun onStopTrackingTouch(p0: SeekBar?) {
      }

    })

    binding.tvPlay.setOnClickListener {
      listener?.onItemClick(layoutPosition, list?.get(layoutPosition),RecyclerViewActionType.VMN_PLAY_CLICKED.ordinal)
    }
  }



  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val model =item as VmnCallModel
    binding.seekBar.progress = model.audioPosition.toInt()

    if (model.audioPosition == 0L && model
        .audioPosition == 0L && !model.isAudioPlayState
    ) {
      binding.seekBar.progress = 0
      binding.tvRecTime.text = "0:00"
      binding.tvEndTime.text = " / 0:00"

      binding.tvPlay.setImageResource(R.drawable.ic_audio_play)
    } else {
      binding.seekBar.progress = model.audioPosition.toInt()
      binding.seekBar.max = model.audioLength.toInt()
      binding.tvRecTime.text = getTimeFromMilliSeconds(model.audioPosition)
      binding.tvEndTime.text = " / " + getTimeFromMilliSeconds(model.audioLength)
      if (model.isAudioPlayState) {
        binding.tvPlay.setImageResource(R.drawable.ic_pause_gray)
      } else {
        binding.tvPlay.setImageResource(R.drawable.ic_audio_play)
      }
    }


    val longDate = model.callDateTime?.replace("/Date(", "")?.replace(")/", "")?.toLong()?:0
    val date = getDate(longDate,"dd-MM-yyyy")
    val time = getDate(longDate,"hh:mm:ss a")
    binding.tvDate.text = date
    binding.tvTime.text = time


    binding.divider.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
    if (model.callStatus.equals("MISSED", ignoreCase = true)) {
      binding.tvCallType.text = BaseApplication.instance.getString(R.string.missed)

      //hide player and line
      binding.playerLayout.visibility = View.GONE
      binding.divider.visibility = View.GONE
      binding.ivCallIcon.setImageResource(R.drawable.ic_call_missed_app_service)

    } else {
      binding.playerLayout.visibility = View.VISIBLE
      binding.divider.visibility = View.VISIBLE
      binding.tvCallType.text = BaseApplication.instance.getString(R.string.connected)
      binding.ivCallIcon.setImageResource(R.drawable.ic_call_received_app_service)

    }
    binding.tvNumber.text = model.callerNumber
    binding.ivCall.setOnClickListener(View.OnClickListener {

      makeCall(model.callerNumber)
    })



  }

  private fun getTimeFromMilliSeconds(pos: Long): String? {
    val seconds = pos / 1000 % 60
    val minutes = pos / (1000 * 60) % 60
    return String.format(Locale.ENGLISH, "%d:%02d", minutes, seconds)
  }


}
