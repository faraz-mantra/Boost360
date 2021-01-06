package com.appservice.staffs.ui.services

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.databinding.FragmentSelectServicesBinding
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.staffs.ui.Constants
import com.appservice.staffs.model.ServiceModel
import com.framework.models.BaseViewModel
import java.util.*
import kotlin.collections.ArrayList

class StaffServicesFragment : AppBaseFragment<FragmentSelectServicesBinding, BaseViewModel>(), RecyclerItemClickListener {


    private lateinit var serviceData: ArrayList<ServiceModel>
    private var listservices: ArrayList<String> = ArrayList()


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
        initView()
    }

    private fun initView() {
        this.serviceData = ServiceModel().serviceData()
        binding?.ctvHeading?.text = Html.fromHtml(getString(R.string.select_what_services_that_the_staff))
        binding!!.ctvServicesCountTitle.text = "0/${serviceData.size} services Selected"
        binding!!.ctvServicesCount.text = getString(R.string.confirm_services)
        binding?.rvServiceProvided?.adapter = AppBaseRecyclerViewAdapter(baseActivity, serviceData, this@StaffServicesFragment)
        setOnClickListener(binding!!.flConfirmServices)
    }


    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        val serviceModel = item as ServiceModel
        when (serviceModel.isChecked) {
            true -> {
                serviceModel.isChecked = false
            }
            else -> {
                serviceModel.isChecked = true
            }
        }
        when (serviceModel.isChecked) {
            true -> listservices.add(serviceModel.serviceName!!)
            false -> listservices.remove(serviceModel.serviceName)
        }
        val serviceCount = "${listservices.size}/${serviceData.size} services selected"
        val serviceConfirm = "CONFIRM ${listservices.size} SERVICES"
        binding!!.ctvServicesCountTitle.text = serviceCount
        binding!!.ctvServicesCount.text = serviceConfirm

    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding!!.flConfirmServices -> {
                when {
                    listservices.size > 0 -> {
                        val intent = Intent();
                        intent.putExtra(Constants.SERVICES_LIST, listservices);
                        requireActivity().setResult(AppCompatActivity.RESULT_OK, intent);
                        requireActivity().finish();
                    }
                }
            }
        }
    }
}
