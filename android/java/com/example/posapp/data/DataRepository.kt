package com.example.posapp.data

import androidx.compose.runtime.mutableStateListOf
// ✅ SỬA DÒNG IMPORT NÀY: Trỏ đích danh tới RoomModel trong gói api
import com.example.posapp.api.RoomModel

// Kho lưu trữ tạm thời trong bộ nhớ RAM
object DataRepository {
    // Danh sách các phòng và bàn đã được lưu
    val savedRooms = mutableStateListOf<RoomModel>()

    fun saveConfig(newList: List<RoomModel>) {
        savedRooms.clear()
        savedRooms.addAll(newList)
    }
}