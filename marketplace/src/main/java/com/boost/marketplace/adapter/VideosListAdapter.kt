package com.boost.marketplace.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.dbcenterapi.upgradeDB.model.YoutubeVideoModel
import com.boost.marketplace.R
import com.boost.marketplace.interfaces.VideosListener
import com.bumptech.glide.Glide
import com.framework.utils.setNoDoubleClickListener

class VideosListAdapter(videoList: List<YoutubeVideoModel>, listen: VideosListener) :
  RecyclerView.Adapter<VideosListAdapter.ViewHolder>() {

  private lateinit var context: Context
  private var list = ArrayList<YoutubeVideoModel>()
  private lateinit var listener: VideosListener

  init {
    list = videoList as ArrayList<YoutubeVideoModel>
    listener = listen
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val itemView = LayoutInflater.from(parent?.context).inflate(
      R.layout.video_item, parent, false
    )
    context = itemView.context
    return ViewHolder(itemView)
  }

  override fun getItemCount(): Int {
    return list.size
  }

  fun addUpdates(videoList: List<YoutubeVideoModel>) {
    val initPosition = list.size
    list.clear()
    list.addAll(videoList)
    notifyItemRangeInserted(initPosition, list.size)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    if (list.get(position).youtube_link != null && list.get(position).youtube_link!!.isNotEmpty())
    {
      val link: String? = list.get(position).youtube_link
      Glide.with(context)
        .load(list.get(position).youtube_image!!)
        .into(holder.image)

    }
    holder.title.setText(list.get(position).title)
    //holder.videoType.setText(list.get(position).desc)

    holder.itemView.setNoDoubleClickListener ({
      listener.onPlayYouTubeVideo(list.get(position))
    }, 3000)

  }

  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val image = itemView.findViewById<ImageView>(R.id.primary_image)
    val title = itemView.findViewById<TextView>(R.id.video_title)
    //val videoType = itemView.findViewById<TextView>(R.id.video_type)
  }

}