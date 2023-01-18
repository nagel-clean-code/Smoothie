package com.example.smoothie.presentation.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.smoothie.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }


}