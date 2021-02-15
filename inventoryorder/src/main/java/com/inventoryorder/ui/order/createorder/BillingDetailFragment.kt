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
import com.inventoryorder.databinding.FragmentBillingDetailBinding
import com.inventoryorder.model.PreferenceData
import com.inventoryorder.model.orderRequest.ItemsItem
import com.inventoryorder.model.orderRequest.OrderInitiateRequest
import com.inventoryorder.recyclerView.AppBaseRecyclerViewAdapter
import com.inventoryorder.recyclerView.BaseRecyclerViewItem
import com.inventoryorder.recyclerView.RecyclerItemClickListener
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.FragmentContainerOrderActivity
import com.inventoryorder.ui.order.sheetOrder.*
import com.inventoryorder.ui.startFragmentOrderActivity
import com.inventoryorder.utils.WebEngageController


class BillingDetailFragment : BaseInventoryFragment<FragmentBillingDetailBinding>(), RecyclerItemClickListener {

  private var productList : ArrayList<ItemsItem> ?= null
  private var itemsAdapter: AppBaseRecyclerViewAdapter<ItemsItem>? = null
  private var layoutManagerN: LinearLayoutManager? = null
  private var createOrderRequest = OrderInitiateRequest()
  private var totalPrice = 0.0

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

    createOrderRequest = arguments?.getSerializable("order_req") as OrderInitiateRequest
    totalPrice = arguments?.getSerializable("total") as Double

    setUpData()
    setOnClickListener(binding?.ivOptions, binding?.tvDeliveryType, binding?.tvPaymentMode,
            binding?.tvPaymentStatus, binding?.buttonConfirmOrder, binding?.buttonGoBack, binding?.textAddDeliveryFee)
  }

  private fun setUpData() {

  /*  binding?.layoutOrderBillingAddress?.textAddrTitle?.text = getString(R.string.billing_address)
    binding?.layoutOrderBillingAddress?.imageLocation?.setImageDrawable(ContextCompat.getDrawable(this.requireActivity(), R.drawable.ic_billing_address))
*/

    binding?.tvName?.text = createOrderRequest?.buyerDetails?.contactDetails?.fullName ?: ""
    binding?.tvEmail?.text = createOrderRequest?.buyerDetails?.contactDetails?.emailId ?: ""
    binding?.tvPhone?.text = createOrderRequest?.buyerDetails?.contactDetails?.primaryContactNumber ?: ""
    binding?.layoutOrderShippingAddress?.tvShippingAddress?.text = createOrderRequest?.buyerDetails?.address?.addressLine ?: ""
    binding?.textAmount?.text = totalPrice.toString()
    binding?.textTotalPayableAmount?.text = totalPrice.toString()
   // binding?.tvGst?.text = (createOrderRequest?.gstCharges ?: 0.0).toString()

    productList = ArrayList()
    productList?.addAll(createOrderRequest.items?.toList()!!)
    setAdapterOrderList(productList!!)
  }

  override fun onClick(v: View) {
    when(v) {

      binding?.ivOptions -> {
        showPopUp(binding?.ivOptions!!)
      }

      binding?.tvDeliveryType -> {
        val deliveryTypeBottomSheetDialog = DeliveryTypeBottomSheetDialog()
        deliveryTypeBottomSheetDialog.show(this.parentFragmentManager, DeliveryTypeBottomSheetDialog::class.java.name)
      }

      binding?.tvPaymentMode -> {
        val paymentModeBottomSheetDialog = PaymentModeBottomSheetDialog()
        paymentModeBottomSheetDialog.show(this.parentFragmentManager, PaymentModeBottomSheetDialog::class.java.name)
      }

      binding?.tvPaymentStatus -> {
/*        val deliveryTypeBottomSheetDialog = DeliveryTypeBottomSheetDialog()
        deliveryTypeBottomSheetDialog.show(this.parentFragmentManager, DeliveryTypeBottomSheetDialog::class.java.name)*/
      }

      binding?.buttonConfirmOrder -> {

        // createOrderRequest.mode = OrderItem.DeliveryMode.OFFLINE.name /*else OrderSummaryRequest.DeliveryMode.OFFLINE.name*/

        createOrderRequest.mode = "DELIVERY"
        createOrderRequest.sellerID = preferenceData?.fpTag.toString()
        createOrder()
      }

      binding?.buttonConfirmOrder -> {
        (context as FragmentContainerOrderActivity).finish()
      }

      binding?.textAddDeliveryFee -> {
        val addDeliveryFeeBottomSheetDialog = AddDeliveryFeeBottomSheetDialog()
        addDeliveryFeeBottomSheetDialog.show(this.parentFragmentManager, AddDeliveryFeeBottomSheetDialog::class.java.name)
      }

      binding?.buttonGoBack -> {
        (context as FragmentContainerOrderActivity).finish()
      }
    }
  }

  private fun setAdapterOrderList(list: ArrayList<ItemsItem>) {
    binding?.productRecycler?.apply {
      itemsAdapter = AppBaseRecyclerViewAdapter(baseActivity, list, this@BillingDetailFragment)
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
      val editCustomerInfoBottomSheetDialog = EditCustomerInfoBottomSheetDialog()
      editCustomerInfoBottomSheetDialog.show(this.parentFragmentManager, EditCustomerInfoBottomSheetDialog::class.java.name)
      popupWindow.dismiss()
    }

    textEditCustomerAddr.setOnClickListener {
      val editCustomerAddressBottomSheetDialog = EditCustomerAddressBottomSheetDialog()
      editCustomerAddressBottomSheetDialog.show(this.parentFragmentManager, EditCustomerAddressBottomSheetDialog::class.java.name)
      popupWindow.dismiss()
    }

    pickAnotherCustomer.setOnClickListener {
      val bottomSheetRemoveItem = RemoveItemBottomSheetDialog()
      bottomSheetRemoveItem.show(this.parentFragmentManager, RemoveItemBottomSheetDialog::class.java.name)

      popupWindow.dismiss()
    }

    popupWindow.showAsDropDown(view, 0, -50)
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {

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
        showShortToast("Success")
      } else {
        hideProgress()
        showLongToast(if (it.message().isNotEmpty()) it.message() else "Cannot create a booking at this time. Please try later.")
      }
    })
  }
}