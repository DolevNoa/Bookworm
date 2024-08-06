package com.example.bookworm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        if (savedInstanceState == null) {
            // Initially load the LoginFragment
            navigateToLogin()
        }
    }

    fun navigateToLogin() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, LoginFragment.newInstance())
            .commit()
    }

    fun navigateToRegister() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, RegisterFragment.newInstance())
            .commit()
    }

    fun navigateToOut() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, OutFragment.newInstance())
            .commit()
    }
}
