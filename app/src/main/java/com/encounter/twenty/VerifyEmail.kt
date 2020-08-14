package com.encounter.twenty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_verify_email.*

class VerifyEmail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_email)

        verify_code_btn.setOnClickListener {
            startActivity(Intent(applicationContext, NewPassword::class.java))
            finish()
        }

    }
}