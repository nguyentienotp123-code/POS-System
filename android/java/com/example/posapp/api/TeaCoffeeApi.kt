package com.example.posapp.api

import retrofit2.Response
import retrofit2.http.*

interface TeaCoffeeApi {

    // --- ✅ QUẢN LÝ KHU VỰC (ROOMS) ---
    @GET("api/rooms")
    suspend fun getAllRooms(): List<RoomModel>

    // Thêm 1 khu vực mới (Dùng cho nút THÊM)
    @POST("api/rooms")
    suspend fun addRoom(@Body room: RoomModel): Response<RoomModel>

    // Lưu/Đồng bộ toàn bộ danh sách
    @POST("api/rooms/save")
    suspend fun saveRooms(@Body rooms: List<RoomModel>): Response<Any>

    // Cập nhật tên khu vực
    @PUT("api/rooms/{id}")
    suspend fun updateRoom(@Path("id") id: String, @Body data: Map<String, String>): Response<Any>

    // Xóa khu vực
    @DELETE("api/rooms/{id}")
    suspend fun deleteRoom(@Path("id") id: String): Response<Any>


    // --- QUẢN LÝ BÀN TRONG PHÒNG ---
    @PUT("api/rooms/{roomId}/tables/{tableId}")
    suspend fun updateTableName(
        @Path("roomId") roomId: String,
        @Path("tableId") tableId: String,
        @Body data: Map<String, String>
    ): Response<Any>

    @POST("api/tables/transfer")
    suspend fun transferTable(@Body data: Map<String, @JvmSuppressWildcards Any>): Response<Any>

    @POST("api/tables/update-order")
    suspend fun updateTableOrder(@Body data: Map<String, @JvmSuppressWildcards Any>): Response<Any>

    @POST("api/tables/pay-and-clear")
    suspend fun payAndClearTable(@Body data: Map<String, @JvmSuppressWildcards Any>): Response<Any>


    // --- QUẢN LÝ THỰC ĐƠN ---
    @GET("api/menu")
    suspend fun getAllMenu(): List<CategoryModel>

    @POST("api/menu/save")
    suspend fun saveMenu(@Body menu: List<CategoryModel>): Response<Any>

    @PUT("api/menu/{id}")
    suspend fun updateMenuCategory(@Path("id") id: String, @Body category: CategoryModel): Response<Any>


    // --- QUẢN LÝ KHÁCH HÀNG ---
    @GET("api/customers")
    suspend fun getCustomers(): List<CustomerModel>

    @POST("api/customers")
    suspend fun addCustomer(@Body customer: CustomerModel): Response<Any>

    @PUT("api/customers/{id}")
    suspend fun updateCustomer(@Path("id") id: String, @Body customer: CustomerModel): Response<Any>

    @DELETE("api/customers/{id}")
    suspend fun deleteCustomer(@Path("id") id: String): Response<Any>


    // --- QUẢN LÝ NHÂN SỰ ---
    @GET("api/users")
    suspend fun getAllUsers(): List<UserModel>

    @POST("api/users")
    suspend fun addUser(@Body user: UserModel): Response<Any>

    @PUT("api/users/{id}")
    suspend fun updateUser(@Path("id") id: String, @Body user: UserModel): Response<Any>

    @DELETE("api/users/{id}")
    suspend fun deleteUser(@Path("id") id: String): Response<Any>


    // --- QUẢN LÝ BÁO CÁO & KHO ---
    @GET("api/reports")
    suspend fun getAllReports(@Query("date") date: String? = null): List<ReportModel>

    @GET("api/inventories")
    suspend fun getAllInventories(@Query("date") date: String? = null): List<InventoryModel>

    @POST("api/inventories")
    suspend fun postInventory(@Body inventory: InventoryModel): Response<Any>

    @PUT("api/inventories/{id}")
    suspend fun updateInventory(@Path("id") id: String, @Body inventory: InventoryModel): Response<Any>

    @DELETE("api/inventories/{id}")
    suspend fun deleteInventory(@Path("id") id: String): Response<Any>
}