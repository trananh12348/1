package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

data class OrderItemDetail(
    val menuItemId: Long,
    val name: String,
    val quantity: Int,
    val priceAtOrder: Double,
    val costAtOrder: Double
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,            // Ngày giờ cụ thể
    val totalAmount: Double,         // Tổng tiền đơn hàng
    val totalProfit: Double,         // Lợi nhuận của đơn hàng
    val memoCode: Int,              // Mã QR chuyển khoản ngẫu nhiên (phạm vi 10000)
    val itemsSummary: String,        // Ví dụ: Bánh mì (x2), Phở Bò (x1)
    val itemsJson: String,          // Chuỗi JSON chi tiết đơn hàng
    val bankAccount: String,         // Tài khoản ngân hàng thụ hưởng tại thời điểm bán
    val bankName: String,            // Tên ngân hàng thụ hưởng
    val accountOwner: String,         // Tên chủ tài khoản thụ hưởng
    val status: String = "PROCESSING" // Trạng thái đơn: "PROCESSING" (Đang xử lý), "FINISHED" (Xử lý xong), "PAID" (Đã thanh toán)
)
