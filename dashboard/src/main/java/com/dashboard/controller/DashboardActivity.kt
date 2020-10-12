package com.dashboard.controller

import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavArgument
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.dashboard.R
import com.dashboard.base.AppBaseActivity
import com.dashboard.databinding.ActivityDashboardBinding
import com.framework.models.BaseViewModel
import com.framework.views.bottombar.OnItemSelectedListener

class DashboardActivity : AppBaseActivity<ActivityDashboardBinding, BaseViewModel>(), OnItemSelectedListener {

  private lateinit var mNavController: NavController

  override fun getLayout(): Int {
    return R.layout.activity_dashboard
  }

  override fun onCreateView() {
    super.onCreateView()
    mNavController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
    val graph = mNavController.graph
    graph.addArgument("data", NavArgument.Builder().setDefaultValue("data").build())
    mNavController.graph = graph
    navControllerListener()
    binding?.navView?.setOnItemSelectedListener(this)
    binding?.navView?.setActiveItem(0)
  }

  private fun navControllerListener() {
    mNavController.addOnDestinationChangedListener { _, destination, _ -> print("" + destination) }
  }

  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onItemSelect(pos: Int) {
    when (pos) {
      0 -> mNavController.navigate(R.id.navigation_dashboard, Bundle(), getNavOptions())
      1 -> mNavController.navigate(R.id.navigation_content, Bundle(), getNavOptions())
      2 -> mNavController.navigate(R.id.navigation_patients, Bundle(), getNavOptions())
      3 -> mNavController.navigate(R.id.navigation_help, Bundle(), getNavOptions())
      4 -> mNavController.navigate(R.id.navigation_more, Bundle(), getNavOptions())
      else -> mNavController.navigate(R.id.navigation_dashboard, Bundle(), getNavOptions())
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
    if (mNavController.currentDestination?.id == R.id.navigation_dashboard) this.finish() else openDashboard()
  }
}