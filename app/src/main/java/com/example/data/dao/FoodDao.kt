package com.example.data.dao

import androidx.room.*
import com.example.data.entity.MenuItemEntity
import com.example.data.entity.OrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    // Menu items
    @Query("SELECT COUNT(*) FROM menu_items")
    suspend fun getMenuItemsCount(): Int

    @Query("SELECT * FROM menu_items WHERE isActive = 1 ORDER BY id DESC")
    fun getAllMenuItems(): Flow<List<MenuItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMenuItem(item: MenuItemEntity): Long

    @Update
    suspend fun updateMenuItem(item: MenuItemEntity)

    @Delete
    suspend fun deleteMenuItem(item: MenuItemEntity)

    // Orders
    @Query("SELECT * FROM orders ORDER BY timestamp DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query("DELETE FROM orders WHERE id = :orderId")
    suspend fun deleteOrderById(orderId: Long)
}
