package com.appservice.holder

import android.text.method.LinkMovementMethod
import com.appservice.databinding.ItemDomainSuggestionsBinding
import com.appservice.databinding.ItemPdfFileBinding
import com.appservice.databinding.ListItemStepsDomainBinding
import com.appservice.model.FileModel
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.ui.domainbooking.model.DomainStepsModel
import com.appservice.ui.domainbooking.model.DomainSuggestionModel

class DomainSuggestionsViewHolder(binding: ItemDomainSuggestionsBinding) :
    AppBaseRecyclerViewHolder<ItemDomainSuggestionsBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)
        val model = item as DomainSuggestionModel
        binding.tvDomainName.text = model.domainName
    }
}
