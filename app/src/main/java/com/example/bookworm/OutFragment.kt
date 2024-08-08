package com.example.bookworm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

class OutFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var logoutButton: Button
    private lateinit var welcomeTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_out, container, false)
        logoutButton = view.findViewById(R.id.logoutButton)
        welcomeTextView = view.findViewById(R.id.welcomeTextView)

        // Display a welcome message with the user's email
        val currentUser = auth.currentUser
        welcomeTextView.text = "Welcome, ${currentUser?.email ?: "Guest"}"

        logoutButton.setOnClickListener {
            auth.signOut()
        }

        return view
    }

    companion object {
        fun newInstance(): OutFragment {
            return OutFragment()
        }
    }
}
