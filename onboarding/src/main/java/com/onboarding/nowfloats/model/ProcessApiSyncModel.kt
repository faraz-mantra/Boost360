package com.onboarding.nowfloats.model

import com.onboarding.nowfloats.constant.RecyclerViewItemType
import com.onboarding.nowfloats.model.channel.ChannelModel
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewItem

class ProcessApiSyncModel(
    val title: String? = null,
    val channels: List<ChannelModel>? = null,
    val profileFreePlanItems: String? = null,
    var status: String? = SyncStatus.PROCESSING.name
) : AppBaseRecyclerViewItem {


    override fun getViewType(): Int {
        return RecyclerViewItemType.API_PROCESS_BUSINESS_ITEM.getLayout()
    }

    fun getData(channels: ArrayList<ChannelModel>?): ArrayList<ProcessApiSyncModel> {
        val businessProfile = "•  Registering business name...\n•  Adding business location...\n•  Adding Contact number...\n•  Adding Business Email address..."
        val list = ArrayList<ProcessApiSyncModel>()
        val selectedItems = channels?.map { it.recyclerViewType = RecyclerViewItemType.API_PROCESS_CHANNEL_ITEM.getLayout(); it }
        list.add(ProcessApiSyncModel("Creating your business profile...", null, businessProfile))
        list.add(ProcessApiSyncModel("Syncing business on " + getChannelTxt(channels?.size), selectedItems, null))
        list.add(ProcessApiSyncModel("Activating your free forever plan...", null, "•  Activating plan features..."))
        return list
    }

    private fun getChannelTxt(size: Int?): String {
        return takeIf { size == 1 }?.let { "$size channel..." } ?: "$size channels..."
    }

    enum class SyncStatus {
        PROCESSING, SUCCESS, ERROR
    }

}