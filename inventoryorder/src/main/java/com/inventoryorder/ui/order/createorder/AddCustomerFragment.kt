package com.inventoryorder.ui.order.createorder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.inventoryorder.R
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.databinding.FragmentAddCustomerBinding
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.startFragmentActivity
import com.inventoryorder.utils.WebEngageController


class AddCustomerFragment : BaseInventoryFragment<FragmentAddCustomerBinding>() {

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

        setOnClickListener(binding?.vwNext)
    }


    override fun onClick(v: View) {
        super.onClick(v)
        when(v){
            binding?.vwNext->{startFragmentActivity(FragmentType.ADD_PRODUCT, Bundle())}
        }
    }

}