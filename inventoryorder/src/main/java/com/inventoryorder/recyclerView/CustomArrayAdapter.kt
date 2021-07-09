package com.inventoryorder.recyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.framework.utils.fromHtml
import com.inventoryorder.R
import com.inventoryorder.model.spaAppointment.ServiceItem
import java.util.*
import kotlin.collections.ArrayList

class CustomArrayAdapter(context: Context, resource: Int, var list: ArrayList<ServiceItem>) : ArrayAdapter<ServiceItem>(context, resource, list) {

  private var copyList: ArrayList<ServiceItem> = list
  private var filteredList: ArrayList<ServiceItem>? = null

  fun initList() {
    copyList = ArrayList()
    for (item in list) {
      copyList.add(item); }
  }

  override fun getCount(): Int {
    return list.size
  }

  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    var v = convertView
    if (v == null) {
      val vi = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
      v = vi.inflate(R.layout.layout_service_item, null)
    }
    val serviceItem: ServiceItem = list[position]
    val customerNameLabel = v?.findViewById<View>(R.id.text_service) as? TextView
    customerNameLabel?.text = fromHtml("<b><color='#2a2a2a'>${serviceItem.Name}</color></b> <color='#adadad'>(${serviceItem.Currency}${serviceItem.DiscountedPrice} for ${serviceItem.Duration}min)</color>")
    return v!!
  }

  override fun getFilter(): Filter {
    return nameFilter
  }

  private var nameFilter: Filter = object : Filter() {
    override fun convertResultToString(resultValue: Any): String {
      val item = (resultValue as ServiceItem)
      val res = fromHtml("<b><color='#2a2a2a'>${item.Name}</color></b> <color='#adadad'>(${item.Currency}${item.DiscountedPrice} for ${item.Duration}min)</color>")
      return res.toString()
    }

    override fun performFiltering(searchString: CharSequence): FilterResults {
      val filterResult = FilterResults()
      filteredList = ArrayList()
      if (searchString.isNotEmpty()) {
        for (item in copyList) {
          if (item.Name?.toLowerCase(Locale.ROOT)?.contains(searchString.toString().toLowerCase(Locale.ROOT)) == true) {
            filteredList?.add(item)
          }
        }
      } else {
        filteredList?.addAll(copyList)
      }

      filterResult.values = filteredList
      filterResult.count = filteredList?.size!!
      return filterResult

    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
      clear()
      if (results.values != null) addAll(results.values as ArrayList<ServiceItem>)
      else addAll(copyList)
      notifyDataSetChanged()
    }
  }
}