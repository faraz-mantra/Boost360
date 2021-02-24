package com.inventoryorder.ui.appointmentSpa.sheetAptSpa

import android.view.View
import com.framework.base.BaseBottomSheetDialog
import com.framework.extensions.gone
import com.framework.models.BaseViewModel
import com.framework.utils.DateUtils
import com.framework.utils.DateUtils.parseDate
import com.framework.utils.fromHtml
import com.inventoryorder.R
import com.inventoryorder.databinding.BottomSheetStartAptBinding
import com.inventoryorder.model.orderRequest.shippedRequest.Address
import com.inventoryorder.model.orderRequest.shippedRequest.MarkAsShippedRequest
import com.inventoryorder.model.ordersdetails.OrderItem

class StartAptSheetDialog : BaseBottomSheetDialog<BottomSheetStartAptBinding, BaseViewModel>() {

  private var orderItem: OrderItem? = null
  var onClicked: (request: MarkAsShippedRequest) -> Unit = {}

  override fun getLayout(): Int {
    return R.layout.bottom_sheet_start_apt
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  fun setData(orderItem: OrderItem) {
    this.orderItem = orderItem
  }

  override fun onCreateView() {
    setOnClickListener(binding?.buttonDone, binding?.tvCancel)
    binding?.tvSubTitle?.text = "Appointment ID #${orderItem?.ReferenceNumber ?: ""}"
    val item = orderItem?.firstItemForAptConsult()
    val aptData = item?.getAptSpaExtraDetail()
    val contact = orderItem?.BuyerDetails?.ContactDetails
    val fullName = contact?.FullName
    val number = contact?.PrimaryContactNumber
    val staffName = aptData?.staffName
    val serviceName = item?.product()?.Name
    if (fullName.isNullOrEmpty()) binding?.txtCustomerName?.gone()
    if (number.isNullOrEmpty()) binding?.txtPhoneNo?.gone()
    if (staffName.isNullOrEmpty()) binding?.txtStaffName?.gone()
    if (serviceName.isNullOrEmpty()) binding?.txtServices?.gone()
    binding?.txtCustomerName?.text = fromHtml("Customer Name: <b>${fullName}</b>")
    binding?.txtPhoneNo?.text = fromHtml("Phone No: <b>${number}</b>")
    binding?.txtStaffName?.text = fromHtml("Staff Name: <b>${staffName}</b>")
    binding?.txtServices?.text = fromHtml("Service: <b>${serviceName}</b>")
  }

  override fun onClick(v: View) {
    super.onClick(v)
    dismiss()
    when (v) {
      binding?.buttonDone -> {
        val date = DateUtils.getCurrentDate().parseDate(DateUtils.FORMAT_SERVER_DATE)
        val address = Address(addressLine1 = orderItem?.BuyerDetails?.address()?.addressLine1(),
            addressLine2 = orderItem?.BuyerDetails?.address()?.AddressLine2, city = orderItem?.BuyerDetails?.address()?.City,
            country = orderItem?.BuyerDetails?.address()?.Country, region = orderItem?.BuyerDetails?.address()?.Region,
            zipcode = orderItem?.BuyerDetails?.address()?.Zipcode)
        onClicked(MarkAsShippedRequest(orderId = orderItem?._id,shippedOn = date, address = address, shippedBy = MarkAsShippedRequest.ShippedBy.SELLER.value))
      }
    }
  }

}