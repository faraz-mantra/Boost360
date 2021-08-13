package com.boost.presignin.ui.registration

import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import com.boost.presignin.R
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.constant.RecyclerViewActionType
import com.boost.presignin.constant.RecyclerViewItemType
import com.boost.presignin.databinding.FragmentCategoryBinding
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.model.BusinessInfoModel
import com.boost.presignin.model.onboardingRequest.CreateProfileRequest
import com.boost.presignin.model.category.CategoryDataModel
import com.boost.presignin.model.onboardingRequest.CategoryFloatsRequest
import com.boost.presignin.recyclerView.AppBaseRecyclerViewAdapter
import com.boost.presignin.recyclerView.BaseRecyclerViewItem
import com.boost.presignin.recyclerView.RecyclerItemClickListener
import com.boost.presignin.rest.response.ResponseDataCategory
import com.boost.presignin.viewmodel.CategoryVideoModel
import com.framework.base.BaseFragment
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.webengageconstant.*

class CategoryFragment : BaseFragment<FragmentCategoryBinding, CategoryVideoModel>(), RecyclerItemClickListener {

  private val TAG = "CategoryFragment"
  private lateinit var baseAdapter: AppBaseRecyclerViewAdapter<CategoryDataModel>
  private var category: CategoryDataModel? = null
  private var categoryList = ArrayList<CategoryDataModel>()

  companion object {

    @JvmStatic
    fun newInstance(bundle: Bundle?) = CategoryFragment().apply {
      arguments = bundle
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_category
  }

  override fun getViewModelClass(): Class<CategoryVideoModel> {
    return CategoryVideoModel::class.java
  }

  private val phoneNumber by lazy {
    arguments?.getString(IntentConstant.EXTRA_PHONE_NUMBER.name)
  }

  override fun onCreateView() {
    WebEngageController.trackEvent(PS_BUSINESS_CATEGORY_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
    baseAdapter = AppBaseRecyclerViewAdapter(baseActivity, ArrayList(), this)
    binding?.civBack?.setOnClickListener { baseActivity.onNavPressed() }
    binding?.recyclerView?.adapter = baseAdapter
    viewModel?.getCategories(requireContext())?.observeOnce(viewLifecycleOwner) {
      if (it.error != null) return@observeOnce
      val categoryResponse = it as ResponseDataCategory
      baseAdapter.addItems(categoryList.apply {
        clear()
        addAll(categoryResponse.data!!)
      })
    }

    binding?.confirmButton?.setOnClickListener {
      WebEngageController.trackEvent(PS_BUSINESS_CATEGORY_CLICK, CATEGORY, NO_EVENT_VALUE)
      addFragmentReplace(
        com.framework.R.id.container,
        BusinessDetailsFragment.newInstance(
          CategoryFloatsRequest(
            categoryDataModel = category, userBusinessMobile = phoneNumber,
            requestProfile = CreateProfileRequest(ProfileProperties = BusinessInfoModel(userMobile = phoneNumber))
          )
        ), true
      )
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.CATEGORY_ITEM_CLICKED.ordinal -> {
        category = item as? CategoryDataModel
        for (listItem in categoryList) {
          (listItem as? CategoryDataModel)?.let {
            it.isSelected = (it.category_key == (item as? CategoryDataModel)?.category_key) && it.isSelected!=false
          }
        }
        binding?.recyclerView?.post { baseAdapter.notifyDataSetChanged() }
        binding?.confirmButton?.visible()
      }
    }
  }
}