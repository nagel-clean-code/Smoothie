package com.example.smoothie

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.smoothie.databinding.ActivityMainBinding
import com.example.smoothie.presentation.AddRecipeFragment
import com.example.smoothie.presentation.FindRecipeFragment
import com.example.smoothie.presentation.HomeFragment
import com.example.smoothie.presentation.SettingsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity() : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(savedInstanceState == null)
            replaceFragment(HomeFragment())

        binding.bottomNavigationView.setOnItemSelectedListener {
            val bufFragment = when(it.itemId){
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
    }

    /** Скрытие поднятого меню над клавиатурой при вводе текста */
    private fun settingKeyboard(){
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

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout_placeholder, fragment)
        fragmentTransaction.commit()
    }

}