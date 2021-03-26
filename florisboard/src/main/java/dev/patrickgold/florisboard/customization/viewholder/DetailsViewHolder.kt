package dev.patrickgold.florisboard.customization.viewholder

import android.view.View
import android.widget.TextView
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerItem
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerViewHolder
import dev.patrickgold.florisboard.customization.model.response.CustomerDetails

class DetailsViewHolder(itemView: View) : BaseRecyclerViewHolder(itemView) {

    val contactName: TextView = itemView.findViewById(R.id.tv_name)
    val businessName: TextView = itemView.findViewById(R.id.tv_business_name)
    val website: TextView = itemView.findViewById(R.id.tv_website)

    override fun bindTo(item: BaseRecyclerItem?) {
        val customerDetails = item as CustomerDetails
        // bind views with data
        contactName.text = customerDetails.contactName
        businessName.text = customerDetails.name
        website.text = customerDetails.rootAliasUri
    }
}