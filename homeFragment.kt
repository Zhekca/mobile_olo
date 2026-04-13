package com.example.glitchstore

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.glitchstore.data.AppDatabase
import com.example.glitchstore.data.DatabaseProvider
import com.example.glitchstore.data.ProductAdapter
import com.example.glitchstore.data.ProductWithImage
import com.example.glitchstore.data.SortOrder
import com.example.glitchstore.data.SortType
import com.example.glitchstore.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class homeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ProductAdapter
    private lateinit var db: AppDatabase

    private var currentList: List<ProductWithImage> = emptyList()

    private var currentSortType = SortType.NEW
    private var currentSortOrder = SortOrder.DESC

    private var selectedCatId: Int? = null
    private var selectedCollectionId: Int? = null

    private var recyclerState: Parcelable? = null
    private var isDataLoaded = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val catId = it.getInt("catId", -1)
            val collectionId = it.getInt("collectionId", -1)

            if (catId != -1) selectedCatId = catId
            if (collectionId != -1) selectedCollectionId = collectionId
        }

        db = DatabaseProvider.getDatabase(requireContext())

        setupRecycler()
        setupClicks()

        loadProducts()
    }

    // -------------------------
    // RECYCLER
    // -------------------------
    private fun setupRecycler() {
        adapter = ProductAdapter(
            onItemClick = { productId ->
                openProductDetail(productId)
            }
        )

        binding.prodContHome.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)

            setHasFixedSize(true)   // ✔ ВСТАВИТЬ СЮДА
            itemAnimator = null     // ✔ ВСТАВИТЬ СЮДА

            adapter = this@homeFragment.adapter
        }
    }

    // -------------------------
    // CLICKS
    // -------------------------
    private fun setupClicks() {

        binding.sortPriceFull.setOnClickListener {
            handleSortClick(SortType.PRICE)
        }

        binding.sortRatingFull.setOnClickListener {
            handleSortClick(SortType.RATING)
        }

        binding.sortNewFull.setOnClickListener {
            handleSortClick(SortType.NEW)
        }

        binding.sortReset.setOnClickListener {
            resetFiltersAndSort()
        }
    }

    // -------------------------
    // LOAD DATA
    // -------------------------
    private fun loadProducts() {

        viewLifecycleOwner.lifecycleScope.launch {

            val products = db.productDao()
                .getFilteredProductsWithImages(
                    selectedCatId,
                    selectedCollectionId
                )

            currentList = products

            val sorted = sortList(currentList)
            updateListWithScrollFix(sorted)

            updateUI()
            updateResetButtonVisibility()

            isDataLoaded = true

            restoreScroll()
        }
    }

    // -------------------------
    // SCROLL FIX (MAIN PART)
    // -------------------------
    private fun restoreScroll() {

        binding.prodContHome.post {
            recyclerState?.let {
                binding.prodContHome.layoutManager?.onRestoreInstanceState(it)
            }
        }
    }

    override fun onPause() {
        super.onPause()

        recyclerState =
            binding.prodContHome.layoutManager?.onSaveInstanceState()
    }

    // -------------------------
    // RESET FILTERS
    // -------------------------
    private fun resetFiltersAndSort() {

        selectedCatId = null
        selectedCollectionId = null

        currentSortType = SortType.NEW
        currentSortOrder = SortOrder.DESC

        loadProducts()
    }

    private fun updateResetButtonVisibility() {
        binding.sortReset.visibility =
            if (selectedCatId != null || selectedCollectionId != null)
                View.VISIBLE
            else
                View.GONE
    }

    // -------------------------
    // SORT
    // -------------------------
    private fun handleSortClick(type: SortType) {

        if (currentSortType == type) {
            currentSortOrder =
                if (currentSortOrder == SortOrder.ASC)
                    SortOrder.DESC else SortOrder.ASC
        } else {
            currentSortType = type
            currentSortOrder = SortOrder.DESC
        }

        val sorted = sortList(currentList)
        updateListWithScrollFix(sorted)

        updateUI()
    }

    private fun sortList(list: List<ProductWithImage>): List<ProductWithImage> {
        return when (currentSortType) {

            SortType.PRICE -> {
                if (currentSortOrder == SortOrder.DESC)
                    list.sortedByDescending { it.price }
                else
                    list.sortedBy { it.price }
            }

            SortType.RATING -> {
                if (currentSortOrder == SortOrder.DESC)
                    list.sortedByDescending { it.avgRating ?: -1 }
                else
                    list.sortedBy { it.avgRating ?: Int.MAX_VALUE }
            }

            SortType.NEW -> {
                if (currentSortOrder == SortOrder.DESC)
                    list.sortedByDescending { it.id }
                else
                    list.sortedBy { it.id }
            }
        }
    }

    // -------------------------
    // UI
    // -------------------------
    private fun updateUI() {

        binding.priceArrowUp.visibility =
            if (currentSortType == SortType.PRICE && currentSortOrder == SortOrder.ASC)
                View.VISIBLE else View.GONE

        binding.priceArrowDown.visibility =
            if (currentSortType == SortType.PRICE && currentSortOrder == SortOrder.DESC)
                View.VISIBLE else View.GONE

        binding.ratingArrowUp.visibility =
            if (currentSortType == SortType.RATING && currentSortOrder == SortOrder.ASC)
                View.VISIBLE else View.GONE

        binding.ratingArrowDown.visibility =
            if (currentSortType == SortType.RATING && currentSortOrder == SortOrder.DESC)
                View.VISIBLE else View.GONE

        binding.newArrowUp.visibility =
            if (currentSortType == SortType.NEW && currentSortOrder == SortOrder.ASC)
                View.VISIBLE else View.GONE

        binding.newArrowDown.visibility =
            if (currentSortType == SortType.NEW && currentSortOrder == SortOrder.DESC)
                View.VISIBLE else View.GONE
    }
    private fun updateListWithScrollFix(newList: List<ProductWithImage>) {

        val state = binding.prodContHome.layoutManager?.onSaveInstanceState()

        adapter.setData(newList)

        binding.prodContHome.post {
            binding.prodContHome.layoutManager?.onRestoreInstanceState(state)
        }
    }
    // -------------------------
    // NAVIGATION
    // -------------------------
    private fun openProductDetail(productId: Int) {
        (activity as MainActivity).openProduct(productId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}