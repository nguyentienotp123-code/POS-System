package com.example.posapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey
    val id: String,
    val tableName: String,
    val totalAmount: Double,
    val details: String,
    val timestamp: Long = System.currentTimeMillis(), // 👈 PHẢI CÓ (Sửa lỗi Cannot resolve symbol 'timestamp')
    val isSynced: Int = 0
)