package com.appservice.ui.catalog.common

import android.os.Bundle
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.FragmentStaffTimingBinding
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.ui.catalog.RequestWeeklyAppointment
import com.appservice.ui.catalog.Time
import com.appservice.ui.catalog.TimingsItem
import com.framework.models.BaseViewModel
import java.util.*
import kotlin.collections.ArrayList

class WeeklyAppointmentFragment : AppBaseFragment<FragmentStaffTimingBinding, BaseViewModel>(), RecyclerItemClickListener {

    private var requestWeeklyAppointment: RequestWeeklyAppointment? = null
    private lateinit var defaultTimings: ArrayList<AppointmentModel>
    private val timingsItem: ArrayList<TimingsItem> = ArrayList()
    private lateinit var adapter: AppBaseRecyclerViewAdapter<AppointmentModel>

    companion object {
        fun newInstance(): WeeklyAppointmentFragment {
            val args = Bundle()
            val fragment = WeeklyAppointmentFragment()
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
        this.defaultTimings = AppointmentModel().getDefaultTimings()
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
                val (_, _, isAppliedOnAllDays, _, _, _, _) = item as AppointmentModel
                defaultTimings.forEach { if (it != item) it.isTurnedOn = isAppliedOnAllDays }
                adapter.notifyDataSetChanged()
            }
            RecyclerViewActionType.ADD_SESSION.ordinal -> {
                val (day, _, isAppliedOnAllDays, _, toTiming, fromTiming,_) = item as AppointmentModel
                when (isAppliedOnAllDays) {
                    true -> {
                        defaultTimings.forEach { timingsItem.add(TimingsItem(Time(toTiming, fromTiming), it.day)) }
                        this.requestWeeklyAppointment = RequestWeeklyAppointment(duration = 45, timings = timingsItem, serviceId = "")
                    }
                    false -> {
                        timingsItem.add(TimingsItem(Time(toTiming,fromTiming),day))
                          this.requestWeeklyAppointment = RequestWeeklyAppointment(duration = 45,timingsItem,serviceId = "")
                    }
                }
            }
        }

    }
}