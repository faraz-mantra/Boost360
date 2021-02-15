package com.inventoryorder.ui.order.createorder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.inventoryorder.R
import com.inventoryorder.databinding.FragmentOrderDetailBinding
import com.inventoryorder.databinding.FragmentOrderPlacedBinding
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.order.OrderInvoiceFragment

class OrderPlacedFragment : BaseInventoryFragment<FragmentOrderPlacedBinding>() {

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): OrderPlacedFragment {
            val fragment = OrderPlacedFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView() {
        super.onCreateView()


    }
}