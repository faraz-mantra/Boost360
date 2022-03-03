package com.appservice.holder

import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.SeekBar
import androidx.core.view.isVisible
import com.appservice.R
import com.appservice.databinding.SingleItemVmnCallItemV2Binding
import com.appservice.model.VmnCallModel
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.framework.BaseApplication
import com.framework.utils.DateUtils.getDate
import com.framework.utils.ExoPlayerUtils
import com.framework.utils.ExoPlayerUtils.player
import com.framework.utils.makeCall
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import java.util.*


class VmnCallViewHolder(binding: SingleItemVmnCallItemV2Binding) : AppBaseRecyclerViewHolder<SingleItemVmnCallItemV2Binding>(binding) {


  private var audioProgressHandler= Handler(Looper.getMainLooper())
  private var audioProgressRunnable: Runnable?=null

  fun startTracking() {

    audioProgressRunnable = Runnable {
      val model = getCurrentPlayerModel()
      model?.audioPosition = player.currentPosition
      model?.audioLength = player.duration
      adapter?.notifyItemChanged(currentPlayerIndex(),Unit)
      audioProgressHandler.postDelayed(audioProgressRunnable!!, 1000)

    }
    audioProgressHandler.postDelayed(audioProgressRunnable!!, 100)

  }

  fun currentPlayerIndex(): Int {
   return player.currentMediaItem?.mediaId?.toInt()?:0
  }
  fun getCurrentPlayerModel(): VmnCallModel? {
    val index = player.currentMediaItem?.mediaId?.toInt()
    if (index!=null) {
      return (list?.get(index) as VmnCallModel)
    }else
      return null
  }


  fun stopTracking(){
    audioProgressRunnable?.let { audioProgressHandler.removeCallbacks(it) }
  }

  init {

      ExoPlayerUtils.addListener {isPlaying->
        val model = getCurrentPlayerModel()
        model?.isAudioPlayState = isPlaying
        if (isPlaying){
          startTracking()
        }else{
          stopTracking()
        }
        val playbackState = player.playbackState
        when {
          playbackState == Player.STATE_ENDED ->{
            model?.audioPosition=0L
          }
        }
        adapter?.notifyItemChanged(currentPlayerIndex(),Unit)
      }

    binding.seekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
      override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        if (p2){
          val model = getCurrentPlayerModel()

          if (model?.audioLength?:0>p1){
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

      if ((player.isLoading|| player.isPlaying)&&layoutPosition!= player.currentMediaItem?.mediaId?.toInt()){
        showLongToast("You can only play one audio clip at a time.")
      }else{
        if (player.isPlaying){
          player.pause()
        }else{
          val listItem = list?.get(layoutPosition) as VmnCallModel

          if (layoutPosition!= player.currentMediaItem?.mediaId?.toInt()||getCurrentPlayerModel()?.audioPosition==0L){
            listItem.callRecordingUri?.let { it1 -> play(it1,layoutPosition) }
          }else{
            player.play()
          }
        }
      }


    }
  }

  fun play(url:String,position: Int){
    val mediaItem = MediaItem.Builder().setMediaId(position.toString()).setUri(url)
      .build()
    player.setMediaItem(mediaItem)
    player.prepare()
    adapter?.notifyItemChanged(position,Unit)

    player.play()

  }

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val model =item as VmnCallModel

    binding.seekBar.setProgress(model.audioPosition.toInt())

    if (model.audioPosition == 0L && model
        .audioPosition == 0L && !model.isAudioPlayState
    ) {
      binding.seekBar.setProgress(0)
      binding.tvRecTime.setText("0:00")
      binding.tvEndTime.setText(" / 0:00")

      binding.tvPlay.setImageResource(R.drawable.ic_audio_play)
    } else {
      binding.seekBar.setProgress(model.audioPosition.toInt())
      binding.seekBar.setMax(model.audioLength.toInt())
      binding.tvRecTime.setText(getTimeFromMilliSeconds(model.audioPosition))
      binding.tvEndTime.setText(
        " / " + getTimeFromMilliSeconds(
          model.audioLength
        )
      )
      if (model.isAudioPlayState) {
        binding.tvPlay.setImageResource(R.drawable.ic_pause_gray)
      } else {
        binding.tvPlay.setImageResource(R.drawable.ic_audio_play)
      }
    }


    val longDate = model.callDateTime?.
    replace("/Date(", "")?.
    replace(")/", "")?.toLong()?:0
    val date =
      getDate(longDate
        ,"dd-MM-yyyy")
    val time =
      getDate(longDate
        ,"hh:mm:ss a")
    binding.tvDate.text = date
    binding.tvTime.text = time
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

    }
    binding.tvNumber.setText(model.callerNumber)
    binding.llayoutNumber.setOnClickListener(View.OnClickListener {

      makeCall(model.callerNumber)
    })



  }

  private fun getTimeFromMilliSeconds(pos: Long): String? {
    val seconds = pos / 1000 % 60
    val minutes = pos / (1000 * 60) % 60
    return String.format(Locale.ENGLISH, "%d:%02d", minutes, seconds)
  }


}
