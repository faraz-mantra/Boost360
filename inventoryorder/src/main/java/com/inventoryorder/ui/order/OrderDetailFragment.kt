package com.inventoryorder.ui.order

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isGone
import androidx.lifecycle.Observer
import com.framework.exceptions.NoNetworkException
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.utils.DateUtils
import com.framework.utils.DateUtils.FORMAT_SERVER_DATE
import com.framework.utils.DateUtils.FORMAT_SERVER_TO_LOCAL_2
import com.framework.views.customViews.CustomTextView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.inventoryorder.R
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.databinding.FragmentOrderDetailBinding
import com.inventoryorder.model.OrderConfirmStatus
import com.inventoryorder.model.bottomsheet.DeliveryModel
import com.inventoryorder.model.ordersdetails.ItemN
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.model.ordersdetails.PaymentDetailsN
import com.inventoryorder.model.ordersummary.OrderStatusValue
import com.inventoryorder.model.ordersummary.OrderSummaryModel
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.rest.response.order.OrderDetailResponse
import com.inventoryorder.rest.response.order.ProductResponse
import com.inventoryorder.ui.BaseInventoryFragment
import java.text.DecimalFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class OrderDetailFragment : BaseInventoryFragment<FragmentOrderDetailBinding>() {

  private var deliverySheetDialog: DeliveryBottomSheetDialog? = null
  private var orderItem: OrderItem? = null
  private var deliveryList = DeliveryModel().getData()
  private var isRefresh: Boolean? = null
  private var productList: ArrayList<ProductResponse>? = null

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): OrderDetailFragment {
      val fragment = OrderDetailFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    arguments?.getString(IntentConstant.ORDER_ID.name)?.let { apiGetOrderDetails(it) }
    setOnClickListener(binding?.tvCustomerContactNumber, binding?.tvCustomerEmail) //binding?.btnPickUp,
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

  private fun errorUi(message: String) {
    hideProgress()
    binding?.mainView?.gone()
    binding?.error?.visible()
    binding?.error?.text = message
  }

  private fun setDetails(order: OrderItem) {
    setToolbarTitle("${getString(R.string.orders)}: #${order.ReferenceNumber}")
    checkStatusOrder(order)
    setOrderDetails(order)
    order.Items?.let { setAdapter(it) }
  }

  private fun checkStatusOrder(order: OrderItem) {
    if (order.isConfirmActionBtn()) {
      binding?.buttonConfirmOrder?.visible()
      binding?.buttonConfirmOrder?.setOnClickListener(this)
    } else binding?.buttonConfirmOrder?.gone()
    if (order.isCancelActionBtn()) {
      binding?.tvCancelOrder?.visible()
      binding?.tvCancelOrder?.setOnClickListener(this)
    } else binding?.tvCancelOrder?.gone()
  }

  private fun setAdapter(orderItems: ArrayList<ItemN>) {
    binding?.recyclerViewOrderDetails?.post {
      val adapter = AppBaseRecyclerViewAdapter(baseActivity, orderItems)
      binding?.recyclerViewOrderDetails?.adapter = adapter
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    val item: MenuItem = menu.findItem(R.id.menu_item_invoice)
    item.actionView.findViewById<CustomTextView>(R.id.tvInvoice).setOnClickListener {
      showLongToast("Coming soon..")
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

  private fun buttonDisable(color: Int) {
    activity?.let {
      val newDrawable: Drawable? = binding?.buttonConfirmOrder?.background
      newDrawable?.let { it1 -> DrawableCompat.setTint(it1, ContextCompat.getColor(it, color)) }
      binding?.buttonConfirmOrder?.background = newDrawable
    }
  }

  private fun setOrderDetails(order: OrderItem) {
    binding?.orderType?.text = getStatusText(order)
    OrderStatusValue.fromStatusOrder(order.status())?.icon?.let { binding?.statusIcon?.setImageResource(it) }
    binding?.tvOrderStatus?.text = order.PaymentDetails?.status()
    binding?.tvPaymentMode?.text = order.PaymentDetails?.methodValue()
    binding?.tvDeliveryType?.text = order.deliveryType()
    binding?.tvItemCount?.visibility = if (order.Items.isNullOrEmpty().not()) View.VISIBLE else View.GONE
    binding?.tvItemCount?.text = if (order.Items?.size ?: 0 > 1) "(${order.Items?.size} items)" else "(${order.Items?.size} item)"

    order.BillingDetails?.let { bill ->
      val currency = takeIf { bill.CurrencyCode.isNullOrEmpty().not() }?.let { bill.CurrencyCode?.trim() } ?: "INR"
      binding?.tvOrderAmount?.text = "$currency ${DecimalFormat("##,##,##0").format(bill.AmountPayableByBuyer)}"
//            binding?.tvOrderAmount?.text = "$currency ${bill.AmountPayableByBuyer}"
    }
    binding?.orderDate?.text = DateUtils.parseDate(order.UpdatedOn, FORMAT_SERVER_DATE, FORMAT_SERVER_TO_LOCAL_2, timeZone = TimeZone.getTimeZone("IST"))

    // customer details
    binding?.tvCustomerName?.text = order.BuyerDetails?.ContactDetails?.FullName?.trim()
    binding?.tvCustomerDetail?.text = order.BuyerDetails?.getPhoneEmailFull()
    binding?.userAddress?.tvShippingAddress?.text = order.BuyerDetails?.address()?.addressLine1()
    binding?.userAddress?.tvBillingAddress?.text = order.BuyerDetails?.address()?.addressLine1()

//        binding?.tvCustomerContactNumber?.paintFlags?.or(Paint.UNDERLINE_TEXT_FLAG)?.let { binding?.tvCustomerContactNumber?.setPaintFlags(it) }
//        binding?.tvCustomerEmail?.paintFlags?.or(Paint.UNDERLINE_TEXT_FLAG)?.let { binding?.tvCustomerEmail?.setPaintFlags(it) }
//        binding?.tvCustomerContactNumber?.text = order.BuyerDetails?.ContactDetails?.PrimaryContactNumber?.trim()

    if (order.BuyerDetails?.ContactDetails?.PrimaryContactNumber?.trim()?.let { !checkValidMobile(it) }==true)
      binding?.tvCustomerContactNumber?.setTextColor(getColor(R.color.watermelon_light_10))
    if (order.BuyerDetails?.ContactDetails?.EmailId.isNullOrEmpty().not()) {
//            binding?.tvCustomerEmail?.text = order.BuyerDetails.ContactDetails.EmailId?.trim()
      if (!checkValidEmail(order.BuyerDetails?.ContactDetails?.EmailId!!.trim())) binding?.tvCustomerEmail?.setTextColor(getColor(R.color.watermelon_light_10))
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

    binding?.tvShippingCost?.text = "Shipping Cost: $currency ${DecimalFormat("##,##,##0").format(shippingCost)}"
    binding?.tvTotalOrderAmount?.text = "Total Amount: $currency ${DecimalFormat("##,##,##0").format(salePrice + shippingCost)}"

//        binding?.tvShippingCost?.text = "Shipping Cost: $currency $shippingCost"
//        binding?.tvTotalOrderAmount?.text = "Total Amount: $currency ${salePrice + shippingCost}"

  }

  private fun getStatusText(order: OrderItem): String? {
    val statusValue = OrderStatusValue.fromStatusOrder(order.status())?.value
    return when (OrderSummaryModel.OrderStatus.ORDER_CANCELLED.name) {
      order.status().toUpperCase(Locale.ROOT) -> {
        return if (order.PaymentDetails?.status()?.toUpperCase(Locale.ROOT) == PaymentDetailsN.STATUS.CANCELLED.name) {
          OrderStatusValue.ABANDONED_1.value
        } else statusValue.plus(order.cancelledText())
      }
      else -> statusValue
    }
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
//            binding?.btnPickUp -> showBottomSheetDialog()
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
//      binding?.tvCustomerContactNumber -> openDialer()
//      binding?.tvCustomerEmail -> openEmailApp()
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
    MaterialAlertDialogBuilder(requireContext())
        .setTitle(getString(R.string.cancel_order_confirmation_message))
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
        data?.let { d -> showLongToast(d.Message as String?) }
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
        showLongToast(getString(R.string.order_confirmed))
        refreshStatus(OrderSummaryModel.OrderStatus.ORDER_CONFIRMED)
      } else showLongToast(it.message())
    })
  }

  private fun refreshStatus(statusOrder: OrderSummaryModel.OrderStatus) {
    isRefresh = true
    orderItem?.Status = statusOrder.name
    orderItem?.let { orderItem ->
      binding?.orderType?.text = getStatusText(orderItem)
      OrderStatusValue.fromStatusOrder(orderItem.status())?.icon?.let { binding?.statusIcon?.setImageResource(it) }
    }
    orderItem?.let { checkStatusOrder(it) }
  }

  private fun showBottomSheetDialog() {
    showLongToast("Coming soon..")
//    deliverySheetDialog = DeliveryBottomSheetDialog()
//    deliverySheetDialog?.onDoneClicked = { clickDeliveryItem(it) }
//    deliverySheetDialog?.setList(deliveryList)
//    deliverySheetDialog?.show(this@OrderDetailFragment.parentFragmentManager, DeliveryBottomSheetDialog::class.java.name)
  }

  private fun clickDeliveryItem(deliveryItem: DeliveryModel?) {
    deliveryList.forEach { it.isSelected = (it.deliveryOptionSelectedName == deliveryItem?.deliveryOptionSelectedName) }
  }
}