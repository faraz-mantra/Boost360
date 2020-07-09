package com.boost.presignup

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_sign_up_jio_id.*

class JioSignupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_jio_id)

        create_jio_secure_id_section.setOnClickListener{
            createJioSecureId()
        }
    }

    private fun createJioSecureId(){

    }
}
