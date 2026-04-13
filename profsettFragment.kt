package com.example.glitchstore

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.glitchstore.data.AppDatabase
import com.example.glitchstore.data.DatabaseProvider
import com.example.glitchstore.data.HashUtils
import com.example.glitchstore.data.UserDao
import com.example.glitchstore.data.UserEntity
import com.example.glitchstore.databinding.FragmentAuthBinding
import com.example.glitchstore.databinding.FragmentProfsettBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.toString

class profsettFragment : Fragment() {
    private var _binding: FragmentProfsettBinding? = null
    private val binding get() = _binding!!
    private var isPasswordVisible = false
    private var currentUser: UserEntity? = null
    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao
    private val prefs by lazy { PrefsManager(requireContext()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfsettBinding.inflate(inflater, container, false)
        return binding.root
    }
    companion object {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = DatabaseProvider.getDatabase(requireContext())
        userDao = db.userDao()

        loadUser()

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.btnSave.setOnClickListener {
            saveUser()
        }

        binding.btnSeePass.setOnClickListener {
            togglePassword()
        }

        binding.btnLogout.setOnClickListener {
            prefs.clearUser()

            startActivity(Intent(requireContext(), WelcomeActivity::class.java))
            requireActivity().finish()
        }

        binding.btnDelete.setOnClickListener {

            val user = currentUser ?: return@setOnClickListener

            viewLifecycleOwner.lifecycleScope.launch {
                userDao.deleteUser(user)
                prefs.clearUser()

                startActivity(Intent(requireContext(), WelcomeActivity::class.java))
                requireActivity().finish()
            }
        }
    }
    private fun loadUser() {

        val userId = prefs.getUserId()

        viewLifecycleOwner.lifecycleScope.launch {

            val user = withContext(Dispatchers.IO) {
                userDao.getUserById(userId)
            }

            currentUser = user

            user?.let {
                binding.etFio.setText(it.fio)
                binding.etPassword.setText("")
            }
        }
    }

    private fun saveUser() {

        val user = currentUser ?: return

        val newPass = binding.etPassword.text.toString()

        val updated = user.copy(
            fio = binding.etFio.text.toString(),
            password = if (newPass.isNotEmpty())
                HashUtils.hash(newPass)
            else user.password
        )

        viewLifecycleOwner.lifecycleScope.launch {
            userDao.updateUser(updated)
            showCustomToast(getString(R.string.toast_saved))
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
