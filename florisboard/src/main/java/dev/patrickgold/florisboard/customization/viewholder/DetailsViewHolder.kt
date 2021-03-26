package dev.patrickgold.florisboard.customization.viewholder

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.customization.adapter.OnItemClickListener
import dev.patrickgold.florisboard.customization.model.response.CustomerDetails

class DetailsViewHolder(itemView: View, private val listener: OnItemClickListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    init {
        val btnShare: Button = itemView.findViewById(R.id.btn_share)
        btnShare.setOnClickListener(this)
    }

    val contactName: TextView = itemView.findViewById(R.id.tv_name)
    val businessName: TextView = itemView.findViewById(R.id.tv_business_name)
    val website: TextView = itemView.findViewById(R.id.tv_website)

    fun bindTo(customerDetails: CustomerDetails) {
        // bind views with data
        contactName.text = customerDetails.contactName
        businessName.text = customerDetails.name
        website.text = customerDetails.rootAliasUri
    }

    override fun onClick(v: View?) {
        val pos = adapterPosition
        if (pos != RecyclerView.NO_POSITION) {
            listener.onItemClick(pos)
        }
    }
}