package com.appservice.ui.catalog.widgets

import android.view.View
import com.appservice.R
import com.appservice.databinding.BottomSheetServiceDeliveryBinding
import com.appservice.model.pickUpAddress.PickUpData
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ServiceDeliveryBottomSheet : BaseBottomSheetDialog<BottomSheetServiceDeliveryBinding, BaseViewModel>() {

  private var pickUpDataAddress: ArrayList<PickUpData>? = null
  private var pickUpData: PickUpData? = null
  var onClicked: (pickUpData: PickUpData?) -> Unit = { }
  private var selectPosition: Int = -1

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_service_delivery
  }

  fun setList(pickUpDataAddress: ArrayList<PickUpData>?, addressId: String?) {
    this.pickUpDataAddress = pickUpDataAddress
    if (addressId.isNullOrEmpty().not()) pickUpData = this.pickUpDataAddress?.firstOrNull { addressId == it.id }
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreateView() {
    setOnClickListener(binding?.vwMyBusinessLocation)
    setOnClickListener(binding?.btnDone, binding?.btnCancel, binding?.changeAddress)
    if (pickUpData != null) {
      binding?.txtAddress?.text = pickUpData?.streetAddress
      binding?.rbMyBusinessLocation?.isChecked = true
    } else if (pickUpData == null && this.pickUpDataAddress.isNullOrEmpty().not()) {
      pickUpData = this.pickUpDataAddress!![0]
      binding?.txtAddress?.text = pickUpData?.streetAddress
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.vwMyBusinessLocation -> {
        if (this.pickUpDataAddress.isNullOrEmpty().not()) {
          binding?.rbMyBusinessLocation?.isChecked = true
        } else showLongToast("Pick up address not available")
      }
      binding?.changeAddress -> openDialogPicker()
      binding?.btnDone -> {
        onClicked(pickUpData)
        dismiss()
      }
      binding?.btnCancel -> dismiss()
    }
  }

  private fun openDialogPicker() {
    if (this.pickUpDataAddress.isNullOrEmpty().not()) {
      val singleItems = this.pickUpDataAddress?.map { it.streetAddress }?.toTypedArray()
      MaterialAlertDialogBuilder(baseActivity).setTitle("Select one").setPositiveButton("Ok") { d, _ ->
        if (selectPosition == -1) return@setPositiveButton
        pickUpData = this.pickUpDataAddress?.firstOrNull { it.streetAddress == singleItems?.get(selectPosition) }
        binding?.txtAddress?.text = pickUpData?.streetAddress ?: ""
        binding?.rbMyBusinessLocation?.isChecked = true
        d.dismiss()
      }.setNeutralButton("Cancel") { d, _ ->
        d.dismiss()
      }.setSingleChoiceItems(singleItems, selectPosition) { _, pos ->
        selectPosition = pos
      }.show()
    } else showLongToast("Pick up address not available")
  }

}