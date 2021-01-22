package com.appservice.staffs.ui.profile

import android.text.Html
import android.view.View
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentStaffProfileBinding
import com.appservice.staffs.model.DataItem
import com.framework.models.BaseViewModel

class StaffProfileDetailsFragment : AppBaseFragment<FragmentStaffProfileBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.fragment_staff_profile
    }
    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    companion object {
        fun newInstance(): StaffProfileDetailsFragment {
            return StaffProfileDetailsFragment()
        }
    }
    override fun onCreateView() {
        super.onCreateView()
        setOnClickListener(binding!!.civMenu,binding!!.ctvEdit,binding!!.ctvEditLeaves,binding!!.ctvEditServices,binding!!.ctvEditTiming)
        setData()


    }

    private fun setData() {
        binding!!.ctvEdit.text = Html.fromHtml(getString(R.string.u_edit_info_u))
        val get = arguments?.get("STAFF_DETAILS") as DataItem
        binding?.ctvTiming?.text = get.timings.toString()
        binding?.ctvStaffName?.text = get.name
        binding?.ctvExperience?.text=get.experience.toString()
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding!!.civMenu -> {

            }
            binding!!.ctvEdit -> {

            }
            binding!!.ctvEditLeaves -> {

            }
            binding!!.ctvEditServices -> {

            }
            binding!!.ctvEditTiming -> {

            }
        }
    }
}