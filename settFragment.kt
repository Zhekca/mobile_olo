package com.example.glitchstore

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.glitchstore.data.AppDatabase
import com.example.glitchstore.data.DatabaseProvider
import com.example.glitchstore.data.UserDao
import com.example.glitchstore.databinding.FragmentSettBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class settFragment : Fragment() {
    private var _binding: FragmentSettBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao
    private val prefs by lazy { PrefsManager(requireContext()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSettBinding.inflate(inflater, container, false)
        return binding.root }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupThemeSwitch()
        super.onViewCreated(view, savedInstanceState)

        db = DatabaseProvider.getDatabase(requireContext())
        userDao = db.userDao()


        viewLifecycleOwner.lifecycleScope.launch {

            val user = withContext(Dispatchers.IO) {
                userDao.getUserById(prefs.getUserId())
            }

            binding.fioTxt.text =
                user?.fio ?: getString(R.string.sett_name_box)
            Log.d("DB_CHECK", "UserId = ${prefs.getUserId()}")
            Log.d("DB_CHECK", "User = $user")
        }

        binding.switchNotification.setOnCheckedChangeListener(null)

        binding.switchNotification.isChecked = prefs.getNotificationsEnabled()

        binding.switchNotification.setOnCheckedChangeListener { _, isChecked ->
            prefs.setNotificationsEnabled(isChecked)

            if (isChecked) {
                showCustomToast(getString(R.string.switch_notif_on))
            } else {
                showCustomToast(getString(R.string.switch_notif_off))
            }
        }
        setupButtons()
    }
    override fun onResume() {
        super.onResume()

        binding.switchLightDark.setOnCheckedChangeListener(null)
        binding.switchLightDark.isChecked = prefs.getThemeState()
        setupThemeSwitch()
    }
    private fun setupThemeSwitch() {
        binding.switchLightDark.setOnCheckedChangeListener { _, isChecked ->

            if (!binding.switchLightDark.isPressed) return@setOnCheckedChangeListener

            prefs.saveThemeState(isChecked)

            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }
    private fun setupButtons() {

        binding.btnEditPrefs.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentCont, profsettFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.btnSupport.setOnClickListener {
            val layout = layoutInflater.inflate(R.layout.big_toast, null)

            val title = layout.findViewById<TextView>(R.id.title)
            val text = layout.findViewById<TextView>(R.id.text)
            val btnok = layout.findViewById<Button>(R.id.btnOk)
            val btnCancel = layout.findViewById<Button>(R.id.btnCancel)


            title.text = getString(R.string.support_title)
            text.text = getString(R.string.support_txt)
            btnok.text = getString(R.string.support_btn_inside)
            btnCancel.visibility = View.GONE


            val dialog = AlertDialog.Builder(requireContext())
                .setView(layout)
                .create()

            btnok.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()

            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
        binding.btnAbout.setOnClickListener {
            val layout = layoutInflater.inflate(R.layout.big_toast, null)

            val title = layout.findViewById<TextView>(R.id.title)
            val text = layout.findViewById<TextView>(R.id.text)
            val btnok = layout.findViewById<Button>(R.id.btnOk)
            val btnCancel = layout.findViewById<Button>(R.id.btnCancel)


            title.text = getString(R.string.about_title)
            text.text = getString(R.string.about_txt)
            btnok.text = getString(R.string.about_btn_inside)
            btnCancel.visibility = View.GONE


            val dialog = AlertDialog.Builder(requireContext())
                .setView(layout)
                .create()

            btnok.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()

            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
