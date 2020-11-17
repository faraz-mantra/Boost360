package com.dashboard.controller

import android.content.Intent
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.navigation.NavArgument
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.dashboard.R
import com.dashboard.base.AppBaseActivity
import com.dashboard.databinding.ActivityDashboardBinding
import com.dashboard.model.live.drawerData.DrawerHomeData
import com.dashboard.pref.UserSessionManager
import com.dashboard.pref.clientId
import com.dashboard.recyclerView.AppBaseRecyclerViewAdapter
import com.dashboard.recyclerView.BaseRecyclerViewItem
import com.dashboard.recyclerView.RecyclerItemClickListener
import com.dashboard.viewmodel.DashboardViewModel
import com.framework.extensions.observeOnce
import com.framework.views.bottombar.OnItemSelectedListener
import com.inventoryorder.model.floatMessage.MessageModel
import java.util.*

class DashboardActivity : AppBaseActivity<ActivityDashboardBinding, DashboardViewModel>(), OnItemSelectedListener, RecyclerItemClickListener {

  private lateinit var mNavController: NavController
  private var session: UserSessionManager? = null
  private var adapterDrawer: AppBaseRecyclerViewAdapter<DrawerHomeData>? = null

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
  }

  private fun setDrawerHome() {
    binding?.rvLeftDrawer?.apply {
      adapterDrawer = AppBaseRecyclerViewAdapter(this@DashboardActivity, DrawerHomeData().getData(), this@DashboardActivity)
      adapter = adapterDrawer
    }
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
    val navHostFragment = supportFragmentManager.fragments.first() as? NavHostFragment
    navHostFragment?.let {
      val childFragments = navHostFragment.childFragmentManager.fragments
      childFragments.forEach { fragment -> fragment.onActivityResult(requestCode, resultCode, data) }
    }
  }

  override fun onBackPressed() {
    when {
      (binding?.drawerLayout?.isDrawerOpen(GravityCompat.START) == true) -> binding?.drawerLayout?.closeDrawers()
      (mNavController.currentDestination?.id == R.id.navigation_dashboard) -> this.onNavPressed()
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

  override fun onItemClick(position: Int, item: BaseRecyclerViewItem?, actionType: Int) {

  }
}