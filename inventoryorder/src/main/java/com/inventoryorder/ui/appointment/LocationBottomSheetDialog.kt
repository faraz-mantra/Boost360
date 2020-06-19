package com.inventoryorder.ui.appointment

import androidx.recyclerview.widget.LinearLayoutManager
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.inventoryorder.R
import com.inventoryorder.databinding.BottomSheetServiceLocationBinding
import com.inventoryorder.model.bottomsheet.LocationsModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.recyclerView.RecyclerItemClickListener

class LocationBottomSheetDialog : BaseBottomSheetDialog<BottomSheetServiceLocationBinding, BaseViewModel>(), RecyclerItemClickListener {

  private var list = ArrayList<LocationsModel>()
  private var adapter: AppBaseRecyclerViewAdapter<LocationsModel>? = null
  var onDoneClicked: (location: LocationsModel?) -> Unit = { }


    override fun getLayout(): Int {
        return R.layout.bottom_sheet_service_location
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }


  fun setList(list: ArrayList<LocationsModel>) {
        this.list.clear()
        this.list.addAll(list)
    }

    override fun onCreateView() {
        binding?.recyclerViewBottomSheetServiceLocation?.post {
            adapter = AppBaseRecyclerViewAdapter(baseActivity, list, this)
            binding?.recyclerViewBottomSheetServiceLocation?.layoutManager = LinearLayoutManager(baseActivity)
            binding?.recyclerViewBottomSheetServiceLocation?.adapter = adapter
            binding?.recyclerViewBottomSheetServiceLocation?.let { adapter?.runLayoutAnimation(it) }
        }
    }

    override fun onItemClick(position: Int, item: com.inventoryorder.recyclerView.BaseRecyclerViewItem?, actionType: Int) {
      val deliveryItem = item as? LocationsModel
        list.forEach { it.isSelected = (it.serviceOptionSelectedName == deliveryItem?.serviceOptionSelectedName) }
        adapter?.notifyDataSetChanged()
      onDoneClicked((item as? @kotlin.ParameterName(name = "serviceLocation") LocationsModel))
        dismiss()
    }


}