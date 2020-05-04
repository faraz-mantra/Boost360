package com.inventoryorder.ui.order

import androidx.recyclerview.widget.LinearLayoutManager
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.inventoryorder.R
import com.inventoryorder.databinding.BottomSheetPickUpDeliveryOptionBinding
import com.inventoryorder.model.bottomsheet.DeliveryOptionsModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import com.inventoryorder.recyclerView.RecyclerItemClickListener

class DeliveryBottomSheetDialog : BaseBottomSheetDialog<BottomSheetPickUpDeliveryOptionBinding, BaseViewModel>(), RecyclerItemClickListener {

  private var list = ArrayList<DeliveryOptionsModel>()
  private var adapter: AppBaseRecyclerViewAdapter<DeliveryOptionsModel>? = null
  var onDoneClicked: (deliveryItem: DeliveryOptionsModel?) -> Unit = { }

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_pick_up_delivery_option
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  fun setList(list: ArrayList<DeliveryOptionsModel>) {
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
    val deliveryItem = item as? DeliveryOptionsModel
    list.forEach { it.isSelected = (it.deliveryOptionSelectedName == deliveryItem?.deliveryOptionSelectedName) }
    adapter?.notifyDataSetChanged()
    onDoneClicked((item as? DeliveryOptionsModel))
    dismiss()
  }
}