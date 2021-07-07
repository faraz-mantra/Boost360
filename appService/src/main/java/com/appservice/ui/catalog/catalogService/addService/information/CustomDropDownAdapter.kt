package com.appservice.ui.catalog.catalogService.addService.information

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.appservice.R
import com.framework.views.customViews.CustomImageView
import com.framework.views.customViews.CustomTextView

class CustomDropDownAdapter(val context: Context, var dataSource: List<SpinnerImageModel>) :
  BaseAdapter() {

  private val inflater: LayoutInflater =
    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

  override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

    val view: View
    val vh: ItemHolder
    if (convertView == null) {
      view = inflater.inflate(R.layout.custom_spinner_item, parent, false)
      vh = ItemHolder(view)
      view?.tag = vh
    } else {
      view = convertView
      vh = view.tag as ItemHolder
    }
    vh.label.text = dataSource[position].state.first
    vh.img.setBackgroundResource(dataSource[position].resId)

    return view
  }

  override fun getItem(position: Int): Any {
    return dataSource[position];
  }

  override fun getCount(): Int {
    return dataSource.size;
  }

  override fun getItemId(position: Int): Long {
    return position.toLong();
  }

  private class ItemHolder(row: View?) {
    val label: CustomTextView = row?.findViewById(R.id.ctv_option) as CustomTextView
    val img: CustomImageView = row?.findViewById(R.id.civ_dot) as CustomImageView

  }

}