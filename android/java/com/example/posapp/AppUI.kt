package com.example.posapp

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.*
// ✅ THÊM 3 DÒNG IMPORT NÀY LÀ HẾT SẠCH LỖI:
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.posapp.api.TableModel

import com.example.posapp.ui.screen.*

@Composable
fun AppUI() {
    // Các State quản lý điều hướng
    var currentScreen by remember { mutableStateOf("LOGIN") }
    var isAdmin by remember { mutableStateOf(false) }
    var selectedTable by remember { mutableStateOf<TableModel?>(null) } // Hết lỗi Null và Delegate

    Crossfade(targetState = currentScreen, label = "AppTransition") { screen ->
        when (screen) {
            "LOGIN" -> {
                LoginScreen { role ->
                    isAdmin = (role == "ADMIN")
                    currentScreen = "DASHBOARD"
                }
            }

            "DASHBOARD" -> {
                if (isAdmin) {
                    AdminDashboardScreen(
                        onNavigateToTableConfig = { currentScreen = "TABLE_CONFIG" },
                        onNavigateToMenuConfig = { currentScreen = "MENU_CONFIG" },
                        onNavigateToSodophong = { currentScreen = "SODO_PHONG" },
                        onNavigateToReport = { currentScreen = "REPORT_SCREEN" },
                        onNavigateToStaff = { currentScreen = "STAFF_SCREEN" },
                        onNavigateToCustomer = { currentScreen = "CUSTOMER_SCREEN" },
                        onLogout = {
                            isAdmin = false
                            currentScreen = "LOGIN"
                        }
                    )
                } else {
                    StaffDashboardScreen(
                        onNavigateToSodophong = { currentScreen = "SODO_PHONG" },
                        onNavigateToCustomer = { currentScreen = "CUSTOMER_SCREEN" },
                        onLogout = {
                            isAdmin = false
                            currentScreen = "LOGIN"
                        }
                    )
                }
            }

            "SODO_PHONG" -> {
                TableScreen(
                    isAdmin = isAdmin,
                    onBack = { currentScreen = "DASHBOARD" },
                    onNavigateToConfig = { currentScreen = "TABLE_CONFIG" },
                    onTableClick = { table ->
                        selectedTable = table
                        currentScreen = "ORDER_SCREEN"
                    }
                )
            }

            "ORDER_SCREEN" -> {
                OrderScreen(
                    table = selectedTable,
                    onBack = { currentScreen = "SODO_PHONG" },
                    onNavigateToMoveTable = { currentScreen = "SODO_PHONG" }
                )
            }

            "TABLE_CONFIG" -> TableConfigScreen(onBack = { currentScreen = "DASHBOARD" })
            "MENU_CONFIG" -> MenuConfigScreen(onBack = { currentScreen = "DASHBOARD" })
            "REPORT_SCREEN" -> ReportScreen(onBack = { currentScreen = "DASHBOARD" })
            "STAFF_SCREEN" -> StaffScreen(onBack = { currentScreen = "DASHBOARD" })
            "CUSTOMER_SCREEN" -> CustomerScreen(onBack = { currentScreen = "DASHBOARD" })
        }
    }
}