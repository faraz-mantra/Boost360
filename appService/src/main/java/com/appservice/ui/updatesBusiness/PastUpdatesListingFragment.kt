package com.appservice.ui.updatesBusiness

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appservice.R
import com.appservice.base.AppBaseFragment
import com.appservice.constant.FragmentType
import com.appservice.constant.RecyclerViewActionType
import com.appservice.databinding.FragmentPastUpdatesListingBinding
import com.appservice.model.updateBusiness.pastupdates.*
import com.appservice.recyclerView.AppBaseRecyclerViewAdapter
import com.appservice.recyclerView.BaseRecyclerViewItem
import com.appservice.recyclerView.PaginationScrollListener
import com.appservice.recyclerView.RecyclerItemClickListener
import com.appservice.utils.WebEngageController
import com.appservice.viewmodel.PastUpdatesViewModel
import com.festive.poster.ui.promoUpdates.PromoUpdatesActivity
import com.framework.constants.IntentConstants
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.pref.clientId
import com.framework.utils.ContentSharing
import com.framework.utils.saveAsTempFile
import com.framework.webengageconstant.POSTED_UPDATE_LIST_PAGE_LOAD
import com.framework.webengageconstant.Promotional_Update_Posted_Updates_Click
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PastUpdatesListingFragment : AppBaseFragment<FragmentPastUpdatesListingBinding, PastUpdatesViewModel>(), RecyclerItemClickListener {

  private var pastPostListing = ArrayList<PastPostItem>()
  private lateinit var categoryDataList: ArrayList<PastCategoriesModel>
  private lateinit var postCategoryAdapter: AppBaseRecyclerViewAdapter<PastCategoriesModel>
  private lateinit var pastPostListingAdapter: AppBaseRecyclerViewAdapter<PastPostItem>
  private lateinit var tagListAdapter: AppBaseRecyclerViewAdapter<PastPromotionalCategoryModel>
  var postType: Int = 0
  var isFilterVisible = false
  var tagArray: MutableList<String> = mutableListOf()
  var promoUpdateCatsList: ArrayList<PastPromotionalCategoryModel> = arrayListOf()
  lateinit var linearLayoutManager: LinearLayoutManager

  /* Paging */
  private var isLoadingD = false
  private var TOTAL_ELEMENTS = 0

  //  private var skip: Int = PaginationScrollListener.PAGE_START
  private var isLastPageD = false

  companion object {
    @JvmStatic
    fun newInstance(bundle: Bundle? = null): PastUpdatesListingFragment {
      val fragment = PastUpdatesListingFragment()
      fragment.arguments = bundle
      return fragment
    }
  }

  override fun getLayout(): Int {
    return R.layout.fragment_past_updates_listing
  }

  override fun getViewModelClass(): Class<PastUpdatesViewModel> {
    return PastUpdatesViewModel::class.java
  }

  override fun onCreateView() {
    super.onCreateView()
    WebEngageController.trackEvent(POSTED_UPDATE_LIST_PAGE_LOAD,"","")
    initUI()
    setOnClickListener(binding.btnPostNewUpdate)

  }

  private fun initUI() {
    linearLayoutManager = LinearLayoutManager(requireActivity())
    baseActivity.window.statusBarColor = getColor(R.color.color_4a4a4a_jio_ec008c)
    showSimmer(true)
    getTemplateViewConfig()

    //apiCallPastUpdates()
  }

  private fun handlePagination() {

/*        binding.rvPostListing.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) { //check for scroll down
                   val  visibleItemCount = linearLayoutManager.getChildCount()
                   val  totalItemCount = linearLayoutManager.getItemCount()
                   val  pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition()
                    if (isLoadingD) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            isLoadingD = false
                            Log.v("...", "Last Item Wow !")
                            // Do pagination.. i.e. fetch new data
                            apiCallPastUpdates(false,pastPostListing.size)

                        }
                    }
                }
            }
        })*/
    binding?.rvPostListing.addOnScrollListener(object : PaginationScrollListener(linearLayoutManager) {
      override fun loadMoreItems() {
        if (!isLastPageD) {
          isLoadingD = true
          pastPostListingAdapter.addLoadingFooter(PastPostItem().getLoaderItem())
          apiCallPastUpdates(false, pastPostListing.size)
        }
      }

      override val totalPageCount: Int
        get() = TOTAL_ELEMENTS
      override val isLastPage: Boolean
        get() = isLastPageD
      override val isLoading: Boolean
        get() = isLoadingD
    })
  }

  override fun onClick(v: View) {
    super.onClick(v)
    when (v) {
      binding.btnPostNewUpdate -> {
       navigateToUpdateStudio()
      }
    }
  }

  private fun getPostCategories() {
    categoryDataList = PastCategoriesModel().getData(baseActivity)
    postCategoryAdapter = AppBaseRecyclerViewAdapter(baseActivity, categoryDataList, this)
    binding.rvFilterCategory.adapter = postCategoryAdapter
    apiCallPastUpdates(true, 0)
  }

  private fun getTemplateViewConfig() {
    viewModel?.promoUpdatesCats?.observeOnce(this) {
      promoUpdateCatsList.clear()
      promoUpdateCatsList.addAll(it)
      tagListAdapter = AppBaseRecyclerViewAdapter(baseActivity, promoUpdateCatsList, this)
      binding.rvFilterSubCategory.adapter = tagListAdapter
      getPostCategories()
    }
  }


  private fun apiCallPastUpdates(isFirst: Boolean, skipBy: Int) {

    viewModel?.getPastUpdatesListV6(
      clientId = clientId, fpId = sessionLocal.fPID, postType = postType, tagListRequest = TagListRequest(tagArray), skipBy = skipBy
    )?.observeOnce(viewLifecycleOwner) { it ->
      hideProgress()
      if (it.isSuccess()) {
        it as PastUpdatesNewListingResponse

        handleListResponse(it, isFirst)

      }
      showSimmer(false)
      Log.i("pastUpdates", "PastUpdates: $it")
    }
  }

  private fun handleListResponse(it: PastUpdatesNewListingResponse, isFirst: Boolean) {
    if (isFirst) {
      pastPostListing.clear()
    }

    //Setting Category Counts
    if (categoryDataList.isNotEmpty()) {
      categoryDataList[0].categoryCount = it.totalCount ?: 0
      categoryDataList[1].categoryCount = it.postCount ?: 0
      categoryDataList[2].categoryCount = it.imageCount ?: 0
      categoryDataList[3].categoryCount = it.textCount ?: 0
      postCategoryAdapter.notifyItemRangeChanged(0, 4)
    }
    TOTAL_ELEMENTS = categoryDataList[postType].categoryCount
    isLastPageD = pastPostListing.size == TOTAL_ELEMENTS


    it.floats?.let { list ->
      if (isFirst.not()) {
        removeLoader()
      }
      if (list.isNullOrEmpty() && isFirst) {
        binding.tvNoPost.visible()
        pastPostListingAdapter.notifyDataSetChanged()
      } else {
        binding.tvNoPost.gone()
        binding.rvPostListing.visible()
        list.forEach { item ->
          item.category = promoUpdateCatsList.find { category ->
            item.tags?.firstOrNull() == category.id
          }
        }
        pastPostListing.addAll(list)
        notifyList()
      }


    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.PAST_CATEGORY_CLICKED.ordinal -> {
        showProgress()
        val pastCategoriesModel = item as PastCategoriesModel

        categoryDataList.forEach { it.isSelected = false }
        categoryDataList[position].isSelected = true

        postType = pastCategoriesModel.postType
        tagChanges()
        postCategoryAdapter.notifyDataSetChanged()
        apiCallPastUpdates(true, 0)
      }
      RecyclerViewActionType.PAST_TAG_CLICKED.ordinal -> {
        showProgress()
        item as PastPromotionalCategoryModel
        if (tagArray.contains(item.id)) tagArray.remove(item.id)
        else tagArray.add(item.id)

        binding.rvFilterSubCategory.postDelayed(
          Runnable {
            tagListAdapter.notifyDataSetChanged()
          }, 100
        )
        apiCallPastUpdates(true, 0)
      }
      RecyclerViewActionType.PAST_SHARE_BUTTON_CLICKED.ordinal -> {
        showProgress()
        val pastPostItem = item as PastPostItem
        ContentSharing.share(
          activity = baseActivity, shareText = pastPostItem.message ?: "", imageUri = pastPostItem.imageUri ?: ""
        )
        hideProgress()
      }
      RecyclerViewActionType.PAST_REUSE_BUTTON_CLICKED.ordinal -> {
        item as PastPostItem
        lifecycleScope.launch {
          withContext(Dispatchers.IO) {
            if (item.imageUri != null && item.imageUri.toString() != "") {
              val bitmapPastUpdateReuse = Picasso.get().load(item.imageUri.toString()).get()
              val saveAsTempFile = bitmapPastUpdateReuse.saveAsTempFile()
              startActivity(
                Intent(
                  requireActivity(), Class.forName(
                    "com.festive.poster.ui.promoUpdates.PostPreviewSocialActivity"
                  )
                ).putExtra(IntentConstants.MARKET_PLACE_ORIGIN_NAV_DATA, Bundle().apply {
                  putString(
                    IntentConstants.IK_CAPTION_KEY, item.message.toString()
                  )
                  putString(
                    IntentConstants.IK_POSTER, saveAsTempFile?.path.toString()
                  )
                  putString(
                    IntentConstants.IK_UPDATE_TYPE, IntentConstants.UpdateType.UPDATE_PROMO_POST.name
                  )
                })
              )
            } else {
              lifecycleScope.launch(Dispatchers.Main) {
                Toast.makeText(activity, "Something went wrong, Try again later", Toast.LENGTH_SHORT).show()
              }
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

  private fun removeLoader() {
    if (isLoadingD) {
      isLoadingD = false
      pastPostListingAdapter.removeLoadingFooter()
    }
  }

  fun notifyList() {
    if (::pastPostListingAdapter.isInitialized.not()) {
      pastPostListingAdapter = AppBaseRecyclerViewAdapter(baseActivity, pastPostListing, this)
      binding.rvPostListing.adapter = pastPostListingAdapter
      binding.rvPostListing.layoutManager = linearLayoutManager
      pastPostListingAdapter.runLayoutAnimation(binding.rvPostListing)
      handlePagination()
    } else {
      pastPostListingAdapter.notifyDataSetChanged()
    }

  }

}