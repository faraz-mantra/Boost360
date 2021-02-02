package com.appservice.ui.catalog.common

import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.IntentConstant
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.FragmentStaffTimingBinding
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.staffs.model.StaffTimingAddUpdateRequest
import com.appservice.staffs.ui.viewmodel.StaffViewModel
import com.framework.extensions.observeOnce
import java.util.*
import kotlin.collections.ArrayList

class WeeklyAppointmentFragment : AppBaseFragment<FragmentStaffTimingBinding, StaffViewModel>(), RecyclerItemClickListener {
    private var staffId: String? = null
    private lateinit var defaultTimings: ArrayList<AppointmentModel>
    private lateinit var adapter: AppBaseRecyclerViewAdapter<AppointmentModel>
    companion object {
        fun newInstance(): WeeklyAppointmentFragment = WeeklyAppointmentFragment()
    }

    override fun getLayout(): Int {
        return R.layout.fragment_staff_timing
    }

    override fun getViewModelClass(): Class<StaffViewModel> {
        return StaffViewModel::class.java
    }

    private fun getBundleData() {
        staffId = arguments?.getSerializable(IntentConstant.STAFF_ID.name) as? String
    }

    override fun onCreateView() {
        super.onCreateView()
        getBundleData()
        setOnClickListener(binding?.btnSave)
        this.defaultTimings = AppointmentModel.getDefaultTimings()
        this.adapter = AppBaseRecyclerViewAdapter(
                activity = baseActivity,
                list = defaultTimings,
                itemClickListener = this@WeeklyAppointmentFragment
        )
        binding!!.rvStaffTiming.adapter = adapter

    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        when (actionType) {
            RecyclerViewActionType.TOGGLE_STATE_CHANGED.ordinal -> {
                adapter.notifyDataSetChanged()
            }
            RecyclerViewActionType.CHECK_BOX_APPLY_ALL.ordinal -> {
                  item as AppointmentModel
                applyOnAllDays(item);
                adapter.notifyDataSetChanged()
            }
            RecyclerViewActionType.ADD_SESSION.ordinal -> {
                adapter.notifyDataSetChanged()
            }
        }

    }

    private fun applyOnAllDays(data: AppointmentModel) {
        if (data.isAppliedOnAllDays) {
            applyOnAllDaysTurnedOn(data)
        } else {
            applyOnAllDaysTurnedOff(data)
        }
    }

    private fun applyOnAllDaysTurnedOn(data: AppointmentModel) {
        for (i in 1 until defaultTimings.size) {
            val item = defaultTimings[i];
            item.isTurnedOn = true;
            item.isViewEnabled = false
            item.isDataAppliedOnMyDay = true;
            item.timeSlots = ArrayList()
            for (t in data.timeSlots) {
                t.to = data.toTiming
                t.from = data.fromTiming
                item.timeSlots.add(t);
            }
        }
    }


    private fun applyOnAllDaysTurnedOff(data: AppointmentModel) {
        for (i in 1 until defaultTimings.size) {
            val item = defaultTimings[i];
            item.isTurnedOn = false
            item.isViewEnabled = true
            item.isDataAppliedOnMyDay = false
            item.timeSlots = ArrayList()
        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.btnSave -> {
                addStaffTimings()
            }
        }
    }

    private fun addStaffTimings() {
        showProgress(getString(R.string.staff_timing_add))
        viewModel?.addStaffTiming(StaffTimingAddUpdateRequest(staffId = staffId, defaultTimings))?.observeOnce(viewLifecycleOwner, androidx.lifecycle.Observer{
            when (it.status) {
                200 -> {
                    Log.v(getString(R.string.staff_timings), getString(R.string.staff_timings_added))
                    hideProgress()
                    finishAndGoBack()
                }
                else -> {
                    Log.v(getString(R.string.staff_timings), getString(R.string.something_went_wrong))
                }
            }
        })
    }

    private fun finishAndGoBack() {
        baseActivity.setResult(AppCompatActivity.RESULT_OK)
        baseActivity.finish()
    }

}