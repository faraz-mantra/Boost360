package com.inventoryorder.ui.createAptOld

import androidx.recyclerview.widget.LinearLayoutManager
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.inventoryorder.R
import com.inventoryorder.databinding.BottomSheetAppointmentTypeBinding
import com.inventoryorder.model.bottomsheet.AppointMentTypeModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import com.inventoryorder.recyclerView.RecyclerItemClickListener

class AppointmentTypeBottomSheetDialog : BaseBottomSheetDialog<BottomSheetAppointmentTypeBinding, BaseViewModel>(), RecyclerItemClickListener {

    private var list = ArrayList<AppointMentTypeModel>()
    private var adapter : AppBaseRecyclerViewAdapter<AppointMentTypeModel>? = null
    var onDoneClicked : ( appointmentTypeModel : AppointMentTypeModel?) -> Unit = {}

    override fun getLayout(): Int {
       return R.layout.bottom_sheet_appointment_type
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
      return BaseViewModel::class.java
    }

    fun setList(list : ArrayList<AppointMentTypeModel>){
      this.list.clear()
      this.list.addAll(list)
    }

    override fun onCreateView() {
        binding?.recyclerViewBottomSheetAppointmentType?.post {
            adapter = AppBaseRecyclerViewAdapter(baseActivity,list,this)
            binding?.recyclerViewBottomSheetAppointmentType?.layoutManager = LinearLayoutManager(baseActivity)
            binding?.recyclerViewBottomSheetAppointmentType?.adapter = adapter
            binding?.recyclerViewBottomSheetAppointmentType?.let {adapter?.runLayoutAnimation(it)  }
        }
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
      val selectAppointmentTypeModel = item as AppointMentTypeModel
      list.forEach { it.isSelected = (it.appointmentTypeSelectedName == selectAppointmentTypeModel.appointmentTypeSelectedName) }
      adapter?.notifyDataSetChanged()
      onDoneClicked(item)
        dismiss()
    }


}