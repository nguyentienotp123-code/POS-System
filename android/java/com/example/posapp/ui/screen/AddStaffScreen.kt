package com.example.posapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStaffScreen(
    onAddSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("STAFF") }

    val orangeColor = Color(0xFFF97316)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thêm Nhân Sự", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = username, onValueChange = { username = it },
                label = { Text("Tên đăng nhập nhân viên") },
                leadingIcon = { Icon(Icons.Default.Person, null) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password, onValueChange = { password = it },
                label = { Text("Mật khẩu cấp phát") },
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Phân quyền nội bộ
            Text("Cấp quyền:", fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                RadioButton(selected = role == "STAFF", onClick = { role = "STAFF" }, colors = RadioButtonDefaults.colors(selectedColor = orangeColor))
                Text("Nhân viên Order", modifier = Modifier.padding(end = 16.dp))

                RadioButton(selected = role == "ADMIN", onClick = { role = "ADMIN" }, colors = RadioButtonDefaults.colors(selectedColor = orangeColor))
                Text("Quản lý")
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    // TODO: Ghi vào Database nhân viên mới
                    onAddSuccess()
                },
                colors = ButtonDefaults.buttonColors(containerColor = orangeColor),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("TẠO TÀI KHOẢN", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}