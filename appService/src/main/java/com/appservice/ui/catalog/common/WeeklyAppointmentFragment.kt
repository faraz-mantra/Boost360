package com.appservice.ui.catalog.common

import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.FragmentStaffTimingBinding
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.staffs.ui.viewmodel.StaffViewModel
import com.appservice.ui.catalog.RequestWeeklyAppointment
import com.framework.models.BaseViewModel
import io.reactivex.rxkotlin.Observables
import java.util.*
import kotlin.collections.ArrayList

class WeeklyAppointmentFragment : AppBaseFragment<FragmentStaffTimingBinding, StaffViewModel>(), RecyclerItemClickListener {

    private var requestWeeklyAppointment: RequestWeeklyAppointment? = null
    private lateinit var defaultTimings: ArrayList<AppointmentModel>
//    private val timingsItem: ArrayList<TimingsItem> = ArrayList()
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

    override fun onCreateView() {
        super.onCreateView()
        this.defaultTimings = AppointmentModel.getDefaultTimings()
        this.adapter = AppBaseRecyclerViewAdapter(
                activity = baseActivity,
                list = defaultTimings,
                itemClickListener = this@WeeklyAppointmentFragment
        )
        binding!!.rvStaffTiming.adapter = adapter
        viewModel?.getStaffTimings()?.observe(viewLifecycleOwner, androidx.lifecycle.Observer{
            adapter.updateList(it)
        })
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

    private fun applyOnAllDays(data: AppointmentModel){
        if(data.isAppliedOnAllDays){
            applyOnAllDaysTurnedOn(data)
        }else{
            applyOnAllDaysTurnedOff(data)
        }
    }

    private fun applyOnAllDaysTurnedOn(data: AppointmentModel){
        for (i in 1 until defaultTimings.size){
            val item = defaultTimings.get(i);
            item.isTurnedOn = true;
            item.isDataAppliedOnMyDay = true;
            item.timeSlots = ArrayList<TimeSlot>()
            for(t in data.timeSlots!!){
                item.timeSlots?.add(t);
            }
        }
    }

    private fun applyOnAllDaysTurnedOff(data: AppointmentModel){
        for (i in 1 until defaultTimings.size){
            val item = defaultTimings.get(i);
            item.isTurnedOn = false
            item.isDataAppliedOnMyDay = false
            item.timeSlots = ArrayList<TimeSlot>()
        }
    }
}