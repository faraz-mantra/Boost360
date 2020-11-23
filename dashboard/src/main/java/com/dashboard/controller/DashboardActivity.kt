package com.dashboard.controller

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavArgument
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.dashboard.R
import com.dashboard.base.AppBaseActivity
import com.dashboard.constant.FragmentType
import com.dashboard.constant.IntentConstant
import com.dashboard.constant.RecyclerViewActionType
import com.dashboard.controller.ui.dashboard.DashboardFragment
import com.dashboard.databinding.ActivityDashboardBinding
import com.dashboard.model.live.drawerData.DrawerHomeData
import com.dashboard.model.live.drawerData.DrawerHomeDataResponse
import com.dashboard.pref.BASE_IMAGE_URL
import com.dashboard.pref.Key_Preferences
import com.dashboard.pref.UserSessionManager
import com.dashboard.pref.clientId
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
import com.dashboard.utils.*
import com.dashboard.viewmodel.DashboardViewModel
import com.framework.extensions.observeOnce
import com.framework.glide.util.glideLoad
import com.framework.utils.fromHtml
import com.framework.views.bottombar.OnItemSelectedListener
import com.inventoryorder.model.floatMessage.MessageModel
import java.util.*

class DashboardActivity : AppBaseActivity<ActivityDashboardBinding, DashboardViewModel>(), OnItemSelectedListener, RecyclerItemClickListener {

  private lateinit var mNavController: NavController
  private var session: UserSessionManager? = null
  private var adapterDrawer: AppBaseRecyclerViewAdapter<DrawerHomeData>? = null
  private var isHigh = false
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
    mNavController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
    val graph = mNavController.graph
    graph.addArgument("data", NavArgument.Builder().setDefaultValue("data").build())
    mNavController.graph = graph
    navControllerListener()
    binding?.navView?.setOnItemSelectedListener(this)
    binding?.navView?.setActiveItem(0)
    setDrawerHome()
    getFloatMessage()
    val versionName: String = packageManager.getPackageInfo(packageName, 0).versionName
    binding?.drawerView?.txtVersion?.text = "Version $versionName"
  }

  override fun onResume() {
    super.onResume()
    setUserData()
    setOnClickListener(binding?.drawerView?.btnSiteMeter, binding?.drawerView?.imgBusinessLogo, binding?.drawerView?.backgroundImage)
  }

  private fun setUserData() {
    binding?.drawerView?.txtBusinessName?.text = session?.getFPDetails(Key_Preferences.GET_FP_DETAILS_BUSINESS_NAME)
    binding?.drawerView?.txtDomainName?.text = fromHtml("<u>${session!!.getDomainName(false)}</u>")
    val score = session?.siteMeterCalculation() ?: 0
    isHigh = (score >= 80)
    binding?.drawerView?.txtPercentage?.text = "$score%"
    binding?.drawerView?.progressBar?.progress = score
    binding?.drawerView?.txtSiteHelth?.setTextColor(ContextCompat.getColor(this, if (isHigh) R.color.light_green_3 else R.color.accent_dark))
    binding?.drawerView?.progressBar?.progressDrawable = ContextCompat.getDrawable(this, if (isHigh) R.drawable.ic_progress_bar_horizontal_high else R.drawable.progress_bar_horizontal)
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
//    data.forEach {
//      if (it.navType== DrawerHomeData.NavType.NAV_CALLS.name && ){
//
//      }
//    }
    return data
  }

  private fun getFloatMessage() {
    viewModel.getBizFloatMessage(getRequestFloat()).observeOnce(this, {
      if (it?.isSuccess() == true) (it as? MessageModel)?.saveData()
    })
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
      1 -> mNavController.navigate(R.id.navigation_content, Bundle(), getNavOptions())
      2 -> mNavController.navigate(R.id.navigation_patients, Bundle(), getNavOptions())
      3 -> mNavController.navigate(R.id.navigation_academy, Bundle(), getNavOptions())
//      4 -> mNavController.navigate(R.id.navigation_more, Bundle(), getNavOptions())
      else -> mNavController.navigate(R.id.navigation_dashboard, Bundle(), getNavOptions())
    }
  }

  override fun onItemClick(pos: Int) {
    super.onItemClick(pos)
    when (pos) {
      4 -> binding?.drawerLayout?.openDrawer(GravityCompat.START, true)
    }
  }

  private fun getNavOptions(): NavOptions? {
    return NavOptions.Builder().setLaunchSingleTop(true).build()
  }

  private fun openDashboard() {
    binding?.navView?.setActiveItem(0)
    mNavController.navigate(R.id.navigation_dashboard, Bundle(), getNavOptions())
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    childFragments?.forEach { fragment -> fragment.onActivityResult(requestCode, resultCode, data) }
  }

  override fun onBackPressed() {
    when {
      (binding?.drawerLayout?.isDrawerOpen(GravityCompat.START) == true) -> binding?.drawerLayout?.closeDrawers()
      (mNavController.currentDestination?.id == R.id.navigation_dashboard) -> this.finish()
      else -> openDashboard()
    }
  }

  private fun getRequestFloat(): Map<String, String> {
    val map = HashMap<String, String>()
    map["clientId"] = clientId
    map["skipBy"] = "0"
    map["fpId"] = session?.fPID!!
    return map
  }

  override fun onClick(v: View?) {
    super.onClick(v)
    when (v) {
      binding?.drawerView?.btnSiteMeter -> startFragmentDashboardActivity(FragmentType.DIGITAL_READINESS_SCORE, bundle = Bundle().apply { putInt(IntentConstant.POSITION.name, 0) })
      binding?.drawerView?.imgBusinessLogo -> {
      }
      binding?.drawerView?.backgroundImage -> {
      }
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
      DrawerHomeData.NavType.NAV_CALLS -> this.startVmnCallCard()
      DrawerHomeData.NavType.NAV_ENQUIRY -> this.startBusinessEnquiry()
      DrawerHomeData.NavType.NAV_ORDER_APT_BOOKING -> session?.let { this.startManageInventoryActivity(it) }
      DrawerHomeData.NavType.NAV_NEWS_LETTER_SUB -> this.startSubscriber()
      DrawerHomeData.NavType.NAV_BOOST_KEYBOARD -> session?.let { this.startKeyboardActivity(it) }
      DrawerHomeData.NavType.NAV_ADD_ONS_MARKET -> session?.let { this.initiateAddonMarketplace(it, false, "", "") }
      DrawerHomeData.NavType.NAV_SETTING -> session?.let { this.startSettingActivity(it) }
      DrawerHomeData.NavType.NAV_HELP_SUPPORT -> session?.let { this.startHelpAndSupportActivity(it) }
      DrawerHomeData.NavType.NAV_ABOUT_BOOST -> session?.let { this.startAboutBoostActivity(it) }
      DrawerHomeData.NavType.NAV_REFER_FRIEND -> {
      }
    }
    if (binding?.drawerLayout?.isDrawerOpen(GravityCompat.START) == true) binding?.drawerLayout?.closeDrawers()
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