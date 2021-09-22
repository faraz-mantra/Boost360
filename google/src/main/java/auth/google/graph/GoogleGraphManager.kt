package auth.google.graph

import android.app.Activity
import auth.google.constants.GoogleGraphPath.GMBScope
import auth.google.constants.GoogleGraphPath.GMB_SIGN_IN
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope

object GoogleGraphManager {


  fun getClient(activity: Activity, serverClientId: String, type: String = GMB_SIGN_IN): GoogleSignInClient {
    return if (type == GMB_SIGN_IN) getGMBClient(activity, serverClientId) else getGoogleClient(activity)
  }

  //TODO for Google my business allow scope
  private fun getGMBClient(activity: Activity, serverClientId: String): GoogleSignInClient {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
      .requestScopes(Scope(GMBScope))
      .requestIdToken(serverClientId)
      .requestServerAuthCode(serverClientId, true)
      .requestEmail()
      .build()
    return GoogleSignIn.getClient(activity, gso)
  }

  //TODO for Google SignIn client
  private fun getGoogleClient(activity: Activity): GoogleSignInClient {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
      .requestEmail()
      .build()
    return GoogleSignIn.getClient(activity, gso)
  }

  fun validateServerClientID(serverClientId: String): String {
    val suffix = ".apps.googleusercontent.com"
    if (!serverClientId.trim { it <= ' ' }.endsWith(suffix)) {
      return "Invalid server client ID in strings.xml, must end with $suffix"
    }
    return ""
  }

}