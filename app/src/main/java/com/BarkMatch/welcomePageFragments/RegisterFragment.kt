package com.BarkMatch.welcomePageFragments

import android.app.Activity
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
import androidx.navigation.Navigation.findNavController
import com.BarkMatch.HomeActivity
import com.BarkMatch.databinding.FragmentRegisterBinding
import com.BarkMatch.models.Model
import com.BarkMatch.models.User
import com.BarkMatch.utils.ImagesUtils
import com.BarkMatch.utils.SnackbarUtils
import com.BarkMatch.utils.Validations

class RegisterFragment : Fragment() {

    private var imageView: ImageView? = null
    private var etFirstName: EditText? = null
    private var etLastName: EditText? = null
    private var etEmail: EditText? = null
    private var etPassword: EditText? = null
    private var etDescription: EditText? = null
    private var etPhoneNumber: EditText? = null
    private var registerBtn: Button? = null
    private var selectedImageUri: Uri? = null
    private var pbRegister: ProgressBar? = null

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            imageView?.let { ImagesUtils.loadImage(uri, it) }
            selectedImageUri = uri
        }

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view = binding.root

        pbRegister = binding.pbRegister

        val backButton: ImageButton = binding.btnRegisterBack
        backButton.setOnClickListener {
            findNavController(view).popBackStack()
        }

        imageView = binding.ivRegisterProfileImg
        imageView?.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        etFirstName = binding.etRegisterFirstName
        etLastName = binding.etRegisterLastName
        etEmail = binding.etRegisterEmail
        etPassword = binding.etRegisterPassword
        etDescription = binding.etRegisterDescription
        etPhoneNumber = binding.etRegisterPhoneNumber

        registerBtn = binding.btnRegister
        registerBtn?.setOnClickListener {
            // Validations
            if (!Validations.isEmailValid(etEmail?.text.toString())) {
                SnackbarUtils.showSnackbar(view, "Invalid email address")
                return@setOnClickListener
            }

            if (!Validations.isPasswordValid(etPassword?.text.toString())) {
                SnackbarUtils.showSnackbar(
                    view,
                    "Password must be at least 6 characters and contain both letters and numbers"
                )
                return@setOnClickListener
            }

            if (Validations.isFieldEmpty(etFirstName?.text.toString())) {
                SnackbarUtils.showSnackbar(view, "First name is required")
                return@setOnClickListener
            }

            if (Validations.isFieldEmpty(etLastName?.text.toString())) {
                SnackbarUtils.showSnackbar(view, "Last name is required")
                return@setOnClickListener
            }

            if (Validations.isFieldEmpty(etDescription?.text.toString())) {
                SnackbarUtils.showSnackbar(view, "Description is required")
                return@setOnClickListener
            }

            if (Validations.isFieldEmpty(etPhoneNumber?.text.toString())) {
                SnackbarUtils.showSnackbar(view, "Phone number is required")
                return@setOnClickListener
            }

            pbRegister?.visibility = View.VISIBLE

            // Register user
            Model.instance.isUserWithEmailExists(etEmail?.text.toString()) { userExists ->
                if (userExists) {
                    SnackbarUtils.showSnackbar(view, "User with this email already exists")
                } else {
                    val newUser = User(
                        etFirstName?.text.toString(),
                        etLastName?.text.toString(),
                        etPhoneNumber?.text.toString(),
                        etDescription?.text.toString(),
                        etEmail?.text.toString()
                    )

                    Model.instance.registerUser(
                        etEmail?.text.toString(),
                        etPassword?.text.toString(),
                        newUser,
                        selectedImageUri
                    ) { isSuccess ->
                        if (isSuccess) {
                            // User data saved successfully - moving to feed
                            val intent = Intent(context, HomeActivity::class.java)
                            context?.startActivity(intent)
                            (context as Activity).finish()
                        } else {
                            SnackbarUtils.showSnackbar(view, "Registration failed")
                        }
                    }
                }

                pbRegister?.visibility = View.GONE
            }
        }

        return view
    }
}