package com.appservice.ui.catalog

import android.os.Bundle
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentStaffTimingBinding
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.staffs.model.StaffTimingModel
import com.framework.models.BaseViewModel

class StaffTimingFragment : AppBaseFragment<FragmentStaffTimingBinding, BaseViewModel>(), RecyclerItemClickListener {

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
        binding!!.ctvTextHeader.text = ""
        binding!!.rvStaffTiming.adapter = AppBaseRecyclerViewAdapter(baseActivity, StaffTimingModel().getDefaultTimings(), this@StaffTimingFragment)
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
//        Toast.makeText(requireActivity(), "hiii", Toast.LENGTH_SHORT).show()

    }
}