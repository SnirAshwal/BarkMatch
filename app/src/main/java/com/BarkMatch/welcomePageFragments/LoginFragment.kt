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

class LoginFragment : Fragment() {

    private var edUsername: EditText? = null
    private var edPassword: EditText? = null
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

        edUsername = view.findViewById(R.id.etLoginUsername)
        edPassword = view.findViewById(R.id.etLoginPassword)

        loginBtn = view.findViewById(R.id.btnLogin)
        loginBtn?.setOnClickListener {
            val intent = Intent(activity, HomeActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        return view
    }
}