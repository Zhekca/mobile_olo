package com.example.glitchstore

import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.glitchstore.data.AppDatabase
import com.example.glitchstore.data.DatabaseProvider
import com.example.glitchstore.data.HashUtils
import com.example.glitchstore.databinding.FragmentAuthBinding
import kotlinx.coroutines.launch
class authFragment : Fragment() {
    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!
    private val regFragment = regFragment()
    private var isPasswordVisible = false
    private lateinit var db: AppDatabase
    private lateinit var prefs: PrefsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.regAsk.paintFlags = binding.regAsk.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        binding.regAsk.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentCont, regFragment)
                .addToBackStack(null)
                .commit()
        }

        db = DatabaseProvider.getDatabase(requireContext())
        prefs = PrefsManager(requireContext())

        binding.btnEnter.setOnClickListener {

            val login = binding.etLogin.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (login.isEmpty() || password.isEmpty()) {
                showCustomToast(getString(R.string.toast_no_data_in_field))
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch {

                val user = db.userDao().getUserByLogin(login)

                if (user == null) {
                    showCustomToast(getString(R.string.toast_no_account))
                    return@launch
                }

                if (user.password != HashUtils.hash(password)) {
                    showCustomToast(getString(R.string.toast_wrong_pass))
                    return@launch
                }

                prefs.saveUserId(user.id)

                showCustomToast(getString(R.string.toast_succes_enter))
                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            }
        }

        binding.btnSeePass.setOnClickListener {
            togglePassword()
        }
    }
    private fun togglePassword() {
        val editText = binding.etPassword
        val cursorPosition = editText.selectionEnd

        if (isPasswordVisible) {
            editText.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

            binding.btnSeePass.setImageResource(R.drawable.btn_see_pass_close)
        } else {
            editText.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

            binding.btnSeePass.setImageResource(R.drawable.btn_see_pass_open)
        }
        editText.setTypeface(null, Typeface.BOLD)
        editText.setSelection(cursorPosition)
        isPasswordVisible = !isPasswordVisible
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

