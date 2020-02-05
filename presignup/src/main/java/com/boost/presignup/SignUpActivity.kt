package com.boost.presignup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.boost.presignup.datamodel.Apis
import com.boost.presignup.datamodel.userprofile.ProfileProperties
import com.boost.presignup.datamodel.userprofile.UserProfileRequest
import com.boost.presignup.datamodel.userprofile.UserProfileResponse
import com.boost.presignup.utils.Utils.hideSoftKeyBoard
import kotlinx.android.synthetic.main.activity_sign_up.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SignUpActivity : AppCompatActivity() {
    var profileUrl = ""
    var email = ""
    var personName = ""
    var personGivenName = ""
    var personFamilyName = ""
    var personIdToken = ""
    var userPassword = ""
    var userMobile = ""
    lateinit var retrofit: Retrofit
    lateinit var ApiService: Apis
    var passwordVisiblity = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)


        profileUrl = intent.getStringExtra("url")
        email = intent.getStringExtra("email")
        personName = intent.getStringExtra("person_name")
        personGivenName = intent.getStringExtra("personGivenName")
        personFamilyName = intent.getStringExtra("personFamilyName")
        personIdToken = intent.getStringExtra("personIdToken")


        retrofit = Retrofit.Builder()
                .baseUrl("https://api2.withfloats.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        ApiService = retrofit.create(Apis::class.java)

        user_email.setText(email)
        user_email.isClickable = false
        user_name.setText(personName)

        password_visiblity.setOnClickListener {
            if(passwordVisiblity){
                user_password.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                passwordVisiblity = false
            }else{
                user_password.inputType = InputType.TYPE_CLASS_TEXT
                passwordVisiblity = true
            }
        }

        back_button.setOnClickListener {
            finish()
        }
        
        user_name.addTextChangedListener {
            personName = it.toString()
        }

        user_password.addTextChangedListener {
            userPassword = it.toString()
        }

        user_mobile.addTextChangedListener {
            userMobile = it.toString()
        }


        create_account_button.setOnClickListener {
            hideSoftKeyBoard(applicationContext,it)
            if(validateInput()) {
                requestUserProfileAPI()
            }else{
                Toast.makeText(applicationContext,"Fields are Empty!!",Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun validateInput(): Boolean {
            if(user_email.text!!.isEmpty() && user_password.text!!.isEmpty() && user_mobile.text!!.isEmpty()){
                return false
            }
        return true
    }

    fun requestUserProfileAPI() {
        val userInfo = UserProfileRequest(
                personIdToken,
                "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21",
                email,
                userPassword,
                ProfileProperties(email, userMobile, personName, userPassword), "GOOGLE")

        ApiService.createUserProfile(userInfo).enqueue(object : Callback<UserProfileResponse> {
            override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                Toast.makeText(applicationContext, "error >>" + t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<UserProfileResponse>, response: Response<UserProfileResponse>) {
                val intent = Intent(applicationContext, SignUpConfirmation::class.java)
                intent.putExtra("profileUrl", profileUrl)
                intent.putExtra("person_name", personName)
                startActivity(intent)
            }
        })
    }

}

