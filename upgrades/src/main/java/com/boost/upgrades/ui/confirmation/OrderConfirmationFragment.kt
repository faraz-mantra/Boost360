package com.boost.upgrades.ui.confirmation

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.biz2.nowfloats.boost.updates.base_class.BaseFragment

import com.boost.upgrades.R
import com.boost.upgrades.UpgradeActivity
import kotlinx.android.synthetic.main.order_confirmation_fragment.*

class OrderConfirmationFragment : BaseFragment() {

    companion object {
        fun newInstance() = OrderConfirmationFragment()
    }

    private lateinit var viewModel: OrderConfirmationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.order_confirmation_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(OrderConfirmationViewModel::class.java)

        back_button.setOnClickListener {
            (activity as UpgradeActivity).popFragmentFromBackStack()
        }

        check_activation_status.setOnClickListener {

        }

        order_needs_help.setOnClickListener {

        }


    }

}
