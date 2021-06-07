package dev.patrickgold.florisboard.customization

import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.inputmethod.EditorInfo
import com.bumptech.glide.request.target.Target
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.inputmethod.EditorInfoCompat
import androidx.core.view.inputmethod.InputConnectionCompat
import androidx.core.view.inputmethod.InputContentInfoCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.utils.DateUtils
import com.framework.utils.NetworkUtils
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerItem
import dev.patrickgold.florisboard.customization.adapter.OnItemClickListener
import dev.patrickgold.florisboard.customization.adapter.SharedAdapter
import dev.patrickgold.florisboard.customization.model.response.CustomerDetails
import dev.patrickgold.florisboard.customization.model.response.FloatUpdate
import dev.patrickgold.florisboard.customization.model.response.Photo
import dev.patrickgold.florisboard.customization.model.response.Product
import dev.patrickgold.florisboard.customization.util.MethodUtils
import dev.patrickgold.florisboard.customization.util.MethodUtils.getImageUri
import dev.patrickgold.florisboard.customization.util.PaginationScrollListener.Companion.PAGE_SIZE
import dev.patrickgold.florisboard.customization.util.PaginationScrollListener.Companion.PAGE_START
import dev.patrickgold.florisboard.customization.util.SharedPrefUtil
import dev.patrickgold.florisboard.databinding.BusinessFeaturesLayoutBinding
import dev.patrickgold.florisboard.ime.core.FlorisBoard
import dev.patrickgold.florisboard.ime.core.InputView
import dev.patrickgold.florisboard.ime.text.smartbar.SmartbarView
import timber.log.Timber

// keyborad ImePresenterImpl

class BusinessFeaturesManager(inputView: InputView, florisBoard: FlorisBoard) : OnItemClickListener {

  init {
    onRegisterInputView(inputView, florisBoard)
  }

  private val serviceSet = mutableSetOf<Product>()
  private val photosSet = mutableSetOf<Photo>()
  private val detailsSet = mutableSetOf<CustomerDetails>()
  private val updatesSet = mutableSetOf<FloatUpdate?>()
  private lateinit var mContext: Context
  private var florisBoard: FlorisBoard? = null
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

  private fun onRegisterInputView(inputView: InputView, florisBoard: FlorisBoard) {
    mContext = inputView.context
    this.florisBoard = florisBoard

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
      else -> {
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
                it.floats?.let { list -> adapter.submitList(list, hasMoreItems = true) }
                updatesSet.addAll(it.floats!!)
                SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(2)?.text = "UPDATES (${it.totalCount})"
              } else {
                adapter.removeLoader()
                Timber.i("List from api came empty")
              }
              isLoading = false
            }
          }
          BusinessFeatureEnum.INVENTORY -> {
            viewModel.getProducts(SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).fpTag, mContext.getString(R.string.client_id), 0, "SINGLE")
            viewModel.products.observeForever {
              Timber.i("products - $it.")
              businessFeatureProgressBar.gone()

              adapter.removeLoader()

              if (it.isNotEmpty()) {
                it.let { list -> adapter.submitList(list, hasMoreItems = true) }
                serviceSet.addAll(it)
                SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(1)?.text = "SERVICES (${serviceSet.size})"
              } else {
                adapter.removeLoader()
                Timber.i("List from api came empty")
              }
              isLoading = false
            }
          }
          BusinessFeatureEnum.BUSINESS_CARD -> {
            viewModel.getDetails(SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).fpTag, mContext.getString(R.string.client_id))
            viewModel.details.observeForever {
              Timber.e("details - $it.")
              businessFeatureProgressBar.gone()
              adapter.submitList(listOf(it))
              detailsSet.add(it)
              SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(4)?.text = "DETAILS (${detailsSet.size})"
            }
          }
          BusinessFeatureEnum.PHOTOS -> {
            viewModel.getPhotos(SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).fpId)
            viewModel.photos.observeForever {
              Timber.e("photos - $it.")
              businessFeatureProgressBar.gone()
              adapter.removeLoader()
              if (it.isNotEmpty()) {
                it.let { list -> adapter.submitList(list, hasMoreItems = true) }
                photosSet.addAll(it)
                SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(3)?.text = "PHOTOS (${photosSet.size})"
              } else {
                adapter.removeLoader()
                Timber.i("List from api came empty")
              }
              isLoading = false
            }
          }
          else -> {
          }
        }
      } else {
        Toast.makeText(mContext, mContext.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
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

  private fun handleListItemClick(businessFeatureEnum: BusinessFeatureEnum, pos: Int, item: BaseRecyclerItem) {
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
      else -> {
      }
    }
  }

  fun EditorInfo.getImageSupport(): Boolean {
    val mimeTypes: Array<String> = EditorInfoCompat.getContentMimeTypes(this)
    return mimeTypes.any { ClipDescription.compareMimeTypes(it, "image/*") }
  }

  private fun shareUpdates(item: BaseRecyclerItem) {
    val float = item as? FloatUpdate
    if (NetworkUtils.isNetworkConnected()) {
      val shareText = String.format("*%s*", float?.message)
      pathToUriGet(float?.imageUri, shareText)
    } else Toast.makeText(mContext, mContext.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
  }

  private fun onClickedShareInventory(item: BaseRecyclerItem) {
    val product = item as? Product
    if (NetworkUtils.isNetworkConnected()) {
      val shareText = String.format("*%s* %s\n*%s* %s\n\n-------------\n%s\n", product?.name?.trim { it <= ' ' }, product?.description,
        "${product?.getCurrencySymbol()} ${product?.discountAmount}", "${product?.getCurrencySymbol()} ${product?.price}", product?.description?.trim { it <= ' ' })
      pathToUriGet(product?.imageUri, shareText)
    } else Toast.makeText(mContext, mContext.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
  }

  private fun pathToUriGet(imageUri: String?, shareText: String) {
    val listenerRequest = object : RequestListener<Bitmap> {
      override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
        shareService(null, shareText)
        return false
      }

      override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
        val uri = getImageUri(mContext, resource, "boost_${DateUtils.getCurrentDate().time}")
        shareService(uri, shareText)
        return false
      }
    }

    if (imageUri.isNullOrEmpty().not()) {
      Glide.with(mContext).asBitmap().load(imageUri ?: "").listener(listenerRequest).submit()
    } else shareService(null, shareText)
  }

  private fun shareService(uri: Uri?, shareText: String) {
    Timber.i("Image passed: $uri \n text: $shareText")
    val packageNames: Array<String>? = mContext.packageManager.getPackagesForUid(florisBoard?.currentInputBinding?.uid ?: 0) as? Array<String>
    if (uri != null) {
      if (packageNames.isNullOrEmpty().not() && florisBoard?.currentInputEditorInfo?.getImageSupport() == true) {
        commitImageWithText(uri, shareText, packageNames!!)
      } else {
        florisBoard?.currentInputConnection?.commitText(shareText, 1)
        Toast.makeText(mContext, mContext.getString(R.string.image_not_suopported), Toast.LENGTH_SHORT).show()
      }
    } else florisBoard?.currentInputConnection?.commitText(shareText, 1)
  }

  fun commitImageWithText(contentUri: Uri, shareText: String, packageNames: Array<String>) {
    florisBoard?.let {
      val inputContentInfo = InputContentInfoCompat(contentUri, ClipDescription(shareText, arrayOf("image/png")), null)
      var flags = 1
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
        flags = flags or InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION
      } else {
        mContext.grantUriPermission(packageNames[0], contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }
      if (it.currentInputConnection != null) it.currentInputConnection.commitText(shareText, 1)
      InputConnectionCompat.commitContent(it.currentInputConnection, it.currentInputEditorInfo, inputContentInfo, flags, null)
    }
  }

  private fun multipleImageToUriListGet() {
    if (NetworkUtils.isNetworkConnected()) {
      var count = 0
      val imageUriArray = ArrayList<Uri>()
      val selectedImage = photosSet.filter { it.selected }
      val listenerRequest = object : RequestListener<Bitmap> {
        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
          count += 1
          if (selectedImage.size == count) doCommitContentMultiple(imageUriArray)
          return false
        }

        override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
          val uri = getImageUri(mContext, resource, "boost_${DateUtils.getCurrentDate().time}")
          uri?.let { imageUriArray.add(it) }
          count += 1
          if (selectedImage.size == count) doCommitContentMultiple(imageUriArray)
          return false
        }
      }
      selectedImage.forEach { Glide.with(mContext).asBitmap().load(it.imageUri ?: "").listener(listenerRequest).submit() }
    } else Toast.makeText(mContext, mContext.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
  }

  fun doCommitContentMultiple(uris: ArrayList<Uri>) {
    florisBoard?.let {
      val packageNames: Array<String>? = mContext.packageManager.getPackagesForUid(it.currentInputBinding?.uid ?: 0) as? Array<String>
      if (packageNames.isNullOrEmpty().not() && it.currentInputEditorInfo?.getImageSupport() == true) {
        it.currentInputConnection.beginBatchEdit()
        uris.forEach { uri ->
          var flags = 1
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            flags = flags or InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION
          } else {
            mContext.grantUriPermission(packageNames!![0], uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
          }
          val inputContentInfo = InputContentInfoCompat(uri, ClipDescription("", arrayOf("image/png")), null)
          InputConnectionCompat.commitContent(it.currentInputConnection, it.currentInputEditorInfo, inputContentInfo, flags, null)
        }
        it.currentInputConnection.beginBatchEdit()
      } else {
        Toast.makeText(mContext, mContext.getString(R.string.image_not_suopported), Toast.LENGTH_SHORT).show()
      }
    }
  }

  private fun onPhotoSelected(item: BaseRecyclerItem) {
    photosSet.forEach { if (item == it) it.selected = (item as? Photo)?.selected == true }
    adapter.clearList()
    adapter.submitList(photosSet.toList())
    adapter.notifyDataSetChanged()
    updateLayout()
  }

  private fun updateLayout() {
    binding.btnImageShare.setOnClickListener { multipleImageToUriListGet() }
    binding.btnCancel.setOnClickListener { removeSelected() }
    if (photosSet.any { it.selected }) {
      binding.tvPhotos.gone()
      binding.btnLink.gone()
      binding.btnShare.gone()
      binding.containerShareImage.visible()
    } else {
      binding.tvPhotos.visible()
      binding.btnLink.visible()
      binding.btnShare.visible()
      binding.containerShareImage.gone()
    }
  }

  private fun removeSelected() {
    photosSet.forEach { it.selected = false }
    adapter.clearList()
    adapter.submitList(photosSet.toList())
    adapter.notifyDataSetChanged()
    updateLayout()
  }


  private fun loadMoreItems(businessFeatureEnum: BusinessFeatureEnum) {
    when (businessFeatureEnum) {
      BusinessFeatureEnum.UPDATES -> {
        viewModel.getUpdates(SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).fpId, mContext.getString(R.string.client_id), adapter.list.size, 10)
      }
      BusinessFeatureEnum.INVENTORY -> {
        viewModel.getProducts(SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).fpTag, mContext.getString(R.string.client_id), adapter.list.size, "SINGLE")
      }
      else -> {
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