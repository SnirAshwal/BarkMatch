package com.barkMatch.utils

import android.util.Patterns

object Validations {
    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isPasswordValid(password: String): Boolean {
        val containsLetter = password.matches(".*[a-zA-Z].*".toRegex())
        val containsDigit = password.matches(".*\\d.*".toRegex())
        return password.length >= 6 && containsLetter && containsDigit
    }

    fun isFieldEmpty(field: String): Boolean {
        return field.isEmpty()
    }
}