package com.example.posapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToTableConfig: () -> Unit,
    onNavigateToMenuConfig: () -> Unit,
    onNavigateToSodophong: () -> Unit,
    onNavigateToReport: () -> Unit,
    onNavigateToStaff: () -> Unit,
    onNavigateToCustomer: () -> Unit // ✅ Thêm sự kiện điều hướng Khách Hàng
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Quản Trị Hệ Thống", color = Color.White, fontWeight = FontWeight.Bold)
                },
                actions = {
                    IconButton(onClick = { /* Xử lý đăng xuất nếu cần */ }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF97316))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F9FA))
        ) {
            // Phần Header chào mừng
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Cửa hàng: TEA Coffee",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF97316)
                )
                Text(
                    text = "Quyền hạn: Quản trị viên",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Lưới các chức năng (Grid 2 cột)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // 1. Sơ đồ Phòng/Bàn
                item {
                    MenuCard(
                        title = "Sơ đồ Phòng/Bàn",
                        icon = Icons.Default.TableBar,
                        color = Color(0xFFE0E7FF),
                        iconColor = Color(0xFF4F46E5),
                        onClick = onNavigateToSodophong
                    )
                }

                // 2. Tạo Phòng/Bàn
                item {
                    MenuCard(
                        title = "Tạo Phòng/Bàn",
                        icon = Icons.Default.AppRegistration,
                        color = Color(0xFFECFDF5),
                        iconColor = Color(0xFF10B981),
                        onClick = onNavigateToTableConfig
                    )
                }

                // 3. Thực Đơn Món
                item {
                    MenuCard(
                        title = "Thực Đơn Món",
                        icon = Icons.Default.RestaurantMenu,
                        color = Color(0xFFFFF7ED),
                        iconColor = Color(0xFFF97316),
                        onClick = onNavigateToMenuConfig
                    )
                }

                // 4. Báo Cáo & Kho
                item {
                    MenuCard(
                        title = "Báo Cáo & Kho",
                        icon = Icons.Default.BarChart,
                        color = Color(0xFFF0FDF4),
                        iconColor = Color(0xFF22C55E),
                        onClick = onNavigateToReport
                    )
                }

                // 5. Nhân Sự
                item {
                    MenuCard(
                        title = "Nhân Sự",
                        icon = Icons.Default.Badge,
                        color = Color(0xFFF5F3FF),
                        iconColor = Color(0xFF8B5CF6),
                        onClick = onNavigateToStaff
                    )
                }

                // 6. Khách Hàng (Đã cập nhật đồng bộ màu Vàng/Cam)
                item {
                    MenuCard(
                        title = "Khách Hàng",
                        icon = Icons.Default.Loyalty,
                        color = Color(0xFFFFFBEB), // Màu nền vàng nhạt
                        iconColor = Color(0xFFF59E0B), // Màu icon Gold đồng bộ
                        onClick = onNavigateToCustomer // ✅ Gắn sự kiện chuyển trang
                    )
                }
            }
        }
    }
}

@Composable
fun MenuCard(
    title: String,
    icon: ImageVector,
    color: Color,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(color, shape = RoundedCornerShape(30.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF374151)
            )
        }
    }
}