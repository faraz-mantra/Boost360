package com.onboarding.nowfloats.holders.apiprocess

import com.onboarding.nowfloats.model.ProcessApiSyncModel
import com.onboarding.nowfloats.model.channel.ChannelModel
import com.onboarding.nowfloats.model.channel.getDrawable
import com.onboarding.nowfloats.model.channel.getName
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.databinding.ItemChildApiCallingBinding
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewHolder
import com.onboarding.nowfloats.recyclerView.BaseRecyclerViewItem

class ApiProcessChannelRecyclerViewHolder(binding: ItemChildApiCallingBinding) : AppBaseRecyclerViewHolder<ItemChildApiCallingBinding>(binding) {

    private var model: ChannelModel? = null

    override fun bind(position: Int, item: BaseRecyclerViewItem) {
        super.bind(position, item)
        model = item as? ChannelModel
        setViews(model)
    }

    private fun setViews(model: ChannelModel?) {
        binding.imageCard.image.setImageDrawable(model?.getDrawable(activity?.applicationContext))
        if (model?.status == ProcessApiSyncModel.SyncStatus.PROCESSING.name) {
            binding.title.text = model.getName()
            binding.imageCard.image.makeGreyscale()
            getColor(R.color.greyish_brown_40)?.let { binding.title.setTextColor(it) }
        } else if (model?.status == ProcessApiSyncModel.SyncStatus.ERROR.name) {
            binding.title.text = model.getName().replace("""[$,.]""".toRegex(), "")
            getColor(R.color.scarlet_40)?.let { binding.title.setTextColor(it) }
        } else {
            binding.title.text = model?.getName()?.replace("""[$,.]""".toRegex(), "")
            getColor(R.color.greyish_brown_40)?.let { binding.title.setTextColor(it) }
        }
    }
}