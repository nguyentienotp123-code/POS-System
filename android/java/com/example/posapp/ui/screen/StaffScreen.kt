package com.example.posapp.ui.screen

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.posapp.api.RetrofitClient
import com.example.posapp.api.UserModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffScreen(onBack: () -> Unit) {
    val context = LocalContext.current // Đã hết lỗi nhờ import
    val scope = rememberCoroutineScope()
    val orangeMain = Color(0xFFF59E0B)
    val navyDark = Color(0xFF111827)

    var users by remember { mutableStateOf(listOf<UserModel>()) }
    var showSheet by remember { mutableStateOf(false) }
    var editingUser by remember { mutableStateOf<UserModel?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Form states
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("STAFF") }

    fun refreshData() {
        scope.launch {
            try { users = RetrofitClient.instance.getAllUsers() } catch (e: Exception) {}
        }
    }

    LaunchedEffect(Unit) { refreshData() }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Quản Lý Nhân Sự", fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().background(Color.White)) {
            Button(
                onClick = { editingUser = null; fullName = ""; username = ""; password = ""; showSheet = true },
                modifier = Modifier.padding(16.dp).fillMaxWidth().height(64.dp),
                colors = ButtonDefaults.buttonColors(containerColor = orangeMain),
                shape = RoundedCornerShape(20.dp),
                elevation = ButtonDefaults.buttonElevation(8.dp)
            ) {
                Icon(Icons.Default.Add, null)
                Text(" TẠO NHÂN VIÊN MỚI", fontWeight = FontWeight.ExtraBold)
            }

            LazyColumn(Modifier.padding(horizontal = 16.dp)) {
                items(users) { user ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(50.dp).background(Color(0xFFFFFBEB), CircleShape), Alignment.Center) {
                                Icon(Icons.Default.Badge, null, tint = orangeMain)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(user.fullName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text("@${user.username} • ${user.role}", color = Color.Gray, fontSize = 13.sp)
                            }
                            IconButton(onClick = { editingUser = user; fullName = user.fullName; username = user.username; showSheet = true }) { Icon(Icons.Default.Edit, null, tint = orangeMain) }
                            if (user.role != "ADMIN") {
                                IconButton(onClick = { scope.launch { RetrofitClient.instance.deleteUser(user.id!!); refreshData() } }) { Icon(Icons.Default.Delete, null, tint = Color.Red) }
                            }
                        }
                    }
                }
            }
        }

        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                containerColor = Color.White
            ) {
                Column(Modifier.padding(24.dp).padding(bottom = 40.dp)) {
                    Text(if (editingUser == null) "Nhân Viên Mới" else "Cập Nhật", fontSize = 22.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.height(20.dp))
                    CustomTextField(fullName, { fullName = it }, "Họ tên", Icons.Default.Badge, orangeMain)
                    Spacer(Modifier.height(12.dp))
                    CustomTextField(username, { username = it }, "Tên tài khoản", Icons.Default.Person, orangeMain)
                    Spacer(Modifier.height(12.dp))
                    CustomTextField(password, { password = it }, "Mật khẩu", Icons.Default.Key, orangeMain, true)
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = {
                            scope.launch {
                                val data = UserModel(username = username, password = password, fullName = fullName, role = role)
                                if (editingUser == null) RetrofitClient.instance.addUser(data)
                                else RetrofitClient.instance.updateUser(editingUser!!.id!!, data)
                                refreshData()
                                showSheet = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = navyDark),
                        shape = RoundedCornerShape(16.dp)
                    ) { Text("XÁC NHẬN LƯU", color = orangeMain, fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}