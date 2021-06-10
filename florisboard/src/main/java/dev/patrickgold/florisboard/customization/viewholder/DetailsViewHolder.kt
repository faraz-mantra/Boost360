package dev.patrickgold.florisboard.customization.viewholder

import android.widget.Button
import android.widget.TextView
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerItem
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerViewHolder
import dev.patrickgold.florisboard.customization.adapter.OnItemClickListener
import dev.patrickgold.florisboard.customization.model.response.CustomerDetails
import dev.patrickgold.florisboard.databinding.AdapterItemDetailsBinding

class DetailsViewHolder(binding: AdapterItemDetailsBinding, val listener: OnItemClickListener?) : BaseRecyclerViewHolder<AdapterItemDetailsBinding>(binding) {

    private val contactName: TextView = binding.root.findViewById(R.id.tv_name)
    private val businessName: TextView = binding.root.findViewById(R.id.tv_business_name)
    private val website: TextView = binding.root.findViewById(R.id.tv_website)
    private val btnShare: Button = binding.root.findViewById(R.id.btn_share)

    override fun bindTo(position: Int, item: BaseRecyclerItem?) {
        val customerDetails = item as CustomerDetails
        // bind views with data
        contactName.text = customerDetails.contactName
        businessName.text = customerDetails.name
        website.text = customerDetails.rootAliasUri
        btnShare.setOnClickListener { listener?.onItemClick(position, customerDetails) }
    }
}