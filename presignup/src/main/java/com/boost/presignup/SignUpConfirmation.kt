package com.boost.presignup

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.boost.presignup.utils.WebEngageController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_sign_up_confirmation.*
import java.net.URL


class SignUpConfirmation : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_confirmation)

        WebEngageController.trackEvent("PS_Account Creation Confirmation", "Account Creation Confirmation", "")
        val currentFirebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val profile_id = intent.getStringExtra("profile_id")

        var personName = intent.getStringExtra("person_name")
        if (personName.isEmpty()) {
            personName = currentFirebaseUser?.displayName
        }
        welcome_user.setText(getString(R.string.welcome) + " " + personName)


        var profileUrl = intent.getStringExtra("profileUrl")
        if (profileUrl == null || profileUrl.isEmpty()) {
            profileUrl = currentFirebaseUser?.photoUrl?.toString()
        } else
            if (!profileUrl.isEmpty()) {
                val url = URL(profileUrl)
                val bmp: Bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                userProfileImage.setImageBitmap(bmp)
            }

        set_up_business_profile.setOnClickListener {
            WebEngageController.trackEvent("PS_Business Creation Initiated", "Business Creation Initiated", "")
            val intent = Intent(applicationContext, Class.forName("com.nowfloats.signup.UI.UI.PreSignUpActivityRia"))
            intent.putExtra("profile_id", profile_id)
            startActivity(intent)
        }
    }
}
