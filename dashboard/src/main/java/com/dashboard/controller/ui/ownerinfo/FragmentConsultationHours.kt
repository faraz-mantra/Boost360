package com.dashboard.controller.ui.ownerinfo

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dashboard.R
import com.dashboard.base.AppBaseFragment
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.databinding.FragmentConsultationHoursBinding
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
import com.dashboard.viewmodel.DashboardViewModel
import java.util.*
import kotlin.collections.ArrayList

class FragmentConsultationHours : AppBaseFragment<FragmentConsultationHoursBinding, DashboardViewModel>(), RecyclerItemClickListener {

    private lateinit var defaultTimings: ArrayList<ConsultationHoursModel>
    private lateinit var adapter: AppBaseRecyclerViewAdapter<ConsultationHoursModel>


    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): FragmentConsultationHours {
            val fragment = FragmentConsultationHours()
            fragment.arguments = bundle
            return fragment
        }
    }
    override fun getLayout(): Int {
        return R.layout.fragment_consultation_hours
    }

    override fun getViewModelClass(): Class<DashboardViewModel> {
        return DashboardViewModel::class.java
    }


    override fun onCreateView() {
        super.onCreateView()
//        getBundleData()
        setOnClickListener(binding?.btnSave)
        this.adapter = AppBaseRecyclerViewAdapter(
                activity = baseActivity,
                list = this.defaultTimings,
                itemClickListener = this@FragmentConsultationHours
        )
        binding!!.rvConsultationHour.adapter = adapter

    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        when (actionType) {
            RecyclerViewActionType.TOGGLE_STATE_CHANGED.ordinal -> {
                adapter.notifyDataSetChanged()
            }
            RecyclerViewActionType.CHECK_BOX_APPLY_ALL.ordinal -> {
                item as ConsultationHoursModel
                applyOnAllDays(item)
                adapter.notifyDataSetChanged()
            }
            RecyclerViewActionType.ADD_SESSION.ordinal -> {
                adapter.notifyDataSetChanged()
            }
        }

    }

    private fun applyOnAllDays(data: ConsultationHoursModel) {
        if (data.isAppliedOnAllDays!!) {
            applyOnAllDaysTurnedOn(data)
        } else {
            applyOnAllDaysTurnedOff(data)
        }
    }

    private fun applyOnAllDaysTurnedOn(data: ConsultationHoursModel) {
        for (i in 1 until defaultTimings.size) {
            val item = defaultTimings[i]
            item.isTurnedOn = true
            item.isViewEnabled = false
            item.isDataAppliedOnMyDay = true
            item.timeSlots = ArrayList()
            for (t in data.timeSlots) {
                t.to = data.toTiming
                t.from = data.fromTiming
                item.timeSlots.add(t)
            }
        }
    }


    private fun applyOnAllDaysTurnedOff(data: ConsultationHoursModel) {
        for (i in 1 until defaultTimings.size) {
            val item = defaultTimings[i]
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
                if (isValid()) {
                    finishAndGoBack()
                }
            }
        }
    }

    fun isValid(): Boolean {
        return true
    }


    private fun finishAndGoBack() {
        // send staff data to the intent
        val intent = Intent()
        requireActivity().setResult(AppCompatActivity.RESULT_OK, intent)
        baseActivity.finish()
    }


}