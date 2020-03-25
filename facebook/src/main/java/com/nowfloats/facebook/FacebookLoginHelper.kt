package com.nowfloats.facebook

import android.app.Activity
import androidx.fragment.app.Fragment
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.nowfloats.facebook.constants.FacebookPermissions

interface FacebookLoginHelper {

  fun onFacebookLoginSuccess(result: LoginResult?)

  fun onFacebookLoginCancel()

  fun onFacebookLoginError(error: FacebookException?)

  fun registerFacebookLoginCallback(fragment: Fragment, callbackManager: CallbackManager) {
    LoginManager.getInstance().registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
              override fun onSuccess(result: LoginResult?) {
                onFacebookLoginSuccess(result)
              }

              override fun onCancel() {
                onFacebookLoginCancel()
              }

              override fun onError(error: FacebookException?) {
                onFacebookLoginError(error)
              }

            })

  }

  /***
   * @param permissions
   * @see <a href="https://developers.facebook.com/docs/facebook-login/android?sdk=maven">Facebook Login for Android - Quickstart</a>
   * @see <a href="https://developers.facebook.com/docs/facebook-login/permissions/overview">Permissions with Facebook Login</a>
   */
  fun loginWithFacebook(fragment: Fragment, permissions: List<FacebookPermissions>, arePublishPermissions: Boolean = false) {
    if (arePublishPermissions) LoginManager.getInstance().logInWithPublishPermissions(fragment, permissions.map { it.name })
    else LoginManager.getInstance().logInWithReadPermissions(fragment, permissions.map { it.name })
  }

  /***
   * @param permissions
   * @see <a href="https://developers.facebook.com/docs/facebook-login/android?sdk=maven">Facebook Login for Android - Quickstart</a>
   * @see <a href="https://developers.facebook.com/docs/facebook-login/permissions/overview">Permissions with Facebook Login</a>
   */
  fun loginWithFacebook(activity: Activity, permissions: List<FacebookPermissions>, arePublishPermissions: Boolean = false) {
    if (arePublishPermissions) LoginManager.getInstance().logInWithPublishPermissions(activity, permissions.map { it.name })
    else LoginManager.getInstance().logInWithReadPermissions(activity, permissions.map { it.name })
  }
}