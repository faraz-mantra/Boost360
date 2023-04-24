package com.festive.poster.ui.promoUpdates

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.festive.poster.R
import com.festive.poster.base.AppBaseFragment
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.FragmentBrowseCategoriesBinding
import com.festive.poster.models.BrowseAll
import com.festive.poster.models.asBrowseAllModels
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.recyclerView.RecyclerItemClickListener
import com.festive.poster.utils.WebEngageController
import com.festive.poster.viewmodels.PostUpdatesViewModel
import com.festive.poster.viewmodels.PromoUpdatesViewModel
import com.framework.base.BaseActivity
import com.framework.models.BaseViewModel
import com.framework.rest.NetworkResult
import com.framework.utils.showToast
import com.framework.webengageconstant.CLICK
import com.framework.webengageconstant.CLICKED
import com.framework.webengageconstant.PROMOTIONAL_UPDATE_CATEGORY_CLICK
import com.framework.webengageconstant.Promotional_Update_View_More_Click

class BrowseCategoriesFragment :
    AppBaseFragment<FragmentBrowseCategoriesBinding, PostUpdatesViewModel>(),
    RecyclerItemClickListener {

    private var promoUpdatesViewModel: PromoUpdatesViewModel? = null
    private var categoriesRvAdapter: AppBaseRecyclerViewAdapter<BrowseAll>? = null
    var categoryList = ArrayList<BrowseAll>()
    override fun getLayout(): Int {
        return R.layout.fragment_browse_categories
    }

    override fun getViewModelClass(): Class<PostUpdatesViewModel> {
        return PostUpdatesViewModel::class.java
    }

    companion object {
        fun newInstance(): BrowseCategoriesFragment {
            return BrowseCategoriesFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        promoUpdatesViewModel =
            ViewModelProvider(requireActivity()).get(PromoUpdatesViewModel::class.java)
    }

    private fun getTemplatesData() {
        showProgress()
        promoUpdatesViewModel?.browseAllLData?.observe(viewLifecycleOwner) {
            when (it) {
                is NetworkResult.Success -> {
                    hideProgress()
                    val data = it.data ?: return@observe
                    categoryList.clear()
                    for (category in data) {
                        categoryList.add(
                            BrowseAll(
                                category.name,
                                category.getParentTemplates()!!.size
                            )
                        )
                    }
                    binding.rvBrowse.layoutManager = GridLayoutManager(requireActivity(), 2)
                    categoriesRvAdapter = AppBaseRecyclerViewAdapter(
                        requireActivity() as BaseActivity<*, *>, categoryList, this
                    )
                    binding.rvBrowse.adapter = categoriesRvAdapter
                }
                is NetworkResult.Error -> {
                    hideProgress()
                    showToast(it.msg)
                }

                else -> {}
            }
        }
    }

    override fun onCreateView() {
        super.onCreateView()
        getTemplatesData()

    }


    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        when (actionType) {
            RecyclerViewActionType.BROWSE_ALL_CAT_CLICKED.ordinal -> {
                WebEngageController.trackEvent(Promotional_Update_View_More_Click)
                addFragment(R.id.container, BrowseAllFragment.newInstance(position.toString(), true), true, true)
            }
        }
    }


}