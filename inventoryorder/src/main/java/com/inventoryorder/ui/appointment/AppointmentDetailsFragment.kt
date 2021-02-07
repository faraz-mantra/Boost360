package com.inventoryorder.ui.appointment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.appsflyer.Foreground.listener
import com.framework.exceptions.NoNetworkException
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.utils.DateUtils
import com.inventoryorder.R
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.FragmentAppointmentDetailsBinding
import com.inventoryorder.model.OrderConfirmStatus
import com.inventoryorder.model.UpdateOrderNPropertyRequest
import com.inventoryorder.model.bottomsheet.LocationsModel
import com.inventoryorder.model.orderRequest.UpdateExtraPropertyRequest
import com.inventoryorder.model.orderRequest.extraProperty.ExtraPropertiesOrder
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.model.ordersummary.OrderMenuModel
import com.inventoryorder.model.ordersummary.OrderSummaryModel
import com.inventoryorder.model.product.Product
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import com.inventoryorder.recyclerView.RecyclerItemClickListener
import com.inventoryorder.rest.response.order.OrderDetailResponse
import com.inventoryorder.rest.response.order.ProductResponse
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.order.sheetOrder.CancelBottomSheetDialog
import com.inventoryorder.ui.startFragmentOrderActivity
import com.inventoryorder.utils.capitalizeUtil
import com.squareup.picasso.Picasso
import java.util.*


class AppointmentDetailsFragment : BaseInventoryFragment<FragmentAppointmentDetailsBinding>(), RecyclerItemClickListener {

  private var locationsBottomSheetDialog: LocationBottomSheetDialog? = null
  private var orderItem: OrderItem? = null
  private var serviceLocationsList = LocationsModel().getData()
  private var isRefresh: Boolean? = null
  lateinit var mPopupWindow: PopupWindow
  private var productList: ArrayList<ProductResponse>? = null

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): AppointmentDetailsFragment {
      val fragment = AppointmentDetailsFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    arguments?.getString(IntentConstant.ORDER_ID.name)?.let { apiGetOrderDetails(it) }
   // setOnClickListener(binding?.btnBusiness, binding?.tvCustomerContactNumber, binding?.tvCustomerEmail)

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

  private fun apiGetOrderDetails(orderId: String) {
    showProgress()
    viewModel?.getOrderDetails(clientId, orderId)?.observeOnce(viewLifecycleOwner, Observer {
      if (it.error is NoNetworkException) {
        errorUi(resources.getString(R.string.internet_connection_not_available))
        return@Observer
      }
      if (it.status == 200 || it.status == 201 || it.status == 202) {
        orderItem = (it as? OrderDetailResponse)?.Data
        if (orderItem != null) {
          getProductAllDetails()
        } else errorUi("Order item null.")
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
     hideProgress()
     binding?.mainView?.visible()
     binding?.error?.gone()
     setDetails(orderItem!!)
   }


    private fun setDetails(order: OrderItem) {

      binding?.textFromBookingValue?.text = "#${order.ReferenceNumber}"
     // binding?.textDateTime?.text = order.CreatedOn
      binding?.textDateTime?.text = DateUtils.parseDate(order?.CreatedOn, DateUtils.FORMAT_SERVER_DATE, DateUtils.FORMAT_SERVER_TO_LOCAL_3, timeZone = TimeZone.getTimeZone("IST"))

      binding?.textAmount?.text = "${order?.BillingDetails?.CurrencyCode} ${order?.BillingDetails?.GrossAmount}"

      binding?.textServiceName?.text = order?.firstItemForConsultation()?.product()?.Name
     // binding?.textDate?.text = order?.firstItemForConsultation()?.product()?.extraItemProductConsultation()?.scheduledDateTime

      var appointmentDate = java.lang.StringBuilder(DateUtils.parseDate(order?.firstItemForConsultation()?.Product?.extraItemProductConsultation()?.startTime(), DateUtils.FORMAT_HH_MM, DateUtils.FORMAT_HH_MM_A) ?: "") /*"${} on ${}"*/
      if (!DateUtils.parseDate(order?.firstItemForConsultation()?.product()?.extraItemProductConsultation()?.scheduledDateTime, DateUtils.FORMAT_SERVER_DATE, DateUtils.FORMAT_SERVER_TO_LOCAL_5, timeZone = TimeZone.getTimeZone("IST")).isNullOrEmpty()) {
        appointmentDate.append(" on ${DateUtils.parseDate(order?.firstItemForConsultation()?.product()?.extraItemProductConsultation()?.scheduledDateTime, DateUtils.FORMAT_SERVER_DATE, DateUtils.FORMAT_SERVER_TO_LOCAL_5)}")
      }
      binding?.textDate?.text =  appointmentDate

      binding?.textStaff?.text = if (!order?.firstItemForConsultation()?.product()?.extraItemProductConsultation()?.staffName.isNullOrBlank())  "Staff : ${order?.firstItemForConsultation()?.product()?.extraItemProductConsultation()?.staffName}" else ""
      binding?.textAppointmentAmount?.text = "${order?.firstItemForConsultation()?.product()?.CurrencyCode} ${order?.firstItemForConsultation()?.product()?.price()}"

      if (order?.firstItemForConsultation()?.product()?.ImageUri.isNullOrEmpty().not()) {
        Picasso.get().load(order?.firstItemForConsultation()?.product()?.ImageUri).into(binding?.imageServiceProvider)
      }

      binding?.textCustomerName?.text = order?.BuyerDetails?.ContactDetails?.FullName
      binding?.textCustomerPhone?.text = order?.BuyerDetails?.ContactDetails?.PrimaryContactNumber
      binding?.textCustomerEmail?.text = order?.BuyerDetails?.ContactDetails?.EmailId

      binding?.textPaymentStatusDropdown?.text = "${order?.PaymentDetails?.status()?.capitalizeUtil()}"
      binding?.textPaymentTypeDropdown?.text = "${order?.PaymentDetails?.methodValue()?.capitalizeUtil()}"
      binding?.textServiceLocationDropdown?.text = "${order?.SellerDetails?.Address?.City?.capitalizeUtil()}"

      order?.BuyerDetails?.Address?.let {
        var address = StringBuilder()

        if (order?.BuyerDetails?.Address?.AddressLine1.isNullOrBlank().not()) {
          address.append("${order?.BuyerDetails?.Address?.AddressLine1 ?: ""}")
        }

        if (order?.BuyerDetails?.Address?.AddressLine2.isNullOrBlank().not()) {
          address.append(", ${order?.BuyerDetails?.Address?.AddressLine2 ?: ""}")
        }

        if (order?.BuyerDetails?.Address?.City.isNullOrBlank().not()) {
          address.append(", ${order?.BuyerDetails?.Address?.City ?: ""} ")
        }

        if (address.isNullOrEmpty()) {
          binding?.groupCustomerAddress?.visibility = View.GONE
        } else {
          binding?.textAddrValue?.text = address
        }
      }

      setButtonStatus(order)
    }

    private fun setButtonStatus(order: OrderItem) {
      //settings up button
      var colorCode = "#4a4a4a"
      val btnStatusMenu = order.appointmentButtonStatus()
      binding?.lytStatusBtn?.visible()
      if (btnStatusMenu.isNullOrEmpty().not()) {
        when (val btnOrderMenu = btnStatusMenu.removeAt(0)) {
          OrderMenuModel.MenuStatus.CONFIRM_APPOINTMENT -> {
            colorCode = "#f16629"
            changeButtonStatus(btnOrderMenu.title, R.drawable.ic_initiated_order_btn_bkg, R.color.white, R.drawable.ic_arrow_down_white)
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
          OrderMenuModel.MenuStatus.CUSTOMER_SERVED -> {
            colorCode = "#52AAC6"
            changeButtonStatus(btnOrderMenu.title, R.drawable.ic_in_transit_order_btn_bkg, R.color.blue_52AAC6, R.drawable.ic_arrow_down_blue)
          }
          else -> binding?.lytStatusBtn?.gone()
        }
        binding?.tvDropdownOrderStatus?.setOnClickListener { onButtonClicked(orderItem!!) }
      } else binding?.lytStatusBtn?.gone()

      if (btnStatusMenu.isNullOrEmpty()) {
        binding?.divider?.gone()
        binding?.ivDropdown?.gone()
      } else {
        binding?.ivDropdown?.setOnClickListener { popUpMenuButton(it) }
        binding?.divider?.visible()
        binding?.ivDropdown?.visible()
      }

      OrderSummaryModel.OrderStatus.from(order.status())?.let {
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

    private fun onButtonClicked(item: OrderItem) {
      if (((item as? OrderItem)?.Status?.equals("ORDER_CONFIRMED")) == true) {
        this.orderItem = item
        val sheetCancel = CancelBottomSheetDialog()
        sheetCancel.setData(item)
        sheetCancel.onClicked = this@AppointmentDetailsFragment::apiCancelOrder
        sheetCancel.show(this.parentFragmentManager, CancelBottomSheetDialog::class.java.name)
      } else {
        apiConfirmOrder(item)
      }
    }

  private fun popUpMenuButton(view: View) {
    val list = OrderMenuModel().getAppointmentMenu(orderItem)
    if (list.isNotEmpty()) list.removeAt(0)
    val orderMenuView: View = LayoutInflater.from(baseActivity).inflate(R.layout.menu_order_button, null)
    val rvOrderMenu: RecyclerView? = orderMenuView.findViewById(R.id.rv_menu_order)
    rvOrderMenu?.apply {
      val adapterMenu = AppBaseRecyclerViewAdapter(baseActivity, list, this@AppointmentDetailsFragment)
      adapter = adapterMenu
    }
    mPopupWindow = PopupWindow(orderMenuView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true)
    mPopupWindow.showAsDropDown(view, 0, 0)
  }

  private fun apiCancelOrder(cancellingEntity: String, reasonText: String) {
    showProgress()
    viewModel?.cancelOrder(clientId, this.orderItem?._id, cancellingEntity)?.observeOnce(viewLifecycleOwner, Observer {
      if (it.error is NoNetworkException) {
        showShortToast(resources.getString(R.string.internet_connection_not_available))
        hideProgress()
        return@Observer
      }
      if (it.isSuccess()) {
        isRefresh = true
        val data = it as? OrderConfirmStatus
        if (reasonText.isNotEmpty()) {
          updateReason(resources.getString(R.string.order_cancel), UpdateExtraPropertyRequest.PropertyType.CANCELLATION.name, ExtraPropertiesOrder(cancellationRemark = reasonText))
        } else {
          apiGetOrderDetails(this.orderItem?._id ?: "")
          showLongToast(resources.getString(R.string.order_cancel))
        }
      } else {
        showLongToast(it.message())
        hideProgress()
      }
    })
  }

  private fun apiConfirmOrder(order: OrderItem) {
    showProgress()
    viewModel?.confirmOrder(clientId, order?._id)?.observeOnce(viewLifecycleOwner, Observer {
      hideProgress()
      if (it.error is NoNetworkException) {
        showShortToast(resources.getString(R.string.internet_connection_not_available))
        return@Observer
      }
      if (it.status == 200 || it.status == 201 || it.status == 202) {
        isRefresh = true
        val data = it as? OrderConfirmStatus
        showLongToast(getString(R.string.appointment_confirmed))
        orderItem?.Status = OrderSummaryModel.OrderStatus.ORDER_CONFIRMED.name
        setButtonStatus(orderItem!!)
      }
    })
  }

  private fun updateReason(message: String, type: String, extraPropertiesOrder: ExtraPropertiesOrder) {
    val propertyRequest = UpdateOrderNPropertyRequest(updateExtraPropertyType = type,
            existingKeyName = "", orderId = this.orderItem?._id, extraPropertiesOrder = extraPropertiesOrder)
    viewModel?.updateExtraPropertyOrder(clientId, requestCancel = propertyRequest)?.observeOnce(viewLifecycleOwner, {
      if (it.isSuccess()) showLongToast(message)
      apiGetOrderDetails(this.orderItem?._id ?: "")
    })
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
        clickActionOrderButton(orderMenu, this.orderItem!!)
      }
    }
  }

  private fun clickActionOrderButton(orderMenu: OrderMenuModel.MenuStatus, orderItem: OrderItem) {
    when (orderMenu) {
      /* OrderMenuModel.MenuStatus.CONFIRM_ORDER -> {
         val sheetConfirm = ConfirmBottomSheetDialog()
         sheetConfirm.setData(orderItem)
         sheetConfirm.onClicked = { apiConfirmOrder(it) }
         sheetConfirm.show(this.parentFragmentManager, ConfirmBottomSheetDialog::class.java.name)
       }
       OrderMenuModel.MenuStatus.REQUEST_PAYMENT -> {
         val sheetRequestPayment = RequestPaymentBottomSheetDialog()
         sheetRequestPayment.setData(orderItem)
         sheetRequestPayment.onClicked = {
           showProgress()
           sendPaymentLinkOrder(getString(R.string.payment_request_send))
         }
         sheetRequestPayment.show(this.parentFragmentManager, RequestPaymentBottomSheetDialog::class.java.name)
       }*/
      OrderMenuModel.MenuStatus.CANCEL_APPOINTMENT -> {
        val sheetCancel = CancelBottomSheetDialog()
        sheetCancel.setData(orderItem)
        sheetCancel.onClicked = this@AppointmentDetailsFragment::apiCancelOrder
        sheetCancel.show(this.parentFragmentManager, CancelBottomSheetDialog::class.java.name)
      }
      /*  OrderMenuModel.MenuStatus.MARK_PAYMENT_DONE -> markCodPaymentRequest()
        OrderMenuModel.MenuStatus.MARK_AS_DELIVERED -> {
          val sheetDelivered = DeliveredBottomSheetDialog()
          sheetDelivered.setData(orderItem)
          sheetDelivered.onClicked = { deliveredOrder(it) }
          sheetDelivered.show(this.parentFragmentManager, DeliveredBottomSheetDialog::class.java.name)
        }
        OrderMenuModel.MenuStatus.MARK_AS_SHIPPED -> {
          val sheetShipped = ShippedBottomSheetDialog()
          sheetShipped.setData(orderItem)
          sheetShipped.onClicked = { shippedOrder(it) }
          sheetShipped.show(this.parentFragmentManager, ShippedBottomSheetDialog::class.java.name)
        }*/
    }
  }


  /*  private fun checkStatusOrder(order: OrderItem) {
      if (order.isConfirmActionBtn()) {
        binding?.bottomBtn?.visible()
        binding?.buttonConfirmOrder?.setOnClickListener(this)
      } else binding?.bottomBtn?.gone()
      if (order.isCancelActionBtn()) {
        binding?.tvCancelOrder?.visible()
        binding?.tvCancelOrder?.setOnClickListener(this)
      } else binding?.tvCancelOrder?.gone()
    }

    private fun setAdapter(orderItems: ArrayList<ItemN>) {
      binding?.recyclerViewBookingDetails?.post {
        val adapter = AppBaseRecyclerViewAdapter(baseActivity, orderItems)
        binding?.recyclerViewBookingDetails?.adapter = adapter
      }
    }


    private fun buttonDisable(color: Int) {
      activity?.let {
        val newDrawable: Drawable? = binding?.buttonConfirmOrder?.background
        newDrawable?.let { it1 -> DrawableCompat.setTint(it1, ContextCompat.getColor(it, color)) }
        binding?.buttonConfirmOrder?.background = newDrawable
      }
    }

    fun getBundleData(): Bundle? {
      isRefresh?.let {
        val bundle = Bundle()
        bundle.putBoolean(IntentConstant.IS_REFRESH.name, it)
        return bundle
      }
      return null
    }


    private fun setOrderDetails(order: OrderItem) {
      binding?.orderType?.text = getStatusText(order)
      binding?.tvOrderStatus?.text = order.PaymentDetails?.status()
      val b = (PaymentDetailsN.STATUS.from(order.PaymentDetails?.Status ?: "") == PaymentDetailsN.STATUS.PENDING)
      if (b) binding?.tvOrderStatus?.setTextColor(getColor(R.color.watermelon_light_10))
      binding?.tvPaymentMode?.text = order.PaymentDetails?.methodValue()
      order.BillingDetails?.let { bill ->
        val currency = takeIf { bill.CurrencyCode.isNullOrEmpty().not() }?.let { bill.CurrencyCode?.trim() } ?: "INR"
        binding?.tvOrderAmount?.text = "$currency ${bill.AmountPayableByBuyer}"
      }
      val scheduleDate = order.firstItemForConsultation()?.scheduledStartDate()
      val dateApt = DateUtils.parseDate(scheduleDate, DateUtils.FORMAT_SERVER_DATE, DateUtils.FORMAT_SERVER_TO_LOCAL_2)
      binding?.bookingDate?.text = if (dateApt.isNullOrEmpty().not()) {
        dateApt
      } else {
        DateUtils.parseDate(order.CreatedOn, DateUtils.FORMAT_SERVER_DATE, DateUtils.FORMAT_SERVER_TO_LOCAL_2, timeZone = TimeZone.getTimeZone("IST"))
      }
      // customer details
      binding?.tvCustomerName?.text = order.BuyerDetails?.ContactDetails?.FullName?.trim()
      binding?.tvCustomerAddress?.text = order.BuyerDetails?.getAddressFull()

      binding?.tvCustomerContactNumber?.paintFlags?.or(Paint.UNDERLINE_TEXT_FLAG)?.let { binding?.tvCustomerContactNumber?.setPaintFlags(it) }
      binding?.tvCustomerEmail?.paintFlags?.or(Paint.UNDERLINE_TEXT_FLAG)?.let { binding?.tvCustomerEmail?.setPaintFlags(it) }
      binding?.tvCustomerContactNumber?.text = order.BuyerDetails?.ContactDetails?.PrimaryContactNumber?.trim()

      if (order.BuyerDetails?.ContactDetails?.PrimaryContactNumber?.trim()?.let { !checkValidMobile(it) }!!)
        binding?.tvCustomerContactNumber?.setTextColor(getColor(R.color.watermelon_light_10))
      if (order.BuyerDetails.ContactDetails.EmailId.isNullOrEmpty().not()) {
        binding?.tvCustomerEmail?.text = order.BuyerDetails.ContactDetails.EmailId?.trim()
        if (!checkValidEmail(order.BuyerDetails.ContactDetails.EmailId!!.trim())) binding?.tvCustomerEmail?.setTextColor(getColor(R.color.watermelon_light_10))
      } else binding?.tvCustomerEmail?.isGone = true


      // shipping details
      var shippingCost = 0.0
      var salePrice = 0.0
      var currency = "INR"
      order.Items?.forEachIndexed { index, item ->
        shippingCost += item.Product?.ShippingCost ?: 0.0
        salePrice += item.product().price() - item.product().discountAmount()
        if (index == 0) currency = takeIf { item.Product?.CurrencyCode.isNullOrEmpty().not() }
            ?.let { item.Product?.CurrencyCode?.trim() } ?: "INR"
      }
      binding?.tvTotalOrderAmount?.text = "Total Amount: $currency $salePrice"

    }

    private fun getStatusText(order: OrderItem): String? {
      val statusValue = OrderStatusValue.fromStatusAppointment(order.status())?.value
      return when (OrderSummaryModel.OrderStatus.ORDER_CANCELLED.name) {
        order.status().toUpperCase(Locale.ROOT) -> {
          return if (order.PaymentDetails?.status()?.toUpperCase(Locale.ROOT) == PaymentDetailsN.STATUS.CANCELLED.name) {
            OrderStatusValue.ESCALATED_2.value
          } else statusValue.plus(order.cancelledText())
        }
        else -> statusValue
      }
    }

    override fun onClick(v: View) {
      super.onClick(v)
      when (v) {
        binding?.btnBusiness -> showBottomSheetDialog()
        binding?.buttonConfirmOrder -> apiConfirmOrder()
        binding?.tvCancelOrder -> cancelOrderDialog()
        binding?.tvCustomerContactNumber -> {
          if (orderItem?.BuyerDetails?.ContactDetails?.PrimaryContactNumber?.trim()?.let { checkValidMobile(it) }!!)
            openDialer()
          else
            showShortToast(getString(R.string.phone_invalid_format_error))

        }
        binding?.tvCustomerEmail -> {
          if (orderItem?.BuyerDetails?.ContactDetails?.EmailId?.trim()?.let { checkValidEmail(it) }!!) {
            openEmailApp()
          } else {
            showShortToast(getString(R.string.email_invalid_format_error))
          }
        }
      }
    }

    private fun openEmailApp() {
      val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
          "mailto", orderItem?.BuyerDetails?.ContactDetails?.EmailId?.trim(), null))
      startActivity(emailIntent)
    }

    private fun openDialer() {
      val intent = Intent(Intent.ACTION_DIAL)
      intent.data = (Uri.parse("tel:${orderItem?.BuyerDetails?.ContactDetails?.PrimaryContactNumber?.trim()}"))
      startActivity(intent)
    }

    private fun checkValidEmail(email: String): Boolean {
      return Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$").matcher(email).find()
    }

    private fun checkValidMobile(mobile: String): Boolean {
      return Pattern.compile("^[+]?[0-9]{10,12}\$").matcher(mobile).find()
    }

    private fun cancelOrderDialog() {
      MaterialAlertDialogBuilder(baseActivity)
          .setTitle(getString(R.string.cancel_appointment_confirmation_message))
          .setNeutralButton(getString(R.string.no)) { dialog, _ ->
            dialog.dismiss()
          }
          .setPositiveButton(getString(R.string.yes)) { dialog, which ->
            apiCancelOrder()
            dialog.dismiss()
          }
          .show()
    }

    private fun apiCancelOrder() {
      showProgress()
      viewModel?.cancelOrder(clientId, orderItem?._id, OrderItem.CancellingEntity.SELLER.name)?.observeOnce(viewLifecycleOwner, Observer {
        hideProgress()
        if (it.error is NoNetworkException) {
          showShortToast(resources.getString(R.string.internet_connection_not_available))
          return@Observer
        }
        if (it.status == 200 || it.status == 201 || it.status == 202) {
          val data = it as? OrderConfirmStatus
          data?.let { d -> showLongToast(getString(R.string.the_appointment_has_been_cancelled)) }
          refreshStatus(OrderSummaryModel.OrderStatus.ORDER_CANCELLED)
        } else showLongToast(it.message())
      })
    }

    private fun apiConfirmOrder() {
      showProgress()
      viewModel?.confirmOrder(clientId, orderItem?._id)?.observeOnce(viewLifecycleOwner, Observer {
        hideProgress()
        if (it.error is NoNetworkException) {
          showShortToast(resources.getString(R.string.internet_connection_not_available))
          return@Observer
        }
        if (it.status == 200 || it.status == 201 || it.status == 202) {
          val data = it as? OrderConfirmStatus
          showLongToast(getString(R.string.appointment_confirmed))
          refreshStatus(OrderSummaryModel.OrderStatus.ORDER_CONFIRMED)
        } else showLongToast(it.message())
      })
    }

    private fun refreshStatus(statusOrder: OrderSummaryModel.OrderStatus) {
      isRefresh = true
      orderItem?.Status = statusOrder.name
      orderItem?.let { binding?.orderType?.text = getStatusText(it) }
      orderItem?.let { checkStatusOrder(it) }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
      super.onCreateOptionsMenu(menu, inflater)
      val item: MenuItem = menu.findItem(R.id.menu_item_share)
      item.actionView.findViewById<CustomButton>(R.id.button_share).setOnClickListener {
        showLongToast("Coming soon..")
      }
    }


    private fun showBottomSheetDialog() {
      showLongToast("Coming soon..")
  //    locationsBottomSheetDialog = LocationBottomSheetDialog()
  //    locationsBottomSheetDialog?.onDoneClicked = { clickDeliveryItem(it) }
  //    locationsBottomSheetDialog?.setList(serviceLocationsList)
  //    locationsBottomSheetDialog?.show(this.parentFragmentManager, DeliveryBottomSheetDialog::class.java.name)
    }

    private fun clickDeliveryItem(list: LocationsModel?) {
      serviceLocationsList.forEach { it.isSelected = (it.serviceOptionSelectedName == list?.serviceOptionSelectedName) }
    }*/
}