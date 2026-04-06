package com.example.posapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// ✅ 1. Sửa đường dẫn import về đúng nơi chứa Model
import com.example.posapp.api.ProductModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PosViewModel @Inject constructor() : ViewModel() {


    private val _products = MutableStateFlow<List<ProductModel>>(emptyList())
    val products: StateFlow<List<ProductModel>> = _products

    fun loadProducts() {
        viewModelScope.launch {
            _products.value = listOf(

                ProductModel(name = "Coca Cola", price = 10000.0),
                ProductModel(name = "Pepsi", price = 9000.0),
                ProductModel(name = "Snack", price = 15000.0)
            )
        }
    }
}