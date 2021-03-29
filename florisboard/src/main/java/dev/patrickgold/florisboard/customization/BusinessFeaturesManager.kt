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
import dev.patrickgold.florisboard.customization.util.MethodUtils
import dev.patrickgold.florisboard.customization.util.SharedPrefUtil
import dev.patrickgold.florisboard.databinding.BusinessFeaturesLayoutBinding
import dev.patrickgold.florisboard.ime.core.InputView
import timber.log.Timber
import java.util.*

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

    private fun onRegisterInputView(inputView: InputView) {
        mContext = inputView.context

        viewModel = BusinessFeaturesViewModel()
        adapter = SharedAdapter(arrayListOf(), this)
        pagerSnapHelper = PagerSnapHelper()

        // initialize business features views
        binding = BusinessFeaturesLayoutBinding.bind(inputView.findViewById(R.id.business_features))

        businessFeatureProgressBar = binding.businessFeatureProgress

        linearLayoutManager = LinearLayoutManager(mContext).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }

        recyclerViewPost = binding.productShareRvList.also {
            it.layoutManager = linearLayoutManager
            it.adapter = adapter
        }
        pagerSnapHelper.attachToRecyclerView(recyclerViewPost)

        recyclerViewPhotos = binding.rvListPhotos.also {
            it.layoutManager = GridLayoutManager(mContext, 2,
                    GridLayoutManager.HORIZONTAL, false)
            it.adapter = adapter
        }

        recyclerViewPost.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!isLoading) {
                    if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == adapter.itemCount - 1) {
                        //bottom of list!
                        loadMoreItems(currentSelectedFeature)
                        isLoading = true;
                    }
                }
            }
        })
    }


    fun showSelectedBusinessFeature(businessFeatureEnum: BusinessFeatureEnum) {
        currentSelectedFeature = businessFeatureEnum
        when (businessFeatureEnum) {
            BusinessFeatureEnum.UPDATES, BusinessFeatureEnum.INVENTORY, BusinessFeatureEnum.DETAILS -> {
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

                            try {
                                // last item can be null which is added for pagination
                                adapter.list.removeLast()
                            } catch (e: NoSuchElementException) {
                                Timber.i("List is empty")
                            }

                            if (it.totalCount!! > adapter.list.size) {
                                it.floats?.let { it1 -> adapter.submitList(it1, true) }
                            } else {
                                it.floats?.let { it1 -> adapter.submitList(it1) }
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
                            // need to now how to load more products as there's no flag in response which indicates to load more data
                            adapter.submitList(it)
                        }
                    }
                    BusinessFeatureEnum.DETAILS -> {
                        viewModel.getDetails(
                                SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).fpTag,
                                mContext.getString(R.string.client_id)
                        )
                        viewModel.details.observeForever {
                            Timber.e("details - $it.")
                            businessFeatureProgressBar.gone()
                            adapter.submitList(listOf(it))
                        }
                    }
                    BusinessFeatureEnum.PHOTOS -> {
                        viewModel.getPhotos(
                                SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).fpId
                        )
                        viewModel.photos.observeForever {
                            Timber.e("photos - $it.")
                            businessFeatureProgressBar.gone()
                            adapter.submitList(it)
                        }
                    }
                }
            } else {
                Toast.makeText(mContext, "Check your Network Connection", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onItemClick(pos: Int, item: BaseRecyclerItem) {
        handleListItemClick(currentSelectedFeature, pos, item)
    }

    private fun handleListItemClick(businessFeatureEnum: BusinessFeatureEnum, pos: Int, item: BaseRecyclerItem) {
        when (businessFeatureEnum) {
            BusinessFeatureEnum.UPDATES -> {
                Timber.i("pos - $pos item = $item")
            }
            BusinessFeatureEnum.INVENTORY -> {
                Timber.i("pos - $pos item = $item")
            }
            BusinessFeatureEnum.PHOTOS -> {
                Timber.i("pos - $pos item = $item")
            }
            BusinessFeatureEnum.DETAILS -> {
                Timber.i("pos - $pos item = $item")
            }
        }
    }

    private fun loadMoreItems(businessFeatureEnum: BusinessFeatureEnum) {
        when (businessFeatureEnum) {
            BusinessFeatureEnum.UPDATES -> {
                viewModel.getUpdates(SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).fpId,
                        mContext.getString(R.string.client_id),
                        adapter.list.size, 10
                )
            }
            BusinessFeatureEnum.INVENTORY -> {
                viewModel.getProducts(
                        SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).fpTag,
                        mContext.getString(R.string.client_id),
                        adapter.list.size, "SINGLE"
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