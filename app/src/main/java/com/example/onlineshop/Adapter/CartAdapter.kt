package com.example.onlineshop.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.onlineshop.Model.ItemsModel
import com.example.onlineshop.databinding.ViewholderCartBinding
import com.example.project1762.Helper.ChangeNumberItemsListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CartAdapter(
    private val listItemSelected: MutableList<ItemsModel>,
    private val context: Context,
    private val changeNumberItemsListener: ChangeNumberItemsListener
) : RecyclerView.Adapter<CartAdapter.Viewholder>() {

    class Viewholder(val binding: ViewholderCartBinding) : RecyclerView.ViewHolder(binding.root)


    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val binding = ViewholderCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        if (listItemSelected.isNotEmpty()) {
            val item = listItemSelected[position]

            // Hiển thị thông tin sản phẩm trong giỏ hàng
            holder.binding.titleTxt.text = item.title
            holder.binding.feeEachTime.text = "$${item.price}"
            holder.binding.totalEachItem.text = "$${Math.round(item.numberInCart * item.price)}"
            holder.binding.numberItemTxt.text = item.numberInCart.toString()

            // Sử dụng Glide để load hình ảnh sản phẩm
            Glide.with(holder.itemView.context)
                .load(item.picUrl[0])  // Đảm bảo rằng picUrl là một mảng và phần tử đầu tiên chứa URL hình ảnh
                .into(holder.binding.pic)

            // Tăng số lượng sản phẩm trong giỏ hàng
            holder.binding.plusCartBtn.setOnClickListener {
                val newQuantity = item.numberInCart + 1
                updateCartInFirestore(item, newQuantity) { success ->
                    if (success) {
                        item.numberInCart = newQuantity
                        notifyItemChanged(position)
                        changeNumberItemsListener.onChanged()
                    }
                }
            }

            // Giảm số lượng sản phẩm trong giỏ hàng
            holder.binding.minusCartBtn.setOnClickListener {
                if (item.numberInCart > 1) {
                    val newQuantity = item.numberInCart - 1
                    updateCartInFirestore(item, newQuantity) { success ->
                        if (success) {
                            item.numberInCart = newQuantity
                            notifyItemChanged(position)
                            changeNumberItemsListener.onChanged()
                        }
                    }
                } else {
                    // Xóa sản phẩm nếu số lượng bằng 1 và người dùng muốn giảm thêm
                    removeItemFromFirestore(item) { success ->
                        if (success) {
                            listItemSelected.removeAt(position)
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position, listItemSelected.size)
                            changeNumberItemsListener.onChanged()
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = listItemSelected.size

    // Cập nhật số lượng sản phẩm trong Firestore
    private fun updateCartInFirestore(item: ItemsModel, newQuantity: Int, callback: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val itemRef = firestore.collection("Cart")
                .document(userId)
                .collection("Items")
                .document(item.itemId)

            // Cập nhật số lượng sản phẩm trong Firestore
            itemRef.update("quantity", newQuantity)
                .addOnSuccessListener {
                    callback(true)  // Gọi lại hàm thành công
                }
                .addOnFailureListener {
                    callback(false)  // Xử lý khi cập nhật thất bại
                }
        } else {
            callback(false)  // Người dùng chưa đăng nhập
        }
    }
    // Xóa sản phẩm khỏi Firestore
    private fun removeItemFromFirestore(item: ItemsModel, callback: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val itemRef = firestore.collection("Cart")
                .document(userId)
                .collection("Items")
                .document(item.itemId)

            // Xóa sản phẩm khỏi Firestore
            itemRef.delete()
                .addOnSuccessListener {
                    callback(true)  // Xóa thành công
                }
                .addOnFailureListener {
                    callback(false)  // Xử lý khi xóa thất bại
                }
        } else {
            callback(false)  // Người dùng chưa đăng nhập
        }
    }
}
