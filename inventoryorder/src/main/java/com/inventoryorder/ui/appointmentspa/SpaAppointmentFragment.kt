package com.inventoryorder.ui.appointmentspa

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.inventoryorder.R
import com.inventoryorder.constant.FragmentType
import com.inventoryorder.databinding.FragmentSpaAppointmentBinding
import com.inventoryorder.ui.BaseInventoryFragment
import com.inventoryorder.ui.order.createorder.OrderPlacedFragment
import com.inventoryorder.ui.startFragmentOrderActivity

class SpaAppointmentFragment : BaseInventoryFragment<FragmentSpaAppointmentBinding>() {

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): SpaAppointmentFragment {
            val fragment = SpaAppointmentFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView() {
        super.onCreateView()

        setOnClickListener(binding?.buttonReviewDetails)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v) {
            binding?.buttonReviewDetails -> {
                startFragmentOrderActivity(FragmentType.REVIEW_SPA_DETAILS, Bundle())
            }
        }
    }
}