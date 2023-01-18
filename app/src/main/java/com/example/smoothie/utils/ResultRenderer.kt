package com.example.smoothie.utils

import android.view.View
import android.widget.Button
import com.example.smoothie.R
import com.example.smoothie.presentation.fragments.BaseFragment

fun BaseFragment.onTryAgain(root: View, onTryAgainPressed: () -> Unit) {
    root.findViewById<Button>(R.id.onTryAgainButton).setOnClickListener { onTryAgainPressed() }
}