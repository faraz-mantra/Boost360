package com.boost.presignin.ui.newOnboarding

import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.boost.presignin.BuildConfig
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
import com.boost.presignin.model.vertical_categories.CategoriesItem
import com.boost.presignin.recyclerView.AppBaseRecyclerViewAdapter
import com.boost.presignin.recyclerView.BaseRecyclerViewItem
import com.boost.presignin.recyclerView.RecyclerItemClickListener
import com.boost.presignin.rest.response.ResponseDataCategory
import com.boost.presignin.ui.newOnboarding.categoryService.startServiceCategory
import com.boost.presignin.viewmodel.CategoryVideoModel
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.pref.UserSessionManager
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
  protected lateinit var session: UserSessionManager

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
    this.session = UserSessionManager(baseActivity)
    WebEngageController.trackEvent(PS_SIGNUP_CATEGORY_SELECTION_SCREEN_LOAD, PAGE_VIEW, NO_EVENT_VALUE)
    setOnClickListeners()
    initialize()
    if (BuildConfig.FLAVOR.equals("partone") || BuildConfig.FLAVOR.equals("jioonline")) {
      loadLocalCategoryData()
    } else {
      loadLocalVerticalCategoryData()
    }
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

  private fun loadLocalVerticalCategoryData() {
    val webTemplateIds: HashMap<String, String> = hashMapOf("DOC" to "59d74e153872831a6483491e","EDU" to "5a952f3dac626704fc9b6d86","HOS" to "597f48fd38728384d4b85cdd","SPA" to "5b34bd254030c804fbbd8414", "HOT" to "5b3b2b6aec3c7704fee7ae93","CAF" to "5ad9c409889084051f87a4b6","SAL" to "5b34bd254030c804fbbd8414","MFG" to "590b3f09ee786c1d88879129","RTL" to "5b864dd931bfd4054774ec1b","SVC" to "5eda2675d0c6770001f67d91")
    val primaryCategories: HashMap<String, String> = hashMapOf("DOC" to "HEALTHCARE - GENERAL","EDU" to "EDUCATION","HOS" to "HEALTHCARE - CLINICS","SPA" to "SPA", "HOT" to "HOTEL & MOTELS","CAF" to "FOOD & BEVERAGES","SAL" to "SALON","MFG" to "MANUFACTURERS","RTL" to "OTHER RETAIL","SVC" to "OTHER SERVICES")
    adapterLocalCategory()
    val appExerienceCode = when (BuildConfig.FLAVOR) {
      "healthgro" -> "DOC"
      "ardhim" -> "MFG"
      "arantoo" -> "SVC"
      "checkkinn" -> "HOT"
      else -> "MFG"
    }
    if (appExerienceCode=="DOC"){
      showProgress("Please wait ...")
      viewModel?.getVerticalCategories(appExerienceCode)?.observeOnce(viewLifecycleOwner) { it0 ->
        val response = it0.anyResponse as? List<CategoriesItem>
        val categoryListTemp = ArrayList<CategoryDataModel>()
        if (it0.isSuccess() && response.isNullOrEmpty().not()) {
          val mainCategories = response?.filter { it -> it.info != null }
          mainCategories?.forEach { it ->
            val categoryItem = CategoryDataModel(
              experience_code = it.appexperiencecodes[0],
              webTemplateId = webTemplateIds[it.appexperiencecodes[0]],
              category_key = primaryCategories[it.appexperiencecodes[0]],
              category_Name = it.name,
              category_descriptor = "",
              icon = it.info.icon,
              sections = null,
            )
            categoryListTemp.add(categoryItem)
          }
        }
        viewModel?.getVerticalCategories("HOS")?.observeOnce(viewLifecycleOwner) { it0 ->
          val response = it0.anyResponse as? List<CategoriesItem>
          if (it0.isSuccess() && response.isNullOrEmpty().not()) {
            val mainCategories = response?.filter { it -> it.info != null }
            mainCategories?.forEach { it ->
              val categoryItem = CategoryDataModel(
                experience_code = it.appexperiencecodes[0],
                webTemplateId = webTemplateIds[it.appexperiencecodes[0]],
                category_key = primaryCategories[it.appexperiencecodes[0]],
                category_Name = it.name,
                category_descriptor = "",
                icon = it.info.icon,
                sections = null,
              )
              categoryListTemp.add(categoryItem)
            }
          }
          hideProgress()
          categoryList = ArrayList(categoryListTemp.map {
            it.recyclerViewItem = RecyclerViewItemType.CATEGORY_ITEM_OV2.getLayout();it
          })
          categoryNoDataList =
            ArrayList(categoryList.filter { (it.experience_code == "RTL" || it.experience_code == "SVC") })
          adapterCategoryLocal?.notify(categoryList)
        }
      }
    }else{
      showProgress("Please wait ...")
      viewModel?.getVerticalCategories(appExerienceCode)?.observeOnce(viewLifecycleOwner) { it0 ->
        hideProgress()
        val response = it0.anyResponse as? List<CategoriesItem>
        if (it0.isSuccess() && response.isNullOrEmpty().not()) {
          val mainCategories = response?.filter { it -> it.info != null }
          val categoryListTemp = ArrayList<CategoryDataModel>()
          mainCategories?.forEach { it ->
            val categoryItem = CategoryDataModel(
              experience_code = it.appexperiencecodes[0],
              webTemplateId = webTemplateIds[it.appexperiencecodes[0]],
              category_key = primaryCategories[it.appexperiencecodes[0]],
              category_Name = it.name,
              category_descriptor = "",
              icon = it.info.icon,
              sections = null,
            )
            categoryListTemp.add(categoryItem)
          }
          categoryList = ArrayList(categoryListTemp.map {
            it.recyclerViewItem = RecyclerViewItemType.CATEGORY_ITEM_OV2.getLayout();it
          })
          categoryNoDataList =
            ArrayList(categoryList.filter { (it.experience_code == "RTL" || it.experience_code == "SVC") })
          adapterCategoryLocal?.notify(categoryList)
        }
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

    if (!BuildConfig.FLAVOR.equals("partone") || !BuildConfig.FLAVOR.equals("jioonline")) {
      binding.tvNextStep1.backgroundTintList= ContextCompat.getColorStateList(context!!, R.color.buttonTint)
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
    if (this!=null && this.isNotEmpty()){
      for (i in 0 until this.size) {
        val subCategory = this[i].name!!
        for (j in 0 until this[i].appexperiencecodedetails!!.size){
          val categoryResponse = ApiCategoryResponseCategory()
          val categoryDescription = categoryList.firstOrNull { it.experience_code == this[i].appexperiencecodedetails!![j].name }?.getCategoryWithoutNewLine()
          if (categoryDescription.isNullOrEmpty().not()) {
            categoryResponse.fpExperienceCode = this[i].appexperiencecodedetails!![j]
            categoryResponse.name = subCategory
            categoryResponse.subCategoryDescription = categoryDescription
            categoryResponse.searchKeyword = str.toString()
            newFilterList.add(categoryResponse)
          }
        }
      }
    }
    return newFilterList
  }

  private fun setAdapterSearch(filteredList: ArrayList<ApiCategoryResponseCategory>) {
    AppBaseRecyclerViewAdapter(baseActivity, filteredList, this).apply {
      binding?.rvCatSuggestion?.adapter = this
    }
  }

  private fun handleSuggestionNoResult(str: Editable?) {
    binding?.tvNoResultFound?.visible()
    binding?.includeNoSearchResultFound?.root?.visible()
    val list = ArrayList<CategoryDataModel>()
    list.addAll(categoryNoDataList)
    noCatListAdapter = AppBaseRecyclerViewAdapter(baseActivity, ArrayList(list.map { it.textChangeRTLAndSVC = true;it }), this)
    binding?.includeNoSearchResultFound?.rvCategories?.adapter = noCatListAdapter

    didYouMeanAlgorithm(str)
  }

  private fun didYouMeanAlgorithm(str: Editable?) {
    var queryString = str
    if (str?.length ?: 0 >= 3) {
      var newFilterList: List<ApiCategoryResponseCategory>?

      var didYouMeanFilterList = arrayListOf<ApiCategoryResponseCategory>()
      while (didYouMeanFilterList.isNullOrEmpty() && queryString?.length?:0 >= 3) {
        queryString = SpannableStringBuilder(queryString.toString().dropLast(1))
        newFilterList = categoryListLive?.filter {
          it.getNameLower().startsWith(queryString.toString().lowercase())
        }?.take(40)
        didYouMeanFilterList = newFilterList.getFinalList(queryString)
      }
      if (!didYouMeanFilterList.isNullOrEmpty()) {
        binding?.layoutDidYouMean?.visible()
        binding?.tvDidYouMean?.text = "${getString(R.string.did_you_mean) + " " + didYouMeanFilterList[0].name}"
      }
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
        if (!BuildConfig.FLAVOR.equals("partone") || !BuildConfig.FLAVOR.equals("jioonline")) {
          dataCategory.subCategoryName = dataCategory.category_Name!!
          binding.tvNextStep1.backgroundTintList= ContextCompat.getColorStateList(context!!, R.color.buttonTint)
          if (binding?.includeNoSearchResultFound?.root?.visibility == View.VISIBLE) {
            categoryNoDataList.forEach { it.isSelected = (it.category_Name == dataCategory.category_Name) }
            noCatListAdapter?.notifyDataSetChanged()
          } else {
            categoryList.forEach { it.isSelected = (it.category_Name == dataCategory.category_Name) }
            adapterCategoryLocal?.notifyDataSetChanged()
          }
          selectedCategoryLive = null
          setSelectedCat(dataCategory)
        }else{
          if(dataCategory.experience_code == "EDU"){
            dataCategory.subCategoryName = "Education Center"
          } else if(dataCategory.experience_code == "CAF"){
            dataCategory.subCategoryName = "Cafe"
          } else if(dataCategory.experience_code == "RTL"){
            dataCategory.subCategoryName = "Store"
          } else if(dataCategory.experience_code == "SVC"){
            dataCategory.subCategoryName = "SERVICE CENTER"
          } else if(dataCategory.experience_code == "DOC"){
            dataCategory.subCategoryName = "Doctor"
          } else if(dataCategory.experience_code == "HOS"){
            dataCategory.subCategoryName = "Hospital"
          } else if(dataCategory.experience_code == "MFG"){
            dataCategory.subCategoryName = "Manufacturer"
          } else if(dataCategory.experience_code == "HOT"){
            dataCategory.subCategoryName = "Hotel"
          } else if(dataCategory.experience_code == "SAL"){
            dataCategory.subCategoryName = "Hair Salon"
          } else if(dataCategory.experience_code == "SPA"){
            dataCategory.subCategoryName = "Spa"
          }
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
      }
      RecyclerViewActionType.CATEGORY_SUGGESTION_CLICKED.ordinal -> {
        WebEngageController.trackEvent(PS_SIGNUP_CATEGORY_SELECTION_LIST_LOAD, CLICK, NO_EVENT_VALUE)
        selectedCategoryLive = (item as? ApiCategoryResponseCategory) ?: return
        baseActivity.hideKeyBoard()
        val dataCategory = categoryList.firstOrNull { it.experience_code == selectedCategoryLive?.fpExperienceCode?.name }
        dataCategory?.let {
          dataCategory.subCategoryName = selectedCategoryLive!!.name!!
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
      putString(IntentConstant.SUB_CATEGORY_ID.name, selectedCategoryLive?._kid ?: "")
      putSerializable(IntentConstant.CATEGORY_DATA.name, selectedCategory)
      putBoolean(IntentConstant.WHATSAPP_CONSENT_FLAG.name, whatsappConsent ?: false)
    }), true)
  }

  fun setSelectedCat(category: CategoryDataModel) {
    selectedCategory = category
    binding?.tvNextStep1?.isEnabled = true
  }
}