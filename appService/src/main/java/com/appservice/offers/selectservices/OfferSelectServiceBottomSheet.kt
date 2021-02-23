package com.appservice.offers.selectservices

import com.appservice.R
import com.appservice.constant.IntentConstant
import com.appservice.databinding.BottomSheetSelectServiceListingBinding
import com.appservice.offers.models.SelectServiceModel.DataItemOfferService
import com.appservice.offers.viewmodel.OfferViewModel
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.staffs.model.DataItemService
import com.appservice.staffs.model.FilterBy
import com.appservice.staffs.model.ServiceListRequest
import com.appservice.staffs.model.ServiceListResponse
import com.appservice.staffs.ui.UserSession
import com.framework.base.BaseBottomSheetDialog
import java.util.*

class OfferSelectServiceBottomSheet : BaseBottomSheetDialog<BottomSheetSelectServiceListingBinding, OfferViewModel>(), RecyclerItemClickListener {
    private var isEdit: Boolean? = null
    lateinit var data: List<DataItemService?>
    var adapter: AppBaseRecyclerViewAdapter<DataItemOfferService>? = null
    private var listServices: ArrayList<DataItemOfferService>? = null
    private var serviceIds: ArrayList<String>? = null
    override fun onCreateView() {
        init()
        getBundleData()

    }

    private fun getBundleData() {
        if (listServices == null) listServices = arrayListOf()
        serviceIds = arguments?.getStringArrayList(IntentConstant.OFFER_SERVICES.name)
        isEdit = serviceIds.isNullOrEmpty().not()
    }


    private fun fetchServices() {
        viewModel!!.getServiceListing(ServiceListRequest(
                FilterBy("ALL", 0, 0), "", floatingPointTag = UserSession.fpTag)
        ).observe(viewLifecycleOwner, {
            data = (it as ServiceListResponse).result!!.data!!
            this.adapter = AppBaseRecyclerViewAdapter(activity = baseActivity, list = data as ArrayList<DataItemOfferService>, itemClickListener = this@OfferSelectServiceBottomSheet)
            binding?.rvServices?.adapter = adapter
//            when {
//                isEdit!! -> {
//                    data.forEach { datum ->
//                        if (serviceIds?.contains(datum?.id) == true) {
//                            datum?.isChecked = true
//
//                            listServices?.add(datum!!)
//                        }
//                    }
//                }
//            }
            adapter?.notifyDataSetChanged()

        })
    }

    private fun init() {
        fetchServices()
        setOnClickListener(binding?.btnApply, binding?.btnCancel)
    }


    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        val dataItem = item as DataItemOfferService
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

    }

    override fun getLayout(): Int {
        return R.layout.bottom_sheet_select_service_listing
    }

    override fun getViewModelClass(): Class<OfferViewModel> {
        return OfferViewModel::class.java
    }
}