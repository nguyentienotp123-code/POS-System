package com.example.posapp.domain.model

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val barcode: String,
    val stock: Int
)