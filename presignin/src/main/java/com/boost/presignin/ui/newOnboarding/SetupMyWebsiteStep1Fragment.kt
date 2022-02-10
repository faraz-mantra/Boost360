package com.boost.presignin.ui.newOnboarding

import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.constant.RecyclerViewActionType
import com.boost.presignin.constant.RecyclerViewItemType
import com.boost.presignin.databinding.LayoutSetUpMyWebsiteStep1Binding
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.model.category.ApiCategoryResponseCategory
import com.boost.presignin.model.category.CategoryDataModel
import com.boost.presignin.model.category.getCategoryLiveData
import com.boost.presignin.recyclerView.AppBaseRecyclerViewAdapter
import com.boost.presignin.recyclerView.BaseRecyclerViewItem
import com.boost.presignin.recyclerView.RecyclerItemClickListener
import com.boost.presignin.rest.response.ResponseDataCategory
import com.boost.presignin.ui.newOnboarding.categoryService.startServiceCategory
import com.boost.presignin.viewmodel.CategoryVideoModel
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.utils.hideKeyBoard
import com.framework.utils.onDone
import com.framework.utils.onRightDrawableClicked
import com.framework.utils.showKeyBoard
import com.framework.webengageconstant.*

class SetupMyWebsiteStep1Fragment : AppBaseFragment<LayoutSetUpMyWebsiteStep1Binding, CategoryVideoModel>(), RecyclerItemClickListener {

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): SetupMyWebsiteStep1Fragment {
      val fragment = SetupMyWebsiteStep1Fragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  private var noCatListAdapter: AppBaseRecyclerViewAdapter<CategoryDataModel>? = null
  private var adapterCategoryLocal: AppBaseRecyclerViewAdapter<CategoryDataModel>? = null

  private val categoryListLive: List<ApiCategoryResponseCategory>?
    get() {
      return getCategoryLiveData()
    }

  private var categoryList: ArrayList<CategoryDataModel> = arrayListOf()
  private var categoryNoDataList: ArrayList<CategoryDataModel> = arrayListOf()
  private var selectedCategory: CategoryDataModel? = null
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
    WebEngageController.trackEvent(PS_SIGNUP_CATEGORY_SELECTION_SCREEN_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
    setOnClickListeners()
    initialize()
    loadLocalCategoryData()
  }

  private fun loadLocalCategoryData() {
    adapterLocalCategory()
    viewModel?.getCategoriesOv2(baseActivity)?.observeOnce(viewLifecycleOwner) { it0 ->
      val response = it0 as? ResponseDataCategory
      if (it0.isSuccess() && response?.data.isNullOrEmpty().not()) {
        categoryList = ArrayList(response!!.data!!.map { it.recyclerViewItem = RecyclerViewItemType.CATEGORY_ITEM_OV2.getLayout();it })
        categoryNoDataList = ArrayList(categoryList.filter { (it.experience_code == "RTL" || it.experience_code == "SVC") })
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
    if (getCategoryLiveData().isNullOrEmpty()) baseActivity.startServiceCategory()
    childFragmentManager.addOnBackStackChangedListener {
        CategoryDataModel.clearSelection()
        baseActivity.finishAfterTransition()
    }

    binding?.autocompleteSearchCategory?.onDone { binding?.autocompleteSearchCategory?.clearFocus() }
    binding?.autocompleteSearchCategory?.setOnFocusChangeListener { _, hasFocus ->
      if (binding?.includeCatSuggSelected?.root?.visibility != View.VISIBLE) uiChangeSearchCategory(hasFocus)
    }
    binding?.autocompleteSearchCategory?.onRightDrawableClicked {
      baseActivity.hideKeyBoard()
      it.setText("")
      it.clearFocus()
      uiChangeSearchCategory(false)
    }
  }

  private fun uiChangeSearchCategory(hasFocus: Boolean) {
    if ((hasFocus.not()) && binding?.autocompleteSearchCategory?.text?.toString().isNullOrEmpty()) {
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

  private fun observeCategorySearch() {
    binding?.autocompleteSearchCategory?.addTextChangedListener { str ->
      searchCategoryLiveItem(str)
      binding?.autocompleteSearchCategory?.post {
        if (str?.isNotEmpty() == true) {
          binding?.autocompleteSearchCategory?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search_onboarding, 0, R.drawable.ic_close_black_rounded, 0)
        } else {
          binding?.autocompleteSearchCategory?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search_onboarding, 0, 0, 0)
        }
      }
    }
  }

  private fun searchCategoryLiveItem(str: Editable?) {
    if (categoryList.isNullOrEmpty().not() && categoryListLive.isNullOrEmpty().not() && str?.toString().isNullOrEmpty().not()) {
      binding?.layoutEtSugestion?.visible()
      binding?.linearFeaturedCategories?.gone()
      categoryListLive?.filter {
        it.getNameLower().startsWith(str.toString().lowercase()) || it.getNameLower().contains(str.toString().lowercase())
      }?.take(40).apply { setSearchAdapterListItem(str) }
    } else {
      (categoryListLive ?: arrayListOf()).take(40).apply { setSearchAdapterListItem(str) }
    }
  }

  private fun List<ApiCategoryResponseCategory>?.setSearchAdapterListItem(str: Editable?) {
    val newFilterList = getFinalList(str)
    if (newFilterList.isNullOrEmpty().not()) {
      binding?.tvNoResultFound?.gone()
      binding?.layoutDidYouMean?.gone()
      binding?.includeNoSearchResultFound?.root?.gone()
    } else {
      handleSuggestionNoResult(str)
    }
    setAdapterSearch(newFilterList)
  }

  private fun List<ApiCategoryResponseCategory>?.getFinalList(str: Editable?): ArrayList<ApiCategoryResponseCategory> {
    val newFilterList = arrayListOf<ApiCategoryResponseCategory>()
    this?.forEach { cItem ->
      cItem.appexperiencecodedetails?.forEach { exp ->
        val categoryName = categoryList.firstOrNull { it.experience_code == exp.name }?.getCategoryWithoutNewLine()
        if (categoryName.isNullOrEmpty().not()) {
          cItem.fpExperienceCode = exp
          cItem.subCategory = categoryName
          cItem.searchKeyword = str.toString()
          newFilterList.add(cItem)
        }
      }
    }
    return newFilterList
  }

  private fun setAdapterSearch(filteredList: ArrayList<ApiCategoryResponseCategory>) {
    AppBaseRecyclerViewAdapter(
      baseActivity,
      filteredList,
      this
    ).apply { binding?.rvCatSuggestion?.adapter = this }
  }

  private fun handleSuggestionNoResult(str: Editable?) {
    binding?.tvNoResultFound?.visible()
    binding?.includeNoSearchResultFound?.root?.visible()
    val list = ArrayList<CategoryDataModel>()
    list.addAll(categoryNoDataList)
    noCatListAdapter = AppBaseRecyclerViewAdapter(
      baseActivity,
      ArrayList(list.map { it.textChangeRTLAndSVC = true;it }),
      this
    )
    binding?.includeNoSearchResultFound?.rvCategories?.adapter = noCatListAdapter

    //didYouMeanAlgorithm(str)
  }

  private fun didYouMeanAlgorithm(str: Editable?) {
    var queryString = str
    if (str?.length ?: 0 >= 3) {
      var newFilterList: List<ApiCategoryResponseCategory>?

      var didYouMeanFilterList = arrayListOf<ApiCategoryResponseCategory>()
      while (didYouMeanFilterList.isNullOrEmpty()) {
        queryString = SpannableStringBuilder(queryString.toString().dropLast(1))
        newFilterList = categoryListLive?.filter {
          it.getNameLower().startsWith(queryString.toString().lowercase())
        }?.take(40)
        didYouMeanFilterList = newFilterList.getFinalList(queryString)
      }

      binding?.layoutDidYouMean?.visible()
      binding?.tvDidYouMean?.text = "${getString(R.string.did_you_mean) + " "}"
    }
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
    binding?.tvClickHereStill?.setOnClickListener {
      WebEngageController.trackEvent(PS_SIGNUP_CATEGORY_CANT_FIND_CLICK, CLICK, NO_EVENT_VALUE)
    }
    binding?.layoutDidYouMean?.setOnClickListener {
      WebEngageController.trackEvent(PS_SIGNUP_CATEGORY_DID_YOU_MEAN_CLICK, CLICK, NO_EVENT_VALUE)
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.CATEGORY_ITEM_CLICKED.ordinal -> {
        WebEngageController.trackEvent(PS_SIGNUP_CATEGORY_SELECTION_MAIN_LOAD, CLICK, NO_EVENT_VALUE)
        val dataCategory = (item as? CategoryDataModel) ?: return
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
        WebEngageController.trackEvent(PS_SIGNUP_CATEGORY_SELECTION_LIST_LOAD, CLICK, NO_EVENT_VALUE)
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

  private fun showCatSuggestionSelected(category: CategoryDataModel) {
    binding?.tvTitle?.visible()
    binding?.tvSubtitle?.visible()
    binding?.includeCatSuggSelected?.root?.visible()
    binding?.linearFeaturedCategories?.visible()
    binding?.autocompleteSearchCategory?.gone()
    binding?.includeNoSearchResultFound?.root?.gone()
    binding?.layoutEtSugestion?.gone()
    binding?.includeCatSuggSelected?.tvCatSelected?.text = selectedCategoryLive?.name
    binding?.includeCatSuggSelected?.tvSubcat?.text = "in ${category.getCategoryWithoutNewLine()}"
    setAdapterCategory(category)
  }

  private fun setAdapterCategory(dataCategory: CategoryDataModel) {
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
      putBoolean(IntentConstant.WHATSAPP_CONSENT_FLAG.name, whatsappConsent ?: false)
    }), true)
  }

  fun setSelectedCat(category: CategoryDataModel) {
    selectedCategory = category
    binding?.tvNextStep1?.isEnabled = true
  }
}