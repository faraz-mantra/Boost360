package com.boost.marketplace.holder

import android.text.Html
import com.boost.dbcenterapi.data.api_model.GetAllFeatures.response.MarketPlaceOffers
import com.boost.dbcenterapi.recycleritem.AppBaseRecyclerViewHolder
import com.boost.marketplace.databinding.ItemMarketplaceoffersInfoBinding

class MarketPlaceOfferDetailsViewHolder(binding: ItemMarketplaceoffersInfoBinding) :
    AppBaseRecyclerViewHolder<ItemMarketplaceoffersInfoBinding>(binding) {

    override fun bind(position: Int, item: com.boost.dbcenterapi.recycleritem.BaseRecyclerViewItem) {
        super.bind(position, item)
         var list = ArrayList<String>()
        val data = item as? MarketPlaceOffers ?: return
       binding.offerDetails.setText(Html.fromHtml(list.get(position)))


        }
    }




