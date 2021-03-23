package dev.patrickgold.florisboard.customization

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.customization.adapter.DetailsAdapter
import dev.patrickgold.florisboard.customization.adapter.PhotosAdapter
import dev.patrickgold.florisboard.customization.adapter.ProductsAdapter
import dev.patrickgold.florisboard.customization.adapter.UpdatesAdapter
import dev.patrickgold.florisboard.customization.util.MethodUtils
import dev.patrickgold.florisboard.customization.util.SharedPrefUtil
import dev.patrickgold.florisboard.databinding.BusinessFeaturesLayoutBinding
import dev.patrickgold.florisboard.ime.core.FlorisBoard
import timber.log.Timber

class BusinessFeaturesViewPager : ViewPager, FlorisBoard.EventListener {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private lateinit var mContext: Context
    private lateinit var binding: BusinessFeaturesLayoutBinding
    private lateinit var recyclerViewPost: RecyclerView
    private lateinit var recyclerViewPhotos: RecyclerView
    private lateinit var viewModel: BusinessFeaturesViewModel
    private lateinit var updatesAdapter: UpdatesAdapter
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var detailsAdapter: DetailsAdapter
    private lateinit var photosAdapter: PhotosAdapter
    private val pagerSnapHelper = PagerSnapHelper()

    override fun onWindowShown() {
        Timber.i("onWindowShown")
        adapter = BusinessFeaturePagerAdapter()
    }

    inner class BusinessFeaturePagerAdapter : PagerAdapter() {

        val tabs: List<String> = BusinessFeatureEnum.values().map { it.name }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            mContext = container.context
            val view = LayoutInflater.from(mContext)
                    .inflate(R.layout.business_features_layout, container, false)
            binding = BusinessFeaturesLayoutBinding.bind(view)

            init(BusinessFeatureEnum.values()[position])

            container.addView(view)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun getCount(): Int = tabs.size

        override fun isViewFromObject(view: View, `object`: Any): Boolean = view === `object`
    }

    private fun init(businessFeatureEnum: BusinessFeatureEnum) {

        recyclerViewPost = binding.productShareRvList
        recyclerViewPost.layoutManager = LinearLayoutManager(mContext).apply {
            orientation = LinearLayoutManager.HORIZONTAL
        }
        pagerSnapHelper.attachToRecyclerView(recyclerViewPost)

        recyclerViewPhotos = binding.rvListPhotos
        recyclerViewPhotos.layoutManager = GridLayoutManager(mContext, 2,
                GridLayoutManager.HORIZONTAL, false)

        when (businessFeatureEnum) {
            BusinessFeatureEnum.UPDATES -> {
                binding.clSelectionLayout.visibility = View.GONE
                binding.productShareRvList.visibility = View.VISIBLE
                binding.rvListPhotos.visibility = View.GONE

                recyclerViewPost.adapter = UpdatesAdapter().also {
                    updatesAdapter = it
                }
            }
            BusinessFeatureEnum.INVENTORY -> {
                binding.clSelectionLayout.visibility = View.GONE
                binding.productShareRvList.visibility = View.VISIBLE
                binding.rvListPhotos.visibility = View.GONE

                recyclerViewPost.adapter = ProductsAdapter().also {
                    productsAdapter = it
                }
            }
            BusinessFeatureEnum.PHOTOS -> {
                binding.clSelectionLayout.visibility = View.VISIBLE
                binding.productShareRvList.visibility = View.GONE
                binding.rvListPhotos.visibility = View.VISIBLE
                recyclerViewPhotos.adapter = PhotosAdapter().also {
                    photosAdapter = it
                }
            }
            BusinessFeatureEnum.DETAILS -> {
                binding.clSelectionLayout.visibility = View.GONE
                binding.productShareRvList.visibility = View.VISIBLE
                binding.rvListPhotos.visibility = View.GONE
                recyclerViewPost.adapter = DetailsAdapter().also {
                    detailsAdapter = it
                }
            }
        }

        initializeAdapters(businessFeatureEnum)
    }

    private fun initializeAdapters(businessFeatureEnum: BusinessFeatureEnum) {
        viewModel = BusinessFeaturesViewModel()
        if (!SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).isLoggedIn) {
            Timber.i("Please do login")
            // set login adapter
            //recyclerViewPost.adapter =
        } else {
            if (MethodUtils.isOnline(mContext)) {
                //refreshRecyclerView()
                when (businessFeatureEnum) {
                    BusinessFeatureEnum.UPDATES -> {
                        viewModel.getUpdates(
                                SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).fpId,
                                mContext.getString(R.string.client_id),
                                0, 10
                        )
                        viewModel.updates.observeForever {
                            Timber.e("updates - $it.")
                            updatesAdapter.submitList(it.floats)
                        }
                    }
                    BusinessFeatureEnum.INVENTORY -> {
                        viewModel.getProducts(
                                SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).fpTag,
                                mContext.getString(R.string.client_id),
                                0, "SINGLE"
                        )
                        viewModel.products.observeForever {
                            Timber.e("products - $it.")
                            productsAdapter.submitList(it)
                        }
                    }
                    BusinessFeatureEnum.DETAILS -> {
                        viewModel.getDetails(
                                SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).fpTag,
                                mContext.getString(R.string.client_id)
                        )
                        viewModel.details.observeForever {
                            Timber.e("details - $it.")
                            detailsAdapter.submitList(listOf(it))
                        }
                    }
                    BusinessFeatureEnum.PHOTOS -> {
                        viewModel.getPhotos(
                                SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).fpId
                        )
                        viewModel.photos.observeForever {
                            Timber.e("photos - $it.")
                            photosAdapter.submitList(it)
                        }
                    }
                }
            } else {
                Toast.makeText(mContext, "Check your Network Connection", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        Timber.i("onDestroy")
        viewModel.updates.removeObserver {}
        viewModel.details.removeObserver {}
        viewModel.photos.removeObserver {}
        viewModel.products.removeObserver {}
    }
}