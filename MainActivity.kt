package com.example.glitchstore

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.glitchstore.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private var activeFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = PrefsManager(this)

        if (prefs.getUserId() == -1) {
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
            return
        }
        initTabs()
        initNav()
    }
    // -------------------------
    // INIT TABS
    // -------------------------
    private fun initTabs() {

        if (supportFragmentManager.findFragmentByTag("home") == null) {

            val home = homeFragment()
            val cart = cartFragment()
            val fav = FavFragment()
            val cat = CatFragment()
            val sett = settFragment()

            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentCont, sett, "sett").hide(sett)
                .add(R.id.fragmentCont, cat, "cat").hide(cat)
                .add(R.id.fragmentCont, cart, "cart").hide(cart)
                .add(R.id.fragmentCont, fav, "fav").hide(fav)
                .add(R.id.fragmentCont, home, "home")
                .commit()

            activeFragment = home

        } else {
            activeFragment = supportFragmentManager.findFragmentByTag("home")
        }
    }

    // -------------------------
    // NAV BUTTONS
    // -------------------------
    private fun initNav() {

        binding.btnHome.setOnClickListener { switchTab("home") }
        binding.btnCart.setOnClickListener { switchTab("cart") }
        binding.btnFav.setOnClickListener { switchTab("fav") }
        binding.btnCat.setOnClickListener { switchTab("cat") }
        binding.btnSett.setOnClickListener { switchTab("sett") }
    }

    // -------------------------
    // SWITCH TAB (SAFE)
    // -------------------------
    fun switchTab(tag: String) {

        closeDetailIfOpen()

        val target = supportFragmentManager.findFragmentByTag(tag)

        if (target == null || target == activeFragment) return

        supportFragmentManager.beginTransaction()
            .hide(activeFragment!!)
            .show(target)
            .commit()

        activeFragment = target
    }

    // -------------------------
    // CLOSE PRODUCT IF OPEN
    // -------------------------
    private fun closeDetailIfOpen() {
        val current = supportFragmentManager.findFragmentById(R.id.fragmentCont)
        if (current is tovarFragment) {
            supportFragmentManager.popBackStack()
        }
    }

    // -------------------------
    // OPEN PRODUCT
    // -------------------------
    fun openProduct(productId: Int) {

        val fragment = tovarFragment.newInstance(productId)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentCont, fragment)
            .addToBackStack("product")
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}