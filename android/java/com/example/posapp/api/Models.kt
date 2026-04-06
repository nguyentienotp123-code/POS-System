package com.example.posapp.api

import com.google.gson.annotations.SerializedName

// --- PHÒNG & BÀN ---
data class RoomModel(
    @SerializedName("_id") val id: String? = null,
    @SerializedName("name") var name: String = "",
    @SerializedName("tables") var tables: List<TableModel> = emptyList()
)

data class TableModel(
    @SerializedName("id") val id: String = "",
    @SerializedName("name") var name: String = "",
    @SerializedName("status") var status: String = "EMPTY",
    @SerializedName("currentTotal") var currentTotal: Double = 0.0,

    @SerializedName("orderedItems") var orderedItems: Any? = null,
    @SerializedName("itemNotes") var itemNotes: Any? = null,

    @SerializedName("checkInTime") var checkInTime: Long? = 0L
)

// --- THỰC ĐƠN ---
data class CategoryModel(
    @SerializedName("_id") val id: String? = null,
    @SerializedName("name") var name: String = "",
    @SerializedName("products") var products: List<ProductModel> = emptyList()
)

data class ProductModel(
    @SerializedName("id") val id: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("price") val price: Double = 0.0,
    @SerializedName("image") val image: String = ""
)

// --- NHÂN SỰ ---
data class UserModel(
    @SerializedName("_id") val id: String? = null,
    @SerializedName("username") val username: String = "",
    @SerializedName("password") val password: String = "",
    @SerializedName("fullName") val fullName: String = "",
    @SerializedName("role") val role: String = "STAFF"
)

// --- KHÁCH HÀNG ---
data class CustomerModel(
    @SerializedName("_id") val id: String? = null,
    @SerializedName("name") val name: String = "",
    @SerializedName("phone") val phone: String = "",
    @SerializedName("note") val note: String = "",
    @SerializedName("points") val points: Int = 0,
    @SerializedName("debt") val debt: Double = 0.0
)

// --- BÁO CÁO ---
data class ReportModel(
    @SerializedName("_id") val id: String? = null,
    @SerializedName("tableId") val tableId: String? = "",
    @SerializedName("tableName") val tableName: String? = "Bàn (Chưa rõ)",
    @SerializedName("totalAmount") val totalAmount: Double? = 0.0,

    @SerializedName("details") val details: Any? = null,

    @SerializedName("timestamp") val timestamp: String? = null
)

// --- KHO HÀNG ---
data class InventoryModel(
    @SerializedName("_id") val id: String? = null,
    @SerializedName("itemName") val itemName: String? = "Không tên",
    @SerializedName("type") val type: String? = "IMPORT",
    @SerializedName("quantity") val quantity: Double? = 0.0,
    @SerializedName("unit") val unit: String? = "",
    @SerializedName("note") val note: String? = "",
    @SerializedName("timestamp") val timestamp: String? = null
)