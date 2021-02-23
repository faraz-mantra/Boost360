package com.inventoryorder.ui.appointmentSpa.list

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.framework.exceptions.NoNetworkException
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.utils.DateUtils
import com.inventoryorder.R
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.FragmentAppointmentSpaDetailsBinding
import com.inventoryorder.model.OrderConfirmStatus
import com.inventoryorder.model.UpdateOrderNPropertyRequest
import com.inventoryorder.model.bottomsheet.LocationsModel
import com.inventoryorder.model.orderRequest.UpdateExtraPropertyRequest
import com.inventoryorder.model.orderRequest.extraProperty.ExtraPropertiesOrder
import com.inventoryorder.model.orderRequest.shippedRequest.MarkAsShippedRequest
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.model.ordersummary.OrderMenuModel
import com.inventoryorder.model.ordersummary.OrderSummaryModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import com.inventoryorder.recyclerView.RecyclerItemClickListener
import com.inventoryorder.rest.response.order.OrderDetailResponse
import com.inventoryorder.rest.response.order.ProductResponse
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.appointment.LocationBottomSheetDialog
import com.inventoryorder.ui.order.sheetOrder.CancelBottomSheetDialog
import com.inventoryorder.ui.order.sheetOrder.ConfirmBottomSheetDialog
import com.inventoryorder.ui.order.sheetOrder.DeliveredBottomSheetDialog
import com.inventoryorder.ui.order.sheetOrder.RequestPaymentBottomSheetDialog
import com.inventoryorder.utils.capitalizeUtil
import com.squareup.picasso.Picasso
import java.util.*

class AppointmentSpaDetailsFragment : BaseInventoryFragment<FragmentAppointmentSpaDetailsBinding>(), RecyclerItemClickListener {

  private var orderItem: OrderItem? = null
  private var isRefresh: Boolean? = null
  lateinit var mPopupWindow: PopupWindow
  private var productList: ArrayList<ProductResponse>? = null
  private var locationsBottomSheetDialog: LocationBottomSheetDialog? = null
  private var serviceLocationsList = LocationsModel().getData()

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): AppointmentSpaDetailsFragment {
      val fragment = AppointmentSpaDetailsFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    arguments?.getString(IntentConstant.ORDER_ID.name)?.let { apiGetOrderDetails(it) }
    binding?.textPhone?.setOnClickListener {
      if (orderItem?.BuyerDetails?.ContactDetails?.PrimaryContactNumber.isNullOrEmpty()) {
        showShortToast(getString(R.string.contact_number_not_available))
      } else {
        callCustomer(orderItem?.BuyerDetails?.ContactDetails?.PrimaryContactNumber!!)
      }
    }

    binding?.textEmail?.setOnClickListener {
      if (orderItem?.BuyerDetails?.ContactDetails?.EmailId.isNullOrEmpty()) {
        showShortToast(getString(R.string.customer_email_not_available))
      } else {
        emailCustomer(orderItem?.BuyerDetails?.ContactDetails?.EmailId!!)
      }
    }
  }

  private fun callCustomer(phone: String) {
    val intent = Intent(Intent.ACTION_DIAL)
    intent.data = Uri.parse("tel:$phone")
    startActivity(intent)
  }

  private fun emailCustomer(email: String) {
    val i = Intent(Intent.ACTION_SENDTO)
    i.data = Uri.parse("mailto:${email}")
    try {
      startActivity(i)
    } catch (ex: ActivityNotFoundException) {
      showShortToast("There are no email clients installed.")
    }
  }

  private fun apiGetOrderDetails(orderId: String, message: String="") {
    showProgress()
    viewModel?.getOrderDetails(clientId, orderId)?.observeOnce(viewLifecycleOwner, Observer {
      if (it.isSuccess()) {
        orderItem = (it as? OrderDetailResponse)?.Data
        if (orderItem != null) {
          getProductAllDetails()
          if (message.isNotEmpty()) {
            isRefresh = true
            showShortToast(message)
          }
        } else errorUi("Appointment detail empty.")
      } else errorUi(it.message())
    })
  }

  private fun getProductAllDetails() {
    productList = ArrayList()
    var count = 0
    if (orderItem?.Items.isNullOrEmpty().not()) {
      orderItem?.Items?.forEach {
        viewModel?.getProductDetails(it.Product?._id)?.observeOnce(viewLifecycleOwner, Observer { it1 ->
          count += 1
          val product = it1 as? ProductResponse
          if (count == orderItem?.Items?.size) {
            product?.let { it2 -> productList?.add(it2) }
            addProductToOrder()
          } else product?.let { it2 -> productList?.add(it2) }
        })
      }
    } else addProductToOrder()
  }

  private fun addProductToOrder() {
    productList?.forEach { orderItem?.Items?.firstOrNull { it1 -> it1.Product?._id?.trim() == it.Product?._id?.trim() }?.product_detail = it.Product }
    binding?.mainView?.visible()
    binding?.error?.gone()
    setDetails(orderItem)
    hideProgress()
  }


  private fun setDetails(order: OrderItem?) {
    binding?.textFromBookingValue?.text = "#${order?.ReferenceNumber}"
    // binding?.textDateTime?.text = order.CreatedOn
    binding?.textDateTime?.text = DateUtils.parseDate(order?.CreatedOn, DateUtils.FORMAT_SERVER_DATE, DateUtils.FORMAT_SERVER_TO_LOCAL_3, timeZone = TimeZone.getTimeZone("IST"))

    binding?.textAmount?.text = "${order?.BillingDetails?.CurrencyCode} ${order?.BillingDetails?.GrossAmount}"

    binding?.textServiceName?.text = order?.firstItemForAptConsult()?.product()?.Name
    // binding?.textDate?.text = order?.firstItemForConsultation()?.product()?.extraItemProductConsultation()?.scheduledDateTime

    val extraDataSpa = order?.firstItemForAptConsult()?.getAptSpaExtraDetail()

    val appointmentDate = "${extraDataSpa?.startTime()} on ${DateUtils.parseDate(extraDataSpa?.scheduledDateTime ?: "", DateUtils.FORMAT_YYYY_MM_DD, DateUtils.FORMAT_SERVER_TO_LOCAL_5)}"

    binding?.textDate?.text = appointmentDate

    binding?.textStaff?.text = if (!extraDataSpa?.staffName.isNullOrBlank()) "Staff : ${extraDataSpa?.staffName}" else ""
    binding?.textAppointmentAmount?.text = "${order?.firstItemForAptConsult()?.product()?.getCurrencyCodeValue()} ${order?.firstItemForAptConsult()?.product()?.price()}"

    if (order?.firstItemForAptConsult()?.product()?.ImageUri.isNullOrEmpty().not()) {
      Picasso.get().load(order?.firstItemForAptConsult()?.product()?.ImageUri).into(binding?.imageServiceProvider)
    }

    binding?.textCustomerName?.text = order?.BuyerDetails?.ContactDetails?.FullName
    binding?.textCustomerPhone?.text = order?.BuyerDetails?.ContactDetails?.PrimaryContactNumber
    binding?.textCustomerEmail?.text = order?.BuyerDetails?.ContactDetails?.EmailId

    binding?.textPaymentStatusDropdown?.text = "${order?.PaymentDetails?.statusValue()}"
    binding?.textPaymentTypeDropdown?.text = "${order?.PaymentDetails?.methodValue()}"
    binding?.textServiceLocationDropdown?.text = "${order?.SellerDetails?.Address?.City?.capitalizeUtil()}"

    order?.BuyerDetails?.let {
      val address = it.getFullAddressDetail()
      if (address.isEmpty()) binding?.groupCustomerAddress?.visibility = View.GONE
      else binding?.textAddrValue?.text = address
    }
    setButtonStatus(order)
  }

  private fun setButtonStatus(order: OrderItem?) {
    //settings up button
    var colorCode = "#4a4a4a"
    val btnStatusMenu = order?.appointmentSpaButtonStatus()
    binding?.lytStatusBtn?.visible()
    if (btnStatusMenu.isNullOrEmpty().not()) {
      when (val btnOrderMenu = btnStatusMenu?.removeAt(0)) {
        OrderMenuModel.MenuStatus.CONFIRM_APPOINTMENT -> {
          colorCode = "#f16629"
          changeButtonStatus(btnOrderMenu.title, R.drawable.ic_initiated_order_btn_bkg, R.color.white, R.drawable.ic_arrow_down_white)
        }
        OrderMenuModel.MenuStatus.START_APPOINTMENT -> {
          colorCode = "#f16629"
          changeButtonStatus(btnOrderMenu.title, R.drawable.ic_initiated_order_btn_green, R.color.white, R.drawable.ic_arrow_down_white)
        }
        OrderMenuModel.MenuStatus.REQUEST_PAYMENT -> {
          colorCode = "#f16629"
          changeButtonStatus(btnOrderMenu.title, R.drawable.ic_initiated_order_btn_bkg, R.color.white, R.drawable.ic_arrow_down_white)
        }
        OrderMenuModel.MenuStatus.CANCEL_APPOINTMENT -> {
          colorCode = "#9B9B9B"
          changeButtonStatus(btnOrderMenu.title, R.drawable.ic_cancelled_order_btn_bkg, R.color.warm_grey_two, R.drawable.ic_arrow_down_grey)
        }
        OrderMenuModel.MenuStatus.MARK_PAYMENT_DONE -> {
          colorCode = "#FFB900"
          changeButtonStatus(btnOrderMenu.title, R.drawable.ic_confirmed_order_btn_bkg, R.color.orange, R.drawable.ic_arrow_down_orange)
        }
        OrderMenuModel.MenuStatus.MARK_AS_SERVED -> {
          colorCode = "#78AF00"
          changeButtonStatus(btnOrderMenu.title, R.drawable.ic_transit_order_btn_green, R.color.green_78AF00, R.drawable.ic_arrow_down_green)
        }
        else -> binding?.lytStatusBtn?.gone()
      }
      binding?.tvDropdownOrderStatus?.setOnClickListener {
        orderItem?.let { it1 -> clickActionAptButton(order?.appointmentSpaButtonStatus()?.firstOrNull(), it1) }
      }
    } else binding?.lytStatusBtn?.gone()

    if (btnStatusMenu.isNullOrEmpty()) {
      binding?.divider?.gone()
      binding?.ivDropdown?.gone()
    } else {
      binding?.ivDropdown?.setOnClickListener { popUpMenuButton(it) }
      binding?.divider?.visible()
      binding?.ivDropdown?.visible()
    }

    OrderSummaryModel.OrderStatus.from(order?.status())?.let {
      when (it) {
        OrderSummaryModel.OrderStatus.ORDER_CANCELLED -> {
          // changeBackground(View.GONE, View.VISIBLE, R.drawable.cancel_order_bg, R.color.primary_grey, R.color.primary_grey)
          // binding.btnConfirm.gone()
        }
        else -> {
          //changeBackground(View.VISIBLE, View.GONE, R.drawable.ic_apt_order_bg, R.color.watermelon_light, R.color.light_green)
          // checkConfirmBtn(order)
        }
      }
    }
  }


  private fun popUpMenuButton(view: View) {
    val list = OrderMenuModel().getAppointmentMenu(orderItem)
    if (list.isNotEmpty()) list.removeAt(0)
    val orderMenuView: View = LayoutInflater.from(baseActivity).inflate(R.layout.menu_order_button, null)
    val rvOrderMenu: RecyclerView? = orderMenuView.findViewById(R.id.rv_menu_order)
    rvOrderMenu?.apply {
      val adapterMenu = AppBaseRecyclerViewAdapter(baseActivity, list, this@AppointmentSpaDetailsFragment)
      adapter = adapterMenu
    }
    mPopupWindow = PopupWindow(orderMenuView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true)
    mPopupWindow.showAsDropDown(view, 0, 0)
  }

  fun getBundleData(): Bundle? {
    isRefresh?.let {
      val bundle = Bundle()
      bundle.putBoolean(IntentConstant.IS_REFRESH.name, it)
      return bundle
    }
    return null
  }

  private fun errorUi(message: String) {
    hideProgress()
    binding?.mainView?.gone()
    binding?.error?.visible()
    binding?.error?.text = message
  }

  private fun changeButtonStatus(btnTitle: String, @DrawableRes buttonBkg: Int, @ColorRes dropDownDividerColor: Int, @DrawableRes resId: Int) {
    activity?.let {
      binding?.tvDropdownOrderStatus?.text = btnTitle
      binding?.tvDropdownOrderStatus?.visibility = View.VISIBLE
      binding?.tvDropdownOrderStatus?.setTextColor(ContextCompat.getColor(it, dropDownDividerColor))
      binding?.lytStatusBtn?.background = ContextCompat.getDrawable(it, buttonBkg)
      binding?.divider?.setBackgroundColor(ContextCompat.getColor(it, dropDownDividerColor))
      binding?.ivDropdown?.setImageResource(resId)
      //DrawableCompat.setTint(binding.ivDropdownOrderStatus.drawable, ContextCompat.getColor(it.applicationContext, dropDownArrowColor))
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.ORDER_DROPDOWN_CLICKED.ordinal -> {
        if (::mPopupWindow.isInitialized && mPopupWindow.isShowing) mPopupWindow.dismiss()
        val orderMenu = OrderMenuModel.MenuStatus.from((item as? OrderMenuModel)?.type) ?: return
        clickActionAptButton(orderMenu, this.orderItem!!)
      }
    }
  }

  private fun clickActionAptButton(orderMenu: OrderMenuModel.MenuStatus?, orderItem: OrderItem) {
    when (orderMenu) {
      OrderMenuModel.MenuStatus.CONFIRM_APPOINTMENT -> {
        val sheetConfirm = ConfirmBottomSheetDialog()
        sheetConfirm.setData(orderItem)
        sheetConfirm.onClicked = { apiConfirmApt(it) }
        sheetConfirm.show(this.parentFragmentManager, ConfirmBottomSheetDialog::class.java.name)
      }
      OrderMenuModel.MenuStatus.REQUEST_PAYMENT -> {
        val sheetRequestPayment = RequestPaymentBottomSheetDialog()
        sheetRequestPayment.setData(orderItem)
        sheetRequestPayment.onClicked = {
          showProgress()
          sendPaymentLinkApt(getString(R.string.payment_request_send))
        }
        sheetRequestPayment.show(this.parentFragmentManager, RequestPaymentBottomSheetDialog::class.java.name)
      }
      OrderMenuModel.MenuStatus.CANCEL_APPOINTMENT -> {
        this.orderItem = orderItem
        val sheetCancel = CancelBottomSheetDialog()
        sheetCancel.setData(orderItem)
        sheetCancel.onClicked = this@AppointmentSpaDetailsFragment::apiCancelApt
        sheetCancel.show(this.parentFragmentManager, CancelBottomSheetDialog::class.java.name)
      }
      OrderMenuModel.MenuStatus.MARK_PAYMENT_DONE -> markCodPaymentRequest()
      OrderMenuModel.MenuStatus.MARK_AS_SERVED -> {
        val sheetDelivered = DeliveredBottomSheetDialog()
        sheetDelivered.setData(orderItem)
        sheetDelivered.onClicked = { serveCustomer(it) }
        sheetDelivered.show(this.parentFragmentManager, DeliveredBottomSheetDialog::class.java.name)
      }
      OrderMenuModel.MenuStatus.START_APPOINTMENT -> {
        showShortToast("Coming soon...")
      }
      else -> {
      }
    }
  }


  private fun startApt(markAsShippedRequest: MarkAsShippedRequest) {
    showProgress()
    viewModel?.markAsShipped(clientId, markAsShippedRequest)?.observeOnce(viewLifecycleOwner, Observer {
      if (it.error is NoNetworkException) {
        showShortToast(resources.getString(R.string.internet_connection_not_available))
        hideProgress()
        return@Observer
      }
      if (it.isSuccess()) {
        orderItem?._id?.let { it1 -> apiGetOrderDetails(it1, resources.getString(R.string.order_shipped)) }
      } else {
        showLongToast(it.message())
        hideProgress()
      }
    })
  }

  private fun serveCustomer(message: String) {
    showProgress()
    viewModel?.markAsDelivered(clientId, this.orderItem?._id)?.observeOnce(viewLifecycleOwner, Observer {
      if (it.error is NoNetworkException) {
        showShortToast(resources.getString(R.string.internet_connection_not_available))
        hideProgress()
        return@Observer
      }
      if (it.isSuccess()) {
        if (message.isNotEmpty()) {
          updateReason(resources.getString(R.string.order_delivery), UpdateExtraPropertyRequest.PropertyType.DELIVERY.name, ExtraPropertiesOrder(deliveryRemark = message))
        } else {
          orderItem?._id?.let { it1 -> apiGetOrderDetails(it1, resources.getString(R.string.order_cancel)) }
        }
      } else {
        showLongToast(it.message())
        hideProgress()
      }
    })
  }

  private fun apiCancelApt(cancellingEntity: String, reasonText: String) {
    showProgress()
    viewModel?.cancelOrder(clientId, this.orderItem?._id, cancellingEntity)?.observeOnce(viewLifecycleOwner, Observer {
      if (it.error is NoNetworkException) {
        showShortToast(resources.getString(R.string.internet_connection_not_available))
        hideProgress()
        return@Observer
      }
      if (it.isSuccess()) {
        val data = it as? OrderConfirmStatus
        if (reasonText.isNotEmpty()) {
          updateReason(resources.getString(R.string.order_cancel), UpdateExtraPropertyRequest.PropertyType.CANCELLATION.name, ExtraPropertiesOrder(cancellationRemark = reasonText))
        } else {
          orderItem?._id?.let { it1 -> apiGetOrderDetails(it1, resources.getString(R.string.order_cancel)) }
        }
      } else {
        showLongToast(it.message())
        hideProgress()
      }
    })
  }

  private fun updateReason(message: String, type: String, extraPropertiesOrder: ExtraPropertiesOrder) {
    val propertyRequest = UpdateOrderNPropertyRequest(updateExtraPropertyType = type,
        existingKeyName = "", orderId = this.orderItem?._id, extraPropertiesOrder = extraPropertiesOrder)
    viewModel?.updateExtraPropertyOrder(clientId, requestCancel = propertyRequest)?.observeOnce(viewLifecycleOwner, {
      orderItem?._id?.let { it1 -> apiGetOrderDetails(it1, message) }
    })
  }

  private fun markCodPaymentRequest() {
    showProgress()
    viewModel?.markCodPaymentDone(clientId, this.orderItem?._id)?.observeOnce(viewLifecycleOwner, Observer {
      if (it.error is NoNetworkException) {
        showShortToast(resources.getString(R.string.internet_connection_not_available))
        hideProgress()
        return@Observer
      }
      if (it.isSuccess()) {
        orderItem?._id?.let { it1 -> apiGetOrderDetails(it1, getString(R.string.order_payment_done)) }
      } else {
        showLongToast(it.message())
        hideProgress()
      }
    })
  }

  private fun apiConfirmApt(isSendPaymentLink: Boolean) {
    showProgress()
    viewModel?.confirmOrder(clientId, this.orderItem?._id)?.observeOnce(viewLifecycleOwner, Observer {
      if (it.error is NoNetworkException) {
        showShortToast(resources.getString(R.string.internet_connection_not_available))
        hideProgress()
        return@Observer
      }
      if (it.isSuccess()) {
        if (isSendPaymentLink) sendPaymentLinkApt(getString(R.string.order_confirmed))
        else {
          orderItem?._id?.let { it1 -> apiGetOrderDetails(it1, getString(R.string.order_confirmed)) }
        }
      } else {
        showLongToast(it.message())
        hideProgress()
      }
    })
  }

  private fun sendPaymentLinkApt(message: String) {
    viewModel?.sendPaymentReminder(clientId, this.orderItem?._id)?.observeOnce(viewLifecycleOwner, { it1 ->
      orderItem?._id?.let { it1 -> apiGetOrderDetails(it1, message) }
    })
  }
}