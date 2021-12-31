package auth.google

import android.app.Activity
import android.content.Intent
import auth.google.constants.GoogleGraphPath
import auth.google.constants.GoogleGraphPath.GMB_SIGN_IN
import auth.google.constants.GoogleGraphPath.RC_SIGN_IN
import auth.google.graph.GoogleGraphManager
import com.framework.utils.BuildConfigUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status

interface GoogleLoginHelper {

  fun onGoogleLoginSuccess(result: GoogleSignInAccount?)

  fun onGoogleLoginError(error: ApiException?)
  fun onGoogleLogout() {

  }

  fun onRevokeAccess() {

  }

  fun googleLoginCallback(activity: Activity, type: String) {
    if (type == GMB_SIGN_IN && validClientID().not()) return
    val clientIdGoogle=  BuildConfigUtil.getBuildConfigField("GOOGLE_SERVER_CLIENT_ID") ?: ""
    val mGoogleSignInClient = GoogleGraphManager.getClient(activity, clientIdGoogle, type)
    val signInIntent = mGoogleSignInClient.signInIntent
    activity.startActivityForResult(signInIntent, RC_SIGN_IN)
  }


  fun handleGoogleSignInResult(data: Intent?) {
    val completedTask = GoogleSignIn.getSignedInAccountFromIntent(data)
    try {
      val account = completedTask.getResult(ApiException::class.java)
      onGoogleLoginSuccess(account)
    } catch (e: ApiException) {
      onGoogleLoginError(e)
    }
  }

  fun logoutGoogle(activity: Activity, type: String) {
    if (type == GMB_SIGN_IN && validClientID().not()) return
    val clientIdGoogle=  BuildConfigUtil.getBuildConfigField("GOOGLE_SERVER_CLIENT_ID") ?: ""
    GoogleGraphManager.getClient(activity, clientIdGoogle, type).signOut()
      .addOnCompleteListener(activity) { onGoogleLogout() }
  }

  fun revokeAccess(activity: Activity, type: String) {
    if (type == GMB_SIGN_IN && validClientID().not()) return
    val clientIdGoogle=  BuildConfigUtil.getBuildConfigField("GOOGLE_SERVER_CLIENT_ID") ?: ""
    GoogleGraphManager.getClient(activity, clientIdGoogle, type).revokeAccess()
      .addOnCompleteListener(activity) { onRevokeAccess() }
  }

  fun validClientID(): Boolean {
    val clientIdGoogle=  BuildConfigUtil.getBuildConfigField("GOOGLE_SERVER_CLIENT_ID") ?: ""
    val message = GoogleGraphManager.validateServerClientID(clientIdGoogle)
    if (message.isNotEmpty()) {
      onGoogleLoginError(ApiException(Status(400, message)))
      return false
    }
    return true
  }
}