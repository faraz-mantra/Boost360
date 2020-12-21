package com.boost.upgrades.ui.compare

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.boost.upgrades.R
import com.boost.upgrades.data.model.FeaturesModel
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection

class MySection // call constructor with layout resources for this Section header and items
//        super(R.layout.compare_package_item_header, R.layout.compare_package_item);
(var title: String?, var list: List<FeaturesModel>?) : StatelessSection(SectionParameters.builder()
        .itemResourceId(R.layout.compare_package_item)
        .headerResourceId(R.layout.compare_package_item_header)
        .build()) {

    var context: Context? = null
    override fun getContentItemsTotal(): Int {
        return list!!.size // number of items of this section
//        return 0 // number of items of this section
    }

    override fun getItemViewHolder(view: View): RecyclerView.ViewHolder {
        // return a custom instance of ViewHolder for the items of this section
        context = view.getContext();
        return ItemViewHolder(view)
    }

    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemHolder = holder as ItemViewHolder

        // bind your view here
        itemHolder.title.text = list!![position].name
//        itemHolder.title.text = list!!.name
//        Log.v("onBindItemViewHolder", " "+ list!![position].name )
        if(position %2 == 1)
        {
            holder.item_mainLayout!!.setBackgroundColor(Color.parseColor("#F8F8F8"))
        }
        else
        {
            holder.item_mainLayout!!.setBackgroundColor(Color.parseColor("#FFFFFF"))
        }
        //        itemHolder.description.setText(list.get(position).FromTime + " - "+ list.get(position).ToTime);
      /*  holder.infoIcon!!.setOnClickListener {
            Log.v("infoIcon", " "+ list!![position].description_title)
            SimpleTooltip.Builder(context)
                    .anchorView(holder.infoIcon)
                    .text(list!![position].description_title)
                    .gravity(Gravity.END)
                    .animated(true)
                    .transparentOverlay(false)
                    .build()
                    .show();
        }*/
    }

    override fun getHeaderViewHolder(view: View): RecyclerView.ViewHolder {
        return HeaderViewHolder(view)
    }

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder) {
        val headerHolder = holder as HeaderViewHolder
//Log.v("getHeaderViewHolder", " "+ title)
        // bind your header view here
        headerHolder.txt_needsreview.text = title
//        headerHolder.txt_needsreview.text = title.get(headerHolder)
    }

    private inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txt_needsreview: TextView
        var txt_planned: TextView? = null
        var txt_inprogress: TextView? = null
        var txt_completed: TextView? = null

        init {
            txt_needsreview = view.findViewById<View>(R.id.textHomeCareServices) as TextView
        }
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView
//        var description: TextView
        var upward_count: TextView? = null
        var comment_count: TextView? = null
        var status: TextView? = null
        var item_mainLayout: ConstraintLayout? =null
        var infoIcon: ImageView? =null

        init {
            title = itemView.findViewById<View>(R.id.textA) as TextView
//            infoIcon = itemView.findViewById<View>(R.id.info_icon) as ImageView
            item_mainLayout = itemView.findViewById<View>(R.id.item_mainLayout) as ConstraintLayout
        }
    }

}