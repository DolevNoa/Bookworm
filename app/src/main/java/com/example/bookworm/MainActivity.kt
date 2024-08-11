package com.example.bookworm

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.bookworm.ui.settings.SettingsFragment
import com.example.bookworm.ui.userProfile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        NavigationUI.setupWithNavController(bottomNavigationView, navController)

//        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
//            val fragment: Fragment = when (item.itemId) {
//                R.id.nav_settings -> SettingsFragment.newInstance()
////                R.id.nav_add_post -> AddPostFragment.newInstance()
////                R.id.nav_user_posts -> UserPostsFragment.newInstance()
////                R.id.nav_feed -> FeedFragment.newInstance()
//                else -> throw IllegalArgumentException("Unexpected item ID")
//            }
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.nav_host_fragment, fragment)
//                .commit()
//            true
//        }
//
//        // Set default fragment
        if (savedInstanceState == null) {
            bottomNavigationView.selectedItemId = R.id.settingsFragment
        }
    }
}