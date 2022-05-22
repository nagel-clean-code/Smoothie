package com.example.smoothie.presentation

import android.view.View
import android.widget.Button
import com.example.smoothie.R

fun BaseFragment.onTryAgain(root: View, onTryAgainPressed: () -> Unit) {
    root.findViewById<Button>(R.id.onTryAgainButton).setOnClickListener { onTryAgainPressed() }
}