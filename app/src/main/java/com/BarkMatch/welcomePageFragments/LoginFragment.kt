package com.BarkMatch.welcomePageFragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.BarkMatch.HomeActivity
import com.BarkMatch.R
import com.BarkMatch.models.Model
import com.BarkMatch.utils.SnackbarUtils
import com.BarkMatch.utils.Validations

class LoginFragment : Fragment() {

    private var etEmail: EditText? = null
    private var etPassword: EditText? = null
    private var loginBtn: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val backButton: ImageButton = view.findViewById(R.id.btnLoginBack)
        backButton.setOnClickListener {
            findNavController(view).popBackStack()
        }

        etEmail = view.findViewById(R.id.etLoginEmail)
        etPassword = view.findViewById(R.id.etLoginPassword)

        loginBtn = view.findViewById(R.id.btnLogin)
        loginBtn?.setOnClickListener {
            // validations
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

            activity?.let { context ->
                Model.instance.loginUser(
                    context,
                    view,
                    etEmail?.text.toString(),
                    etPassword?.text.toString()
                )
            }
        }

        return view
    }
}