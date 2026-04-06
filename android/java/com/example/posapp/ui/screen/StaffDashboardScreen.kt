package com.example.posapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffDashboardScreen(
    onNavigateToSodophong: () -> Unit,
    onNavigateToCustomer: () -> Unit,
    onLogout: () -> Unit
) {
    val bgApp = Color(0xFFF8FAFC)
    val textPrimary = Color(0xFF0F172A)
    val goldAccent = Color(0xFFF59E0B)
    val navyBlue = Color(0xFF1E293B)
    val redSoft = Color(0xFFF43F5E)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Nhân Viên Bán Hàng", fontWeight = FontWeight.ExtraBold, color = textPrimary, fontSize = 20.sp)
                        Text("Chúc bạn một ngày làm việc vui vẻ!", color = Color.Gray, fontSize = 13.sp)
                    }
                },
                actions = {
                    // Nút đăng xuất cho nhân viên
                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier.padding(end = 8.dp).background(Color(0xFFFFF1F2), CircleShape)
                    ) {
                        Icon(Icons.Default.Logout, contentDescription = "Đăng xuất", tint = redSoft)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = bgApp
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Card chào mừng nhân viên
            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(navyBlue).padding(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(50.dp).background(goldAccent, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Khu vực làm việc", color = Color(0xFF94A3B8), fontSize = 12.sp)
                        Text("Sảnh Chính / Quầy", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
            Text("CHỨC NĂNG CHÍNH", fontWeight = FontWeight.ExtraBold, color = textPrimary, fontSize = 14.sp, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))

            // Danh sách chức năng thu gọn cho nhân viên
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                // 1. Chức năng Sơ đồ bàn (Quan trọng nhất)
                StaffFeatureItem(
                    title = "Sơ Đồ Phòng / Bàn",
                    subtitle = "Mở bàn, gọi món và thanh toán",
                    icon = Icons.Default.TableBar,
                    color = goldAccent,
                    onClick = onNavigateToSodophong
                )

                // 2. Chức năng Khách hàng
                StaffFeatureItem(
                    title = "Khách Hàng",
                    subtitle = "Quản lý điểm và thông tin khách",
                    icon = Icons.Default.SupervisedUserCircle,
                    color = Color(0xFF8B5CF6),
                    onClick = onNavigateToCustomer
                )
            }
        }
    }
}

@Composable
fun StaffFeatureItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(56.dp).background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A))
                Text(subtitle, fontSize = 13.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, null, tint = Color.LightGray)
        }
    }
}