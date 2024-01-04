package com.BarkMatch.welcomePageFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation.findNavController
import com.BarkMatch.R

class WelcomeFragment : Fragment() {

    private var registerBtn: Button? = null
    private var loginBtn: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_welcome, container, false)

        registerBtn = view.findViewById(R.id.btnWelcomeRegister);
        registerBtn?.setOnClickListener {
            findNavController(view).navigate(R.id.registerFragment)
        }

        loginBtn = view.findViewById(R.id.btnWelcomeLogin);
        loginBtn?.setOnClickListener {
            findNavController(view).navigate(R.id.loginFragment)
        }

        return view
    }
}