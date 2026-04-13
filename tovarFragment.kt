package com.example.glitchstore

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import coil.Coil.imageLoader
import coil.ImageLoader
import coil.imageLoader
import coil.request.ImageRequest
import com.example.glitchstore.data.AppDatabase
import com.example.glitchstore.data.CartEntity
import com.example.glitchstore.data.DatabaseProvider
import com.example.glitchstore.data.ImageAdapter
import com.example.glitchstore.databinding.FragmentTovarBinding
import com.google.gson.Gson
import kotlinx.coroutines.launch

class tovarFragment : Fragment() {

    private var counter = 0
    private var bool_for_fav = true
    private var _binding: FragmentTovarBinding? = null
    private val binding get() = _binding!!
    private var productId: Int = -1
    private lateinit var db: AppDatabase
    private var rating = 0
    private var userId: Int = -1
    private var cartItem: CartEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productId = arguments?.getInt("product_id") ?: -1 }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTovarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnAddToCart.visibility = View.INVISIBLE
        binding.CountEditor.visibility = View.INVISIBLE
        super.onViewCreated(view, savedInstanceState)

        userId = PrefsManager(requireContext()).getUserId()
        db = DatabaseProvider.getDatabase(requireContext())

        // 🔥 загрузка данных
        loadProduct()
        loadImages(productId)

        // 🔥 корзина (ВАЖНО: только через Room state)
        checkCartState()
        setupCartButtons()

        // 🔥 рейтинг
        initStars()
        updateStars()

        // 🔥 UI кнопки
        setupHeader()
        setupCartUi()
    }

    companion object {
        private const val ARG_PRODUCT_ID = "product_id"
        fun newInstance(productId: Int): tovarFragment {
            val fragment = tovarFragment()
            val args = Bundle()
            args.putInt(ARG_PRODUCT_ID, productId)
            fragment.arguments = args
            return fragment
        }
    }
    private fun setupHeader() {

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnFav.setOnClickListener {
            bool_for_fav = !bool_for_fav

            if (bool_for_fav) {
                binding.btnFav.setImageResource(R.drawable.btn_like_1)
            } else {
                binding.btnFav.setImageResource(R.drawable.btn_fav_0)
            }

            // TODO: сохранить в БД favorites
        }
        binding.btnGoToCart.setOnClickListener {
            (activity as MainActivity).switchTab("cart")
        }
    }
    private fun setupCartUi() {

        binding.btnAddToCart.setOnClickListener {
            addToCart()
        }

        binding.btnPlus.setOnClickListener {
            updateCount(+1)
        }

        binding.btnMinus.setOnClickListener {
            updateCount(-1)
        }
    }
    private fun addToCart() {

        viewLifecycleOwner.lifecycleScope.launch {

            db.cartDao().insert(
                CartEntity(
                    userid = userId,
                    productid = productId,
                    count = 1
                )
            )

            checkCartState()
        }
    }
    private fun checkCartState() {

        viewLifecycleOwner.lifecycleScope.launch {

            cartItem = db.cartDao().getCartItem(userId, productId)

            if (cartItem != null) {
                showCountEditor(cartItem!!.count)
            } else {
                showAddButton()
            }
        }
    }
    private fun showAddButton() {
        binding.btnAddToCart.visibility = View.VISIBLE
        binding.CountEditor.visibility = View.GONE
    }
    private fun showCountEditor(count: Int) {
        binding.btnAddToCart.visibility = View.GONE
        binding.CountEditor.visibility = View.VISIBLE
        binding.txtCount.text = count.toString()
    }
    private fun setupCartButtons() {

        binding.btnAddToCart.setOnClickListener {

            viewLifecycleOwner.lifecycleScope.launch {

                val newItem = CartEntity(
                    userid = userId,
                    productid = productId,
                    count = 1
                )

                db.cartDao().insert(newItem)

                checkCartState()
            }
        }
    }
    private fun updateCount(delta: Int) {

        viewLifecycleOwner.lifecycleScope.launch {

            val current = cartItem ?: return@launch
            val newCount = current.count + delta

            if (newCount <= 0) {
                db.cartDao().delete(userId, productId)
            } else {
                db.cartDao().updateCount(userId, productId, newCount)
            }

            checkCartState()
        }
    }
    private fun initStars() {
        val stars = listOf(
            binding.btnStar1,
            binding.btnStar2,
            binding.btnStar3,
            binding.btnStar4,
            binding.btnStar5
        )
        stars.forEachIndexed { index, star ->
            star.setOnClickListener {
                rating = index + 1
                updateStars()
            }
        }
    }
    private fun updateStars() {
        val stars = listOf(
            binding.btnStar1,
            binding.btnStar2,
            binding.btnStar3,
            binding.btnStar4,
            binding.btnStar5
        )
        for (i in stars.indices) {
            if (i < rating) {
                stars[i].setImageResource(R.drawable.star_filled)
            } else {
                stars[i].setImageResource(R.drawable.star)
            }
        }
    }
    private fun loadProduct() {
        viewLifecycleOwner.lifecycleScope.launch {

            val product = db.productDao().getProductDetail(productId)

            binding.prodName.text = product.name
            binding.prodDiscr.text = product.discr
            binding.prodMoney.text = "${product.price.toInt()} ₽"
            binding.prodMaterial.text = "${getString(R.string.name_product_material)} ${product.material}"
            binding.prodCollection.text = "${getString(R.string.name_product_collection)} ${product.collectionName}"
        }
    }
    private fun setupImagesRecycler(images: List<String>) {

        val adapter = ImageAdapter(images)

        binding.imgScroller.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            this.adapter = adapter

            PagerSnapHelper().attachToRecyclerView(this)
        }
    }

    private fun loadImages(productId: Int) {
        viewLifecycleOwner.lifecycleScope.launch {

            val json = db.productDao().getProductImages(productId)
            val images = parseImages(json)

            val imageLoader = requireContext().imageLoader
            images.forEach { url ->
                val request = ImageRequest.Builder(requireContext())
                    .data(url)
                    .build()
                imageLoader.enqueue(request)
            }

            setupImagesRecycler(images)
        }
    }
    private fun parseImages(json: String?): List<String> {
        if (json.isNullOrEmpty()) return emptyList()

        return try {
            val type = object : com.google.gson.reflect.TypeToken<List<String>>() {}.type
            Gson().fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

