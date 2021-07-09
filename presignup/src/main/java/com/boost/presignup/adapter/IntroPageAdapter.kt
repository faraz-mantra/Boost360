package com.boost.presignup.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.RecyclerView
import com.boost.presignup.R
import com.boost.presignup.datamodel.SingleScreenModel
import kotlinx.android.synthetic.main.item_page.view.*
import java.util.*

class IntroPageAdapter(val list: ArrayList<SingleScreenModel>) : RecyclerView.Adapter<PagerVH>() {

//    //array of colors to change the background color of screen
//    private val imageslist = intArrayOf(
//        R.drawable.fourthscreen,
//        R.drawable.fifthscreen,
//        R.drawable.sixthscreen,
//        R.drawable.seventhscreen,
//        R.drawable.eighthscreen
//    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerVH =
        PagerVH(LayoutInflater.from(parent.context).inflate(R.layout.item_page, parent, false))

    //get the size of color array
    override fun getItemCount(): Int = list.size

    //binding the screen with view
    override fun onBindViewHolder(holder: PagerVH, position: Int) = holder.itemView.run {
        image_title.setText(list.get(position).title)
        image_description.setText(list.get(position).description)
        page_images.setImageResource(list.get(position).image!!)
    }
}

class PagerVH(itemView: View) : RecyclerView.ViewHolder(itemView)