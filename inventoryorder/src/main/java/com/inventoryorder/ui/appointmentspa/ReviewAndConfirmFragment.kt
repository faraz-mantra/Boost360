package com.inventoryorder.ui.appointmentspa

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.inventoryorder.R
import com.inventoryorder.databinding.FragmentReviewAndConfirmBinding
import com.inventoryorder.databinding.FragmentSpaAppointmentBinding
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.order.sheetOrder.AddDeliveryFeeBottomSheetDialog

class ReviewAndConfirmFragment : BaseInventoryFragment<FragmentReviewAndConfirmBinding>() {

    private var serviceFee = 0.0

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): ReviewAndConfirmFragment {
            val fragment = ReviewAndConfirmFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView() {
        setOnClickListener(binding?.textAdd)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v) {
            binding?.textAdd -> {
                showAddServiceFeeDialog()
            }
        }
    }

    private fun showAddServiceFeeDialog() {
        val addDeliveryFeeBottomSheetDialog = AddDeliveryFeeBottomSheetDialog(serviceFee)
        addDeliveryFeeBottomSheetDialog.onClicked = { onServiceFeeAdded(it) }
        addDeliveryFeeBottomSheetDialog.show(this.parentFragmentManager, AddDeliveryFeeBottomSheetDialog::class.java.name)
    }

    private fun onServiceFeeAdded(fee : Double) {

    }
}