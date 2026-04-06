package com.example.posapp.data.repository

import com.example.posapp.data.local.OrderDao
import com.example.posapp.data.local.OrderEntity
import java.util.UUID

class PosRepository(
    private val orderDao: OrderDao
) {

    suspend fun insertDummyOrder() {
        val order = OrderEntity(
            id = UUID.randomUUID().toString(), // Tạo ID ngẫu nhiên
            tableName = "Bàn Demo",            // Truyền tên bàn
            totalAmount = 10000.0,             // Đổi 'total' thành 'totalAmount'
            details = "{}",                    // Truyền chuỗi JSON trống cho details
            timestamp = System.currentTimeMillis(),
            isSynced = 0                       // Mặc định là chưa đồng bộ
        )

        orderDao.insertOrder(order)
    }


    suspend fun getOrders(): List<OrderEntity> {
        return orderDao.getAllOrders()
    }

    suspend fun getUnsyncedOrders(): List<OrderEntity> {
        return orderDao.getUnsyncedOrders()
    }

    suspend fun markSynced(orderId: String) {
        orderDao.markSynced(orderId)
    }
}