package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.entity.MenuItemEntity
import com.example.ui.viewmodel.FoodViewModel
import java.io.File

@Composable
fun ConfigScreen(viewModel: FoodViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val menuItems by viewModel.menuItems.collectAsState()

    val currentBankName by viewModel.bankName.collectAsState()
    val currentAccountNumber by viewModel.accountNumber.collectAsState()
    val currentAccountOwner by viewModel.accountOwner.collectAsState()

    var editingBankName by remember(currentBankName) { mutableStateOf(currentBankName) }
    var editingAccountNumber by remember(currentAccountNumber) { mutableStateOf(currentAccountNumber) }
    var editingAccountOwner by remember(currentAccountOwner) { mutableStateOf(currentAccountOwner) }

    var showAddMenuDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<MenuItemEntity?>(null) }
    var showBankSelectDialog by remember { mutableStateOf(false) }

    // Danh sách ngân hàng phổ biến hỗ trợ VietQR nhanh chóng
    val availableBanks = remember {
        listOf("MB", "Vietcombank", "Techcombank", "VPBank", "ACB", "Vietinbank", "BIDV", "Agribank", "TPBank")
    }

    val useCustomQr by viewModel.useCustomQr.collectAsState()
    val customQrPath by viewModel.customQrPath.collectAsState()
    var editingCustomQrUrl by remember(customQrPath) { mutableStateOf(customQrPath) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val file = File(context.filesDir, "custom_qr_image.png")
                    file.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                    viewModel.updateCustomQrConfig(true, file.absolutePath)
                    editingCustomQrUrl = file.absolutePath
                    Toast.makeText(context, "Đã lưu ảnh QR từ thiết bị thành công!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Lỗi khi lưu ảnh: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    var activeConfigTab by remember { mutableStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // TabRow modern M3 layout for clear configurations
        TabRow(
            selectedTabIndex = activeConfigTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = activeConfigTab == 0,
                onClick = { activeConfigTab = 0 },
                text = { Text("Cấu Hình Hệ Thống", fontWeight = FontWeight.Bold, fontSize = 14.sp) },
                icon = { Icon(Icons.Default.Settings, contentDescription = "Hệ thống") }
            )
            Tab(
                selected = activeConfigTab == 1,
                onClick = { activeConfigTab = 1 },
                text = { Text("Quản Lý Thực Đơn", fontWeight = FontWeight.Bold, fontSize = 14.sp) },
                icon = { Icon(Icons.Default.RestaurantMenu, contentDescription = "Thực đơn") }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (activeConfigTab == 0) {
                // --- PHẦN 1.76: CẤU HÌNH ÂM THANH ỨNG DỤNG (ĐƯA LÊN ĐẦU CHO DỄ THẤY) ---
                item {
                    val isSoundEnabled by viewModel.isSoundEnabled.collectAsState()
                    val isPearl = MaterialTheme.colorScheme.primary == Color(0xFF111111)
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSoundEnabled) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.04f)
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (isSoundEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isPearl) 0.dp else 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (isSoundEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "ÂM THANH ỨNG DỤNG",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "Bật/tắt âm thanh khi bấm nút và hoàn thành đơn hàng",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                                Switch(
                                    checked = isSoundEnabled,
                                    onCheckedChange = { isChecked ->
                                        viewModel.updateSoundEnabled(isChecked)
                                    }
                                )
                            }
                        }
                    }
                }

                // --- PHẦN 1: CẤU HÌNH NGÂN HÀNG THỤ HƯỞNG ---
                item {
                    val isPearl = MaterialTheme.colorScheme.primary == Color(0xFF111111)
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = if (isPearl) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant) else null,
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isPearl) 0.dp else 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "CẤU HÌNH NHẬN TIỀN CHUYỂN KHOẢN",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // Ngân hàng drowdown kích hoạt bằng Dialog
                            OutlinedTextField(
                                value = editingBankName,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Ngân hàng nhận tiền") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showBankSelectDialog = true },
                                trailingIcon = {
                                    IconButton(onClick = { showBankSelectDialog = true }) {
                                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Chọn ngân hàng")
                                    }
                                },
                                enabled = false, // Kích hoạt sự kiện bấm thông qua Modifier.clickable
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                                    disabledLabelColor = MaterialTheme.colorScheme.onSurface,
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Số tài khoản nhận
                            OutlinedTextField(
                                value = editingAccountNumber,
                                onValueChange = { editingAccountNumber = it.filter { digit -> digit.isDigit() } },
                                label = { Text("Số tài khoản thụ hưởng") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Tên chủ tài khoản nhận (Tự động viết hoa để QR thống nhất đẹp mắt)
                            OutlinedTextField(
                                value = editingAccountOwner,
                                onValueChange = { editingAccountOwner = it.uppercase() },
                                label = { Text("Họ & Tên chủ tài khoản") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    if (editingAccountNumber.isBlank() || editingAccountOwner.isBlank()) {
                                        Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin thụ hưởng!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        viewModel.updateBankConfig(
                                            editingBankName,
                                            editingAccountNumber,
                                            editingAccountOwner
                                        )
                                        Toast.makeText(context, "Lưu cấu hình ngân hàng thành công!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(imageVector = Icons.Default.Save, contentDescription = "Lưu")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Lưu Cấu Hình Nhận Tiền", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // --- PHẦN 1.5: CẤU HÌNH ẢNH QR RIÊNG THAY THẾ (HOẶC PHỤ) ---
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "SỬ DỤNG ẢNH QR CỦA BẠN",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Tải lên hoặc nhập URL ảnh QR riêng của cửa hàng",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                                Switch(
                                    checked = useCustomQr,
                                    onCheckedChange = { isChecked ->
                                        viewModel.updateCustomQrConfig(isChecked, customQrPath)
                                    }
                                )
                            }

                            if (useCustomQr) {
                                Spacer(modifier = Modifier.height(16.dp))

                                // Chọn ảnh từ Gallery hoặc nhập URL ảnh
                                OutlinedTextField(
                                    value = editingCustomQrUrl,
                                    onValueChange = {
                                        editingCustomQrUrl = it
                                        viewModel.updateCustomQrConfig(true, it)
                                    },
                                    label = { Text("Đường dẫn (URL hoặc cục bộ) ảnh QR") },
                                    placeholder = { Text("Ví dụ: https://example.com/qr.png") },
                                    modifier = Modifier.fillMaxWidth(),
                                    trailingIcon = {
                                        if (editingCustomQrUrl.isNotEmpty()) {
                                            IconButton(onClick = {
                                                editingCustomQrUrl = ""
                                                viewModel.updateCustomQrConfig(true, "")
                                            }) {
                                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Xóa")
                                            }
                                        }
                                    }
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { imagePickerLauncher.launch("image/*") },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    ) {
                                        Icon(imageVector = Icons.Default.Image, contentDescription = "Thư viện")
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Chọn Từ Thiết Bị", fontWeight = FontWeight.Bold)
                                    }

                                    if (editingCustomQrUrl.isNotEmpty() && !editingCustomQrUrl.startsWith("http")) {
                                        Button(
                                            onClick = {
                                                viewModel.updateCustomQrConfig(true, editingCustomQrUrl)
                                                Toast.makeText(context, "Đã áp dụng ảnh QR thiết bị!", Toast.LENGTH_SHORT).show()
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(imageVector = Icons.Default.Check, contentDescription = "Áp dụng")
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Áp Dụng", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                if (editingCustomQrUrl.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Xem trước ảnh QR riêng của bạn:",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Box(
                                        modifier = Modifier
                                            .size(160.dp)
                                            .align(Alignment.CenterHorizontally)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color.White)
                                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                            .padding(8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(context)
                                                .data(editingCustomQrUrl)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = "Xem trước QR",
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // --- PHẦN 1.75: GỢI Ý GIAO DIỆN CHUYÊN NGHIỆP ---
                item {
                    val appTheme by viewModel.appTheme.collectAsState()
                    val isPearl = MaterialTheme.colorScheme.primary == Color(0xFF111111)
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = if (isPearl) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant) else null,
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isPearl) 0.dp else 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Palette,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "PHONG CÁCH GIAO DIỆN",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Chọn cá tính ẩm thực cho cửa hàng của bạn",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            val themesList = listOf(
                                Triple("foodgo", "FoodGo Đỏ Coral (Mặc Định)", "Sắc đỏ coral thời thượng, rực rỡ và tràn đầy hứng khởi"),
                                Triple("indigo", "Bếp Đô Thị (Hiện Đại)", "Màu xanh tím thanh nhã tinh tế hiện đại")
                            )

                            themesList.forEach { (id, title, desc) ->
                                val isSelected = appTheme == id
                                val primaryCircleColor = when (id) {
                                    "foodgo" -> Color(0xFFEF3C46)
                                    else -> Color(0xFF1A237E)
                                }
                                val secondaryCircleColor = when (id) {
                                    "foodgo" -> Color(0xFFFFEBEC)
                                    else -> Color(0xFFAD1457)
                                }

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable { viewModel.updateAppTheme(id) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) {
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                                        } else {
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.40f)
                                        }
                                    ),
                                    border = if (isSelected) {
                                        androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                                    } else {
                                        null
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        // Vòng tròn kép màu sắc giao diện
                                        Box(
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(primaryCircleColor)
                                                    .align(Alignment.TopStart)
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(secondaryCircleColor)
                                                    .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                                                    .align(Alignment.BottomEnd)
                                            )
                                        }

                                        Column(
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(
                                                text = title,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = desc,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.outline
                                            )
                                        }

                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { viewModel.updateAppTheme(id) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // --- PHẦN 1.8: QUẢN LÝ PHÂN LOẠI MÓN ĂN (DANH MỤC) ---
                item {
                    val isPearl = MaterialTheme.colorScheme.primary == Color(0xFF111111)
                    var showAddCategoryDialog by remember { mutableStateOf(false) }
                    var categoryToRename by remember { mutableStateOf<String?>(null) }
                    val categoriesList by viewModel.categories.collectAsState()

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = if (isPearl) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant) else null,
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isPearl) 0.dp else 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Category,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "QUẢN LÝ PHÂN LOẠI MÓN ĂN",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "Thêm, chỉnh sửa hoặc xóa nhóm thực đơn",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = { showAddCategoryDialog = true },
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    ),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = "Thêm phân loại", modifier = Modifier.size(20.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Danh sách phân loại
                            if (categoriesList.isEmpty()) {
                                Text(
                                    text = "Chưa có phân loại nào. Bấm nút + để thêm.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    categoriesList.forEach { cat ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                                .padding(horizontal = 12.dp, vertical = 6.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = cat,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )

                                            Row {
                                                IconButton(
                                                    onClick = { categoryToRename = cat },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Edit,
                                                        contentDescription = "Sửa tên phân loại",
                                                        tint = MaterialTheme.colorScheme.primary,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }

                                                IconButton(
                                                    onClick = {
                                                        if (categoriesList.size <= 1) {
                                                            Toast.makeText(context, "Phải giữ lại ít nhất 1 phân loại mặc định!", Toast.LENGTH_SHORT).show()
                                                        } else {
                                                            viewModel.deleteCategory(cat)
                                                            Toast.makeText(context, "Đã xóa phân loại $cat", Toast.LENGTH_SHORT).show()
                                                        }
                                                    },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = "Xóa phân loại",
                                                        tint = MaterialTheme.colorScheme.error,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Dialog thêm phân loại mới
                    if (showAddCategoryDialog) {
                        var newCatName by remember { mutableStateOf("") }
                        AlertDialog(
                            onDismissRequest = { showAddCategoryDialog = false },
                            title = { Text("Thêm Phân Loại Mới", fontWeight = FontWeight.Bold) },
                            text = {
                                OutlinedTextField(
                                    value = newCatName,
                                    onValueChange = { newCatName = it },
                                    label = { Text("Tên phân loại") },
                                    placeholder = { Text("Ví dụ: Tráng miệng, Ăn vặt...") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        if (newCatName.isBlank()) {
                                            Toast.makeText(context, "Tên phân loại không được để trống!", Toast.LENGTH_SHORT).show()
                                        } else if (categoriesList.any { it.equals(newCatName.trim(), ignoreCase = true) }) {
                                            Toast.makeText(context, "Phân loại này đã tồn tại!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            viewModel.addCategory(newCatName)
                                            showAddCategoryDialog = false
                                            Toast.makeText(context, "Đã thêm phân loại thành công!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                ) {
                                    Text("Thêm")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showAddCategoryDialog = false }) {
                                    Text("Hủy")
                                }
                            }
                        )
                    }

                    // Dialog đổi tên phân loại
                    categoryToRename?.let { oldCat ->
                        var renameValue by remember { mutableStateOf(oldCat) }
                        AlertDialog(
                            onDismissRequest = { categoryToRename = null },
                            title = { Text("Sửa Tên Phân Loại", fontWeight = FontWeight.Bold) },
                            text = {
                                Column {
                                    Text(
                                        text = "Đổi tên phân loại từ \"$oldCat\" thành tên mới. Các món ăn thuộc nhóm phân loại cũ này cũng sẽ tự động đổi sang tên phân loại mới.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.outline,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    OutlinedTextField(
                                        value = renameValue,
                                        onValueChange = { renameValue = it },
                                        label = { Text("Tên phân loại mới") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        if (renameValue.isBlank()) {
                                            Toast.makeText(context, "Tên phân loại không được để trống!", Toast.LENGTH_SHORT).show()
                                        } else if (categoriesList.any { it.equals(renameValue.trim(), ignoreCase = true) && !it.equals(oldCat, ignoreCase = true) }) {
                                            Toast.makeText(context, "Tên phân loại mới này đã thuộc danh mục khác!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            viewModel.renameCategory(oldCat, renameValue)
                                            categoryToRename = null
                                            Toast.makeText(context, "Đã đổi tên phân loại thành công!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                ) {
                                    Text("Lưu")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { categoryToRename = null }) {
                                    Text("Hủy")
                                }
                            }
                        )
                    }
                }

                // --- PHẦN 2: QUẢN LÝ DANH SÁCH MÓN ĂN ---
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DANH SÁCH THỰC ĐƠN (${menuItems.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Button(
                            onClick = { showAddMenuDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Thêm Món", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Thiết lập danh sách món đang bán
                items(menuItems, key = { it.id }) { item ->
                    AdminMenuItemCard(
                        item = item,
                        onEditClick = { itemToEdit = item },
                        onDeleteClick = {
                            viewModel.deleteMenuItem(item)
                            Toast.makeText(context, "Đã xóa ${item.name}", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    // --- DIALOG CHỌN NGÂN HÀNG ---
    if (showBankSelectDialog) {
        Dialog(onDismissRequest = { showBankSelectDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .wrapContentHeight()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Chọn ngân hàng chuyển nhanh",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Divider()
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(
                        modifier = Modifier
                            .heightIn(max = 280.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        availableBanks.forEach { bank ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        editingBankName = bank
                                        showBankSelectDialog = false
                                    }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = bank,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                if (editingBankName == bank) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                    Button(
                        onClick = { showBankSelectDialog = false },
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 12.dp)
                    ) {
                        Text("Hủy")
                    }
                }
            }
        }
    }

    val categoriesList by viewModel.categories.collectAsState()

    // --- DIALOG THÊM MÓN ĂN MỚI ---
    if (showAddMenuDialog) {
        AddEditItemDialog(
            title = "Thêm Món Ăn Mới",
            categoriesList = categoriesList,
            onDismiss = { showAddMenuDialog = false },
            onConfirm = { name, price, cost, category, imageUrl ->
                viewModel.addMenuItem(name, price, cost, category, imageUrl)
                showAddMenuDialog = false
                Toast.makeText(context, "Đã thêm món: $name", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // --- DIALOG SỬA MÓN ĂN HIỆN CÓ ---
    itemToEdit?.let { item ->
        AddEditItemDialog(
            title = "Sửa Thông Tin Món Ăn",
            existingItem = item,
            categoriesList = categoriesList,
            onDismiss = { itemToEdit = null },
            onConfirm = { name, price, cost, category, imageUrl ->
                viewModel.updateMenuItem(item.copy(name = name, price = price, cost = cost, category = category, imageUrl = imageUrl))
                itemToEdit = null
                Toast.makeText(context, "Đã cập nhật món: $name", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun AdminMenuItemCard(
    item: MenuItemEntity,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val isPearl = MaterialTheme.colorScheme.primary == Color(0xFF111111)
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPearl) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        border = if (isPearl) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = if (isPearl) 0.dp else 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hiển thị Emoji / Ảnh và Nội dung chính
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(getFoodColor(item.name)),
                    contentAlignment = Alignment.Center
                ) {
                    if (item.imageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(item.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = item.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    } else {
                        Text(text = getEmojiForFood(item.name, item.category), fontSize = 20.sp)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Bán: ${formatVND(item.price)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Vốn: ${formatVND(item.cost)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Hàng hành động chỉnh sửa / xóa món ăn
            Row {
                IconButton(onClick = onEditClick) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Sửa", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Xóa", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun AddEditItemDialog(
    title: String,
    existingItem: MenuItemEntity? = null,
    categoriesList: List<String>,
    onDismiss: () -> Unit,
    onConfirm: (name: String, price: Double, cost: Double, category: String, imageUrl: String) -> Unit
) {
    var name by remember { mutableStateOf(existingItem?.name ?: "") }
    var priceText by remember { mutableStateOf(existingItem?.price?.toInt()?.toString() ?: "") }
    var costText by remember { mutableStateOf(existingItem?.cost?.toInt()?.toString() ?: "") }
    var category by remember { mutableStateOf(existingItem?.category ?: (categoriesList.firstOrNull() ?: "Món chính")) }
    var imageUrl by remember { mutableStateOf(existingItem?.imageUrl ?: "") }

    val dialogContext = LocalContext.current

    val itemImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                dialogContext.contentResolver.openInputStream(uri)?.use { inputStream ->
                    val file = File(dialogContext.filesDir, "product_${System.currentTimeMillis()}.png")
                    file.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                    imageUrl = file.absolutePath
                    Toast.makeText(dialogContext, "Đã chọn ảnh món ăn thành công!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(dialogContext, "Lỗi khi lưu ảnh: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                HorizontalDivider()

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên món ăn") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it.filter { digit -> digit.isDigit() } },
                        label = { Text("Giá Bán (đ)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = costText,
                        onValueChange = { costText = it.filter { digit -> digit.isDigit() } },
                        label = { Text("Giá Vốn (đ)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                // --- PHẦN MINH HỌA ẢNH MÓN ĂN ---
                Text(
                    text = "Hình ảnh đại diện món ăn",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Xem trước ảnh
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(getFoodColor(name))
                            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUrl.isNotEmpty()) {
                            AsyncImage(
                                model = ImageRequest.Builder(dialogContext)
                                    .data(imageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Xem trước ảnh món ăn",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        } else {
                            Text(text = getEmojiForFood(name, category), fontSize = 28.sp)
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Button(
                            onClick = { itemImagePickerLauncher.launch("image/*") },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Chọn ảnh từ máy", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        if (imageUrl.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            TextButton(
                                onClick = { imageUrl = "" },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text("Xóa ảnh (dùng Emoji gốc)", color = MaterialTheme.colorScheme.error, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Chọn phân loại bằng Row các RadioButton đơn giản hỗ trợ cuộn ngang nếu danh sách dài
                Column {
                    Text(
                        text = "Phân loại món ăn",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        categoriesList.forEach { cat ->
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { category = cat }
                                    .padding(vertical = 4.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = category == cat,
                                    onClick = { category = cat }
                                )
                                Text(text = cat, fontSize = 13.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Hủy")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val priceValue = priceText.toDoubleOrNull() ?: 0.0
                            val costValue = costText.toDoubleOrNull() ?: 0.0
                            if (name.isBlank() || priceValue <= 0) {
                                // Cảnh báo lướt qua
                            } else {
                                onConfirm(name, priceValue, costValue, category, imageUrl)
                            }
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Xác nhận")
                    }
                }
            }
        }
    }
}
