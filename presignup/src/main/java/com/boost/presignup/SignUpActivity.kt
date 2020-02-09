package com.boost.presignup

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.boost.presignup.datamodel.Apis
import com.boost.presignup.datamodel.userprofile.ProfileProperties
import com.boost.presignup.datamodel.userprofile.UserProfileRequest
import com.boost.presignup.datamodel.userprofile.UserProfileResponse
import com.boost.presignup.utils.Utils.hideSoftKeyBoard
import com.google.firebase.auth.FirebaseAuth
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
    lateinit var retrofit: Retrofit
    lateinit var ApiService: Apis
    var passwordVisiblity = false

    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        mAuth = FirebaseAuth.getInstance();

        if (intent.hasExtra("provider")) {
            if (intent.getStringExtra("provider").equals("GOOGLE")) {
                profileUrl = intent.getStringExtra("url")
                email = intent.getStringExtra("email")
                personName = intent.getStringExtra("person_name")
                personFamilyName = intent.getStringExtra("personFamilyName")
                personIdToken = intent.getStringExtra("personIdToken")
                provider = intent.getStringExtra("provider")

                user_email.setText(email)
                user_email.isClickable = false
                user_email.isFocusable = false

                user_name.setText(personName)
            } else if (intent.getStringExtra("provider").equals("FACEBOOK")) {
                profileUrl = intent.getStringExtra("url")
                email = intent.getStringExtra("email")
                personName = intent.getStringExtra("person_name")
                personIdToken = intent.getStringExtra("personIdToken")
                provider = intent.getStringExtra("provider")

                user_email.setText(email)
                user_email.isClickable = false
                user_email.isFocusable = false
                user_name.setText(personName)

            } else if (intent.getStringExtra("provider").equals("EMAIL")) {

                provider = intent.getStringExtra("provider")


            }
        }


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
            hideSoftKeyBoard(applicationContext, it)
            if (validateInput()) {
                mAuth.createUserWithEmailAndPassword(email, userPassword)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Log.d("createUserProfile", ">>>> Successfull")
                                requestUserProfileAPI()
                            } else {
                                Log.d("createUserProfile", ">>>> Failure")
                                Toast.makeText(applicationContext, "ERROR: " + it.exception!!.message, Toast.LENGTH_LONG).show()
                            }
                        }
            }
        }

    }

    private fun validateInput(): Boolean {
        if (user_name.text!!.isEmpty() || user_email.text!!.isEmpty() || user_password.text!!.isEmpty() || user_mobile.text!!.isEmpty()) {
            Toast.makeText(applicationContext, "Fields are Empty!!", Toast.LENGTH_SHORT).show()
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
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private fun isValidMobile(phone: String): Boolean {
        val MOBILE_STRING = ("^(?:(?:\\+|0{0,2})91(\\s*[\\-]\\s*)?|[0]?)?[789]\\d{9}\$")
        return Pattern.compile(MOBILE_STRING).matcher(phone).matches()
    }

    fun requestUserProfileAPI() {
        val userInfo = UserProfileRequest(
                personIdToken,
                "2FA76D4AFCD84494BD609FDB4B3D76782F56AE790A3744198E6F517708CAAA21",
                email,
                userPassword,
                ProfileProperties(email, userMobile, personName, userPassword), provider, null)

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

