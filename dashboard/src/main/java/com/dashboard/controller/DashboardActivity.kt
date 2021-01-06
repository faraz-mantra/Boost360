package com.dashboard.controller

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavArgument
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.anachat.chatsdk.AnaCore
import com.appservice.ui.catlogService.widgets.ClickType
import com.appservice.ui.catlogService.widgets.ImagePickerBottomSheet
import com.dashboard.R
import com.dashboard.base.AppBaseActivity
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.controller.ui.dashboard.DashboardFragment
import com.dashboard.databinding.ActivityDashboardBinding
import com.dashboard.model.live.drawerData.DrawerHomeData
import com.dashboard.model.live.drawerData.DrawerHomeDataResponse
import com.dashboard.pref.*
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
import com.dashboard.utils.*
import com.dashboard.viewmodel.DashboardViewModel
import com.framework.extensions.observeOnce
import com.framework.glide.util.glideLoad
import com.framework.imagepicker.ImagePicker
import com.framework.utils.AppsFlyerUtils
import com.framework.utils.fromHtml
import com.framework.views.bottombar.OnItemSelectedListener
import com.framework.views.customViews.CustomToolbar
import com.google.firebase.iid.FirebaseInstanceId
import com.inventoryorder.utils.DynamicLinkParams
import com.inventoryorder.utils.DynamicLinksManager
import com.onboarding.nowfloats.model.uploadfile.UploadFileBusinessRequest
import com.webengage.sdk.android.WebEngage
import com.zopim.android.sdk.api.ZopimChat
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import zendesk.core.AnonymousIdentity
import zendesk.core.Zendesk
import zendesk.support.Support
import java.io.File
import java.util.*

class DashboardActivity : AppBaseActivity<ActivityDashboardBinding, DashboardViewModel>(), OnItemSelectedListener, RecyclerItemClickListener {

  private var mDeepLinkUrl: String? = null;
  private var mPayload: String? = null
  private var deepLinkUtil: DeepLinkUtil? = null
  private lateinit var mNavController: NavController
  private var session: UserSessionManager? = null
  private var adapterDrawer: AppBaseRecyclerViewAdapter<DrawerHomeData>? = null
  private var isSecondaryImage = false
  private val navHostFragment: NavHostFragment?
    get() {
      return supportFragmentManager.fragments.first() as? NavHostFragment
    }

  private val childFragments: List<Fragment>?
    get() {
      return navHostFragment?.childFragmentManager?.fragments
    }

  override fun getLayout(): Int {
    return R.layout.activity_dashboard
  }

  override fun onCreateView() {
    super.onCreateView()
    session = UserSessionManager(this)
    session?.let { deepLinkUtil = DeepLinkUtil(this, it) }
    mNavController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
    val graph = mNavController.graph
    graph.addArgument("data", NavArgument.Builder().setDefaultValue("data").build())
    mNavController.graph = graph
    navControllerListener()
    binding?.navView?.setOnItemSelectedListener(this)
    binding?.navView?.setActiveItem(0)
    toolbarPropertySet(0)
    setDrawerHome()
    val versionName: String = packageManager.getPackageInfo(packageName, 0).versionName
    binding?.drawerView?.txtVersion?.text = "Version $versionName"
    intentDataCheckAndDeepLink()
    initialize()
  }

  private fun initialize() {
    val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
    StrictMode.setThreadPolicy(policy)
    WebEngageController.initiateUserLogin(session?.userProfileId)
    WebEngageController.setUserContactAttributes(session?.userProfileEmail, session?.userPrimaryMobile, session?.userProfileName, session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME))
    WebEngageController.setFPTag(session?.fpTag)
    WebEngageController.trackEvent("DASHBOARD HOME", "pageview", session?.fpTag ?: "")
    FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
      val token = instanceIdResult.token
      WebEngage.get().setRegistrationID(token)
    }
    initialiseZendeskSupportSdk()
    if (FirebaseInstanceId.getInstance().token != null) {
      AnaCore.saveFcmToken(this, FirebaseInstanceId.getInstance().token ?: "")
      AnaCore.registerUser(this, session?.fpTag ?: "", ANA_BUSINESS_ID, ANA_CHAT_API_URL)
    }
    //checkCustomerAssistantService()
  }

  private fun intentDataCheckAndDeepLink() {
    if (intent.extras != null) {
      if (intent.extras!!.containsKey("url")) mDeepLinkUrl = intent.extras!!.getString("url")
      if (intent.extras!!.containsKey("payload")) mPayload = intent.extras!!.getString("payload")
    }
    if (intent != null && intent.data != null) {
      val action = intent.action
      val data = intent.dataString
      val uri = intent.data
      Log.d("Data: ", "$data  $action")
      if (session?.isLoginCheck == true) {
        //Appsflyer Deep Link...
        if (uri != null && uri.toString().contains("onelink", true)) {
          if (AppsFlyerUtils.sAttributionData.containsKey(DynamicLinkParams.viewType.name)) {
            val viewType = AppsFlyerUtils.sAttributionData[DynamicLinkParams.viewType.name] ?: ""
            val buyItemKey = AppsFlyerUtils.sAttributionData[DynamicLinkParams.buyItemKey.name] ?: ""

            if (deepLinkUtil != null) deepLinkUtil?.deepLinkPage(viewType ?: "", buyItemKey ?: "", false)
          }
        } else {
          //Default Deep Link..
          val deepHashMap: HashMap<DynamicLinkParams, String> = DynamicLinksManager().getURILinkParams(uri)
          if (deepHashMap.containsKey(DynamicLinkParams.viewType)) {
            val viewType = deepHashMap[DynamicLinkParams.viewType]
            val buyItemKey = deepHashMap[DynamicLinkParams.buyItemKey]
            if (deepLinkUtil != null) deepLinkUtil?.deepLinkPage(viewType ?: "", buyItemKey ?: "", false)
          } else deepLinkUtil?.deepLinkPage(data?.substring(data?.lastIndexOf("/") + 1) ?: "", "", false)
        }
      } else this.startPreSignUp(session)
    } else {
      if (deepLinkUtil != null) deepLinkUtil?.deepLinkPage(mDeepLinkUrl ?: "", "", false)
    }
  }


  override fun onResume() {
    super.onResume()
    setUserData()
    setOnClickListener(binding?.drawerView?.btnSiteMeter, binding?.drawerView?.imgBusinessLogo, binding?.drawerView?.backgroundImage, binding?.drawerView?.txtDomainName)
  }

  override fun getToolbar(): CustomToolbar? {
    return binding?.toolbar
  }



  fun setPercentageData(score: Int) {
    val isHigh = (score >= 80)
    binding?.drawerView?.txtPercentage?.text = "$score%"
    binding?.drawerView?.progressBar?.progress = score
    binding?.drawerView?.txtSiteHelth?.setTextColor(ContextCompat.getColor(this, if (isHigh) R.color.light_green_3 else R.color.accent_dark))
    binding?.drawerView?.progressBar?.progressDrawable = ContextCompat.getDrawable(this, if (isHigh) R.drawable.ic_progress_bar_horizontal_high else R.drawable.progress_bar_horizontal)
  }

  private fun setUserData() {
    binding?.drawerView?.txtBusinessName?.text = session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME)
    binding?.drawerView?.txtDomainName?.text = fromHtml("<u>${session!!.getDomainName(false)}</u>")
    setPercentageData(session?.siteMeterCalculation() ?: 0)
    var imageUri = session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_IMAGE_URI)
    if (imageUri.isNullOrEmpty().not() && imageUri!!.contains("http").not()) {
      imageUri = BASE_IMAGE_URL + imageUri
    }
    binding?.drawerView?.imgBusinessLogo?.let { glideLoad(it, imageUri ?: "", R.drawable.business_edit_profile_icon_d) }
    var bgImageUri = session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_BG_IMAGE)
    if (bgImageUri.isNullOrEmpty().not() && bgImageUri!!.contains("http").not()) {
      bgImageUri = BASE_IMAGE_URL + bgImageUri
    }
    binding?.drawerView?.bgImage?.let { glideLoad(it, bgImageUri ?: "", R.drawable.general_services_background_img_d) }
  }

  private fun setDrawerHome() {
    viewModel.getNavDashboardData(this).observeOnce(this, {
      val response = it as? DrawerHomeDataResponse
      if (response?.isSuccess() == true && response.data.isNullOrEmpty().not()) {
        binding?.drawerView?.rvLeftDrawer?.apply {
          adapterDrawer = AppBaseRecyclerViewAdapter(this@DashboardActivity, checkLockData(response.data!!), this@DashboardActivity)
          adapter = adapterDrawer
        }
      } else showShortToast(this.getString(R.string.navigation_data_error))
    })
  }

  private fun checkLockData(data: ArrayList<DrawerHomeData>): ArrayList<DrawerHomeData> {
    data.forEach { it1 ->
      when (it1.navType) {
        DrawerHomeData.NavType.NAV_ORDER_APT_BOOKING.name -> {
          it1.title = getDefaultTrasactionsTaxonomyFromServiceCode(session?.fP_AppExperienceCode)
        }
        DrawerHomeData.NavType.NAV_CALLS.name -> {
          it1.isLockShow = (session?.getStoreWidgets()?.firstOrNull { it == PremiumCode.CALLTRACKER.value } == null)
        }
        DrawerHomeData.NavType.NAV_BOOST_KEYBOARD.name -> {
          it1.isLockShow = (session?.getStoreWidgets()?.firstOrNull { it == PremiumCode.BOOSTKEYBOARD.value } == null)
        }
      }
    }
    return data
  }

  private fun navControllerListener() {
    mNavController.addOnDestinationChangedListener { _, destination, _ -> print("" + destination) }
  }

  override fun getViewModelClass(): Class<DashboardViewModel> {
    return DashboardViewModel::class.java
  }

  override fun onItemSelect(pos: Int) {
    when (pos) {
      0 -> mNavController.navigate(R.id.navigation_dashboard, Bundle(), getNavOptions())
      1 -> mNavController.navigate(R.id.navigation_website, Bundle(), getNavOptions())
      2 -> mNavController.navigate(R.id.navigation_customer, Bundle(), getNavOptions())
      else -> mNavController.navigate(R.id.navigation_dashboard, Bundle(), getNavOptions())
    }
    toolbarPropertySet(pos)
  }

  private fun toolbarPropertySet(pos: Int) {
    when (pos) {
      1 -> showToolbar(getString(R.string.website))
      2 -> showToolbar((if (session?.fP_AppExperienceCode == "DOC" || session?.fP_AppExperienceCode == "HOS") getString(R.string.patient) else getString(R.string.customer)).plus(" Interaction"))
      else -> getToolbar()?.apply { visibility = View.GONE }
    }
  }

  private fun showToolbar(title: String) {
    getToolbar()?.apply {
      visibility = View.VISIBLE
      setTitle(title)
      setBackgroundColor(ContextCompat.getColor(this@DashboardActivity, R.color.colorPrimary))
      supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }
  }

  override fun onItemClick(pos: Int) {
    super.onItemClick(pos)
    when (pos) {
      3 -> session?.let { this.initiateAddonMarketplace(it, false, "", "") }
      4 -> binding?.drawerLayout?.openDrawer(GravityCompat.END, true)
    }
  }


  private fun getNavOptions(): NavOptions? {
    return NavOptions.Builder().setLaunchSingleTop(true).build()
  }

  private fun openDashboard() {
    binding?.navView?.setActiveItem(0)
    mNavController.navigate(R.id.navigation_dashboard, Bundle(), getNavOptions())
    toolbarPropertySet(0)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK && isSecondaryImage) {
      val mPaths = data?.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH) as ArrayList<String>
      if (mPaths.isNullOrEmpty().not()) uploadSecondaryImage(mPaths[0])
    } else childFragments?.forEach { fragment -> fragment.onActivityResult(requestCode, resultCode, data) }
  }

  override fun onBackPressed() {
    when {
      (binding?.drawerLayout?.isDrawerOpen(GravityCompat.END) == true) -> binding?.drawerLayout?.closeDrawers()
      (mNavController.currentDestination?.id == R.id.navigation_dashboard) -> this.finish()
      else -> openDashboard()
    }
  }

  override fun onClick(v: View?) {
    super.onClick(v)
    when (v) {
      binding?.drawerView?.btnSiteMeter -> {
        session?.let { this.startOldSiteMeter(it) }
//        startFragmentDashboardActivity(FragmentType.DIGITAL_READINESS_SCORE, bundle = Bundle().apply { putInt(IntentConstant.POSITION.name, 0) })
      }
      binding?.drawerView?.imgBusinessLogo -> this.startBusinessDescriptionEdit(session)
      binding?.drawerView?.txtDomainName -> this.startWebViewPageLoad(session, session!!.getDomainName(false))
      binding?.drawerView?.backgroundImage -> openImagePicker(true)
    }
  }

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {
    when (actionType) {
      RecyclerViewActionType.NAV_CLICK_ITEM_CLICK.ordinal -> {
        val data = item as? DrawerHomeData ?: return
        DrawerHomeData.NavType.from(data.navType)?.let { navClickEvent(it) }
      }
    }
  }

  private fun navClickEvent(type: DrawerHomeData.NavType) {
    when (type) {
      DrawerHomeData.NavType.NAV_HOME -> if ((getFragment(DashboardFragment::class.java) == null)) openDashboard()
      DrawerHomeData.NavType.NAV_DIGITAL_CHANNEL -> session?.let { this.startDigitalChannel(it) }
      DrawerHomeData.NavType.NAV_MANAGE_CONTENT -> session?.let { this.startManageContentActivity(it) }
      DrawerHomeData.NavType.NAV_CALLS -> this.startVmnCallCard(session)
      DrawerHomeData.NavType.NAV_ENQUIRY -> this.startBusinessEnquiry(session)
      DrawerHomeData.NavType.NAV_ORDER_APT_BOOKING -> session?.let { this.startManageInventoryActivity(it) }
      DrawerHomeData.NavType.NAV_NEWS_LETTER_SUB -> this.startSubscriber(session)
      DrawerHomeData.NavType.NAV_BOOST_KEYBOARD -> session?.let { this.startKeyboardActivity(it) }
      DrawerHomeData.NavType.NAV_ADD_ONS_MARKET -> session?.let { this.initiateAddonMarketplace(it, false, "", "") }
      DrawerHomeData.NavType.NAV_SETTING -> session?.let { this.startSettingActivity(it) }
      DrawerHomeData.NavType.NAV_HELP_SUPPORT -> session?.let { this.startHelpAndSupportActivity(it) }
      DrawerHomeData.NavType.NAV_ABOUT_BOOST -> session?.let { this.startAboutBoostActivity(it) }
      DrawerHomeData.NavType.NAV_REFER_FRIEND -> this.startReferralView(session)
    }
    if (binding?.drawerLayout?.isDrawerOpen(GravityCompat.END) == true) binding?.drawerLayout?.closeDrawers()
  }

  private fun openImagePicker(isSecondaryImage: Boolean) {
    this.isSecondaryImage = isSecondaryImage
    val filterSheet = ImagePickerBottomSheet()
    filterSheet.isHidePdf(true)
    filterSheet.onClicked = { clickPickerType(it) }
    filterSheet.show(supportFragmentManager, ImagePickerBottomSheet::class.java.name)
  }

  private fun clickPickerType(it: ClickType) {
    val type = if (it == ClickType.CAMERA) ImagePicker.Mode.CAMERA else ImagePicker.Mode.GALLERY
    ImagePicker.Builder(this)
        .mode(type)
        .compressLevel(ImagePicker.ComperesLevel.MEDIUM).directory(ImagePicker.Directory.DEFAULT)
        .extension(ImagePicker.Extension.PNG).allowMultipleImages(true)
        .scale(800, 800)
        .enableDebuggingMode(true).build()
  }

  private fun uploadSecondaryImage(path: String) {
    val imageFile = File(path)
    isSecondaryImage = false
    showProgress()
    viewModel.putUploadSecondaryImage(getRequestImageDate(imageFile)).observeOnce(this, {
      if (it.isSuccess()) {
        if (it.stringResponse.isNullOrEmpty().not()) {
          session?.storeFPDetails(Key_Preferences.GET_FP_DETAILS_BG_IMAGE, it.stringResponse)
          binding?.drawerView?.bgImage?.let { it1 -> glideLoad(it1, it.stringResponse ?: "", R.drawable.general_services_background_img_d) }
        }
      } else showLongToast(it.message())
      hideProgress()
    })
  }

  private fun getRequestImageDate(businessImage: File): UploadFileBusinessRequest {
    val responseBody = RequestBody.create("image/png".toMediaTypeOrNull(), businessImage.readBytes())
    val fileName = takeIf { businessImage.name.isNullOrEmpty().not() }?.let { businessImage.name }
        ?: "bg_${UUID.randomUUID()}.png"
    return UploadFileBusinessRequest(clientId, session?.fPID, UploadFileBusinessRequest.Type.SINGLE.name, fileName, responseBody)
  }

  private fun initialiseZendeskSupportSdk() {
    try {
      Zendesk.INSTANCE.init(this, "https://boost360.zendesk.com",
          "684341b544a77a2a73f91bd3bb2bc77141d4fc427decda49", "mobile_sdk_client_6c56562cfec5c64c7857")
      val identity = AnonymousIdentity.Builder()
          .withNameIdentifier(session?.fpTag)
          .withEmailIdentifier(session?.fPEmail)
          .build()
      Zendesk.INSTANCE.setIdentity(identity)
      Support.INSTANCE.init(Zendesk.INSTANCE)
      ZopimChat.init("MJwgUJn9SKy2m9ooxsQgJSeTSR5hU3A5")
    } catch (e: Exception) {
    }
  }
}


fun UserSessionManager.getDomainName(isRemoveHttp: Boolean = false): String? {
  val rootAliasUri = getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI)?.toLowerCase(Locale.ROOT)
  val normalUri = "${getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG)?.toLowerCase(Locale.ROOT)}.nowfloats.com"
  return if (rootAliasUri.isNullOrEmpty().not() && rootAliasUri != "null") {
    return if (isRemoveHttp && rootAliasUri!!.contains("http://")) rootAliasUri.replace("http://", "")
    else if (isRemoveHttp && rootAliasUri!!.contains("https://")) rootAliasUri.replace("https://", "") else rootAliasUri
  } else normalUri
}