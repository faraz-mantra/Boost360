package com.boost.presignin.ui.newOnboarding

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.boost.presignin.R
import com.boost.presignin.base.AppBaseFragment
import com.boost.presignin.constant.IntentConstant
import com.boost.presignin.constant.RecyclerViewActionType
import com.boost.presignin.databinding.LayoutSetUpMyWebsiteStep1Binding
import com.boost.presignin.helper.WebEngageController
import com.boost.presignin.model.CategorySuggestionUiModel
import com.boost.presignin.model.category.*
import com.boost.presignin.recyclerView.AppBaseRecyclerViewAdapter
import com.boost.presignin.recyclerView.BaseRecyclerViewItem
import com.boost.presignin.recyclerView.RecyclerItemClickListener
import com.boost.presignin.rest.response.ResponseDataCategoryOv2
import com.boost.presignin.viewmodel.CategoryVideoModel
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.utils.showDecoration
import com.framework.utils.toArrayList
import com.framework.webengageconstant.*
import com.google.gson.Gson

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
  private var notCatFoundList: ArrayList<CategoryDataModelOv2>? = null
  private var apiCatResponse: ApiCategoryResponse? = null
  private var selectedCat: CategoryDataModelOv2? = null
  private var selectedCatSugg: CategorySuggestionUiModel? = null
  private var categorylocalResponse: ResponseDataCategoryOv2? = null
  private var baseAdapter: AppBaseRecyclerViewAdapter<CategoryDataModelOv2>? = null
  private var categoryList = ArrayList<CategoryDataModelOv2>()
  private var apiCategoryList: ArrayList<CategorySuggestionUiModel>? = null
  val noResultCat1UiTitle = "Do you sell products?"
  var noResultCat1OriginalTitle: String? = null

  val noResultCat2UiTitle = "Do you provide services?"
  var noResultCat2OriginalTitle: String? = null

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
    setOnClickListeners()
    WebEngageController.trackEvent(PS_BUSINESS_CATEGORY_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
    baseAdapter = AppBaseRecyclerViewAdapter(baseActivity, ArrayList(), this)

    binding?.autocompleteSearchCategory?.setOnFocusChangeListener { view, b ->
      if (b) {
      } else {
        binding?.layoutEtSugestion?.gone()
        binding?.linearFeaturedCategories?.visible()
      }
    }
    observeCategorySearch()
    activity?.onBackPressedDispatcher?.addCallback(object : OnBackPressedCallback(true) {
      override fun handleOnBackPressed() {
        CategoryDataModel.clearSelection()
        baseActivity.finishAfterTransition()
      }
    })
    binding?.rvCategories?.adapter = baseAdapter
    binding?.rvCategories?.layoutManager = LinearLayoutManager(requireActivity())

    viewModel?.getCategoriesOv2(baseActivity)?.observeOnce(viewLifecycleOwner) {
      if (it.error != null) return@observeOnce
      categorylocalResponse = it as ResponseDataCategoryOv2
      noResultCat1OriginalTitle = categorylocalResponse?.data?.find { it.experience_code == "RTL" }?.category_Name
      noResultCat2OriginalTitle = categorylocalResponse?.data?.find { it.experience_code == "SVC" }?.category_Name
      loadCategoryFromApi()
      baseAdapter?.addItems(categoryList.apply {
        clear()
        if (CategoryDataModelOv2.getSavedStateCategory() != null) {
          val data = categorylocalResponse?.data!!
          addAll(data)
        } else addAll(categorylocalResponse?.data!!)
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

  private fun observeCategorySearch() {
    binding?.autocompleteSearchCategory?.addTextChangedListener {
      if (apiCategoryList != null) {
        binding?.layoutEtSugestion?.visible()
        binding?.linearFeaturedCategories?.gone()
        val filteredList = apiCategoryList?.filter { category -> category.category.contains(it.toString(), ignoreCase = true) }?.toArrayList() ?: arrayListOf()
        filteredList.forEach { list -> list.searchKeyword = it.toString() }
        if (filteredList.isNullOrEmpty()) {
          handleSuggestionNoResult()
        } else {
          binding?.tvNoResultFound?.gone()
          binding?.includeNoSearchResultFound?.root?.gone()
        }
        val adapter = AppBaseRecyclerViewAdapter(baseActivity, filteredList, this)
        binding?.rvCatSuggestion?.adapter = adapter
        binding?.rvCatSuggestion?.layoutManager = LinearLayoutManager(baseActivity)
        binding?.rvCatSuggestion?.showDecoration()
      }
    }
  }

  private fun handleSuggestionNoResult() {
    binding?.tvNoResultFound?.visible()
    binding?.includeNoSearchResultFound?.root?.visible()
    notCatFoundList = ArrayList()
    categorylocalResponse?.data?.find { it.experience_code == "RTL" }?.apply { category_Name = noResultCat1UiTitle }?.let { it1 -> notCatFoundList?.add(it1) }
    categorylocalResponse?.data?.find { it.experience_code == "SVC" }?.apply { category_Name = noResultCat2UiTitle }?.let { it1 -> notCatFoundList?.add(it1) }
    noCatListAdapter = AppBaseRecyclerViewAdapter(baseActivity, notCatFoundList ?: arrayListOf(), this)
    binding?.includeNoSearchResultFound?.rvCategories?.adapter = noCatListAdapter
    binding?.includeNoSearchResultFound?.rvCategories?.layoutManager = LinearLayoutManager(requireActivity())
  }

  private fun loadCategoryFromApi() {
    showProgress()
    viewModel?.getCategoriesFromApi()?.observeOnce(viewLifecycleOwner, {
      apiCategoryList = ArrayList()
      apiCatResponse = it as? ApiCategoryResponse
      if (apiCatResponse?.isSuccess() == true) {
        apiCatResponse?.Data?.firstOrNull()?.categories?.forEach { cat ->
          cat.appexperiencecodedetails?.forEach { appExpDet ->
            val subCat = categorylocalResponse?.data?.find { localCat -> localCat.experience_code == appExpDet.name }?.category_Name
            apiCategoryList?.add(CategorySuggestionUiModel(cat.name ?: "", subCat ?: "", appExpDet.name ?: "", ""))
          }
        }
      }
      hideProgress()
    })
  }

  private fun setOnClickListeners() {
    binding?.tvNextStep1?.setOnClickListener { goToNextStep() }
    binding?.includeCatSuggSelected?.root?.setOnClickListener {
      binding?.includeCatSuggSelected?.root?.gone()
      binding?.autocompleteSearchCategory?.visible()
      binding?.layoutEtSugestion?.visible()

    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.CATEGORY_ITEM_CLICKED.ordinal -> {
        setSelectedCat(item as CategoryDataModelOv2)
        for (listItem in categoryList) {
          listItem.isSelected = (listItem.category_key == selectedCat?.category_key)
        }
        binding?.rvCategories?.post { baseAdapter?.notifyDataSetChanged() }

        if (notCatFoundList != null) {
          for (listItem in notCatFoundList ?: arrayListOf()) {
            listItem.isSelected = (listItem.category_key == selectedCat?.category_key)
          }
          noCatListAdapter?.notifyDataSetChanged()
        }
        // if (categoryList.filter { it.isSelected }.isNullOrEmpty()) binding?.confirmButton?.gone() else binding?.confirmButton?.visible()
      }
      RecyclerViewActionType.CATEGORY_SUGGESTION_CLICKED.ordinal -> {
        item as CategorySuggestionUiModel
        selectedCatSugg = item
        setSelectedCat(categoryList.find { it.experience_code == item.appExpCode }!!)
        showCatSugesstionSelected()
      }
    }
  }

  private fun showCatSugesstionSelected() {
    binding?.includeCatSuggSelected?.root?.visible()
    binding?.layoutEtSugestion?.gone()
    binding?.includeCatSuggSelected?.tvCatSelected?.text = selectedCatSugg?.category
    binding?.includeCatSuggSelected?.tvSubcat?.text = selectedCatSugg?.subCategory
  }

  private fun goToNextStep() {
    var mobilePreview: String? = null
    selectedCat?.experience_code
    var desktopPreview: String? = null
    loop@ for (cat in apiCatResponse?.Data?.firstOrNull()?.categories ?: arrayListOf()) {
      mobilePreview = cat.appexperiencecodedetails?.find { expCodeDet ->
        expCodeDet.name == selectedCat?.experience_code || expCodeDet.name == selectedCatSugg?.appExpCode
      }?.mobilepreview?.url
      if (mobilePreview != null) break@loop
    }
    loop@ for (cat in apiCatResponse?.Data?.firstOrNull()?.categories ?: arrayListOf()) {
      desktopPreview = cat.appexperiencecodedetails?.find { expCodeDet ->
        expCodeDet.name == selectedCat?.experience_code || expCodeDet.name == selectedCatSugg?.appExpCode
      }?.desktoppreview?.url
      if (desktopPreview != null) break@loop
    }

    if (selectedCat != null) {
      if (selectedCat?.category_Name == noResultCat1UiTitle) {
        selectedCat?.category_Name = noResultCat1OriginalTitle
      }
      if (selectedCat?.category_Name == noResultCat2UiTitle) {
        selectedCat?.category_Name = noResultCat2OriginalTitle
      }
    }

    addFragment(R.id.inner_container, BusinessCategoryPreviewFragment.newInstance(Bundle().apply {
      putString(IntentConstant.DESKTOP_PREVIEW.name, desktopPreview)
      putString(IntentConstant.MOBILE_PREVIEW.name, mobilePreview)
      putString(IntentConstant.EXTRA_PHONE_NUMBER.name, phoneNumber)
      putString(IntentConstant.CATEGORY_SUGG_UI.name, Gson().toJson(selectedCatSugg))
      putString(IntentConstant.CATEGORY_DATA.name, Gson().toJson(selectedCat))
      putBoolean(IntentConstant.WHATSAPP_CONSENT_FLAG.name, whatsappConsent == true)
    }), true)
  }

  fun setSelectedCat(cat: CategoryDataModelOv2) {
    selectedCat = cat
    binding?.tvNextStep1?.isEnabled = true
  }
}