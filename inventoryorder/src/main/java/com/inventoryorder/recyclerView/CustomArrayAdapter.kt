package com.inventoryorder.recyclerView

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.inventoryorder.R
import com.inventoryorder.model.spaAppointment.ServiceItem
import java.lang.Exception
import java.lang.reflect.Executable


class CustomArrayAdapter(context: Context, resource: Int, val list: ArrayList<ServiceItem>) :
        ArrayAdapter<ServiceItem>(context, resource, list) {

    private var copyList : ArrayList<ServiceItem> = list

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var v = convertView
        if (v == null) {
            val vi = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            v = vi.inflate(R.layout.layout_service_item, null)
        }
        val serviceItem: ServiceItem = list[position]
        if (serviceItem != null) {
            val customerNameLabel = v!!.findViewById<View>(R.id.text_service) as TextView
            customerNameLabel?.text = Html.fromHtml("<b><color='#2a2a2a'>${serviceItem?.Name}</color></b> <color='#adadad'>(${serviceItem?.Currency}${serviceItem?.DiscountAmount} for ${serviceItem?.Duration}min)</color>")
        }
        return v!!
    }

    override fun getFilter(): Filter {
        return nameFilter
    }

    private var nameFilter: Filter = object : Filter() {
        override fun convertResultToString(resultValue: Any): String {
            var item = (resultValue as ServiceItem)
            var res = Html.fromHtml("<b><color='#2a2a2a'>${item?.Name}</color></b> <color='#adadad'>(${item?.Currency}${item?.DiscountAmount} for ${item?.Duration}min)</color>")
            return res.toString()
        }

        override fun performFiltering(searchString: CharSequence): FilterResults {

            var filterResult = FilterResults()
            if (searchString != null && searchString.isNotEmpty()) {
                var suggestions = ArrayList<ServiceItem>()
                for (item in list) {
                    if (item.Name?.toLowerCase()?.startsWith(searchString.toString().toLowerCase()) == true) {
                        suggestions?.add(item)
                    }
                }

                filterResult.values = suggestions
                filterResult.count = suggestions?.size!!
            } else {
                filterResult.values = copyList
                filterResult.count = copyList?.size!!
            }

            return filterResult
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults) {
            try {
                clear()
                if (constraint != null && constraint.isNotEmpty() && results != null && results.count > 0) {
                    addAll((results.values as ArrayList<ServiceItem?>))
                    notifyDataSetChanged()
                } else {
                    addAll(copyList)
                    notifyDataSetChanged()
                }
            } catch (e : Exception) {}
        }
    }
}