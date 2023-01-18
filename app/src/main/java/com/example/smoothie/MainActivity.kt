package com.example.smoothie

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.smoothie.databinding.ActivityMainBinding
import com.example.smoothie.presentation.fragments.AddRecipeFragment
import com.example.smoothie.presentation.fragments.FindRecipeFragment
import com.example.smoothie.presentation.fragments.HomeFragment
import com.example.smoothie.presentation.fragments.SettingsFragment
import com.example.smoothie.presentation.viewmodels.MainActivityViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity() : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Smoothie)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null)
            replaceFragment(HomeFragment())

        binding.bottomNavigationView.setOnItemSelectedListener {
            val bufFragment = when (it.itemId) {
                R.id.home -> HomeFragment()
                R.id.settings -> SettingsFragment()
                R.id.add_recipe -> AddRecipeFragment()
                R.id.find_recipe -> FindRecipeFragment()
                else -> throw IllegalArgumentException()
            }
            replaceFragment(bufFragment)
            true
        }
        settingKeyboard()
        auth = Firebase.auth
        val currentUser = auth.currentUser
        signWhyAnonymous()
        updateUI(currentUser)
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user == null) {
            Toast.makeText(this, "Не авторизирован", Toast.LENGTH_LONG).show()
        }
    }

    private fun signWhyAnonymous() {
        auth.signInAnonymously()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Sign in success", Toast.LENGTH_LONG).show()
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Toast.makeText(this, "sign in fails", Toast.LENGTH_LONG).show()
                    updateUI(null)
                }
                if (viewModel.getUserName().isBlank()) {
                    viewModel.setupNewUserName()
                }
            }
    }

    /** Скрытие поднятого меню над клавиатурой при вводе текста */
    private fun settingKeyboard() {
        binding.bottomNavigationView.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            binding.root.getWindowVisibleDisplayFrame(r)
            if (binding.root.rootView.height - (r.bottom - r.top) > 500) {
                binding.bottomNavigationView.visibility = View.GONE
            } else {
                binding.bottomNavigationView.visibility = View.VISIBLE
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout_placeholder, fragment)
            .commit()
    }

}