package com.example.posapp.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    // --- CÁC HÀM HIỆN CÓ (GIỮ NGUYÊN) ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Query("SELECT * FROM orders ORDER BY timestamp DESC")
    suspend fun getAllOrders(): List<OrderEntity>

    @Query("SELECT * FROM orders WHERE isSynced = 0")
    suspend fun getUnsyncedOrders(): List<OrderEntity>

    @Query("UPDATE orders SET isSynced = 1 WHERE id = :orderId")
    suspend fun markSynced(orderId: String)

    @Query("DELETE FROM orders WHERE isSynced = 1")
    suspend fun deleteSyncedOrders()

    // --- CÁC HÀM BỔ SUNG NÂNG CAO ---

    /**
     * Lấy thông tin chi tiết của một hóa đơn dựa trên ID.
     * Hữu ích khi nhân viên muốn xem lại hoặc in lại hóa đơn cũ.
     */
    @Query("SELECT * FROM orders WHERE id = :orderId LIMIT 1")
    suspend fun getOrderById(orderId: String): OrderEntity?

    /**
     * Đếm số lượng hóa đơn chưa đồng bộ.
     * Sử dụng Flow để UI tự động cập nhật số lượng (ví dụ: hiện số đỏ trên icon mây).
     */
    @Query("SELECT COUNT(*) FROM orders WHERE isSynced = 0")
    fun getUnsyncedCountFlow(): Flow<Int>

    /**
     * Tìm kiếm hóa đơn theo tên bàn.
     * Giúp quản lý tìm lại hóa đơn nhanh hơn khi danh sách quá dài.
     */
    @Query("SELECT * FROM orders WHERE tableName LIKE '%' || :searchQuery || '%' ORDER BY timestamp DESC")
    suspend fun searchOrdersByTable(searchQuery: String): List<OrderEntity>

    /**
     * Tính tổng doanh thu hiện có trong máy (bao gồm cả chưa đồng bộ).
     * Dùng để hiện báo cáo nhanh tại máy mà không cần mạng.
     */
    @Query("SELECT SUM(totalAmount) FROM orders")
    suspend fun getTotalLocalRevenue(): Double?

    /**
     * Xóa toàn bộ dữ liệu (Dùng khi đăng xuất hoặc đổi ca).
     */
    @Query("DELETE FROM orders")
    suspend fun clearAllData()
}