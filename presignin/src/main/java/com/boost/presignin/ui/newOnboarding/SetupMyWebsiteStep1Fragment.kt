package com.boost.presignin.ui.newOnboarding

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.constant.RecyclerViewActionType
import com.boost.presignin.databinding.LayoutSetUpMyWebsiteStep1Binding
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.model.category.CategoryDataModel
import com.boost.presignin.model.category.CategoryDataModelOv2
import com.boost.presignin.recyclerView.AppBaseRecyclerViewAdapter
import com.boost.presignin.recyclerView.BaseRecyclerViewItem
import com.boost.presignin.recyclerView.RecyclerItemClickListener
import com.boost.presignin.rest.response.ResponseDataCategoryOv2
import com.boost.presignin.viewmodel.CategoryVideoModel
import com.framework.extensions.observeOnce
import com.framework.webengageconstant.*

class SetupMyWebsiteStep1Fragment : AppBaseFragment<LayoutSetUpMyWebsiteStep1Binding, CategoryVideoModel>(),
    RecyclerItemClickListener {

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): SetupMyWebsiteStep1Fragment {
            val fragment = SetupMyWebsiteStep1Fragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var baseAdapter: AppBaseRecyclerViewAdapter<CategoryDataModelOv2>
    private var categoryList = ArrayList<CategoryDataModelOv2>()
    private val phoneNumber by lazy {
        arguments?.getString(IntentConstant.EXTRA_PHONE_NUMBER.name)
    }


    override fun getLayout(): Int {
      return R.layout.layout_set_up_my_website_step_1
    }

    override fun getViewModelClass(): Class<CategoryVideoModel> {
        return CategoryVideoModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        setOnClickListeners()
        WebEngageController.trackEvent(PS_BUSINESS_CATEGORY_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
        baseAdapter = AppBaseRecyclerViewAdapter(baseActivity, ArrayList(), this)
        activity?.onBackPressedDispatcher?.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                CategoryDataModel.clearSelection()
                baseActivity.finishAfterTransition()
            }
        })
        binding?.rvCategories?.adapter = baseAdapter
        binding?.rvCategories?.layoutManager = LinearLayoutManager(requireActivity())

        viewModel?.getCategoriesOv2(requireContext())?.observeOnce(viewLifecycleOwner) {
            if (it.error != null) return@observeOnce
            val categoryResponse = it as ResponseDataCategoryOv2
            baseAdapter.addItems(categoryList.apply {
                clear()
                if (CategoryDataModelOv2.getSavedStateCategory() != null) {
                    val data = categoryResponse.data!!
                    for ((index, categoryDataModel) in data.withIndex()) {
                        if (categoryDataModel.category_Name == CategoryDataModel.getSavedStateCategory()?.category_Name) {
                            data[index].isSelected = true
                        }
                    }
                    addAll(data)
                } else
                    addAll(categoryResponse.data!!)
            })
        }

       /* binding?.confirmButton?.setOnClickListener {
            WebEngageController.trackEvent(PS_BUSINESS_CATEGORY_CLICK, CATEGORY, NO_EVENT_VALUE)
            CategoryDataModel.saveCategoryState(category)
            addFragmentReplace(
                com.framework.R.id.container,
                BusinessDetailsFragment.newInstance(
                    CategoryFloatsRequest(
                        categoryDataModel = category, userBusinessMobile = phoneNumber,
                        requestProfile = CreateProfileRequest(ProfileProperties = BusinessInfoModel(userMobile = phoneNumber))
                    )
                ), true
            )
        }*/

    }

    private fun setOnClickListeners() {
        binding?.tvNextStep1?.setOnClickListener {
            addFragment(R.id.inner_container,BusinessCategoryPreviewFragment.newInstance(Bundle()
                .apply
                {
                    putString(IntentConstant.EXTRA_PHONE_NUMBER.name,phoneNumber)
                }),true)
        }
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        when (actionType) {
            RecyclerViewActionType.CATEGORY_ITEM_CLICKED.ordinal -> {
                CategoryDataModel.clearSelection()
               val category = item as? CategoryDataModel
                for (listItem in categoryList) {
                    (listItem as? CategoryDataModel)?.let {
                        it.isSelected = (it.category_key == (item as? CategoryDataModel)?.category_key) && it.isSelected != false
                    }
                }
                binding?.rvCategories?.post { baseAdapter.notifyDataSetChanged() }
               // if (categoryList.filter { it.isSelected }.isNullOrEmpty()) binding?.confirmButton?.gone() else binding?.confirmButton?.visible()
            }
        }

    }
}