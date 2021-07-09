package com.boost.presignup.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import com.boost.presignup.R

class LanguageGridAdapter internal constructor(context: Context, private val resource: Int, private val itemList: Array<String>?) : ArrayAdapter<LanguageGridAdapter.ItemHolder>(context, resource) {

    override fun getCount(): Int {
        return if (this.itemList != null) this.itemList.size else 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView

        val holder: ItemHolder
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, null)
            holder = ItemHolder()
            holder.button = convertView.findViewById(R.id.lang_select_button)
            convertView.tag = holder
        } else {
            holder = convertView.tag as ItemHolder
        }

        holder.button!!.text = this.itemList!![position]

        return convertView!!
    }

    class ItemHolder {
        var button: Button? = null
    }
}
