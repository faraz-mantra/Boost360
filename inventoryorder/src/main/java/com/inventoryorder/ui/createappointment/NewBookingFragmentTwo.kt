package com.inventoryorder.ui.createappointment

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.framework.views.customViews.CustomButton
import com.inventoryorder.R
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.databinding.FragmentNewBookingTwoBinding
import com.inventoryorder.model.bottomsheet.GenderSelectionModel
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.startFragmentActivity

class NewBookingFragmentTwo : BaseInventoryFragment<FragmentNewBookingTwoBinding>() {


  private var selectGenderBottomSheetDialog: SelectGenderBottomSheetDialog? = null
  private var selectGenderList = GenderSelectionModel().getData()

  companion object {
    fun newInstance(bundle: Bundle? = null): NewBookingFragmentTwo {
      val fragment = NewBookingFragmentTwo()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView() {
    super.onCreateView()

    setOnClickListener(binding?.buttonCreateBooking, binding?.tvBack, binding?.buttonPayAtClinic, binding?.buttonPayOnline, binding?.llSelectGender)
  }

  @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
  override fun onClick(v: View) {
    super.onClick(v)

    when (v) {

      binding?.tvBack -> {
        baseActivity.onBackPressed()
      }

      binding?.buttonCreateBooking -> {
        startFragmentActivity(FragmentType.BOOKING_SUCCESSFUL, Bundle())
        baseActivity.finish()
      }
      binding?.buttonPayAtClinic -> setBacUI(binding?.buttonPayAtClinic, binding?.buttonPayOnline)
      binding?.buttonPayOnline -> setBacUI(binding?.buttonPayOnline, binding?.buttonPayAtClinic)
      binding?.llSelectGender -> {
        showBottomSheetDialogSelectGender()
      }

    }

  }

  private fun setBacUI(btn1: CustomButton?, btn2: CustomButton?) {
    btn1?.background = ContextCompat.getDrawable(baseActivity, R.color.colorAccent)
    btn2?.background = ContextCompat.getDrawable(baseActivity, R.drawable.payment_mode_button_bg)
    btn1?.setTextColor(ContextCompat.getColor(baseActivity, R.color.warm_grey_10))
    btn2?.setTextColor(ContextCompat.getColor(baseActivity, R.color.primary_grey))
  }

  private fun showBottomSheetDialogSelectGender() {
    selectGenderBottomSheetDialog = SelectGenderBottomSheetDialog()
    selectGenderBottomSheetDialog?.onDoneClicked = { selectGenderFromList(it) }
    selectGenderBottomSheetDialog?.setList(selectGenderList)
    selectGenderBottomSheetDialog?.show(this.parentFragmentManager, SelectGenderBottomSheetDialog::class.java.name)
  }

  private fun selectGenderFromList(list: GenderSelectionModel?) {
    selectGenderList.forEach { it.isSelected = (it.selectedGender == list?.selectedGender) }
  }


}