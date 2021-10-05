package com.boost.presignup.ui

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.boost.presignup.JioSignupActivity
import com.boost.presignup.R
import com.boost.presignup.SignUpActivity
import com.boost.presignup.utils.WebEngageController
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.framework.extensions.gone
import com.framework.extensions.visible
import com.framework.utils.BuildConfigUtil
import com.framework.webengageconstant.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import com.nowfloats.facebook.FacebookLoginHelper
import com.nowfloats.facebook.constants.FacebookGraphRequestType
import com.nowfloats.facebook.constants.FacebookPermissions
import com.nowfloats.facebook.graph.FacebookGraphManager
import com.nowfloats.facebook.models.BaseFacebookGraphResponse
import kotlinx.android.synthetic.main.curve_popup_layout.view.*

class PopUpDialogFragment : DialogFragment(), FacebookLoginHelper,
  FacebookGraphManager.GraphRequestUserAccountCallback {

  lateinit var root: View
  val RC_SIGN_IN = 1
  val TAG = "PopUpDialogFragment"
  lateinit var mAuth: FirebaseAuth
  lateinit var mGoogleSignInClient: GoogleSignInClient

  private val callbackManager = CallbackManager.Factory.create()
  lateinit var actionCodeSettings: ActionCodeSettings
  lateinit var popUpWebViewFragment: PopUpWebViewFragment

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setStyle(STYLE_NORMAL, R.style.FullScreenDialogStyle)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    root = inflater.inflate(R.layout.curve_popup_layout, container, false)
    //webview
    popUpWebViewFragment = PopUpWebViewFragment()
    //Login Spannable
    spannableString()
    // Initialize Firebase Auth
    mAuth = FirebaseAuth.getInstance()
    // Configure Google Sign In
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//        .requestIdToken(getString(R.string.default_web_client_id))
      .requestIdToken(BuildConfigUtil.getBuildConfigField("GOOGLE_SERVER_CLIENT_ID") ?: "")
      .requestEmail()
      .build()

    // Build a GoogleSignInClient with the options specified by gso.
    mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

    //email authentication
    actionCodeSettings = ActionCodeSettings.newBuilder()
      // URL you want to redirect back to. The domain (www.example.com) for this
      // URL must be whitelisted in the Firebase Console.
      .setUrl("boost.nowfloats.com")
      // This must be true
      .setHandleCodeInApp(true)
      .setIOSBundleId("com.example.ios")
      .setAndroidPackageName(
        "com.example.android",
        true, /* installIfNotAvailable */
        "12" /* minimumVersion */
      )
      .build()

    root.popup_layout.setOnClickListener {
      WebEngageController.trackEvent(
        PS_CLICKED_OUTLIDE_THE_POP_UP_AREA,
        CLICKED_OUTLIDE_THE_POP_UP_AREA,
        NO_EVENT_VALUE
      )
      dialog?.dismiss()
    }
    root.view.setOnClickListener {
    }

    root.google_button.setOnClickListener {
      googleSignIn()
    }
    root.email_button.setOnClickListener {
      createNewEmailSignUp()
    }

    root.jio_id_button.setOnClickListener {
      createNewJioSecureIdSignUp()
    }

    root.popup_login_text.setOnClickListener {

    }

    return root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    registerFacebookLoginCallback(this, callbackManager)
    root.facebook_button.setOnClickListener {
      loginWithFacebook(this, listOf(FacebookPermissions.public_profile, FacebookPermissions.email))
    }
  }

  private fun googleSignIn() {
    root.progress.visible()
    val signInIntent = mGoogleSignInClient.signInIntent
    startActivityForResult(signInIntent, RC_SIGN_IN)
  }


  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    root.progress.gone()
    // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
    if (requestCode == RC_SIGN_IN) {
      val task = GoogleSignIn.getSignedInAccountFromIntent(data)
      try {
        // Google Sign In was successful, authenticate with Firebase
        val account = task.getResult(ApiException::class.java)
        firebaseAuthWithGoogle(account)
      } catch (e: ApiException) {
        // Google Sign In failed, update UI appropriately
        Log.w(TAG, "Google sign in failed", e)
      }
    } else {
//      Pass the activity result back to the Facebook SDK
      callbackManager.onActivityResult(requestCode, resultCode, data)
    }
  }

  private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
    root.progress.visible()
    Log.d(TAG, "firebaseAuthWithGoogle:" + acct?.id)
    val credential = GoogleAuthProvider.getCredential(acct?.idToken, null)
    mAuth.signInWithCredential(credential)
      .addOnCompleteListener(requireActivity()) { task ->
        root.progress.gone()
        if (task.isSuccessful) {
          // Sign in success, update UI with the signed-in user's information
          Log.d(TAG, "signInWithCredential:success")
          val user = mAuth.currentUser
          AuthorizedGoogleUser(user, acct)
          WebEngageController.trackEvent(
            PS_AUTH_PROVIDER_SUCCESS_GOOGLE,
            PROVIDER_SUCCESS_GOOGLE,
            NO_EVENT_VALUE
          )
        } else {
          // If sign in fails, display a message to the user.
          Log.w(TAG, "signInWithCredential:failure", task.exception)
          Toast.makeText(
            context, "Authentication failed.",
            Toast.LENGTH_SHORT
          ).show()
          WebEngageController.trackEvent(
            PS_AUTH_PROVIDER_FAILED_GOOGLE,
            PROVIDER_FAILED_GOOGLE,
            NO_EVENT_VALUE
          )
        }
      }
  }

  fun spannableString() {
    val ss = SpannableString(getString(R.string.terms_of_use_and_privacy_policy))
    val termsOfUseClicked: ClickableSpan = object : ClickableSpan() {
      override fun onClick(textView: View) {
        if (popUpWebViewFragment.isAdded) return
        // navigate to sign up fragment
        // Toast.makeText(requireContext(),"Terms of process is clicked...",Toast.LENGTH_LONG).show()
        val args = Bundle()
        args.putString("link", "https://www.getboost360.com/tnc?src=android&stage=presignup")
        args.putString("title", "Boost360 - Terms & Conditions")
        popUpWebViewFragment.arguments = args
        popUpWebViewFragment.show(
          requireActivity().supportFragmentManager,
          "popUpWebViewFragment_tag"
        )
      }

      override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.isUnderlineText = false
      }
    }
    val privacyPolicyClicked: ClickableSpan = object : ClickableSpan() {
      override fun onClick(textView: View) {
        if (popUpWebViewFragment.isAdded) return
        // navigate to sign up fragment
        // Toast.makeText(requireContext(),"Privacy policy is clicked...",Toast.LENGTH_LONG).show()
        val args = Bundle()
        args.putString("link", "https://www.getboost360.com/privacy?src=android&stage=presignup")
        args.putString("title", "Boost360 - Privacy Policy")
        popUpWebViewFragment.arguments = args
        popUpWebViewFragment.show(
          requireActivity().supportFragmentManager,
          "popUpWebViewFragment_tag"
        )
      }

      override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.isUnderlineText = false
      }
    }
    //By creating Boost account, you agree to our Terms of use and Privacy Policy
    ss.setSpan(termsOfUseClicked, 44, 56, 0)
    ss.setSpan(privacyPolicyClicked, 61, ss.length, 0)
    ss.setSpan(
      ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.common_text_color)),
      44,
      56,
      0
    )
    ss.setSpan(
      ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.common_text_color)),
      61,
      ss.length,
      0
    )
    ss.setSpan(UnderlineSpan(), 44, 56, 0)
    ss.setSpan(UnderlineSpan(), 61, ss.length, 0)
    root.popup_login_text.text = ss
    root.popup_login_text.movementMethod = LinkMovementMethod.getInstance()
    root.popup_login_text.highlightColor = resources.getColor(android.R.color.transparent)
  }

  fun AuthorizedGoogleUser(currentUser: FirebaseUser?, acct: GoogleSignInAccount?) {
    if (currentUser != null) {
      val personName = if (currentUser.displayName.isNullOrEmpty()
          .not()
      ) currentUser.displayName else acct?.displayName
      val personEmail =
        if (currentUser.email.isNullOrEmpty().not()) currentUser.email else acct?.email
      val token = currentUser.getIdToken(true).toString()
      val personIdToken = if (token.isNotEmpty()) token else acct?.idToken
      val personPhoto = if (currentUser.photoUrl != null) currentUser.photoUrl else acct?.photoUrl

      mGoogleSignInClient.signOut()
      val intent = Intent(requireContext(), SignUpActivity::class.java)
      intent.putExtra("url", personPhoto)
      intent.putExtra("email", personEmail)
      intent.putExtra("person_name", personName)
      intent.putExtra("personIdToken", personIdToken)
      intent.putExtra("provider", "GOOGLE")
      startActivity(intent)
      dialog?.dismiss()
    }
  }

  fun AuthorizedFacebookUser(currentUser: FirebaseUser?) {
    if (currentUser != null) {
      logoutFacebook()
      val userInfoList = currentUser.providerData
      var userInfo: UserInfo? = null
      if (userInfoList.isNullOrEmpty().not() && userInfoList.size >= 2) userInfo = userInfoList[1]

      val personName =
        if (currentUser.displayName.isNullOrEmpty() && userInfo != null) userInfo.displayName else currentUser.displayName
      val personEmail =
        if (currentUser.email.isNullOrEmpty() && userInfo != null) userInfo.email else currentUser.email
      val personIdToken = currentUser.getIdToken(true).toString()
      val personPhoto =
        if (currentUser.photoUrl == null && userInfo != null) userInfo.photoUrl else currentUser.photoUrl

      Log.d(TAG, "updateUI: photo = " + personPhoto)

      val intent = Intent(requireContext(), SignUpActivity::class.java)
      intent.putExtra("url", personPhoto)
      intent.putExtra("email", personEmail)
      intent.putExtra("person_name", personName)
      intent.putExtra("personIdToken", personIdToken)
      intent.putExtra("provider", "FACEBOOK")
      startActivity(intent)
      dialog?.dismiss()
    }
  }

  private fun createNewJioSecureIdSignUp() {
    WebEngageController.trackEvent(
      PS_AUTH_PROVIDER_SUCCESS_JIO_ID,
      PROVIDER_SUCCESS_JIO_ID,
      NO_EVENT_VALUE
    )
    val intent = Intent(requireContext(), JioSignupActivity::class.java)
    startActivity(intent)
    dialog!!.dismiss()
  }

  private fun createNewEmailSignUp() {
    val intent = Intent(requireContext(), SignUpActivity::class.java)
    intent.putExtra("provider", "EMAIL")
    startActivity(intent)
    WebEngageController.trackEvent(
      PS_AUTH_PROVIDER_SUCCESS_EMAIL,
      PROVIDER_SUCCESS_EMAIL,
      NO_EVENT_VALUE
    )
    dialog!!.dismiss()
  }

  private fun handleFacebookAccessToken(token: AccessToken) {
    Log.d(TAG, "handleFacebookAccessToken:$token")
    val credential = FacebookAuthProvider.getCredential(token.token)
    mAuth.signInWithCredential(credential)
      .addOnCompleteListener(requireActivity()) { task ->
        if (task.isSuccessful) {
          // Sign in success, update UI with the signed-in user's information
          Log.d(TAG, "signInWithCredential:success")
          AuthorizedFacebookUser(mAuth.currentUser)
          WebEngageController.trackEvent(
            PS_AUTH_PROVIDER_SUCCESS_FACEBOOK,
            PROVIDER_SUCCESS_FACEBOOK,
            NO_EVENT_VALUE
          )
        } else {
          // If sign in fails, display a message to the user.
          Log.w(TAG, "signInWithCredential:failure", task.exception)
          Toast.makeText(
            context, "SignIn Failed: " + task.exception!!.message,
            Toast.LENGTH_LONG
          ).show()
          WebEngageController.trackEvent(
            PS_AUTH_PROVIDER_FAILED_FACEBOOK,
            PROVIDER_FAILED_FACEBOOK,
            NO_EVENT_VALUE
          )
          mAuth.signOut()
          LoginManager.getInstance().logOut()
        }
      }
  }

  override fun onStart() {
    super.onStart()
    val dialog = dialog
    if (dialog != null) {
      val width = ViewGroup.LayoutParams.MATCH_PARENT
      val height = ViewGroup.LayoutParams.MATCH_PARENT
      dialog.window?.setLayout(width, height)
    }
  }

  override fun onFacebookLoginSuccess(result: LoginResult?) {
    val accessToken = result?.accessToken ?: return
    handleFacebookAccessToken(accessToken)
  }

  override fun onFacebookLoginCancel() {
    Toast.makeText(activity, getString(R.string.login_cancel), Toast.LENGTH_SHORT).show()
  }

  override fun onFacebookLoginError(error: FacebookException?) {
    Toast.makeText(activity, error?.localizedMessage, Toast.LENGTH_SHORT).show()
  }

  override fun onCompleted(
    type: FacebookGraphRequestType,
    facebookGraphResponse: BaseFacebookGraphResponse?
  ) {
  }
}