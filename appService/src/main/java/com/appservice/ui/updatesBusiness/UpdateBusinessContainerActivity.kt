package com.appservice.ui.updatesBusiness

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.appservice.R
import com.appservice.base.AppBaseActivity
import com.appservice.constant.FragmentType
import com.framework.base.BaseFragment
import com.framework.base.FRAGMENT_TYPE
import com.framework.databinding.ActivityFragmentContainerBinding
import com.framework.models.BaseViewModel
import com.framework.pref.Key_Preferences
import com.framework.pref.UserSessionManager
import com.framework.views.customViews.CustomToolbar
import java.util.*

open class UpdateBusinessContainerActivity :
  AppBaseActivity<ActivityFragmentContainerBinding, BaseViewModel>() {

  private var type: FragmentType? = null
  private var session: UserSessionManager? = null

  override fun getLayout(): Int {
    return com.framework.R.layout.activity_fragment_container
  }


  override fun getViewModelClass(): Class<BaseViewModel> {
    return BaseViewModel::class.java
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    intent?.extras?.getInt(FRAGMENT_TYPE)?.let { type = FragmentType.values()[it] }
    session = UserSessionManager(this)
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView() {
    super.onCreateView()
    setFragment()
  }

  override fun getToolbarTitleGravity(): Int {
    return Gravity.START
  }

  override fun getToolbar(): CustomToolbar? {
    return binding?.appBarLayout?.toolbar
  }

  override fun getToolbarBackgroundColor(): Int? {
    return when (type) {
      FragmentType.UPDATE_BUSINESS_FRAGMENT, FragmentType.ADD_UPDATE_BUSINESS_FRAGMENT, FragmentType.DETAIL_UPDATE_BUSINESS_FRAGMENT -> ContextCompat.getColor(
        this,
        R.color.colorPrimary
      )
      else -> super.getToolbarBackgroundColor()
    }
  }

  override fun getToolbarTitleColor(): Int? {
    return when (type) {
      FragmentType.UPDATE_BUSINESS_FRAGMENT, FragmentType.ADD_UPDATE_BUSINESS_FRAGMENT, FragmentType.DETAIL_UPDATE_BUSINESS_FRAGMENT -> ContextCompat.getColor(
        this,
        R.color.white
      )
      else -> super.getToolbarTitleColor()
    }
  }

  override fun getNavigationIcon(): Drawable? {
    return when (type) {
      FragmentType.UPDATE_BUSINESS_FRAGMENT, FragmentType.ADD_UPDATE_BUSINESS_FRAGMENT, FragmentType.DETAIL_UPDATE_BUSINESS_FRAGMENT -> ContextCompat.getDrawable(
        this,
        R.drawable.ic_back_arrow_new
      )
      else -> super.getNavigationIcon()
    }
  }

  override fun getToolbarTitle(): String? {
    return when (type) {
      FragmentType.UPDATE_BUSINESS_FRAGMENT -> getLatestUpdatesTaxonomyFromServiceCode(session?.fP_AppExperienceCode)
      FragmentType.ADD_UPDATE_BUSINESS_FRAGMENT -> getString(R.string.post_an_update_n)
      FragmentType.DETAIL_UPDATE_BUSINESS_FRAGMENT -> ""
      else -> super.getToolbarTitle()
    }
  }

  private fun shouldAddToBackStack(): Boolean {
    return when (type) {
      else -> false
    }
  }

  private fun setFragment() {
    val fragment = getFragmentInstance(type)
    fragment?.arguments = intent.extras
    binding?.container?.id?.let { addFragmentReplace(it, fragment, shouldAddToBackStack()) }
  }

  private fun getFragmentInstance(type: FragmentType?): BaseFragment<*, *>? {
    return when (type) {
      FragmentType.UPDATE_BUSINESS_FRAGMENT -> UpdatesBusinessFragment.newInstance()
      FragmentType.ADD_UPDATE_BUSINESS_FRAGMENT -> AddUpdateBusinessFragment.newInstance()
      FragmentType.DETAIL_UPDATE_BUSINESS_FRAGMENT -> DetailUpdateBusinessFragment.newInstance()
      else -> UpdatesBusinessFragment.newInstance()
    }
  }


  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    for (fragment in supportFragmentManager.fragments) {
      fragment.onActivityResult(requestCode, resultCode, data)
    }
  }

}

fun Fragment.startUpdateFragmentActivity(
  type: FragmentType,
  bundle: Bundle = Bundle(),
  clearTop: Boolean = false,
  isResult: Boolean = false
) {
  val intent = Intent(activity, UpdateBusinessContainerActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  if (isResult.not()) startActivity(intent) else startActivityForResult(intent, 101)
}

fun startUpdateFragmentActivityNew(
  activity: Activity,
  type: FragmentType,
  bundle: Bundle = Bundle(),
  clearTop: Boolean,
  isResult: Boolean = false
) {
  val intent = Intent(activity, UpdateBusinessContainerActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  if (isResult.not()) activity.startActivity(intent) else activity.startActivityForResult(
    intent,
    101
  )
}

fun AppCompatActivity.startUpdateFragmentActivity(
  type: FragmentType,
  bundle: Bundle = Bundle(),
  clearTop: Boolean = false
) {
  val intent = Intent(this, UpdateBusinessContainerActivity::class.java)
  intent.putExtras(bundle)
  intent.setFragmentType(type)
  if (clearTop) intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
  startActivity(intent)
}

fun Intent.setFragmentType(type: FragmentType): Intent {
  return this.putExtra(FRAGMENT_TYPE, type.ordinal)
}


fun UserSessionManager.getDomainName(isRemoveHttp: Boolean = false): String? {
  val rootAliasUri =
    getFPDetails(Key_Preferences.GET_FP_DETAILS_ROOTALIASURI)?.toLowerCase(Locale.ROOT)
  val normalUri =
    "https://${getFPDetails(Key_Preferences.GET_FP_DETAILS_TAG)?.toLowerCase(Locale.ROOT)}.nowfloats.com"
  return if (rootAliasUri.isNullOrEmpty().not() && rootAliasUri != "null") {
    return if (isRemoveHttp && rootAliasUri!!.contains("http://")) rootAliasUri.replace(
      "http://",
      ""
    )
    else if (isRemoveHttp && rootAliasUri!!.contains("https://")) rootAliasUri.replace(
      "https://",
      ""
    ) else rootAliasUri
  } else normalUri
}


fun getLatestUpdatesTaxonomyFromServiceCode(category_code: String?): String? {
  return when (category_code) {
    "DOC", "HOS" -> "Latest Updates & Health Tips"
    "SPA", "SAL" -> "Latest Updates & Offers"
    "HOT" -> "Latest Updates, News & Events"
    "MFG" -> "Latest Updates & News"
    "CAF", "EDU" -> "Latest Updates & Tips"
    else -> "Latest Updates"
  }
}

fun isService(category_code: String?): Boolean {
  return when (category_code) {
    "SVC", "DOC", "HOS", "SPA", "SAL" -> true
    else -> false
  }
}

