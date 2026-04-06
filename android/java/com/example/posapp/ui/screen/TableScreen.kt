package com.example.posapp.ui.screen

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.posapp.api.RetrofitClient
import com.example.posapp.api.RoomModel
import com.example.posapp.api.TableModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TableScreen(
    isAdmin: Boolean,
    onBack: () -> Unit,
    onNavigateToConfig: () -> Unit,
    onTableClick: (TableModel) -> Unit
) {
    var rooms by remember { mutableStateOf(listOf<RoomModel>()) }
    var selectedRoomIndex by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    // Quản lý chuyển bàn
    var sourceTable by remember { mutableStateOf<TableModel?>(null) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val formatter = DecimalFormat("#,###đ")

    val goldPremium = Color(0xFFF59E0B)
    val navyDark = Color(0xFF0F172A)
    val bgApp = Color(0xFFF1F5F9)

    // Hàm load dữ liệu dùng chung
    fun loadData() {
        scope.launch {
            try {
                isLoading = true
                rooms = RetrofitClient.instance.getAllRooms()
            } catch (e: Exception) {
                Toast.makeText(context, "Không thể kết nối máy chủ", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    // Tự động load lại khi quay lại màn hình
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                loadData()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Cập nhật thời gian thực để tính phút
    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(60000L) // Cập nhật mỗi phút
            currentTime = System.currentTimeMillis()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("LUXURY POS", fontWeight = FontWeight.Black, letterSpacing = 2.sp, color = Color.White)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = Color.White) }
                },
                actions = {
                    if (sourceTable != null) {
                        Button(
                            onClick = { sourceTable = null },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f))
                        ) {
                            Text("HỦY CHUYỂN", color = Color.White, fontSize = 12.sp)
                        }
                    }
                    if (isAdmin) {
                        IconButton(onClick = onNavigateToConfig) {
                            Icon(Icons.Default.Settings, null, tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = goldPremium)
            )
        },
        containerColor = bgApp
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            // Banner thông báo đang chuyển bàn
            if (sourceTable != null) {
                Surface(
                    color = navyDark,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.SyncAlt, null, tint = goldPremium, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Đang chuyển: ${sourceTable?.name} ➔ Chọn bàn trống",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = goldPremium)
                }
            } else if (rooms.isNotEmpty()) {
                // Tab danh sách khu vực
                ScrollableTabRow(
                    selectedTabIndex = selectedRoomIndex,
                    edgePadding = 16.dp,
                    containerColor = Color.White,
                    contentColor = goldPremium,
                    divider = {},
                    indicator = { tabPositions ->
                        if (selectedRoomIndex < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedRoomIndex]),
                                color = goldPremium,
                                height = 3.dp
                            )
                        }
                    }
                ) {
                    rooms.forEachIndexed { index, room ->
                        Tab(
                            selected = selectedRoomIndex == index,
                            onClick = { selectedRoomIndex = index },
                            text = {
                                Text(
                                    room.name.uppercase(),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp,
                                    color = if (selectedRoomIndex == index) goldPremium else Color.Gray
                                )
                            }
                        )
                    }
                }

                val currentTables = rooms.getOrNull(selectedRoomIndex)?.tables ?: emptyList()

                // Grid danh sách bàn
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(currentTables) { table ->
                        val isOccupied = table.status == "OCCUPIED"
                        val isBeingMoved = sourceTable?.id == table.id

                        Card(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(24.dp))
                                .combinedClickable(
                                    onClick = {
                                        if (sourceTable == null) {
                                            onTableClick(table)
                                        } else {
                                            // Thực hiện chuyển bàn
                                            if (!isOccupied) {
                                                scope.launch {
                                                    try {
                                                        // ✅ Fix 400: Đảm bảo ID được gửi dưới dạng String
                                                        val body = mapOf(
                                                            "sourceTableId" to sourceTable!!.id.toString(),
                                                            "targetTableId" to table.id.toString()
                                                        )
                                                        val res = RetrofitClient.instance.transferTable(body)
                                                        if (res.isSuccessful) {
                                                            Toast.makeText(context, "Chuyển bàn thành công!", Toast.LENGTH_SHORT).show()
                                                            sourceTable = null
                                                            loadData() // Refresh lại sơ đồ
                                                        } else {
                                                            Toast.makeText(context, "Lỗi Server: ${res.code()}", Toast.LENGTH_SHORT).show()
                                                        }
                                                    } catch (e: Exception) {
                                                        Toast.makeText(context, "Lỗi kết nối", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            } else {
                                                Toast.makeText(context, "Hãy chọn bàn còn trống!", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    onLongClick = {
                                        if (isOccupied && sourceTable == null) {
                                            sourceTable = table
                                        }
                                    }
                                ),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = when {
                                    isBeingMoved -> navyDark
                                    isOccupied -> goldPremium
                                    else -> Color.White
                                }
                            ),
                            elevation = CardDefaults.cardElevation(if (isOccupied) 8.dp else 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(8.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = table.name,
                                    fontWeight = FontWeight.Black,
                                    color = if (isOccupied || isBeingMoved) Color.White else navyDark,
                                    fontSize = 17.sp,
                                    textAlign = TextAlign.Center
                                )

                                if (isOccupied) {
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = formatter.format(table.currentTotal),
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    val checkIn = table.checkInTime ?: 0L
                                    if (checkIn > 0L) {
                                        val minutes = (currentTime - checkIn) / 60000
                                        Text(
                                            text = "⌛ ${minutes}m",
                                            color = Color.White.copy(alpha = 0.7f),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                } else {
                                    Spacer(Modifier.height(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.TableRestaurant,
                                        contentDescription = null,
                                        tint = if (isBeingMoved) Color.White.copy(alpha = 0.3f) else Color.LightGray.copy(alpha = 0.5f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Trạng thái trống
                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Inbox, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Spacer(Modifier.height(16.dp))
                    Text("Chưa có sơ đồ phòng bàn", color = Color.Gray, fontWeight = FontWeight.Bold)
                    if (isAdmin) {
                        Button(onClick = onNavigateToConfig, colors = ButtonDefaults.buttonColors(containerColor = goldPremium)) {
                            Text("THIẾT LẬP NGAY")
                        }
                    }
                }
            }
        }
    }
}