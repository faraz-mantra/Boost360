package com.inventoryorder.ui.createappointment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.databinding.FragmentNewBookingOneBinding
import com.inventoryorder.model.AppointmentScheduleModel
import com.inventoryorder.model.bottomsheet.ChoosePurposeModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.startFragmentActivity

class NewBookingFragmentOne : BaseInventoryFragment<FragmentNewBookingOneBinding>(){

    private var choosePurposeBottomSheetDialog : ChoosePurposeBottomSheetDialog? = null
    private var choosePurposeList = ChoosePurposeModel().getData()
    private var adapter : AppBaseRecyclerViewAdapter<AppointmentScheduleModel>? = null
//    private var list : ArrayList<AppointmentScheduleModel>? = null

    companion object{
        fun newInstance(bundle: Bundle?= null) : NewBookingFragmentOne {
            val fragment  = NewBookingFragmentOne()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView() {
        super.onCreateView()

        setOnClickListener( binding?.choosePurpose, binding?.appointmentType, binding?.selectDuration, binding?.selectTimeSlot, binding?.buttonProceed)

        setAdapterForAppointmentSchedule(AppointmentScheduleModel().getData())
    }

    override fun onClick(v: View) {
        super.onClick(v)

        when(v){

            binding?.buttonProceed -> {
            startFragmentActivity(FragmentType.CREATE_NEW_BOOKING_PAGE_2, Bundle())
            baseActivity.finish()
            }

            binding?.choosePurpose ->{
                showBottomSheetDialogForChoosePurpose()
            }


        }

    }

    private fun setAdapterForAppointmentSchedule(appointmentScheduleModel : ArrayList<AppointmentScheduleModel>){
        binding?.recyclerViewAppointmentSchedule?.post {
        adapter = AppBaseRecyclerViewAdapter(baseActivity,appointmentScheduleModel)
        binding?.recyclerViewAppointmentSchedule?.layoutManager =  LinearLayoutManager(baseActivity,LinearLayoutManager.HORIZONTAL,false)
        binding?.recyclerViewAppointmentSchedule?.adapter = adapter
        }

    }

    private fun showBottomSheetDialogForChoosePurpose() {
        choosePurposeBottomSheetDialog = ChoosePurposeBottomSheetDialog()
        choosePurposeBottomSheetDialog?.onDoneClicked = { clickChoosePurposeItem(it) }
        choosePurposeBottomSheetDialog?.setList(choosePurposeList)
        choosePurposeBottomSheetDialog?.show(this.parentFragmentManager, ChoosePurposeBottomSheetDialog::class.java.name)
    }

    private fun clickChoosePurposeItem(list: ChoosePurposeModel?) {
        choosePurposeList.forEach { it.isSelected = (it.choosePurposeSelectedName == list?.choosePurposeSelectedName) }
    }


}