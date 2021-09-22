package auth.google

import android.app.Activity
import android.content.Intent
import auth.google.constants.GoogleGraphPath
import auth.google.constants.GoogleGraphPath.GMB_SIGN_IN
import auth.google.constants.GoogleGraphPath.RC_SIGN_IN
import auth.google.graph.GoogleGraphManager
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
    val mGoogleSignInClient = GoogleGraphManager.getClient(activity, GoogleGraphPath.SERVER_CLIENT_ID, type)
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
    GoogleGraphManager.getClient(activity, GoogleGraphPath.SERVER_CLIENT_ID, type).signOut()
      .addOnCompleteListener(activity) { onGoogleLogout() }
  }

  fun revokeAccess(activity: Activity, type: String) {
    if (type == GMB_SIGN_IN && validClientID().not()) return
    GoogleGraphManager.getClient(activity, GoogleGraphPath.SERVER_CLIENT_ID, type).revokeAccess()
      .addOnCompleteListener(activity) { onRevokeAccess() }
  }

  fun validClientID(): Boolean {
    val message = GoogleGraphManager.validateServerClientID(GoogleGraphPath.SERVER_CLIENT_ID)
    if (message.isNotEmpty()) {
      onGoogleLoginError(ApiException(Status(400, message)))
      return false
    }
    return true
  }
}