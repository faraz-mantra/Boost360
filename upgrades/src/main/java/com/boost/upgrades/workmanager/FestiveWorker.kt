package com.boost.upgrades.workmanager

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProviders
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.boost.upgrades.UpgradeActivity
import com.boost.upgrades.ui.home.HomeFragment
import com.boost.upgrades.ui.home.HomeViewModel
import com.boost.upgrades.ui.home.HomeViewModelFactory

class FestiveWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
//  private var viewModel: HomeViewModel
//  private var homeViewModelFactory: HomeViewModelFactory = HomeViewModelFactory(requireNotNull(applicationContext) as Application)
//  private var homeFragment: HomeFragment = HomeFragment()


  override fun doWork(): Result {
//    loadData()
    return Result.success()
  }

//  init {
//
//    viewModel = ViewModelProviders.of(homeFragment, homeViewModelFactory)
//      .get(HomeViewModel::class.java)
//  }
//
//  fun loadData() {
//    val pref = applicationContext.getSharedPreferences("nowfloatsPrefs", Context.MODE_PRIVATE)
//    val fpTag = pref.getString("GET_FP_DETAILS_TAG", null)
//    var code: String = (applicationContext as UpgradeActivity).experienceCode!!
//    if (!code.equals("null", true)) {
//      viewModel.setCurrentExperienceCode(code, fpTag!!)
//    }

//    viewModel.loadUpdates(
//      (applicationContext as? UpgradeActivity)?.getAccessToken() ?: "",
//      (applicationContext as UpgradeActivity).fpid!!,
//      (applicationContext as UpgradeActivity).clientid,
//      (applicationContext as UpgradeActivity).experienceCode,
//      (applicationContext as UpgradeActivity).fpTag
//    )
//  }


}