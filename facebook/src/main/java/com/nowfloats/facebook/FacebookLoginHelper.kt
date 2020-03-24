package com.nowfloats.facebook

import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton

interface FacebookLoginHelper {

  fun getFacebookLoginButton(): LoginButton?

  fun onFacebookLoginSuccess(result: LoginResult?)

  fun onFacebookLoginCancel()

  fun onFacebookLoginError(error: FacebookException?)

  /***
   * @param callbackManager
   * @param permissions
   * @see <a href="https://developers.facebook.com/docs/facebook-login/android?sdk=maven">Facebook Login for Android - Quickstart</a>
   * @see <a href="https://developers.facebook.com/docs/facebook-login/permissions/overview">Permissions with Facebook Login</a>
   */
  fun registerFacebookLogin(callbackManager: CallbackManager, permissions: List<String>) {
    getFacebookLoginButton()?.setPermissions(permissions)
    getFacebookLoginButton()?.registerCallback(
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
}