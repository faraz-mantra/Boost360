package auth.google.model

import android.accounts.Account
import java.io.Serializable

data class GoogleSignInResponse(
  val googleId: String? = null,
  val googleFirstName: String? = null,
  val googleLastName: String? = null,
  val googleEmail: String? = null,
  val googleProfilePicURL: String? = null,
  val googleIdToken: String? = null,
  val account: Account? = null
) : Serializable