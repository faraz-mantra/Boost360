package com.boost.presignin.ui.newOnboarding

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
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
import com.framework.base.BaseActivity
import com.framework.extensions.gone
import com.framework.extensions.isVisible
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.utils.showDecoration
import com.framework.utils.toArrayList
import com.framework.webengageconstant.*
import com.google.gson.Gson

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

    private var selectedCat:CategoryDataModelOv2?=null
    private var categorylocalResponse: ResponseDataCategoryOv2?=null
    private lateinit var baseAdapter: AppBaseRecyclerViewAdapter<CategoryDataModelOv2>
    private var categoryList = ArrayList<CategoryDataModelOv2>()
    private var apiCategoryList:ArrayList<CategorySuggestionUiModel>?=null
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

        binding?.autocompleteSearchCategory?.setOnFocusChangeListener { view, b ->
            if (b){

            }else{
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

        viewModel?.getCategoriesOv2(requireContext())?.observeOnce(viewLifecycleOwner) {
            if (it.error != null) return@observeOnce
            categorylocalResponse = it as ResponseDataCategoryOv2
            loadCategoryFromApi()

            baseAdapter.addItems(categoryList.apply {
                clear()
                if (CategoryDataModelOv2.getSavedStateCategory() != null) {
                    val data = categorylocalResponse?.data!!
                    addAll(data)
                } else
                    addAll(categorylocalResponse?.data!!)
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
            if (apiCategoryList!=null){
                binding?.layoutEtSugestion?.visible()
                binding?.linearFeaturedCategories?.gone()
                val filterdList = apiCategoryList?.filter { category->category.category.contains(it.toString(),ignoreCase = true)}!!.toArrayList()
                filterdList.forEach { filterdCat->
                    filterdCat.searchKeyword =it.toString()
                }
                if (filterdList.isEmpty()){
                    handleSuggestionNoResult()
                }else{
                    binding?.tvNoResultFound?.gone()
                    binding?.includeNoSearchResultFound?.root?.gone()
                }

                val adapter = AppBaseRecyclerViewAdapter<CategorySuggestionUiModel>(requireActivity() as BaseActivity<*, *>
                    ,filterdList,this)
                binding?.rvCatSuggestion?.adapter = adapter
                binding?.rvCatSuggestion?.layoutManager = LinearLayoutManager(requireActivity())
                binding?.rvCatSuggestion?.showDecoration()
            }
        }
    }

    private fun handleSuggestionNoResult() {
        binding?.tvNoResultFound?.visible()
        binding?.includeNoSearchResultFound?.root?.visible()
        val list = ArrayList<CategoryDataModelOv2>()
        list.add(
            categorylocalResponse?.data?.find { it.experience_code=="RTL" }?.apply {
                category_Name="Do you sell products?"
            }!!
        )

        list.add(
            categorylocalResponse?.data?.find { it.experience_code=="SVC" }?.apply {
                category_Name="Do you provide services?"
            }!!
        )

        val adapter = AppBaseRecyclerViewAdapter(requireActivity() as BaseActivity<*, *>
            ,list,this)
        binding?.includeNoSearchResultFound?.rvCategories?.adapter = adapter
        binding?.includeNoSearchResultFound?.rvCategories?.layoutManager = LinearLayoutManager(requireActivity())


    }

    private fun loadCategoryFromApi() {
        showProgress()
        viewModel?.getCategoriesFromApi()?.observe(viewLifecycleOwner,{

            apiCategoryList = ArrayList()
            if (it.isSuccess()){
                it as ApiCategoryResponse
                it.Data.firstOrNull()?.categories?.forEach { cat->
                    cat.appexperiencecodes.forEach { appExp->
                      val subCat =  categorylocalResponse!!.data!!.find { localCat->localCat.experience_code==appExp }!!.category_Name!!
                        apiCategoryList?.add(CategorySuggestionUiModel(cat.name,subCat,appExp,""))
                    }
                }

            }
            hideProgress()
        })
    }

    private fun setOnClickListeners() {
        binding?.tvNextStep1?.setOnClickListener {
          goToNextStep()
        }
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        when (actionType) {
            RecyclerViewActionType.CATEGORY_ITEM_CLICKED.ordinal -> {
               setSelectedCat(item as CategoryDataModelOv2)
                for (listItem in categoryList) {
                    listItem.isSelected = (listItem.category_key == selectedCat?.category_key)
                }
                binding?.rvCategories?.post { baseAdapter.notifyDataSetChanged() }
               // if (categoryList.filter { it.isSelected }.isNullOrEmpty()) binding?.confirmButton?.gone() else binding?.confirmButton?.visible()
            }

            RecyclerViewActionType.CATEGORY_SUGGESTION_CLICKED.ordinal->{
                item as CategorySuggestionUiModel
                setSelectedCat(categoryList.find { it.experience_code==item.appExpCode }!!)
                goToNextStep()
            }
        }

    }

    private fun goToNextStep() {
        addFragment(R.id.inner_container,BusinessCategoryPreviewFragment.newInstance(Bundle()
            .apply
            {
                putString(IntentConstant.EXTRA_PHONE_NUMBER.name,phoneNumber)
                putString(IntentConstant.CATEGORY_DATA.name,Gson().toJson(selectedCat))
            }),true)
    }

    fun setSelectedCat(cat:CategoryDataModelOv2){
        selectedCat = cat
        binding?.tvNextStep1?.isEnabled = true
    }
}