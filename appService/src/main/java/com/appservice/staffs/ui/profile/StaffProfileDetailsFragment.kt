package com.appservice.staffs.ui.profile

import android.text.Html
import android.view.View
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentStaffProfileBinding
import com.framework.models.BaseViewModel

class StaffProfileDetailsFragment : AppBaseFragment<FragmentStaffProfileBinding, BaseViewModel>() {
    override fun getLayout(): Int {
        return R.layout.fragment_staff_profile
    }
    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        setOnClickListener(binding!!.civMenu)
        setOnClickListener(binding!!.ctvEdit)
        setOnClickListener(binding!!.ctvEditLeaves)
        setOnClickListener(binding!!.ctvEditServices)
        setOnClickListener(binding!!.ctvEditTiming)
        binding!!.ctvEdit.text = Html.fromHtml(getString(R.string.u_edit_info_u))

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