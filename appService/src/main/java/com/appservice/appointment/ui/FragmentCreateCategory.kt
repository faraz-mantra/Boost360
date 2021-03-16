package com.appservice.appointment.ui

import android.os.Bundle
import android.view.*
import com.appservice.R
import com.appservice.appointment.widgets.BottomSheetCreateCategory
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.IntentConstant
import com.appservice.constant.RecyclerViewItemType
import com.appservice.databinding.FragmentCreateCategoryBinding
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.AppBaseRecyclerViewItem
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.rest.TaskCode
import com.appservice.ui.catalog.startFragmentActivity
import com.appservice.ui.model.ServiceListingRequest
import com.appservice.ui.model.ServiceListingResponse
import com.appservice.viewmodel.AppointmentSettingsViewModel
import com.framework.base.BaseResponse
import kotlinx.android.synthetic.main.fragment_create_category.*
import kotlinx.android.synthetic.main.toolbar_catalog.*
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

class CreateCategoryFragment : AppBaseFragment<FragmentCreateCategoryBinding, AppointmentSettingsViewModel>(), RecyclerItemClickListener {
    private var fpTag: String? = null
    private var categoryList: ArrayList<Category> = arrayListOf()

    override fun getLayout(): Int {
        return R.layout.fragment_create_category
    }

    override fun getViewModelClass(): Class<AppointmentSettingsViewModel> {
        return AppointmentSettingsViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        getBundleData()
        getServiceListing()
//        setToolbarTitle("Categories (${list.size})")

        setOnClickListener(binding?.btnAddNewCategory)
    }

    private fun getServiceListing() {
        hitApi(viewModel?.getServiceListing(ServiceListingRequest(floatingPointTag = fpTag)), R.string.error_getting_category)
    }

    override fun onSuccess(it: BaseResponse) {
        when (it.taskcode) {
            TaskCode.GET_SERVICE_LISTING.ordinal -> onServiceListingReceived(it)
//            TaskCode.UPDATE_OFFER.ordinal -> onOfferUpdated(it)
//            TaskCode.ADD_OFFER_IMAGE.ordinal -> onPrimaryImageUploaded(it)
//            TaskCode.OFFER_DETAILS.ordinal -> onOfferDetailsResponseReceived(it)
//            TaskCode.DELETE_OFFER.ordinal -> onDeleteOffer(it)
//            TaskCode.GET_SERVICE_DETAILS.ordinal -> onServiceDetailResponseReceived(it)

        }
    }

    private fun onServiceListingReceived(it: BaseResponse) {
        val categories = ArrayList<String>()
        val serviceListingResponse = it as ServiceListingResponse
        val serviceList = serviceListingResponse.result?.map { it?.services?.items?.get(0) }
        serviceList?.forEach { service->categories.add(service?.category!!) }
        categories.forEach { cat->categoryList.add(Category(cat,Collections.frequency(categories,cat))) }
        binding?.rvCategory?.apply {
            val adapterN = AppBaseRecyclerViewAdapter(baseActivity, categoryList, this@CreateCategoryFragment)
            adapter = adapterN
        }
    }

    companion object {
        fun newInstance(fpTag: String?): CreateCategoryFragment {
            val bundle = Bundle()
            bundle.putString(IntentConstant.FP_TAG.name, fpTag)
            val fragment = CreateCategoryFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
    private fun getBundleData() {
        fpTag = arguments?.getString(IntentConstant.FP_TAG.name)
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_service_listing, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_service_configuration -> {
                startFragmentActivity(FragmentType.APPOINTMENT_SETTINGS)
                true
            }
            else -> super.onOptionsItemSelected(item)

        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v) {
            binding?.btnAddNewCategory -> {
                showCreateCategory()
            }
        }
    }

    private fun showCreateCategory() {
        val bottomSheetCreateCategory = BottomSheetCreateCategory()
        bottomSheetCreateCategory.show(parentFragmentManager, BottomSheetCreateCategory::class.java.name)
    }
}

data class Category(var name: String, var countItems: Int? = 0, var isSelected: Boolean? = false, var recyclerViewItem: Int = RecyclerViewItemType.CREATE_CATEGORY_ITEM_VIEW.getLayout()) : AppBaseRecyclerViewItem, Serializable {
    override fun getViewType(): Int {
        return recyclerViewItem
    }

}