package com.inventoryorder.ui.order

import androidx.recyclerview.widget.LinearLayoutManager
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.inventoryorder.R
import com.inventoryorder.databinding.BottomSheetPickUpDeliveryOptionBinding
import com.inventoryorder.model.bottomsheet.DeliveryModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import com.inventoryorder.recyclerView.RecyclerItemClickListener

class DeliveryBottomSheetDialog : BaseBottomSheetDialog<BottomSheetPickUpDeliveryOptionBinding, BaseViewModel>(), RecyclerItemClickListener {

  private var list = ArrayList<DeliveryModel>()
  private var adapter: AppBaseRecyclerViewAdapter<DeliveryModel>? = null
  var onDoneClicked: (deliveryItem: DeliveryModel?) -> Unit = { }

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_pick_up_delivery_option
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  fun setList(list: ArrayList<DeliveryModel>) {
    this.list.clear()
    this.list.addAll(list)
  }

  override fun onCreateView() {
    binding?.recycler?.post {
      adapter = AppBaseRecyclerViewAdapter(baseActivity, list, this)
      binding?.recycler?.layoutManager = LinearLayoutManager(baseActivity)
      binding?.recycler?.adapter = adapter
      binding?.recycler?.let { adapter?.runLayoutAnimation(it) }
    }
  }


  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    val deliveryItem = item as? DeliveryModel
    list.forEach { it.isSelected = (it.deliveryOptionSelectedName == deliveryItem?.deliveryOptionSelectedName) }
    adapter?.notifyDataSetChanged()
    onDoneClicked((item as? DeliveryModel))
    dismiss()
  }
}