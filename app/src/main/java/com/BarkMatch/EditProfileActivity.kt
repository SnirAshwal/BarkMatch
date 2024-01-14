package com.BarkMatch

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class EditProfileActivity : AppCompatActivity() {

    private var backButton: ImageButton? = null
    private var btnEditProfile: Button? = null
    private var btnLogout: Button? = null
    private var etEditProfileFirstName: EditText? = null
    private var etEditProfileLastName: EditText? = null
    private var etEditProfileDescription: EditText? = null
    private var etEditProfilePhoneNumber: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        btnEditProfile = findViewById(R.id.btnEditProfile)
        btnLogout = findViewById(R.id.btnLogout)
        backButton = findViewById(R.id.btnEditProfileBack)
        etEditProfileFirstName = findViewById(R.id.etEditProfileFirstName)
        etEditProfileLastName = findViewById(R.id.etEditProfileLastName)
        etEditProfileDescription = findViewById(R.id.etEditProfileDescription)
        etEditProfilePhoneNumber = findViewById(R.id.etEditProfilePhoneNumber)

        backButton?.setOnClickListener {
            finish()
        }

        btnEditProfile?.setOnClickListener {
            // TODO: write edit profile functionality
            finish() // navigate back on finish editing
        }

        btnLogout?.setOnClickListener {
            // TODO: write logout functionality

            // move to welcome page
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        val extras = intent.extras
        if (extras != null) {
            val value = extras.getString("userId")
            if (value != null) {
                initUserDetails(value.toInt())
            }
        }
    }

    fun initUserDetails(userId: Int) {
        // TODO: write init user details functionality

        etEditProfileFirstName?.setText("test")
        etEditProfileLastName?.setText("test")
        etEditProfileDescription?.setText("test")
        etEditProfilePhoneNumber?.setText("test")
    }
}