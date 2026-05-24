package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.data.db.FoodDatabase
import com.example.data.pref.BankPrefManager
import com.example.data.repository.FoodRepository
import com.example.ui.screens.AddEditItemDialog
import com.example.ui.screens.ConfigScreen
import com.example.ui.screens.ProcessScreen
import com.example.ui.screens.QrPaymentDialog
import com.example.ui.screens.SaleScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.util.SoundHelper
import com.example.ui.viewmodel.FoodViewModel
import com.example.ui.viewmodel.FoodViewModelFactory

class MainActivity : ComponentActivity() {

    // Lazy initialization of Database and Repository layers
    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            FoodDatabase::class.java,
            "com_ngon_qr_food_db"
        )
        .fallbackToDestructiveMigration(dropAllTables = true) // Supports smooth database upgrades
        .build()
    }

    private val repository by lazy {
        FoodRepository(database.foodDao())
    }

    private val bankPrefManager by lazy {
        BankPrefManager(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = androidx.activity.SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = androidx.activity.SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )

        val viewModel: FoodViewModel by viewModels {
            FoodViewModelFactory(repository, bankPrefManager)
        }

        setContent {
            val appTheme by viewModel.appTheme.collectAsState()
            MyApplicationTheme(themeName = appTheme) {
                MainLayout(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLayout(viewModel: FoodViewModel) {
    var activeTab by remember { mutableStateOf("sale") } // Tabs: "sale", "process", "stats", "config"
    val activePaymentOrder by viewModel.activePaymentOrder.collectAsState()
    val context = LocalContext.current
    val categoriesList by viewModel.categories.collectAsState()
    var showGlobalAddDialog by remember { mutableStateOf(false) }

    if (showGlobalAddDialog) {
        AddEditItemDialog(
            title = "Thêm Món Ăn Mới",
            categoriesList = categoriesList,
            onDismiss = { showGlobalAddDialog = false },
            onConfirm = { name, price, cost, category, imageUrl ->
                viewModel.addMenuItem(name, price, cost, category, imageUrl)
                showGlobalAddDialog = false
                Toast.makeText(context, "Đã thêm món: $name", Toast.LENGTH_SHORT).show()
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = when (activeTab) {
                                "sale" -> "Gọi Món & Bán Hàng"
                                "process" -> "Xử Lý Đơn Hàng"
                                "stats" -> "Doanh Thu & Lợi Nhuận"
                                "config" -> "Thiết Lập Thực Đơn"
                                else -> "Cơm Ngon QR"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = when (activeTab) {
                                "sale" -> "Bấm chọn để thêm vào giỏ hàng"
                                "process" -> "Chuẩn bị đơn & Thanh toán nhanh"
                                "stats" -> "Thống kê lãi ròng chi tiết"
                                "config" -> "Cài đặt tài khoản ngân hàng"
                                else -> "Thanh toán thông minh"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            val pendingOrders by viewModel.pendingOrders.collectAsState()
            val finishedOrders by viewModel.finishedOrders.collectAsState()
            val pendingCount = pendingOrders.size
            val finishedCount = finishedOrders.size

            CustomBottomAppBar(
                activeTab = activeTab,
                onTabSelected = { activeTab = it },
                onAddClick = {
                    activeTab = "process"
                    viewModel.selectProcessSubTab(1)
                },
                pendingCount = pendingCount,
                finishedCount = finishedCount
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            when (activeTab) {
                "sale" -> SaleScreen(viewModel = viewModel)
                "process" -> ProcessScreen(viewModel = viewModel)
                "stats" -> StatsScreen(viewModel = viewModel)
                "config" -> ConfigScreen(viewModel = viewModel)
            }
        }
    }

    // Hiển thị cửa sổ QR chuyển khoản của đơn hàng vừa đặt thành công
    activePaymentOrder?.let { order ->
        QrPaymentDialog(
            order = order,
            viewModel = viewModel,
            onDismiss = { viewModel.dismissPayment() }
        )
    }
}

// Custom Bottom App Bar matches the exact shape, gap and color in FoodGo image
@Composable
fun CustomBottomAppBar(
    activeTab: String,
    onTabSelected: (String) -> Unit,
    onAddClick: () -> Unit,
    pendingCount: Int,
    finishedCount: Int
) {
    val density = LocalContext.current.resources.displayMetrics.density
    val cutoutRadiusPx = 36f * density
    val cornerRadiusPx = 28f * density

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding() // Support system gesture navigation padding safely
            .wrapContentHeight()
    ) {
        // Red Curved Bar Background
        Surface(
            color = MaterialTheme.colorScheme.primary,
            shape = CurvedCutoutShape(cutoutRadius = cutoutRadiusPx, cornerRadius = cornerRadiusPx),
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .align(Alignment.BottomCenter),
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Part: Home (Bán Hàng) + Process (Xử Lý Đơn)
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BottomNavItem(
                        selected = activeTab == "sale",
                        onClick = { onTabSelected("sale") },
                        icon = Icons.Outlined.Home,
                        selectedIcon = Icons.Filled.Home,
                        contentDescription = "Bán Hàng"
                    )

                    BottomNavItem(
                        selected = activeTab == "process",
                        onClick = { onTabSelected("process") },
                        icon = Icons.AutoMirrored.Outlined.Assignment,
                        selectedIcon = Icons.AutoMirrored.Filled.Assignment,
                        contentDescription = "Xử Lý Đơn",
                        badgeCount = if (pendingCount > 0 || finishedCount > 0) "$pendingCount/$finishedCount" else null
                    )
                }

                // Symmetrical gap inside the bar to account for the cutout and the FAB
                Spacer(modifier = Modifier.width(72.dp))

                // Right Part: Stats (Thống Kê) + Config (Cấu Hình)
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BottomNavItem(
                        selected = activeTab == "stats",
                        onClick = { onTabSelected("stats") },
                        icon = Icons.AutoMirrored.Outlined.TrendingUp,
                        selectedIcon = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = "Thống Kê"
                    )

                    BottomNavItem(
                        selected = activeTab == "config",
                        onClick = { onTabSelected("config") },
                        icon = Icons.Outlined.Person,
                        selectedIcon = Icons.Filled.Person,
                        contentDescription = "Cấu Hình"
                    )
                }
            }
        }

        // Coral-red Circular FAB layered exactly in the cutout space with background matching border stroke
        FloatingActionButton(
            onClick = {
                SoundHelper.playClickSound()
                onAddClick()
            },
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 0.dp) // Sits overlapping
                .size(56.dp)
                .border(4.dp, MaterialTheme.colorScheme.background, CircleShape),
            elevation = FloatingActionButtonDefaults.elevation(0.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Xem đơn xử lý xong",
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Composable
fun BottomNavItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    badgeCount: String? = null
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null, // Disable default material blocky ripple for custom clean sleek feel
                onClick = {
                    SoundHelper.playClickSound()
                    onClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (selected) selectedIcon else icon,
                    contentDescription = contentDescription,
                    tint = if (selected) Color.White else Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(24.dp)
                )

                if (badgeCount != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 10.dp, y = (-4).dp)
                            .background(Color.White, CircleShape)
                            .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            .padding(horizontal = 4.dp, vertical = 0.5.dp)
                    ) {
                        Text(
                            text = badgeCount,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // High-fidelity dot indicator below active item
            if (selected) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(Color.White, CircleShape)
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

class CurvedCutoutShape(
    val cutoutRadius: Float,
    val cornerRadius: Float
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val w = size.width
            val h = size.height
            val cx = w / 2f

            moveTo(0f, cornerRadius)
            quadraticTo(0f, 0f, cornerRadius, 0f)

            val startDipX = cx - cutoutRadius
            val endDipX = cx + cutoutRadius

            lineTo(startDipX, 0f)

            // Curved smooth dip in the middle
            cubicTo(
                x1 = startDipX + cutoutRadius * 0.25f, y1 = 0f,
                x2 = cx - cutoutRadius * 0.55f, y2 = cutoutRadius * 0.9f,
                x3 = cx, y3 = cutoutRadius * 0.9f
            )
            cubicTo(
                x1 = cx + cutoutRadius * 0.55f, y1 = cutoutRadius * 0.9f,
                x2 = endDipX - cutoutRadius * 0.25f, y2 = 0f,
                x3 = endDipX, y3 = 0f
            )

            lineTo(w - cornerRadius, 0f)
            quadraticTo(w, 0f, w, cornerRadius)

            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        return Outline.Generic(path)
    }
}
