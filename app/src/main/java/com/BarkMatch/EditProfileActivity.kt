package com.BarkMatch

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.BarkMatch.models.Model

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
            Model.instance.logoutUser(this)
        }

        val extras = intent.extras
        if (extras != null) {
            val value = extras.getString("userId")
            if (value != null) {
                initUserDetails(value)
            }
        }
    }

    fun initUserDetails(userId: String) {
        // TODO: write init user details functionality

        etEditProfileFirstName?.setText("test")
        etEditProfileLastName?.setText("test")
        etEditProfileDescription?.setText("test")
        etEditProfilePhoneNumber?.setText("test")
    }
}