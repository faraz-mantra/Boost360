package com.inventoryorder.ui.appointment.sheetApt

import android.view.View
import com.framework.base.BaseBottomSheetDialog
import com.framework.models.BaseViewModel
import com.framework.utils.DateUtils.FORMAT_SERVER_DATE
import com.framework.utils.DateUtils.getCurrentDate
import com.framework.utils.DateUtils.parseDate
import com.framework.utils.fromHtml
import com.inventoryorder.R
import com.inventoryorder.databinding.BottomSheetStartAptBinding
import com.inventoryorder.model.orderRequest.shippedRequest.Address
import com.inventoryorder.model.orderRequest.shippedRequest.DeliveryPersonDetails
import com.inventoryorder.model.orderRequest.shippedRequest.MarkAsShippedRequest
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.utils.capitalizeUtil

class StartAptSheetDialog : BaseBottomSheetDialog<BottomSheetStartAptBinding, BaseViewModel>() {

  private var orderItem: OrderItem? = null
  private var deliveryProvider: String = MarkAsShippedRequest.ShippedBy.SELLER.value
  var onClicked: (markAsShippedRequest: MarkAsShippedRequest) -> Unit = {}

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
    val extraProperty = orderItem?.firstItemForConsultation()?.product()?.extraItemProductConsultation()
    binding?.txtName?.text = fromHtml("Customer Name: <b>${extraProperty?.patientName?.capitalizeUtil()}</b>")
    binding?.txtNumber?.text = fromHtml("Phone No: <b>${extraProperty?.patientMobileNumber}</b>")
    binding?.txtService?.text = fromHtml("Service: <b>${extraProperty?.consultationFor?.capitalizeUtil()}</b>")
    binding?.txtStaffName?.text = fromHtml("Staff Name: <b>${extraProperty?.doctorName?.capitalizeUtil()}</b>")
  }


  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.buttonDone -> {
        val request: MarkAsShippedRequest
        val date = getCurrentDate().parseDate(FORMAT_SERVER_DATE)
        val extraProperty = orderItem?.firstItemForConsultation()?.product()?.extraItemProductConsultation()
        val address = Address(addressLine1 = orderItem?.BuyerDetails?.address()?.addressLine1(),
            addressLine2 = orderItem?.BuyerDetails?.address()?.AddressLine2, city = orderItem?.BuyerDetails?.address()?.City,
            country = orderItem?.BuyerDetails?.address()?.Country, region = orderItem?.BuyerDetails?.address()?.Region,
            zipcode = orderItem?.BuyerDetails?.address()?.Zipcode)
        request = MarkAsShippedRequest(orderId = orderItem?._id, shippedOn = date, deliveryProvider = deliveryProvider,
            deliveryPersonDetails = DeliveryPersonDetails(fullName = extraProperty?.patientName ?: "", primaryContactNumber = extraProperty?.patientMobileNumber ?: "", emailId = ""), address = address)
        dismiss()
        onClicked(request)
      }
      binding?.tvCancel -> dismiss()
    }
  }

}