package com.appservice.ui.updatesBusiness

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.FragmentUpdatesListingBinding
import com.appservice.model.updateBusiness.pastupdates.*
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.viewmodel.PastUpdatesViewModel
import com.framework.constants.IntentConstants
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.pref.clientId
import com.framework.utils.ContentSharing
import com.framework.utils.saveAsTempFile
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PastUpdatesListingFragment : AppBaseFragment<FragmentUpdatesListingBinding, PastUpdatesViewModel>(),
    RecyclerItemClickListener {

    private var pastPostListing = ArrayList<PastPostItem>()
    private lateinit var categoryDataList: ArrayList<PastCategoriesModel>
    private lateinit var postCategoryAdapter: AppBaseRecyclerViewAdapter<PastCategoriesModel>
    private lateinit var pastPostListingAdapter: AppBaseRecyclerViewAdapter<PastPostItem>
    private lateinit var tagListAdapter: AppBaseRecyclerViewAdapter<PastPromotionalCategoryModel>
    var postType: Int = 0
    var isFilterVisible = false
    var tagArray: MutableList<String> = mutableListOf()
    var promoUpdateCatsList: ArrayList<PastPromotionalCategoryModel> = arrayListOf()

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle? = null): PastUpdatesListingFragment {
            val fragment = PastUpdatesListingFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getLayout(): Int {
        return R.layout.fragment_updates_listing
    }

    override fun getViewModelClass(): Class<PastUpdatesViewModel> {
        return PastUpdatesViewModel::class.java
    }

    override fun onCreateView() {
        super.onCreateView()
        initUI()
        setOnClickListener(binding.btnPostNewUpdate)

    }

    private fun initUI() {
        baseActivity.window.statusBarColor = getColor(R.color.color_4a4a4a_jio_ec008c)
        showSimmer(true)
        getTemplateViewConfig()


        //apiCallPastUpdates()
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v){
            binding.btnPostNewUpdate->{
                startUpdateFragmentActivity(FragmentType.ADD_UPDATE_BUSINESS_FRAGMENT_V2)
            }
        }
    }

    private fun getPostCategories() {
        categoryDataList = PastCategoriesModel().getData(baseActivity)
        postCategoryAdapter = AppBaseRecyclerViewAdapter(baseActivity, categoryDataList, this)
        binding.rvFilterCategory.adapter = postCategoryAdapter
        apiCallPastUpdates()
    }

    private fun getTemplateViewConfig() {
        viewModel?.promoUpdatesCats
            ?.observeOnce(this) {
                promoUpdateCatsList.clear()
                promoUpdateCatsList.addAll(it)
                tagListAdapter = AppBaseRecyclerViewAdapter(baseActivity, promoUpdateCatsList, this)
                binding.rvFilterSubCategory.adapter = tagListAdapter
                getPostCategories()
            }
    }


    private fun apiCallPastUpdates() {
        viewModel?.getPastUpdatesListV6(
            clientId = clientId,
            fpId = sessionLocal.fPID,
            postType = postType,
            tagListRequest = TagListRequest(tagArray)
        )
            ?.observeOnce(viewLifecycleOwner) { it ->
                hideProgress()
                if (it.isSuccess()) {
                    it as PastUpdatesNewListingResponse

                  handleListResponse(it)

                }
                showSimmer(false)
                Log.i("pastUpdates", "PastUpdates: $it")
            }
    }

    private fun handleListResponse(it: PastUpdatesNewListingResponse) {
        it.floats?.let { list ->
            if (list.isNullOrEmpty()) {
                binding.tvNoPost.visible()
                pastPostListing.clear()
                pastPostListingAdapter.notifyDataSetChanged()
            } else {
                binding.tvNoPost.gone()
                binding.rvPostListing.visible()
                pastPostListing.clear()
                list.forEach {item->
                    item.category = promoUpdateCatsList.find { category->
                        item.tags?.firstOrNull()==category.id
                    }
                }
                pastPostListing.addAll(list)
                pastPostListingAdapter = AppBaseRecyclerViewAdapter(baseActivity, pastPostListing, this)
                binding.rvPostListing.adapter = pastPostListingAdapter
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

    override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
        when (actionType) {
            RecyclerViewActionType.PAST_CATEGORY_CLICKED.ordinal -> {
                showProgress()
                val pastCategoriesModel = item as PastCategoriesModel

                categoryDataList.forEach { it.isSelected=false }
                categoryDataList[position].isSelected=true

                postType = pastCategoriesModel.postType
                tagChanges()
                postCategoryAdapter.notifyDataSetChanged()
                apiCallPastUpdates()
            }
            RecyclerViewActionType.PAST_TAG_CLICKED.ordinal -> {
                showProgress()
                item as PastPromotionalCategoryModel
                if (tagArray.contains(item.id))
                    tagArray.remove(item.id)
                else
                    tagArray.add(item.id)

                binding.rvFilterSubCategory.postDelayed(
                    Runnable {
                        tagListAdapter.notifyDataSetChanged()
                    }, 100
                )
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
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        if (item.imageUri.toString().isNullOrBlank().not()) {
                            val bitmapPastUpdateReuse =
                                Picasso.get().load(item.imageUri.toString()).get()
                            val saveAsTempFile = bitmapPastUpdateReuse.saveAsTempFile()
                            startActivity(
                                Intent(
                                    requireActivity(), Class.forName(
                                        "com.festive.poster.ui.promoUpdates.PostPreviewSocialActivity"
                                    )
                                )
                                    .putExtra(
                                        IntentConstants.MARKET_PLACE_ORIGIN_NAV_DATA,
                                        Bundle().apply {
                                            putString(
                                                IntentConstants.IK_CAPTION_KEY,
                                                item.message.toString()
                                            )
                                            putString(
                                                IntentConstants.IK_POSTER,
                                                saveAsTempFile?.path.toString()
                                            )
                                            putString(
                                                IntentConstants.IK_UPDATE_TYPE,
                                                IntentConstants.UpdateType.UPDATE_PROMO_POST.name
                                            )
                                        })
                            )

                        }
                    }
                }
            }
        }
    }

    private fun tagChanges() {
        promoUpdateCatsList.forEach { it.isSelected = false }
        tagArray.clear()
        tagListAdapter.notifyDataSetChanged()
        binding.tagWrapper.apply {
            if (postType == 1) {
                visible()
            } else {
                gone()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.filter_menu_past_updates, menu)
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
                    item.icon = ContextCompat.getDrawable(baseActivity, R.drawable.ic_filter_hollow_past)
                } else {
                    binding.rvFilterCategory.visible()
                    item.icon = ContextCompat.getDrawable(baseActivity, R.drawable.ic_filter_funnel_white)
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
                binding.btnPostNewUpdate.gone()
            } else {
                binding.btnPostNewUpdate.visible()
                binding.rvPostListing.visible()
                binding.shimmerLayoutPast.parentShimmerLayout.gone()
                binding.shimmerLayoutPast.parentShimmerLayout.stopShimmer()
            }
        }
    }

}