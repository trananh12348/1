package com.example.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.dao.FoodDao
import com.example.data.entity.MenuItemEntity
import com.example.data.entity.OrderEntity

@Database(
    entities = [MenuItemEntity::class, OrderEntity::class],
    version = 3,
    exportSchema = false
)
abstract class FoodDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao
}
