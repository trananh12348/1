package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.entity.MenuItemEntity
import com.example.data.entity.OrderEntity
import com.example.data.pref.BankPrefManager
import com.example.data.repository.FoodRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

data class DailyStat(val date: String, val revenue: Double, val profit: Double, val orderCount: Int)
data class MonthlyStat(val month: String, val revenue: Double, val profit: Double, val orderCount: Int)

class FoodViewModel(
    private val repository: FoodRepository,
    private val bankPrefManager: BankPrefManager
) : ViewModel() {

    // Menu list from Room
    val menuItems: StateFlow<List<MenuItemEntity>> = repository.allMenuItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Order list from Room
    val orders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Order flows for process tracking
    val pendingOrders: StateFlow<List<OrderEntity>> = orders.map { list ->
        list.filter { it.status == "PROCESSING" }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val finishedOrders: StateFlow<List<OrderEntity>> = orders.map { list ->
        list.filter { it.status == "FINISHED" }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val paidOrders: StateFlow<List<OrderEntity>> = orders.map { list ->
        list.filter { it.status == "PAID" }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart state: Map of Item to Quantity
    private val _cart = MutableStateFlow<Map<MenuItemEntity, Int>>(emptyMap())
    val cart: StateFlow<Map<MenuItemEntity, Int>> = _cart.asStateFlow()

    // Active order awaiting QR payment display
    private val _activePaymentOrder = MutableStateFlow<OrderEntity?>(null)
    val activePaymentOrder: StateFlow<OrderEntity?> = _activePaymentOrder.asStateFlow()

    // Bank configurations matching merchant details
    private val _bankName = MutableStateFlow(bankPrefManager.bankName)
    val bankName: StateFlow<String> = _bankName.asStateFlow()

    private val _accountNumber = MutableStateFlow(bankPrefManager.accountNumber)
    val accountNumber: StateFlow<String> = _accountNumber.asStateFlow()

    private val _accountOwner = MutableStateFlow(bankPrefManager.accountOwner)
    val accountOwner: StateFlow<String> = _accountOwner.asStateFlow()

    private val _useCustomQr = MutableStateFlow(bankPrefManager.useCustomQr)
    val useCustomQr: StateFlow<Boolean> = _useCustomQr.asStateFlow()

    private val _customQrPath = MutableStateFlow(bankPrefManager.customQrPath)
    val customQrPath: StateFlow<String> = _customQrPath.asStateFlow()

    private val _appTheme = MutableStateFlow(bankPrefManager.appTheme)
    val appTheme: StateFlow<String> = _appTheme.asStateFlow()

    private val _isSoundEnabled = MutableStateFlow(bankPrefManager.isSoundEnabled)
    val isSoundEnabled: StateFlow<Boolean> = _isSoundEnabled.asStateFlow()

    private val _categories = MutableStateFlow(bankPrefManager.categories)
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    private val _selectedProcessSubTab = MutableStateFlow(0)
    val selectedProcessSubTab: StateFlow<Int> = _selectedProcessSubTab.asStateFlow()

    fun selectProcessSubTab(index: Int) {
        _selectedProcessSubTab.value = index
    }

    init {
        // Pre-populate database with default items on startup if missing
        viewModelScope.launch {
            repository.prePopulateIfEmpty()
        }
        com.example.ui.util.SoundHelper.isSoundEnabled = bankPrefManager.isSoundEnabled
    }

    // --- CART ACTIONS ---
    fun addToCart(item: MenuItemEntity) {
        val current = _cart.value.toMutableMap()
        current[item] = (current[item] ?: 0) + 1
        _cart.value = current
    }

    fun decreaseQuantity(item: MenuItemEntity) {
        val current = _cart.value.toMutableMap()
        val count = current[item] ?: 0
        if (count > 1) {
            current[item] = count - 1
        } else {
            current.remove(item)
        }
        _cart.value = current
    }

    fun removeFromCart(item: MenuItemEntity) {
        val current = _cart.value.toMutableMap()
        current.remove(item)
        _cart.value = current
    }

    fun clearCart() {
        _cart.value = emptyMap()
    }

    // --- ORDER ACTIONS ---
    fun placeOrder() {
        val cartItems = _cart.value
        if (cartItems.isEmpty()) return

        viewModelScope.launch {
            var totalAmount = 0.0
            var totalProfit = 0.0
            val summaryList = mutableListOf<String>()
            val detailsList = mutableListOf<String>()

            cartItems.forEach { (item, qty) ->
                val priceSum = item.price * qty
                val profitSum = (item.price - item.cost) * qty
                totalAmount += priceSum
                totalProfit += profitSum
                summaryList.add("${item.name} (x$qty)")
                // Create simple json details representation
                detailsList.add("{\"name\":\"${item.name}\",\"qty\":$qty,\"price\":${item.price},\"profit\":${item.price - item.cost}}")
            }

            val itemsSummary = summaryList.joinToString(", ")
            val itemsJson = "[${detailsList.joinToString(",")}]"

            // Sinh mã nội dung chuyển khoản số random trong phạm vi < 10000 (1..9999)
            val randomMemoCode = Random.nextInt(1, 10000)

            val newOrder = OrderEntity(
                timestamp = System.currentTimeMillis(),
                totalAmount = totalAmount,
                totalProfit = totalProfit,
                memoCode = randomMemoCode,
                itemsSummary = itemsSummary,
                itemsJson = itemsJson,
                bankAccount = bankPrefManager.accountNumber,
                bankName = bankPrefManager.bankName,
                accountOwner = bankPrefManager.accountOwner,
                status = "PROCESSING"
            )

            repository.insertOrder(newOrder)
            clearCart()
        }
    }

    fun updateOrderStatus(order: OrderEntity, newStatus: String) {
        viewModelScope.launch {
            repository.updateOrder(order.copy(status = newStatus))
        }
    }

    fun showPaymentQr(order: OrderEntity) {
        _activePaymentOrder.value = order
    }

    fun markOrderAsPaid(order: OrderEntity) {
        viewModelScope.launch {
            repository.updateOrder(order.copy(status = "PAID"))
            if (_activePaymentOrder.value?.id == order.id) {
                _activePaymentOrder.value = null
            }
        }
    }

    fun dismissPayment() {
        _activePaymentOrder.value = null
    }

    fun deleteOrder(orderId: Long) {
        viewModelScope.launch {
            repository.deleteOrderById(orderId)
        }
    }

    // --- MENU MANAGEMENT ---
    fun addMenuItem(name: String, price: Double, cost: Double, category: String, imageUrl: String = "") {
        viewModelScope.launch {
            repository.insertMenuItem(
                MenuItemEntity(
                    name = name,
                    price = price,
                    cost = cost,
                    category = category,
                    imageUrl = imageUrl
                )
            )
        }
    }

    fun updateMenuItem(item: MenuItemEntity) {
        viewModelScope.launch {
            repository.updateMenuItem(item)
        }
    }

    fun deleteMenuItem(item: MenuItemEntity) {
        viewModelScope.launch {
            repository.deleteMenuItem(item)
        }
    }

    // --- MERCH BANKING ACTIONS ---
    fun updateBankConfig(bank: String, account: String, owner: String) {
        bankPrefManager.bankName = bank
        bankPrefManager.accountNumber = account
        bankPrefManager.accountOwner = owner.uppercase()

        _bankName.value = bank
        _accountNumber.value = account
        _accountOwner.value = owner.uppercase()
    }

    fun updateCustomQrConfig(useCustom: Boolean, path: String) {
        bankPrefManager.useCustomQr = useCustom
        bankPrefManager.customQrPath = path

        _useCustomQr.value = useCustom
        _customQrPath.value = path
    }

    fun updateAppTheme(theme: String) {
        bankPrefManager.appTheme = theme
        _appTheme.value = theme
    }

    fun updateSoundEnabled(enabled: Boolean) {
        bankPrefManager.isSoundEnabled = enabled
        _isSoundEnabled.value = enabled
        com.example.ui.util.SoundHelper.isSoundEnabled = enabled
    }

    // --- CATEGORY MANAGEMENT ACTIONS ---
    fun addCategory(categoryName: String) {
        val current = _categories.value.toMutableList()
        val trimmed = categoryName.trim()
        if (trimmed.isNotEmpty() && !current.contains(trimmed)) {
            current.add(trimmed)
            bankPrefManager.categories = current
            _categories.value = current
        }
    }

    fun renameCategory(oldName: String, newName: String) {
        val current = _categories.value.toMutableList()
        val trimmedNew = newName.trim()
        if (trimmedNew.isNotEmpty() && trimmedNew != oldName) {
            val index = current.indexOf(oldName)
            if (index != -1) {
                current[index] = trimmedNew
                bankPrefManager.categories = current
                _categories.value = current
                
                // Cập nhật tất cả các món ăn thuộc nhóm cũ này sang nhóm mới
                viewModelScope.launch {
                    val itemsToUpdate = menuItems.value.filter { it.category == oldName }
                    itemsToUpdate.forEach { item ->
                        repository.updateMenuItem(item.copy(category = trimmedNew))
                    }
                }
            }
        }
    }

    fun deleteCategory(categoryName: String) {
        val current = _categories.value.toMutableList()
        if (current.remove(categoryName)) {
            if (current.isEmpty()) {
                current.add("Món chính")
            }
            bankPrefManager.categories = current
            _categories.value = current
            
            // Chuyển món ăn của nhóm bị xóa sang nhóm đầu tiên còn lại làm dự phòng
            val fallbackCategory = current.first()
            viewModelScope.launch {
                val itemsToUpdate = menuItems.value.filter { it.category == categoryName }
                itemsToUpdate.forEach { item ->
                    repository.updateMenuItem(item.copy(category = fallbackCategory))
                }
            }
        }
    }

    // --- STATS DERIVED STATEFLOWS ---
    val dailyStats: StateFlow<List<DailyStat>> = orders.map { orderList ->
        val paidOrders = orderList.filter { it.status == "PAID" }
        val groups = paidOrders.groupBy { order ->
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(order.timestamp))
        }
        groups.map { (date, list) ->
            DailyStat(
                date = date,
                revenue = list.sumOf { it.totalAmount },
                profit = list.sumOf { it.totalProfit },
                orderCount = list.size
            )
        }.sortedByDescending { stat ->
            groups[stat.date]?.maxOfOrNull { it.timestamp } ?: 0L
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val monthlyStats: StateFlow<List<MonthlyStat>> = orders.map { orderList ->
        val paidOrders = orderList.filter { it.status == "PAID" }
        val groups = paidOrders.groupBy { order ->
            SimpleDateFormat("MM/yyyy", Locale.getDefault()).format(Date(order.timestamp))
        }
        groups.map { (month, list) ->
            MonthlyStat(
                month = month,
                revenue = list.sumOf { it.totalAmount },
                profit = list.sumOf { it.totalProfit },
                orderCount = list.size
            )
        }.sortedByDescending { stat ->
            groups[stat.month]?.maxOfOrNull { it.timestamp } ?: 0L
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

class FoodViewModelFactory(
    private val repository: FoodRepository,
    private val bankPrefManager: BankPrefManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FoodViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FoodViewModel(repository, bankPrefManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
