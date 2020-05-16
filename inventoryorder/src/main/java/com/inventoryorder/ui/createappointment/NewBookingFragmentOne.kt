package com.inventoryorder.ui.createappointment

import android.os.Bundle
import android.view.View
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.databinding.FragmentNewBookingOneBinding
import com.inventoryorder.model.bottomsheet.ChoosePurposeModel
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.startFragmentActivity

class NewBookingFragmentOne : BaseInventoryFragment<FragmentNewBookingOneBinding>(){

    private var choosePurposeBottomSheetDialog : ChoosePurposeBottomSheetDialog? = null
    private var choosePurposeList = ChoosePurposeModel().getData()

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

    private fun setAdapterForAppointmentSchedule(choosePurposeModel: ChoosePurposeModel){


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