package com.boost.presignup

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_sign_up_confirmation.*
import java.net.URL


class SignUpConfirmation : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_confirmation)
        val personName = intent.getStringExtra("person_name")
        val temp = getString(R.string.welcome) + " " + personName
        welcome_user.setText(temp)
        val profileUrl = intent.getStringExtra("profileUrl")
        if (!profileUrl.isEmpty()) {
            val url = URL(profileUrl)
            val bmp: Bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            userProfileImage.setImageBitmap(bmp)
        }
//        userProfileImage.setImageURI(Uri.parse(profileUrl))
        set_up_business_profile.setOnClickListener {
            val intent = Intent(applicationContext, Class.forName("com.nowfloats.signup.UI.UI.RiaChatInitActivity"))
            startActivity(intent)
        }
    }
}
