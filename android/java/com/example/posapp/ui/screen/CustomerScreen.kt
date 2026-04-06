package com.example.posapp.ui.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.posapp.api.CustomerModel
import com.example.posapp.api.RetrofitClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // --- BẢNG MÀU PREMIUM ---
    val orangeMain = Color(0xFFF59E0B) // Gold/Orange
    val navyDark = Color(0xFF0F172A)  // Navy đậm
    val redDebt = Color(0xFFE11D48)   // Đỏ báo nợ

    // --- STATES ---
    var customers by remember { mutableStateOf(listOf<CustomerModel>()) }
    var isLoading by remember { mutableStateOf(true) }
    var showSheet by remember { mutableStateOf(false) }
    var editingCust by remember { mutableStateOf<CustomerModel?>(null) }

    // Form States
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var points by remember { mutableStateOf("0") }
    var debt by remember { mutableStateOf("0") }

    fun loadData() {
        scope.launch {
            isLoading = true
            try {
                customers = RetrofitClient.instance.getCustomers()
            } catch (e: Exception) {
                Toast.makeText(context, "Lỗi kết nối Server!", Toast.LENGTH_SHORT).show()
                Log.e("API_ERROR", "Lỗi tải khách hàng: ", e)
            } finally {
                isLoading = false
            }
        }
    }

    // Tự động tải lần đầu khi vào màn hình
    LaunchedEffect(Unit) { loadData() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("KHÁCH HÀNG THÂN THIẾT", fontWeight = FontWeight.Black, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBackIosNew, null, modifier = Modifier.size(20.dp)) }
                }
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            // --- NÚT THÊM MỚI ---
            Button(
                onClick = {
                    editingCust = null; name = ""; phone = ""; note = ""; points = "0"; debt = "0"; showSheet = true
                },
                modifier = Modifier.padding(20.dp).fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = orangeMain),
                shape = RoundedCornerShape(20.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Icon(Icons.Default.PersonAdd, null)
                Spacer(Modifier.width(8.dp))
                Text("THÊM KHÁCH HÀNG MỚI", fontWeight = FontWeight.ExtraBold)
            }

            if (isLoading) {
                Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = orangeMain) }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    items(customers) { customer ->
                        CustomerItemCard(
                            customer = customer,
                            orangeMain = orangeMain,
                            redDebt = redDebt,
                            onEdit = {
                                editingCust = customer
                                name = customer.name; phone = customer.phone; note = customer.note
                                points = customer.points.toString(); debt = customer.debt.toString()
                                showSheet = true
                            },
                            onDelete = {
                                scope.launch {
                                    try {
                                        if (customer.id != null) {
                                            RetrofitClient.instance.deleteCustomer(customer.id)
                                            loadData()
                                        } else {
                                            Toast.makeText(context, "Lỗi: Khách hàng không có ID", Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Lỗi xóa khách hàng!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        // --- MODAL BOTTOM SHEET ---
        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                containerColor = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).padding(bottom = 40.dp)
                        .fillMaxWidth().verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = if (editingCust == null) "Khách Hàng Mới" else "Sửa Thông Tin",
                        fontSize = 24.sp, fontWeight = FontWeight.Black, color = navyDark
                    )
                    Spacer(Modifier.height(24.dp))

                    CustomPremiumField(name, { name = it }, "Tên khách hàng", Icons.Default.Person, orangeMain)
                    Spacer(Modifier.height(12.dp))
                    CustomPremiumField(phone, { phone = it }, "Số điện thoại", Icons.Default.Phone, orangeMain, KeyboardType.Phone)

                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth()) {
                        Box(Modifier.weight(1f)) {
                            // Bật bàn phím số
                            CustomPremiumField(points, { points = it }, "Điểm", Icons.Default.Star, orangeMain, KeyboardType.Number)
                        }
                        Spacer(Modifier.width(12.dp))
                        Box(Modifier.weight(1f)) {
                            // Bật bàn phím số
                            CustomPremiumField(debt, { debt = it }, "Ghi nợ", Icons.Default.Payments, redDebt, KeyboardType.Number)
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    CustomPremiumField(note, { note = it }, "Ghi chú (Ví dụ: Hay uống ít đường...)", Icons.Default.EditNote, Color.Gray)

                    Spacer(Modifier.height(32.dp))

                    Button(
                        onClick = {
                            if (name.isBlank() || phone.isBlank()) {
                                Toast.makeText(context, "Vui lòng nhập Tên và SĐT", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            scope.launch {
                                try {
                                    val data = CustomerModel(
                                        id = editingCust?.id, // Đẩy ID ngược lên nếu đang Update
                                        name = name,
                                        phone = phone,
                                        note = note,
                                        points = points.toIntOrNull() ?: 0,
                                        debt = debt.toDoubleOrNull() ?: 0.0
                                    )

                                    // ✅ LOGIC CẬP NHẬT AN TOÀN CHỐNG CRASH
                                    val res = if (editingCust == null) {
                                        RetrofitClient.instance.addCustomer(data)
                                    } else {
                                        val targetId = editingCust?.id
                                        if (targetId.isNullOrEmpty()) {
                                            Toast.makeText(context, "Lỗi: Dữ liệu bị thiếu ID", Toast.LENGTH_SHORT).show()
                                            return@launch
                                        }
                                        RetrofitClient.instance.updateCustomer(targetId, data)
                                    }

                                    if (res.isSuccessful) {
                                        showSheet = false
                                        loadData()
                                        Toast.makeText(context, "Lưu thành công!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Lỗi Server: Mã ${res.code()}", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_LONG).show()
                                    Log.e("API_ERROR", "Lỗi lưu khách hàng", e)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(60.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = navyDark),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("XÁC NHẬN LƯU", fontWeight = FontWeight.Bold, color = orangeMain)
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerItemCard(
    customer: CustomerModel,
    orangeMain: Color,
    redDebt: Color,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(50.dp).background(Color(0xFFFFFBEB), CircleShape), Alignment.Center) {
                Icon(Icons.Default.Person, null, tint = orangeMain)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(customer.name, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
                Text(customer.phone, color = Color.Gray, fontSize = 13.sp)

                Row(Modifier.padding(top = 8.dp)) {
                    CustomerBadge(text = "⭐ ${customer.points}", color = orangeMain)
                    Spacer(Modifier.width(8.dp))
                    CustomerBadge(text = "💸 Nợ: ${customer.debt}đ", color = redDebt)
                }
                if (customer.note.isNotBlank()) {
                    Text("📝 ${customer.note}", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
                }
            }
            IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null, tint = orangeMain) }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, tint = Color.LightGray) }
        }
    }
}

@Composable
fun CustomerBadge(text: String, color: Color) {
    Surface(color = color.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
        Text(text = text, color = color, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
    }
}

// ✅ THÊM KEYBOARD TYPE ĐỂ DỄ NHẬP SỐ
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomPremiumField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    keyboardType: KeyboardType = KeyboardType.Text // Mặc định là bàn phím chữ
) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null, tint = color) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = color, unfocusedBorderColor = Color(0xFFE2E8F0))
    )
}