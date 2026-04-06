package com.example.posapp.ui.screen

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.core.content.FileProvider
import androidx.work.*
import com.example.posapp.api.*
import com.example.posapp.data.local.AppDatabase
import com.example.posapp.data.local.OrderEntity
import com.example.posapp.data.worker.SyncWorker
import dev.shreyaspatil.capturable.Capturable
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    table: TableModel?,
    onBack: () -> Unit,
    onNavigateToMoveTable: () -> Unit
) {
    if (table == null) return

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val formatter = DecimalFormat("#,###đ")
    val database = AppDatabase.getDatabase(context)
    val captureController = rememberCaptureController()

    var isCapturing by remember { mutableStateOf(false) }

    val goldPremium = Color(0xFFF59E0B)
    val navyDark = Color(0xFF0F172A)
    val bgGray = Color(0xFFF8FAFC)

    // ✅ Lọc dữ liệu tránh Null Key
    val initialCart: Map<String, Int> = remember(table.orderedItems) {
        val raw = table.orderedItems
        if (raw is Map<*, *>) {
            raw.mapNotNull { (k, v) ->
                val key = k?.toString()
                val value = (v as? Number)?.toInt() ?: 0
                if (key != null) key to value else null
            }.toMap()
        } else emptyMap()
    }

    val initialNotes: Map<String, String> = remember(table.itemNotes) {
        val raw = table.itemNotes
        if (raw is Map<*, *>) {
            raw.mapNotNull { (k, v) ->
                val key = k?.toString()
                val note = v?.toString() ?: ""
                if (key != null) key to note else null
            }.toMap()
        } else emptyMap()
    }

    var cart by remember { mutableStateOf(initialCart) }
    var notes by remember { mutableStateOf(initialNotes) }
    var menuCategories by remember { mutableStateOf(listOf<CategoryModel>()) }
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var showNoteDialogFor by remember { mutableStateOf<String?>(null) }
    var noteInput by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        try { menuCategories = RetrofitClient.instance.getAllMenu() }
        catch (e: Exception) { Log.e("API", "Error load menu") }
        finally { isLoading = false }
    }

    val currentTotal = cart.entries.sumOf { (id, qty) ->
        val product = menuCategories.flatMap { it.products }.find { it.id == id }
        (product?.price ?: 0.0) * qty
    }

    fun updateCart(id: String, delta: Int) {
        val newCart = cart.toMutableMap()
        val currentQty = newCart[id] ?: 0
        val newQty = currentQty + delta
        if (newQty <= 0) {
            newCart.remove(id)
            notes = notes.toMutableMap().apply { remove(id) }
        } else { newCart[id] = newQty }
        cart = newCart
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(table.name, fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) } },
                actions = { IconButton(onClick = onNavigateToMoveTable) { Icon(Icons.Default.SyncAlt, null, tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = goldPremium)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().background(bgGray)) {

            // --- MENU (0.45f) ---
            Column(modifier = Modifier.weight(0.45f)) {
                if (isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = goldPremium) }
                } else {
                    ScrollableTabRow(selectedTabIndex = selectedCategoryIndex, containerColor = Color.White, contentColor = goldPremium, edgePadding = 16.dp) {
                        menuCategories.forEachIndexed { index, cat ->
                            Tab(selected = selectedCategoryIndex == index, onClick = { selectedCategoryIndex = index }, text = { Text(cat.name, fontWeight = FontWeight.Bold) })
                        }
                    }
                    LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(12.dp)) {
                        val currentProducts = menuCategories.getOrNull(selectedCategoryIndex)?.products ?: emptyList()
                        items(currentProducts) { product ->
                            Card(onClick = { updateCart(product.id, 1) }, modifier = Modifier.padding(4.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                                Column(modifier = Modifier.padding(12.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.LocalCafe, null, tint = goldPremium)
                                    Text(product.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                                    Text(formatter.format(product.price), color = goldPremium, fontWeight = FontWeight.ExtraBold)
                                }
                            }
                        }
                    }
                }
            }

            // --- GIỎ HÀNG & HÓA ĐƠN ---
            Capturable(
                controller = captureController,
                onCaptured = { bitmap, _ ->
                    isCapturing = false
                    if (bitmap != null) shareBitmapToFunPrint(context, bitmap.asAndroidBitmap())
                },
                modifier = Modifier.weight(0.55f).verticalScroll(rememberScrollState())
            ) {
                LuxuryReceiptContent(
                    tableName = table.name,
                    cart = cart,
                    menu = menuCategories,
                    total = currentTotal,
                    notes = notes,
                    isCapturing = isCapturing,
                    onUpdateCart = { id, delta -> updateCart(id, delta) },
                    onShowNote = { id, note -> showNoteDialogFor = id; noteInput = note }
                )
            }

            // --- NÚT BẤM ---
            val jsonDetailsForRoom = cart.entries.joinToString(separator = ",", prefix = "{", postfix = "}") { "\"${it.key}\": ${it.value}" }

            Row(modifier = Modifier.fillMaxWidth().padding(16.dp).background(bgGray), horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                // GỬI BẾP
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                val time = if (table.checkInTime == null || table.checkInTime == 0L) System.currentTimeMillis() else table.checkInTime!!
                                val req = mapOf("tableId" to table.id, "status" to "OCCUPIED", "currentTotal" to currentTotal, "orderedItems" to cart, "itemNotes" to notes, "checkInTime" to time)
                                val res = RetrofitClient.instance.updateTableOrder(req)
                                if (res.isSuccessful) {
                                    Toast.makeText(context, "Đã gửi bếp thành công!", Toast.LENGTH_SHORT).show()
                                    onBack()
                                }
                            } catch (e: Exception) { Toast.makeText(context, "Lỗi kết nối API!", Toast.LENGTH_SHORT).show() }
                        }
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = goldPremium),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("GỬI BẾP", fontSize = 11.sp, fontWeight = FontWeight.Bold) }

                // TẠM TÍNH
                Button(
                    onClick = {
                        scope.launch {
                            isCapturing = true
                            delay(200)
                            captureController.capture()
                        }
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("TẠM TÍNH", fontSize = 11.sp, fontWeight = FontWeight.Bold) }

                // THANH TOÁN
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                val offlineOrder = OrderEntity(id = UUID.randomUUID().toString(), tableName = table.name, totalAmount = currentTotal, details = jsonDetailsForRoom, isSynced = 0)
                                database.orderDao().insertOrder(offlineOrder)

                                // ✅ SỬA LỖI TẠI ĐÂY: Dùng đường dẫn đầy đủ để tránh trùng tên với thư viện Compose UI
                                val constraints = androidx.work.Constraints.Builder()
                                    .setRequiredNetworkType(NetworkType.CONNECTED)
                                    .build()

                                val syncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
                                    .setConstraints(constraints)
                                    .build()
                                WorkManager.getInstance(context).enqueue(syncRequest)

                                RetrofitClient.instance.payAndClearTable(mapOf("tableId" to table.id, "totalAmount" to currentTotal, "tableName" to table.name, "orderedItems" to cart, "itemNotes" to notes))
                                Toast.makeText(context, "Thanh toán thành công!", Toast.LENGTH_SHORT).show()
                                onBack()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Lỗi Server. Đã lưu offline.", Toast.LENGTH_SHORT).show()
                                onBack()
                            }
                        }
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = navyDark),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("T.TOÁN", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            }
        }

        // --- DIALOG GHI CHÚ ---
        if (showNoteDialogFor != null) {
            AlertDialog(
                onDismissRequest = { showNoteDialogFor = null },
                title = { Text("Ghi chú món") },
                text = { OutlinedTextField(value = noteInput, onValueChange = { noteInput = it }, modifier = Modifier.fillMaxWidth()) },
                confirmButton = {
                    Button(onClick = {
                        val newNotes = notes.toMutableMap()
                        newNotes[showNoteDialogFor!!] = noteInput
                        notes = newNotes
                        showNoteDialogFor = null
                    }) { Text("LƯU") }
                }
            )
        }
    }
}

/**
 * GIAO DIỆN HÓA ĐƠN LUXURY
 */
@Composable
fun LuxuryReceiptContent(
    tableName: String, cart: Map<String, Int>, menu: List<CategoryModel>,
    total: Double, notes: Map<String, String>, isCapturing: Boolean,
    onUpdateCart: (String, Int) -> Unit, onShowNote: (String, String) -> Unit
) {
    val formatter = DecimalFormat("#,###")
    val currentTime = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(java.util.Date())

    Column(
        modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("LUXURY COFFEE", style = TextStyle(fontSize = 26.sp, fontWeight = FontWeight.Black, letterSpacing = 5.sp))
        Text("EXPERIENCE THE DIFFERENCE", style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp))
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(thickness = 2.5.dp, color = Color.Black)
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("BÀN: ${tableName.uppercase()}", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
            Text(currentTime, fontSize = 12.sp)
        }
        HorizontalDivider(thickness = 1.dp, color = Color.Black)
        Spacer(modifier = Modifier.height(10.dp))
        cart.forEach { (id, qty) ->
            val product = menu.flatMap { it.products }.find { it.id == id }
            if (product != null) {
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.name.uppercase(), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            if (!notes[id].isNullOrBlank()) { Text(" *${notes[id]}", fontSize = 11.sp, fontStyle = FontStyle.Italic, color = Color.DarkGray) }
                        }
                        if (isCapturing) { Text("x$qty", fontWeight = FontWeight.Bold, fontSize = 14.sp) }
                        else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { onUpdateCart(id, -1) }, modifier = Modifier.size(28.dp)) { Icon(Icons.Default.RemoveCircleOutline, null, tint = Color.Red, modifier = Modifier.size(20.dp)) }
                                Text("$qty", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp), fontSize = 15.sp)
                                IconButton(onClick = { onUpdateCart(id, 1) }, modifier = Modifier.size(28.dp)) { Icon(Icons.Default.AddCircleOutline, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(20.dp)) }
                                IconButton(onClick = { onShowNote(id, notes[id] ?: "") }, modifier = Modifier.size(28.dp)) { Icon(Icons.Default.EditNote, null, tint = Color.Gray, modifier = Modifier.size(20.dp)) }
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(formatter.format(product.price * qty), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(thickness = 2.5.dp, color = Color.Black)
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 15.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("TỔNG CỘNG", style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.ExtraBold))
            Text("${formatter.format(total)} đ", style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Black))
        }
        HorizontalDivider(thickness = 1.dp, color = Color.Black)
        Spacer(modifier = Modifier.height(24.dp))
        Text("CẢM ƠN QUÝ KHÁCH & HẸN GẶP LẠI", fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Text("WWW.LUXURYCOFFEE.COM", fontSize = 9.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(10.dp))
        Text("* * * * * * * * * * * *", letterSpacing = 4.sp)
    }
}

fun shareBitmapToFunPrint(context: android.content.Context, bitmap: Bitmap) {
    try {
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()
        val imageFile = File(cachePath, "temp_bill.png")
        val stream = FileOutputStream(imageFile)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.close()
        val contentUri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", imageFile)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, contentUri)
            type = "image/png"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "In hóa đơn"))
    } catch (e: Exception) { Log.e("SHARE", "Error: ${e.message}") }
}