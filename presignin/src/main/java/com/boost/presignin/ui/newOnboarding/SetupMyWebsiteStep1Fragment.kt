package com.boost.presignin.ui.newOnboarding

import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.addTextChangedListener
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.constant.RecyclerViewActionType
import com.boost.presignin.databinding.LayoutSetUpMyWebsiteStep1Binding
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.model.category.ApiCategoryResponseCategory
import com.boost.presignin.model.category.CategoryDataModel
import com.boost.presignin.model.category.CategoryDataModelOv2
import com.boost.presignin.model.category.getCategoryLiveData
import com.boost.presignin.recyclerView.AppBaseRecyclerViewAdapter
import com.boost.presignin.recyclerView.BaseRecyclerViewItem
import com.boost.presignin.recyclerView.RecyclerItemClickListener
import com.boost.presignin.rest.response.ResponseDataCategoryOv2
import com.boost.presignin.ui.newOnboarding.categoryService.startServiceCategory
import com.boost.presignin.viewmodel.CategoryVideoModel
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.utils.hideKeyBoard
import com.framework.utils.showKeyBoard
import com.framework.webengageconstant.NO_EVENT_VALUE
import com.framework.webengageconstant.PAGE_VIEW
import com.framework.webengageconstant.PS_BUSINESS_CATEGORY_LOAD

class SetupMyWebsiteStep1Fragment : AppBaseFragment<LayoutSetUpMyWebsiteStep1Binding, CategoryVideoModel>(), RecyclerItemClickListener {

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): SetupMyWebsiteStep1Fragment {
      val fragment = SetupMyWebsiteStep1Fragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  private var noCatListAdapter: AppBaseRecyclerViewAdapter<CategoryDataModelOv2>? = null
  private var adapterCategoryLocal: AppBaseRecyclerViewAdapter<CategoryDataModelOv2>? = null

  private val categoryListLive: List<ApiCategoryResponseCategory>?
    get() {
      return getCategoryLiveData()
    }

  private var categoryList: ArrayList<CategoryDataModelOv2> = arrayListOf()
  private var categoryNoDataList: ArrayList<CategoryDataModelOv2> = arrayListOf()
  private var selectedCategory: CategoryDataModelOv2? = null
  private var selectedCategoryLive: ApiCategoryResponseCategory? = null

  private val phoneNumber by lazy {
    arguments?.getString(IntentConstant.EXTRA_PHONE_NUMBER.name)
  }
  private val whatsappConsent by lazy {
    arguments?.getBoolean(IntentConstant.WHATSAPP_CONSENT_FLAG.name)
  }


  override fun getLayout(): Int {
    return R.layout.layout_set_up_my_website_step_1
  }

  override fun getViewModelClass(): Class<CategoryVideoModel> {
    return CategoryVideoModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    WebEngageController.trackEvent(PS_BUSINESS_CATEGORY_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
    setOnClickListeners()
    initialize()
    loadLocalCategoryData()
  }

  private fun loadLocalCategoryData() {
    adapterLocalCategory()
    viewModel?.getCategoriesOv2(baseActivity)?.observeOnce(viewLifecycleOwner) { it0 ->
      val response = it0 as? ResponseDataCategoryOv2
      if (it0.isSuccess() && response?.data.isNullOrEmpty().not()) {
        categoryList = response!!.data!!
        categoryNoDataList = ArrayList(response.data!!.filter { (it.experience_code == "RTL" || it.experience_code == "SVC") })
        adapterCategoryLocal?.notify(categoryList)
      }
    }
  }

  private fun adapterLocalCategory() {
    adapterCategoryLocal = AppBaseRecyclerViewAdapter(baseActivity, ArrayList(), this)
    binding?.rvCategories?.adapter = adapterCategoryLocal
  }

  private fun initialize() {
    observeCategorySearch()
    activity?.onBackPressedDispatcher?.addCallback(object : OnBackPressedCallback(true) {
      override fun handleOnBackPressed() {
        CategoryDataModel.clearSelection()
        baseActivity.finishAfterTransition()
      }
    })
    binding?.autocompleteSearchCategory?.setOnFocusChangeListener { _, b ->
      if (binding?.includeCatSuggSelected?.root?.visibility != View.VISIBLE) {
        if (b.not() && binding?.autocompleteSearchCategory?.text?.toString().isNullOrEmpty()) {
          binding?.layoutEtSugestion?.gone()
          binding?.linearFeaturedCategories?.visible()
          binding?.tvTitle?.visible()
          binding?.tvSubtitle?.visible()
        } else {
          binding?.layoutEtSugestion?.visible()
          binding?.linearFeaturedCategories?.gone()
          binding?.tvTitle?.gone()
          binding?.tvSubtitle?.gone()
          searchCategoryLiveItem(binding?.autocompleteSearchCategory?.text)
        }
      }
    }
    if (getCategoryLiveData().isNullOrEmpty()) baseActivity.startServiceCategory()
  }

  private fun observeCategorySearch() {
    binding?.autocompleteSearchCategory?.addTextChangedListener { str -> searchCategoryLiveItem(str) }
  }

  private fun searchCategoryLiveItem(str: Editable?) {
    if (categoryList.isNullOrEmpty().not() && categoryListLive.isNullOrEmpty().not() && str?.toString().isNullOrEmpty().not()) {
      binding?.layoutEtSugestion?.visible()
      binding?.linearFeaturedCategories?.gone()
      categoryListLive?.filter {
        it.getNameLower().startsWith(str.toString().lowercase())
            || it.getNameLower().contains(str.toString().lowercase())
      }?.take(40).apply { setSearchAdapterListItem(str) }
    } else (categoryListLive ?: arrayListOf()).take(40).apply { setSearchAdapterListItem(str) }
  }

  private fun List<ApiCategoryResponseCategory>?.setSearchAdapterListItem(str: Editable?) {
    val newFilterList = getFinalList(str)
    if (newFilterList.isNullOrEmpty().not()) {
      binding?.tvNoResultFound?.gone()
      binding?.includeNoSearchResultFound?.root?.gone()
    } else handleSuggestionNoResult()
    setAdapterSearch(newFilterList)
  }

  private fun List<ApiCategoryResponseCategory>?.getFinalList(str: Editable?): ArrayList<ApiCategoryResponseCategory> {
    val newFilterList = arrayListOf<ApiCategoryResponseCategory>()
    this?.forEach { cItem ->
      cItem.appexperiencecodedetails?.forEach { exp ->
        cItem.fpExperienceCode = exp
        cItem.subCategory = categoryList.firstOrNull { it.experience_code == exp.name }?.getCategoryWithoutNewLine()
        cItem.searchKeyword = str.toString()
        newFilterList.add(cItem)
      }
    }
    return newFilterList
  }

  private fun setAdapterSearch(filteredList: ArrayList<ApiCategoryResponseCategory>) {
    AppBaseRecyclerViewAdapter(baseActivity, filteredList, this).apply { binding?.rvCatSuggestion?.adapter = this }
  }

  private fun handleSuggestionNoResult() {
    binding?.tvNoResultFound?.visible()
    binding?.includeNoSearchResultFound?.root?.visible()
    val list = ArrayList<CategoryDataModelOv2>()
    list.addAll(categoryNoDataList)
    noCatListAdapter = AppBaseRecyclerViewAdapter(baseActivity, ArrayList(list.map { it.textChangeRTLAndSVC = true;it }), this)
    binding?.includeNoSearchResultFound?.rvCategories?.adapter = noCatListAdapter
  }

  private fun setOnClickListeners() {
    binding?.tvNextStep1?.setOnClickListener { goToNextStep() }
    binding?.includeCatSuggSelected?.root?.setOnClickListener {
      binding?.includeCatSuggSelected?.root?.gone()
      binding?.autocompleteSearchCategory?.visible()
      binding?.tvNextStep1?.isEnabled = false
      selectedCategoryLive = null
      selectedCategory = null
      baseActivity.showKeyBoard(binding?.autocompleteSearchCategory)
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.CATEGORY_ITEM_CLICKED.ordinal -> {
        val dataCategory = (item as? CategoryDataModelOv2) ?: return
        if (binding?.includeNoSearchResultFound?.root?.visibility == View.VISIBLE) {
          categoryNoDataList.forEach { it.isSelected = (it.category_key == dataCategory.category_key) }
          noCatListAdapter?.notifyDataSetChanged()
        } else {
          categoryList.forEach { it.isSelected = (it.category_key == dataCategory.category_key) }
          adapterCategoryLocal?.notifyDataSetChanged()
        }
        selectedCategoryLive = null
        setSelectedCat(dataCategory)
      }
      RecyclerViewActionType.CATEGORY_SUGGESTION_CLICKED.ordinal -> {
        selectedCategoryLive = (item as? ApiCategoryResponseCategory) ?: return
        baseActivity.hideKeyBoard()
        val dataCategory = categoryList.firstOrNull { it.experience_code == selectedCategoryLive?.fpExperienceCode?.name }
        dataCategory?.let {
          showCatSuggestionSelected(it)
          setSelectedCat(it)
        }
      }
    }
  }

  private fun showCatSuggestionSelected(category: CategoryDataModelOv2) {
    binding?.tvTitle?.visible()
    binding?.tvSubtitle?.visible()
    binding?.includeCatSuggSelected?.root?.visible()
    binding?.linearFeaturedCategories?.visible()
    binding?.autocompleteSearchCategory?.gone()
    binding?.includeNoSearchResultFound?.root?.gone()
    binding?.layoutEtSugestion?.gone()
    binding?.includeCatSuggSelected?.tvCatSelected?.text = selectedCategoryLive?.name
    binding?.includeCatSuggSelected?.tvSubcat?.text = "in ${category?.getCategoryWithoutNewLine()}"
    setAdapterCategory(category)
  }

  private fun setAdapterCategory(dataCategory: CategoryDataModelOv2) {
    categoryList.forEach { it.isSelected = (it.category_key == dataCategory.category_key) }
    adapterCategoryLocal?.notifyDataSetChanged()
  }

  private fun goToNextStep() {
    if (selectedCategoryLive == null) {
      selectedCategoryLive = categoryListLive?.firstOrNull { it0 -> (it0.appexperiencecodedetails?.firstOrNull { it.name == selectedCategory?.experience_code } != null) }
      selectedCategoryLive?.name = ""
      selectedCategoryLive?.fpExperienceCode = selectedCategoryLive?.appexperiencecodedetails?.firstOrNull { it.name == selectedCategory?.experience_code }
    }
    addFragment(R.id.inner_container, BusinessCategoryPreviewFragment.newInstance(Bundle().apply {
      putString(IntentConstant.DESKTOP_PREVIEW.name, selectedCategoryLive?.fpExperienceCode?.desktoppreview?.url)
      putString(IntentConstant.MOBILE_PREVIEW.name, selectedCategoryLive?.fpExperienceCode?.mobilepreview?.url)
      putString(IntentConstant.EXTRA_PHONE_NUMBER.name, phoneNumber)
      putString(IntentConstant.CATEGORY_SUGG_UI.name, selectedCategoryLive?.name ?: "")
      putSerializable(IntentConstant.CATEGORY_DATA.name, selectedCategory)
      putBoolean(IntentConstant.WHATSAPP_CONSENT_FLAG.name, whatsappConsent?:false)
    }), true)
  }

  fun setSelectedCat(category: CategoryDataModelOv2) {
    selectedCategory = category
    binding?.tvNextStep1?.isEnabled = true
  }
}