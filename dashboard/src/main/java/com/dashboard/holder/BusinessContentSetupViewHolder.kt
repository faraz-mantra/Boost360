package com.dashboard.holder

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import com.dashboard.R
import com.dashboard.databinding.ItemBusinessContentSetupBinding
import com.dashboard.model.BusinessContentSetupData
import com.dashboard.model.BusinessSetupData
import com.dashboard.model.live.SiteMeterModel
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
import com.framework.extensions.gone
import com.framework.extensions.visible


class BusinessContentSetupViewHolder(binding: ItemBusinessContentSetupBinding) : AppBaseRecyclerViewHolder<ItemBusinessContentSetupBinding>(binding), RecyclerItemClickListener {

  private var adapterSiteMeter: AppBaseRecyclerViewAdapter<SiteMeterModel>? = null
  var list: ArrayList<SiteMeterModel>? = null

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? BusinessContentSetupData ?: return
    binding.txtTitle.text = data.title
    binding.txtDes.text = data.subTitle
    if (data.type == BusinessSetupData.ActiveViewType.ONLINE_SYNC.name) {
      WRAP_CONTENT.setHeight()
      getColor(R.color.light_green_2)?.let { binding.txtDes.setTextColor(it) }
      binding.viewImage.gone()
      binding.gifSyncOk.visible()
      binding.gifSyncOk.apply {
        data.gifIcon?.let { gifResource = it }
        play()
      }
      list = data.businessData ?: ArrayList()
    } else {
      MATCH_PARENT.setHeight()
      getColor(R.color.light_grey_3)?.let { binding.txtDes.setTextColor(it) }
      binding.viewImage.visible()
      binding.gifSyncOk.gone()
      data.icon1?.let { binding.imgCircle.setImageResource(it) }
      data.icon2?.let { binding.imgIcon.setImageResource(it) }
      list = ArrayList(data.businessData?.sortedBy { it.status == false } ?: ArrayList())
    }
    if (adapterSiteMeter == null) {
      binding.rvBusinessItemState.apply {
        adapterSiteMeter = activity?.let { AppBaseRecyclerViewAdapter(it, list!!, this@BusinessContentSetupViewHolder) }
        adapter = adapterSiteMeter
      }
    } else adapterSiteMeter?.notify(list)
  }

  private fun Int.setHeight() {
    val layoutParams = binding.cardView.layoutParams
    layoutParams.width = MATCH_PARENT
    layoutParams.height = this
    binding.cardView.layoutParams = layoutParams
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
  }
}
