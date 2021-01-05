package com.appservice.ui.staffs.ui.details.timing

import android.os.Bundle
import android.text.Html
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentStaffTimingBinding
import com.appservice.ui.staffs.ui.details.timing.adapter.RecyclerSessionAdapter
import com.appservice.ui.staffs.ui.details.timing.adapter.RecyclerSessionAdapter.RecyclerItemClick
import com.framework.models.BaseViewModel

class StaffTimingFragment : AppBaseFragment<FragmentStaffTimingBinding, BaseViewModel>(), RecyclerItemClick {
    override fun onToggle() {}
    override fun onAddSession() {}
    override fun onApplyAllDays() {}

    companion object {
        fun newInstance(): StaffTimingFragment {
            val args = Bundle()
            val fragment = StaffTimingFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_staff_timing
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        binding!!.ctvTextHeader.text = Html.fromHtml(getString(R.string.clinic_businesses_hour))
        binding!!.rvStaffTiming.adapter = RecyclerSessionAdapter(this, requireActivity())
    }
}