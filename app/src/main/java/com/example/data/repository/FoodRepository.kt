package com.example.data.repository

import com.example.data.dao.FoodDao
import com.example.data.entity.MenuItemEntity
import com.example.data.entity.OrderEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class FoodRepository(private val foodDao: FoodDao) {
    val allMenuItems: Flow<List<MenuItemEntity>> = foodDao.getAllMenuItems()
    val allOrders: Flow<List<OrderEntity>> = foodDao.getAllOrders()

    suspend fun insertMenuItem(item: MenuItemEntity) {
        foodDao.insertMenuItem(item)
    }

    suspend fun updateMenuItem(item: MenuItemEntity) {
        foodDao.updateMenuItem(item)
    }

    suspend fun deleteMenuItem(item: MenuItemEntity) {
        // We will do soft delete by setting active to false or actual delete.
        // Let's do actual deletion for simplicity.
        foodDao.deleteMenuItem(item)
    }

    suspend fun insertOrder(order: OrderEntity): Long {
        return foodDao.insertOrder(order)
    }

    suspend fun updateOrder(order: OrderEntity) {
        foodDao.updateOrder(order)
    }

    suspend fun deleteOrderById(orderId: Long) {
        foodDao.deleteOrderById(orderId)
    }

    suspend fun prePopulateIfEmpty() = withContext(Dispatchers.IO) {
        try {
            val count = foodDao.getMenuItemsCount()
            if (count == 0) {
                val defaultItems = listOf(
                    MenuItemEntity(name = "Phở Bò Đặc Biệt", price = 55000.0, cost = 30000.0, category = "Món chính"),
                    MenuItemEntity(name = "Bánh Mì Sài Gòn", price = 25000.0, cost = 12000.0, category = "Ăn nhẹ"),
                    MenuItemEntity(name = "Cơm Sườn Chả Trứng", price = 45000.0, cost = 25000.0, category = "Món chính"),
                    MenuItemEntity(name = "Bún Chả Hà Nội", price = 40000.0, cost = 22000.0, category = "Món chính"),
                    MenuItemEntity(name = "Cà Phê Sữa Đá", price = 20000.0, cost = 8000.0, category = "Thức uống"),
                    MenuItemEntity(name = "Trà Đào Sả Vải", price = 25000.0, cost = 10000.0, category = "Thức uống")
                )
                for (item in defaultItems) {
                    foodDao.insertMenuItem(item)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
