package com.appservice.ui.paymentgateway

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.appservice.R
import com.appservice.utils.getBitmap
import java.io.File

internal class AdditionalDocsGridviewAdapter internal constructor(context: Context,
                                                                  private val resource: Int,
                                                                  private val itemList: ArrayList<File>?)
    : ArrayAdapter<AdditionalDocsGridviewAdapter.ItemHolder>(context, resource){

    override fun getCount(): Int {
        return if (this.itemList != null) this.itemList.size else 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView

        val holder: ItemHolder

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(resource, null)
            holder = ItemHolder()
            holder.image = convertView!!.findViewById(R.id.image)
            convertView.tag = holder
        }else {
            holder = convertView.tag as ItemHolder
        }

        holder.image!!.setImageBitmap(this.itemList!![position].getBitmap())
        return convertView
    }

    internal class ItemHolder {
        var image: ImageView? = null
        var crossIcon: CardView? = null
    }

}

