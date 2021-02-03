package com.appservice.staffs.ui.services

import android.content.Intent
import android.text.Html
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentSelectServicesBinding
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.staffs.model.*
import com.appservice.staffs.ui.UserSession
import com.appservice.staffs.ui.viewmodel.StaffViewModel
import kotlinx.android.synthetic.main.fragment_kyc_details.*
import java.util.*

class StaffServicesFragment() : AppBaseFragment<FragmentSelectServicesBinding, StaffViewModel>(), RecyclerItemClickListener {
    private var isEdit: Boolean? = null
    lateinit var data: List<DataItemService?>
    var adapter: AppBaseRecyclerViewAdapter<DataItemService>? = null
    private var listServices: ArrayList<DataItemService>? = null
    private var serviceIds: ArrayList<String>? = null

    companion object {
        fun newInstance(): StaffServicesFragment {
            return StaffServicesFragment()
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
        getBundleData()

    }

    private fun getBundleData() {
        if (listServices == null) listServices = arrayListOf()
        serviceIds = (arguments?.getSerializable(IntentConstant.STAFF_SERVICES.name) as? ArrayList<String>)
        isEdit = (serviceIds != null && serviceIds?.isNullOrEmpty()?.not()!!)

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
            this.adapter = AppBaseRecyclerViewAdapter(activity = baseActivity, list = data as ArrayList<DataItemService>, itemClickListener = this@StaffServicesFragment)
            binding?.rvServiceProvided?.adapter = adapter
            when {
                isEdit!! -> {
                    data.forEach { datum ->
                        if (serviceIds?.contains(datum?.id) == true) {
                            datum?.isChecked = true
                            listServices?.add(datum!!)
                        }
                    }
                }
            }
            setServiceCount()
            adapter?.notifyDataSetChanged()

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
            true -> listServices?.add(dataItem)
            false -> listServices?.remove(dataItem)
        }
        setServiceCount()

    }

    private fun setServiceCount() {
        val serviceCount = "${listServices?.size}/${data.size} services selected"
        val serviceConfirm = "CONFIRM ${listServices?.size} SERVICES"
        binding!!.ctvServicesCountTitle.text = serviceCount
        binding!!.ctvServicesCount.text = serviceConfirm
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding!!.flConfirmServices -> {
                when {
                    listServices?.size!! > 0 -> {
                        val intent = Intent();
                        intent.putExtra(IntentConstant.STAFF_SERVICES.name, listServices);
                        requireActivity().setResult(AppCompatActivity.RESULT_OK, intent);
                        requireActivity().finish();
                    }
                }
            }
        }
    }
}
