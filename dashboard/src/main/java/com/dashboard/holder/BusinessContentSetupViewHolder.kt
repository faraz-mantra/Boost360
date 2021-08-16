package com.dashboard.holder

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import com.dashboard.R
import com.dashboard.databinding.ItemBusinessContentSetupBinding
import com.dashboard.model.live.drScore.DrScoreItem
import com.dashboard.model.live.drScore.DrScoreSetupData
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.recyclerView.AppBaseRecyclerViewHolder
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
import com.framework.extensions.gone
import com.framework.extensions.visible

class BusinessContentSetupViewHolder(binding: ItemBusinessContentSetupBinding) :
  AppBaseRecyclerViewHolder<ItemBusinessContentSetupBinding>(binding), RecyclerItemClickListener {

  private var adapterSiteMeter: AppBaseRecyclerViewAdapter<DrScoreItem>? = null
  var list: ArrayList<DrScoreItem>? = null

  override fun bind(position: Int, item: BaseRecyclerViewItem) {
    super.bind(position, item)
    val data = item as? DrScoreSetupData ?: return
    binding.txtTitle.text = data.type?.title
    binding.txtDes.text = data.getRemainingPercentage()
    if (data.percentage == 100) {
//      WRAP_CONTENT.setHeight()
      getColor(R.color.light_green_2)?.let { binding.txtDes.setTextColor(it) }
      binding.viewImage.gone()
      binding.lottySyncOk.visible()
      startCheckAnimation(true)
    } else {
//      MATCH_PARENT.setHeight()
      getColor(R.color.red_light_1)?.let { binding.txtDes.setTextColor(it) }
      binding.viewImage.visible()
      binding.lottySyncOk.gone()
      startCheckAnimation(false)
      binding.progressRemaining.setProgressWithAnimation((data.percentage ?: 0).toFloat(), 1000)
      data.type?.icon?.let { binding.imgIcon.setImageResource(it) }
    }
    list = ArrayList(data.drScoreItem?.sortedBy { it.isUpdate } ?: ArrayList())

    if (adapterSiteMeter == null) {
      binding.rvBusinessItemState.apply {
        adapterSiteMeter = activity?.let {
          AppBaseRecyclerViewAdapter(
            it,
            list!!,
            this@BusinessContentSetupViewHolder
          )
        }
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
