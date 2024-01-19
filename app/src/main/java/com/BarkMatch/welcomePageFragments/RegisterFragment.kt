package com.BarkMatch.welcomePageFragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.BarkMatch.R
import com.BarkMatch.models.Model
import com.BarkMatch.models.User
import com.BarkMatch.utils.ImagesUtils
import com.BarkMatch.utils.SnackbarUtils
import com.BarkMatch.utils.Validations
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation

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

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            imageView?.let { ImagesUtils.loadImage(uri, it) }
            selectedImageUri = uri
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        val backButton: ImageButton = view.findViewById(R.id.btnRegisterBack)
        backButton.setOnClickListener {
            findNavController(view).popBackStack()
        }

        imageView = view.findViewById(R.id.ivRegisterProfileImg);
        imageView?.setOnClickListener {
            openFileChooser()
        }

        etFirstName = view.findViewById(R.id.etRegisterFirstName)
        etLastName = view.findViewById(R.id.etRegisterLastName)
        etEmail = view.findViewById(R.id.etRegisterEmail)
        etPassword = view.findViewById(R.id.etRegisterPassword)
        etDescription = view.findViewById(R.id.etRegisterDescription)
        etPhoneNumber = view.findViewById(R.id.etRegisterPhoneNumber)

        registerBtn = view.findViewById(R.id.btnRegister)
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

                    // Register
                    activity?.let { context ->
                        Model.instance.registerUser(
                            context,
                            view,
                            etEmail?.text.toString(),
                            etPassword?.text.toString(),
                            newUser,
                            selectedImageUri
                        )
                    }
                }
            }
        }

        return view
    }

    private fun openFileChooser() {
        pickImageLauncher.launch("image/*")
    }
}