package com.example.glitchstore

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.glitchstore.data.AppDatabase
import com.example.glitchstore.data.CatAdapter
import com.example.glitchstore.data.CollectionAdapter
import com.example.glitchstore.data.DatabaseProvider
import com.example.glitchstore.databinding.FragmentCatBinding
import kotlinx.coroutines.launch

class CatFragment : Fragment() {

    private var _binding: FragmentCatBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: AppDatabase
    private lateinit var collectionAdapter: CollectionAdapter
    private lateinit var catAdapter: CatAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = DatabaseProvider.getDatabase(requireContext())

        setupCollections()
        setupCats()
        loadData()
    }

    // -------------------------
    // 🔹 COLLECTIONS (горизонтальный)
    // -------------------------

    private fun setupCollections() {
        collectionAdapter = CollectionAdapter { collectionId ->
            openHome(catId = null, collectionId = collectionId)
        }

        binding.recyclerListWideCats.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = collectionAdapter

            val snapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(this)

            // 🔥 ВОТ ЭТО ДОБАВЛЯЕШЬ СЮДА
            post {
                scrollToPosition(Int.MAX_VALUE / 2)
            }
        }
    }

    // -------------------------
    // 🔹 CATEGORIES (вертикальный)
    // -------------------------
    private fun setupCats() {
        catAdapter = CatAdapter { catId ->
            openHome(catId = catId, collectionId = null)
        }

        binding.recyclerListCats.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = catAdapter
        }
    }

    // -------------------------
    // 🔹 Загрузка данных из Room
    // -------------------------
    private fun loadData() {
        viewLifecycleOwner.lifecycleScope.launch {

            val collections = db.CollectionDao().getAll()
            val cats = db.CatDao().getAll()

            collectionAdapter.setData(collections)
            catAdapter.setData(cats)
        }
    }

    // -------------------------
    // 🔹 Переход в homeFragment
    // -------------------------
    private fun openHome(catId: Int?, collectionId: Int?) {
        val fragment = homeFragment()

        fragment.arguments = Bundle().apply {
            putInt("catId", catId ?: -1)
            putInt("collectionId", collectionId ?: -1)
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentCont, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}