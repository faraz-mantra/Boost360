package com.festive.poster.recyclerView.viewholders

import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.ItemSocialPlatformPromoAdapBinding
import com.festive.poster.models.promoModele.SocialPlatformModel
import com.festive.poster.recyclerView.AppBaseRecyclerViewHolder
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.framework.extensions.gone
import com.framework.extensions.invisible
import com.framework.extensions.visible
import com.framework.utils.fromHtml

class SocialPlatformOptionListViewHolder(binding: ItemSocialPlatformPromoAdapBinding) :
    AppBaseRecyclerViewHolder<ItemSocialPlatformPromoAdapBinding>(binding) {

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        val model = item as SocialPlatformModel
        binding.ivSocialIcon.setImageDrawable(model.icon)
        binding.tvSocialTitle.text = model.socialTitle ?: ""
        binding.checkboxSocialSelected.isChecked = model.isConnected ?: false
        binding.checkboxSocialSelected.isEnabled = model.isEnabled == true

        if (model.isConnected == true) {
            binding.ivLock.invisible()
            binding.checkboxSocialSelected.visible()
            binding.tvSocialSubtitleStatus.text = model.socialSubTitleData ?: ""
        } else {
            binding.ivLock.visible()
            binding.checkboxSocialSelected.invisible()
            binding.tvSocialSubtitleStatus.text =
                fromHtml("<font color=#E39595>Not connected</font>")
        }
        binding.checkboxSocialSelected.setOnCheckedChangeListener { compoundButton, b ->
            model.isEnabled=b
            listener?.onItemClick(position,model,RecyclerViewActionType.SOCIAL_CHANNEL_CHECK_CLICKED.ordinal)
        }

        super.bind(position, item)
    }

}
