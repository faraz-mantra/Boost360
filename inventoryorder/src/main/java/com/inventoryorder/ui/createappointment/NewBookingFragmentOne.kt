package com.inventoryorder.ui.createappointment

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.framework.views.customViews.CustomTextView
import com.inventoryorder.R
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.databinding.FragmentNewBookingOneBinding
import com.inventoryorder.model.AppointmentScheduleModel
import com.inventoryorder.model.bottomsheet.AppointMentTypeModel
import com.inventoryorder.model.bottomsheet.ChoosePurposeModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.startFragmentActivity
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NewBookingFragmentOne : BaseInventoryFragment<FragmentNewBookingOneBinding>(){

    private var choosePurposeBottomSheetDialog : ChoosePurposeBottomSheetDialog? = null
    private var choosePurposeList = ChoosePurposeModel().getData()

    private var selectAppointmentTypeBottomSheetDialog : AppointmentTypeBottomSheetDialog? = null
    private var selectAppointMentList = AppointMentTypeModel().getData()

    private var adapter : AppBaseRecyclerViewAdapter<AppointmentScheduleModel>? = null

    companion object{
        fun newInstance(bundle: Bundle?= null) : NewBookingFragmentOne {
            val fragment  = NewBookingFragmentOne()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView() {
        super.onCreateView()

        setOnClickListener( binding?.choosePurpose, binding?.appointmentType, binding?.selectDuration, binding?.selectTimeSlot, binding?.buttonProceed,
                binding?.selectTimeSlot, binding?.selectDuration, binding?.selectTimeSlotSpinner, binding?.selectDurationSpinner)

        setAdapterForAppointmentSchedule(AppointmentScheduleModel().getData())
    }

    override fun onClick(v: View) {
        super.onClick(v)

        when(v){
            binding?.buttonProceed -> {
            startFragmentActivity(FragmentType.CREATE_NEW_BOOKING_PAGE_2, Bundle())
//            baseActivity.finish()
            }
            binding?.choosePurpose ->{
                showBottomSheetDialogForChoosePurpose()
            }
            binding?.appointmentType ->{
                showBottomSheetDialogForAppointmentType()
            }
            binding?.selectTimeSlot ->{
                setTime(binding!!.selectTimeSlotText,baseActivity)
            }
//            binding?.selectTimeSlotSpinner ->{
//                setTime(binding!!.selectTimeSlotText,baseActivity)
//            }
            binding?.selectDuration ->{
                setTime(binding!!.selectDurationText,baseActivity)
            }
//            binding?.selectDurationSpinner ->{
//                setTime(binding!!.selectDurationText,baseActivity)
//            }


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
//        choosePurposeBottomSheetDialog?.onDoneClicked = { clickChoosePurposeItem(it) }
        choosePurposeBottomSheetDialog?.setList(choosePurposeList)
        choosePurposeBottomSheetDialog?.show(this.parentFragmentManager, ChoosePurposeBottomSheetDialog::class.java.name)
    }

    private fun showBottomSheetDialogForAppointmentType(){
        selectAppointmentTypeBottomSheetDialog = AppointmentTypeBottomSheetDialog()
//        selectAppointmentTypeBottomSheetDialog?.onDoneClicked = { clickChooseAppointTypeItem(it)}
        selectAppointmentTypeBottomSheetDialog?.setList(selectAppointMentList)
        selectAppointmentTypeBottomSheetDialog?.show(this.parentFragmentManager,AppointmentTypeBottomSheetDialog::class.java.name)
    }

    private fun clickChooseAppointTypeItem(list : AppointMentTypeModel){
        selectAppointMentList.forEach { it.isSelected = it.appointmentTypeSelectedName == list.appointmentTypeSelectedName }
    }

    private fun clickChoosePurposeItem(list: ChoosePurposeModel) {
        choosePurposeList.forEach { it.isSelected = (it.choosePurposeSelectedName == list?.choosePurposeSelectedName) }
    }

    private fun setTime(timePickerText: CustomTextView, context: Context){

        val calender = Calendar.getInstance()

        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            calender.set(Calendar.HOUR_OF_DAY, hour)
            calender.set(Calendar.MINUTE, minute)

            timePickerText.text = SimpleDateFormat("HH:mm aa").format(calender.time)
        }

        timePickerText.setOnClickListener {
//            TimePickerDialog(context ,timeSetListener, calender.get(Calendar.HOUR_OF_DAY), calender.get(Calendar.MINUTE), false).show()
            TimePickerDialog(context, R.style.TimePickerDialogTheme ,timeSetListener, calender.get(Calendar.HOUR_OF_DAY), calender.get(Calendar.MINUTE), false).show()
        }
    }

}