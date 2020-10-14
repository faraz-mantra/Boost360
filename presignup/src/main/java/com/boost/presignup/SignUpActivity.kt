package com.boost.presignup

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.boost.presignup.datamodel.Apis
import com.boost.presignup.datamodel.userprofile.ProfileProperties
import com.boost.presignup.datamodel.userprofile.UserProfileRequest
import com.boost.presignup.datamodel.userprofile.UserProfileResponse
import com.boost.presignup.utils.Utils.hideSoftKeyBoard
import com.boost.presignup.utils.WebEngageController
import com.framework.utils.showKeyBoard
import com.google.firebase.auth.FirebaseAuth
import com.onboarding.nowfloats.ui.webview.WebViewTNCDialog
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.regex.Pattern


class SignUpActivity : AppCompatActivity() {
  var profileUrl = ""
  var email = ""
  var personName = ""
  var personFamilyName = ""
  var personIdToken = ""
  var provider = ""
  var userPassword = ""
  var userMobile = ""
  var registerWithFirebaseEmailProvider = true
  lateinit var retrofit: Retrofit
  lateinit var ApiService: Apis

  lateinit var mAuth: FirebaseAuth

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_sign_up)
    WebEngageController.trackEvent("PS_Signup Form Loaded", "Signup Form Loaded", "")
    mAuth = FirebaseAuth.getInstance()
    spannableString()
    if (intent != null && intent.hasExtra("provider")) {
      val intentProvider = intent.getStringExtra("provider") as String

      if (intentProvider.equals("GOOGLE")) {
        profileUrl = intent.getStringExtra("url") ?: ""
        email = intent.getStringExtra("email") ?: ""
        personName = intent.getStringExtra("person_name") ?: ""
        personFamilyName = intent.getStringExtra("personFamilyName") ?: ""
        personIdToken = intent.getStringExtra("personIdToken") ?: ""
        provider = intentProvider

        if (!email.contains('@'))
          email = ""

        user_email.setText(email)

        user_name.setText(personName)
        registerWithFirebaseEmailProvider = false
      } else if (intentProvider.equals("FACEBOOK")) {
        profileUrl = intent.getStringExtra("url") ?: ""
        email = intent.getStringExtra("email") ?: ""
        personName = intent.getStringExtra("person_name") ?: ""
        personIdToken = intent.getStringExtra("personIdToken") ?: ""
        provider = intentProvider

        if (!email.contains('@'))
          email = ""

        user_email.setText(email)

        user_name.setText(personName)

        registerWithFirebaseEmailProvider = false

      } else if (intentProvider.equals("EMAIL")) {
        provider = intentProvider
        registerWithFirebaseEmailProvider = true
      }

    }
    create_account_button.isVisible = true
    enableFormInput()
    retrofit = Retrofit.Builder()
        .baseUrl("https://api2.withfloats.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    ApiService = retrofit.create(Apis::class.java)


    back_button.setOnClickListener {
      finish()
    }

    user_name.addTextChangedListener {
      personName = it.toString()
    }

    user_email.addTextChangedListener {
      email = it.toString()
    }

    user_password.addTextChangedListener {
      userPassword = it.toString()
    }

    user_mobile.addTextChangedListener {
      userMobile = it.toString()
    }
    this.showKeyBoard(user_name)
    create_account_button.setOnClickListener {
      if (validateInput()) {
        openTNCDialog(true, "https://www.getboost360.com/tnc?src=android&stage=user_account_create", resources.getString(com.onboarding.nowfloats.R.string.boost360_terms_conditions))
        hideSoftKeyBoard(applicationContext, it)
      }
    }
  }

  private fun createUser() {
    Toast.makeText(applicationContext, "Processing...", Toast.LENGTH_SHORT).show()
    create_account_button.isVisible = false
    disableFormInput()
    if (registerWithFirebaseEmailProvider) {
      mAuth.createUserWithEmailAndPassword(email, userPassword)
          .addOnCompleteListener {
            if (it.isSuccessful) {
              Log.d("createUserProfile", ">>>> Successfull")
              registerUserProfileAPI()
            } else {
              Log.d("createUserProfile", ">>>> Failure")
              enableFormInput()
//              email = "" // Remove previous email data.
              create_account_button.isVisible = true
              Toast.makeText(applicationContext, "ERROR: " + it.exception!!.message, Toast.LENGTH_LONG).show()
              WebEngageController.trackEvent("PS_Account Creation Failed in Firebase " + provider, "Create User Failed in Firebase With " + provider, "")
            }
          }
    } else {
      registerUserProfileAPI()
    }
  }

  private fun createUserProfileFirebase(responseResult: UserProfileResponse?) {
    mAuth.createUserWithEmailAndPassword(email, userPassword)
        .addOnCompleteListener {
          if (it.isSuccessful) {
            Log.d("createUserProfile", ">>>> Successfull")
            WebEngageController.initiateUserLogin(responseResult?.Result?.LoginId)
            WebEngageController.setUserContactAttributes(email, userMobile, personName)
            WebEngageController.trackEvent("PS_Account Creation Success", "Account Creation Success", "")

            val intent = Intent(applicationContext, SignUpConfirmation::class.java)
            intent.putExtra("profileUrl", profileUrl)
            intent.putExtra("person_name", personName)
            intent.putExtra("profile_id", responseResult?.Result?.LoginId)
            startActivity(intent)
          } else {
            Log.d("createUserProfile", ">>>> Failure")
            enableFormInput()
//                email = "" // Remove previous email data.
            create_account_button.isVisible = true
            Toast.makeText(applicationContext, "ERROR: " + it.exception!!.message, Toast.LENGTH_LONG).show()
            WebEngageController.trackEvent("PS_Account Creation Failed in Firebase " + provider, "Create User Failed in Firebase With " + provider, "")
          }
        }
  }

  private fun createUserProfileAPINew() {
    Toast.makeText(applicationContext, "Processing...", Toast.LENGTH_SHORT).show()
    create_account_button.isVisible = false
    disableFormInput()

    val userInfo = UserProfileRequest(
        personIdToken,
        "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21",
        email,
        userPassword,
        ProfileProperties(email, userMobile, personName, userPassword),
        provider,
        null
    )
    ApiService.createUserProfile(userInfo).enqueue(object : Callback<UserProfileResponse> {

      override fun onResponse(call: Call<UserProfileResponse>, response: Response<UserProfileResponse>) {
        if (response.isSuccessful) {
          val responseResult: UserProfileResponse? = response.body()
          if (responseResult?.Result?.LoginId.isNullOrEmpty().not()) {
            if (registerWithFirebaseEmailProvider) {
              // Start Firebase registration here
              createUserProfileFirebase(responseResult)
            } else {
              // These 3 must happen when firebase creation is successful too
              WebEngageController.initiateUserLogin(responseResult?.Result?.LoginId)
              WebEngageController.setUserContactAttributes(email, userMobile, personName)
              WebEngageController.trackEvent("PS_Account Creation Success", "Account Creation Success", "")
            }
          } else {
//            email = "" // Remove previous email data.
            create_account_button.isVisible = true
            enableFormInput()
            Toast.makeText(applicationContext, applicationContext.getString(R.string.failed_create_user), Toast.LENGTH_SHORT).show()
          }
        } else {
          try {
            val error: Throwable = PreSignUpException(response.errorBody()?.string() ?: "")
            val reader = JSONObject(error.localizedMessage)
            val message: String = reader.getJSONObject("Error")?.getJSONObject("ErrorList")
                ?.getString("EXCEPTION") ?: applicationContext.getString(R.string.failed_create_user)
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
          } catch (e: Exception) {
            Toast.makeText(applicationContext, applicationContext.getString(R.string.failed_create_user), Toast.LENGTH_SHORT).show()
          }
//          email = "" // Remove previous email data.
          create_account_button.isVisible = true
          enableFormInput()
        }
      }

      override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
        Toast.makeText(applicationContext, "error >>" + t.message, Toast.LENGTH_LONG).show()
        WebEngageController.trackEvent("PS_Account Creation Failed", "Account Creation Failed", "")
//        email = "" // Remove previous email data.
        create_account_button.isVisible = true
        enableFormInput()
      }
    })
  }

  private fun disableFormInput() {
    user_name.isEnabled = false
    user_email.isEnabled = false
    user_password.isEnabled = false
    user_mobile.isEnabled = false
  }

  private fun enableFormInput() {
    user_name.isEnabled = true
    user_email.isEnabled = true
    user_password.isEnabled = true
    user_mobile.isEnabled = true
  }

  private fun validateInput(): Boolean {
    this.email = user_email.text.toString()

    if (user_name.text!!.isEmpty() || user_password.text!!.isEmpty() || user_mobile.text!!.isEmpty()) {
      Toast.makeText(applicationContext, "Please enter all values.", Toast.LENGTH_SHORT).show()
      return false
    }
    if (!isValidMail(email, allowEmpty = true)) {
      Toast.makeText(applicationContext, "Enter Valid EmailId.", Toast.LENGTH_SHORT).show()
      return false
    }
    if (!isValidMobile(userMobile)) {
      Toast.makeText(applicationContext, "Enter Valid Mobile No.", Toast.LENGTH_SHORT).show()
      return false
    }
    return true
  }

  private fun isValidMail(email: String, allowEmpty: Boolean = false): Boolean {
    if (allowEmpty && email.isNullOrEmpty()) {
//      registerWithFirebaseEmailProvider = false
      // Use the email template to allow creation of user using firebase
      this.email = "noemail-${userMobile}@noemail.com"
      return true
    }
    return Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
            "\\@" +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
            "(" +
            "\\." +
            "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
            ")+"
    ).matcher(email).matches()
  }

  private fun isValidMobile(phone: String): Boolean {
    return Pattern.compile(
        "^(?:(?:\\+|0{0,2})91(\\s*[\\-]\\s*)?|[0]?)?[789]\\d{9}\$")
        .matcher(phone).matches()
  }

  fun registerUserProfileAPI() {
    val userInfo = UserProfileRequest(
        personIdToken,
        "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21",
        email,
        userPassword,
        ProfileProperties(email, userMobile, personName, userPassword), provider, null)

    ApiService.createUserProfile(userInfo).enqueue(object : Callback<UserProfileResponse> {
      override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
        Toast.makeText(applicationContext, "error >>" + t.message, Toast.LENGTH_LONG).show()
        WebEngageController.trackEvent("PS_Account Creation Failed", "Account Creation Failed", "")
        create_account_button.isVisible = true
        enableFormInput()
      }

      override fun onResponse(call: Call<UserProfileResponse>, response: Response<UserProfileResponse>) {
        if (response.isSuccessful) {
          val responseResult: UserProfileResponse? = response.body()
          if (responseResult?.Result?.LoginId.isNullOrEmpty().not()) {
            WebEngageController.initiateUserLogin(responseResult?.Result?.LoginId)
            WebEngageController.setUserContactAttributes(email, userMobile, personName)
            WebEngageController.trackEvent("PS_Account Creation Success", "Account Creation Success", "")

            val intent = Intent(applicationContext, SignUpConfirmation::class.java)
            intent.putExtra("profileUrl", profileUrl)
            intent.putExtra("person_name", personName)
            intent.putExtra("profile_id", responseResult?.Result?.LoginId)
            startActivity(intent)
          } else {
            create_account_button.isVisible = true
            Toast.makeText(applicationContext, applicationContext.getString(R.string.failed_create_user), Toast.LENGTH_SHORT).show()
          }
        } else {
          try {
            val error: Throwable = PreSignUpException(response.errorBody()?.string() ?: "")
            val reader = JSONObject(error.localizedMessage)
            val message: String = reader.getJSONObject("Error")?.getJSONObject("ErrorList")
                ?.getString("EXCEPTION") ?: applicationContext.getString(R.string.failed_create_user)
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
          } catch (e: Exception) {
            Toast.makeText(applicationContext, applicationContext.getString(R.string.failed_create_user), Toast.LENGTH_SHORT).show()
          }
          create_account_button.isVisible = true
          enableFormInput()
        }
      }
    })
  }

  private fun openTNCDialog(isApiHit: Boolean, url: String, title: String) {
    WebViewTNCDialog().apply {
      setData(isApiHit, url, title)
      onClickType = { btnClickType(it) }
      show(this@SignUpActivity.supportFragmentManager, title)
    }
  }

  private fun btnClickType(type: String) {
    when (type) {
      WebViewTNCDialog.DECLINE -> {
      }
      // Old code (Create User in Firebase -> Create User with Boost API -> Sign Up)
//      WebViewTNCDialog.ACCEPT -> createUser()
      // New flow (Create User with Boost API -> Create User in Firebase -> Sign Up)
      WebViewTNCDialog.ACCEPT -> createUserProfileAPINew()
    }
  }

  private fun spannableString() {
    val ss = SpannableString(getString(R.string.terms_of_use_and_privacy_policy))
    val termsOfUseClicked: ClickableSpan = object : ClickableSpan() {
      override fun onClick(textView: View) {
        openTNCDialog(false, "https://www.getboost360.com/tnc?src=android&stage=presignup", resources.getString(R.string.boost360_terms_conditions))
      }

      override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.isUnderlineText = false
      }
    }
    val privacyPolicyClicked: ClickableSpan = object : ClickableSpan() {
      override fun onClick(textView: View) {
        openTNCDialog(false, "https://www.getboost360.com/privacy?src=android&stage=presignup", resources.getString(R.string.boost360_privacy_policy))
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
        ForegroundColorSpan(ContextCompat.getColor(this, R.color.common_text_color)),
        44,
        56,
        0
    )
    ss.setSpan(
        ForegroundColorSpan(ContextCompat.getColor(this, R.color.common_text_color)),
        61,
        ss.length,
        0
    )
    ss.setSpan(UnderlineSpan(), 44, 56, 0)
    ss.setSpan(UnderlineSpan(), 61, ss.length, 0)
    popup_login_text.text = ss
    popup_login_text.movementMethod = LinkMovementMethod.getInstance()
    popup_login_text.highlightColor = resources.getColor(android.R.color.transparent)
  }
}

