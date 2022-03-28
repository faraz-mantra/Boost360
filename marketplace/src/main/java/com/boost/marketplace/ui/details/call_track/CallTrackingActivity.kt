package com.boost.marketplace.ui.details.call_track

import com.boost.marketplace.R
import com.boost.marketplace.adapter.NumberListAdapter
import com.boost.marketplace.base.AppBaseActivity
import com.boost.marketplace.databinding.ActivityCallTrackingBinding
import com.boost.marketplace.ui.details.domain.*


class CallTrackingActivity : AppBaseActivity<ActivityCallTrackingBinding, CallTrackViewModel>() {
    lateinit var numberListAdapter: NumberListAdapter

    override fun getLayout(): Int {
        return R.layout.activity_call_tracking
    }

    companion object {
        fun newInstance() = CallTrackingActivity()
    }


    override fun getViewModelClass(): Class<CallTrackViewModel> {
        return CallTrackViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()


        binding?.help?.setOnClickListener {
            val dialogCard = CallTrackingHelpBottomSheet()
            dialogCard.show(this.supportFragmentManager, CustomDomainHelpBottomSheet::class.java.name)
        }
        binding?.btnSelectNumber?.setOnClickListener {
            val dialogCard = SelectedNumberBottomSheet()
            dialogCard.show(this.supportFragmentManager, SelectedNumberBottomSheet::class.java.name)
        }

    }
}