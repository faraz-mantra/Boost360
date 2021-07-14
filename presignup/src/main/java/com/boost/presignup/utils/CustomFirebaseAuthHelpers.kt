package com.boost.presignup.utils

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.boost.presignup.R
import com.boost.presignup.datamodel.Apis
import com.boost.presignup.datamodel.userprofile.*
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.framework.webengageconstant.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.gson.Gson
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

class CustomFirebaseAuthHelpers constructor(
  activity: Activity,
  listener: CustomFirebaseAuthListeners,
  fpId: String = ""
) {

  private var TAG = "CustomFirebaseAuthHelpers"
  private var currentActivity: Activity
  private var mAuth: FirebaseAuth
  private var listener: CustomFirebaseAuthListeners
  private var ApiService: Apis
  private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
  private lateinit var phoneVerificationId: String
  private lateinit var phoneVerificationTOken: String
  private lateinit var phoneNumber: String
  private var userFpId: String

  private var autoUserProfileCreateMode = true

  val RC_SIGN_IN = 1
  private var mGoogleSignInClient: GoogleSignInClient

  private var retrofit: Retrofit

  private fun FirebaseAuthHelpers() {}

  init {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//        .requestIdToken(activity.getString(R.string.default_web_client_id))
      .requestIdToken(activity.getString(R.string.server_client_id))
      .requestEmail()
      .build()
    retrofit = Retrofit.Builder()
      .baseUrl("https://api2.withfloats.com")
      .addConverterFactory(GsonConverterFactory.create())
      .build()
    this.currentActivity = activity
    this.listener = listener
    ApiService = retrofit.create(Apis::class.java)
    mAuth = FirebaseAuth.getInstance()
    mGoogleSignInClient = GoogleSignIn.getClient(activity, gso)
    this.userFpId = fpId

  }

  fun disableAutoUserProfileCreationMode() {
    autoUserProfileCreateMode = false
  }


  fun startFacebookLogin(facebook_login: LoginButton, callbackManager: CallbackManager) {
    // this.currentActivity = currentActivity;
    // facebook functionality
    facebook_login.setPermissions("email", "public_profile")
    facebook_login.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
      override fun onSuccess(loginResult: LoginResult) {
        Log.d(TAG, "facebook:onSuccess:$loginResult")
        handleFacebookAccessToken(loginResult.accessToken)
      }

      override fun onCancel() {
        Log.d(TAG, "facebook:onCancel")
      }

      override fun onError(error: FacebookException) {
        Log.d(TAG, "facebook:onError", error)
      }
    })
  }

  fun startGoogleLogin() {
    val signInIntent = mGoogleSignInClient.signInIntent
    currentActivity.startActivityForResult(signInIntent, RC_SIGN_IN)
  }

  fun googleLoginActivityResult(requestCode: Int, data: Intent) {
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
    }
  }

  private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {
    Log.d(TAG, "firebaseAuthWithGoogle:" + acct?.id)
    val credential = GoogleAuthProvider.getCredential(acct?.idToken, null)
    mAuth.signInWithCredential(credential)
      .addOnCompleteListener(currentActivity) { task ->
        if (task.isSuccessful) {
          // Sign in success, update UI with the signed-in user's information
          Log.d(TAG, "signInWithCredential:success")
          val user = mAuth.currentUser
          AuthorizedGoogleUser(user)
        } else {
          // If sign in fails, display a message to the user.
          listener.onFailure()
        }
      }
  }

  private fun AuthorizedGoogleUser(currentUser: FirebaseUser?) {
    var acct = GoogleSignIn.getLastSignedInAccount(currentActivity)
    if (acct != null) {
      val personName = acct.displayName.toString()
      val personFamilyName = acct.familyName.toString()
      val personEmail = acct.email.toString()
      val personIdToken = acct.idToken.toString()
      val personPhoto = acct.photoUrl.toString()
      mGoogleSignInClient.signOut()
      Log.d(TAG, "updateUI: photo = " + personPhoto)
      if (autoUserProfileCreateMode) {
        requestUserProfileAPI(personIdToken, personEmail, "", "", personName, "GOOGLE", personEmail)
      } else {
        verifyUserProfileAPI(personIdToken, "", "GOOGLE")
      }
    }
  }

  private fun handleFacebookAccessToken(token: AccessToken) {
    Log.d(TAG, "handleFacebookAccessToken:$token")
    val credential = FacebookAuthProvider.getCredential(token.token)
    mAuth.signInWithCredential(credential)
      .addOnCompleteListener(currentActivity) { task ->
        if (task.isSuccessful) {
          // Sign in success, update UI with the signed-in user's information
          Log.d(TAG, "signInWithCredential:success")
          val user = mAuth.currentUser
          AuthorizedFacebookUser(user)
        } else {
          // If sign in fails, display a message to the user.
          Log.w(TAG, "signInWithCredential:failure", task.exception)
          Toast.makeText(
            currentActivity,
            "SignIn Failed: " + task.exception?.message,
            Toast.LENGTH_LONG
          ).show()
          mAuth.signOut()
          LoginManager.getInstance().logOut()
        }
      }
  }

  fun AuthorizedFacebookUser(currentUser: FirebaseUser?) {
    if (currentUser != null) {
      val userInfoList = currentUser.providerData
      var userInfo: UserInfo? = null
      if (userInfoList.isNullOrEmpty().not() && userInfoList.size >= 2) userInfo = userInfoList[1]

      var personName =
        if (currentUser.displayName.isNullOrEmpty() && userInfo != null) userInfo.displayName else currentUser.displayName
      var personEmail =
        (if (currentUser.email.isNullOrEmpty() && userInfo != null) userInfo.email else currentUser.email)
          ?: ""
      var uid = personEmail
      val personIdToken = currentUser.getIdToken(true).toString()
      val personPhoto =
        if (currentUser.photoUrl == null && userInfo != null) userInfo.photoUrl else currentUser.photoUrl

      for (data in userInfoList) {
        if (data.providerId.contains("facebook")) {
          uid = data.uid
          personName = data.displayName
          break
        }
      }
      logoutFacebook()
      if (autoUserProfileCreateMode) {
        requestUserProfileAPI(
          personIdToken,
          personEmail,
          "",
          "",
          personName as String,
          "FACEBOOK",
          uid
        )
      } else {
        verifyUserProfileAPI(uid, "", "FACEBOOK")
      }
    }
  }

  fun logoutFacebook() {
    if (AccessToken.getCurrentAccessToken() == null) return
    GraphRequest(
      AccessToken.getCurrentAccessToken(),
      "/me/permissions/",
      null,
      HttpMethod.DELETE,
      GraphRequest.Callback {
        LoginManager.getInstance().logOut()
      }).executeAsync()
  }

  fun requestUserProfileAPI(
    personIdToken: String,
    email: String,
    userPassword: String,
    userMobile: String,
    personName: String,
    provider: String,
    loginKey: String
  ) {

    val userInfo = UserProfileRequest(
      personIdToken,
      "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21",
      loginKey,
      userPassword,
      ProfileProperties(email, userMobile, personName, userPassword), provider, null
    )

    if (userFpId == "") {
      ApiService.createUserProfile(userInfo).enqueue(object : Callback<UserProfileResponse> {
        override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
          listener.onFailure()
        }

        override fun onResponse(
          call: Call<UserProfileResponse>,
          response: Response<UserProfileResponse>
        ) {
          WebEngageController.initiateUserLogin(response.body()?.Result?.LoginId)
          WebEngageController.setUserContactAttributes(
            email,
            userMobile,
            personName,
            response.body()?.Result?.ClientId
          )
          WebEngageController.trackEvent(
            PS_ACCOUNT_CREATION_SUCCESS,
            ACCOUNT_CREATION_SUCCESS,
            NO_EVENT_VALUE
          )
//          SmartLookController.setUserAttributes(email, userMobile, personName, response.body()?.Result?.ClientId)
          listener.onSuccess(response.body(), loginKey)
        }
      })
    } else {
      //used from post login - account mapping scenarios
      userInfo.FpIds = arrayOf(userFpId)

      ApiService.connectUserProfile(userInfo)
        .enqueue(object : Callback<ConnectUserProfileResponse> {
          override fun onResponse(
            call: Call<ConnectUserProfileResponse>,
            response: Response<ConnectUserProfileResponse>
          ) {
            listener.onSuccess(response.body())
          }

          override fun onFailure(call: Call<ConnectUserProfileResponse>, t: Throwable) {
            listener.onFailure()
          }


        })
    }
  }

  fun verifyUserProfileAPI(loginKey: String, loginSecret: String, provider: String) {
    val userInfo = UserProfileVerificationRequest(
      "", provider,
      loginKey, loginSecret, "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21"
    )

    ApiService.verifyUserProfileAny(userInfo).enqueue(object : Callback<ResponseBody> {
      override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
        listener.onFailure()
      }

      override fun onResponse(call: Call<ResponseBody>, responseObj: Response<ResponseBody>) {
        if (responseObj.isSuccessful) {
          if (responseObj.body() != null) {
            try {
              val source: BufferedSource? = responseObj.body()?.source()
              source?.request(Long.MAX_VALUE)
              val buffer: Buffer? = source?.buffer
              val responseBodyString: String? =
                buffer?.clone()?.readString(Charset.forName("UTF-8"))
              val response: VerificationRequestResult? =
                Gson().fromJson(responseBodyString, VerificationRequestResult::class.java)
              if (response == null) {
                listener.onSuccess(VerificationRequestResult())
                return
              }
              WebEngageController.initiateUserLogin(response.loginId)
              WebEngageController.setUserContactAttributes(
                response.profileProperties?.userEmail,
                response.profileProperties?.userMobile,
                response.profileProperties?.userName,
                response.sourceClientId
              )
              WebEngageController.trackEvent(PS_LOGIN_SUCCESS, LOGIN_SUCCESS, NO_EVENT_VALUE)
//              SmartLookController.setUserAttributes(response.profileProperties?.userEmail, response.profileProperties?.userMobile, response.profileProperties?.userName,response.sourceClientId)
              listener.onSuccess(response)
            } catch (e: Exception) {
              listener.onSuccess(VerificationRequestResult())
            }
          } else {
            listener.onSuccess(VerificationRequestResult())
          }
        } else {
          listener.onFailure()
        }
      }
    })
  }

  fun startPhoneAuth(phoneNumber: String, phoneAuthListner: PhoneAuthListener) {
    this.phoneNumber = "+91" + phoneNumber
    callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
      override fun onVerificationFailed(p0: FirebaseException) {
        listener.onFailure()
      }

      override fun onVerificationCompleted(credential: PhoneAuthCredential) {
        Log.d(TAG, "onVerificationCompleted:$credential")
        signInWithPhoneAuthCredential(credential)
      }

      override fun onCodeSent(
        verificationId: String,
        token: PhoneAuthProvider.ForceResendingToken
      ) {
        super.onCodeSent(verificationId, token)
        phoneVerificationId = verificationId
        phoneVerificationTOken = token.toString()
        phoneAuthListner.onCodeSent()
      }
    }

    PhoneAuthProvider.getInstance().verifyPhoneNumber(
      this.phoneNumber, // Phone number to verify
      30, // Timeout duration
      TimeUnit.SECONDS, // Unit of timeout
      currentActivity, // Activity (for callback binding)
      callbacks
    ) // OnVerificationStateChangedCallbacks
  }

  fun phoneAuthVerification(code: String) {
    val credential = PhoneAuthProvider.getCredential(phoneVerificationId, code)
    signInWithPhoneAuthCredential(credential)
  }

  private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
    mAuth.signInWithCredential(credential)
      .addOnCompleteListener(currentActivity) { task ->
        if (task.isSuccessful) {
          // Sign in success, update UI with the signed-in user's information
          Log.d(TAG, "signInWithCredential:success")


          if (autoUserProfileCreateMode) {
            requestUserProfileAPI(
              "",
              "", "", phoneNumber, "", "OTP", phoneNumber
            )
          } else {
            verifyUserProfileAPI(phoneNumber, "", "OTP")
          }
          // ...
        } else {
          listener.onFailure()
          // Sign in failed, display a message and update the UI
          Log.w(TAG, "signInWithCredential:failure", task.exception)
          if (task.exception is FirebaseAuthInvalidCredentialsException) {
            // The verification code entered was invalid
          }
        }
      }
  }

  interface PhoneAuthListener {
    fun onCodeSent()
  }

}