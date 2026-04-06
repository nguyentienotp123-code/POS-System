package com.example.posapp.ui.screen

import android.app.DatePickerDialog
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.posapp.api.CategoryModel
import com.example.posapp.api.InventoryModel
import com.example.posapp.api.ReportModel
import com.example.posapp.api.RetrofitClient
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

val navyDark = Color(0xFF111827)
val orangeMain = Color(0xFFF59E0B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val formatter = DecimalFormat("#,###đ")

    val sdfDisplay = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val sdfQuery = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    var selectedTab by remember { mutableIntStateOf(0) }

    var reports by remember { mutableStateOf(listOf<ReportModel>()) }
    var inventories by remember { mutableStateOf(listOf<InventoryModel>()) }
    var menuCategories by remember { mutableStateOf(listOf<CategoryModel>()) }
    var isLoading by remember { mutableStateOf(false) }

    val calendar = remember { Calendar.getInstance() }
    var selectedDateStr by remember { mutableStateOf(sdfQuery.format(calendar.time)) }
    var displayDateStr by remember { mutableStateOf(sdfDisplay.format(calendar.time)) }

    var showDialog by remember { mutableStateOf(false) }
    var transType by remember { mutableStateOf("IMPORT") }
    var itemName by remember { mutableStateOf("") }
    var itemQty by remember { mutableStateOf("") }
    var itemUnit by remember { mutableStateOf("kg") }

    val showDatePicker = {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                selectedDateStr = sdfQuery.format(calendar.time)
                displayDateStr = sdfDisplay.format(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun loadData() {
        scope.launch {
            try {
                isLoading = true
                menuCategories = RetrofitClient.instance.getAllMenu()
                if (selectedTab == 0) {
                    reports = RetrofitClient.instance.getAllReports(selectedDateStr)
                } else {
                    inventories = RetrofitClient.instance.getAllInventories(selectedDateStr)
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Lỗi tải dữ liệu: ", e)
                Toast.makeText(context, "Lỗi kết nối Server", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(selectedTab, selectedDateStr) {
        loadData()
    }

    val onSaveInventory: () -> Unit = {
        if (itemName.isBlank() || itemQty.isBlank()) {
            Toast.makeText(context, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show()
        } else {
            scope.launch {
                try {
                    val newItem = InventoryModel(
                        itemName = itemName,
                        type = transType,
                        quantity = itemQty.toDoubleOrNull() ?: 0.0,
                        unit = itemUnit
                    )
                    val res = RetrofitClient.instance.postInventory(newItem)
                    if (res.isSuccessful) {
                        Toast.makeText(context, "Lưu kho thành công!", Toast.LENGTH_SHORT).show()
                        showDialog = false
                        itemName = ""; itemQty = ""
                        loadData()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Lỗi lưu kho", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thống Kê & Kho", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                },
                actions = {
                    IconButton(onClick = { loadData() }) { Icon(Icons.Default.Refresh, null) }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().background(Color.White)) {
            // Tabs
            Row(modifier = Modifier.padding(16.dp).fillMaxWidth().height(56.dp).background(Color(0xFFE5E7EB), RoundedCornerShape(28.dp)).padding(4.dp)) {
                listOf("Tổng kết ngày", "Quản lý Kho").forEachIndexed { index, title ->
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().background(if (selectedTab == index) Color.White else Color.Transparent, RoundedCornerShape(24.dp)).clickable { selectedTab = index }, contentAlignment = Alignment.Center) {
                        Text(title, fontWeight = FontWeight.Bold, color = if (selectedTab == index) navyDark else Color.Gray)
                    }
                }
            }

            // Thanh chọn ngày
            Card(
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth().clickable { showDatePicker() },
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F6)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarMonth, null, tint = orangeMain, modifier = Modifier.size(32.dp))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Dữ liệu ngày", fontSize = 12.sp, color = Color.Gray)
                        Text(displayDateStr, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Default.Edit, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(Modifier.height(16.dp))

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = orangeMain) }
            } else if (selectedTab == 0) {
                // --- TAB 1: TỔNG KẾT ---
                val totalRevenue = reports.sumOf { it.totalAmount ?: 0.0 }
                val itemSummary = mutableMapOf<String, Int>()

                reports.forEach { r ->
                    val detailsMap = when (val d = r.details) {
                        is Map<*, *> -> d
                        is String -> {
                            try {
                                d.trim('{', '}', ' ').split(",").mapNotNull { pair ->
                                    val parts = pair.split(":")
                                    if (parts.size == 2) {
                                        val id = parts[0].trim(' ', '"', '\\')
                                        val qty = parts[1].trim(' ', '"', '\\').toDoubleOrNull()?.toInt() ?: 0
                                        id to qty
                                    } else null
                                }.toMap()
                            } catch (e: Exception) { emptyMap<String, Int>() }
                        }
                        else -> emptyMap<String, Int>()
                    }

                    detailsMap.forEach { (k, v) ->
                        val key = k.toString()
                        val value = (v as? Number)?.toInt() ?: v.toString().toDoubleOrNull()?.toInt() ?: 0
                        itemSummary[key] = (itemSummary[key] ?: 0) + value
                    }
                }

                val productNameMap = remember(menuCategories) {
                    val map = mutableMapOf<String, String>()
                    menuCategories.forEach { cat -> cat.products.forEach { p -> map[p.id] = p.name } }
                    map
                }

                val productPriceMap = remember(menuCategories) {
                    val map = mutableMapOf<String, Double>()
                    menuCategories.forEach { cat -> cat.products.forEach { p -> map[p.id] = p.price } }
                    map
                }

                Column(modifier = Modifier.padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = navyDark), shape = RoundedCornerShape(32.dp)) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text("TỔNG DOANH THU", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(formatter.format(totalRevenue), color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
                            Spacer(Modifier.height(8.dp))
                            Text("Hóa đơn: ${reports.size}", color = Color.White.copy(0.7f))
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                    Text("CHI TIẾT MÓN BÁN RA", fontWeight = FontWeight.Black, fontSize = 16.sp)
                    Spacer(Modifier.height(12.dp))

                    if (reports.isEmpty() || itemSummary.isEmpty()) {
                        Text("Chưa có dữ liệu bán hàng trong ngày này.", modifier = Modifier.padding(16.dp), color = Color.Gray)
                    }

                    itemSummary.forEach { (id, qty) ->
                        val realName = productNameMap[id] ?: "Món không xác định"
                        val unitPrice = productPriceMap[id] ?: 0.0
                        val totalPrice = unitPrice * qty
                        ReportItemRow(realName, "Số lượng: $qty", if (totalPrice > 0) formatter.format(totalPrice) else "")
                    }
                    Spacer(Modifier.height(20.dp))
                }
            } else {
                // --- TAB 2: QUẢN LÝ KHO ---
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = { transType = "IMPORT"; showDialog = true }, modifier = Modifier.weight(1f).height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)), shape = RoundedCornerShape(12.dp)) { Text("NHẬP KHO") }
                        Button(onClick = { transType = "EXPORT"; showDialog = true }, modifier = Modifier.weight(1f).height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF43F5E)), shape = RoundedCornerShape(12.dp)) { Text("XUẤT KHO") }
                    }

                    Spacer(Modifier.height(24.dp))
                    Text("LỊCH SỬ GIAO DỊCH", fontWeight = FontWeight.Black, fontSize = 16.sp)
                    Spacer(Modifier.height(12.dp))

                    if (inventories.isEmpty()) {
                        Text("Không có giao dịch kho trong ngày này", modifier = Modifier.padding(16.dp), color = Color.Gray)
                    }

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                        items(inventories) { item ->
                            InventoryItemRow(item, onDelete = {
                                scope.launch { item.id?.let { RetrofitClient.instance.deleteInventory(it); loadData() } }
                            })
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(if(transType == "IMPORT") "Nhập kho hàng" else "Xuất kho hàng") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = itemName, onValueChange = { itemName = it }, label = { Text("Tên mặt hàng") })
                    OutlinedTextField(value = itemQty, onValueChange = { itemQty = it }, label = { Text("Số lượng") })
                    OutlinedTextField(value = itemUnit, onValueChange = { itemUnit = it }, label = { Text("Đơn vị (kg, thùng...)") })
                }
            },
            confirmButton = { Button(onClick = { onSaveInventory() }) { Text("LƯU") } },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("HỦY") } }
        )
    }
}

@Composable
fun InventoryItemRow(item: InventoryModel, onDelete: () -> Unit) {
    val isImport = item.type == "IMPORT"
    val timeStr = try {
        if (!item.timestamp.isNullOrEmpty()) {
            val dateStr = item.timestamp.take(19)
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).apply { timeZone = TimeZone.getTimeZone("UTC") }
            val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            inputFormat.parse(dateStr)?.let { outputFormat.format(it) } ?: "--:--"
        } else "--:--"
    } catch (e: Exception) { "--:--" }

    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFF3F4F6))) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp).background(if (isImport) Color(0xFFE1FBF2) else Color(0xFFFFE4E6), CircleShape), Alignment.Center) {
                Icon(if (isImport) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward, null, tint = if (isImport) Color(0xFF10B981) else Color(0xFFF43F5E))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(item.itemName ?: "Không tên", fontWeight = FontWeight.Bold)
                Text(timeStr, fontSize = 10.sp, color = Color.Gray)
            }
            Text("${if (isImport) "+" else "-"}${item.quantity} ${item.unit ?: ""}", color = if (isImport) Color(0xFF10B981) else Color(0xFFF43F5E), fontWeight = FontWeight.Bold)
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, tint = Color.LightGray) }
        }
    }
}

@Composable
fun ReportItemRow(name: String, sold: String, price: String) {
    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA))) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp).background(Color(0xFFFFFBEB), CircleShape), Alignment.Center) {
                Icon(Icons.Default.Restaurant, null, tint = Color(0xFFF59E0B))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.Bold)
                Text(sold, fontSize = 12.sp, color = Color.Gray)
            }
            if (price.isNotEmpty()) {
                // ✅ Đã có thể nhận diện navyDark vì biến này nằm ở Global Scope
                Text(price, fontWeight = FontWeight.Bold, color = navyDark)
            }
        }
    }
}