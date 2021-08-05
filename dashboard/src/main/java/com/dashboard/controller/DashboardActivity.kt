package com.dashboard.controller

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavArgument
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.anachat.chatsdk.AnaCore
import com.appservice.ui.catalog.widgets.ClickType
import com.appservice.ui.catalog.widgets.ImagePickerBottomSheet
import com.dashboard.R
import com.dashboard.base.AppBaseActivity
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.controller.ui.dashboard.DashboardFragment
import com.dashboard.controller.ui.dialog.WelcomeHomeDialog
import com.dashboard.databinding.ActivityDashboardBinding
import com.dashboard.model.live.drawerData.DrawerHomeData
import com.dashboard.model.live.drawerData.DrawerHomeDataResponse
import com.dashboard.model.live.welcomeData.WelcomeDashboardResponse
import com.dashboard.model.live.welcomeData.WelcomeData
import com.dashboard.model.live.welcomeData.getIsShowWelcome
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
import com.dashboard.utils.*
import com.dashboard.viewmodel.DashboardViewModel
import com.framework.extensions.observeOnce
import com.framework.glide.util.glideLoad
import com.framework.imagepicker.ImagePicker
import com.framework.models.firestore.FirestoreManager
import com.framework.models.firestore.FirestoreManager.initData
import com.framework.pref.*
import com.framework.utils.AppsFlyerUtils
import com.framework.utils.ConversionUtils
import com.framework.utils.fromHtml
import com.framework.utils.roundToFloat
import com.framework.views.bottombar.OnItemSelectedListener
import com.framework.views.customViews.CustomToolbar
import com.framework.webengageconstant.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.inventoryorder.utils.DynamicLinkParams
import com.inventoryorder.utils.DynamicLinksManager
import com.onboarding.nowfloats.model.googleAuth.FirebaseTokenResponse
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

  private var doubleBackToExitPressedOnce = false
  private var mDeepLinkUrl: String? = null;
  private var mPayload: String? = null
  private var deepLinkUtil: DeepLinkUtil? = null
  private lateinit var mNavController: NavController
  private var session: UserSessionManager? = null
  private var adapterDrawer: AppBaseRecyclerViewAdapter<DrawerHomeData>? = null
  private var isSecondaryImage = false
  var isLoadShimmer = true
  var count = 0
  private val navHostFragment: NavHostFragment?
    get() {
      return supportFragmentManager.fragments.first() as? NavHostFragment
    }

  private val childFragments: List<Fragment>?
    get() {
      return navHostFragment?.childFragmentManager?.fragments
    }

  private var welcomeData: ArrayList<WelcomeData>? = null

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
    toolbarPropertySet(0)
    setDrawerHome()
    val versionName: String = packageManager.getPackageInfo(packageName, 0).versionName
    binding?.drawerView?.txtVersion?.text = "Version $versionName"
    intentDataCheckAndDeepLink(intent)
    getWelcomeData()
    session?.initializeWebEngageLogin()
    initialize()
    session?.let { initData(it.fpTag ?: "", it.fPID ?: "", clientId) }
    //registerFirebaseToken()
  }

  private fun registerFirebaseToken() {
    viewModel.getFirebaseToken().observe(this,{
      val response = it as FirebaseTokenResponse
      val token = response.Result?:""
      Log.i(TAG, "registerFirebaseToken: "+token)
      FirebaseAuth.getInstance().signInWithCustomToken(token).addOnCompleteListener(this) { task ->
        if (task.isSuccessful) {
          // Sign in success, update UI with the signed-in user's information
          Log.d(TAG, "signInWithCustomToken:success")
        } else {
          // If sign in fails, display a message to the user.
          Log.w(TAG, "signInWithCustomToken:failure", task.exception)
        }
      }
    })
  }

  private fun UserSessionManager.initializeWebEngageLogin() {
    WebEngageController.setUserContactInfoProperties(this)
    WebEngageController.setUserContactInfoProperties(this)
    WebEngageController.setFPTag(this.fpTag)
    WebEngageController.trackAttribute(this)
  }

  private fun initialize() {
    WebEngageController.trackEvent(DASHBOARD_HOME_PAGE, PAGE_VIEW, NO_EVENT_VALUE)
    StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())
    FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
      val token = instanceIdResult.token
      if (token.isNotEmpty()) {
        WebEngage.get().setRegistrationID(token)
        AnaCore.saveFcmToken(this, token)
        AnaCore.registerUser(this, session?.fpTag ?: "", ANA_BUSINESS_ID, ANA_CHAT_API_URL)
      }
    }
    initialiseZendeskSupportSdk()
  }

  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    intentDataCheckAndDeepLink(intent)
  }
  private fun intentDataCheckAndDeepLink(intent: Intent?) {
    Log.i(TAG, "intentDataCheckAndDeepLink: ")
    if (intent?.extras != null) {
      if (intent.extras!!.containsKey("url")) mDeepLinkUrl = intent.extras!!.getString("url")
      if (intent.extras!!.containsKey("payload")) mPayload = intent.extras!!.getString("payload")
    }
    if (intent != null && intent.data != null) {
      val action = intent.action
      val data = intent.dataString
      val uri = intent.data
      Log.d("Data: ", "$data  $action $uri")
      if (session?.isLoginCheck == true) {
        if (uri != null && uri.toString().contains("onelink", true)) {
          isAppFlyerLink()
        } else {
          //Default Deep Link..
          val deepHashMap: HashMap<DynamicLinkParams, String> = DynamicLinksManager().getURILinkParams(uri)
          if (deepHashMap.containsKey(DynamicLinkParams.viewType)) {
            val viewType = deepHashMap[DynamicLinkParams.viewType]
            val buyItemKey = deepHashMap[DynamicLinkParams.buyItemKey]
            if (deepLinkUtil != null) deepLinkUtil?.deepLinkPage(viewType ?: "", buyItemKey ?: "", false)
          } else deepLinkUtil?.deepLinkPage(data?.substring(data.lastIndexOf("/") + 1) ?: "", "", false)
        }
      } else {
        this.startPreSignUp(session, true)
        finish()
      }
    } else isAppFlyerLink()
  }

  private fun isAppFlyerLink() {
    if (AppsFlyerUtils.sAttributionData.containsKey(DynamicLinkParams.viewType.name)) {
      val viewType = AppsFlyerUtils.sAttributionData[DynamicLinkParams.viewType.name] ?: ""
      val buyItemKey = AppsFlyerUtils.sAttributionData[DynamicLinkParams.buyItemKey.name] ?: ""
      if (deepLinkUtil != null) deepLinkUtil?.deepLinkPage(viewType, buyItemKey, false)
      AppsFlyerUtils.sAttributionData = mapOf()
    } else {
      if (deepLinkUtil != null) deepLinkUtil?.deepLinkPage(mDeepLinkUrl ?: "", "", false)
    }
  }

  override fun onResume() {
    super.onResume()
    setUserData()
    setOnClickListener(
      binding?.drawerView?.btnSiteMeter,
      binding?.drawerView?.imgBusinessLogo,
      binding?.drawerView?.backgroundImage,
      binding?.drawerView?.txtDomainName
    )
  }

  override fun getToolbar(): CustomToolbar? {
    return binding?.toolbar
  }

  override fun getToolbarTitleSize(): Float {
    return ConversionUtils.dp2px(18f).toFloat()
  }

  fun setPercentageData(score: Int) {
    val isHigh = (score >= 85)
    binding?.drawerView?.txtPercentage?.text = "$score% "
    val percentage = ((100 - score).toDouble() / 100).roundToFloat(2)
    (binding?.drawerView?.progressBar?.layoutParams as? ConstraintLayout.LayoutParams)?.matchConstraintPercentWidth = percentage
    binding?.drawerView?.progressBar?.requestLayout()
  }

  private fun setUserData() {
    binding?.drawerView?.txtBusinessName?.text = session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME)
    binding?.drawerView?.txtDomainName?.text = fromHtml("<u>${session!!.getDomainName(false)}</u>")
    setPercentageData(FirestoreManager.getDrScoreData()?.getDrsTotal() ?: 0)
    var imageUri = session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_IMAGE_URI)
    if (imageUri.isNullOrEmpty().not() && imageUri!!.contains("http").not()) {
      imageUri = BASE_IMAGE_URL + imageUri
    }
    binding?.drawerView?.imgBusinessLogo?.let {
      glideLoad(it, imageUri ?: "", R.drawable.business_edit_profile_icon_d)
    }
    var bgImageUri = session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_BG_IMAGE)
    if (bgImageUri.isNullOrEmpty().not() && bgImageUri!!.contains("http").not()) {
      bgImageUri = BASE_IMAGE_URL + bgImageUri
    }
    binding?.drawerView?.bgImage?.let {
      glideLoad(it, bgImageUri ?: "", R.drawable.general_services_background_img_d
      )
    }
  }

  private fun setDrawerHome() {
    viewModel.getNavDashboardData(this).observeOnce(this, {
      val response = it as? DrawerHomeDataResponse
      if (response?.isSuccess() == true && response.data.isNullOrEmpty().not()) {
        binding?.drawerView?.rvLeftDrawer?.apply {
          adapterDrawer = AppBaseRecyclerViewAdapter(
            this@DashboardActivity,
            checkLockData(response.data!!),
            this@DashboardActivity
          )
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
      0 -> openDashboard(false)
      1 -> checkWelcomeShowScreen(pos)
      2 -> checkWelcomeShowScreen(pos)
      else -> {
        mNavController.navigate(R.id.navigation_dashboard, Bundle(), getNavOptions())
        toolbarPropertySet(0)
      }
    }

  }

  private fun checkWelcomeShowScreen(pos: Int) {
    when (pos) {
      1 -> {
        val dataWebsite = welcomeData?.get(0)
        if (dataWebsite?.welcomeType?.let { getIsShowWelcome(it) } != true) dataWebsite?.let {
          showWelcomeDialog(it)
        }
        else {
          mNavController.navigate(R.id.navigation_website, Bundle(), getNavOptions())
          toolbarPropertySet(pos)
        }
      }
      2 -> {
        val dataCustomer = welcomeData?.get(1)
        if (dataCustomer?.welcomeType?.let { getIsShowWelcome(it) } != true) dataCustomer?.let {
          showWelcomeDialog(it)
        }
        else {
          mNavController.navigate(R.id.navigation_enquiries, Bundle(), getNavOptions())
          toolbarPropertySet(pos)
        }
      }
      3 -> {
        val dataAddOns = welcomeData?.get(2)
        if (dataAddOns?.welcomeType?.let { getIsShowWelcome(it) } != true) dataAddOns?.let {
          showWelcomeDialog(it)
        }
        else session?.let { this.initiateAddonMarketplace(it, false, "", "") }

      }
    }
  }

  private fun showWelcomeDialog(data: WelcomeData) {
    val dialog = WelcomeHomeDialog.newInstance()
    dialog.setData(data)
    dialog.onClicked = { type ->
      when (type) {
        WelcomeData.WelcomeType.ADD_ON_MARKETPLACE.name -> {
          session?.let { this.initiateAddonMarketplace(it, false, "", "") }
        }
        WelcomeData.WelcomeType.WEBSITE_CONTENT.name -> {
          mNavController.navigate(R.id.navigation_website, Bundle(), getNavOptions())
          toolbarPropertySet(1)
        }
        WelcomeData.WelcomeType.MANAGE_INTERACTION.name -> {
          mNavController.navigate(R.id.navigation_enquiries, Bundle(), getNavOptions())
          toolbarPropertySet(2)
        }
      }
    }
    dialog.showProgress(supportFragmentManager)
  }

  private fun toolbarPropertySet(pos: Int) {
    when (pos) {
      1 -> showToolbar(getString(R.string.my_website))
      2 -> showToolbar(getString(R.string.my_enquiry))
      else -> {
        changeTheme(R.color.colorPrimary, R.color.colorPrimary)
        getToolbar()?.apply { visibility = View.GONE }
      }
    }
  }

  private fun showToolbar(title: String) {
    changeTheme(R.color.black_4a4a4a, R.color.black_4a4a4a)
    getToolbar()?.apply {
      visibility = View.VISIBLE
      supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }
    setToolbarTitle(title)
  }


  override fun onItemClick(pos: Int) {
    super.onItemClick(pos)
    when (pos) {
      3 -> checkWelcomeShowScreen(pos)
      4 -> {
        binding?.drawerLayout?.openDrawer(GravityCompat.END, true)
        WebEngageController.trackEvent(DASHBOARD_MORE, CLICK, TO_BE_ADDED)
      }
    }
  }

  private fun getNavOptions(): NavOptions {
    return NavOptions.Builder().setExitAnim(R.anim.slide_out_left)
      .setEnterAnim(R.anim.slide_in_right).setPopEnterAnim(R.anim.slide_in_left)
      .setPopExitAnim(R.anim.slide_out_right).setLaunchSingleTop(true).build()
  }

  private fun openDashboard(isSet: Boolean = true) {
    mNavController.navigate(R.id.navigation_dashboard, Bundle(), getNavOptions())
    if (isSet) binding?.navView?.setActiveItem(0)
    toolbarPropertySet(0)
    WebEngageController.trackEvent(DASHBOARD_HOME_PAGE, PAGE_VIEW, NO_EVENT_VALUE)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK && isSecondaryImage) {
      val mPaths = data?.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH) as ArrayList<String>
      if (mPaths.isNullOrEmpty().not()) uploadSecondaryImage(mPaths[0])
    } else childFragments?.forEach { fragment ->
      fragment.onActivityResult(
        requestCode,
        resultCode,
        data
      )
    }
  }

  override fun onBackPressed() {
    when {
      (binding?.drawerLayout?.isDrawerOpen(GravityCompat.END) == true) -> binding?.drawerLayout?.closeDrawers()
      (mNavController.currentDestination?.id == R.id.navigation_dashboard) -> {
        if (!doubleBackToExitPressedOnce) {
          this.doubleBackToExitPressedOnce = true
          showShortToast(resources.getString(R.string.press_again_exit))
          Handler(Looper.getMainLooper()).postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
        } else this.finish()
      }
      else -> openDashboard()
    }
  }

  override fun onClick(v: View?) {
    super.onClick(v)
    when (v) {
      binding?.drawerView?.btnSiteMeter -> {
        startReadinessScoreView(session, 0)
        if (binding?.drawerLayout?.isDrawerOpen(GravityCompat.END) == true) binding?.drawerLayout?.closeDrawers()
      }
      binding?.drawerView?.imgBusinessLogo -> {
        this.startFeatureLogo(session)
        if (binding?.drawerLayout?.isDrawerOpen(GravityCompat.END) == true) binding?.drawerLayout?.closeDrawers()
      }
      binding?.drawerView?.txtDomainName -> {
        this.startWebViewPageLoad(session, session!!.getDomainName(false))
        if (binding?.drawerLayout?.isDrawerOpen(GravityCompat.END) == true) binding?.drawerLayout?.closeDrawers()
      }
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
      DrawerHomeData.NavType.NAV_ORDER_APT_BOOKING -> session?.let {
        this.startManageInventoryActivity(
          it
        )
      }
      DrawerHomeData.NavType.NAV_NEWS_LETTER_SUB -> this.startSubscriber(session)
      DrawerHomeData.NavType.NAV_BOOST_KEYBOARD -> session?.let { this.startKeyboardActivity(it) }
      DrawerHomeData.NavType.NAV_ADD_ONS_MARKET -> session?.let {
        this.initiateAddonMarketplace(it, false, "", "")
      }
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
    viewModel.putUploadSecondaryImage(getRequestImageDate(imageFile))
      .observeOnce(this, androidx.lifecycle.Observer {
        if (it.isSuccess()) {
          if (it.stringResponse.isNullOrEmpty().not()) {
            session?.storeFPDetails(Key_Preferences.GET_FP_DETAILS_BG_IMAGE, it.stringResponse)
            binding?.drawerView?.bgImage?.let { it1 ->
              glideLoad(
                it1,
                it.stringResponse ?: "",
                R.drawable.general_services_background_img_d
              )
            }
          }
        } else showLongToast(it.message())
        hideProgress()
      })
  }

  private fun getRequestImageDate(businessImage: File): UploadFileBusinessRequest {
    val responseBody = RequestBody.create("image/png".toMediaTypeOrNull(), businessImage.readBytes())
    val fileName = takeIf { businessImage.name.isNullOrEmpty().not() }?.let { businessImage.name } ?: "bg_${UUID.randomUUID()}.png"
    return UploadFileBusinessRequest(clientId, session?.fPID, UploadFileBusinessRequest.Type.SINGLE.name, fileName, responseBody)
  }

  private fun initialiseZendeskSupportSdk() {
    try {
      Zendesk.INSTANCE.init(
        this, "https://boost360.zendesk.com",
        "684341b544a77a2a73f91bd3bb2bc77141d4fc427decda49", "mobile_sdk_client_6c56562cfec5c64c7857"
      )
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

  private fun getWelcomeData() {
    viewModel.getWelcomeDashboardData(this).observeOnce(this, androidx.lifecycle.Observer {
      val response = it as? WelcomeDashboardResponse
      val data = response?.data?.firstOrNull { it1 ->
        it1.type.equals(
          session?.fP_AppExperienceCode,
          ignoreCase = true
        )
      }?.actionItem
      if (response?.isSuccess() == true && data.isNullOrEmpty().not()) {
        this.welcomeData = data
      }
    })
  }
}


fun UserSessionManager.getDomainName(isRemoveHttp: Boolean = false): String? {
  val rootAliasUri = getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI)?.toLowerCase(Locale.ROOT)
  val normalUri = "https://${getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG)?.toLowerCase(Locale.ROOT)}.nowfloats.com"
  return if (rootAliasUri.isNullOrEmpty().not() && rootAliasUri != "null") {
    return if (isRemoveHttp && rootAliasUri!!.contains("http://")) rootAliasUri.replace("http://", "")
    else if (isRemoveHttp && rootAliasUri!!.contains("https://")) rootAliasUri.replace("https://", "") else rootAliasUri
  } else normalUri
}