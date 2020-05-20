package com.inventoryorder.ui.createappointment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.framework.views.customViews.CustomTextView
import com.inventoryorder.R
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.databinding.FragmentNewBookingTwoBinding
import com.inventoryorder.model.bottomsheet.GenderSelectionModel
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.startFragmentActivity
import kotlinx.android.synthetic.main.bottom_sheet_select_gender.*
import kotlinx.android.synthetic.main.item_bottom_sheet_select_gender.*

class NewBookingFragmentTwo : BaseInventoryFragment<FragmentNewBookingTwoBinding>() {

  private var selectGenderBottomSheetDialog: SelectGenderBottomSheetDialog? = null
  private var selectGenderList = GenderSelectionModel().getData()
  private var recyclerViewSetGender : RecyclerView? = null

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

//    recyclerViewSetGender = recyclerViewBottomSheetSelectGender.findViewById(R.id.recyclerViewBottomSheetSelectGender)
//    for(genderPosition in 0 until  selectGenderList.size){
//      binding?.tvSetGender =
//    }

//    binding?.tvSetGender =

  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.tvBack -> baseActivity.onBackPressed()
      binding?.buttonCreateBooking -> startFragmentActivity(FragmentType.BOOKING_SUCCESSFUL, Bundle())
      binding?.buttonPayAtClinic -> setBacUI(binding?.buttonPayAtClinic, binding?.buttonPayOnline, R.drawable.payment_bg_right)
      binding?.buttonPayOnline -> setBacUI(binding?.buttonPayOnline, binding?.buttonPayAtClinic, R.drawable.payment_bg_left)
      binding?.llSelectGender -> showBottomSheetDialogSelectGender()
    }

  }

  private fun setBacUI(btn1: CustomTextView?, btn2: CustomTextView?, paymentBgType: Int) {
    btn1?.background = ContextCompat.getDrawable(baseActivity, R.color.colorAccent)
    btn2?.background = ContextCompat.getDrawable(baseActivity, paymentBgType)
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
//    selectGenderList.forEach { it.isSelected = (it.genderType == list?.genderType) }
    selectGenderList.forEach { it.isSelected = (it.genderType == list?.genderType) }
//    for(genderPosition in 0 until  selectGenderList.size){
////      binding?.tvSetGender?.text =
////              genderPosition.toString()
////      showShortToast(genderPosition)
////              Toast.makeText(baseActivity,"pos",Toast.LENGTH_SHORT).show()
//      Toast.makeText(activity, ""+genderPosition, Toast.LENGTH_SHORT).show()

//    }

  }
}