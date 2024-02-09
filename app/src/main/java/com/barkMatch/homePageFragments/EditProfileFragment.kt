package com.barkMatch.homePageFragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.barkMatch.MainActivity
import com.barkMatch.databinding.FragmentEditProfileBinding
import com.barkMatch.models.Model
import com.barkMatch.models.User
import com.barkMatch.utils.ImagesUtils
import com.barkMatch.utils.SnackbarUtils
import com.barkMatch.utils.Validations

class EditProfileFragment : Fragment() {

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

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            ivEditProfileImg?.let { ImagesUtils.loadImage(uri, it) }
            selectedImageUri = uri
        }

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        pbEditProfile = binding.pbEditProfile
        btnEditProfile = binding.btnEditProfile
        btnLogout = binding.btnLogout
        backButton = binding.btnEditProfileBack
        etEditProfileFirstName = binding.etEditProfileFirstName
        etEditProfileLastName = binding.etEditProfileLastName
        etEditProfileDescription = binding.etEditProfileDescription
        etEditProfilePhoneNumber = binding.etEditProfilePhoneNumber
        ivEditProfileImg = binding.ivEditProfileImg
        ivEditProfileImg?.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        backButton?.setOnClickListener {
            findNavController().popBackStack()
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
                    findNavController().popBackStack() // navigate back on finish editing
                } else {
                    SnackbarUtils.showSnackbar(
                        view,
                        "Something went wrong, failed to save changes"
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
                requireActivity().finish()
            }
        }

        initUserDetails()

        return view
    }

    private fun initUserDetails() {
        val args: EditProfileFragmentArgs by navArgs()

        etEditProfileFirstName?.setText(args.firstName)
        etEditProfileLastName?.setText(args.lastName)
        etEditProfileDescription?.setText(args.description)
        etEditProfilePhoneNumber?.setText(args.phoneNumber)

        val userProfileImageUrl = args.profileImage
        if (userProfileImageUrl.isNotEmpty()) {
            ivEditProfileImg?.let { ImagesUtils.loadImage(userProfileImageUrl, it) }
        }
    }
}