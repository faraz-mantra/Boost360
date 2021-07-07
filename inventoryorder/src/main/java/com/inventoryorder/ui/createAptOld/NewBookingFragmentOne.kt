package com.inventoryorder.ui.createAptOld

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.framework.views.customViews.CustomTextView
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.databinding.FragmentNewBookingOneBinding
import com.inventoryorder.model.AppointmentScheduleModel
import com.inventoryorder.model.bottomsheet.AppointMentTypeModel
import com.inventoryorder.model.bottomsheet.ChoosePurposeModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import com.inventoryorder.recyclerView.RecyclerItemClickListener
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.startFragmentOrderActivity
import java.text.SimpleDateFormat
import java.util.*

class NewBookingFragmentOne : BaseInventoryFragment<FragmentNewBookingOneBinding>(),
  RecyclerItemClickListener {

  private var choosePurposeList = ChoosePurposeModel().getData()
  private var selectAppointmentList = AppointMentTypeModel().getData()
  private var choosePurposeBottomSheetDialog: ChoosePurposeBottomSheetDialog? = null
  private var selectAppointmentTypeBottomSheetDialog: AppointmentTypeBottomSheetDialog? = null
  private var appointMeantScheduleList = AppointmentScheduleModel().getData()
  private var adapterAppointmentSchedule: AppBaseRecyclerViewAdapter<AppointmentScheduleModel>? =
    null
  var onDoneClicked: (appointmentScheduleModel: AppointmentScheduleModel?) -> Unit = {}

  companion object {
    fun newInstance(bundle: Bundle? = null): NewBookingFragmentOne {
      val fragment = NewBookingFragmentOne()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    setOnClickListener(
      binding?.choosePurpose,
      binding?.appointmentType,
      binding?.buttonProceed,
      binding?.selectTimeSlot,
      binding?.selectDuration
    )
    setAdapterForAppointmentSchedule()
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.buttonProceed -> startFragmentOrderActivity(
        FragmentType.CREATE_NEW_BOOKING_PAGE_2,
        Bundle()
      )
      binding?.choosePurpose -> showBottomSheetDialogForChoosePurpose()
      binding?.appointmentType -> showBottomSheetDialogForAppointmentType()
      binding?.selectTimeSlot -> setTime(binding?.selectTimeSlotText, baseActivity)
      binding?.selectDuration -> setTime(binding?.selectDurationText, baseActivity)
    }

  }

  private fun setAdapterForAppointmentSchedule() {
    binding?.recyclerViewAppointmentSchedule?.post {
      adapterAppointmentSchedule =
        AppBaseRecyclerViewAdapter(baseActivity, appointMeantScheduleList, this)
      binding?.recyclerViewAppointmentSchedule?.layoutManager =
        LinearLayoutManager(baseActivity, LinearLayoutManager.HORIZONTAL, false)
      binding?.recyclerViewAppointmentSchedule?.adapter = adapterAppointmentSchedule
    }
  }

  private fun showBottomSheetDialogForChoosePurpose() {
    choosePurposeBottomSheetDialog = ChoosePurposeBottomSheetDialog()
    choosePurposeBottomSheetDialog?.onDoneClicked = { clickChoosePurposeItem(it) }
    choosePurposeBottomSheetDialog?.setList(choosePurposeList)
    choosePurposeBottomSheetDialog?.show(
      this.parentFragmentManager,
      ChoosePurposeBottomSheetDialog::class.java.name
    )
  }

  private fun showBottomSheetDialogForAppointmentType() {
    selectAppointmentTypeBottomSheetDialog = AppointmentTypeBottomSheetDialog()
    selectAppointmentTypeBottomSheetDialog?.onDoneClicked = { clickChooseAppointTypeItem(it) }
    selectAppointmentTypeBottomSheetDialog?.setList(selectAppointmentList)
    selectAppointmentTypeBottomSheetDialog?.show(
      this.parentFragmentManager,
      AppointmentTypeBottomSheetDialog::class.java.name
    )
  }

  private fun clickChooseAppointTypeItem(item: AppointMentTypeModel?) {
    selectAppointmentList.forEach {
      it.isSelected = it.appointmentTypeSelectedName == item?.appointmentTypeSelectedName
    }
  }

  private fun clickChoosePurposeItem(item: ChoosePurposeModel?) {
    choosePurposeList.forEach {
      it.isSelected = (it.choosePurposeSelectedName == item?.choosePurposeSelectedName)
    }
  }

  private fun setTime(timePickerText: CustomTextView?, context: Context) {
    val calender = Calendar.getInstance()
    val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
      calender.set(Calendar.HOUR_OF_DAY, hour)
      calender.set(Calendar.MINUTE, minute)
      timePickerText?.text = SimpleDateFormat("hh:mm aa").format(calender.time)
    }
    TimePickerDialog(
      baseActivity,
      timeSetListener,
      calender.get(Calendar.HOUR_OF_DAY),
      calender.get(Calendar.MINUTE),
      false
    ).show()
  }

  fun setList(list: ArrayList<AppointmentScheduleModel>) {
    this.appointMeantScheduleList.clear()
    this.appointMeantScheduleList.addAll(list)
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    val selectedItem = item as AppointmentScheduleModel
    appointMeantScheduleList.forEach {
      it.isSelected = (it.appointMeantSchedule == selectedItem.appointMeantSchedule)
    }
    onDoneClicked(selectedItem)
    adapterAppointmentSchedule?.notifyDataSetChanged()

  }
}
