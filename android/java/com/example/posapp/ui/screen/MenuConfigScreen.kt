    package com.example.posapp.ui.screen

    import android.widget.Toast
    import androidx.compose.animation.Crossfade
    import androidx.compose.foundation.BorderStroke
    import androidx.compose.foundation.background
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
    import androidx.compose.ui.draw.clip
    import androidx.compose.ui.draw.shadow
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.text.input.KeyboardType
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import com.example.posapp.api.RetrofitClient
    // ✅ CHỈ IMPORT TỪ API PACKAGE
    import com.example.posapp.api.CategoryModel
    import com.example.posapp.api.ProductModel
    import kotlinx.coroutines.launch
    import java.text.DecimalFormat
    import java.util.UUID

    enum class MenuConfigView { LIST, ADD_CATEGORY, ADD_PRODUCT }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MenuConfigScreen(onBack: () -> Unit) {
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        val formatter = DecimalFormat("#,###đ")

        val bgApp = Color(0xFFF8FAFC)
        val textPrimary = Color(0xFF0F172A)
        val goldAccent = Color(0xFFF59E0B)
        val redSoft = Color(0xFFF43F5E)

        // ✅ State sử dụng đúng Type từ API package
        var categories by remember { mutableStateOf(listOf<CategoryModel>()) }
        var isLoading by remember { mutableStateOf(true) }
        var currentView by remember { mutableStateOf(MenuConfigView.LIST) }

        var selectedCategoryForProduct by remember { mutableStateOf<CategoryModel?>(null) }
        var catName by remember { mutableStateOf("") }
        var prodName by remember { mutableStateOf("") }
        var prodPrice by remember { mutableStateOf("") }

        LaunchedEffect(Unit) {
            try {
                categories = RetrofitClient.instance.getAllMenu()
            } catch (e: Exception) {
                Toast.makeText(context, "Không thể tải thực đơn", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }

        Crossfade(targetState = currentView, label = "") { view ->
            when (view) {
                MenuConfigView.LIST -> {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = { Text("Quản Lý Thực Đơn", fontWeight = FontWeight.ExtraBold) },
                                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } },
                                actions = {
                                    IconButton(onClick = { catName = ""; currentView = MenuConfigView.ADD_CATEGORY }) {
                                        Icon(Icons.Default.Category, null, tint = goldAccent)
                                    }
                                }
                            )
                        },
                        bottomBar = {
                            Button(
                                onClick = {
                                    scope.launch {
                                        try {
                                            RetrofitClient.instance.saveMenu(categories)
                                            Toast.makeText(context, "Đã lưu thành công", Toast.LENGTH_SHORT).show()
                                            onBack()
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Lỗi lưu: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = textPrimary),
                                shape = RoundedCornerShape(16.dp)
                            ) { Text("LƯU THỰC ĐƠN", fontWeight = FontWeight.Bold) }
                        }
                    ) { padding ->
                        if (isLoading) {
                            Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator(color = goldAccent) }
                        } else {
                            LazyColumn(modifier = Modifier.padding(padding).fillMaxSize().padding(horizontal = 20.dp)) {
                                items(categories) { category ->
                                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).shadow(4.dp, RoundedCornerShape(20.dp)).clip(RoundedCornerShape(20.dp)).background(Color.White).padding(16.dp)) {
                                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                                            Text(category.name, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                                            IconButton(onClick = { categories = categories.filter { it.id != category.id } }) { Icon(Icons.Default.DeleteOutline, null, tint = redSoft) }
                                        }

                                        HorizontalDivider(Modifier.padding(vertical = 12.dp), thickness = 1.dp, color = Color(0xFFF1F5F9))

                                        // Hiển thị sản phẩm (products lúc này đã được ánh xạ từ "items" trên server)
                                        category.products.forEach { product ->
                                            Row(Modifier.fillMaxWidth().padding(vertical = 4.dp).background(bgApp, RoundedCornerShape(12.dp)).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                                Column(Modifier.weight(1f)) {
                                                    Text(product.name, fontWeight = FontWeight.Bold)
                                                    Text(formatter.format(product.price), color = goldAccent)
                                                }
                                                IconButton(onClick = {
                                                    val updated = category.products.filter { it.id != product.id }
                                                    categories = categories.map { if (it.id == category.id) it.copy(products = updated) else it }
                                                }) { Icon(Icons.Default.Close, null, tint = redSoft) }
                                            }
                                        }

                                        Spacer(Modifier.height(12.dp))
                                        OutlinedButton(
                                            onClick = { selectedCategoryForProduct = category; prodName = ""; prodPrice = ""; currentView = MenuConfigView.ADD_PRODUCT },
                                            modifier = Modifier.fillMaxWidth()
                                        ) { Text("+ Thêm món") }
                                    }
                                }
                            }
                        }
                    }
                }

                MenuConfigView.ADD_CATEGORY -> {
                    Scaffold(topBar = { TopAppBar(title = { Text("Thêm Danh Mục") }, navigationIcon = { IconButton(onClick = { currentView = MenuConfigView.LIST }) { Icon(Icons.Default.Close, null) } }) }) { p ->
                        Column(Modifier.padding(p).padding(20.dp)) {
                            OutlinedTextField(value = catName, onValueChange = { catName = it }, label = { Text("Tên danh mục") }, modifier = Modifier.fillMaxWidth())
                            Button(onClick = {
                                if (catName.isNotBlank()) {
                                    categories = categories + CategoryModel(id = UUID.randomUUID().toString(), name = catName)
                                    currentView = MenuConfigView.LIST
                                }
                            }, modifier = Modifier.padding(top = 16.dp).fillMaxWidth()) { Text("XÁC NHẬN") }
                        }
                    }
                }

                MenuConfigView.ADD_PRODUCT -> {
                    Scaffold(topBar = { TopAppBar(title = { Text("Thêm Món Mới") }, navigationIcon = { IconButton(onClick = { currentView = MenuConfigView.LIST }) { Icon(Icons.Default.Close, null) } }) }) { p ->
                        Column(Modifier.padding(p).padding(20.dp)) {
                            OutlinedTextField(value = prodName, onValueChange = { prodName = it }, label = { Text("Tên món") }, modifier = Modifier.fillMaxWidth())
                            Spacer(Modifier.height(12.dp))
                            OutlinedTextField(value = prodPrice, onValueChange = { prodPrice = it }, label = { Text("Giá tiền") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                            Button(onClick = {
                                val pPrice = prodPrice.toDoubleOrNull() ?: 0.0
                                if (prodName.isNotBlank() && selectedCategoryForProduct != null) {
                                    val newProd = ProductModel(id = UUID.randomUUID().toString(), name = prodName, price = pPrice)
                                    categories = categories.map {
                                        if (it.id == selectedCategoryForProduct!!.id) it.copy(products = it.products + newProd) else it
                                    }
                                    currentView = MenuConfigView.LIST
                                }
                            }, modifier = Modifier.padding(top = 16.dp).fillMaxWidth()) { Text("XÁC NHẬN") }
                        }
                    }
                }
            }
        }
    }