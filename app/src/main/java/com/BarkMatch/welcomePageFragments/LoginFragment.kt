package com.BarkMatch.welcomePageFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation.findNavController
import com.BarkMatch.R

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val backButton: Button = view.findViewById(R.id.btnLoginBack)
        backButton.setOnClickListener {
            findNavController(view).popBackStack()
        }

        return view
    }
}