package com.inventoryorder.ui.order.createorder

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import com.framework.extensions.observeOnce
import com.google.android.material.textview.MaterialTextView
import com.inventoryorder.R
import com.inventoryorder.constant.AppConstant
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.FragmentBillingDetailBinding
import com.inventoryorder.model.OrderInitiateResponse
import com.inventoryorder.model.order.orderbottomsheet.BottomSheetOptionsItem
import com.inventoryorder.model.order.orderbottomsheet.OrderBottomSheet
import com.inventoryorder.model.orderRequest.*
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.model.ordersdetails.PaymentDetailsN
import com.inventoryorder.model.ordersummary.OrderSummaryRequest
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import com.inventoryorder.recyclerView.RecyclerItemClickListener
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.FragmentContainerOrderActivity
import com.inventoryorder.ui.appointmentSpa.create.AddDeliveryFeeBottomSheetDialog
import com.inventoryorder.ui.appointmentSpa.create.CreateOrderBottomSheetDialog
import com.inventoryorder.ui.order.sheetOrder.*
import com.inventoryorder.ui.startFragmentOrderActivity
import com.inventoryorder.utils.WebEngageController
import java.math.RoundingMode
import java.text.DecimalFormat

class BillingDetailFragment : BaseInventoryFragment<FragmentBillingDetailBinding>(), RecyclerItemClickListener {

  private var itemsAdapter: AppBaseRecyclerViewAdapter<ItemsItem>? = null
  private var layoutManagerN: LinearLayoutManager? = null
  private var createOrderRequest = OrderInitiateRequest()

  private var totalPrice = 0.0
  private var totalPricePayable = 0.0
  private var totalPriceDiscount = 0.0
  private var deliveryFee = 0.0
  private var selectedDeliveryType: String = OrderItem.OrderMode.PICKUP.name
  private var orderBottomSheet = OrderBottomSheet()
  private var deliveryTypeBottomSheet = OrderBottomSheet()
  private var shouldFinish = false
  private var shouldReInitiate = false
  private var paymentStatus: String = PaymentDetailsN.STATUS.PENDING.name

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): BillingDetailFragment {
      val fragment = BillingDetailFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    fpTag?.let { WebEngageController.trackEvent("Clicked on Add Customer", "ORDERS", it) }
    layoutManagerN = LinearLayoutManager(baseActivity)
    setOnClickListener(binding?.ivOptions, binding?.tvDeliveryType, binding?.tvPaymentMode, binding?.tvPaymentStatus, binding?.buttonConfirmOrder,
        binding?.buttonGoBack, binding?.textAddDeliveryFee, binding?.textAddDeliveryFeeEdit, binding?.tvAddMore)
    createOrderRequest = arguments?.getSerializable(IntentConstant.ORDER_REQUEST.name) as OrderInitiateRequest
    setUpData()
    getTotalPayableAmount()
    preparePaymentStatusOptions()
    prepareDeliveryTypeOptions()
  }

  fun getBundleData(): Bundle {
    val bundle = Bundle()
    bundle.putBoolean(IntentConstant.SHOULD_FINISH.name, shouldFinish)
    bundle.putBoolean(IntentConstant.SHOULD_REINITIATE.name, shouldReInitiate)
    bundle.putSerializable(IntentConstant.ORDER_REQUEST.name, createOrderRequest)
    return bundle
  }

  private fun setUpData() {
    setUpContactDetailsInfo()
    setUpAddress()
    setAdapterOrderList()
  }

  private fun setUpContactDetailsInfo() {
    binding?.tvName?.text = createOrderRequest.buyerDetails?.contactDetails?.fullName ?: ""
    binding?.tvPhone?.text = createOrderRequest.buyerDetails?.contactDetails?.primaryContactNumber ?: ""
    binding?.layoutOrderShippingAddress?.textAddrTitle?.text = getString(R.string.billing_address)
    if (createOrderRequest.buyerDetails?.contactDetails?.emailId?.isNotEmpty() == true) {
      binding?.tvEmail?.text = createOrderRequest.buyerDetails?.contactDetails?.emailId ?: ""
      binding?.tvEmail?.visibility = View.VISIBLE
    } else {
      binding?.tvEmail?.visibility = View.GONE
    }
  }

  private fun setUpAddress() {
    val addrStr = StringBuilder()
    addrStr.append(createOrderRequest.buyerDetails?.address?.addressLine)
    if (createOrderRequest.buyerDetails?.address?.city.isNullOrEmpty().not()) addrStr.append(", ${createOrderRequest.buyerDetails?.address?.city}")
    if (createOrderRequest.buyerDetails?.address?.region.isNullOrEmpty().not()) addrStr.append(", ${createOrderRequest.buyerDetails?.address?.region}")
    if (createOrderRequest.buyerDetails?.address?.zipcode.isNullOrEmpty().not()) addrStr.append(", ${createOrderRequest.buyerDetails?.address?.zipcode}")
    binding?.layoutOrderShippingAddress?.tvShippingAddress?.text = addrStr
  }

  override fun onClick(v: View) {
    when (v) {
      binding?.ivOptions -> showPopUp(binding?.ivOptions!!)
      binding?.tvDeliveryType -> {
        val createOrderBottomSheetDialog = CreateOrderBottomSheetDialog(deliveryTypeBottomSheet)
        createOrderBottomSheetDialog.onClicked = this::onDeliveryTypeSelected
        createOrderBottomSheetDialog.show(this.parentFragmentManager, CreateOrderBottomSheetDialog::class.java.name)
      }

      binding?.tvPaymentMode -> {

      }

      binding?.tvPaymentStatus -> {
        val createOrderBottomSheetDialog = CreateOrderBottomSheetDialog(orderBottomSheet)
        createOrderBottomSheetDialog.onClicked = this::onPaymentStatusSelected
        createOrderBottomSheetDialog.show(this.parentFragmentManager, CreateOrderBottomSheetDialog::class.java.name)
      }

      binding?.buttonConfirmOrder -> {
        val currency = createOrderRequest.items?.firstOrNull()?.productDetails?.getCurrencyCodeValue() ?: "INR"
        val paymentDetails = PaymentDetails(method = PaymentDetailsN.METHOD.COD.type, status = paymentStatus)
        val shippingDetails = ShippingDetails(shippedBy = ShippingDetails.ShippedBy.SELLER.name,
            deliveryMode = OrderSummaryRequest.DeliveryMode.OFFLINE.name, shippingCost = deliveryFee, currencyCode = currency)

        createOrderRequest.mode = selectedDeliveryType
        createOrderRequest.paymentDetails = paymentDetails
        createOrderRequest.shippingDetails = shippingDetails
        createOrderRequest.sellerID = preferenceData?.fpTag
        createOrderRequest.shippingDetails?.shippingCost = deliveryFee
        createOrder()
      }

      binding?.textAddDeliveryFee -> {
        showAddDeliveryFeeDialog()
      }

      binding?.textAddDeliveryFeeEdit -> {
        showAddDeliveryFeeDialog()
      }

      binding?.buttonGoBack -> {
        (context as? FragmentContainerOrderActivity)?.onBackPressed()
      }

      binding?.tvAddMore -> {
        shouldFinish = true
        (context as? FragmentContainerOrderActivity)?.onBackPressed()
      }
    }
  }

  private fun preparePaymentStatusOptions() {
    orderBottomSheet.title = getString(R.string.str_payment_status)

    val optionsList = ArrayList<BottomSheetOptionsItem>()

    val bottomSheetOptionsItem2 = BottomSheetOptionsItem()
    bottomSheetOptionsItem2.title = getString(R.string.playment_already_received)
    bottomSheetOptionsItem2.description = getString(R.string.customer_paid_via_cash_card_upi)
    bottomSheetOptionsItem2.displayValue = getString(R.string.payment_received)
    bottomSheetOptionsItem2.serverValue = PaymentDetailsN.STATUS.INITIATED.name

    val bottomSheetOptionsItem1 = BottomSheetOptionsItem()
    bottomSheetOptionsItem1.title = getString(R.string.collect_later)
    bottomSheetOptionsItem1.description = getString(R.string.send_payment_to_customer)
    bottomSheetOptionsItem1.displayValue = getString(R.string.collect_later)
    bottomSheetOptionsItem1.isChecked = true
    bottomSheetOptionsItem1.serverValue = PaymentDetailsN.STATUS.PENDING.name

    optionsList.add(bottomSheetOptionsItem1)
    optionsList.add(bottomSheetOptionsItem2)
    orderBottomSheet.items = optionsList
  }

  private fun prepareDeliveryTypeOptions() {
    deliveryTypeBottomSheet.title = getString(R.string.delivery_type)
    deliveryTypeBottomSheet.decription = getString(R.string.select_how_you_want_customer_to_receive_this_order)

    val optionsList = ArrayList<BottomSheetOptionsItem>()

    val bottomSheetOptionsItem1 = BottomSheetOptionsItem()
    bottomSheetOptionsItem1.title = getString(R.string.store_pickup)
    bottomSheetOptionsItem1.description = getString(R.string.ask_customer_to_pick_the_order_from_your_store)
    bottomSheetOptionsItem1.displayValue = getString(R.string.store_pickup)
    bottomSheetOptionsItem1.isChecked = true
    bottomSheetOptionsItem1.serverValue = OrderItem.OrderMode.PICKUP.name

    val bottomSheetOptionsItem2 = BottomSheetOptionsItem()
    bottomSheetOptionsItem2.title = getString(R.string.home_delivery)
    bottomSheetOptionsItem2.description = getString(R.string.deliver_to_customer_via_your_shipping_partner)
    bottomSheetOptionsItem2.displayValue = getString(R.string.home_delivery)
    bottomSheetOptionsItem2.serverValue = OrderItem.OrderMode.DELIVERY.name

    optionsList.add(bottomSheetOptionsItem1)
    optionsList.add(bottomSheetOptionsItem2)
    deliveryTypeBottomSheet.items = optionsList
  }

  private fun onDeliveryTypeSelected(bottomSheetOptionsItem: BottomSheetOptionsItem, orderBottomSheet: OrderBottomSheet) {
    binding?.tvDeliveryType?.text = bottomSheetOptionsItem.displayValue
    selectedDeliveryType = bottomSheetOptionsItem.serverValue!!
    binding?.tvDeliveryType?.text = bottomSheetOptionsItem.displayValue!!
    // paymentStatus = bottomSheetOptionsItem?.serverValue!!
    this.deliveryTypeBottomSheet = orderBottomSheet
  }

  private fun onPaymentStatusSelected(bottomSheetOptionsItem: BottomSheetOptionsItem, orderBottomSheet: OrderBottomSheet) {
    binding?.tvPaymentStatus?.text = bottomSheetOptionsItem.displayValue
    createOrderRequest.paymentDetails?.status = bottomSheetOptionsItem.serverValue
    paymentStatus = bottomSheetOptionsItem.serverValue!!
    this.orderBottomSheet = orderBottomSheet
  }

  private fun showAddDeliveryFeeDialog() {
    val addDeliveryFeeBottomSheetDialog = AddDeliveryFeeBottomSheetDialog(deliveryFee)
    addDeliveryFeeBottomSheetDialog.onClicked = { onDeliveryFeeAdded(it) }
    addDeliveryFeeBottomSheetDialog.show(this.parentFragmentManager, AddDeliveryFeeBottomSheetDialog::class.java.name)
  }

  private fun onDeliveryFeeAdded(value: Double) {
    val currencyCode = createOrderRequest.items?.firstOrNull()?.productDetails?.getCurrencyCodeValue() ?: "INR"
    if (value > 0) {
      deliveryFee = value
      binding?.textAddDeliveryFeeValue?.text = "$currencyCode $value"
      binding?.textAddDeliveryFeeEdit?.visibility = View.VISIBLE
      binding?.textAddDeliveryFee?.visibility = View.GONE
      binding?.textAddDeliveryFeeValue?.visibility = View.VISIBLE
      binding?.textTotalPayableAmount?.text = "$currencyCode ${totalPricePayable + value}"
    } else if (value == 0.0) {
      deliveryFee = 0.0
      binding?.textAddDeliveryFeeEdit?.visibility = View.GONE
      binding?.textAddDeliveryFee?.visibility = View.VISIBLE
      binding?.textAddDeliveryFeeValue?.visibility = View.GONE
      binding?.textTotalPayableAmount?.text = "$currencyCode $totalPricePayable"
    }
    binding?.textGstAmount?.text = "$currencyCode ${calculateGST(totalPricePayable + deliveryFee)}"
  }

  private fun setAdapterOrderList() {
    if (createOrderRequest.items.isNullOrEmpty().not()) {
      binding?.productRecycler?.apply {
        itemsAdapter = AppBaseRecyclerViewAdapter(baseActivity, createOrderRequest.items!!.toCollection(ArrayList()), this@BillingDetailFragment)
        layoutManager = layoutManagerN
        adapter = itemsAdapter
      }
    }
  }

  private fun showPopUp(view: View?) {
    val inflater = LayoutInflater.from(baseActivity)
    val popupView: View = inflater.inflate(R.layout.popup_billing_details, null)
    val width = LinearLayout.LayoutParams.WRAP_CONTENT
    val height = LinearLayout.LayoutParams.WRAP_CONTENT
    val focusable = true
    val popupWindow = PopupWindow(popupView, width, height, focusable)
    val textEditCustomerInfo = popupWindow.contentView.findViewById<MaterialTextView>(R.id.text_edit_customer_info)
    val textEditCustomerAddr = popupWindow.contentView.findViewById<MaterialTextView>(R.id.text_edit_customer_addr)
    val pickAnotherCustomer = popupWindow.contentView.findViewById<MaterialTextView>(R.id.text_pick_customer)

    textEditCustomerInfo.setOnClickListener {
      val editCustomerInfoBottomSheetDialog = EditCustomerInfoBottomSheetDialog(createOrderRequest.buyerDetails?.contactDetails!!)
      editCustomerInfoBottomSheetDialog.onClicked = { onCustomerInfo(it) }
      editCustomerInfoBottomSheetDialog.show(this.parentFragmentManager, EditCustomerInfoBottomSheetDialog::class.java.name)
      popupWindow.dismiss()
    }
    textEditCustomerAddr.setOnClickListener {
      val editCustomerAddressBottomSheetDialog = EditCustomerAddressBottomSheetDialog(createOrderRequest.buyerDetails?.address!!)
      editCustomerAddressBottomSheetDialog.onClicked = { onCustomerAddress(it) }
      editCustomerAddressBottomSheetDialog.show(this.parentFragmentManager, EditCustomerAddressBottomSheetDialog::class.java.name)
      popupWindow.dismiss()
    }

    pickAnotherCustomer.setOnClickListener {
      popupWindow.dismiss()
    }

    popupWindow.showAsDropDown(view, 0, -50)
  }

  private fun onCustomerInfo(contactDetails: ContactDetails) {
    createOrderRequest.buyerDetails?.contactDetails = contactDetails
    setUpContactDetailsInfo()
  }

  private fun onCustomerAddress(address: Address) {
    createOrderRequest.buyerDetails?.address = address
    setUpAddress()
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    if (actionType == RecyclerViewActionType.PRODUCT_SELECTED_ITEM_OPTIONS_REMOVE.ordinal) {
      val removeItemBottomSheetDialog = RemoveItemBottomSheetDialog()
      removeItemBottomSheetDialog.onClicked = {
        if (it) {
          if (createOrderRequest.items?.size ?: 0 > position) {
            createOrderRequest.items?.removeAt(position)
            itemsAdapter?.notify(createOrderRequest.items)
            getTotalPayableAmount()
          } else showShortToast(getString(R.string.you_cannot_remove_all_items))
        }
      }
      removeItemBottomSheetDialog.show(this.parentFragmentManager, RemoveItemBottomSheetDialog::class.java.name)
    }
  }

  private fun getTotalPayableAmount() {
    totalPrice = 0.0
    totalPricePayable = 0.0
    totalPriceDiscount = 0.0
    createOrderRequest.items?.forEach {
      totalPrice += it.getActualPriceAmount()
      totalPricePayable += it.getPayablePriceAmount()
      totalPriceDiscount += it.getTotalDisPriceAmount()
    }
    updateData()
  }

  private fun updateData() {
    val currencyCode = createOrderRequest.items?.firstOrNull()?.productDetails?.getCurrencyCodeValue() ?: "INR"
    binding?.textItemTotalAmount?.text = "$currencyCode $totalPrice"
    binding?.textItemTotalDiscount?.text = "-$currencyCode $totalPriceDiscount"
    binding?.textGstAmount?.text = "$currencyCode ${calculateGST(totalPricePayable + deliveryFee)}"
    binding?.textTotalPayableAmount?.text = "$currencyCode $totalPricePayable"
  }

  private fun createOrder() {
    showProgress()
    viewModel?.postAppointment(AppConstant.CLIENT_ID_2, createOrderRequest)?.observeOnce(viewLifecycleOwner, {
      if (it.isSuccess()) {
        hideProgress()
        val orderInitiateResponse = (it as? OrderInitiateResponse)
        apiConfirmOrder(orderInitiateResponse = orderInitiateResponse!!)
      } else {
        hideProgress()
        showLongToast(if (it.message().isNotEmpty()) it.message() else getString(R.string.unable_to_create_order))
      }
    })
  }

  private fun apiConfirmOrder(orderInitiateResponse: OrderInitiateResponse) {
    showProgress()
    viewModel?.confirmOrder(preferenceData?.clientId, orderInitiateResponse.data._id)?.observeOnce(viewLifecycleOwner, {
      hideProgress()
      if (it.isSuccess()) {
        val bundle = Bundle()
        bundle.putSerializable(IntentConstant.ORDER_ID.name, orderInitiateResponse.data._id)
        bundle.putSerializable(IntentConstant.PREFERENCE_DATA.name, preferenceData)
        startFragmentOrderActivity(FragmentType.ORDER_PLACED, bundle, isResult = true)
      } else {
        showLongToast(if (it.message().isNotEmpty()) it.message() else getString(R.string.unable_to_create_order))
      }
    })
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
      val bundle = data?.extras?.getBundle(IntentConstant.RESULT_DATA.name)
      shouldReInitiate = bundle?.getBoolean(IntentConstant.SHOULD_REINITIATE.name) ?: false
      shouldFinish = bundle?.getBoolean(IntentConstant.SHOULD_FINISH.name) ?: false
      if (shouldReInitiate || shouldFinish) (context as? FragmentContainerOrderActivity)?.onBackPressed()
    }
  }

  private fun calculateGST(amount: Double): Double {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.CEILING
    return df.format((amount - (df.format(amount / AppConstant.GST_PERCENTAGE).toDouble()))).toDouble()
  }
}