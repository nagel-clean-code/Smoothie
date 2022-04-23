package com.example.smoothie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.smoothie.databinding.ActivityMainBinding
import com.example.smoothie.screens.AddRecipeFragment
import com.example.smoothie.screens.FindRecipeFragment
import com.example.smoothie.screens.HomeFragment
import com.example.smoothie.screens.SettingsFragment

class MainActivity() : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // При повороте экрана востановленный фрагмент
        // (с empty конструктором) заменяется
        replaceFragment(HomeFragment(this))

        binding.bottomNavigationView.setOnItemSelectedListener {
            val bufFragment = when(it.itemId){
                R.id.home -> HomeFragment(this)
                R.id.settings -> SettingsFragment()
                R.id.add_recipe -> AddRecipeFragment()
                R.id.find_recipe -> FindRecipeFragment()
                else -> throw IllegalArgumentException()
            }
            replaceFragment(bufFragment)
            true
        }
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

}