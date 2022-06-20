package com.festive.poster.ui.promoUpdates.pastUpdates

import android.os.Bundle
import android.util.Log
import com.boost.dbcenterapi.utils.observeOnce
import com.festive.poster.R
import com.festive.poster.base.AppBaseFragment
import com.festive.poster.constant.Constants
import com.festive.poster.constant.RecyclerViewActionType
import com.festive.poster.databinding.FragmentUpdatesListingBinding
import com.festive.poster.models.PosterPackTagModel
import com.festive.poster.models.promoModele.*
import com.festive.poster.models.response.GetTemplateViewConfigResponse
import com.festive.poster.recyclerView.AppBaseRecyclerViewAdapter
import com.festive.poster.recyclerView.BaseRecyclerViewItem
import com.festive.poster.recyclerView.RecyclerItemClickListener
import com.festive.poster.viewmodels.PostUpdatesViewModel
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.pref.clientId
import com.framework.utils.ContentSharing

class UpdatesListingFragment : AppBaseFragment<FragmentUpdatesListingBinding, PostUpdatesViewModel>(), RecyclerItemClickListener {

    private var pastPostListing = ArrayList<PastPostItem>()
    private lateinit var categoryDataList:ArrayList<PastCategoriesModel>
    private lateinit var postCategoryAdapter:AppBaseRecyclerViewAdapter<PastCategoriesModel>
    private lateinit var pastPostListingAdapter:AppBaseRecyclerViewAdapter<PastPostItem>
    private lateinit var tagListAdapter:AppBaseRecyclerViewAdapter<PastTagModel>
    var postType: Int = 0
    var tagArray:MutableList<String> = mutableListOf()
    lateinit var tagListRequest:TagListRequest

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
        getTemplateViewConfig()
        getPostCategories()

        pastPostListingAdapter = AppBaseRecyclerViewAdapter(baseActivity, pastPostListing, this)
        binding.rvPostListing.adapter = pastPostListingAdapter
        apiCallPastUpdates()
    }

    private fun getPostCategories() {
        categoryDataList = PastCategoriesModel().getData(baseActivity)
        postCategoryAdapter = AppBaseRecyclerViewAdapter(baseActivity, categoryDataList, this)
        binding.rvFilterCategory.adapter = postCategoryAdapter
    }

    private fun getTemplateViewConfig() {
        viewModel?.getTemplateConfig(Constants.PROMO_FEATURE_CODE, sessionLocal.fPID, sessionLocal.fpTag)
            ?.observeOnce(this) {
                val response = it as? GetTemplateViewConfigResponse
                response?.let {
                    val tagArray = prepareTagForApi(response.Result.allTemplates.tags)
                    tagListAdapter = AppBaseRecyclerViewAdapter(baseActivity, tagArray, this)
                    binding.rvFilterSubCategory.adapter = tagListAdapter
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
        viewModel?.getPastUpdatesListV6(clientId = clientId, fpId = sessionLocal.fPID, postType = postType, tagListRequest = TagListRequest(tagArray))
            ?.observeOnce( viewLifecycleOwner, {it ->
                if (it.isSuccess()){
                    it as PastUpdatesNewListingResponse

                    it.floats?.let { it1 ->
                        if (it1.isNullOrEmpty()){
                            binding.tvNoPost.visible()
                            binding.rvPostListing.gone()
                        }else {
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

                Log.i("pastUpdates", "PastUpdates: $it")
            })
    }

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        when (actionType) {
            RecyclerViewActionType.PAST_CATEGORY_CLICKED.ordinal -> {
                val pastCategoriesModel = item as PastCategoriesModel
                postType = pastCategoriesModel.postType
                categoryDataList
                postCategoryAdapter.notifyDataSetChanged()
                apiCallPastUpdates()
            }
            RecyclerViewActionType.PAST_TAG_CLICKED.ordinal -> {
                item as PastTagModel
                if (tagArray.contains(item.tag))
                    tagArray.remove(item.tag)
                else
                    tagArray.add(item.tag)
                tagListAdapter.notifyDataSetChanged()
                apiCallPastUpdates()
            }
            RecyclerViewActionType.PAST_SHARE_BUTTON_CLICKED.ordinal -> {
                val pastPostItem = item as PastPostItem
                ContentSharing.share(activity = baseActivity, shareText = pastPostItem.message?:"", imageUri = pastPostItem.imageUri?:"")
            }
            RecyclerViewActionType.PAST_REUSE_BUTTON_CLICKED.ordinal -> {

            }
        }
    }
}