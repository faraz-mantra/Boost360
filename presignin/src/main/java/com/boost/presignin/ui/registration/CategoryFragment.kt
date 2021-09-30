package com.boost.presignin.ui.registration

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.constant.RecyclerViewActionType
import com.boost.presignin.databinding.FragmentCategoryBinding
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.model.BusinessInfoModel
import com.boost.presignin.model.category.CategoryDataModel
import com.boost.presignin.model.onboardingRequest.CategoryFloatsRequest
import com.boost.presignin.model.onboardingRequest.CreateProfileRequest
import com.boost.presignin.recyclerView.AppBaseRecyclerViewAdapter
import com.boost.presignin.recyclerView.BaseRecyclerViewItem
import com.boost.presignin.recyclerView.RecyclerItemClickListener
import com.boost.presignin.rest.response.ResponseDataCategory
import com.boost.presignin.viewmodel.CategoryVideoModel
import com.framework.base.BaseFragment
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.webengageconstant.*
import android.content.Intent
import android.net.Uri
import com.framework.analytics.SentryController

class CategoryFragment : AppBaseFragment<FragmentCategoryBinding, CategoryVideoModel>(), RecyclerItemClickListener {

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
    activity?.onBackPressedDispatcher?.addCallback(object : OnBackPressedCallback(true) {
      override fun handleOnBackPressed() {
        CategoryDataModel.clearSelection()
        baseActivity.finishAfterTransition()
      }
    })
    binding?.recyclerView?.adapter = baseAdapter
    viewModel?.getCategories(requireContext())?.observeOnce(viewLifecycleOwner) {
      if (it.error != null) return@observeOnce
      val categoryResponse = it as ResponseDataCategory
      baseAdapter.addItems(categoryList.apply {
        clear()
        if (CategoryDataModel.getSavedStateCategory() != null) {
          val data = categoryResponse.data!!
          for ((index, categoryDataModel) in data.withIndex()) {
            if (categoryDataModel.category_Name == CategoryDataModel.getSavedStateCategory()?.category_Name) {
              data[index].isSelected = true
            }
          }
          binding?.confirmButton?.visible()
          addAll(data)
        } else
          addAll(categoryResponse.data!!)
      })
    }

    binding?.confirmButton?.setOnClickListener {
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
    }

    val str_no_category_found = getString(R.string.can_not_find_category_speak_expert)
    binding?.tvNoCategoryFound?.movementMethod  =LinkMovementMethod.getInstance()
    val clickableStr = "our expert"
    val spannableString = SpannableString(str_no_category_found)
    val clickableSpan = object :ClickableSpan(){
      override fun onClick(p0: View) {
        try {
          val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getString(R.string.expert_contact_number)))
          startActivity(intent)
        }catch (e:Exception){
          SentryController.captureException(e)

          e.printStackTrace()
        }
      }
    }
    spannableString.setSpan(clickableSpan,str_no_category_found.indexOf(clickableStr),
      str_no_category_found.indexOf(clickableStr)+clickableStr.length,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    binding?.tvNoCategoryFound?.text = spannableString
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.CATEGORY_ITEM_CLICKED.ordinal -> {
        CategoryDataModel.clearSelection()
        category = item as? CategoryDataModel
        for (listItem in categoryList) {
          (listItem as? CategoryDataModel)?.let {
            it.isSelected = (it.category_key == (item as? CategoryDataModel)?.category_key) && it.isSelected != false
          }
        }
        binding?.recyclerView?.post { baseAdapter.notifyDataSetChanged() }
        if (categoryList.filter { it.isSelected }.isNullOrEmpty()) binding?.confirmButton?.gone() else binding?.confirmButton?.visible()
      }
    }
  }

}