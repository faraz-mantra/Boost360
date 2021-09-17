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
import com.google.gson.Gson
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

// keyborad ImePresenterImpl

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
  private lateinit var mContext: Context
  private var florisBoard: FlorisBoard? = null
  private var session: UserSessionManager? = null
  private lateinit var currentSelectedFeature: BusinessFeatureEnum
  private var tagPosition: Int = 0

  private lateinit var adapterProductService: SharedAdapter<Product>
  private lateinit var adapterUpdates: SharedAdapter<FloatUpdate>
  private lateinit var adapterPhoto: SharedAdapter<Photo>
  private lateinit var adapterStaff: SharedAdapter<DataItem>
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
  private var TOTAL_ELEMENTS = 0
  private var offSet: Int = PAGE_START
  private var limit: Int = PAGE_SIZE
  private var isLastPageD = false

  private fun onRegisterInputView(inputView: InputView, florisBoard: FlorisBoard) {
    this.mContext = inputView.context
    this.session = UserSessionManager(this.mContext)
    this.florisBoard = florisBoard
    this.viewModel = BusinessFeaturesViewModel(mContext)
    this.gridType = Photo.ViewGridType.FOUR_GRID
    this.adapterProductService = SharedAdapter(arrayListOf(), this)
    this.adapterUpdates = SharedAdapter(arrayListOf(), this)
    this.adapterPhoto = SharedAdapter(arrayListOf(), this)
    this.adapterBusinessCard = SharedAdapter(arrayListOf(), this)
    this.adapterStaff = SharedAdapter(arrayListOf(), this)
    // initialize business features views
    this.binding = BusinessFeaturesLayoutBinding.bind(inputView.findViewById(R.id.business_features))

    binding.productShareRvList.also {
      it.adapter = this.adapterProductService
      (it.layoutManager as? LinearLayoutManager)?.let { it1 -> it.paginationListenerProduct(it1) }
    }
    binding.updateRvList.also {
      it.adapter = this.adapterUpdates
      (it.layoutManager as? LinearLayoutManager)?.let { it1 -> it.paginationListenerProduct(it1) }
    }

    binding.viewPagerProfile.also {
      it.adapter = this.adapterBusinessCard
      it.offscreenPageLimit = 3
      it.setPageTransformer { page, position -> OffsetPageTransformer().transformPage(page, position) }
    }

    binding.staffRvList.also {
      it.layoutManager = GridLayoutManager(mContext, 2, GridLayoutManager.VERTICAL, false)
      it.adapter = this.adapterStaff
      (it.layoutManager as? LinearLayoutManager)?.let { it1 -> it.paginationListenerProduct(it1, false) }
    }

    binding.rvListPhotos.also {
      it.layoutManager = GridLayoutManager(mContext, gridType.countGrid, GridLayoutManager.VERTICAL, false)
      it.adapter = this.adapterPhoto
    }
    getChannelAccessToken()
    apiObserveServiceProduct()
    apiObserveUpdates()
    apiObservePhotos()
    apiObserveStaff()
    apiObserveUserDetails()
    errorObserveListener()
    Log.i(TAG, "onRegisterInputView: ")
  }

  private fun RecyclerView.paginationListenerProduct(layoutManager: LinearLayoutManager, isPagerSnap: Boolean = true) {
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
            adapterProductService.addLoadingFooter(Product().getLoaderItem())
            viewModel.getProducts(session?.fpTag, clientId, offSet, "SINGLE")
          } else if (currentSelectedFeature == BusinessFeatureEnum.STAFF) {
            adapterStaff.addLoadingFooter(DataItem().getLoaderItem())
            viewModel.getStaffList(getFilterRequest(offSet, limit))
          }
        }
      }

      override val totalPageCount: Int
        get() = TOTAL_ELEMENTS
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
    Log.i(TAG, "showSelectedBusinessFeature: ")
    val lastSyncTime = sharedPref.lastSyncTime
    if (session?.isUserLoggedIn == false) {
      updateUiNotLoginned()
    } else if (lastSyncTime == null || MethodUtils.getDaysDiff(System.currentTimeMillis(), lastSyncTime) >= 1) {
      Log.i(TAG, "last sync is greater than 24 hour: " + lastSyncTime)
      viewModel.getDetails(session?.fpTag, clientId)
    } else {
      loadDataBasesOnTab()
    }
  }

  private fun loadDataBasesOnTab() {
    resetAdapters()
    binding.msgLayout.gone()
    if (session?.getStoreWidgets()?.contains("BOOSTKEYBOARD") == true) {
      binding.businessFeatureProgress.visible()
      when (businessFeatureEnum) {
        BusinessFeatureEnum.INVENTORY_SERVICE -> {
          SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(tagPosition)?.text = getProductType(session?.fP_AppExperienceCode ?: "")
          visibleSelectType(isI = true)
          initializePaging()
          this.adapterProductService.clearList()
          binding.productShareRvList.removeAllViewsInLayout()
          viewModel.getProducts(session?.fpTag, clientId, offSet, "SINGLE")
        }
        BusinessFeatureEnum.UPDATES -> {
          visibleSelectType(isII = true)
          initializePaging()
          this.adapterUpdates.clearList()
          binding.updateRvList.removeAllViewsInLayout()
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
          this.adapterBusinessCard.clearList()
          if (messageBusiness.isEmpty() && _connectedChannels.isEmpty()) getChannelAccessToken(true)
          businessCardDataLoad()
        }
        BusinessFeatureEnum.STAFF -> {
          visibleSelectType(isV = true)
          if (session?.getStoreWidgets()?.contains("STAFFPROFILE") == true) {
            initializePaging()
            this.adapterStaff.clearList()
            binding.staffRvList.removeAllViewsInLayout()
            viewModel.getStaffList(getFilterRequest(offSet, limit))
          } else {
            binding.businessFeatureProgress.gone()
            updateUiStaffNotRenewd()
          }
        }
        else -> {

        }
      }

    } else {
      Timber.i("Please add boost keyboard in your current plan.")
      updateUiFeatureNotRenewed()
    }
  }

  private fun resetAdapters() {
    adapterBusinessCard.clearList()
    adapterPhoto.clearList()
    adapterProductService.clearList()
    adapterStaff.clearList()
    adapterUpdates.clearList()
    SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(1)?.text = "${getProductType(session?.fP_AppExperienceCode ?: "")}"
    SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(2)?.text = BusinessFeatureEnum.UPDATES.name
    SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(3)?.text = BusinessFeatureEnum.PHOTOS.name
    SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(4)?.text = BusinessFeatureEnum.STAFF.name
    SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(4)?.view?.apply {
      visibility = if (isStaffVisible(session?.fP_AppExperienceCode ?: "")) View.VISIBLE else View.GONE
    }
  }


  private fun updateUiErrorFetchingInformation() {
    binding.msgLayout.visible()
    binding.msgIcon.setImageResource(R.drawable.ic_linkbreak)
    binding.msgTitle.text = mContext.getString(R.string.error_while_fetching_your_business)
    binding.msgDesc.text = mContext.getString(R.string.please_click_on_retry_btn_below)
    binding.msgBtn.text = mContext.getString(R.string.retry)
    binding.msgBtn.icon = mContext.getDrawable(R.drawable.ic_arrowscounterclockwise)
    binding.msgBtn.setOnClickListener {
      loadDataBasesOnTab()
    }
  }

  private fun updateUiInternetNotAvailable() {
    binding.msgLayout.visible()

    binding.msgIcon.setImageResource(R.drawable.ic_wifislash)
    binding.msgTitle.text = mContext.getString(R.string.internet_not_available)
    binding.msgDesc.text = mContext.getString(R.string.a_wifi_or_cellular)
    binding.msgBtn.text = mContext.getString(R.string.open_settings)
    binding.msgBtn.icon = mContext.getDrawable(R.drawable.ic_settings_white)


    binding.msgBtn.setOnClickListener {
      try {
        mContext.startActivity(Intent(Settings.ACTION_DATA_ROAMING_SETTINGS).apply {
          flags = Intent.FLAG_ACTIVITY_NEW_TASK;
        })
      } catch (e: Exception) {
        Toast.makeText(mContext, "Unable to find network settings. Please do it manually from phone's settings", Toast.LENGTH_LONG).show()
        Log.e(TAG, "updateUiInternetNotAvailable: " + e.localizedMessage)
      }
    }
  }

  private fun updateUiNotLoginned() {
    binding.msgLayout.visible()

    binding.msgIcon.setImageResource(R.drawable.ic_storefront)
    binding.msgTitle.text = mContext.getString(R.string.login_to_start_sharing)
    binding.msgDesc.text = mContext.getString(R.string.with_business_keyboard)
    binding.msgBtn.text = mContext.getString(R.string.login_to_boost)
    binding.msgBtn.icon = mContext.getDrawable(R.drawable.ic_key)

    binding.msgBtn.setOnClickListener {
      MethodUtils.startBoostActivity(mContext)
    }
  }

  private fun updateUiFeatureNotRenewed() {
    binding.msgLayout.visible()

    binding.msgIcon.setImageResource(R.drawable.ic_keyboard_renew)
    binding.msgTitle.text = mContext.getString(R.string.feature_not_renewed)
    binding.msgDesc.text = mContext.getString(R.string.with_business_keyboard)
    binding.msgBtn.text = mContext.getString(R.string.renew_feature)
    binding.msgBtn.icon = mContext.getDrawable(R.drawable.ic_lockkey)

    binding.msgBtn.setOnClickListener {
      MethodUtils.startKeyboardActivity(mContext)
    }
  }

  private fun updateUiStaffNotRenewd() {
    binding.msgLayout.visible()
    binding.msgIcon.setImageResource(R.drawable.ic_keyboard_renew)
    binding.msgTitle.text = mContext.getString(R.string.staff_not_added_plan)
    binding.msgDesc.text = mContext.getString(R.string.with_business_keyboard)
    binding.msgBtn.text = mContext.getString(R.string.renew_feature)
    binding.msgBtn.icon = mContext.getDrawable(R.drawable.ic_lockkey)

    binding.msgBtn.setOnClickListener {
      MethodUtils.startStaffActivity(mContext)

    }
  }

  private fun visibleSelectType(isI: Boolean = false, isII: Boolean = false, isIII: Boolean = false, isIV: Boolean = false, isV: Boolean = false) {
    binding.productShareRvList.visibility = if (isI) View.VISIBLE else View.GONE
    binding.updateRvList.visibility = if (isII) View.VISIBLE else View.GONE
    binding.clSelectionLayout.visibility = if (isIII) View.VISIBLE else View.GONE
    binding.rvListPhotos.visibility = if (isIII) View.VISIBLE else View.GONE
    binding.businessCardView.visibility = if (isIV) View.VISIBLE else View.GONE
    binding.staffRvList.visibility = if (isV) View.VISIBLE else View.GONE
  }

  private fun errorObserveListener() {
    viewModel.error.observeForever {
      Log.e(TAG, "errorObserveListener: " + it)
      binding.businessFeatureProgress.gone()
      if (it == Constants.TOKEN_EXPIRED_MESSAGE) {
        updateUiNotLoginned()
      } else if (it.contains("failed to connect", ignoreCase = true) || it.contains("Unable to resolve host", ignoreCase = true)) {
        updateUiInternetNotAvailable()
      } else {
        updateUiErrorFetchingInformation()
      }
    }
  }

  private fun apiObserveUserDetails() {
    viewModel.details.observeForever {
      Log.i(TAG, "apiObserveUserDetails: " + Gson().toJson(it.FPWebWidgets))
      it.FPWebWidgets?.let { list ->
        session?.storeFPDetails(Key_Preferences.STORE_WIDGETS, convertListObjToString(list))
        SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).lastSyncTime = System.currentTimeMillis()
        loadDataBasesOnTab()
      }
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
        binding.rvListPhotos.layoutManager = GridLayoutManager(mContext, gridType.countGrid, GridLayoutManager.VERTICAL, false)
        this.adapterPhoto.notifyNewList(this.photosSet.toList())
        SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(3)?.text = BusinessFeatureEnum.PHOTOS.name + " (${photosSet.size})"
      } else {
        if (businessFeatureEnum == BusinessFeatureEnum.INVENTORY_SERVICE) {
          this.adapterPhoto.notifyNewList(arrayListOf())
          //  Toast.makeText(mContext, "List from api came empty", Toast.LENGTH_SHORT).show()
        }
      }
      clickListenerPhoto()
    }
  }

  private fun apiObserveServiceProduct() {
    viewModel.products.observeForever {
      Log.i(TAG, "apiObserveServiceProduct: observer")
      Timber.i("products - $it.")
      binding.businessFeatureProgress.gone()
      this.adapterProductService.removeLoaderN()
      if (it.isNullOrEmpty().not()) {
        if (isFirstPage) this.adapterProductService.notifyNewList(it)
        else this.adapterProductService.addItems(it)
        TOTAL_ELEMENTS = this.adapterProductService.getListData().size
        SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(1)?.text = "${getProductType(session?.fP_AppExperienceCode ?: "")} (${this.adapterProductService.getListData().size})"
      } else {
        if (businessFeatureEnum == BusinessFeatureEnum.INVENTORY_SERVICE) {
          this.adapterProductService.notifyNewList(arrayListOf())
          //  Toast.makeText(mContext, "List from api came empty", Toast.LENGTH_SHORT).show()
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
        TOTAL_ELEMENTS = it.paging?.count ?: 0
        if (isFirstPage) this.adapterStaff.notifyNewList(it.data!!)
        else this.adapterStaff.addItems(it.data!!)
        isLastPageD = (this.adapterStaff.getListData().size == TOTAL_ELEMENTS)
        SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(4)?.text = BusinessFeatureEnum.STAFF.name + " (${it.paging?.count})"
      } else {
        if (businessFeatureEnum == BusinessFeatureEnum.STAFF) {
          this.adapterStaff.notifyNewList(arrayListOf())
          //  Toast.makeText(mContext, "List from api came empty", Toast.LENGTH_SHORT).show()
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
        TOTAL_ELEMENTS = it.totalCount ?: 0
        if (isFirstPage) this.adapterUpdates.notifyNewList(it.floats!!)
        else this.adapterUpdates.addItems(it.floats!!)
        isLastPageD = (this.adapterUpdates.getListData().size == TOTAL_ELEMENTS)
        SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(2)?.text = BusinessFeatureEnum.UPDATES.name + " (${it.totalCount})"
      } else {
        if (businessFeatureEnum == BusinessFeatureEnum.UPDATES) {
          this.adapterUpdates.notifyNewList(arrayListOf())
          //  Toast.makeText(mContext, "List from api came empty", Toast.LENGTH_SHORT).show()
        }
      }
    }
  }

  private fun <T : BaseRecyclerItem?> SharedAdapter<T>.removeLoaderN() {
    if (isLoadingD) {
      isLoadingD = false
      this.removeLoader()
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
      binding.btnShareImageBusiness.setOnClickListener { shareImageTextBusiness() }
      binding.btnShareImageTextBusiness.setOnClickListener {
        if (finalShareMessage.isNotEmpty()) shareImageTextBusiness(finalShareMessage) else getVisitingMessageData(true)
      }
    }
  }

  private fun clickListenerPhoto() {
    binding.btnSelectGrid.setOnClickListener {
      tapPhotoSelect = tapPhotoSelect.not()
      visiblePhotoTopView()
    }
    binding.btnChangeGridLayout.setOnClickListener {
      tapPhotoSelect = tapPhotoSelect.not()
      visiblePhotoTopView()
    }
    binding.btnShare.setOnClickListener { }
    binding.photoGridOne.setOnClickListener { adapterNotifyGrid(Photo.ViewGridType.FIRST_GRID) }
    binding.photoGridTwo.setOnClickListener { adapterNotifyGrid(Photo.ViewGridType.SECOND_GRID) }
    binding.photoGridThree.setOnClickListener { adapterNotifyGrid(Photo.ViewGridType.THIRD_GRID) }
    binding.photoGridFour.setOnClickListener { adapterNotifyGrid(Photo.ViewGridType.FOUR_GRID) }
    visiblePhotoTopView()
  }

  private fun adapterNotifyGrid(grid: Photo.ViewGridType) {
    this.gridType = grid
    val bacRound = ContextCompat.getDrawable(mContext, R.drawable.ic_mask_bac_f)
    binding.photoGridOne.apply { setGridImageIcon((gridType == Photo.ViewGridType.FIRST_GRID), bacRound) }
    binding.photoGridTwo.apply { setGridImageIcon(gridType == Photo.ViewGridType.SECOND_GRID, bacRound) }
    binding.photoGridThree.apply { setGridImageIcon(gridType == Photo.ViewGridType.THIRD_GRID, bacRound) }
    binding.photoGridFour.apply { setGridImageIcon(gridType == Photo.ViewGridType.FOUR_GRID, bacRound) }
    binding.imgGridImage.setImageResource(this.gridType.icon)
    this.photosSet.map { it.gridType = this.gridType }
    binding.rvListPhotos.layoutManager = GridLayoutManager(mContext, gridType.countGrid, GridLayoutManager.VERTICAL, false)
    this.adapterPhoto.notifyDataSetChanged()
  }

  private fun CustomImageView.setGridImageIcon(isSelect: Boolean, bacRound: Drawable?) {
    alpha = if (isSelect) 1.0F else 0.5F
    background = if (isSelect) bacRound else null
  }


  private fun shareImageTextBusiness(shareText: String = "") {
    val bitmap = binding.viewPagerProfile.getChildAt(0)?.let { viewToBitmap(it) }
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

  override fun onItemClick(pos: Int, item: BaseRecyclerItem) {
    when (currentSelectedFeature) {
      BusinessFeatureEnum.UPDATES -> shareUpdates(item)
      BusinessFeatureEnum.INVENTORY_SERVICE -> onClickedShareInventory(item)
      BusinessFeatureEnum.PHOTOS -> onPhotoSelected(item)
      BusinessFeatureEnum.STAFF -> shareStaff(item)
      BusinessFeatureEnum.BUSINESS_CARD -> Timber.i("pos - $pos item = $item")
      else -> {
      }
    }
  }


  private fun shareStaff(item: BaseRecyclerItem) {
    val staff = item as? DataItem
    if (NetworkUtils.isNetworkConnected()) {
      val shareText = String.format(
        "\uD83D\uDC81 *%s*%s\n\n%s",
        staff?.name,
        "\n${if (staff?.description.isNullOrEmpty()) "" else "_" + staff?.description + "_"}",
        if (staff?.specialData().isNullOrEmpty()) "" else "*Specialization*:\n_${staff?.specialData()}_"
      )
      pathToUriGet(staff?.image, shareText, BusinessFeatureEnum.STAFF)
    } else Toast.makeText(mContext, mContext.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
  }

  private fun shareUpdates(item: BaseRecyclerItem) {
    val float = item as? FloatUpdate
    if (NetworkUtils.isNetworkConnected()) {
      val shareText = String.format("*%s*", float?.message)
      pathToUriGet(float?.imageUri, shareText, BusinessFeatureEnum.UPDATES)
    } else Toast.makeText(mContext, mContext.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
  }

  private fun onClickedShareInventory(item: BaseRecyclerItem) {
    Log.i(TAG, "onClickedShareInventory: ")
    val product = item as? Product
    if (NetworkUtils.isNetworkConnected()) {
      val shareText = String.format("*%s*\n*%s* %s\n\n-------------\n%s\n", product?.name?.trim { it <= ' ' },
        "${product?.getProductDiscountedPriceOrPrice()}", "${if (product?.discountAmount?.toDoubleOrNull() ?: 0.0 != 0.0) "~${product?.getProductPrice()}~" else ""}", product?.description?.trim { it <= ' ' })
      pathToUriGet(product?.imageUri, shareText, BusinessFeatureEnum.INVENTORY_SERVICE)
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

  fun commitImageWithText(contentUri: Uri, shareText: String, packageNames: Array<String>) {
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
      Log.i(TAG, "commitImageWithText: ")
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
                binding.btnCancel.performClick()
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
                binding.btnCancel.performClick()
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
    binding.btnImageShare.setOnClickListener { multipleImageToUriListGet() }
    binding.btnCancel.setOnClickListener { removeSelected() }
    visiblePhotoTopView()
  }

  private fun visiblePhotoTopView() {
    val selectedPhoto = photosSet.filter { it.selected }
    if (selectedPhoto.isNotEmpty()) {
      binding.tabPhotoView.gone()
      binding.changePhotoGridView.gone()
      binding.containerShareImage.visible()
      binding.btnImageShare.text = "Share ${selectedPhoto.size} ${if (selectedPhoto.size > 1) "images" else "image"}"
    } else {
      binding.containerShareImage.gone()
      binding.changePhotoGridView.visibility = if (tapPhotoSelect) View.GONE else View.VISIBLE
      binding.tabPhotoView.visibility = if (tapPhotoSelect) View.VISIBLE else View.GONE
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
    this.TOTAL_ELEMENTS = 0
    this.offSet = PAGE_START
    this.limit = PAGE_SIZE + 1
    this.isLastPageD = false
  }

  fun clearObservers() {
    viewModel.updates.removeObserver {}
    viewModel.details.removeObserver {}
    viewModel.photos.removeObserver {}
    viewModel.products.removeObserver {}
  }

  fun EditorInfo.getImageSupport(): Boolean {
    val mimeTypes: Array<String> = EditorInfoCompat.getContentMimeTypes(this)
    return mimeTypes.any { ClipDescription.compareMimeTypes(it, "image/*") }
  }

  fun getFilterRequest(offSet: Int, limit: Int): GetStaffListingRequest {
    return GetStaffListingRequest(FilterBy(offset = offSet, limit = limit), session?.fpTag, "")
  }

  fun getBindingRoot(): View {
    return binding.root
  }


}
