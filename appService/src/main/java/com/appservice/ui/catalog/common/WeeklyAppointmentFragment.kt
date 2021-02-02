package com.appservice.ui.catalog.common

import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.IntentConstant
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.FragmentStaffTimingBinding
import com.appservice.holder.WeeklyAppointmentViewHolder
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.staffs.model.StaffDetailsResult
import com.appservice.staffs.model.StaffTimingAddUpdateRequest
import com.appservice.staffs.ui.viewmodel.StaffViewModel
import com.appservice.utils.cast
import com.framework.extensions.observeOnce
import java.util.*
import kotlin.collections.ArrayList

class WeeklyAppointmentFragment : AppBaseFragment<FragmentStaffTimingBinding, StaffViewModel>(), RecyclerItemClickListener {
    private var isEdit: Boolean? = null
    private var staffData: StaffDetailsResult? = null

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
        staffData = arguments?.getSerializable(IntentConstant.STAFF_DATA.name) as? StaffDetailsResult
        isEdit = (staffData != null && staffData?.id.isNullOrEmpty())


    }

    private fun setPreviousData() {
//       defaultTimings = staffData?.timings as ArrayList<AppointmentModel>
        defaultTimings.forEachIndexed { index, model ->
            defaultTimings[index].timeSlots = staffData?.timings?.get(index)?.timeSlots!!
//            val viewHolder = adapter.cast<WeeklyAppointmentViewHolder>()
//            viewHolder?.addTimeSlots(defaultTimings.get(index))

        }
        adapter.notifyDataSetChanged()
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
        if (isEdit == true) {
            setPreviousData()
        }
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
                if (isEdit == true) {
                    updateStaffTimings()
                }
            }
        }
    }

    private fun addStaffTimings() {
        showProgress(getString(R.string.staff_timing_add))
        viewModel?.addStaffTiming(StaffTimingAddUpdateRequest(staffId = staffData?.id, defaultTimings))?.observeOnce(viewLifecycleOwner, {
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

    private fun updateStaffTimings() {
        showProgress(getString(R.string.staff_timing_add))
        viewModel?.updateStaffTiming(StaffTimingAddUpdateRequest(staffId = staffData?.id, defaultTimings))?.observeOnce(viewLifecycleOwner, {
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