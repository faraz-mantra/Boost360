package com.boost.presignup

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.boost.presignup.utils.WebEngageController
import com.framework.webengageconstant.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.onboarding.nowfloats.constant.PreferenceConstant
import com.onboarding.nowfloats.managers.NavigatorManager
import kotlinx.android.synthetic.main.activity_sign_up_confirmation.*
import java.net.URL

class SignUpConfirmation : AppCompatActivity() {

  private var personEmail = ""
  private var personNumber = ""
  private var personName = ""
  private var profile_id = ""
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_sign_up_confirmation)

    WebEngageController.trackEvent(PS_ACCOUNT_CREATION_CONFIRMATION, ACCOUNT_CREATION_CONFIRMATION, NO_EVENT_VALUE)
    val currentFirebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    val bundle = intent.extras
    profile_id = bundle?.getString("profile_id") ?: ""
    personName = bundle?.getString("person_name") ?: ""
    personEmail = bundle?.getString("person_email") ?: ""
    personNumber = bundle?.getString("person_number") ?: ""

    if (personName.isEmpty() && currentFirebaseUser != null) {
      personName = currentFirebaseUser.displayName.toString()
    }
    welcome_user.text = getString(R.string.welcome) + " " + personName

    var profileUrl = intent.getStringExtra("profileUrl")
    if ((profileUrl == null || profileUrl.isEmpty()) && currentFirebaseUser != null) {
      profileUrl = currentFirebaseUser.photoUrl?.toString()
    } else
      if (profileUrl.isNullOrEmpty().not()) {
        Thread {
          val url = URL(profileUrl)
          val bmp: Bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
          Handler().post {
            userProfileImage.setImageBitmap(bmp)
          }
        }
      }

    set_up_business_profile.setOnClickListener {
      WebEngageController.trackEvent(PS_BUSINESS_CREATION_INITIATED, BUSINESS_CREATION_INITIATED, NO_EVENT_VALUE)
//      val intent = Intent(applicationContext, Class.forName("com.nowfloats.signup.UI.UI.PreSignUpActivityRia"))
//      intent.putExtra("profile_id", profile_id)
//      startActivity(intent)
      val editor = this.getSharedPreferences(PreferenceConstant.NOW_FLOATS_PREFS, 0).edit()
      editor?.putString("user_profile_id", profile_id)
      editor?.putString("person_name", personName)
      editor?.putString("person_email", personEmail)
      editor?.putString("person_number", personNumber)
      editor?.putBoolean("IsSignUpComplete", true)
      editor?.apply()
      NavigatorManager.startActivities(this@SignUpConfirmation)

    }
  }

  override fun onBackPressed() {
    // do not let the user go back
  }
}
