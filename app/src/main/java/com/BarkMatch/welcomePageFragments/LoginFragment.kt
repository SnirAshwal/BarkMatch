package com.BarkMatch.welcomePageFragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import com.BarkMatch.HomeActivity
import com.BarkMatch.databinding.FragmentLoginBinding
import com.BarkMatch.interfaces.AuthenticationCallback
import com.BarkMatch.models.Model
import com.BarkMatch.utils.SnackbarUtils
import com.BarkMatch.utils.Validations

class LoginFragment : Fragment() {

    private var etEmail: EditText? = null
    private var etPassword: EditText? = null
    private var loginBtn: Button? = null
    private var pbLogin: ProgressBar? = null

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root

        val backButton: ImageButton = binding.btnLoginBack
        backButton.setOnClickListener {
            findNavController(view).popBackStack()
        }

        pbLogin = binding.pbLogin
        etEmail = binding.etLoginEmail
        etPassword = binding.etLoginPassword

        loginBtn = binding.btnLogin
        loginBtn?.setOnClickListener {
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

            pbLogin?.visibility = View.VISIBLE

            Model.instance.loginUser(
                etEmail?.text.toString(),
                etPassword?.text.toString(),
                object : AuthenticationCallback {
                    override fun onSuccess() {
                        val intent = Intent(requireContext(), HomeActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }

                    override fun onFailure() {
                        requireActivity().runOnUiThread {
                            SnackbarUtils.showSnackbar(requireView(), "Incorrect email or password")
                            pbLogin?.visibility = View.GONE
                        }
                    }
                }
            ) { isSuccess ->
                if (isSuccess) Log.i(
                    "TAG",
                    "login successful for user with email ${etEmail?.text.toString()}"
                )
                else Log.e("TAG", "login failed for user with email ${etEmail?.text.toString()}")
            }
        }

        return view
    }
}