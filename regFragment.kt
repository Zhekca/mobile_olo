package com.example.glitchstore

import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.glitchstore.data.AppDatabase
import com.example.glitchstore.data.DatabaseProvider
import com.example.glitchstore.data.HashUtils
import com.example.glitchstore.data.UserEntity
import com.example.glitchstore.databinding.FragmentAuthBinding
import com.example.glitchstore.databinding.FragmentRegBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class regFragment : Fragment() {
    private var _binding: FragmentRegBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: AppDatabase
    private lateinit var prefs: PrefsManager
    private var isPasswordVisible = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        db = DatabaseProvider.getDatabase(requireContext())
        prefs = PrefsManager(requireContext())

        binding.btnRegister.setOnClickListener {

            val login = binding.etLogin.text.toString().trim()
            val fio = binding.etFio.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (login.isEmpty() || fio.isEmpty() || password.isEmpty()) {
                showCustomToast(getString(R.string.toast_no_data_in_field))
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch {

                val existing = db.userDao().getUserByLogin(login)

                if (existing != null) {
                    showCustomToast(getString(R.string.Reg_user_already))
                    return@launch
                }

                val user = UserEntity(
                    login = login,
                    fio = fio,
                    password = HashUtils.hash(password)
                )

                db.userDao().insertUser(user)

                val savedUser = db.userDao().getUserByLogin(login)

                prefs.saveUserId(savedUser!!.id)
                showCustomToast(getString(R.string.toast_yes_reg))
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