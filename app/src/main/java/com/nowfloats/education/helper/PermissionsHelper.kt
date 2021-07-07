package com.nowfloats.education.helper

import android.app.Activity
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

class PermissionsHelper {
  private var activity: Activity? = null
  private var fragment: Fragment? = null
  private var permissions: Array<String>? = null
  private var shouldShowRationale: Boolean = false
  private var rationalePermissions: HashMap<String, String> = HashMap()
  private var listener: RuntimePermissionListener? = null

  constructor(activity: Activity, permissions: Array<String>) {
    this.activity = activity
    this.permissions = permissions
  }

  /*constructor(fragment: Fragment, permissions: Array<String>) {
      this.fragment = fragment
      this.permissions = permissions
  }*/

  constructor(activity: Activity, permissions: HashMap<String, String>) {
    this.activity = activity
    this.rationalePermissions = permissions
  }

  /*constructor(fragment: Fragment, permissions: HashMap<String, String>) {
      this.fragment = fragment
      this.rationalePermissions = permissions
  }*/

  private val shouldAskPermission: Boolean =
    android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M

  fun askPermission(listener: RuntimePermissionListener) {
    this.listener = listener
    if (shouldAskPermission) {
      checkPermissions()
    } else {
      listener.onGranted()
    }
  }

  fun askPermission(listener: RuntimePermissionListener, shouldShowRationale: Boolean) {
    this.listener = listener
    this.shouldShowRationale = shouldShowRationale
    if (shouldAskPermission) {
      checkRationalePermission()
    } else {
      listener.onGranted()
    }
  }

  private fun checkPermissions() {
    val permissionsNeeded = mutableListOf<String>()
    permissions?.forEach {
      when {
        activity?.checkSelfPermissionCompat(it) != PackageManager.PERMISSION_GRANTED ->
          permissionsNeeded.add(it)
      }
    }
    when {
      permissionsNeeded.size > 0 -> requestPermissions(permissionsNeeded)
      else -> listener?.onGranted()
    }
  }

  private fun checkRationalePermission() {
    val permissionsNeeded = mutableListOf<String>()
    rationalePermissions.forEach {
      when {
        activity?.checkSelfPermissionCompat(it.key) != PackageManager.PERMISSION_GRANTED ->
          permissionsNeeded.add(it.key)
      }
    }
    when {
      permissionsNeeded.size > 0 -> requestPermissions(permissionsNeeded)
      else -> listener?.onGranted()
    }
  }

  private fun requestPermissions(permissions: MutableList<String>) {
    activity?.requestPermissionsCompat(
      permissions.toTypedArray(),
      PERMISSION_REQUEST
    )
  }

  fun onRequestPermissionResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
  ) {
    if (requestCode == PERMISSION_REQUEST) {
      var count = 0
      for (p in permissions.indices) {
        if (grantResults[p] != PackageManager.PERMISSION_GRANTED) {
          count += 1
          if (shouldShowRationale) {
            // This condition only becomes true if the user has denied the permission previously
            if (activity?.shouldShowRequestPermissionRationaleCompat(
                rationalePermissions.keys.toTypedArray()[p]
              )!!
            ) {
              val showRationale =
                rationalePermissions.getValue(rationalePermissions.keys.toTypedArray()[p])
              listener?.onShowRationale(showRationale)
              break
            }
          } else {
            listener?.onDenied()
          }
        }
      }
      if (count == 0) listener?.onGranted()
    }
  }

  companion object {
    private const val PERMISSION_REQUEST = 499
  }
}

interface RuntimePermissionListener {
  fun onGranted()
  fun onDenied()
  fun onShowRationale(rationale: String)
}

fun AppCompatActivity.checkSelfPermissionCompat(permission: String) =
  ActivityCompat.checkSelfPermission(this, permission)

fun AppCompatActivity.shouldShowRequestPermissionRationaleCompat(permission: String) =
  ActivityCompat.shouldShowRequestPermissionRationale(this, permission)

fun AppCompatActivity.requestPermissionsCompat(
  permissionsArray: Array<String>,
  requestCode: Int
) {
  ActivityCompat.requestPermissions(this, permissionsArray, requestCode)
}


fun Activity.checkSelfPermissionCompat(permission: String) =
  ActivityCompat.checkSelfPermission(this, permission)

fun Activity.shouldShowRequestPermissionRationaleCompat(permission: String) =
  ActivityCompat.shouldShowRequestPermissionRationale(this, permission)

fun Activity.requestPermissionsCompat(
  permissionsArray: Array<String>,
  requestCode: Int
) {
  ActivityCompat.requestPermissions(this, permissionsArray, requestCode)
}

fun Fragment.checkSelfPermissionCompat(permission: String) =
  ActivityCompat.checkSelfPermission(activity!!, permission)

fun Fragment.shouldShowRequestPermissionRationaleCompat(permission: String) =
  ActivityCompat.shouldShowRequestPermissionRationale(activity!!, permission)

fun Fragment.requestPermissionsCompat(
  permissionsArray: Array<String>,
  requestCode: Int
) {
  ActivityCompat.requestPermissions(activity!!, permissionsArray, requestCode)
}