package dev.patrickgold.florisboard.customization

import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
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
import dev.patrickgold.florisboard.customization.util.*
import dev.patrickgold.florisboard.customization.util.MethodUtils.getImageUri
import dev.patrickgold.florisboard.customization.util.PaginationScrollListener.Companion.PAGE_SIZE
import dev.patrickgold.florisboard.customization.util.PaginationScrollListener.Companion.PAGE_START
import dev.patrickgold.florisboard.databinding.BusinessFeaturesLayoutBinding
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

  private lateinit var binding: BusinessFeaturesLayoutBinding
  private lateinit var viewModel: BusinessFeaturesViewModel

  private val photosSet = mutableSetOf<Photo>()
  private lateinit var mContext: Context
  private var florisBoard: FlorisBoard? = null
  private var session: UserSessionManager? = null
  private lateinit var currentSelectedFeature: BusinessFeatureEnum

  private lateinit var adapterProductService: SharedAdapter<Product>
  private lateinit var adapterUpdates: SharedAdapter<FloatUpdate>
  private lateinit var adapterPhoto: SharedAdapter<Photo>
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
    this.viewModel = BusinessFeaturesViewModel()
    this.gridType = Photo.ViewGridType.FOUR_GRID
    this.adapterProductService = SharedAdapter(arrayListOf(), this)
    this.adapterUpdates = SharedAdapter(arrayListOf(), this)
    this.adapterPhoto = SharedAdapter(arrayListOf(), this)
    this.adapterBusinessCard = SharedAdapter(arrayListOf(), this)
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
    binding.rvListPhotos.also {
      it.layoutManager = GridLayoutManager(mContext, gridType.countGrid, GridLayoutManager.VERTICAL, false)
      it.adapter = this.adapterPhoto
    }
    getChannelAccessToken()
    apiObserveServiceProduct()
    apiObserveUpdates()
    apiObservePhotos()
  }

  private fun RecyclerView.paginationListenerProduct(layoutManager: LinearLayoutManager) {
    val listenerProduct = object : PaginationScrollListener(layoutManager) {
      override fun loadMoreItems() {
        if (!isLastPageD) {
          isFirstPage = false
          isLoadingD = true
          offSet += limit
          if (this@BusinessFeaturesManager::currentSelectedFeature.isInitialized && currentSelectedFeature == BusinessFeatureEnum.UPDATES) {
            adapterUpdates.addLoadingFooter(FloatUpdate().getLoaderItem())
            viewModel.getUpdates(SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).fpId, mContext.getString(R.string.client_id), offSet, limit)
          } else {
            adapterProductService.addLoadingFooter(Product().getLoaderItem())
            viewModel.getProducts(SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).fpTag, mContext.getString(R.string.client_id), offSet, "SINGLE")
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
    PagerSnapHelper().attachToRecyclerView(this)
    addOnScrollListener(listenerProduct)
  }

  fun showSelectedBusinessFeature(businessFeatureEnum: BusinessFeatureEnum) {
    this.currentSelectedFeature = businessFeatureEnum
    this.listenerRequest = null
    if (!SharedPrefUtil.fromBoostPref().getsBoostPref(mContext).isLoggedIn) {
      Timber.i("Please do login")
      binding.pleaseLoginCard.visible()
      binding.pleaseLoginCard.setOnClickListener { MethodUtils.startBoostActivity(mContext) }
    } else if (MethodUtils.isOnline(mContext)) {
      binding.pleaseLoginCard.gone()
      binding.businessFeatureProgress.visible()
      errorObserveListener()
      when (businessFeatureEnum) {
        BusinessFeatureEnum.INVENTORY_SERVICE -> {
          SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(1)?.text = getProductType(session?.fP_AppExperienceCode ?: "")
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
        else -> {
        }
      }
    } else {
      Toast.makeText(mContext, mContext.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
      binding.businessFeatureProgress.gone()
    }
  }

  private fun visibleSelectType(isI: Boolean = false, isII: Boolean = false, isIII: Boolean = false, isIV: Boolean = false) {
    binding.productShareRvList.visibility = if (isI) View.VISIBLE else View.GONE
    binding.updateRvList.visibility = if (isII) View.VISIBLE else View.GONE
    binding.clSelectionLayout.visibility = if (isIII) View.VISIBLE else View.GONE
    binding.rvListPhotos.visibility = if (isIII) View.VISIBLE else View.GONE
    binding.businessCardView.visibility = if (isIV) View.VISIBLE else View.GONE
  }

  private fun errorObserveListener() {
    viewModel.error.observeForever {
      binding.businessFeatureProgress.gone()
      Toast.makeText(mContext, it, Toast.LENGTH_SHORT).show()
    }
  }

  private fun apiObservePhotos() {
    viewModel.photos.observeForever {
      Timber.e("photos - $it.")
      binding.businessFeatureProgress.gone()
      if (it.isNotEmpty()) {
        this.photosSet.addAll(it)
        this.adapterPhoto.notifyNewList(this.photosSet.toList())
        SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(3)?.text = "PHOTOS (${photosSet.size})"
      } else Timber.i("List from api came empty")
      clickListenerPhoto()
    }
  }

  private fun apiObserveServiceProduct() {
    viewModel.products.observeForever {
      Timber.i("products - $it.")
      binding.businessFeatureProgress.gone()
      this.adapterProductService.removeLoaderN()
      if (it.isNullOrEmpty().not()) {
        if (isFirstPage) this.adapterProductService.notifyNewList(it)
        else this.adapterProductService.addItems(it)
        TOTAL_ELEMENTS = this.adapterProductService.getListData().size
        SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(1)?.text = "${getProductType(session?.fP_AppExperienceCode ?: "")} (${this.adapterProductService.getListData().size})"
      } else Timber.i("List from api came empty")
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
        SmartbarView.getSmartViewBinding().businessFeatureTabLayout.getTabAt(2)?.text = "UPDATES (${it.totalCount})"
      } else Timber.i("List from api came empty")
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
          if (lat != null && long != null) location = "${if (messageBusiness.isNotEmpty()) "\n\n" else ""}\uD83D\uDCCD *Find us on map: http://www.google.com/maps/place/$lat,$long*\n\n"
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
    handleListItemClick(currentSelectedFeature, pos, item)
  }

  private fun handleListItemClick(businessFeatureEnum: BusinessFeatureEnum, pos: Int, item: BaseRecyclerItem) {
    when (businessFeatureEnum) {
      BusinessFeatureEnum.UPDATES -> {
        shareUpdates(item)
      }
      BusinessFeatureEnum.INVENTORY_SERVICE -> {
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

  private fun shareUpdates(item: BaseRecyclerItem) {
    val float = item as? FloatUpdate
    if (NetworkUtils.isNetworkConnected()) {
      val shareText = String.format("*%s*", float?.message)
      pathToUriGet(float?.imageUri, shareText, BusinessFeatureEnum.UPDATES)
    } else Toast.makeText(mContext, mContext.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show()
  }

  private fun onClickedShareInventory(item: BaseRecyclerItem) {
    val product = item as? Product
    if (NetworkUtils.isNetworkConnected()) {
      val shareText = String.format("*%s* %s\n*%s* %s\n\n-------------\n%s\n", product?.name?.trim { it <= ' ' }, product?.description,
        "${product?.getCurrencySymbol()} ${product?.discountAmount}", "${product?.getCurrencySymbol()} ${product?.price}", product?.description?.trim { it <= ' ' })
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
      if (it.currentInputConnection != null) it.currentInputConnection.commitText(shareText, 1)
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
    if (photosSet.any { it.selected }) {
      binding.tabPhotoView.gone()
      binding.changePhotoGridView.gone()
      binding.containerShareImage.visible()
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

  fun getBindingRoot(): View {
    return binding.root
  }
}
