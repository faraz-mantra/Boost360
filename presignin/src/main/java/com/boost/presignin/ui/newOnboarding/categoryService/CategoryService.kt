package com.boost.presignin.ui.newOnboarding.categoryService

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import com.boost.presignin.model.category.ApiCategoryResponse
import com.boost.presignin.model.category.saveCategoryLiveData
import com.boost.presignin.rest.repository.BoostKitDevRepository
import com.framework.analytics.SentryController
import com.framework.models.toLiveData
import com.framework.pref.UserSessionManager
import java.lang.Exception

class CategoryService : Service() {

  var userSessionManager: UserSessionManager? = null

  override fun onBind(p0: Intent?): IBinder? {
    return null
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    userSessionManager = UserSessionManager(this.baseContext)
    hitAPIs()
    return START_STICKY
  }

  private fun hitAPIs() {
    BoostKitDevRepository.getCategories().toLiveData().observeForever {
      val categoryData = (it as? ApiCategoryResponse)?.Data?.firstOrNull()
      if (it.isSuccess() && categoryData?.categories.isNullOrEmpty().not()) {
        categoryData?.categories?.saveCategoryLiveData()
        stopSelf()
      }
    }
  }
}

fun AppCompatActivity.startServiceCategory() {
  try {
    startService(Intent(this, CategoryService::class.java))
  } catch (e: Exception) {
    SentryController.captureException(e)
  }
}