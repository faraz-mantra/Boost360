package com.framework.helper

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import com.framework.base.BaseActivity
import java.io.Serializable

class Navigator(private val activity: BaseActivity<*, *>) {

  fun startActivity(intent: Intent) {
    activity.startActivity(intent)
  }

  fun startActivity(action: String) {
    activity.startActivity(Intent(action))
  }

  fun startActivity(action: String, uri: Uri) {
    activity.startActivity(Intent(action, uri))
  }

  @JvmOverloads
  fun startActivity(activityClass: Class<out Activity?>, args: Bundle? = null) {
    val activity = activity
    val intent = Intent(activity, activityClass)
    args?.let { intent.putExtras(it) }
    activity.startActivity(intent)
  }

  fun startActivity(activityClass: Class<out Activity?>, args: Parcelable?) {
    val activity = activity
    val intent = Intent(activity, activityClass)
    if (args != null) intent.putExtra(EXTRA_ARGS, args)
    activity.startActivity(intent)
  }



  fun getExtrasBundle(intent: Intent): Bundle? {
    return if (intent.hasExtra(EXTRA_ARGS)) intent.getBundleExtra(EXTRA_ARGS) else Bundle()
  }

  fun startActivityForResult(intent: Intent?, requestCode: Int) {
    activity.startActivityForResult(intent, requestCode)
  }

  fun startActivityForResult(activityClass: Class<out Activity?>, requestCode: Int, flags: Int) {
    val intent = Intent(activity, activityClass)
    intent.flags = flags
    startActivityForResult(intent, requestCode)
  }

  fun replaceFragment(@IdRes containerId: Int, fragment: Fragment, args: Bundle?) {
    if (args != null) fragment.arguments = args
    val ft = fragment.requireActivity().supportFragmentManager.beginTransaction().replace(containerId, fragment, null)
    ft.commit()
    fragment.requireFragmentManager().executePendingTransactions()
  }

  fun replaceFragment(containerId: Int, fragment: android.app.Fragment, args: Bundle?) {
    if (args != null) fragment.arguments = args
    val ft = activity.fragmentManager.beginTransaction().replace(containerId, fragment, null)
    ft.commit()
    activity.fragmentManager.executePendingTransactions()
  }

  fun startActivityFinish(activityClass: Class<out Activity?>) {
    startActivity(activityClass, null)
    activity.finish()
  }

  fun startActivityFinish(activityClass: Class<out Activity?>, args: Bundle?) {
    startActivity(activityClass, args)
    activity.finish()
  }

  @JvmOverloads
  fun clearBackStackAndStartNextActivity(activityClass: Class<out Activity?>, args: Bundle? = null) {
    val intent = Intent(activity, activityClass)
    if (args != null) intent.putExtras(args)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(intent)
    activity.finish()
  }

  companion object {
    const val EXTRA_ARGS = "_args"
  }

}
