package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "menu_items")
data class MenuItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val price: Double, // Giá bán
    val cost: Double,  // Giá vốn (để tính lợi nhuận)
    val category: String = "Món chính",
    val isActive: Boolean = true,
    val imageUrl: String = ""
)
