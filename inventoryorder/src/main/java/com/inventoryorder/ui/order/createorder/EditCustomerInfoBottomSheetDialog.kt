package com.inventoryorder.ui.order.createorder

import android.view.View
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.inventoryorder.R
import com.inventoryorder.databinding.BottomSheetEditCustomerInfoBinding
import com.inventoryorder.model.orderRequest.ContactDetails
import com.inventoryorder.model.ordersdetails.OrderItem

class EditCustomerInfoBottomSheetDialog(private val contactDetails: ContactDetails) :
  BaseBottomSheetDialog<BottomSheetEditCustomerInfoBinding, BaseViewModel>() {

  private var cancellingEntity: String? = OrderItem.CancellingEntity.BUYER.name
  private var orderItem: OrderItem? = null
  var onClicked: (contactDetails: ContactDetails) -> Unit = { contactDetails: ContactDetails -> }

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_edit_customer_info
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  fun setData(orderItem: OrderItem) {
    this.orderItem = orderItem
  }

  override fun onCreateView() {
    setOnClickListener(binding?.buttonDone, binding?.tvCancel)
    binding?.editCustomerName?.setText(contactDetails.fullName)
    binding?.editCustomerPhone?.setText(contactDetails.primaryContactNumber)
    binding?.editCustomerEmail?.setText(contactDetails.emailId)
  }

  override fun onClick(v: View) {
    super.onClick(v)
    dismiss()
    when (v) {
      binding?.buttonDone -> {

        var name = binding?.editCustomerName?.text?.toString()
        var email = binding?.editCustomerEmail?.text?.toString()
        var phone = binding?.editCustomerPhone?.text?.toString()

        if (name?.isEmpty() == true) {
          showShortToast(getString(R.string.customer_name_cannot_be_empty))
          return
        }

        if (phone?.isEmpty() == true) {
          showShortToast(getString(R.string.customer_phone_cannot_be_empty))
          return
        }

        if (email.isNullOrEmpty().not() && android.util.Patterns.EMAIL_ADDRESS.matcher(email)
            .matches().not()
        ) {
          showShortToast(getString(R.string.please_enter_valid_email))
          return
        }

        var contactDetails =
          ContactDetails(emailId = email!!, fullName = name!!, primaryContactNumber = phone!!)
        onClicked(contactDetails)
      }
    }
  }

}