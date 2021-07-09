package com.boost.upgrades.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.boost.upgrades.R
import com.boost.upgrades.data.model.YoutubeVideoModel
import com.boost.upgrades.interfaces.HomeListener
import com.bumptech.glide.Glide
import java.util.*

class VideosListAdapter(videoList: List<YoutubeVideoModel>, listen: HomeListener) : RecyclerView.Adapter<VideosListAdapter.ViewHolder>() {

    private lateinit var context: Context
    private var list = ArrayList<YoutubeVideoModel>()
    private lateinit var listener: HomeListener

    init {
        list = videoList as ArrayList<YoutubeVideoModel>
        listener = listen
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent?.context).inflate(
                R.layout.videos_item, parent, false
        )
        val lp = ViewGroup.LayoutParams(
                350,
                350
        )
        context = itemView.context

        itemView.layoutParams = lp
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

        if(list.get(position).youtube_link != null && list.get(position).youtube_link!!.isNotEmpty()) {
            val link: List<String> = list.get(position).youtube_link!!.split('/')
            Glide.with(context).load("http://img.youtube.com/vi/"+link.get(link.size-1)+"/default.jpg").into(holder.image)
        }
        holder.title.setText(list.get(position).title)
        holder.videoTiming.setText(list.get(position).duration)

        holder.itemView.setOnClickListener {
            listener.onPlayYouTubeVideo(list.get(position))
        }

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<ImageView>(R.id.primary_image)
        val title = itemView.findViewById<TextView>(R.id.video_title)
        val videoTiming = itemView.findViewById<TextView>(R.id.video_timing_text)
    }

}