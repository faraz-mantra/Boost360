package dev.patrickgold.florisboard.customization

import android.content.Context
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.framework.extensions.gone
import com.framework.extensions.visible
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerItem
import dev.patrickgold.florisboard.customization.adapter.OnItemClickListener
import dev.patrickgold.florisboard.customization.adapter.SharedAdapter
import dev.patrickgold.florisboard.customization.model.response.CustomerDetails
import dev.patrickgold.florisboard.customization.model.response.Float
import dev.patrickgold.florisboard.customization.model.response.Photo
import dev.patrickgold.florisboard.customization.model.response.Product
import dev.patrickgold.florisboard.customization.util.MethodUtils
import dev.patrickgold.florisboard.customization.util.PaginationScrollListener.Companion.PAGE_SIZE
import dev.patrickgold.florisboard.customization.util.PaginationScrollListener.Companion.PAGE_START
import dev.patrickgold.florisboard.customization.util.SharedPrefUtil
import dev.patrickgold.florisboard.databinding.BusinessFeaturesLayoutBinding
import dev.patrickgold.florisboard.ime.core.InputView
import dev.patrickgold.florisboard.ime.text.smartbar.SmartbarView
import timber.log.Timber

class BusinessFeaturesManager(inputView: InputView) : OnItemClickListener {

    init {
        onRegisterInputView(inputView)
    }

    private lateinit var mContext: Context
    private lateinit var currentSelectedFeature: BusinessFeatureEnum
    private lateinit var binding: BusinessFeaturesLayoutBinding
    private lateinit var recyclerViewPost: RecyclerView
    private lateinit var recyclerViewPhotos: RecyclerView
    private lateinit var businessFeatureProgressBar: ProgressBar
    private lateinit var viewModel: BusinessFeaturesViewModel
    private lateinit var adapter: SharedAdapter<BaseRecyclerItem?>
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var pagerSnapHelper: PagerSnapHelper

    var isLoading = false

    /* Paging */
    private var isLoadingD = false
    private var TOTAL_ELEMENTS = 0
    private var offSet: Int = PAGE_START
    private var limit: Int = PAGE_SIZE
    private var isLastPageD = false

    private fun onRegisterInputView(inputView: InputView) {
        mContext = inputView.context

        viewModel = BusinessFeaturesViewModel()
        adapter = SharedAdapter(arrayListOf(), this)
        pagerSnapHelper = PagerSnapHelper()

        // initialize business features views
        binding = BusinessFeaturesLayoutBinding.bind(inputView.findViewById(R.id.business_features))

        businessFeatureProgressBar = binding.businessFeatureProgress

        linearLayoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)

        recyclerViewPost = binding.productShareRvList.also {
            it.layoutManager = linearLayoutManager
            it.adapter = adapter
        }
        pagerSnapHelper.attachToRecyclerView(recyclerViewPost)
        recyclerViewPhotos = binding.rvListPhotos.also {
            it.layoutManager = GridLayoutManager(mContext, 2, GridLayoutManager.HORIZONTAL, false)
            it.adapter = adapter
        }

        recyclerViewPost.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!isLoading) {
                    if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == adapter.itemCount - 1) {
                        //bottom of list!
                        loadMoreItems(currentSelectedFeature)
                        isLoading = true
                    }
                }
            }
        })
    }

    private fun scrollPagingListener(layoutManager: LinearLayoutManager) {
//    recyclerViewPost.addOnScrollListener(object : PaginationScrollListener(layoutManager) {
//      override fun loadMoreItems() {
//        if (!isLastPageD) {
//          isLoadingD = true
////          adapterService?.addLoadingFooter(ItemsItem().getLoaderItem())
//          offSet += limit
////          getListServiceFilterApi(offSet = offSet, limit = limit)
//          loadMoreItems(currentSelectedFeature)
//        }
//      }
//
//      override val totalPageCount: Int
//        get() = TOTAL_ELEMENTS
//      override val isLastPage: Boolean
//        get() = isLastPageD
//      override val isLoading: Boolean
//        get() = isLoadingD
//    })
    }


    fun showSelectedBusinessFeature(businessFeatureEnum: BusinessFeatureEnum) {
        currentSelectedFeature = businessFeatureEnum
        when (businessFeatureEnum) {
            BusinessFeatureEnum.UPDATES, BusinessFeatureEnum.INVENTORY, BusinessFeatureEnum.BUSINESS_CARD -> {
                binding.clSelectionLayout.gone()
                binding.rvListPhotos.gone()
                binding.productShareRvList.visible()
            }

            BusinessFeatureEnum.PHOTOS -> {
                binding.productShareRvList.gone()
                binding.clSelectionLayout.visible()
                binding.rvListPhotos.visible()
            }
        }

        initializeAdapters(businessFeatureEnum)
    }

    private fun initializeAdapters(businessFeatureEnum: BusinessFeatureEnum) {
        //clear adapter dataset
        adapter.clearList()
        if (!SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).isLoggedIn) {
            Timber.i("Please do login")
            // show login UI
            binding.pleaseLoginCard.visible()
            binding.pleaseLoginCard.setOnClickListener {
                MethodUtils.startBoostActivity(mContext)
            }
        } else {
            binding.pleaseLoginCard.gone()
            if (MethodUtils.isOnline(mContext)) {
                businessFeatureProgressBar.visible()
                viewModel.error.observeForever {
                    businessFeatureProgressBar.gone()
                    Toast.makeText(mContext, it, Toast.LENGTH_SHORT).show()
                }
                when (businessFeatureEnum) {
                    BusinessFeatureEnum.UPDATES -> {
                        viewModel.getUpdates(
                            SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).fpId,
                            mContext.getString(R.string.client_id),
                            0, 10
                        )
                        viewModel.updates.observeForever {
                            Timber.i("updates - $it.")
                            businessFeatureProgressBar.gone()

                            adapter.removeLoader()

                            if (it.floats?.isNotEmpty() == true) {
                                it.floats?.let { list ->
                                    adapter.submitList(
                                        list,
                                        hasMoreItems = true
                                    )
                                }
                                updatesSet.addAll(it.floats!!)
                                SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(2)?.text ="UPDATES (${updatesSet.size})"
                            } else {
                                adapter.removeLoader()
                                Timber.i("List from api came empty")
                            }
                            isLoading = false
                        }
                    }
                    BusinessFeatureEnum.INVENTORY -> {
                        viewModel.getProducts(
                            SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).fpTag,
                            mContext.getString(R.string.client_id),
                            0, "SINGLE"
                        )
                        viewModel.products.observeForever {
                            Timber.i("products - $it.")
                            businessFeatureProgressBar.gone()

                            adapter.removeLoader()

                            if (it.isNotEmpty()) {
                                it.let { list -> adapter.submitList(list, hasMoreItems = true) }
                                serviceSet.addAll(it)
                                SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(1)?.text ="SERVICES (${serviceSet.size})"

                            } else {
                                adapter.removeLoader()
                                Timber.i("List from api came empty")
                            }
                            isLoading = false
                        }
                    }
                    BusinessFeatureEnum.BUSINESS_CARD -> {
                        viewModel.getDetails(
                            SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).fpTag,
                            mContext.getString(R.string.client_id)
                        )
                        viewModel.details.observeForever {
                            Timber.e("details - $it.")
                            businessFeatureProgressBar.gone()
                            adapter.submitList(listOf(it))
                            detailsSet.add(it)
                            SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(4)?.text ="DETAILS (${detailsSet.size})"

                        }
                    }
                    BusinessFeatureEnum.PHOTOS -> {
                        viewModel.getPhotos(
                            SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).fpId
                        )
                        viewModel.photos.observeForever {
                            Timber.e("photos - $it.")
                            businessFeatureProgressBar.gone()
                            adapter.removeLoader()

                            if (it.isNotEmpty()) {
                                it.let { list -> adapter.submitList(list, hasMoreItems = true) }
                                photosSet.addAll(it)
                                SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(3)?.text ="PHOTOS (${photosSet.size})"

                            } else {
                                adapter.removeLoader()
                                Timber.i("List from api came empty")
                            }
                            isLoading = false
                        }
                    }
                }
            } else {
                Toast.makeText(mContext, "Check your Network Connection", Toast.LENGTH_SHORT).show()
                businessFeatureProgressBar.gone()
            }
        }
    }

    override fun onItemClick(pos: Int, item: BaseRecyclerItem) {
        handleListItemClick(currentSelectedFeature, pos, item)
    }

    private fun handleListItemClick(
        businessFeatureEnum: BusinessFeatureEnum,
        pos: Int,
        item: BaseRecyclerItem
    ) {
        when (businessFeatureEnum) {
            BusinessFeatureEnum.UPDATES -> {
                Timber.i("pos - $pos item = $item")
            }
            BusinessFeatureEnum.INVENTORY -> {
                Timber.i("pos - $pos item = $item")
            }
            BusinessFeatureEnum.PHOTOS -> {
                photosSet.forEach {
                if (item== it){
                    it.selected= (item as? Photo)?.selected == true
                }else{
                    it.selected=false
                }
                }
                adapter.clearList()
                adapter.submitList(photosSet.toList())
                adapter.notifyDataSetChanged()
            }
            BusinessFeatureEnum.BUSINESS_CARD -> {
                Timber.i("pos - $pos item = $item")
            }
        }
    }
    companion object{
        val serviceSet = mutableSetOf<Product>()
        val photosSet = mutableSetOf<Photo>()
        val detailsSet = mutableSetOf<CustomerDetails>()
        val updatesSet = mutableSetOf<Float?>()

    }

    private fun loadMoreItems(businessFeatureEnum: BusinessFeatureEnum) {
        when (businessFeatureEnum) {
            BusinessFeatureEnum.UPDATES -> {
                viewModel.getUpdates(
                    SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).fpId,
                    mContext.getString(R.string.client_id), adapter.list.size, 10
                )
            }
            BusinessFeatureEnum.INVENTORY -> {
                viewModel.getProducts(
                    SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).fpTag,
                    mContext.getString(R.string.client_id), adapter.list.size, "SINGLE"
                )
            }
        }
    }

    fun clearObservers() {
        viewModel.updates.removeObserver {}
        viewModel.details.removeObserver {}
        viewModel.photos.removeObserver {}
        viewModel.products.removeObserver {}
    }

    fun getBindingRoot(): View {
        return binding.root
    }
}