package com.barkMatch

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.barkMatch.databinding.ActivityEditProfileBinding
import com.barkMatch.models.Model
import com.barkMatch.models.User
import com.barkMatch.utils.ImagesUtils
import com.barkMatch.utils.SnackbarUtils
import com.barkMatch.utils.Validations

class EditProfileActivity : AppCompatActivity() {

    private var backButton: ImageButton? = null
    private var btnEditProfile: Button? = null
    private var btnLogout: Button? = null
    private var etEditProfileFirstName: EditText? = null
    private var etEditProfileLastName: EditText? = null
    private var etEditProfileDescription: EditText? = null
    private var etEditProfilePhoneNumber: EditText? = null
    private var ivEditProfileImg: ImageView? = null
    private var selectedImageUri: Uri? = null
    private var pbEditProfile: ProgressBar? = null

    private var binding: ActivityEditProfileBinding? = null

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            ivEditProfileImg?.let { ImagesUtils.loadImage(uri, it) }
            selectedImageUri = uri
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        pbEditProfile = binding?.pbEditProfile
        btnEditProfile = binding?.btnEditProfile
        btnLogout = binding?.btnLogout
        backButton = binding?.btnEditProfileBack
        etEditProfileFirstName = binding?.etEditProfileFirstName
        etEditProfileLastName = binding?.etEditProfileLastName
        etEditProfileDescription = binding?.etEditProfileDescription
        etEditProfilePhoneNumber = binding?.etEditProfilePhoneNumber
        ivEditProfileImg = binding?.ivEditProfileImg
        ivEditProfileImg?.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        backButton?.setOnClickListener {
            finish()
        }

        btnEditProfile?.setOnClickListener {
            if (Validations.isFieldEmpty(etEditProfileFirstName?.text.toString())) {
                SnackbarUtils.showSnackbar(view, "First name is required")
                return@setOnClickListener
            }

            if (Validations.isFieldEmpty(etEditProfileLastName?.text.toString())) {
                SnackbarUtils.showSnackbar(view, "Last name is required")
                return@setOnClickListener
            }

            if (Validations.isFieldEmpty(etEditProfileDescription?.text.toString())) {
                SnackbarUtils.showSnackbar(view, "Description is required")
                return@setOnClickListener
            }

            if (Validations.isFieldEmpty(etEditProfilePhoneNumber?.text.toString())) {
                SnackbarUtils.showSnackbar(view, "Phone number is required")
                return@setOnClickListener
            }

            val user = User(
                etEditProfileFirstName?.text.toString(),
                etEditProfileLastName?.text.toString(),
                etEditProfilePhoneNumber?.text.toString(),
                etEditProfileDescription?.text.toString()
            )

            pbEditProfile?.visibility = View.VISIBLE
            Model.instance.editUserDetails(user, selectedImageUri) { isSuccess ->
                if (isSuccess) {
                    finish() // navigate back on finish editing
                } else {
                    SnackbarUtils.showSnackbar(
                        view,
                        "Something went went wrong, failed to save changes"
                    )
                }

                pbEditProfile?.visibility = View.GONE
            }
        }

        btnLogout?.setOnClickListener {
            Model.instance.logoutUser {
                // Go back to the welcome page
                val intent = Intent(view.context, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        initUserDetails()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun initUserDetails() {
        etEditProfileFirstName?.setText(intent?.extras?.let { EditProfileActivityArgs.fromBundle(it).firstName })
        etEditProfileLastName?.setText(intent?.extras?.let { EditProfileActivityArgs.fromBundle(it).lastName })
        etEditProfileDescription?.setText(intent?.extras?.let {
            EditProfileActivityArgs.fromBundle(
                it
            ).description
        })
        etEditProfilePhoneNumber?.setText(intent?.extras?.let {
            EditProfileActivityArgs.fromBundle(
                it
            ).phoneNumber
        })

        val userProfileImageUrl =
            intent?.extras?.let { EditProfileActivityArgs.fromBundle(it).profileImage } ?: ""
        if (userProfileImageUrl.isNotEmpty()) {
            ivEditProfileImg?.let { ImagesUtils.loadImage(userProfileImageUrl, it) }
        }
    }
}