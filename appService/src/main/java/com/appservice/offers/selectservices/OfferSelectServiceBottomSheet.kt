package com.appservice.offers.selectservices

import androidx.fragment.app.DialogFragment
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
import kotlin.collections.ArrayList

class OfferSelectServiceBottomSheet : BaseBottomSheetDialog<BottomSheetSelectServiceListingBinding, OfferViewModel>(), RecyclerItemClickListener {
     var onClicked: () -> Unit = {}
    private var isEdit: Boolean? = null
    lateinit var data: List<DataItemService?>
    var adapter: AppBaseRecyclerViewAdapter<DataItemOfferService>? = null
    private var listServices: ArrayList<DataItemOfferService>? = null
    private var serviceIds: ArrayList<String>? = null
    override fun onCreateView() {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.OffersThemeBase)
        init()

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
            val services = ArrayList<DataItemOfferService>()
            data = (it as ServiceListResponse).result!!.data!!
            data.forEach { service -> services.add(DataItemOfferService(service?.isChecked, service?.type, service?.category, service?.secondaryTileImages, service?.price, service?.discountedPrice, service?.duration, service?.id, service?.image, service?.secondaryImages, service?.discountAmount, service?.name, service?.tileImage)) }
            this.adapter = AppBaseRecyclerViewAdapter(activity = baseActivity, list = services, itemClickListener = this@OfferSelectServiceBottomSheet)
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
//            adapter?.notifyDataSetChanged()

        })
    }

    private fun init() {
        getBundleData()
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

    fun setData(edit: Boolean) {

    }
}