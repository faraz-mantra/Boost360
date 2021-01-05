package com.appservice.ui.staffs.ui.services

import android.os.Bundle
import android.text.Html
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentSelectServicesBinding
import com.framework.models.BaseViewModel

class StaffServicesFragment : AppBaseFragment<FragmentSelectServicesBinding, BaseViewModel>() {


    companion object {
        fun newInstance(): StaffServicesFragment {
            val args = Bundle()
            val fragment = StaffServicesFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_select_services
    }

    override fun getViewModelClass(): Class<BaseViewModel> {
        return BaseViewModel::class.java
    }

    override fun onCreateView() {
        binding?.ctvHeading?.text = Html.fromHtml(getString(R.string.select_what_services_that_the_staff))
        binding?.rvServiceProvided?.adapter = ServicesAdapter()
    }
}