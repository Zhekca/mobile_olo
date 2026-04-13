package com.example.glitchstore

import android.app.Activity
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.glitchstore.R

fun Fragment.showCustomToast(message: String) {
    val layout = layoutInflater.inflate(R.layout.custom_toast, null)
    val textView = layout.findViewById<TextView>(R.id.toastText)

    textView.text = message

    Toast(requireContext()).apply {
        duration = Toast.LENGTH_SHORT
        view = layout
        show()
    }
}

fun Activity.showCustomToast(message: String) {
    val layout = LayoutInflater.from(this).inflate(R.layout.custom_toast, null)
    val textView = layout.findViewById<TextView>(R.id.toastText)

    textView.text = message

    Toast(this).apply {
        duration = Toast.LENGTH_SHORT
        view = layout
        show()
    }
}