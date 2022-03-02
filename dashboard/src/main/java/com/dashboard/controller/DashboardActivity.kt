package com.dashboard.controller

import android.content.Intent
import android.content.IntentSender
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavArgument
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.anachat.chatsdk.AnaCore
import com.dashboard.R
import com.dashboard.base.AppBaseActivity
import com.dashboard.controller.ui.dashboard.DashboardFragment
import com.dashboard.controller.ui.dialog.WelcomeHomeDialog
import com.dashboard.databinding.ActivityDashboardBinding
import com.dashboard.model.DisableBadgeNotificationRequest
import com.dashboard.model.live.welcomeData.*
import com.dashboard.utils.*
import com.dashboard.utils.DashboardTabs.Companion.fromUrl
import com.dashboard.viewmodel.DashboardViewModel
import com.framework.analytics.SentryController
import com.framework.extensions.gone
import com.framework.extensions.observeOnce
import com.framework.extensions.visible
import com.framework.firebaseUtils.FirebaseRemoteConfigUtil.appUpdateType
import com.framework.firebaseUtils.FirebaseRemoteConfigUtil.initRemoteConfigData
import com.framework.firebaseUtils.caplimit_feature.CapLimitFeatureResponseItem
import com.framework.firebaseUtils.caplimit_feature.saveCapData
import com.framework.firebaseUtils.firestore.FirestoreManager.initData
import com.framework.firebaseUtils.firestore.badges.BadgesFirestoreManager
import com.framework.firebaseUtils.firestore.badges.BadgesFirestoreManager.getBadgesData
import com.framework.firebaseUtils.firestore.badges.BadgesFirestoreManager.initDataBadges
import com.framework.firebaseUtils.firestore.badges.BadgesFirestoreManager.readDrScoreDocument
import com.framework.firebaseUtils.firestore.badges.BadgesModel
import com.framework.pref.*
import com.framework.pref.Key_Preferences.KEY_FP_CART_COUNT
import com.framework.utils.AppsFlyerUtils
import com.framework.utils.ConversionUtils
import com.framework.views.bottombar.OnItemSelectedListener
import com.framework.views.customViews.CustomToolbar
import com.framework.webengageconstant.DASHBOARD_HOME_PAGE
import com.framework.webengageconstant.NO_EVENT_VALUE
import com.framework.webengageconstant.PAGE_VIEW
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
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
import kotlin.concurrent.schedule

class DashboardActivity : AppBaseActivity<ActivityDashboardBinding, DashboardViewModel>(), OnItemSelectedListener {

  private val MY_REQUEST_CODE = 120
  private var doubleBackToExitPressedOnce = false
  private var mDeepLinkUrl: String? = null;
  private var mPayload: String? = null
  private var deepLinkUtil: DeepLinkUtil? = null
  private lateinit var mNavController: NavController
  private var session: UserSessionManager? = null
  private lateinit var appUpdateManager: AppUpdateManager
  private lateinit var appUpdateInfoTask: Task<AppUpdateInfo>
  var isLoadShimmer = true
  var count = 0
  var activePreviousItem = 0
  private var dataBadges: ArrayList<BadgesModel>? = arrayListOf()
  private val navHostFragment: NavHostFragment?
    get() {
      return supportFragmentManager.fragments.first() as? NavHostFragment
    }

  private val childFragments: List<Fragment>?
    get() {
      return navHostFragment?.childFragmentManager?.fragments
    }

  private val welcomeData: List<WelcomeData>?
    get() {
      return getWelcomeList()
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
    bottomNavInitializer()
    toolbarPropertySet(0)
    setOnClickListener(binding?.viewCartCount)
    getWelcomeData()
    intentDataCheckAndDeepLink(intent)
    session?.initializeWebEngageLogin()
    initialize()
    session?.let { initDataBadges(it.fpTag ?: "", it.fPID ?: "", clientId) }
    session?.let { initData(it.fpTag ?: "", it.fPID ?: "", clientId) }
    initRemoteConfigData(this)
    reloadCapLimitData()
    checkForUpdate()
  }

  private fun bottomNavInitializer() {
    if (this.packageName.equals(APPLICATION_JIO_ID, true))
      binding?.viewBottomBar?.navView?.setClickPosition(ArrayList())
    binding?.viewBottomBar?.navView?.setOnItemSelectedListener(this)
  }

  private fun checkForUpdate() {
    Log.i(TAG, "checkForUpdate: Inside Method")
    appUpdateManager = AppUpdateManagerFactory.create(this)
    appUpdateManager.registerListener(appUpdateListener)
    appUpdateInfoTask = appUpdateManager.appUpdateInfo
    appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
      Log.i(TAG, "checkForUpdate: ${appUpdateInfo.updateAvailability()} ${appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)}")
      if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE || appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
        startUpdate(appUpdateInfo)
      } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
        popupSnackBarForCompleteUpdate();
      }
    }
  }

  private fun startUpdate(appUpdateInfo: AppUpdateInfo) {
    try {
      appUpdateManager.startUpdateFlowForResult(appUpdateInfo, appUpdateType().ordinal, this, MY_REQUEST_CODE)
    } catch (e: IntentSender.SendIntentException) {
      e.printStackTrace();
      SentryController.captureException(e)
    }
  }

  private val appUpdateListener: InstallStateUpdatedListener = InstallStateUpdatedListener { state ->
    when {
      state.installStatus() == InstallStatus.DOWNLOADED -> popupSnackBarForCompleteUpdate()
      state.installStatus() == InstallStatus.INSTALLED -> removeInstallStateUpdateListener()
      else -> showShortToast("InstallStateUpdatedListener: state: ${state.installStatus()}")
    }
  }

  private fun popupSnackBarForCompleteUpdate() {
    Snackbar.make(findViewById<View>(android.R.id.content).rootView, getString(R.string.download_complete), Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.install)) {
      if (this::appUpdateManager.isInitialized) appUpdateManager.completeUpdate()
    }.setActionTextColor(ContextCompat.getColor(this, R.color.green_light)).show()
  }

  private fun removeInstallStateUpdateListener() {
    if (this::appUpdateManager.isInitialized) appUpdateManager.unregisterListener(appUpdateListener)
  }

  private fun reloadCapLimitData() {
    viewModel.getCapLimitFeatureDetails(session?.fPID ?: "", clientId).observeOnce(this) {
      if (it.isSuccess()) {
        val capLimitList = it.arrayResponse as? Array<CapLimitFeatureResponseItem>
        capLimitList?.toCollection(ArrayList())?.saveCapData()
      }
    }
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
    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
      if (!task.isSuccessful) {
        Log.w(TAG, "Fetching FCM registration token failed", task.exception)
        return@OnCompleteListener
      }
      val token = task.result
      if (token.isNotEmpty()) {
        WebEngage.get().setRegistrationID(token)
        AnaCore.saveFcmToken(this, token)
        AnaCore.registerUser(this, session?.fpTag ?: "", ANA_BUSINESS_ID, ANA_CHAT_API_URL)
      }
    })
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
      val url = intent.data
      mDeepLinkUrl = url.toString()
      Log.d("Data: ", "$data  $action $mDeepLinkUrl")
      if (session?.isLoginCheck == true) {
        if (mDeepLinkUrl != null && mDeepLinkUrl.toString().contains("onelink", true)) {
          isAppFlyerLink()
        } else {
          if (!checkIsHomeDeepLink()) {
            val deepHashMap: HashMap<DynamicLinkParams, String> = DynamicLinksManager().getURILinkParams(url)
            if (deepHashMap.containsKey(DynamicLinkParams.viewType)) {
              val viewType = deepHashMap[DynamicLinkParams.viewType]
              val buyItemKey = deepHashMap[DynamicLinkParams.buyItemKey]
              if (deepLinkUtil != null) deepLinkUtil?.deepLinkPage(viewType ?: "", buyItemKey ?: "", false)
            } else deepLinkUtil?.deepLinkPage(data?.substring(data.lastIndexOf("/") + 1) ?: "", "", false)
          }
        }
      } else {
        this.startPreSignUp(session, true)
        finish()
      }
    } else isAppFlyerLink()
  }

  private fun isAppFlyerLink() {
    if (!checkIsHomeDeepLink()) {
      if (AppsFlyerUtils.sAttributionData.containsKey(DynamicLinkParams.viewType.name)) {
        val viewType = AppsFlyerUtils.sAttributionData[DynamicLinkParams.viewType.name] ?: ""
        val buyItemKey = AppsFlyerUtils.sAttributionData[DynamicLinkParams.buyItemKey.name] ?: ""
        if (deepLinkUtil != null) deepLinkUtil?.deepLinkPage(viewType, buyItemKey, false)
      } else {
        if (deepLinkUtil != null) deepLinkUtil?.deepLinkPage(mDeepLinkUrl ?: "", "", false)
      }
      AppsFlyerUtils.sAttributionData = mapOf()
    }
  }

  private fun checkIsHomeDeepLink(): Boolean {
    val value = fromUrl(mDeepLinkUrl)
    return if (value != null) {
      if (binding?.viewBottomBar?.navView?.getActiveItem() != value.position) {
        Timer().schedule(50) {
          binding?.viewBottomBar?.navView?.post {
            binding?.viewBottomBar?.navView?.setActiveItem(value.position)
            onItemSelect(value.position)
          }
        }
      }
      true
    } else false
  }

  override fun onResume() {
    super.onResume()
    setUserData()
  }

  override fun getToolbar(): CustomToolbar? {
    return binding?.toolbar
  }

  override fun getToolbarTitleTypeface(): Typeface? {
    return ResourcesCompat.getFont(this, R.font.bold)
  }

  override fun getToolbarTitleSize(): Float {
    return ConversionUtils.dp2px(22f).toFloat()
  }

  private fun setUserData() {
    val cartCount = session?.getIntDetails(KEY_FP_CART_COUNT) ?: 0
    if ((getFragment(DashboardFragment::class.java) != null) && cartCount > 0) binding?.viewCartCount?.visible() else binding?.viewCartCount?.gone()
    binding?.cartCountTxt?.text = "$cartCount ${if (cartCount > 1) "items" else "item"} waiting in cart"
  }

  private fun cartDataLoad(pos: Int) {
    val cartCount = session?.getIntDetails(KEY_FP_CART_COUNT) ?: 0
    if (pos == 0 && cartCount > 0) {
      binding?.viewCartCount?.visible()
    } else binding?.viewCartCount?.gone()
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
      3 -> checkWelcomeShowScreen(pos)
      4 -> checkWelcomeShowScreen(pos)
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
        disableBadgeNotification(BadgesModel.BadgesType.WEBSITEBADGE.name)
      }
      2 -> {
        val dataCustomer = welcomeData?.get(1)
        if (dataCustomer?.welcomeType?.let { getIsShowWelcome(it) } != true) dataCustomer?.let { showWelcomeDialog(it) }
        else {
          mNavController.navigate(R.id.navigation_enquiries, Bundle(), getNavOptions())
          toolbarPropertySet(pos)
        }
        disableBadgeNotification(BadgesModel.BadgesType.ENQUIRYBADGE.name)
      }
      3 -> {
        if (this.packageName.equals(APPLICATION_JIO_ID, true)) {
          mNavController.navigate(R.id.more_settings, Bundle(), getNavOptions())
          toolbarPropertySet(pos)
          disableBadgeNotification(BadgesModel.BadgesType.MENUBADGE.name)
        } else {
          val dataAddOns = welcomeData?.get(2)
          if (dataAddOns?.welcomeType?.let { getIsShowWelcome(it) } != true) dataAddOns?.let { showWelcomeDialog(it) }
          else session?.let { this.initiateAddonMarketplace(it, false, "", "") }
          disableBadgeNotification(BadgesModel.BadgesType.MARKETPLACEBADGE.name)
        }
      }
      4 -> {
        mNavController.navigate(R.id.more_settings, Bundle(), getNavOptions())
        toolbarPropertySet(pos)
        disableBadgeNotification(BadgesModel.BadgesType.MENUBADGE.name)
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
    cartDataLoad(pos)
    when (pos) {
      1 -> showToolbar(getString(R.string.website))
      2 -> showToolbar(getString(R.string.enquiry))
      3 -> if (this.packageName.equals(APPLICATION_JIO_ID, true)) showToolbar(getString(R.string.more))
      4 -> showToolbar(getString(R.string.more))
      else -> {
        if (packageName.equals(APPLICATION_JIO_ID, ignoreCase = true).not()) {
          changeTheme(R.color.colorPrimary, R.color.colorPrimary)
        }
        getToolbar()?.apply { visibility = View.GONE }
      }
    }
  }

  private fun showToolbar(title: String) {
    changeTheme(R.color.black_4a4a4a_jio, R.color.black_4a4a4a_jio)
    getToolbar()?.apply {
      visibility = View.VISIBLE
      supportActionBar?.setDisplayHomeAsUpEnabled(false)
      setToolbarTitle(title)
    }
  }


  override fun onItemClick(pos: Int) {
    super.onItemClick(pos)
    when (pos) {
      3 -> if (this.packageName.equals(APPLICATION_JIO_ID, true).not()) checkWelcomeShowScreen(pos)
    }
  }

  private fun getNavOptions(): NavOptions {
    val activeItem = binding?.viewBottomBar?.navView?.getActiveItem() ?: 0
    return if (activePreviousItem > activeItem) {
      activePreviousItem = activeItem
      NavOptions.Builder().setExitAnim(R.anim.slide_out_right)
        .setEnterAnim(R.anim.slide_in_left).setPopEnterAnim(R.anim.slide_in_right)
        .setPopExitAnim(R.anim.slide_out_left).setLaunchSingleTop(true).build()
    } else {
      activePreviousItem = activeItem
      NavOptions.Builder().setExitAnim(R.anim.slide_out_left)
        .setEnterAnim(R.anim.slide_in_right).setPopEnterAnim(R.anim.slide_in_left)
        .setPopExitAnim(R.anim.slide_out_right).setLaunchSingleTop(true).build()
    }
  }

  private fun openDashboard(isSet: Boolean = true) {
    mNavController.navigate(R.id.navigation_dashboard, Bundle(), getNavOptions())
    if (isSet) binding?.viewBottomBar?.navView?.setActiveItem(0)
    toolbarPropertySet(0)
    WebEngageController.trackEvent(DASHBOARD_HOME_PAGE, PAGE_VIEW, NO_EVENT_VALUE)
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == MY_REQUEST_CODE) {
      when (resultCode) {
        RESULT_OK -> showShortToast("App updated successfully")
        RESULT_CANCELED -> showShortToast("App update cancelled")
        ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> showShortToast("App update failed")
      }
    } else {
      childFragments?.forEach { fragment ->
        fragment.onActivityResult(requestCode, resultCode, data)
      }
    }
  }

  override fun onBackPressed() {
    when {
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
      binding?.viewCartCount -> session?.let { this.initiateAddonMarketplace(it, true, "", "") }
    }
  }

  private fun getRequestImageDate(businessImage: File): UploadFileBusinessRequest {
    val responseBody = RequestBody.create("image/png".toMediaTypeOrNull(), businessImage.readBytes())
    val fileName = takeIf { businessImage.name.isNullOrEmpty().not() }?.let { businessImage.name } ?: "bg_${UUID.randomUUID()}.png"
    return UploadFileBusinessRequest(clientId, session?.fPID, UploadFileBusinessRequest.Type.SINGLE.name, fileName, responseBody)
  }

  private fun initialiseZendeskSupportSdk() {
    try {
      Zendesk.INSTANCE.init(
        this, com.dashboard.BuildConfig.ZENDESK_URL,
        com.dashboard.BuildConfig.ZENDESK_APPLICATION_ID, com.dashboard.BuildConfig.ZENDESK_OAUTH_CLIENT_ID
      )
      val identity = AnonymousIdentity.Builder().withNameIdentifier(session?.fpTag).withEmailIdentifier(session?.fPEmail).build()
      Zendesk.INSTANCE.setIdentity(identity)
      Support.INSTANCE.init(Zendesk.INSTANCE)
      ZopimChat.init(com.dashboard.BuildConfig.ZOPIM_ACCOUNT_KEY)
    } catch (e: Exception) {
      SentryController.captureException(e)
    }
  }

  private fun getWelcomeData() {
    viewModel.getWelcomeDashboardData(this).observeOnce(this) {
      val response = it as? WelcomeDashboardResponse
      val data = response?.data?.firstOrNull { it1 -> it1.type.equals(session?.fP_AppExperienceCode, ignoreCase = true) }?.actionItem
      if (response?.isSuccess() == true && data.isNullOrEmpty().not()) {
        data?.saveWelcomeList()
      }
    }
  }

  override fun onStop() {
    super.onStop()
    BadgesFirestoreManager.listenerBadges = null
    removeInstallStateUpdateListener()
  }

  override fun onStart() {
    super.onStart()
    BadgesFirestoreManager.listenerBadges = {
      dataBadges = getBadgesData()
      setBadgesData(dataBadges)
    }
  }

  private fun setBadgesData(dataBadges: ArrayList<BadgesModel>?) {
    binding?.viewBottomBar?.navView?.post {
      if (dataBadges.isNullOrEmpty().not())
        dataBadges!!.forEach {
          when (it.badgesType) {
//            BadgesModel.BadgesType.HOMEBADGE.name -> {
//              if (it.getMessageN() > 0) binding?.navView?.setBadge(0, it.getMessageText())
//              else binding?.navView?.removeBadge(0)
//            }
            BadgesModel.BadgesType.WEBSITEBADGE.name -> {
              if (it.getMessageN() > 0 && it.getIsEnable()) binding?.viewBottomBar?.navView?.setBadge(1, it.getMessageText())
              else binding?.viewBottomBar?.navView?.removeBadge(1)
            }
            BadgesModel.BadgesType.ENQUIRYBADGE.name -> {
              if (it.getMessageN() > 0 && it.getIsEnable()) binding?.viewBottomBar?.navView?.setBadge(2, it.getMessageText())
              else binding?.viewBottomBar?.navView?.removeBadge(2)
            }
            BadgesModel.BadgesType.MARKETPLACEBADGE.name -> {
              if (it.getMessageN() > 0 && it.getIsEnable()) binding?.viewBottomBar?.navView?.setBadge(3, it.getMessageText())
              else binding?.viewBottomBar?.navView?.removeBadge(3)
            }
            BadgesModel.BadgesType.MENUBADGE.name -> {
              if (it.getMessageN() > 0 && it.getIsEnable()) binding?.viewBottomBar?.navView?.setBadge(4, it.getMessageText())
              else binding?.viewBottomBar?.navView?.removeBadge(4)
            }
          }
        }
      else {
//        binding?.navView?.removeBadge(0)
        binding?.viewBottomBar?.navView?.removeBadge(1)
        binding?.viewBottomBar?.navView?.removeBadge(2)
        binding?.viewBottomBar?.navView?.removeBadge(3)
        binding?.viewBottomBar?.navView?.removeBadge(4)
      }
    }
  }

  private fun isBadgeCountAvailable(badgeType: String): Boolean {
    val badgeItem = dataBadges?.firstOrNull { it.badgesType == badgeType }
    return badgeItem?.getMessageN() ?: 0 > 0
  }

  private fun disableBadgeNotification(flagId: String) {
    if (isBadgeCountAvailable(flagId)) {
      val request = DisableBadgeNotificationRequest(session?.fpTag, "BADGE", flagId)
      viewModel.disableBadgeNotification(request).observeOnce(this) {
        Log.i("DisableBadge", "Response: $it")
        readDrScoreDocument()
      }
    }
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