package com.inventoryorder.ui.order.createorder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.inventoryorder.R
import com.inventoryorder.databinding.FragmentBillingDetailBinding
import com.inventoryorder.ui.BaseInventoryFragment
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

    }

}