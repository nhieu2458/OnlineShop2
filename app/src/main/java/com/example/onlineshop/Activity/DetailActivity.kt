package com.example.onlineshop.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.onlineshop.Adapter.PicAdapter
import com.example.onlineshop.Adapter.SelectModelAdapter
import com.example.onlineshop.Model.ItemsModel
import com.example.onlineshop.databinding.ActivityDetailBinding
import com.example.project1762.Helper.ManagmentCart
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DetailActivity : BaseActivity() {
    private lateinit var binding:ActivityDetailBinding
    private lateinit var item:ItemsModel
    private var numberOrder=1
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        getBundle()
        initList()


    }

    private fun initList() {
        val modelList=ArrayList<String>()
        for (models in item.model){
            modelList.add(models)
        }

        binding.modelList.adapter=SelectModelAdapter(modelList)
        binding.modelList.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)

        val picList=ArrayList<String>()
        for (imageUrl in item.picUrl){
            picList.add(imageUrl)
        }

        Glide.with(this)
            .load(picList[0])
            .into(binding.img)

        binding.picList.adapter=PicAdapter(picList){selectedImageUrl ->
            Glide.with(this)
                .load(selectedImageUrl)
                .into(binding.img)
        }

        binding.picList.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
    }

    private fun getBundle() {
        item=intent.getParcelableExtra("object")!!

        binding.titleTxt.text=item.title
        binding.descriptionTxt.text=item.description
        binding.priceTxt.text="$"+item.price
        binding.ratingTxt.text="${item.rating} Rating"

        binding.numberItemTxt.text = numberOrder.toString()

        binding.plusCartBtn.setOnClickListener {
            numberOrder++
            binding.numberItemTxt.text = numberOrder.toString()
        }

        binding.minusCartBtn.setOnClickListener {
            if (numberOrder > 1) {
                numberOrder--
                binding.numberItemTxt.text = numberOrder.toString()
            }
        }

        binding.addToCartBtn.setOnClickListener {
            item.numberInCart=numberOrder
            addItemToCart(item)
        }
        binding.backBtn.setOnClickListener { finish() }
//        binding.cartBtn.setOnClickListener {
//            startActivity(Intent(this@DetailActivity, CartActivity::class.java))
//        }
    }

    private fun addItemToCart(item: ItemsModel) {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            // Tạo một Map để lưu thông tin sản phẩm vào Firestore
            val cartItem = hashMapOf(
                "itemId" to item.itemId,
                "title" to item.title,
                "price" to item.price,
                "quantity" to item.numberInCart,
                "imageUrl" to item.picUrl
            )

            // Lưu vào Firestore theo cấu trúc "Cart -> userId -> Items -> itemId"
            firestore.collection("Cart")
                .document(userId)
                .collection("Items")
                .document(item.itemId)  // Sử dụng itemId làm document ID cho mỗi sản phẩm
                .set(cartItem)
                .addOnSuccessListener {
                    Toast.makeText(this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Thêm vào giỏ hàng thất bại!", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Vui lòng đăng nhập để thêm vào giỏ hàng.", Toast.LENGTH_SHORT).show()
        }
    }

}