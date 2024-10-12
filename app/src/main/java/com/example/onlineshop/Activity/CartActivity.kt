package com.example.onlineshop.Activity

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onlineshop.Adapter.CartAdapter
import com.example.onlineshop.Model.ItemsModel
import com.example.onlineshop.R
import com.example.onlineshop.databinding.ActivityCartBinding
import com.example.project1762.Helper.ChangeNumberItemsListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CartActivity : BaseActivity() {
    private lateinit var binding: ActivityCartBinding
    private var cartItems = mutableListOf<ItemsModel>()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var tax:Double=0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)



        setVariable()
        setVariable()
        fetchCartItemsFromFirestore()
    }

    private fun fetchCartItemsFromFirestore() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("Cart")
                .document(userId)
                .collection("Items")
                .get()
                .addOnSuccessListener { documents ->
                    cartItems.clear() // Xóa dữ liệu cũ
                    for (document in documents) {
                        val itemId = document.getString("itemId") ?: ""
                        val title = document.getString("title") ?: ""
                        val price = document.getDouble("price") ?: 0.0
                        val quantity = document.getLong("quantity")?.toInt() ?: 0
                        val imageUrl = document.get("imageUrl") as? ArrayList<String> ?: ArrayList()

                        // Khởi tạo ItemsModel với các thông tin đã lấy
                        val item = ItemsModel(itemId, title, "", imageUrl,ArrayList(), price, 0.0, quantity, false, 0)
                         // Giả sử picUrl là List<String>
                        cartItems.add(item)
                    }
                    initCartList()  // Khởi tạo danh sách sau khi lấy dữ liệu
                    calculatorCart() // Tính toán lại tổng giỏ hàng
                }
                .addOnFailureListener { e ->
                    // Xử lý lỗi nếu không lấy được dữ liệu
                }
        } else {
            // Xử lý nếu người dùng chưa đăng nhập
        }
    }

    private fun initCartList() {
        if (cartItems.isNotEmpty()) {
            binding.viewCart.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            binding.viewCart.adapter = CartAdapter(cartItems, this, object : ChangeNumberItemsListener {
                override fun onChanged() {
                    calculatorCart()  // Cập nhật tổng giỏ hàng khi số lượng thay đổi
                }
            })
        } else {
            binding.emptyTxt.visibility = View.VISIBLE
            binding.scrollView4.visibility = View.GONE
        }
    }


    private fun setVariable() {
        binding.apply {
            backBtn.setOnClickListener {
                finish()
            }

            method1.setOnClickListener{
                method1.setBackgroundResource(R.drawable.green_bg_selected)
                methodIc1.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this@CartActivity,R.color.green))
                methodTitle1.setTextColor(getResources().getColor(R.color.green))
                methodSubtitle1.setTextColor(getResources().getColor(R.color.green))

                method2.setBackgroundResource(R.drawable.grey_bg_selected)
                methodIc2.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this@CartActivity,R.color.green))
                methodTitle2.setTextColor(getResources().getColor(R.color.black))
                methodSubtitle2.setTextColor(getResources().getColor(R.color.grey))
            }

            method2.setOnClickListener{
                method2.setBackgroundResource(R.drawable.green_bg_selected)
                methodIc2.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this@CartActivity,R.color.green))
                methodTitle2.setTextColor(getResources().getColor(R.color.green))
                methodSubtitle2.setTextColor(getResources().getColor(R.color.green))

                method1.setBackgroundResource(R.drawable.grey_bg_selected)
                methodIc1.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this@CartActivity,R.color.green))
                methodTitle1.setTextColor(getResources().getColor(R.color.black))
                methodSubtitle1.setTextColor(getResources().getColor(R.color.grey))
            }
        }
    }

    private fun calculatorCart(){
        val percentTax = 0.02
        val delivery = 10.0

        // Tính tổng chi phí sản phẩm
        val totalFee = cartItems.sumOf { it.price * it.numberInCart }
        val tax = Math.round((totalFee * percentTax) * 100) / 100.0
        val total = Math.round((totalFee + tax + delivery) * 100) / 100
        val itemTotal = Math.round(totalFee * 100) / 100

        with(binding) {
            totalFeeTxt.text = "$$itemTotal"
            taxTxt.text = "$$tax"
            deliveryTxt.text = "$$delivery"
            totalTxt.text = "$$total"
        }
    }
}