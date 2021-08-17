package com.appservice.offers.selectservices

import android.view.View
import android.widget.SearchView
import com.appservice.R
import com.appservice.constant.IntentConstant
import com.appservice.databinding.BottomSheetSelectServiceListingBinding
import com.appservice.model.staffModel.DataItemService
import com.appservice.model.staffModel.FilterBy
import com.appservice.model.staffModel.ServiceListRequest
import com.appservice.model.staffModel.ServiceListResponse
import com.appservice.offers.models.SelectServiceModel.DataItemOfferService
import com.appservice.offers.viewmodel.OfferViewModel
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.framework.base.BaseBottomSheetDialog
import java.util.*
import kotlin.collections.ArrayList

class OfferSelectServiceBottomSheet : BaseBottomSheetDialog<BottomSheetSelectServiceListingBinding, OfferViewModel>(), RecyclerItemClickListener {
    private var dataOffer: DataItemOfferService? = null
    var onClicked: (dataOffer: DataItemOfferService?) -> Unit = {}
    private var isEdit: Boolean? = null
    lateinit var data: List<DataItemService?>
    var adapter: AppBaseRecyclerViewAdapter<DataItemOfferService>? = null
    private var listServices: ArrayList<DataItemOfferService>? = null
    private var finalList: ArrayList<DataItemOfferService>? = arrayListOf()
    private var copylist: ArrayList<DataItemOfferService>? = null
    private var serviceIds: ArrayList<String>? = null
    override fun onCreateView() {

        init()
    }

    private fun getBundleData() {
        if (listServices == null) listServices = arrayListOf()
        serviceIds = arguments?.getStringArrayList(IntentConstant.OFFER_SERVICES.name)
        isEdit = serviceIds.isNullOrEmpty().not()
    }


    private fun fetchServices() {
        viewModel!!.getServiceListing(ServiceListRequest(
                FilterBy("ALL", 0, 0), "", floatingPointTag = sessionManager?.fpTag)
        ).observe(viewLifecycleOwner, {
            listServices = ArrayList()
            data = (it as ServiceListResponse).result!!.data!!
            data.forEach { service -> listServices!!.add(DataItemOfferService(service?.isChecked, service?.type, service?.category, service?.secondaryTileImages, service?.price, service?.discountedPrice, service?.duration, service?.id, service?.image, service?.secondaryImages, service?.discountAmount, service?.name, service?.tileImage)) }
            this.adapter = AppBaseRecyclerViewAdapter(activity = baseActivity, list = listServices!!, itemClickListener = this@OfferSelectServiceBottomSheet)
            binding?.rvServices?.adapter = adapter
            finalList?.addAll(listServices!!)

        })
    }

    private fun init() {

        getBundleData()
        fetchServices()
        setOnClickListener(binding?.btnApply, binding?.btnCancel)
        binding?.searchServices?.setOnQueryTextListener(object : SearchView.OnQueryTextListener, androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let { startFilter(it.trim().toLowerCase(Locale.ROOT)) }
                return false
            }
        })
        binding?.searchServices?.setOnCloseListener {
            adapter?.updateList(finalList!!)
            true
        }
    }

    private fun startFilter(query: String) {
        copylist = arrayListOf()
        copylist?.clear()
        copylist?.addAll(finalList!!)
        val searchList = ArrayList<DataItemOfferService>()
        if (query.isNotEmpty() || query.isNotBlank()) {
            finalList?.forEach { if (it.name?.toLowerCase(Locale.ROOT)?.contains(query) == true) searchList.add(it) }
            adapter?.notify(searchList)
        } else {
            adapter?.notify(copylist!!)
        }
    }


    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        this.dataOffer = item as DataItemOfferService
        listServices?.forEach { if (it != item) it.isChecked = false }
        adapter?.notifyDataSetChanged()
    }

    override fun getLayout(): Int {
        return R.layout.bottom_sheet_select_service_listing
    }

    override fun getViewModelClass(): Class<OfferViewModel> {
        return OfferViewModel::class.java
    }

    fun setData(edit: Boolean) {

    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.btnApply -> {
                if (dataOffer != null)
                    onClicked(dataOffer)
                dismiss()
            }
            binding?.btnCancel -> {
                dismiss()
            }
        }
    }
}