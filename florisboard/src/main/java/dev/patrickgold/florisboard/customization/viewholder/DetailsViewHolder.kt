package dev.patrickgold.florisboard.customization.viewholder

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.customization.model.response.CustomerDetails

class DetailsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val contactName: TextView = itemView.findViewById(R.id.tv_name)
    val businessName: TextView = itemView.findViewById(R.id.tv_business_name)
    val website: TextView = itemView.findViewById(R.id.tv_website)
    val btnShare: Button = itemView.findViewById(R.id.btn_share)

    fun bindTo(customerDetails: CustomerDetails) {
        // bind views with data
        contactName.text = customerDetails.contactName
        businessName.text = customerDetails.name
        website.text = customerDetails.rootAliasUri
    }
}