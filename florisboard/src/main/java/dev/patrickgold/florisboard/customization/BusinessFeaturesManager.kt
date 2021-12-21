package dev.patrickgold.florisboard.customization

import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.content.ContextCompat
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
import com.bumptech.glide.request.target.Target
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.pref.*
import com.framework.utils.*
import com.framework.views.customViews.CustomImageView
import com.framework.views.dotsindicator.OffsetPageTransformer
import com.onboarding.nowfloats.extensions.capitalizeWords
import com.onboarding.nowfloats.model.channel.statusResponse.CHANNEL_STATUS_SUCCESS
import com.onboarding.nowfloats.model.channel.statusResponse.ChannelAccessStatusResponse
import com.onboarding.nowfloats.model.channel.statusResponse.ChannelAccessStatusResponse.Companion.CHANNEL_SHARE_URL
import com.onboarding.nowfloats.model.channel.statusResponse.ChannelAccessStatusResponse.Companion.saveDataConnectedChannel
import com.onboarding.nowfloats.model.channel.statusResponse.ChannelsType
import com.onboarding.nowfloats.model.digitalCard.CardData
import com.onboarding.nowfloats.ui.updateChannel.digitalChannel.addPlus91
import com.onboarding.nowfloats.utils.viewToBitmap
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.customization.adapter.BaseRecyclerItem
import dev.patrickgold.florisboard.customization.adapter.FeaturesEnum
import dev.patrickgold.florisboard.customization.adapter.OnItemClickListener
import dev.patrickgold.florisboard.customization.adapter.SharedAdapter
import dev.patrickgold.florisboard.customization.model.response.*
import dev.patrickgold.florisboard.customization.model.response.moreAction.ActionItem
import dev.patrickgold.florisboard.customization.model.response.moreAction.ActionItem.ActionData.Companion.fromType
import dev.patrickgold.florisboard.customization.model.response.moreAction.MoreData
import dev.patrickgold.florisboard.customization.model.response.staff.DataItem
import dev.patrickgold.florisboard.customization.model.response.staff.FilterBy
import dev.patrickgold.florisboard.customization.model.response.staff.GetStaffListingRequest
import dev.patrickgold.florisboard.customization.util.*
import dev.patrickgold.florisboard.customization.util.MethodUtils.getImageUri
import dev.patrickgold.florisboard.customization.util.PaginationScrollListener.Companion.PAGE_SIZE
import dev.patrickgold.florisboard.customization.util.PaginationScrollListener.Companion.PAGE_START
import dev.patrickgold.florisboard.databinding.BusinessFeaturesLayoutBinding
import dev.patrickgold.florisboard.ime.core.FlorisApplication
import dev.patrickgold.florisboard.ime.core.FlorisBoard
import dev.patrickgold.florisboard.ime.core.InputView
import dev.patrickgold.florisboard.ime.text.smartbar.SmartbarView
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class BusinessFeaturesManager(inputView: InputView, florisBoard: FlorisBoard) : OnItemClickListener {

  init {
    onRegisterInputView(inputView, florisBoard)
  }

  private val TAG = "BusinessFeaturesManager"
  private lateinit var binding: BusinessFeaturesLayoutBinding
  private var sharedPref: SharedPrefUtil = SharedPrefUtil.fromBoostPref().getsBoostPref(FlorisApplication.instance)
  private lateinit var viewModel: BusinessFeaturesViewModel
  private var businessFeatureEnum: BusinessFeatureEnum? = null
  private val photosSet = mutableSetOf<Photo>()
  private val moreItemList = mutableSetOf<MoreData>()
  private lateinit var mContext: Context
  private var florisBoard: FlorisBoard? = null
  private var session: UserSessionManager? = null
  private lateinit var currentSelectedFeature: BusinessFeatureEnum
  private var tagPosition: Int = 0

  private lateinit var adapterProduct: SharedAdapter<Product>
  private lateinit var adapterService: SharedAdapter<ItemServices>
  private lateinit var adapterUpdates: SharedAdapter<FloatUpdate>
  private lateinit var adapterPhoto: SharedAdapter<Photo>
  private lateinit var adapterStaff: SharedAdapter<DataItem>
  private lateinit var adapterMoreAction: SharedAdapter<MoreData>
  private lateinit var adapterBusinessCard: SharedAdapter<DigitalCardDataKeyboard>
  private var listenerRequest: RequestListener<Bitmap>? = null

  private var connectedChannels: ArrayList<String> = arrayListOf()

  private val _connectedChannels: ArrayList<String>
    get() {
      return ChannelAccessStatusResponse.getConnectedChannel()
    }

  private var finalShareMessage = ""
  private var tapPhotoSelect: Boolean = true
  private lateinit var gridType: Photo.ViewGridType

  private val messageBusiness: String
    get() {
      return PreferencesUtils.instance.getData(CHANNEL_SHARE_URL, "") ?: ""
    }

  /* Paging */
  private var isFirstPage = true
  private var isLoadingD = false
  private var PAGING_TOTAL_ELEMENTS = 0
  private var offSet: Int = PAGE_START
  private var limit: Int = PAGE_SIZE
  private var isLastPageD = false

  private fun onRegisterInputView(inputView: InputView, florisBoard: FlorisBoard) {
    this.mContext = inputView.context
    this.session = UserSessionManager(this.mContext)
    this.florisBoard = florisBoard
    this.viewModel = BusinessFeaturesViewModel(mContext)
    this.gridType = Photo.ViewGridType.FOUR_GRID

    this.adapterProduct = SharedAdapter(arrayListOf(), this)
    this.adapterService = SharedAdapter(arrayListOf(), this)
    this.adapterUpdates = SharedAdapter(arrayListOf(), this)
    this.adapterPhoto = SharedAdapter(arrayListOf(), this)
    this.adapterBusinessCard = SharedAdapter(arrayListOf(), this)
    this.adapterStaff = SharedAdapter(arrayListOf(), this)
    this.adapterMoreAction = SharedAdapter(arrayListOf(), this)
    // initialize business features views
    this.binding = BusinessFeaturesLayoutBinding.bind(inputView.findViewById(R.id.business_features))

    binding.rvKeyboard.productRvList.also {
      it.adapter = this.adapterProduct
      (it.layoutManager as? LinearLayoutManager)?.let { it1 -> it.paginationListener(it1) }
    }

    binding.rvKeyboard.serviceRvList.also {
      it.adapter = this.adapterService
      (it.layoutManager as? LinearLayoutManager)?.let { it1 -> it.paginationListener(it1) }
    }

    binding.rvKeyboard.updateRvList.also {
      it.adapter = this.adapterUpdates
      (it.layoutManager as? LinearLayoutManager)?.let { it1 -> it.paginationListener(it1) }
    }

    binding.businessCardView.viewPagerProfile.also {
      it.adapter = this.adapterBusinessCard
      it.offscreenPageLimit = 3
      it.setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position) }
    }

    binding.rvKeyboard.staffRvList.also {
      it.layoutManager = GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false)
      it.adapter = this.adapterStaff
      (it.layoutManager as? LinearLayoutManager)?.let { it1 -> it.paginationListener(it1, false) }
    }

    binding.rvPhotoView.rvListPhotos.also {
      it.layoutManager = GridLayoutManager(mContext, gridType.countGrid, GridLayoutManager.VERTICAL, false)
      it.adapter = this.adapterPhoto
    }

    binding.moreActionView.rvMoreAction.also { it.adapter = this.adapterMoreAction }

    this.binding.moreActionView.btnSetting.setOnClickListener { Toast.makeText(mContext, mContext.getString(R.string.coming_soon), Toast.LENGTH_SHORT).show() }
    getChannelAccessToken()

    apiObserveProduct()
    apiObserveService()
    apiObserveUpdates()
    apiObservePhotos()
    apiObserveStaff()
    apiObserveMoreAction()
    apiObserveUserDetails()
    errorObserveListener()
  }

  private fun RecyclerView.paginationListener(layoutManager: LinearLayoutManager, isPagerSnap: Boolean = true) {
    val listenerProduct = object : PaginationScrollListener(layoutManager) {
      override fun loadMoreItems() {
        if (!isLastPageD) {
          isFirstPage = false
          isLoadingD = true
          offSet += limit
          if (this@BusinessFeaturesManager::currentSelectedFeature.isInitialized && currentSelectedFeature == BusinessFeatureEnum.UPDATES) {
            adapterUpdates.addLoadingFooter(FloatUpdate().getLoaderItem())
            viewModel.getUpdates(session?.fPID, clientId, offSet, limit)
          } else if (currentSelectedFeature == BusinessFeatureEnum.INVENTORY_SERVICE) {
            if (isProductType(session?.fP_AppExperienceCode)) {
              adapterProduct.addLoadingFooter(Product().getLoaderItem())
              viewModel.getProducts(session?.fpTag, clientId, offSet, "SINGLE")
            } else {
              adapterService.addLoadingFooter(ItemServices().getLoaderItem())
              viewModel.getServices(session?.fpTag, session?.fPID, offset = offSet, limit = limit)
            }
          } else if (currentSelectedFeature == BusinessFeatureEnum.STAFF) {
            adapterStaff.addLoadingFooter(DataItem().getLoaderItem())
            viewModel.getStaffList(getFilterRequest(offSet, limit))
          }
        }
      }

      override val totalPageCount: Int
        get() = PAGING_TOTAL_ELEMENTS
      override val isLastPage: Boolean
        get() = isLastPageD
      override val isLoading: Boolean
        get() = isLoadingD
    }
    if (isPagerSnap) PagerSnapHelper().attachToRecyclerView(this)
    addOnScrollListener(listenerProduct)
  }

  fun showSelectedBusinessFeature(tagPosition: Int, businessFeatureEnum: BusinessFeatureEnum) {
    this.session = UserSessionManager(this.mContext)
    this.businessFeatureEnum = businessFeatureEnum
    this.tagPosition = tagPosition
    this.currentSelectedFeature = businessFeatureEnum
    this.listenerRequest = null
    val lastSyncTime = sharedPref.lastSyncTime
    if (session?.isUserLoggedIn == false) {
      updateUiNotLoginned()
    } else if (lastSyncTime == null || MethodUtils.getDaysDiff(System.currentTimeMillis(), lastSyncTime) >= 1) {
      Log.i(TAG, "last sync is greater than 24 hour: $lastSyncTime")
      viewModel.getDetails(session?.fpTag, clientId)
    } else loadDataBasesOnTab()
  }

  private fun loadDataBasesOnTab() {
    resetAdapters()
    binding.lockView.msgLayout.gone()
    if (session?.getStoreWidgets()?.contains("BOOSTKEYBOARD") == true) {
      binding.businessFeatureProgress.visible()
      when (businessFeatureEnum) {
        BusinessFeatureEnum.INVENTORY_SERVICE -> {
          SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(tagPosition)?.text = getProductType(session?.fP_AppExperienceCode ?: "")
          initializePaging()
          if (isProductType(session?.fP_AppExperienceCode)) {
            visibleSelectType(isIP = true)
            this.adapterProduct.clearList()
            binding.rvKeyboard.productRvList.removeAllViewsInLayout()
            viewModel.getProducts(session?.fpTag, clientId, offSet, "SINGLE")
          } else {
            visibleSelectType(isIS = true)
            this.adapterService.clearList()
            binding.rvKeyboard.serviceRvList.removeAllViewsInLayout()
            viewModel.getServices(session?.fpTag, session?.fPID, offset = offSet, limit = limit)
          }

        }
        BusinessFeatureEnum.UPDATES -> {
          visibleSelectType(isII = true)
          initializePaging()
          this.adapterUpdates.clearList()
          binding.rvKeyboard.updateRvList.removeAllViewsInLayout()
          viewModel.getUpdates(session?.fPID, clientId, offSet, limit)
        }
        BusinessFeatureEnum.PHOTOS -> {
          visibleSelectType(isIII = true)
          this.photosSet.clear()
          this.adapterPhoto.clearList()
          viewModel.getPhotos(session?.fPID ?: "")
        }
        BusinessFeatureEnum.BUSINESS_CARD -> {
          visibleSelectType(isIV = true)
          viewModel.checkInternetForBusinessCard()
          this.adapterBusinessCard.clearList()
          if (messageBusiness.isEmpty() && _connectedChannels.isEmpty()) getChannelAccessToken(true)
          businessCardDataLoad()
        }
        BusinessFeatureEnum.STAFF -> {
          visibleSelectType(isV = true)
          if (session?.getStoreWidgets()?.contains("STAFFPROFILE") == true) {
            initializePaging()
            this.adapterStaff.clearList()
            binding.rvKeyboard.staffRvList.removeAllViewsInLayout()
            viewModel.getStaffList(getFilterRequest(offSet, limit))
          } else {
            binding.businessFeatureProgress.gone()
            updateUiStaffNotRenewd()
          }
        }
        BusinessFeatureEnum.MORE -> {
          visibleSelectType(isVI = true)
          val versionName: String = mContext.packageManager.getPackageInfo(mContext.packageName, 0).versionName
          binding.moreActionView.txtVersion.text = "V$versionName"
          this.moreItemList.clear()
          this.adapterMoreAction.clearList()
          viewModel.getMoreActionList(this.mContext, session?.fP_AppExperienceCode ?: "")
        }
        else -> {
        }
      }
    } else {
      Timber.i("Please add keyboard in your current plan.")
      updateUiFeatureNotRenewed()
    }
  }

  private fun resetAdapters() {
    if (isProductType(session?.fP_AppExperienceCode)) this.adapterProduct.clearList() else this.adapterService.clearList()
    this.adapterUpdates.clearList()
    this.adapterPhoto.clearList()
    this.adapterBusinessCard.clearList()
    this.adapterStaff.clearList()
    SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(1)?.text = "${getProductType(session?.fP_AppExperienceCode ?: "")}"
    SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(2)?.text = BusinessFeatureEnum.UPDATES.value
    SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(3)?.text = BusinessFeatureEnum.PHOTOS.value
    SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(4)?.text = BusinessFeatureEnum.STAFF.value
    SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(4)?.view?.apply {
      visibility = if (isStaffVisible(session?.fP_AppExperienceCode ?: "")) View.VISIBLE else View.GONE
    }
    SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(6)?.text = BusinessFeatureEnum.MORE.value
  }


  private fun updateUiErrorFetchingInformation() {
    binding.lockView.msgLayout.visible()
    binding.lockView.msgIcon.setImageResource(R.drawable.ic_linkbreak)
    binding.lockView.msgTitle.text = mContext.getString(R.string.error_while_fetching_your_business)
    binding.lockView.msgDesc.text = mContext.getString(R.string.please_click_on_retry_btn_below)
    binding.lockView.msgBtn.text = mContext.getString(R.string.retry)
    binding.lockView.msgBtn.icon = mContext.getDrawable(R.drawable.ic_arrowscounterclockwise)
    binding.lockView.msgBtn.setOnClickListener { loadDataBasesOnTab() }
  }

  private fun updateUiInternetNotAvailable() {
    binding.lockView.msgLayout.visible()
    binding.lockView.msgIcon.setImageResource(R.drawable.ic_wifislash)
    binding.lockView.msgTitle.text = mContext.getString(R.string.internet_not_available)
    binding.lockView.msgDesc.text = mContext.getString(R.string.a_wifi_or_cellular)
    binding.lockView.msgBtn.text = mContext.getString(R.string.open_settings)
    binding.lockView.msgBtn.icon = mContext.getDrawable(R.drawable.ic_settings_white)
    binding.lockView.msgBtn.setOnClickListener {
      try {
        mContext.startActivity(Intent(Settings.ACTION_DATA_ROAMING_SETTINGS).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK; })
      } catch (e: Exception) {
        Log.e(TAG, "updateUiInternetNotAvailable:  ${e.localizedMessage}")
        Toast.makeText(mContext, "Unable to find network settings. Please do it manually from phone's settings", Toast.LENGTH_LONG).show()
      }
    }
  }

  private fun updateUiNotLoginned() {
    binding.lockView.msgLayout.visible()
    binding.lockView.msgIcon.setImageResource(R.drawable.ic_storefront)
    binding.lockView.msgTitle.text = mContext.getString(R.string.login_to_start_sharing)
    binding.lockView.msgDesc.text = mContext.getString(R.string.with_business_keyboard)
    binding.lockView.msgBtn.text = mContext.getString(R.string.login_to_boost)
    binding.lockView.msgBtn.icon = mContext.getDrawable(R.drawable.ic_key)
    binding.lockView.msgBtn.setOnClickListener { startBoostActivity(mContext) }
  }

  private fun updateUiFeatureNotRenewed() {
    binding.lockView.msgLayout.visible()
    binding.lockView.msgIcon.setImageResource(R.drawable.ic_keyboard_renew)
    binding.lockView.msgTitle.text = mContext.getString(R.string.feature_not_renewed)
    binding.lockView.msgDesc.text = mContext.getString(R.string.with_business_keyboard)
    binding.lockView.msgBtn.text = mContext.getString(R.string.renew_feature)
    binding.lockView.msgBtn.icon = mContext.getDrawable(R.drawable.ic_lockkey)
    binding.lockView.msgBtn.setOnClickListener { startKeyboardActivity(mContext) }
  }

  private fun updateUiStaffNotRenewd() {
    binding.lockView.msgLayout.visible()
    binding.lockView.msgIcon.setImageResource(R.drawable.ic_keyboard_renew)
    binding.lockView.msgTitle.text = mContext.getString(R.string.staff_not_added_plan)
    binding.lockView.msgDesc.text = mContext.getString(R.string.with_business_keyboard)
    binding.lockView.msgBtn.text = mContext.getString(R.string.renew_feature)
    binding.lockView.msgBtn.icon = mContext.getDrawable(R.drawable.ic_lockkey)
    binding.lockView.msgBtn.setOnClickListener { startStaffActivity(mContext) }
  }

  private fun visibleSelectType(
    isIP: Boolean = false, isIS: Boolean = false, isII: Boolean = false, isIII: Boolean = false,
    isIV: Boolean = false, isV: Boolean = false, isVI: Boolean = false
  ) {
    binding.rvKeyboard.productRvList.visibility = if (isIP) View.VISIBLE else View.GONE
    binding.rvKeyboard.serviceRvList.visibility = if (isIS) View.VISIBLE else View.GONE
    binding.rvKeyboard.updateRvList.visibility = if (isII) View.VISIBLE else View.GONE
    binding.rvPhotoView.root.visibility = if (isIII) View.VISIBLE else View.GONE
    binding.businessCardView.root.visibility = if (isIV) View.VISIBLE else View.GONE
    binding.rvKeyboard.staffRvList.visibility = if (isV) View.VISIBLE else View.GONE
    binding.moreActionView.root.visibility = if (isVI) View.VISIBLE else View.GONE
  }

  private fun errorObserveListener() {
    viewModel.error.observeForever { error ->
      Log.e(TAG, "errorObserveListener: $error")
      binding.businessFeatureProgress.gone()
      if (error.contains(this.businessFeatureEnum?.name ?: "", ignoreCase = true)) {
        if (
          error.contains(Constants.FAILED_TO_CONNECT, ignoreCase = true) ||
          error.contains(Constants.UNABLE_TO_RESOLVED_HOST, ignoreCase = true) ||
          error.contains(Constants.NO_INTERNET_CONNECTION, ignoreCase = true)
        ) updateUiInternetNotAvailable()
        else if (error.contains(Constants.TOKEN_EXPIRED_MESSAGE, ignoreCase = true)) updateUiNotLoginned()
        else updateUiErrorFetchingInformation()
      }
    }
  }

  private fun apiObserveUserDetails() {
    viewModel.details.observeForever {
      Log.i(TAG, "apiObserveUserDetails: ${it?.FPWebWidgets}")
      if (it != null) {
        session?.storeFPDetails(Key_Preferences.STORE_WIDGETS, convertListObjToString(it.FPWebWidgets ?: arrayListOf()))
        SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).lastSyncTime = System.currentTimeMillis()
        loadDataBasesOnTab()
      } else loadDataBasesOnTab()
    }
  }

  private fun apiObservePhotos() {
    viewModel.photos.observeForever {
      Timber.e("photos - $it.")
      binding.businessFeatureProgress.gone()
      if (it.isNotEmpty()) {
        this.photosSet.clear()
        this.photosSet.addAll(it)
        this.photosSet.map { it1 -> it1.gridType = this.gridType }
        binding.rvPhotoView.rvListPhotos.layoutManager = GridLayoutManager(mContext, gridType.countGrid, GridLayoutManager.VERTICAL, false)
        this.adapterPhoto.notifyNewList(this.photosSet.toList())
        SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(3)?.text = BusinessFeatureEnum.PHOTOS.value + " (${photosSet.size})"
      } else {
        if (businessFeatureEnum == BusinessFeatureEnum.INVENTORY_SERVICE) {
          this.adapterPhoto.notifyNewList(arrayListOf())
        }
      }
      clickListenerPhoto()
    }
  }

  private fun apiObserveMoreAction() {
    viewModel.moreAction.observeForever {
      Timber.e("photos - $it.")
      binding.businessFeatureProgress.gone()
      if (it.isNotEmpty()) {
        this.moreItemList.clear()
        this.moreItemList.addAll(it)
        this.adapterMoreAction.notifyNewList(this.moreItemList.toList())
        SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(6)?.text = BusinessFeatureEnum.MORE.value
      } else {
        if (businessFeatureEnum == BusinessFeatureEnum.MORE) {
          this.adapterMoreAction.notifyNewList(arrayListOf())
        }
      }
    }
  }

  private fun apiObserveProduct() {
    viewModel.products.observeForever {
      Timber.i("products - $it.")
      binding.businessFeatureProgress.gone()
      this.adapterProduct.removeLoaderN()
      if (it.Products.isNullOrEmpty().not()) {
        if (isFirstPage || it.isRefreshList) {
          it.Products?.let { it1 -> this.adapterProduct.notifyNewList(it1) }
        } else {
          it.Products?.let { it1 -> this.adapterProduct.addItems(it1) }
        }
        PAGING_TOTAL_ELEMENTS = it.TotalCount ?: 0
        SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(1)?.text = "${getProductType(session?.fP_AppExperienceCode ?: "")} (${it.TotalCount})"
      } else {
        if (businessFeatureEnum == BusinessFeatureEnum.INVENTORY_SERVICE && isFirstPage) {
          this.adapterProduct.notifyNewList(arrayListOf())
        }
      }
    }
  }

  private fun apiObserveService() {
    viewModel.services.observeForever {
      Timber.i("products - $it.")
      binding.businessFeatureProgress.gone()
      this.adapterService.removeLoaderN()
      val dataService = it.result
      val listItems = getCopyNewItems(dataService?.data)
      if (listItems.isNullOrEmpty().not()) {
        if (isFirstPage || it.isRefreshList) {
          this.adapterService.notifyNewList(listItems)
        } else {
          this.adapterService.addItems(listItems)
        }
        PAGING_TOTAL_ELEMENTS = dataService?.paging?.count ?: 0
        SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(1)?.text = "${getProductType(session?.fP_AppExperienceCode ?: "")} (${dataService?.paging?.count})"
      } else {
        if (businessFeatureEnum == BusinessFeatureEnum.INVENTORY_SERVICE && isFirstPage) {
          this.adapterService.notifyNewList(arrayListOf())
        }
      }
    }
  }

  private fun apiObserveStaff() {
    viewModel.staff.observeForever {
      Timber.i("updates - $it.")
      binding.businessFeatureProgress.gone()
      this.adapterStaff.removeLoaderN()
      if (it.data.isNullOrEmpty().not()) {
        PAGING_TOTAL_ELEMENTS = it.paging?.count ?: 0
        if (isFirstPage) this.adapterStaff.notifyNewList(it.data!!)
        else this.adapterStaff.addItems(it.data!!)
        isLastPageD = (this.adapterStaff.getListData().size == PAGING_TOTAL_ELEMENTS)
        SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(4)?.text = BusinessFeatureEnum.STAFF.value + " (${it.paging?.count})"
      } else {
        if (businessFeatureEnum == BusinessFeatureEnum.STAFF && isFirstPage) {
          this.adapterStaff.notifyNewList(arrayListOf())
        }
      }
    }
  }

  private fun apiObserveUpdates() {
    viewModel.updates.observeForever {
      Timber.i("updates - $it.")
      binding.businessFeatureProgress.gone()
      this.adapterUpdates.removeLoaderN()
      if (it.floats.isNullOrEmpty().not()) {
        PAGING_TOTAL_ELEMENTS = it.totalCount ?: 0
        if (isFirstPage) this.adapterUpdates.notifyNewList(it.floats!!)
        else this.adapterUpdates.addItems(it.floats!!)
        isLastPageD = (this.adapterUpdates.getListData().size == PAGING_TOTAL_ELEMENTS)
        SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(2)?.text = BusinessFeatureEnum.UPDATES.value + " (${it.totalCount})"
      } else {
        if (businessFeatureEnum == BusinessFeatureEnum.UPDATES && isFirstPage) {
          this.adapterUpdates.notifyNewList(arrayListOf())
        }
      }
    }
  }

  private fun businessCardDataLoad() {
    var imageUri = session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_LogoUrl)
    if (imageUri.isNullOrEmpty().not() && imageUri!!.contains("http").not()) imageUri = BASE_IMAGE_URL + imageUri
    val city = session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_CITY)
    val country = session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_COUNTRY)
    val location = if (city.isNullOrEmpty().not() && country.isNullOrEmpty().not()) "$city, $country" else "$city$country"
    val cardData = CardData(
      session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME), imageUri, location,
      session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_CONTACTNAME)?.capitalizeWords(), addPlus91(session?.userPrimaryMobile),
      session?.fPEmail, session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_CATEGORY),
      session?.getDomainName(false), session?.getIconCard()
    )

    viewModel.getMerchantProfile(session?.fPID)
    viewModel.merchantProfileData.observeForever { it1 ->
      if (it1.result?.channelProfileProperties.isNullOrEmpty().not()) {
        val userDetail = it1.result?.getUserDetail()!!
        cardData.name = when {
          cardData.name.isNullOrEmpty().not() -> cardData.name
          userDetail.userName.isNullOrEmpty().not() -> userDetail.userName
          else -> session?.fpTag
        }
        cardData.number = when {
          cardData.number.isNullOrEmpty().not() -> cardData.number
          userDetail.userMobile.isNullOrEmpty().not() -> userDetail.userMobile
          else -> ""
        }
        cardData.email = when {
          cardData.email.isNullOrEmpty().not() -> cardData.email
          userDetail.userEmail.isNullOrEmpty().not() -> userDetail.userEmail
          else -> ""
        }
      }
      val cardList = ArrayList<DigitalCardDataKeyboard>()
      cardList.add(DigitalCardDataKeyboard(cardData = cardData, recyclerViewType = FeaturesEnum.VISITING_CARD_ONE_ITEM.ordinal))
      cardList.add(DigitalCardDataKeyboard(cardData = cardData, recyclerViewType = FeaturesEnum.VISITING_CARD_FOUR_ITEM.ordinal))
      cardList.add(DigitalCardDataKeyboard(cardData = cardData, recyclerViewType = FeaturesEnum.VISITING_CARD_SIX_ITEM.ordinal))
      cardList.add(DigitalCardDataKeyboard(cardData = cardData, recyclerViewType = FeaturesEnum.VISITING_CARD_EIGHT_ITEM.ordinal))
      cardList.add(DigitalCardDataKeyboard(cardData = cardData, recyclerViewType = FeaturesEnum.VISITING_CARD_TWO_ITEM.ordinal))
      cardList.add(DigitalCardDataKeyboard(cardData = cardData, recyclerViewType = FeaturesEnum.VISITING_CARD_THREE_ITEM.ordinal))
      cardList.add(DigitalCardDataKeyboard(cardData = cardData, recyclerViewType = FeaturesEnum.VISITING_CARD_FIVE_ITEM.ordinal))
      cardList.add(DigitalCardDataKeyboard(cardData = cardData, recyclerViewType = FeaturesEnum.VISITING_CARD_SEVEN_ITEM.ordinal))
      cardList.add(DigitalCardDataKeyboard(cardData = cardData, recyclerViewType = FeaturesEnum.VISITING_CARD_NINE_ITEM.ordinal))
      cardList.add(DigitalCardDataKeyboard(cardData = cardData, recyclerViewType = FeaturesEnum.VISITING_CARD_TEN_ITEM.ordinal))

      this.adapterBusinessCard.notifyNewList(cardList)
      binding.businessFeatureProgress.gone()
      binding.businessCardView.btnShareImageBusiness.setOnClickListener { shareImageTextBusiness() }
      binding.businessCardView.btnShareImageTextBusiness.setOnClickListener {
        if (finalShareMessage.isNotEmpty()) shareImageTextBusiness(finalShareMessage) else getVisitingMessageData(true)
      }
    }
  }

  private fun clickListenerPhoto() {
    binding.rvPhotoView.btnSelectGrid.setOnClickListener {
      tapPhotoSelect = tapPhotoSelect.not()
      visiblePhotoTopView()
    }
    binding.rvPhotoView.btnChangeGridLayout.setOnClickListener {
      tapPhotoSelect = tapPhotoSelect.not()
      visiblePhotoTopView()
    }
    binding.rvPhotoView.btnShare.setOnClickListener { }
    binding.rvPhotoView.photoGridOne.setOnClickListener { adapterNotifyGrid(Photo.ViewGridType.FIRST_GRID) }
    binding.rvPhotoView.photoGridTwo.setOnClickListener { adapterNotifyGrid(Photo.ViewGridType.SECOND_GRID) }
    binding.rvPhotoView.photoGridThree.setOnClickListener { adapterNotifyGrid(Photo.ViewGridType.THIRD_GRID) }
    binding.rvPhotoView.photoGridFour.setOnClickListener { adapterNotifyGrid(Photo.ViewGridType.FOUR_GRID) }
    visiblePhotoTopView()
  }

  private fun adapterNotifyGrid(grid: Photo.ViewGridType) {
    this.gridType = grid
    val bacRound = ContextCompat.getDrawable(mContext, R.drawable.ic_mask_bac_f)
    binding.rvPhotoView.photoGridOne.apply { setGridImageIcon((gridType == Photo.ViewGridType.FIRST_GRID), bacRound) }
    binding.rvPhotoView.photoGridTwo.apply { setGridImageIcon(gridType == Photo.ViewGridType.SECOND_GRID, bacRound) }
    binding.rvPhotoView.photoGridThree.apply { setGridImageIcon(gridType == Photo.ViewGridType.THIRD_GRID, bacRound) }
    binding.rvPhotoView.photoGridFour.apply { setGridImageIcon(gridType == Photo.ViewGridType.FOUR_GRID, bacRound) }
    binding.rvPhotoView.imgGridImage.setImageResource(this.gridType.icon)
    this.photosSet.map { it.gridType = this.gridType }
    binding.rvPhotoView.rvListPhotos.layoutManager = GridLayoutManager(mContext, gridType.countGrid, GridLayoutManager.VERTICAL, false)
    this.adapterPhoto.notifyDataSetChanged()
  }

  private fun CustomImageView.setGridImageIcon(isSelect: Boolean, bacRound: Drawable?) {
    alpha = if (isSelect) 1.0F else 0.5F
    background = if (isSelect) bacRound else null
  }


  private fun shareImageTextBusiness(shareText: String = "") {
    val bitmap = binding.businessCardView.viewPagerProfile.getChildAt(0)?.let { viewToBitmap(it) }
    try {
      val cropBitmap = bitmap?.let { Bitmap.createBitmap(it, 40, 0, bitmap.width - 80, bitmap.height) }
      val path = MediaStore.Images.Media.insertImage(mContext.contentResolver, cropBitmap, "boost_${Date().time}", null)
      val imageUri: Uri = Uri.parse(path)
      shareUriWithText(imageUri, shareText)
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun getVisitingMessageData(isShare: Boolean = false) {
    viewModel.getBoostVisitingMessage(mContext)
    viewModel.shareUserDetailData.observeForever {
      if (it.data.isNullOrEmpty().not()) {
        val messageDetail = it.data?.firstOrNull { it1 -> it1.type.equals(session?.fP_AppExperienceCode, ignoreCase = true) }?.message
        if (messageDetail.isNullOrEmpty().not()) {
          val lat = session?.getFPDetails(Key_Preferences.LATITUDE)
          val long = session?.getFPDetails(Key_Preferences.LONGITUDE)
          var location = ""
          val address = session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_ADDRESS)
          if (lat != null && long != null) location = "${if (messageBusiness.isNotEmpty()) "\n\n" else ""}\uD83D\uDCCD *Find us on map: http://www.google.com/maps/place/$lat,$long*\n\n"
          if (address.isNullOrEmpty().not()) location = "$location Address: $address\n\n"
          finalShareMessage = String.format(messageDetail!!, session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME) ?: "", session?.getDomainName(false), messageBusiness, location)
        }
      }
      if (isShare) shareImageTextBusiness(finalShareMessage)
    }
  }

  private fun getChannelAccessToken(isShow: Boolean = false) {
    if (isShow) binding.businessFeatureProgress.visible() else binding.businessFeatureProgress.gone()
    viewModel.getChannelsAccessTokenStatus(session?.fPID)
    viewModel.channelStatusData.observeForever {
      var urlString = ""
      if (it.channels != null) {
        connectedChannels.clear()
        if (it?.channels?.facebookpage?.status == CHANNEL_STATUS_SUCCESS) {
          urlString = "\n⚡ *Facebook: https://www.facebook.com/${it.channels?.facebookpage?.account?.accountId}*"
          connectedChannels.add(ChannelsType.AccountType.facebookpage.name)
        }
        if (it?.channels?.twitter?.status == CHANNEL_STATUS_SUCCESS) {
          urlString += "\n⚡ *Twitter: https://twitter.com/${it.channels?.twitter?.account?.accountName?.trim()}*"
          connectedChannels.add(ChannelsType.AccountType.twitter.name)
        }

        if (it?.channels?.googlemybusiness?.status == CHANNEL_STATUS_SUCCESS) {
          connectedChannels.add(ChannelsType.AccountType.googlemybusiness.name)
        }
        if (it?.channels?.facebookshop?.status == CHANNEL_STATUS_SUCCESS) {
          connectedChannels.add(ChannelsType.AccountType.facebookshop.name)
        }
      }
      viewModel.getWhatsAppBusiness(request = session?.fpTag, auth = WA_KEY)
      viewModel.channelWhatsAppData.observeForever { it1 ->
        val response = it1.Data?.firstOrNull()
        if (response?.active_whatsapp_number.isNullOrEmpty().not()) {
          urlString += "\n⚡ *WhatsApp: https://wa.me/${response?.getNumberPlus91()}*"
          connectedChannels.add(ChannelsType.AccountType.WAB.name)
        }
        val otherWebsite = session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_WEBSITE)
        if (otherWebsite.isNullOrEmpty().not()) urlString += "\n⚡ *Other Website: $otherWebsite*"
        saveDataConnectedChannel(connectedChannels)
        if (session?.userPrimaryMobile.isNullOrEmpty().not()) urlString += "\n\uD83D\uDCDE *Call: ${session?.userPrimaryMobile}*"
        PreferencesUtils.instance.saveData(CHANNEL_SHARE_URL, urlString)
        getVisitingMessageData()
        if (isShow) binding.businessFeatureProgress.gone()
      }
    }
  }

  override fun onItemClick(pos: Int, item: BaseRecyclerItem, actionType: Int) {
    when (currentSelectedFeature) {
      BusinessFeatureEnum.UPDATES -> shareUpdates(item)
      BusinessFeatureEnum.INVENTORY_SERVICE -> onClickedShareInventory(item, actionType)
      BusinessFeatureEnum.PHOTOS -> onPhotoSelected(item)
      BusinessFeatureEnum.STAFF -> shareStaff(item)
      BusinessFeatureEnum.BUSINESS_CARD -> Timber.i("pos - $pos item = $item")
      BusinessFeatureEnum.MORE -> clickHandleMore(pos, item, actionType)
      else -> {
      }
    }
  }

  private fun clickHandleMore(pos: Int, item: BaseRecyclerItem, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.MORE_ACTION_CLICK.ordinal -> {
        val data = item as? ActionItem ?: return
        when (fromType(data.type)) {
          ActionItem.ActionData.BOOK_ORDER_ID -> startOrderCreateN(mContext)
          ActionItem.ActionData.BOOK_CLINIC_APPOINTMENT_ID -> startBookAppointmentConsultN(mContext, false)
          ActionItem.ActionData.BOOK_VIDEO_CONSULTATION_ID -> startBookAppointmentConsultN(mContext)
          ActionItem.ActionData.ADD_TESTIMONIAL_ID -> startTestimonialN(mContext, true)
          ActionItem.ActionData.REFUND_CANCELLATION_ID -> {
            Toast.makeText(mContext, mContext.getString(R.string.coming_soon), Toast.LENGTH_SHORT).show()
          }
          ActionItem.ActionData.ALL_ORDERS_ID -> startOrderAptConsultListN(mContext, isOrder = true)
          ActionItem.ActionData.IN_CLINIC_APPOINTMENT_ID -> startOrderAptConsultListN(mContext)
          ActionItem.ActionData.VIDEO_CONSULTATION_ID -> startOrderAptConsultListN(mContext, isConsult = true)
          ActionItem.ActionData.CUSTOMER_MESSAGE_ID -> startBusinessEnquiryN(mContext)
          ActionItem.ActionData.CUSTOMER_CALLS_ID -> startVmnCallCardN(mContext)
          ActionItem.ActionData.SERVICES_CATALOG_ID -> startListServiceProductN(mContext)
          ActionItem.ActionData.UPDATES_TIPS_ID -> startUpdateLatestStoryN(mContext)
          ActionItem.ActionData.STAFF_PROFILES_ID -> startListStaffN(mContext)
          ActionItem.ActionData.ALL_IMAGES_ID -> startAllImageN(mContext)
          ActionItem.ActionData.CUSTOMER_TESTIMONIALS_ID -> startTestimonialN(mContext)
          ActionItem.ActionData.CUSTOM_PAGES_ID -> startCustomPageN(mContext)
          ActionItem.ActionData.BUSINESS_TIMINGS_ID -> startBusinessHoursN(mContext)
          ActionItem.ActionData.BUSINESS_ADDRESS_ID -> startBusinessAddressN(mContext)
          ActionItem.ActionData.CONTACT_DETAILS_ID -> startBusinessContactInfoN(mContext)
          ActionItem.ActionData.BASIC_BUSINESS_ID -> startBusinessProfileDetailEditN(mContext)
        }
      }
      RecyclerViewActionType.EYE_MORE_ITEM_CLICK.ordinal -> {
        Toast.makeText(mContext, mContext.getString(R.string.coming_soon), Toast.LENGTH_SHORT).show()
      }
      RecyclerViewActionType.SHARE_MORE_ITEM_CLICK.ordinal -> {
        Toast.makeText(mContext, mContext.getString(R.string.coming_soon), Toast.LENGTH_SHORT).show()
      }
    }
  }


  private fun shareStaff(item: BaseRecyclerItem) {
    val staff = item as? DataItem
    if (NetworkUtils.isNetworkConnected()) {
      val shareText = getStaffShare(staff)
      pathToUriGet(staff?.image, shareText, BusinessFeatureEnum.STAFF)
    } else Toast.makeText(mContext, mContext.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
  }

  private fun shareUpdates(item: BaseRecyclerItem) {
    val float = item as? FloatUpdate
    if (NetworkUtils.isNetworkConnected()) {
      val templateBuilder = session?.shareUpdates(float?.message!!, float.url, session?.userProfileMobile, float.imageUri)
      pathToUriGet(float?.imageUri, templateBuilder.toString(), BusinessFeatureEnum.UPDATES)
    } else Toast.makeText(mContext, mContext.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
  }

  private fun onClickedShareInventory(item: BaseRecyclerItem, actionType: Int) {
    if (NetworkUtils.isNetworkConnected()) {
      when (actionType) {
        RecyclerViewActionType.PRODUCT_SHARE_CLICK.ordinal -> {
          val product = (item as? Product) ?: return
          val templateBuilder = shareProduct(
            product.name, product.getDiscountedPrice().toString(), product.productUrl, session?.userProfileMobile
          )
          pathToUriGet(product.imageUri, templateBuilder, BusinessFeatureEnum.INVENTORY_SERVICE)
        }
        RecyclerViewActionType.SERVICE_SHARE_CLICK.ordinal -> {
          val serviceData = (item as? ItemServices)?.data ?: return
          var fpDetails = session?.getFPDetails(Key_Preferences.PRODUCT_CATEGORY_VERB)
          if (fpDetails.isNullOrEmpty()) fpDetails = "Services"
          val templateBuilder = shareProduct(
            serviceData.name, serviceData.discountedPrice?.toString() ?: "0.0", "${session?.getDomainName()}/all-$fpDetails", session?.userProfileMobile
          )
          pathToUriGet(serviceData.image, templateBuilder, BusinessFeatureEnum.INVENTORY_SERVICE)
        }
      }

    } else Toast.makeText(mContext, mContext.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
  }

  private fun pathToUriGet(imageUri: String?, shareText: String, type: BusinessFeatureEnum) {
    listenerRequest = object : RequestListener<Bitmap> {
      override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
        if (currentSelectedFeature == type) shareUriWithText(null, shareText)
        listenerRequest = null
        return false
      }

      override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
        if (currentSelectedFeature == type) {
          val uri = getImageUri(mContext, resource, "boost_${DateUtils.getCurrentDate().time}")
          shareUriWithText(uri, shareText)
        }
        listenerRequest = null
        return false
      }
    }
    if (imageUri.isNullOrEmpty().not() && florisBoard?.currentInputEditorInfo?.getImageSupport() == true) {
      Glide.with(mContext).asBitmap().load(imageUri ?: "").listener(listenerRequest).submit()
    } else shareUriWithText(null, shareText)
  }

  private fun shareUriWithText(uri: Uri?, shareText: String) {
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

  private fun commitImageWithText(contentUri: Uri, shareText: String, packageNames: Array<String>) {
    florisBoard?.let {
      val inputContentInfo = InputContentInfoCompat(contentUri, ClipDescription(shareText, arrayOf("image/png")), null)
      var flags = 1
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
        flags = flags or InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION
      } else {
        mContext.grantUriPermission(packageNames[0], contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }
      if (it.currentInputConnection != null) {
        it.currentInputConnection.commitText(shareText, 1)
      }
      InputConnectionCompat.commitContent(it.currentInputConnection, it.currentInputEditorInfo, inputContentInfo, flags, null)
    }
  }

  private fun multipleImageToUriListGet() {
    if (NetworkUtils.isNetworkConnected()) {
      if (florisBoard?.currentInputEditorInfo?.getImageSupport() == true) {
        val selectedImage = photosSet.filter { it.selected }
        if (selectedImage.isNotEmpty()) {
          var count = 0
          val imageUriArray = ArrayList<Uri>()
          listenerRequest = object : RequestListener<Bitmap> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
              count += 1
              if (selectedImage.size == count && currentSelectedFeature == BusinessFeatureEnum.PHOTOS) {
                doCommitContentMultiple(imageUriArray)
                binding.rvPhotoView.btnCancel.performClick()
              }
              listenerRequest = null
              return false
            }

            override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
              val uri = getImageUri(mContext, resource, "boost_${DateUtils.getCurrentDate().time}")
              uri?.let { imageUriArray.add(it) }
              count += 1
              if (selectedImage.size == count && currentSelectedFeature == BusinessFeatureEnum.PHOTOS) {
                doCommitContentMultiple(imageUriArray)
                binding.rvPhotoView.btnCancel.performClick()
              }
              listenerRequest = null
              return false
            }
          }
          selectedImage.forEach { Glide.with(mContext).asBitmap().load(it.imageUri ?: "").listener(listenerRequest).submit() }
        } else removeSelected()
      } else Toast.makeText(mContext, mContext.getString(R.string.image_not_suopported), Toast.LENGTH_SHORT).show()
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
    this.adapterPhoto.notifyNewList(photosSet.toList())
    updateLayout()
  }

  private fun updateLayout() {
    binding.rvPhotoView.btnImageShare.setOnClickListener { multipleImageToUriListGet() }
    binding.rvPhotoView.btnCancel.setOnClickListener { removeSelected() }
    visiblePhotoTopView()
  }

  private fun visiblePhotoTopView() {
    val selectedPhoto = photosSet.filter { it.selected }
    if (selectedPhoto.isNotEmpty()) {
      binding.rvPhotoView.tabPhotoView.gone()
      binding.rvPhotoView.changePhotoGridView.gone()
      binding.rvPhotoView.containerShareImage.visible()
      binding.rvPhotoView.btnImageShare.text = "Share ${selectedPhoto.size} ${if (selectedPhoto.size > 1) "images" else "image"}"
    } else {
      binding.rvPhotoView.containerShareImage.gone()
      binding.rvPhotoView.changePhotoGridView.visibility = if (tapPhotoSelect) View.GONE else View.VISIBLE
      binding.rvPhotoView.tabPhotoView.visibility = if (tapPhotoSelect) View.VISIBLE else View.GONE
    }
  }

  private fun removeSelected() {
    photosSet.forEach { it.selected = false }
    this.adapterPhoto.notifyNewList(photosSet.toList())
    updateLayout()
  }

  private fun initializePaging() {
    this.isFirstPage = true
    this.isLoadingD = false
    this.PAGING_TOTAL_ELEMENTS = 0
    this.offSet = PAGE_START
    this.limit = PAGE_SIZE
    this.isLastPageD = false
  }

  fun clearObservers() {
    viewModel.updates.removeObserver {}
    viewModel.details.removeObserver {}
    viewModel.photos.removeObserver {}
    viewModel.products.removeObserver {}
    viewModel.moreAction.removeObserver {}
  }

  private fun EditorInfo.getImageSupport(): Boolean {
    val mimeTypes: Array<String> = EditorInfoCompat.getContentMimeTypes(this)
    return mimeTypes.any { ClipDescription.compareMimeTypes(it, "image/*") }
  }

  fun getFilterRequest(offSet: Int, limit: Int): GetStaffListingRequest {
    return GetStaffListingRequest(FilterBy(offset = offSet, limit = limit), session?.fpTag, "")
  }

  private fun <T : BaseRecyclerItem?> SharedAdapter<T>.removeLoaderN() {
    if (isLoadingD) {
      isLoadingD = false
      this.removeLoader()
    }
  }

  fun getBindingRoot(): View {
    return binding.root
  }
}
