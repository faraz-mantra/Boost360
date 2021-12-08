package com.festive.poster.recyclerView.viewholders

import com.festive.poster.databinding.ItemSocialPlatformPromoAdapBinding
import com.festive.poster.models.promoModele.SocialPlatformModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.utils.fromHtml

class SocialPlatformOptionListViewHolder(binding: ItemSocialPlatformPromoAdapBinding) :
    AppBaseRecyclerViewHolder<ItemSocialPlatformPromoAdapBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as SocialPlatformModel
        binding.ivSocialIcon.setImageResource(model.socialImageResource ?: 0)
        binding.tvSocialTitle.text = model.socialTitle ?: ""
        binding.checkboxSocialSelected.isChecked = model.isSelected ?: false
        binding.checkboxSocialSelected.isEnabled = model.isConnected == true

        if (model.isConnected == true) {
            binding.ivLock.gone()
            binding.tvSocialSubtitleStatus.text = model.socialSubTitleData ?: ""
        } else {
            binding.ivLock.visible()
            binding.tvSocialSubtitleStatus.text =
                fromHtml("<font color=#E39595>Not connected</font>")
        }

        super.bind(position, item)
    }

}
