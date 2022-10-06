package com.boost.marketplace.holder

import android.text.Html
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.MarketPlaceOffers
import com.boost.dbcenterapi.recycleritem.AppBaseRecyclerViewHolder
import com.boost.dbcenterapi.recycleritem.BaseRecyclerViewItem
import com.boost.marketplace.databinding.ItemMarketplaceoffersInfoBinding

class MarketPlaceOfferTermsViewHolder(binding: ItemMarketplaceoffersInfoBinding) :
    AppBaseRecyclerViewHolder<ItemMarketplaceoffersInfoBinding>(binding) {
    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)

        var list = ArrayList<String>()

        binding.offerDetails.setText(Html.fromHtml(list.get(position)))
    }

}