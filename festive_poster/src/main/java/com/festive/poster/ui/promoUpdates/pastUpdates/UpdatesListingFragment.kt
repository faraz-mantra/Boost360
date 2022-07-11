package com.festive.poster.ui.promoUpdates.pastUpdates

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.content.ContextCompat
import com.festive.poster.R
import com.festive.poster.base.AppBaseFragment
import com.festive.poster.constant.Constants
import com.festive.poster.constant.IntentConstant
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.FragmentUpdatesListingBinding
import com.festive.poster.models.PosterPackTagModel
import com.festive.poster.models.promoModele.*
import com.festive.poster.models.response.GetTemplateViewConfigResponse
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.recyclerView.RecyclerItemClickListener
import com.festive.poster.viewmodels.PostUpdatesViewModel
import com.framework.base.setFragmentType
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.pref.clientId
import com.framework.utils.ContentSharing

class UpdatesListingFragment :
    AppBaseFragment<FragmentUpdatesListingBinding, PostUpdatesViewModel>(),
    RecyclerItemClickListener {

    private var pastPostListing = ArrayList<PastPostItem>()
    private lateinit var categoryDataList: ArrayList<PastCategoriesModel>
    private lateinit var postCategoryAdapter: AppBaseRecyclerViewAdapter<PastCategoriesModel>
    private lateinit var pastPostListingAdapter: AppBaseRecyclerViewAdapter<PastPostItem>
    private lateinit var tagListAdapter: AppBaseRecyclerViewAdapter<PastTagModel>
    var postType: Int = 0
    var isFilterVisible = false
    var tagArray: MutableList<String> = mutableListOf()
    var tagArrayList: ArrayList<PastTagModel> = arrayListOf()

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): UpdatesListingFragment {
            val fragment = UpdatesListingFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_updates_listing
    }

    override fun getViewModelClass(): Class<PostUpdatesViewModel> {
        return PostUpdatesViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        initUI()
    }

    private fun initUI() {
        showSimmer(true)
        getTemplateViewConfig()

        pastPostListingAdapter = AppBaseRecyclerViewAdapter(baseActivity, pastPostListing, this)
        binding.rvPostListing.adapter = pastPostListingAdapter
        //apiCallPastUpdates()
    }

    private fun getPostCategories() {
        categoryDataList = PastCategoriesModel().getData(baseActivity)
        postCategoryAdapter = AppBaseRecyclerViewAdapter(baseActivity, categoryDataList, this)
        binding.rvFilterCategory.adapter = postCategoryAdapter
        apiCallPastUpdates()
    }

    private fun getTemplateViewConfig() {
        viewModel?.getTemplateConfig(
            Constants.PROMO_FEATURE_CODE,
            sessionLocal.fPID,
            sessionLocal.fpTag
        )
            ?.observeOnce(this) {
                val response = it as? GetTemplateViewConfigResponse
                response?.let {
                    tagArrayList.clear()
                    tagArrayList.addAll(prepareTagForApi(response.Result.allTemplates.tags))
                    tagListAdapter = AppBaseRecyclerViewAdapter(baseActivity, tagArrayList, this)
                    binding.rvFilterSubCategory.adapter = tagListAdapter
                    getPostCategories()
                }
            }
    }

    private fun prepareTagForApi(tags: List<PosterPackTagModel>): ArrayList<PastTagModel> {
        val list = ArrayList<PastTagModel>()
        tags.forEach {
            list.add(PastTagModel(it.description, it.icon, it.name, it.tag))
        }
        return list
    }

    private fun apiCallPastUpdates() {
        viewModel?.getPastUpdatesListV6(
            clientId = clientId,
            fpId = sessionLocal.fPID,
            postType = postType,
            tagListRequest = TagListRequest(tagArray)
        )
            ?.observeOnce(viewLifecycleOwner, { it ->
                hideProgress()
                if (it.isSuccess()) {
                    it as PastUpdatesNewListingResponse

                    it.floats?.let { it1 ->
                        if (it1.isNullOrEmpty()) {
                            binding.tvNoPost.visible()
                            binding.rvPostListing.gone()
                        } else {
                            binding.tvNoPost.gone()
                            binding.rvPostListing.visible()
                            pastPostListing.clear()
                            pastPostListing.addAll(it1)
                            pastPostListingAdapter.notifyDataSetChanged()
                        }

                        //Setting Category Counts
                        if (categoryDataList.isNotEmpty()) {
                            categoryDataList[0].categoryCount = it.totalCount ?: 0
                            categoryDataList[1].categoryCount = it.postCount ?: 0
                            categoryDataList[2].categoryCount = it.imageCount ?: 0
                            categoryDataList[3].categoryCount = it.textCount ?: 0
                            postCategoryAdapter.notifyItemRangeChanged(0, 4)
                        }
                    }

                }
                showSimmer(false)
                Log.i("pastUpdates", "PastUpdates: $it")
            })
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        when (actionType) {
            RecyclerViewActionType.PAST_CATEGORY_CLICKED.ordinal -> {
                showProgress()
                val pastCategoriesModel = item as PastCategoriesModel
                postType = pastCategoriesModel.postType
                tagChanges()
                postCategoryAdapter.notifyDataSetChanged()
                apiCallPastUpdates()
            }
            RecyclerViewActionType.PAST_TAG_CLICKED.ordinal -> {
                showProgress()
                item as PastTagModel
                if (tagArray.contains(item.tag))
                    tagArray.remove(item.tag)
                else
                    tagArray.add(item.tag)
                tagListAdapter.notifyDataSetChanged()
                apiCallPastUpdates()
            }
            RecyclerViewActionType.PAST_SHARE_BUTTON_CLICKED.ordinal -> {
                showProgress()
                val pastPostItem = item as PastPostItem
                ContentSharing.share(
                    activity = baseActivity,
                    shareText = pastPostItem.message ?: "",
                    imageUri = pastPostItem.imageUri ?: ""
                )
                hideProgress()
            }
            RecyclerViewActionType.PAST_REUSE_BUTTON_CLICKED.ordinal -> {
                item as PastPostItem
                val intent = Intent(requireActivity(), Class.forName("com.appservice.ui.updatesBusiness.UpdateBusinessContainerActivity"))
                    .putExtra(IntentConstant.REUSE_PAST_UPDATE_MESSAGE_TEXT.name, item.message.toString())
                    .putExtra(IntentConstant.REUSE_PAST_UPDATE_IMAGE.name, item.imageUri.toString())
                intent.setFragmentType("ADD_UPDATE_BUSINESS_FRAGMENT_V2")
                startActivity(intent)
            }
        }
    }

    private fun tagChanges() {
        tagArrayList.forEach {
            it.isSelected = false
        }
        tagArray.clear()
        tagListAdapter.notifyDataSetChanged()
        binding.tagWrapper.apply {
            if (postType == 0 || postType == 1) {
                visible()
            } else {
                gone()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.filter_menu_past, menu)
        /* val menuItem = menu.findItem(R.id.filter_past)
         menuItem.actionView.setOnClickListener {
             menu.performIdentifierAction(menuItem.itemId, 0)
         }*/
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.help_past -> {
                showShortToast(getString(R.string.coming_soon))
                return true
            }
            R.id.filter_past -> {
                if (isFilterVisible) {
                    binding.rvFilterCategory.gone()
                    binding.tagWrapper.gone()
                    item.icon =
                        ContextCompat.getDrawable(baseActivity, R.drawable.ic_filter_hollow_past)
                } else {
                    binding.rvFilterCategory.visible()
                    binding.tagWrapper.visible()
                    item.icon =
                        ContextCompat.getDrawable(baseActivity, R.drawable.ic_filter_funnel_white)
                }
                isFilterVisible = isFilterVisible.not()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSimmer(isSimmer: Boolean) {
        binding.root.apply {
            if (isSimmer) {
                binding.shimmerLayoutPast.parentShimmerLayout.visible()
                binding.shimmerLayoutPast.parentShimmerLayout.startShimmer()
                binding.rvPostListing.gone()
                binding.tagWrapper.gone()
            } else {
                binding.rvPostListing.visible()
                binding.shimmerLayoutPast.parentShimmerLayout.gone()
                binding.shimmerLayoutPast.parentShimmerLayout.stopShimmer()
            }
        }
    }

}