package com.example.posapp.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState // ✅ Thêm để quản lý trạng thái cuộn
import androidx.compose.foundation.verticalScroll   // ✅ Thêm để cho phép cuộn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.posapp.api.RetrofitClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginSuccess: (String) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // ✅ Khai báo trạng thái cuộn
    val scrollState = rememberScrollState()

    val goldPremium = Color(0xFFD4AF37)
    val navyDark = Color(0xFF0F172A)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF1E293B), Color(0xFF020617))))
            .imePadding(), // ✅ Tự động đẩy nội dung khi bàn phím hiện lên
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .verticalScroll(scrollState) // ✅ Cho phép cuộn khi màn hình bị hẹp lại do bàn phím
        ) {
            // Spacer ở đầu để giúp nội dung cân đối khi cuộn
            Spacer(Modifier.height(40.dp))

            Surface(
                modifier = Modifier.size(90.dp),
                shape = RoundedCornerShape(28.dp),
                color = Color.White.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Coffee, null, tint = goldPremium, modifier = Modifier.size(45.dp))
                }
            }

            Spacer(Modifier.height(20.dp))
            Text("LUXURY POS", color = goldPremium, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 3.sp)
            Text("Hệ thống quản lý chuyên nghiệp", color = Color.Gray, fontSize = 14.sp)

            Spacer(Modifier.height(40.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(20.dp)
            ) {
                Column(modifier = Modifier.padding(32.dp)) {
                    Text("Đăng nhập", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = navyDark)
                    Spacer(Modifier.height(24.dp))

                    CustomTextField(username, { username = it }, "Tên tài khoản", Icons.Default.Person, goldPremium)
                    Spacer(Modifier.height(16.dp))
                    CustomTextField(password, { password = it }, "Mật khẩu", Icons.Default.Lock, goldPremium, true)

                    Spacer(Modifier.height(32.dp))

                    Button(
                        onClick = {
                            if (username.isBlank() || password.isBlank()) return@Button
                            isLoading = true
                            scope.launch {
                                try {
                                    if (username == "admin" && password == "123") {
                                        onLoginSuccess("ADMIN")
                                    } else {
                                        val usersRes = RetrofitClient.instance.getAllUsers()
                                        val matched = usersRes.find { it.username == username && it.password == password }
                                        if (matched != null) onLoginSuccess(matched.role.uppercase())
                                        else Toast.makeText(context, "Sai tài khoản!", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
                                } finally { isLoading = false }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(60.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = navyDark),
                        shape = RoundedCornerShape(20.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) CircularProgressIndicator(color = goldPremium, modifier = Modifier.size(24.dp))
                        else Text("VÀO HỆ THỐNG", fontWeight = FontWeight.Bold, color = goldPremium)
                    }
                }
            }

            // Spacer ở cuối để tránh nội dung sát mép dưới khi cuộn
            Spacer(Modifier.height(40.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, null, tint = color) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = color,
            unfocusedBorderColor = Color(0xFFE2E8F0)
        )
    )
}