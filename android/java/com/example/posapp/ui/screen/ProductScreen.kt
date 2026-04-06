package com.example.posapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ✅ THÊM DÒNG NÀY ĐỂ TRỊ DỨT ĐIỂM LỖI
import com.example.posapp.api.ProductModel

@Composable
fun ProductOrderCard(
    product: ProductModel,
    quantity: Int,
    priceStr: String,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Icon Ly Cafe
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                // ✅ Đã sửa lỗi Icon: Truyền đúng imageVector và contentDescription
                Icon(
                    imageVector = Icons.Default.Coffee,
                    contentDescription = "Món ăn",
                    tint = Color(0xFF3B82F6)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 2. Thông tin tên món và giá
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = priceStr,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFF97316), // Màu cam đặc trưng
                    fontSize = 14.sp
                )
            }

            // 3. Bộ điều khiển số lượng (+ / -)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                if (quantity > 0) {
                    IconButton(onClick = onRemove) {
                        Icon(
                            imageVector = Icons.Default.RemoveCircleOutline,
                            contentDescription = "Giảm",
                            tint = Color(0xFFEF4444)
                        )
                    }
                    Text(
                        text = "$quantity",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                IconButton(onClick = onAdd) {
                    // ✅ Đã sửa lỗi Color: Chỉ cần Color(0x...)
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "Thêm",
                        tint = Color(0xFFF97316)
                    )
                }
            }
        }
    }
}