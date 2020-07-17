package com.boost.presignup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.boost.presignup.datamodel.Apis
import com.boost.presignup.datamodel.userprofile.ProfileProperties
import com.boost.presignup.datamodel.userprofile.UserProfileRequest
import com.boost.presignup.datamodel.userprofile.UserProfileResponse
import com.boost.presignup.utils.Utils.hideSoftKeyBoard
import com.boost.presignup.utils.WebEngageController
import com.google.firebase.auth.FirebaseAuth
import com.onboarding.nowfloats.ui.webview.WebViewTNCDialog
import kotlinx.android.synthetic.main.activity_sign_up.*
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

    if (intent != null && intent.hasExtra("provider")) {
      val intentProvider = intent.getStringExtra("provider") as String

      if (intentProvider.equals("GOOGLE")) {
        profileUrl = intent.getStringExtra("url")
        email = intent.getStringExtra("email")
        personName = intent.getStringExtra("person_name")
        personFamilyName = intent.getStringExtra("personFamilyName")
        personIdToken = intent.getStringExtra("personIdToken")
        provider = intentProvider

        if (!email.contains('@'))
          email = ""

        user_email.setText(email)

        user_name.setText(personName)
        registerWithFirebaseEmailProvider = false
      } else if (intentProvider.equals("FACEBOOK")) {
        profileUrl = intent.getStringExtra("url")
        email = intent.getStringExtra("email")
        personName = intent.getStringExtra("person_name")
        personIdToken = intent.getStringExtra("personIdToken")
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

    create_account_button.setOnClickListener {
      if (validateInput()) {
        openTNCDialog()
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
              create_account_button.isVisible = true
              Toast.makeText(applicationContext, "ERROR: " + it.exception!!.message, Toast.LENGTH_LONG).show()
              WebEngageController.trackEvent("PS_Account Creation Failed in Firebase " + provider, "Create User Failed in Firebase With " + provider, "")
            }
          }
    } else {
      registerUserProfileAPI()
    }
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
    if (user_name.text!!.isEmpty() || user_email.text!!.isEmpty() || user_password.text!!.isEmpty() || user_mobile.text!!.isEmpty()) {
      Toast.makeText(applicationContext, "Please enter all values.", Toast.LENGTH_SHORT).show()
      return false
    }
    if (!isValidMail(email)) {
      Toast.makeText(applicationContext, "Enter Valid EmailId.", Toast.LENGTH_SHORT).show()
      return false
    }
    if (!isValidMobile(userMobile)) {
      Toast.makeText(applicationContext, "Enter Valid Mobile No.", Toast.LENGTH_SHORT).show()
      return false
    }
    return true
  }

  private fun isValidMail(email: String): Boolean {
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
          Toast.makeText(applicationContext, "Profile not created, please try again.", Toast.LENGTH_SHORT).show()
        }
      }
    })
  }

  private fun openTNCDialog() {
    WebViewTNCDialog().apply {
      setUrl("https://www.getboost360.com/tnc?src=android&stage=user_account_create")
      onClickType = { btnClickType(it) }
      show(this@SignUpActivity.supportFragmentManager, "")
    }
  }

  private fun btnClickType(type: String) {
    when (type) {
      WebViewTNCDialog.DECLINE -> {
      }
      WebViewTNCDialog.ACCEPT -> createUser()
    }
  }
}

