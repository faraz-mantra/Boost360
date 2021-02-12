package com.inventoryorder.ui.order.createorder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import com.inventoryorder.R
import com.inventoryorder.databinding.FragmentBillingDetailBinding
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.order.sheetOrder.CancelBottomSheetDialog
import com.inventoryorder.ui.order.sheetOrder.DeliveredBottomSheetDialog
import com.inventoryorder.ui.order.sheetOrder.DeliveryTypeBottomSheetDialog
import com.inventoryorder.ui.order.sheetOrder.PaymentModeBottomSheetDialog
import com.inventoryorder.utils.WebEngageController

class BillingDetailFragment : BaseInventoryFragment<FragmentBillingDetailBinding>() {

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
    setUpData()
    setOnClickListener(binding?.ivOptions, binding?.tvDeliveryType, binding?.tvPaymentMode, binding?.tvOrderStatus)
  }

  private fun setUpData() {
    binding?.layoutOrderBillingAddress?.textAddrTitle?.text = getString(R.string.billing_address)
    binding?.layoutOrderBillingAddress?.imageLocation?.setImageDrawable(ContextCompat.getDrawable(this.requireActivity(), R.drawable.ic_billing_address))
  }

  override fun onClick(v: View) {
    when(v) {

      binding?.ivOptions -> {

      }

      binding?.tvDeliveryType -> {
        val deliveryTypeBottomSheetDialog = DeliveryTypeBottomSheetDialog()
        deliveryTypeBottomSheetDialog.show(this.parentFragmentManager, DeliveryTypeBottomSheetDialog::class.java.name)
      }

      binding?.tvPaymentMode -> {
        val paymentModeBottomSheetDialog = PaymentModeBottomSheetDialog()
        paymentModeBottomSheetDialog.show(this.parentFragmentManager, PaymentModeBottomSheetDialog::class.java.name)
      }

      binding?.tvOrderStatus -> {
        val deliveryTypeBottomSheetDialog = DeliveryTypeBottomSheetDialog()
        deliveryTypeBottomSheetDialog.show(this.parentFragmentManager, DeliveryTypeBottomSheetDialog::class.java.name)
      }
    }
  }

  private fun showPopUp(view: View) {
    val popupMenu = PopupMenu(activity, view)
    val inflater = LayoutInflater.from(baseActivity)
    val popupView = inflater.inflate(R.layout.popup_billing_details, null)
    popupMenu.show()

    popupMenu.setOnMenuItemClickListener {
      when (it.itemId) {

      }
      true
    }
  }
}