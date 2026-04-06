package com.example.posapp.ui.screen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // ✅ Thêm import này
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.posapp.api.RetrofitClient
import com.example.posapp.api.RoomModel
import com.example.posapp.api.TableModel
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableConfigScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Bảng màu Luxury đồng bộ hệ thống
    val goldAccent = Color(0xFFF59E0B)
    val navyDark = Color(0xFF0F172A)
    val bgApp = Color(0xFFF8FAFC)
    val redSoft = Color(0xFFF43F5E)

    // State quản lý dữ liệu
    var rooms by remember { mutableStateOf(listOf<RoomModel>()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedRoom by remember { mutableStateOf<RoomModel?>(null) }

    // State cho nhập liệu mới
    var newRoomName by remember { mutableStateOf("") }
    var newTableName by remember { mutableStateOf("") }

    // STATE BỔ SUNG CHO CHỨC NĂNG CHỈNH SỬA
    var editingRoom by remember { mutableStateOf<RoomModel?>(null) }
    var editingTable by remember { mutableStateOf<TableModel?>(null) }
    var editTextFieldValue by remember { mutableStateOf("") }

    // Tải danh sách phòng từ Server
    LaunchedEffect(Unit) {
        try {
            rooms = RetrofitClient.instance.getAllRooms()
        } catch (e: Exception) {
            Toast.makeText(context, "Không thể tải dữ liệu: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }

    // Hàm lưu dữ liệu tổng quát
    fun saveToServer(updatedRooms: List<RoomModel>) {
        scope.launch {
            try {
                val response = RetrofitClient.instance.saveRooms(updatedRooms)
                if (response.isSuccessful) {
                    rooms = updatedRooms
                    if (selectedRoom != null) {
                        selectedRoom = updatedRooms.find { it.id == selectedRoom!!.id }
                    }
                    Toast.makeText(context, "Đã lưu thay đổi!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Lỗi Server: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Lỗi kết nối: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Xử lý nút Back hệ thống
    BackHandler {
        if (selectedRoom != null) selectedRoom = null else onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (selectedRoom == null) "Thiết Lập Khu Vực" else "Bàn: ${selectedRoom!!.name}",
                        fontWeight = FontWeight.Black,
                        color = navyDark
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { if (selectedRoom != null) selectedRoom = null else onBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = navyDark)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = bgApp
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = goldAccent)
            }
        } else {
            Crossfade(targetState = selectedRoom, label = "config_anim") { activeRoom ->
                if (activeRoom == null) {
                    // --- MÀN HÌNH 1: QUẢN LÝ KHU VỰC (ROOMS) ---
                    Column(Modifier.padding(padding).padding(horizontal = 20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = newRoomName,
                                onValueChange = { newRoomName = it },
                                label = { Text("Tên khu vực mới") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                            Spacer(Modifier.width(12.dp))
                            Button(
                                onClick = {
                                    if (newRoomName.isNotBlank()) {
                                        val newRoom = RoomModel(id = UUID.randomUUID().toString(), name = newRoomName, tables = emptyList())
                                        saveToServer(rooms + newRoom)
                                        newRoomName = ""
                                    }
                                },
                                modifier = Modifier.height(56.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = goldAccent),
                                shape = RoundedCornerShape(12.dp)
                            ) { Text("THÊM") }
                        }

                        Spacer(Modifier.height(16.dp))
                        Text("DANH SÁCH KHU VỰC", fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 12.sp)
                        Spacer(Modifier.height(8.dp))

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(rooms) { room ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White, RoundedCornerShape(16.dp))
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable { selectedRoom = room }
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(Modifier.size(40.dp).background(Color(0xFFFFF7ED), CircleShape), Alignment.Center) {
                                        Icon(Icons.Default.HolidayVillage, null, tint = goldAccent, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(Modifier.width(16.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(room.name, fontWeight = FontWeight.Bold, color = navyDark)
                                        Text("${room.tables.size} bàn", color = Color.Gray, fontSize = 12.sp)
                                    }

                                    IconButton(onClick = {
                                        editingRoom = room
                                        editTextFieldValue = room.name
                                    }) {
                                        Icon(Icons.Default.Edit, null, tint = goldAccent)
                                    }

                                    IconButton(onClick = { saveToServer(rooms.filter { it.id != room.id }) }) {
                                        Icon(Icons.Default.DeleteOutline, null, tint = redSoft)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // --- MÀN HÌNH 2: QUẢN LÝ BÀN TRONG PHÒNG (TABLES) ---
                    Column(Modifier.padding(padding).padding(horizontal = 20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = newTableName,
                                onValueChange = { newTableName = it },
                                label = { Text("Tên bàn (VD: A1, B2...)") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                            Spacer(Modifier.width(12.dp))
                            Button(
                                onClick = {
                                    if (newTableName.isNotBlank()) {
                                        val newTable = TableModel(
                                            id = UUID.randomUUID().toString(),
                                            name = newTableName,
                                            status = "EMPTY",
                                            currentTotal = 0.0,
                                            orderedItems = null, // Khớp với Any?
                                            itemNotes = null,    // Khớp với Any?
                                            checkInTime = 0L
                                        )
                                        val updatedRooms = rooms.map {
                                            if (it.id == activeRoom.id) {
                                                it.copy(tables = it.tables + newTable)
                                            } else it
                                        }
                                        saveToServer(updatedRooms)
                                        newTableName = ""
                                    }
                                },
                                modifier = Modifier.height(56.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = navyDark),
                                shape = RoundedCornerShape(12.dp)
                            ) { Text("THÊM BÀN") }
                        }

                        Spacer(Modifier.height(16.dp))
                        Text("DANH SÁCH BÀN TẠI ${activeRoom.name}", fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 12.sp)
                        Spacer(Modifier.height(8.dp))

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(activeRoom.tables) { table ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White, RoundedCornerShape(16.dp))
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(Modifier.size(40.dp).background(Color(0xFFF1F5F9), CircleShape), Alignment.Center) {
                                        Icon(Icons.Default.TableBar, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(Modifier.width(16.dp))
                                    Text(table.name, fontWeight = FontWeight.Bold, color = navyDark, modifier = Modifier.weight(1f))

                                    IconButton(onClick = {
                                        editingTable = table
                                        editTextFieldValue = table.name
                                    }) {
                                        Icon(Icons.Default.Edit, null, tint = Color.Gray)
                                    }

                                    IconButton(onClick = {
                                        val updatedRooms = rooms.map {
                                            if (it.id == activeRoom.id) {
                                                it.copy(tables = it.tables.filter { t -> t.id != table.id })
                                            } else it
                                        }
                                        saveToServer(updatedRooms)
                                    }) {
                                        Icon(Icons.Default.DeleteOutline, null, tint = redSoft)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // DIALOG CHỈNH SỬA TÊN KHU VỰC
    if (editingRoom != null) {
        AlertDialog(
            onDismissRequest = { editingRoom = null },
            title = { Text("Đổi tên khu vực") },
            text = {
                OutlinedTextField(
                    value = editTextFieldValue,
                    onValueChange = { editTextFieldValue = it },
                    label = { Text("Tên mới") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(onClick = {
                    val updated = rooms.map {
                        if (it.id == editingRoom!!.id) it.copy(name = editTextFieldValue) else it
                    }
                    saveToServer(updated)
                    editingRoom = null
                }) { Text("LƯU") }
            },
            dismissButton = {
                TextButton(onClick = { editingRoom = null }) { Text("HỦY") }
            }
        )
    }

    // DIALOG CHỈNH SỬA TÊN BÀN
    if (editingTable != null) {
        AlertDialog(
            onDismissRequest = { editingTable = null },
            title = { Text("Đổi tên bàn") },
            text = {
                OutlinedTextField(
                    value = editTextFieldValue,
                    onValueChange = { editTextFieldValue = it },
                    label = { Text("Tên mới") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(onClick = {
                    val updated = rooms.map { room ->
                        if (room.id == selectedRoom?.id) {
                            val updatedTables = room.tables.map { table ->
                                if (table.id == editingTable!!.id) table.copy(name = editTextFieldValue) else table
                            }
                            room.copy(tables = updatedTables)
                        } else room
                    }
                    saveToServer(updated)
                    editingTable = null
                }) { Text("LƯU") }
            },
            dismissButton = {
                TextButton(onClick = { editingTable = null }) { Text("HỦY") }
            }
        )
    }
}