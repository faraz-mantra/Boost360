package com.inventoryorder.ui.order.createorder

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.inventoryorder.R
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.constant.IntentConstant
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
  private var isRefresh: Boolean = false

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

    createOrderRequest = arguments?.getSerializable(IntentConstant.ORDER_REQUEST.name) as OrderInitiateRequest
    totalPrice = arguments?.getSerializable(IntentConstant.TOTAL_PRICE.name) as Double
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

 /* fun getBundleData(): Bundle? {
    isRefresh?.let {
      val bundle = Bundle()
      bundle.putBoolean(IntentConstant.IS_REFRESH.name, it)
      return bundle
    }
    return null
  }*/

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
      showShortToast(getString(R.string.customer_name_cannot_be_empty))
      return
    }

    if (phone.isEmpty()) {
      showShortToast(getString(R.string.customer_phone_cannot_be_empty))
      return
    }

    if (phone.length < 10) {
      showShortToast(getString(R.string.please_enter_valid_phone))
      return
    }

    if (email.isNullOrEmpty().not() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches().not()) {
      showShortToast(getString(R.string.please_enter_valid_email))
      return
    }


    if (address.isEmpty()) {
      showShortToast(getString(R.string.customer_address_cannot_be_empty))
      return
    }

    if (city.isEmpty()) {
      showShortToast(getString(R.string.customer_city_cannot_be_empty))
      return
    }

    if (state.isEmpty()) {
      showShortToast(getString(R.string.customer_state_cannot_be_empty))
      return
    }

    if (pinCode.isEmpty()) {
      showShortToast(getString(R.string.customer_pincode_cannot_be_empty))
      return
    }

    if (pinCode.length < 6) {
      showShortToast(getString(R.string.enter_valid_pincode))
      return
    }

    var contactDetails = ContactDetails(fullName = name.toString(),
            emailId = email.toString(), primaryContactNumber = phone.toString())

    /*var addrStr = StringBuilder()
    addrStr.append(address)
    if (city.isNullOrEmpty().not()) addrStr.append(", $city")
    if (state.isNullOrEmpty().not()) addrStr.append(", $state")
    if (pinCode.isNullOrEmpty().not()) addrStr.append(", $pinCode")*/

    var billingAddress = Address(address.toString(), city = city.toString(), region = state.toString(), zipcode = pinCode.toString())
    var buyerDetails = BuyerDetails(contactDetails = contactDetails, address = billingAddress)

    createOrderRequest.buyerDetails = buyerDetails

    var bundle = Bundle()
    bundle.putSerializable(IntentConstant.ORDER_REQUEST.name, createOrderRequest)
    bundle.putDouble(IntentConstant.TOTAL_PRICE.name, totalPrice)
    bundle.putSerializable(IntentConstant.PREFERENCE_DATA.name, arguments?.getSerializable(IntentConstant.PREFERENCE_DATA.name))
    startFragmentOrderActivity(FragmentType.BILLING_DETAIL, bundle, isResult = true)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)

    if (requestCode == 101 && resultCode == RESULT_OK) {
      val bundle = data?.extras?.getBundle(IntentConstant.RESULT_DATA.name)
      val shouldFinish = bundle?.getBoolean(IntentConstant.SHOULD_FINISH.name)
      if (shouldFinish != null && shouldFinish) {
        (context as FragmentContainerOrderActivity).onBackPressed()
      }
    }
  }
}