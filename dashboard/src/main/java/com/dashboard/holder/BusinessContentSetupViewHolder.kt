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
      binding.lottySyncOk.visible()
      startCheckAnimation(true)
      list = (data.businessData?.filter { it.status == true } ?: ArrayList()) as ArrayList<SiteMeterModel>?
    } else {
      MATCH_PARENT.setHeight()
      getColor(R.color.light_grey_3)?.let { binding.txtDes.setTextColor(it) }
      binding.viewImage.visible()
      binding.lottySyncOk.gone()
      startCheckAnimation(false)
      binding.progressRemaining.setProgressWithAnimation((100 - (data.percentage ?: 0)).toFloat(), 1000)
      data.icon2?.let { binding.imgIcon.setImageResource(it) }
      list = ArrayList(data.businessData?.sortedBy { it.status == true } ?: ArrayList())
    }
    if (adapterSiteMeter == null) {
      binding.rvBusinessItemState.apply {
        adapterSiteMeter = activity?.let { AppBaseRecyclerViewAdapter(it, list!!, this@BusinessContentSetupViewHolder) }
        adapter = adapterSiteMeter
      }
    } else adapterSiteMeter?.notify(list)


  }

  private fun startCheckAnimation(isAnimate: Boolean) {
    binding.lottySyncOk.apply { if (isAnimate) playAnimation() else pauseAnimation() }
  }

  private fun Int.setHeight() {
    val layoutParams = binding.cardView.layoutParams
    layoutParams.width = MATCH_PARENT
    layoutParams.height = this
    binding.cardView.layoutParams = layoutParams
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    listener?.onItemClick(position, item, actionType)
  }
}
