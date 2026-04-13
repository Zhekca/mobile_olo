package com.example.glitchstore

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.glitchstore.data.AppDatabase
import com.example.glitchstore.data.CartAdapter
import com.example.glitchstore.data.DatabaseProvider
import com.example.glitchstore.databinding.FragmentCartBinding
import kotlinx.coroutines.launch

class cartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: AppDatabase
    private lateinit var adapter: CartAdapter

    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = DatabaseProvider.getDatabase(requireContext())
        userId = PrefsManager(requireContext()).getUserId()

        setupRecycler()
        loadCart()
    }

    private fun setupRecycler() {

        adapter = CartAdapter()

        binding.recyclerListCart.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@cartFragment.adapter
        }
    }

    private fun loadCart() {

        viewLifecycleOwner.lifecycleScope.launch {

            val items = db.cartDao().getCartItems(userId)
            adapter.setData(items)
        }
    }

    private fun changeCount(productId: Int, delta: Int) {

        viewLifecycleOwner.lifecycleScope.launch {

            val item = db.cartDao().getCartItem(userId, productId) ?: return@launch
            val newCount = item.count + delta

            if (newCount <= 0) {
                db.cartDao().delete(userId, productId)
            } else {
                db.cartDao().updateCount(userId, productId, newCount)
            }

            loadCart()
        }
    }
    override fun onResume() {
        super.onResume()
        loadCart()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}