/*
 * Copyright (C) 2020 Patrick Goldinger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.patrickgold.florisboard.ime.core

import androidx.multidex.MultiDexApplication
import com.framework.BaseApplication
import dev.patrickgold.florisboard.BuildConfig
import dev.patrickgold.florisboard.crashutility.CrashUtility
import dev.patrickgold.florisboard.customization.network.client.BusinessFeatureApiClient
import dev.patrickgold.florisboard.customization.network.EndPoints
import dev.patrickgold.florisboard.customization.network.client.BoostFloatApiClient
import dev.patrickgold.florisboard.customization.network.client.NfxFloatApiClient
import dev.patrickgold.florisboard.customization.network.client.WebActionBoostApiClient
import dev.patrickgold.florisboard.ime.dictionary.DictionaryManager
import dev.patrickgold.florisboard.ime.extension.AssetManager
import dev.patrickgold.florisboard.ime.theme.ThemeManager
import dev.patrickgold.florisboard.util.PackageManagerUtils
import timber.log.Timber

class FlorisApplication : BaseApplication() {

  companion object {
    lateinit var instance: MultiDexApplication

    @JvmStatic
    fun initModule(application: MultiDexApplication) {
      if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
      CrashUtility.install(application)
      val prefHelper = PrefHelper.getDefaultInstance(application)
      val assetManager = AssetManager.init(application)
      DictionaryManager.init(application)
      ThemeManager.init(application, assetManager, prefHelper)
      prefHelper.initDefaultPreferences()
      PackageManagerUtils.hideAppIcon(application)
      apiInitialize()
    }

    @JvmStatic
    fun apiInitialize() {
      BusinessFeatureApiClient.shared.init(EndPoints.BUSINESS_FEATURE_BASE_URL)
      BoostFloatApiClient.shared.init(EndPoints.BOOST_FLOATS_BASE_URL)
      NfxFloatApiClient.shared.init(EndPoints.NFX_FLOAT_BASE_URL)
      WebActionBoostApiClient.shared.init(EndPoints.WEB_ACTION_BASE_URL)
    }
  }
}
