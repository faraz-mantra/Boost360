package com.festive.poster.ui.promoUpdates

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.festive.poster.R
import com.festive.poster.base.AppBaseActivity
import com.festive.poster.base.AppBaseFragment
import com.festive.poster.constant.Constants
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.constant.RecyclerViewItemType
import com.festive.poster.databinding.FragmentFavouriteListBinding
import com.festive.poster.models.*
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.recyclerView.RecyclerItemClickListener
import com.festive.poster.utils.WebEngageController
import com.festive.poster.utils.posterPostClicked
import com.festive.poster.utils.posterWhatsappShareClicked
import com.festive.poster.viewmodels.PostUpdatesViewModel
import com.festive.poster.viewmodels.PromoUpdatesViewModel
import com.framework.base.BaseActivity
import com.framework.pref.UserSessionManager
import com.framework.utils.toArrayList
import com.framework.webengageconstant.Promotional_Update_Category_Click

class FavouriteListFragment : AppBaseFragment<FragmentFavouriteListBinding, PostUpdatesViewModel>(),
    RecyclerItemClickListener {


    private var promoUpdatesViewModel: PromoUpdatesViewModel? = null
    private var session: UserSessionManager? = null
    private var selectedPos: Int = 0
    private var posterRvAdapter: AppBaseRecyclerViewAdapter<BrowseAllTemplate>? = null
    private var categoryAdapter: AppBaseRecyclerViewAdapter<BrowseAllCategory>? = null
    var categoryList = ArrayList<BrowseAllCategory>()

    override fun getLayout(): Int {
        return R.layout.fragment_favourite_list
    }

    override fun getViewModelClass(): Class<PostUpdatesViewModel> {
        return PostUpdatesViewModel::class.java
    }


    override fun onCreateView() {
        super.onCreateView()
        session = UserSessionManager(requireActivity())
        promoUpdatesViewModel =
            ViewModelProvider(requireActivity()).get(PromoUpdatesViewModel::class.java)
        binding.rvCat.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvPosters.layoutManager = LinearLayoutManager(requireActivity())
        getFavTemp()
    }

    private fun getFavTemp() {
        promoUpdatesViewModel?.getFavTemplates()

        showProgress()
        promoUpdatesViewModel?.favData?.observe(viewLifecycleOwner) {
            hideProgress()

            categoryList = it.asBrowseAllModels().toArrayList()
            if (categoryList.isEmpty()) {
                zerothCase(true)
                return@observe
            } else {
                zerothCase(false)
            }
            categoryAdapter = AppBaseRecyclerViewAdapter(
                requireActivity() as BaseActivity<*, *>,
                categoryList,
                this
            )
            binding.rvCat.adapter = categoryAdapter
            switchToSelectedItem()
        }

    }


    private fun zerothCase(b: Boolean) {
        binding.layoutZeroth.isVisible = b
    }

    companion object {

        val BK_POSTER_PACK_LIST = "POSTER_PACK_LIST"
        val BK_SELECTED_POS = "BK_SELECTED_POS"

        fun newInstance(): FavouriteListFragment {
            val bundle: Bundle = Bundle()
            val fragment = FavouriteListFragment()
            fragment.arguments = bundle
            return fragment
        }


    }


    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        when (actionType) {
            RecyclerViewActionType.BROWSE_ALL_POSTER_CAT_CLICKED.ordinal -> {
                WebEngageController.trackEvent(Promotional_Update_Category_Click)

                selectedPos = position
                switchToSelectedItem()
            }
            RecyclerViewActionType.WHATSAPP_SHARE_CLICKED.ordinal -> {
                posterWhatsappShareClicked(
                    item as TemplateUi,
                    requireActivity() as BaseActivity<*, *>
                )
            }
            RecyclerViewActionType.POST_CLICKED.ordinal -> {
                posterPostClicked(item as TemplateUi, requireActivity() as AppBaseActivity<*, *>)
            }
        }

    }

    private fun switchToSelectedItem() {
        categoryList.forEach { it.isSelected = false }
        categoryList.get(selectedPos).isSelected = true
        categoryAdapter?.notifyDataSetChanged()
        categoryList[selectedPos].templates?.let {
            val selectedItem = categoryList.get(selectedPos)
            selectedItem.isSelected = true
            binding.tvCatTitle.text = selectedItem.name
            binding.tvCatSize.text = selectedItem.templates?.size.toString()
            posterRvAdapter = AppBaseRecyclerViewAdapter(
                requireActivity() as BaseActivity<*, *>,
                it.toArrayList(), this
            )
            binding.rvPosters.adapter = posterRvAdapter
            binding.rvPosters.layoutManager = LinearLayoutManager(requireActivity())
        }

    }
}