package com.appservice.ui.catalog.catalogService.service

import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.IntentConstant
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.FragmentStaffTimingBinding
import com.appservice.model.servicev1.ServiceModelV1
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.staffs.model.StaffDetailsResult
import com.appservice.staffs.ui.viewmodel.StaffViewModel
import com.appservice.ui.catalog.ServiceModel
import com.appservice.ui.catalog.TimeSlot
import com.appservice.viewmodel.ServiceViewModelV1
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ServiceWeeklyScheduleFragment : AppBaseFragment<FragmentStaffTimingBinding, ServiceViewModelV1>(), RecyclerItemClickListener {

    private var isEdit: Boolean? = null
    //private var serviceModel: ServiceModelV1? = null
    private var serviceModel : ArrayList<ServiceModel> ?= null

    private lateinit var defaultTimings: ArrayList<ServiceModel>
    private lateinit var adapter: AppBaseRecyclerViewAdapter<ServiceModel>

    companion object {
        fun newInstance(): ServiceWeeklyScheduleFragment = ServiceWeeklyScheduleFragment()
    }

    override fun getLayout(): Int {
        return R.layout.fragment_staff_timing
    }

    override fun getViewModelClass(): Class<ServiceViewModelV1> {
        return ServiceViewModelV1::class.java
    }

    private fun getBundleData() {
        serviceModel = arguments?.getSerializable(IntentConstant.TIMESLOT.name) as? ArrayList<ServiceModel>
        when {
            serviceModel != null && serviceModel?.size != 0 -> {
                this.defaultTimings = serviceModel!!
            }
            else -> {

                this.defaultTimings = ServiceModel.getDefaultTimings()
            }
        }
    }

    override fun onCreateView() {
        super.onCreateView()
        getBundleData()
        setOnClickListener(binding?.btnSave)
        this.adapter = AppBaseRecyclerViewAdapter(
                activity = baseActivity,
                list = this.defaultTimings,
                itemClickListener = this@ServiceWeeklyScheduleFragment
        )
        binding!!.rvStaffTiming.adapter = adapter
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        when (actionType) {
            RecyclerViewActionType.TOGGLE_STATE_CHANGED.ordinal -> {
                adapter.notifyDataSetChanged()
            }
            RecyclerViewActionType.CHECK_BOX_APPLY_ALL.ordinal -> {
                item as ServiceModel
                applyOnAllDays(item);
                adapter.notifyDataSetChanged()
            }
            RecyclerViewActionType.ADD_SESSION.ordinal -> {
                adapter.notifyDataSetChanged()
            }
        }

    }

    private fun applyOnAllDays(data: ServiceModel) {
        if (data.isAppliedOnAllDays!!) {
            applyOnAllDaysTurnedOn(data)
        } else {
            applyOnAllDaysTurnedOff(data)
        }
    }

    private fun applyOnAllDaysTurnedOn(data: ServiceModel) {
        for (i in 1 until defaultTimings.size) {
            val item = defaultTimings[i];
            item.isTurnedOn = true;
            item.isViewEnabled = false
            item.isDataAppliedOnMyDay = true;
            item.timeSlots = data.timeSlots
        }
    }


    private fun applyOnAllDaysTurnedOff(data: ServiceModel) {
        for (i in 1 until defaultTimings.size) {
            val item = defaultTimings[i];
            item.isTurnedOn = false
            item.isViewEnabled = true
            item.isDataAppliedOnMyDay = false
            item.timeSlots = data.timeSlots
        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.btnSave -> {
                if (isValid()) {
                    finishAndGoBack()
                }
            }
        }
    }

    fun isValid(): Boolean {

        var i = 0
        this.defaultTimings.forEachIndexed { index, _ -> serviceModel?.forEachIndexed {pos, timeSlot ->

            if (timeSlot.isTurnedOn == true) {
                if (timeSlot.timeSlots?.from == timeSlot.timeSlots?.to) {
                    showLongToast("End time cannot be same as start time")
                    return false
                }

                if (convertAndCompareTime(timeSlot.timeSlots?.from!!, timeSlot.timeSlots?.to!!)) {
                    showLongToast("End time cannot be before start time")
                    return false
                }
            }
          }
        }

        return true
    }


    private fun finishAndGoBack() {
        // send staff data to the intent
        val intent = Intent();
        intent.putExtra(IntentConstant.TIMESLOT.name, defaultTimings);
        requireActivity().setResult(AppCompatActivity.RESULT_OK, intent);
        baseActivity.finish()
    }

    private fun convertAndCompareTime(startTime: String, endTime: String) : Boolean {
        val sdf = SimpleDateFormat("hh:mma")
        var d1 = Date()
        var d2 = Date()
        try {
            d1 = sdf.parse(startTime)
            d2 = sdf.parse(endTime)
        } catch (e : Exception) {}

        return d2.before(d1)
    }
}