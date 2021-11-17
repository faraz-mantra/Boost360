package com.appservice.holder

import com.appservice.databinding.ItemSimilarDomainSuggestionsBinding
import com.appservice.recyclerView.AppBaseRecyclerViewHolder
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.ui.domainbooking.model.SimilarDomainSuggestionModel

class SimilarDomainSuggestionViewHolder(binding: ItemSimilarDomainSuggestionsBinding) :
    AppBaseRecyclerViewHolder<ItemSimilarDomainSuggestionsBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)
        val model = item as SimilarDomainSuggestionModel
        binding.tvDomainName.text = model.domainName
        binding.radioSimilarDomain.isChecked = model.isDomainSelected
    }
}
