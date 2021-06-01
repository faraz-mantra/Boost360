package dev.patrickgold.florisboard.customization

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.utils.NetworkUtils
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
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

    private  val serviceSet = mutableSetOf<Product>()
    private val photosSet = mutableSetOf<Photo>()
    private  val detailsSet = mutableSetOf<CustomerDetails>()
    private  val updatesSet = mutableSetOf<Float?>()
    private var targetMap: Target? = null
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
        clearSets()
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
                                SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(
                                    2
                                )?.text = "UPDATES (${updatesSet.size})"
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
                                SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(
                                    1
                                )?.text = "SERVICES (${serviceSet.size})"

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
                            SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(4)?.text =
                                "DETAILS (${detailsSet.size})"

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
                                SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(
                                    3
                                )?.text = "PHOTOS (${photosSet.size})"

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

    private fun clearSets() {
        serviceSet.clear()
        photosSet.clear()
        updatesSet.clear()
        detailsSet.clear()
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
              shareUpdates(item)
            }
            BusinessFeatureEnum.INVENTORY -> {
                onClickedShareInventory(item)
            }
            BusinessFeatureEnum.PHOTOS -> {
                onPhotoSelected(item)
            }
            BusinessFeatureEnum.BUSINESS_CARD -> {
                Timber.i("pos - $pos item = $item")
            }
        }
    }

    private fun shareUpdates(item: BaseRecyclerItem) {
        val float = item as? Float
share(float)
    }

    private fun share(float: Float?) {
        if (NetworkUtils.isNetworkConnected()) {
            val shareText =
                String.format("*%s*",
                    float?.message)
            val target: Target = object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                    targetMap = null
                    try {
                        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                        val view = View(mContext)
                        view.draw(Canvas(mutableBitmap))
                        val path = MediaStore.Images.Media.insertImage(
                            mContext.contentResolver,
                            mutableBitmap,
                            "boost_360",
                            ""
                        )
                        val uri = Uri.parse(path)
                        shareTextService(uri, shareText)
                    } catch (e: OutOfMemoryError) {
                    } catch (e: Exception) {
                    }
                }

                override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {
                    targetMap = null
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                }
            }
            if (float?.imageUri.isNullOrEmpty().not()) {
                targetMap = target
                Picasso.get().load(float?.imageUri ?: "").into(target)
            } else {
                shareTextService(null, shareText)
            }
        }

    }

    private fun onClickedShareInventory(item: BaseRecyclerItem) {
        val product = item as? Product
        share(product)
    }

    fun share(product: Product?) {
        if (NetworkUtils.isNetworkConnected()) {
            val shareText =
                String.format("*%s* %s\n*%s* %s\n\n-------------\n%s\n",
                    product?.name?.trim { it <= ' ' },
                    product?.description,
                    "${product?.getCurrencySymbol()} ${product?.discountAmount}",
                    "${product?.getCurrencySymbol()} ${product?.price}",
                    product?.description?.trim { it <= ' ' })
            val target: Target = object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                    targetMap = null
                    try {
                        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                        val view = View(mContext)
                        view.draw(Canvas(mutableBitmap))
                        val path = MediaStore.Images.Media.insertImage(
                            mContext.contentResolver,
                            mutableBitmap,
                            "boost_360",
                            ""
                        )
                        val uri = Uri.parse(path)
                        shareTextService(uri, shareText)
                    } catch (e: OutOfMemoryError) {
                    } catch (e: Exception) {
                    }
                }

                override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {
                    targetMap = null
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                }
            }
            if (product?.imageUri.isNullOrEmpty().not()) {
                targetMap = target
                Picasso.get().load(product?.imageUri ?: "").into(target)
            } else {
                shareTextService(null, shareText)
            }
        }
    }

    private fun shareTextService(uri: Uri?, shareText: String) {
        val share = Intent(Intent.ACTION_SEND)
        share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        share.putExtra(Intent.EXTRA_TEXT, shareText)
        uri?.let { share.putExtra(Intent.EXTRA_STREAM, uri) }
        share.type = if (uri != null) "image/*" else "text/plain"
        if (share.resolveActivity(mContext.packageManager) != null) {
//            share.setPackage(mContext.getString(R.string.whats_app_package))
            mContext.startActivity(Intent.createChooser(share, "share product"))
        }
    }


    private fun onPhotoSelected(item: BaseRecyclerItem) {
        photosSet.forEach {
            if (item == it) {
                it.selected = (item as? Photo)?.selected == true
            }
        }
        adapter.clearList()
        adapter.submitList(photosSet.toList())
        adapter.notifyDataSetChanged()
     updateLayout()
    }

    private fun updateLayout() {
        binding.btnImageShare.setOnClickListener { shareImages() }
        binding.btnCancel.setOnClickListener { removeSelected() }
        if (photosSet.any { it.selected }) {
            binding.tvPhotos.gone()
            binding.btnLink.gone()
            binding.btnShare.gone()
            binding.containerShareImage.visible()
        }else{
            binding.tvPhotos.visible()
            binding.btnLink.visible()
            binding.btnShare.visible()
            binding.containerShareImage.gone()
        }
    }

    private fun removeSelected() {
        photosSet.forEach { it.selected=false }
        adapter.clearList()
        adapter.submitList(photosSet.toList())
        adapter.notifyDataSetChanged()
        updateLayout()
    }

    private fun shareImages() {
        val imageUriArray = ArrayList<Uri>()
        val target: Target = object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                targetMap = null
                try {
                    val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                    val view = View(mContext)
                    view.draw(Canvas(mutableBitmap))
                    val path = MediaStore.Images.Media.insertImage(
                        mContext.contentResolver,
                        mutableBitmap,
                        "boost_360",
                        ""
                    )
                    val uri = Uri.parse(path)
                   imageUriArray.add(uri)
                } catch (e: OutOfMemoryError) {
                } catch (e: Exception) {
                }
            }

            override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {
                targetMap = null
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            }
        }
       photosSet.filter { it.selected }.forEach {
           if (it.imageUri.isNullOrEmpty().not()) {
               targetMap = target
               Picasso.get().load(it.imageUri ?: "").into(target)
           }
       }
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT, "Text caption message!!")
        intent.type = "text/plain"
        intent.type = "image/jpeg"
        intent.setPackage(mContext.getString(R.string.whats_app_package))
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUriArray)
       mContext. startActivity(intent)
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