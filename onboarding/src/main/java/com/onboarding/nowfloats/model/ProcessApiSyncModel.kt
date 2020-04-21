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

  fun getDataStart(channels: ArrayList<ChannelModel>?): ArrayList<ProcessApiSyncModel> {
    val businessProfile = "•     Registering business name...\n•     Adding business location...\n•     Adding Contact number...\n•     Adding Business Email address..."
    val list = ArrayList<ProcessApiSyncModel>()
    val selectedItems = channels?.map { it.recyclerViewType = RecyclerViewItemType.API_PROCESS_CHANNEL_ITEM.getLayout(); it }
    list.add(ProcessApiSyncModel("Creating your business profile...", null, businessProfile))
    list.add(ProcessApiSyncModel("Connecting business to " + getChannelTxt(channels?.size), selectedItems, null))
    list.add(ProcessApiSyncModel("Activating your free forever plan...", null, "•     Activating plan features..."))
    return list
  }

  fun getDataSuccess(channels: ArrayList<ChannelModel>?): ArrayList<ProcessApiSyncModel> {
    val businessProfile = "•     Business name Registered\n•     Business location added\n•     Contact number added\n•     Business Email address Added"
    val list = ArrayList<ProcessApiSyncModel>()
    val selectedItems = channels?.map {
      it.recyclerViewType = RecyclerViewItemType.API_PROCESS_CHANNEL_ITEM.getLayout()
      it.status = SyncStatus.SUCCESS.name
      it
    }
    list.add(ProcessApiSyncModel("Business profile Created", null, businessProfile, status = SyncStatus.SUCCESS.name))
    list.add(ProcessApiSyncModel("Business connected to " + getChannelTxt(channels?.size), selectedItems, null, status = SyncStatus.SUCCESS.name))
    list.add(ProcessApiSyncModel("Your free forever plan activated", null, "•     Plan features activated", status = SyncStatus.SUCCESS.name))
    return list
  }

  fun getDataErrorFP(channels: ArrayList<ChannelModel>?): ArrayList<ProcessApiSyncModel> {
    val businessProfile = "•     Registering business name...\n•     Adding business location...\n•     Adding Contact number...\n•     Adding Business Email address..."
    val list = ArrayList<ProcessApiSyncModel>()
    val selectedItems = channels?.map {
      it.recyclerViewType = RecyclerViewItemType.API_PROCESS_CHANNEL_ITEM.getLayout()
      it.status = SyncStatus.ERROR.name
      it
    }
    list.add(ProcessApiSyncModel("Error in creating Business profile", null, businessProfile, status = SyncStatus.ERROR.name))
    list.add(ProcessApiSyncModel("Error in connecting to " + getChannelTxt(channels?.size), selectedItems, null, status = SyncStatus.ERROR.name))
    list.add(ProcessApiSyncModel("Error in activating your plan", null, "•     Activating plan features...", status = SyncStatus.ERROR.name))
    return list
  }

  fun getDataErrorChannels(channels: ArrayList<ChannelModel>?): ArrayList<ProcessApiSyncModel> {
    val businessProfile = "•     Business name Registered\n•     Business location added\n•     Contact number added\n•     Business Email address Added"
    val list = ArrayList<ProcessApiSyncModel>()
    val selectedItems = channels?.map { it.recyclerViewType = RecyclerViewItemType.API_PROCESS_CHANNEL_ITEM.getLayout();it }
    val count = channels?.filter { it.status == SyncStatus.ERROR.name }?.size
    list.add(ProcessApiSyncModel("Business profile Created", null, businessProfile, status = SyncStatus.SUCCESS.name))
    list.add(ProcessApiSyncModel("Error in connecting to " + getChannelTxt(count), selectedItems, null, status = SyncStatus.ERROR.name))
    list.add(ProcessApiSyncModel("Error in activating your plan", null, "•     Activating plan features...", status = SyncStatus.ERROR.name))
    return list
  }

  private fun getChannelTxt(size: Int?): String {
    return takeIf { size == 1 }?.let { "$size channel..." } ?: "$size channels..."
  }

  enum class SyncStatus {
    PROCESSING, SUCCESS, ERROR
  }

}