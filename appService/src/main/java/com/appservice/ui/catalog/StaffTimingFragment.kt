package com.appservice.ui.catalog

import android.os.Bundle
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.FragmentStaffTimingBinding
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.staffs.model.StaffTimingModel
import com.framework.models.BaseViewModel

class StaffTimingFragment : AppBaseFragment<FragmentStaffTimingBinding, BaseViewModel>(), RecyclerItemClickListener {

    private lateinit var adapter: AppBaseRecyclerViewAdapter<StaffTimingModel>

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
        this.adapter = AppBaseRecyclerViewAdapter(
                activity = baseActivity,
                list = StaffTimingModel().getDefaultTimings(),
                itemClickListener = this@StaffTimingFragment
        )
        binding!!.rvStaffTiming.adapter = adapter
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        when (actionType) {
            RecyclerViewActionType.TOGGLE_STATE_CHANGED.ordinal -> {
                adapter.notifyDataSetChanged()
            }
        }

    }
}