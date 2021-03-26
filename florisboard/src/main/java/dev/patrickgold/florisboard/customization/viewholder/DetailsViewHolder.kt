package dev.patrickgold.florisboard.customization.viewholder

import android.view.View
import android.widget.Button
import android.widget.TextView
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerItem
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerViewHolder
import dev.patrickgold.florisboard.customization.adapter.OnItemClickListener
import dev.patrickgold.florisboard.customization.model.response.CustomerDetails

class DetailsViewHolder(itemView: View, val listener: OnItemClickListener?) : BaseRecyclerViewHolder(itemView) {

    private val contactName: TextView = itemView.findViewById(R.id.tv_name)
    private val businessName: TextView = itemView.findViewById(R.id.tv_business_name)
    private val website: TextView = itemView.findViewById(R.id.tv_website)
    private val btnShare: Button = itemView.findViewById(R.id.btn_share)

    override fun bindTo(position: Int, item: BaseRecyclerItem?) {
        val customerDetails = item as CustomerDetails
        // bind views with data
        contactName.text = customerDetails.contactName
        businessName.text = customerDetails.name
        website.text = customerDetails.rootAliasUri

        btnShare.setOnClickListener { listener?.onItemClick(position, customerDetails) }
    }
}