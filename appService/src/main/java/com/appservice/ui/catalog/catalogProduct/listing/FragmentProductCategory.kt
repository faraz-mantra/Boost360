package com.appservice.ui.catalog.catalogProduct.listing

import android.os.Bundle
import com.appservice.R
import com.appservice.appointment.ui.Category
import com.appservice.base.AppBaseFragment
import com.appservice.constant.IntentConstant
import com.appservice.databinding.FragmentProductCategoryBinding
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.viewmodel.ProductViewModel

class FragmentProductCategory : AppBaseFragment<FragmentProductCategoryBinding, ProductViewModel>() {
    override fun getLayout(): Int {
        return R.layout.fragment_product_category
    }

    private var fpTag: String? = null
    private var categoryList: ArrayList<Category> = arrayListOf()
    private var adapterN: AppBaseRecyclerViewAdapter<Category>? = null
    override fun getViewModelClass(): Class<ProductViewModel> {
        return ProductViewModel::class.java
    }

    companion object {
        fun newInstance(fpTag: String?): FragmentProductCategory {
            val bundle = Bundle()
            bundle.putString(IntentConstant.FP_TAG.name, fpTag)
            val fragment = FragmentProductCategory()
            fragment.arguments = bundle
            return fragment
        }

        fun newInstance(): FragmentProductCategory {
            return FragmentProductCategory()
        }
    }

    private fun getBundleData() {
        fpTag = arguments?.getString(IntentConstant.FP_TAG.name)
    }

    override fun onCreateView() {
        getBundleData()
        getProductListing()
        setOnClickListener(binding?.btnAddNewCategory)
    }

    private fun getProductListing() {

    }
}