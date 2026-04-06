package com.example.posapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
fun AdminDashboardScreen(
    onNavigateToTableConfig: () -> Unit,
    onNavigateToMenuConfig: () -> Unit,
    onNavigateToSodophong: () -> Unit,
    onNavigateToReport: () -> Unit,
    onNavigateToStaff: () -> Unit,
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
                        Text("Trang Quản Trị", fontWeight = FontWeight.ExtraBold, color = textPrimary, fontSize = 20.sp)
                        Text("Xin chào, Quản lý!", color = Color.Gray, fontSize = 14.sp)
                    }
                },
                actions = {
                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .background(Color(0xFFFFF1F2), CircleShape)
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
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Card Header thống kê nhanh hoặc Slogan
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(navyBlue)
                    .padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text("Tổng quan hệ thống", color = Color(0xFF94A3B8), fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Sẵn sàng hoạt động", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = goldAccent, modifier = Modifier.size(48.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("CHỨC NĂNG QUẢN LÝ", fontWeight = FontWeight.ExtraBold, color = textPrimary, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(16.dp))

            // Lưới chức năng (Grid)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // 1. Sơ đồ bàn (Bán hàng)
                item {
                    AdminFeatureCard(
                        title = "Sơ Đồ Bàn",
                        icon = Icons.Default.TableBar,
                        color = goldAccent,
                        onClick = onNavigateToSodophong
                    )
                }

                // 2. Thống kê & Báo cáo
                item {
                    AdminFeatureCard(
                        title = "Báo Cáo & Kho",
                        icon = Icons.Default.TrendingUp,
                        color = Color(0xFF10B981), // Xanh lá
                        onClick = onNavigateToReport
                    )
                }

                // 3. Thực đơn
                item {
                    AdminFeatureCard(
                        title = "Menu Món",
                        icon = Icons.Default.RestaurantMenu,
                        color = Color(0xFF3B82F6), // Xanh dương
                        onClick = onNavigateToMenuConfig
                    )
                }

                // 4. Khách hàng
                item {
                    AdminFeatureCard(
                        title = "Khách Hàng",
                        icon = Icons.Default.SupervisedUserCircle,
                        color = Color(0xFF8B5CF6), // Tím
                        onClick = onNavigateToCustomer
                    )
                }

                // 5. Nhân sự
                item {
                    AdminFeatureCard(
                        title = "Nhân Sự",
                        icon = Icons.Default.Badge,
                        color = Color(0xFFEC4899), // Hồng
                        onClick = onNavigateToStaff
                    )
                }

                // 6. Cấu hình Phòng/Bàn
                item {
                    AdminFeatureCard(
                        title = "Khu Vực & Bàn",
                        icon = Icons.Default.MeetingRoom,
                        color = Color(0xFF64748B), // Xám
                        onClick = onNavigateToTableConfig
                    )
                }
            }
        }
    }
}

@Composable
fun AdminFeatureCard(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f) // Tạo hình vuông
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}