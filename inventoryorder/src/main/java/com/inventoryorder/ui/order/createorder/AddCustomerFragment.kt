package com.inventoryorder.ui.order.createorder

import android.os.Bundle
import android.view.View
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.databinding.FragmentAddCustomerBinding
import com.inventoryorder.model.orderRequest.Address
import com.inventoryorder.model.orderRequest.BuyerDetails
import com.inventoryorder.model.orderRequest.ContactDetails
import com.inventoryorder.model.orderRequest.OrderInitiateRequest
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.FragmentContainerOrderActivity
import com.inventoryorder.ui.startFragmentOrderActivity
import com.inventoryorder.utils.WebEngageController
import java.lang.StringBuilder

class AddCustomerFragment : BaseInventoryFragment<FragmentAddCustomerBinding>() {

  private var createOrderRequest = OrderInitiateRequest()
  private var totalPrice = 0.0

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): AddCustomerFragment {
      val fragment = AddCustomerFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun onCreateView() {
    super.onCreateView()
    fpTag?.let { WebEngageController.trackEvent("Clicked on Add Customer", "ORDERS", it) }

    setOnClickListener(binding?.vwNext, binding?.textAddCustomerGstin, binding?.tvRemove, binding?.textGoBack)

    createOrderRequest = arguments?.getSerializable("order_req") as OrderInitiateRequest
    totalPrice = arguments?.getSerializable("total") as Double


    /*binding?.checkboxAddressSame?.setOnCheckedChangeListener { p0, isChecked ->
      binding?.lytShippingAddress?.visibility = if (isChecked) View.GONE else View.VISIBLE
    }*/
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding?.vwNext -> {
       onNextTapped()
      }

      binding?.textAddCustomerGstin -> {

        if (binding?.lytCustomerGstn?.visibility == View.GONE) {
          binding?.textAddCustomerGstin?.visibility = View.GONE
          binding?.lytCustomerGstn?.visibility = View.VISIBLE
        }
      }

      binding?.tvRemove -> {
        binding?.textAddCustomerGstin?.visibility = View.VISIBLE
        binding?.lytCustomerGstn?.visibility = View.GONE
      }

      binding?.textGoBack -> {
        (context as FragmentContainerOrderActivity).finish()
      }
    }
  }

  private fun onNextTapped() {

    val name = binding?.editCustomerName?.text ?: ""
    val email = binding?.editCustomerEmail?.text ?: ""
    val phone = binding?.editCustomerPhone?.text ?: ""
    val address = binding?.layoutBillingAddr?.editAddress?.text ?: ""
    val city = binding?.layoutBillingAddr?.editCity?.text ?: ""
    val state = binding?.layoutBillingAddr?.editState?.text ?: ""
    val pinCode = binding?.layoutBillingAddr?.editPin?.text ?: ""

    val gstNo = binding?.editGstin?.text ?: ""

    if (name.isEmpty()) {
      showShortToast("Customer Name cannot be empty")
      return
    }

    if (email.isEmpty()) {
      showShortToast("Customer Email cannot be empty")
      return
    }

    if (phone.isEmpty()) {
      showShortToast("Customer phone cannot be empty")
      return
    }

    if (address.isEmpty()) {
      showShortToast("Customer address cannot be empty")
      return
    }

    if (city.isEmpty()) {
      showShortToast("Customer city cannot be empty")
      return
    }

    if (state.isEmpty()) {
      showShortToast("Customer state cannot be empty")
      return
    }

    if (pinCode.isEmpty()) {
      showShortToast("Customer pincode cannot be empty")
      return
    }

    var contactDetails = ContactDetails(fullName = name.toString(),
            emailId = email.toString(), primaryContactNumber = phone.toString())

    var addrStr = StringBuilder()
    addrStr.append(address)
    if (city.isNullOrEmpty().not()) addrStr.append(", $city")
    if (state.isNullOrEmpty().not()) addrStr.append(", $state")
    if (pinCode.isNullOrEmpty().not()) addrStr.append(", $pinCode")

    var billingAddress = Address("$addrStr")
    var buyerDetails = BuyerDetails(contactDetails = contactDetails, address = billingAddress)

    createOrderRequest.buyerDetails = buyerDetails

    var bundle = Bundle()
    bundle.putSerializable("order_req", createOrderRequest)
    bundle.putDouble("total", totalPrice)
    startFragmentOrderActivity(FragmentType.BILLING_DETAIL, bundle)
  }
}