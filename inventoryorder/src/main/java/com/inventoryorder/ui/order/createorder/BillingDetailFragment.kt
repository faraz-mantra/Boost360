package com.inventoryorder.ui.order.createorder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import com.framework.exceptions.NoNetworkException
import com.framework.extensions.observeOnce
import com.google.android.material.textview.MaterialTextView
import com.inventoryorder.R
import com.inventoryorder.constant.AppConstant
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.constant.IntentConstant
import com.inventoryorder.constant.RecyclerViewActionType
import com.inventoryorder.databinding.FragmentBillingDetailBinding
import com.inventoryorder.model.PreferenceData
import com.inventoryorder.model.order.orderbottomsheet.BottomSheetOptionsItem
import com.inventoryorder.model.order.orderbottomsheet.OrderBottomSheet
import com.inventoryorder.model.orderRequest.ContactDetails
import com.inventoryorder.model.orderRequest.ItemsItem
import com.inventoryorder.model.orderRequest.OrderInitiateRequest
import com.inventoryorder.model.ordersdetails.OrderItem
import com.inventoryorder.model.ordersdetails.PaymentDetailsN
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import com.inventoryorder.recyclerView.RecyclerItemClickListener
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.FragmentContainerOrderActivity
import com.inventoryorder.ui.order.sheetOrder.*
import com.inventoryorder.ui.startFragmentOrderActivity
import com.inventoryorder.utils.WebEngageController
import java.lang.reflect.Method


class BillingDetailFragment : BaseInventoryFragment<FragmentBillingDetailBinding>(), RecyclerItemClickListener {

  private var productList : ArrayList<ItemsItem> ?= null
  private var itemsAdapter: AppBaseRecyclerViewAdapter<ItemsItem>? = null
  private var layoutManagerN: LinearLayoutManager? = null
  private var createOrderRequest = OrderInitiateRequest()
  private var totalPrice = 0.0
  private var deliveryFee = 0.0
  private var selectedPaymentStatusOption = 0
  private var selectedDeliveryType : String = OrderItem.OrderMode.PICKUP.name
  var orderBottomSheet = OrderBottomSheet()
  //private var selectedPaymentMode : String = OrderItem.OrderMode.PICKUP.name

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

    createOrderRequest = arguments?.getSerializable(IntentConstant.ORDER_REQUEST.name) as OrderInitiateRequest
    totalPrice = arguments?.getSerializable(IntentConstant.TOTAL_PRICE.name) as Double

    setUpData()
    preparePaymentStatusOptions()

    setOnClickListener(binding?.ivOptions, binding?.tvDeliveryType, binding?.tvPaymentMode,
            binding?.tvPaymentStatus, binding?.buttonConfirmOrder, binding?.buttonGoBack,
            binding?.textAddDeliveryFee, binding?.textAddDeliveryFeeEdit)
  }

  private fun setUpData() {

    setUpContactDetailsInfo()

    binding?.layoutOrderShippingAddress?.tvShippingAddress?.text = createOrderRequest?.buyerDetails?.address?.addressLine ?: ""
    binding?.textAmount?.text = totalPrice.toString()
    binding?.textTotalPayableAmount?.text = totalPrice.toString()
    binding?.layoutOrderShippingAddress?.textAddrTitle?.text = getString(R.string.billing_address)
   // binding?.tvGst?.text = (createOrderRequest?.gstCharges ?: 0.0).toString()

   // productList = ArrayList()
   // productList?.addAll(createOrderRequest.items?.toList()!!)
    setAdapterOrderList()
  }

  private fun setUpContactDetailsInfo() {
    binding?.tvName?.text = createOrderRequest?.buyerDetails?.contactDetails?.fullName ?: ""
    binding?.tvPhone?.text = createOrderRequest?.buyerDetails?.contactDetails?.primaryContactNumber ?: ""

    if (createOrderRequest?.buyerDetails?.contactDetails?.emailId?.isNullOrEmpty()?.not() == true) {
      binding?.tvEmail?.text = createOrderRequest?.buyerDetails?.contactDetails?.emailId ?: ""
    } else {
      binding?.tvEmail?.visibility = View.GONE
    }
  }

  override fun onClick(v: View) {
    when(v) {

      binding?.ivOptions -> {
        showPopUp(binding?.ivOptions!!)
      }

      binding?.tvDeliveryType -> {
        val deliveryTypeBottomSheetDialog = DeliveryTypeBottomSheetDialog(selectedDeliveryType)
        deliveryTypeBottomSheetDialog.onClicked = {onDeliveryTypeSelected(it)}
        deliveryTypeBottomSheetDialog.show(this.parentFragmentManager, DeliveryTypeBottomSheetDialog::class.java.name)
      }

      binding?.tvPaymentMode -> {
       /* val paymentModeBottomSheetDialog = PaymentModeBottomSheetDialog()
        paymentModeBottomSheetDialog.show(this.parentFragmentManager, PaymentModeBottomSheetDialog::class.java.name)*/
      }

      binding?.tvPaymentStatus -> {
        val createOrderBottomSheetDialog = CreateOrderBottomSheetDialog(orderBottomSheet)
        createOrderBottomSheetDialog.onClicked = this::onPaymentStatusSelected
        createOrderBottomSheetDialog.show(this.parentFragmentManager, CreateOrderBottomSheetDialog::class.java.name)

/*        val deliveryTypeBottomSheetDialog = DeliveryTypeBottomSheetDialog()
        deliveryTypeBottomSheetDialog.show(this.parentFragmentManager, DeliveryTypeBottomSheetDialog::class.java.name)*/
      }

      binding?.buttonConfirmOrder -> {
        createOrderRequest.mode = selectedDeliveryType!!
        createOrderRequest.paymentDetails?.method = PaymentDetailsN.METHOD.COD.type
        createOrderRequest.sellerID = preferenceData?.fpTag.toString()
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
        (context as FragmentContainerOrderActivity).finish()
      }
    }
  }

  private fun preparePaymentStatusOptions() {
    orderBottomSheet.title = getString(R.string.str_payment_status)

    var optionsList = ArrayList<BottomSheetOptionsItem>()

    var bottomSheetOptionsItem2 = BottomSheetOptionsItem()
    bottomSheetOptionsItem2.title = getString(R.string.playment_already_received)
    bottomSheetOptionsItem2.description = getString(R.string.customer_paid_via_cash_card_upi)
    bottomSheetOptionsItem2.displayValue = getString(R.string.payment_received)
    bottomSheetOptionsItem2.serverValue = PaymentDetailsN.STATUS.INITIATED.name


    var bottomSheetOptionsItem1 = BottomSheetOptionsItem()
    bottomSheetOptionsItem1.title = getString(R.string.collect_later)
    bottomSheetOptionsItem1.description = getString(R.string.send_payment_to_customer)
    bottomSheetOptionsItem1.displayValue = getString(R.string.collect_later)
    bottomSheetOptionsItem1.isChecked = true
    bottomSheetOptionsItem1.serverValue = PaymentDetailsN.STATUS.PENDING.name

    optionsList.add(bottomSheetOptionsItem1)
    optionsList.add(bottomSheetOptionsItem2)
    orderBottomSheet.items = optionsList
  }

  private fun onPaymentStatusSelected(bottomSheetOptionsItem : BottomSheetOptionsItem, orderBottomSheet : OrderBottomSheet) {
    binding?.tvPaymentStatus?.text = bottomSheetOptionsItem?.displayValue
    createOrderRequest?.paymentDetails?.status = bottomSheetOptionsItem?.serverValue
    this.orderBottomSheet = orderBottomSheet
  }

  private fun onDeliveryTypeSelected(selectedType : String) {
    selectedDeliveryType = selectedType
    binding?.tvDeliveryType?.text = selectedType
  }

  private fun showAddDeliveryFeeDialog() {
    val addDeliveryFeeBottomSheetDialog = AddDeliveryFeeBottomSheetDialog(deliveryFee)
    addDeliveryFeeBottomSheetDialog.onClicked = { onDeliveryFeeAdded(it) }
    addDeliveryFeeBottomSheetDialog.show(this.parentFragmentManager, AddDeliveryFeeBottomSheetDialog::class.java.name)
  }

  private fun onDeliveryFeeAdded(value : Double) {

    if (value > 0) {

      deliveryFee = value
      binding?.textAddDeliveryFeeValue?.text = "${createOrderRequest?.items?.get(0)?.productDetails?.currencyCode} $value"
      binding?.textAddDeliveryFeeEdit?.visibility = View.VISIBLE
      binding?.textAddDeliveryFee?.visibility = View.GONE
      binding?.textAddDeliveryFeeValue?.visibility = View.VISIBLE
      binding?.textTotalPayableAmount?.text = (totalPrice + value).toString()
    } else if (value == 0.0) {
      deliveryFee = 0.0
      binding?.textAddDeliveryFeeEdit?.visibility = View.GONE
      binding?.textAddDeliveryFee?.visibility = View.VISIBLE
      binding?.textAddDeliveryFeeValue?.visibility = View.GONE
      binding?.textTotalPayableAmount?.text = totalPrice.toString()
    }
  }

  private fun setAdapterOrderList() {
    binding?.productRecycler?.apply {
      itemsAdapter = AppBaseRecyclerViewAdapter(baseActivity, createOrderRequest.items?.toCollection(ArrayList())!!, this@BillingDetailFragment)
      layoutManager = layoutManagerN
      adapter = itemsAdapter
      itemsAdapter?.runLayoutAnimation(this)
    }
  }

  private fun showPopUp(view: View?) {

    // inflate the layout of the popup window
    val inflater = LayoutInflater.from(baseActivity)
    val popupView: View = inflater.inflate(R.layout.popup_billing_details, null)

    // create the popup window
    val width = LinearLayout.LayoutParams.WRAP_CONTENT
    val height = LinearLayout.LayoutParams.WRAP_CONTENT
    val focusable = true // lets taps outside the popup also dismiss it
    val popupWindow = PopupWindow(popupView, width, height, focusable)

    val textEditCustomerInfo = popupWindow.contentView.findViewById<MaterialTextView>(R.id.text_edit_customer_info)
    val textEditCustomerAddr = popupWindow.contentView.findViewById<MaterialTextView>(R.id.text_edit_customer_addr)
    val pickAnotherCustomer = popupWindow.contentView.findViewById<MaterialTextView>(R.id.text_pick_customer)

    textEditCustomerInfo.setOnClickListener {
      val editCustomerInfoBottomSheetDialog = EditCustomerInfoBottomSheetDialog(createOrderRequest?.buyerDetails?.contactDetails!!)
      editCustomerInfoBottomSheetDialog.onClicked = {onCustomerInfo(it)}
      editCustomerInfoBottomSheetDialog.show(this.parentFragmentManager, EditCustomerInfoBottomSheetDialog::class.java.name)
      popupWindow.dismiss()
    }

    textEditCustomerAddr.setOnClickListener {
      val editCustomerAddressBottomSheetDialog = EditCustomerAddressBottomSheetDialog()
      editCustomerAddressBottomSheetDialog.show(this.parentFragmentManager, EditCustomerAddressBottomSheetDialog::class.java.name)
      popupWindow.dismiss()
    }

    pickAnotherCustomer.setOnClickListener {
      popupWindow.dismiss()
    }

    popupWindow.showAsDropDown(view, 0, -50)
  }

  private fun onCustomerInfo(contactDetails: ContactDetails) {
    createOrderRequest?.buyerDetails?.contactDetails = contactDetails
    setUpContactDetailsInfo()
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    if (actionType == RecyclerViewActionType.PRODUCT_SELECTED_ITEM_OPTIONS_REMOVE.ordinal) {
      createOrderRequest?.items?.removeAt(position)
      itemsAdapter?.notify(createOrderRequest?.items)
    }
  }

  private fun createOrder() {
    showProgress()
    viewModel?.postOrderInitiate(AppConstant.CLIENT_ID_2, createOrderRequest)?.observeOnce(viewLifecycleOwner, androidx.lifecycle.Observer {
      if (it.error is NoNetworkException) {
        hideProgress()
        showLongToast(resources.getString(R.string.internet_connection_not_available))
        return@Observer
      }
      if (it.isSuccess()) {
        hideProgress()
        startFragmentOrderActivity(FragmentType.ORDER_PLACED, Bundle())
      } else {
        hideProgress()
        showLongToast(if (it.message().isNotEmpty()) it.message() else getString(R.string.unable_to_create_order))
      }
    })
  }
}