package com.BarkMatch.welcomePageFragments

import android.content.Intent
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
import com.BarkMatch.HomeActivity
import com.BarkMatch.R

class RegisterFragment : Fragment() {

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            imageView?.setImageURI(uri)
        }

    private var imageView: ImageView? = null
    private var etFirstName: EditText? = null
    private var etLastName: EditText? = null
    private var etUsername: EditText? = null
    private var etPassword: EditText? = null
    private var etDescription: EditText? = null
    private var etPhoneNumber: EditText? = null
    private var registerBtn: Button? = null

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
        etUsername = view.findViewById(R.id.etRegisterUsername)
        etPassword = view.findViewById(R.id.etRegisterPassword)
        etDescription = view.findViewById(R.id.etRegisterDescription)
        etPhoneNumber = view.findViewById(R.id.etRegisterPhoneNumber)

        registerBtn = view.findViewById(R.id.btnRegister)
        registerBtn?.setOnClickListener {
            val intent = Intent(activity, HomeActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        return view
    }

    private fun openFileChooser() {
        pickImageLauncher.launch("image/*")
    }
}