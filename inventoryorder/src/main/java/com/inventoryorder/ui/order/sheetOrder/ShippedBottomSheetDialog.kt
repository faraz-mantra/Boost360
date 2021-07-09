package com.inventoryorder.ui.order.sheetOrder

import android.view.View
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.visible
import com.framework.models.BaseViewModel
import com.framework.utils.DateUtils.FORMAT_SERVER_DATE
import com.framework.utils.DateUtils.getCurrentDate
import com.framework.utils.DateUtils.parseDate
import com.framework.utils.ValidationUtils.isMobileNumberValid
import com.inventoryorder.R
import com.inventoryorder.databinding.BottomSheetShippedOrderBinding
import com.inventoryorder.model.orderRequest.shippedRequest.Address
import com.inventoryorder.model.orderRequest.shippedRequest.DeliveryPersonDetails
import com.inventoryorder.model.orderRequest.shippedRequest.MarkAsShippedRequest
import com.inventoryorder.model.ordersdetails.OrderItem

class ShippedBottomSheetDialog : BaseBottomSheetDialog<BottomSheetShippedOrderBinding, BaseViewModel>() {

  private var orderItem: OrderItem? = null
  private var deliveryProvider: String = MarkAsShippedRequest.ShippedBy.LOCAL_PERSON.value
  var onClicked: (markAsShippedRequest: MarkAsShippedRequest) -> Unit = {}

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_shipped_order
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  fun setData(orderItem: OrderItem) {
    this.orderItem = orderItem
  }

  override fun onCreateView() {
    setOnClickListener(binding?.buttonDone, binding?.tvCancel)
    binding?.localEdtView?.visible()
    binding?.tvSubTitle?.text = "Order ID #${orderItem?.ReferenceNumber ?: ""}"
    binding?.radioGroup?.setOnCheckedChangeListener { group, checkedId ->
      val radioButton: View = group.findViewById(checkedId)
      deliveryProvider = when (radioButton) {
        binding?.radioLocal -> {
          changeUi(true)
          MarkAsShippedRequest.ShippedBy.LOCAL_PERSON.value
        }
        binding?.radioPartner -> {
          changeUi(false)
          MarkAsShippedRequest.ShippedBy.SELLER.value
        }
        else -> MarkAsShippedRequest.ShippedBy.LOCAL_PERSON.value
      }
    }
  }

  private fun changeUi(isLocal: Boolean) {
    binding?.localEdtView?.visibility = if (isLocal) View.VISIBLE else View.GONE
    binding?.partnerEdtView?.visibility = if (!isLocal) View.VISIBLE else View.GONE
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.buttonDone -> {
        val request: MarkAsShippedRequest
        val date = getCurrentDate().parseDate(FORMAT_SERVER_DATE)
        val address = Address(addressLine1 = orderItem?.BuyerDetails?.address()?.addressLine1(),
            addressLine2 = orderItem?.BuyerDetails?.address()?.AddressLine2, city = orderItem?.BuyerDetails?.address()?.City,
            country = orderItem?.BuyerDetails?.address()?.Country, region = orderItem?.BuyerDetails?.address()?.Region,
            zipcode = orderItem?.BuyerDetails?.address()?.Zipcode)
        if (deliveryProvider == MarkAsShippedRequest.ShippedBy.LOCAL_PERSON.value) {
          val name = binding?.tvDeliveryPersonName?.text?.toString() ?: ""
          val number = binding?.edtNumber?.text?.toString() ?: ""
          if (name.isEmpty()) {
            showShortToast(getString(R.string.delivery_person_name_cant_be_empty))
            return
          }
          if (number.isEmpty()) {
            showShortToast(getString(R.string.delivery_person_number_cant_be_empty))
            return
          }
          if (!isMobileNumberValid(number)) {
            showShortToast(getString(R.string.invalid_delivery_person_number))
            return
          }
          request = MarkAsShippedRequest(orderId = orderItem?._id, shippedOn = date, deliveryProvider = deliveryProvider,
              deliveryPersonDetails = DeliveryPersonDetails(fullName = name, primaryContactNumber = number, emailId = ""), address = address)
        } else {
          val id = binding?.edtConsignmentId?.text?.toString() ?: ""
          val url = binding?.edtTrackingUrl?.text?.toString() ?: ""
          if (id.isEmpty()) {
            showShortToast(getString(R.string.consignment_id_name_cant_be_empty))
            return
          }
          if (url.isEmpty()) {
            showShortToast(getString(R.string.consignment_tracking_url_cant_be_empty))
            return
          }
          request = MarkAsShippedRequest(orderId = orderItem?._id, shippedOn = date, deliveryProvider = deliveryProvider,
              trackingNumber = id, trackingURL = url, address = address)
        }
        dismiss()
        onClicked(request)
      }
      binding?.tvCancel -> dismiss()
    }
  }

}