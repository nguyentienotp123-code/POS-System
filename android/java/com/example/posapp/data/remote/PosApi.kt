package com.example.posapp.data.remote

import com.example.posapp.domain.model.Product
import com.example.posapp.domain.model.Order
import retrofit2.http.*

interface PosApi {

    @GET("products")
    suspend fun getProducts(): List<Product>

    @GET("products/{barcode}")
    suspend fun getProductByBarcode(
        @Path("barcode") barcode: String
    ): Product

    @POST("orders")
    suspend fun sendOrder(
        @Body order: Order
    )
}