package com.onboarding.nowfloats.holders.apiprocess

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.onboarding.nowfloats.model.ProcessApiSyncModel
import com.onboarding.nowfloats.model.channel.ChannelModel
import com.onboarding.nowfloats.R
import com.onboarding.nowfloats.databinding.ItemApiCallingProcessBinding
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewAdapter
import com.onboarding.nowfloats.recyclerView.AppBaseRecyclerViewHolder
import com.onboarding.nowfloats.recyclerView.BaseRecyclerViewItem
import com.onboarding.nowfloats.recyclerView.RecyclerItemClickListener

class ApiProcessRecyclerViewHolder(binding: ItemApiCallingProcessBinding) :
  AppBaseRecyclerViewHolder<ItemApiCallingProcessBinding>(binding), RecyclerItemClickListener {

  private var model: ProcessApiSyncModel? = null
  private var channelAdapter: AppBaseRecyclerViewAdapter<ChannelModel>? = null


  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    model = item as? ProcessApiSyncModel
    setViews()
  }

  private fun setViews() {
    when (model?.status) {
      ProcessApiSyncModel.SyncStatus.PROCESSING.name -> {
        binding.title.text = model?.title
        getColor(R.color.dodger_blue_two)?.let { binding.title.setTextColor(it) }
        binding.okImage.visibility = View.GONE
      }
      ProcessApiSyncModel.SyncStatus.SUCCESS.name -> {
        binding.title.text = model?.title?.replace("""[$,.]""".toRegex(), "")
        getColor(R.color.dodger_blue_two)?.let { binding.title.setTextColor(it) }
        binding.okImage.visibility = View.VISIBLE
        binding.progress.visibility = View.GONE
        binding.okImage.setImageResource(R.drawable.ic_valid)
      }
      else -> {
        binding.title.text = model?.title?.replace("""[$,.]""".toRegex(), "")
        getColor(R.color.warm_grey)?.let { binding.title.setTextColor(it) }
        binding.okImage.visibility = View.VISIBLE
        binding.progress.visibility = View.GONE
        binding.okImage.setImageResource(R.drawable.ic_error)
      }
    }
    if (model?.channels == null) {
      binding.itemDesc.visibility = View.VISIBLE
      binding.channelRecycler.visibility = View.GONE
      if (model?.status == ProcessApiSyncModel.SyncStatus.SUCCESS.name) {
        binding.itemDesc.text = model?.profileFreePlanItems?.replace("""[$,.]""".toRegex(), "")
      } else binding.itemDesc.text = model?.profileFreePlanItems
    } else {
      binding.itemDesc.visibility = View.GONE
      binding.channelRecycler.visibility = View.VISIBLE
      setChannelAdapter(model?.channels as ArrayList<ChannelModel>?)
    }
  }

  private fun setChannelAdapter(list: ArrayList<ChannelModel>?) {
    list?.let { data ->
      activity?.let {
        channelAdapter = AppBaseRecyclerViewAdapter(it, data, this)
        binding.channelRecycler.layoutManager = LinearLayoutManager(it)
        binding.channelRecycler.adapter = channelAdapter
        if (model?.status == ProcessApiSyncModel.SyncStatus.PROCESSING.name) channelAdapter?.runLayoutAnimation(
          binding.channelRecycler
        )
      }
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    TODO("Not yet implemented")
  }
}