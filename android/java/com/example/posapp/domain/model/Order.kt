package com.example.posapp.domain.model

data class Order(
    val id: String,
    val items: List<OrderItem>,
    val total: Double,
    val isSynced: Boolean
)

data class OrderItem(
    val productId: String,
    val quantity: Int,
    val price: Double
)