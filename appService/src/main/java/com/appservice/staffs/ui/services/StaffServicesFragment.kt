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
import com.appservice.staffs.model.*
import com.appservice.staffs.ui.Constants
import com.appservice.staffs.ui.UserSession
import com.appservice.staffs.ui.viewmodel.StaffViewModel
import kotlinx.android.synthetic.main.fragment_kyc_details.*
import java.util.*
import kotlin.collections.ArrayList

class StaffServicesFragment : AppBaseFragment<FragmentSelectServicesBinding, StaffViewModel>(), RecyclerItemClickListener {
    lateinit var data: List<DataItemService?>
    private var listservices: ArrayList<DataItemService> = ArrayList()

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

    override fun getViewModelClass(): Class<StaffViewModel> {
        return StaffViewModel::class.java
    }

    override fun onCreateView() {
        init()
    }

    private fun fetchServices() {
        showProgress("Loading...")
        viewModel!!.getServiceListing(ServiceListRequest(
                FilterBy("ALL", 0, 0), "", floatingPointTag = UserSession.fpId)
        ).observe(viewLifecycleOwner, {
            hideProgress()
            data = (it as ServiceListResponse).result!!.data!!
            binding!!.ctvServicesCountTitle.text = "0/${data.size} service selected"
            binding!!.ctvServicesCount.text = getString(R.string.confirm_services)
            binding?.rvServiceProvided?.adapter = AppBaseRecyclerViewAdapter(activity = baseActivity, list = data as ArrayList<DataItemService>, itemClickListener = this@StaffServicesFragment)
        })
    }

    private fun init() {
        binding?.ctvHeading?.text = Html.fromHtml(getString(R.string.select_what_services_that_the_staff))
        fetchServices()
        setOnClickListener(binding!!.flConfirmServices)
    }


    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        val dataItem = item as DataItemService
        when (dataItem.isChecked) {
            true -> {
                dataItem.isChecked = false
            }
            else -> {
                dataItem.isChecked = true
            }
        }
        when (dataItem.isChecked) {
            true -> listservices.add(dataItem)
            false -> listservices.remove(dataItem)
        }
        val serviceCount = "${listservices.size}/${data.size} services selected"
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
