package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.OrderEntity
import com.example.ui.util.SoundHelper
import com.example.ui.viewmodel.FoodViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProcessScreen(
    viewModel: FoodViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val selectedSubTab by viewModel.selectedProcessSubTab.collectAsState()

    val pendingOrders by viewModel.pendingOrders.collectAsState()
    val finishedOrders by viewModel.finishedOrders.collectAsState()
    val paidOrders by viewModel.paidOrders.collectAsState()

    val pendingCount = pendingOrders.size
    val finishedCount = finishedOrders.size

    val isPearl = MaterialTheme.colorScheme.primary == Color(0xFF111111)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // --- 1. THÀNH PHẦN THÔNG BÁO TÌNH TRẠNG ĐƠN ---
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "TÌNH TRẠNG PHỤC VỤ HIỆN TẠI",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Có $pendingCount đơn cần chuẩn bị • Có $finishedCount đơn chờ thanh toán",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // --- 2. TIÊU ĐỀ PHÂN CHIA (TABS) ---
        TabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedSubTab == 0,
                onClick = {
                    SoundHelper.playClickSound()
                    viewModel.selectProcessSubTab(0)
                },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Đang xử lý")
                        if (pendingCount > 0) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Badge(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            ) {
                                Text(pendingCount.toString())
                            }
                        }
                    }
                }
            )
            Tab(
                selected = selectedSubTab == 1,
                onClick = {
                    SoundHelper.playClickSound()
                    viewModel.selectProcessSubTab(1)
                },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Xử lý xong")
                        if (finishedCount > 0) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Badge(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ) {
                                Text(finishedCount.toString())
                            }
                        }
                    }
                }
            )
            Tab(
                selected = selectedSubTab == 2,
                onClick = {
                    SoundHelper.playClickSound()
                    viewModel.selectProcessSubTab(2)
                },
                text = { Text("Đã thanh toán") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- 3. HIỂN THỊ DANH SÁCH ĐƠN TƯƠNG ỨNG ---
        val currentList = when (selectedSubTab) {
            0 -> pendingOrders
            1 -> finishedOrders
            else -> paidOrders
        }

        if (currentList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = when (selectedSubTab) {
                            0 -> Icons.Default.Restaurant
                            1 -> Icons.Default.DoneAll
                            else -> Icons.Default.History
                        },
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                        modifier = Modifier.size(72.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = when (selectedSubTab) {
                            0 -> "Tuyệt vời! Không có đơn nào đang chờ xử lý."
                            1 -> "Không có đơn nào chờ thanh toán."
                            else -> "Chưa có lịch sử giao dịch thanh toán."
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = when (selectedSubTab) {
                            0 -> "Đặt món mới tại tab Bán Hàng để gửi đơn chế biến khẩn cấp."
                            1 -> "Đơn sau khi chế biến xong sẽ chuyển sang trạng thái này để thu tiền."
                            else -> "Đơn hoàn tất thanh toán VietQR sẽ được lưu trữ chính xác tại đây."
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = currentList,
                    key = { it.id }
                ) { order ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(animationSpec = spring()),
                        exit = fadeOut(animationSpec = spring())
                    ) {
                        OrderProcessCard(
                            order = order,
                            isPearl = isPearl,
                            selectedSubTab = selectedSubTab,
                            onActionClick = {
                                if (selectedSubTab == 0) {
                                    // Chuyển sang XỬ LÝ XONG -> Phát âm thanh hoàn thành (âm thanh xử lý xong đơn)
                                    SoundHelper.playSuccessSound(context)
                                    viewModel.updateOrderStatus(order, "FINISHED")
                                    Toast.makeText(context, "Đã hoàn thành chế biến đơn hàng #${order.id}", Toast.LENGTH_SHORT).show()
                                } else if (selectedSubTab == 1) {
                                    // Click nút thu tiền -> Phát âm thanh click nhẹ
                                    SoundHelper.playClickSound()
                                    viewModel.showPaymentQr(order)
                                }
                            },
                            onDeleteClick = {
                                SoundHelper.playClickSound()
                                viewModel.deleteOrder(order.id)
                                Toast.makeText(context, "Đã xóa đơn hàng #${order.id}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderProcessCard(
    order: OrderEntity,
    isPearl: Boolean,
    selectedSubTab: Int,
    onActionClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dateStr = remember(order.timestamp) {
        SimpleDateFormat("HH:mm - dd/MM", Locale.getDefault()).format(Date(order.timestamp))
    }

    // Dynamic UI styling matching light and dark themes
    val isDark = false
    val cardBgColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White
    val cardBorderColor = if (isDark) Color(0xFF333333) else Color(0xFFEEEEEE)
    val accentRedPinkColor = Color(0xFFEF3C46) // Theme signature pinkish red
    val goldenOrangeColor = Color(0xFFFFAC1C) // M3 Gold Orange button

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, cardBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            
            // 1. --- HEADER ROW ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    // ID styled in pinkish red like "FG-842918" in reference
                    val displayId = "FG-${842000 + order.id}"
                    Text(
                        text = displayId,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = accentRedPinkColor
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    // Timestamp in gray
                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }

                // Status text on top-right: e.g. "Đang xử lý", "Xử lý xong", "Đã thanh toán"
                val statusText = when (selectedSubTab) {
                    0 -> "Đang xử lý"
                    1 -> "Xử lý xong"
                    else -> "Đã thanh toán"
                }
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = accentRedPinkColor,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. --- ITEMS DETAIL LIST ---
            val itemsList = remember(order.itemsJson, order.itemsSummary) {
                parseOrderItems(order.itemsJson, order.itemsSummary, order.totalAmount)
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsList.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "•",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(end = 6.dp)
                            )
                            Text(
                                text = "${item.name}  x${item.qty}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        
                        // Item total price on the right formatted like reference image
                        Text(
                            text = if (item.price > 0.0) formatVND(item.price * item.qty) else "",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. --- TOTAL Row ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Total Amount bold text on the right
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Tổng: ",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = formatVND(order.totalAmount),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. --- TRANSFER MEMO Row ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Nội dung chuyển khoản:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Text(
                    text = "FOODGO${order.memoCode}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = accentRedPinkColor
                )
            }

            if (selectedSubTab == 0) {
                Spacer(modifier = Modifier.height(12.dp))

                // Automatic text conversion section for selected items
                val context = LocalContext.current
                val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
                val orderText = remember(order, itemsList) {
                    val detailLines = itemsList.joinToString("\n") { "  + ${it.name} (x${it.qty})" }
                    """
                    === ĐƠN HÀNG FG-${842000 + order.id} ===
                    Chi tiết món ăn:
                    $detailLines
                    Tổng cộng: ${formatVND(order.totalAmount)}
                    Nội dung chuyển khoản: FOODGO${order.memoCode}
                    """.trimIndent()
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    tint = accentRedPinkColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Văn bản đơn hàng tự động",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            
                            // Copy Button
                            IconButton(
                                onClick = {
                                    SoundHelper.playClickSound()
                                    clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(orderText))
                                    Toast.makeText(context, "Đã sao chép văn bản đơn!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Sao chép",
                                    tint = accentRedPinkColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        Text(
                            text = orderText,
                            style = androidx.compose.ui.text.TextStyle(
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. --- ACTIONS / BUTTONS at the Bottom ---
            if (selectedSubTab != 2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Outlined Cancel (Hủy đơn) Button
                    Box(
                        modifier = Modifier
                            .weight(0.4f)
                            .height(44.dp)
                            .border(1.dp, Color(0xFFCCCCCC), RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Transparent)
                            .clickable { onDeleteClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Hủy đơn",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Filled Action Button (Xử lý xong or Thanh toán QR) in beautiful golden-orange
                    val actionLabel = if (selectedSubTab == 0) "Xử lý xong" else "Thanh toán QR"
                    Box(
                        modifier = Modifier
                            .weight(0.6f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(goldenOrangeColor)
                            .clickable { onActionClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = actionLabel,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Outlined Delete (Xóa đơn) Button
                    Box(
                        modifier = Modifier
                            .weight(0.4f)
                            .height(44.dp)
                            .border(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Transparent)
                            .clickable { onDeleteClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Xóa đơn",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Paid indicator for tab 2
                    Row(
                        modifier = Modifier
                            .weight(0.6f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFE8F5E9)),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Thành công",
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Đã thanh toán",
                            color = Color(0xFF2E7D32),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

data class ProcessedOrderItem(
    val name: String,
    val qty: Int,
    val price: Double
)

fun parseOrderItems(itemsJson: String, itemsSummary: String, totalAmount: Double): List<ProcessedOrderItem> {
    val result = mutableListOf<ProcessedOrderItem>()
    if (itemsJson.isNotEmpty() && itemsJson.startsWith("[")) {
        try {
            val array = org.json.JSONArray(itemsJson)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val name = obj.optString("name", "")
                val qty = obj.optInt("qty", 1)
                val price = obj.optDouble("price", 0.0)
                result.add(ProcessedOrderItem(name, qty, price))
            }
            if (result.isNotEmpty()) return result
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Fallback parser for comma-separated itemsSummary
    try {
        val parts = itemsSummary.split(",")
        for (part in parts) {
            val cleanPart = part.trim()
            if (cleanPart.isEmpty()) continue
            val qtyRegex = Regex("""\(x(\d+)\)""")
            val match = qtyRegex.find(cleanPart)
            val qty = match?.groupValues?.get(1)?.toIntOrNull() ?: 1
            var name = cleanPart
            if (match != null) {
                name = cleanPart.replace(match.value, "").trim()
            }
            result.add(ProcessedOrderItem(name, qty, 0.0))
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    if (result.isEmpty() && itemsSummary.isNotEmpty()) {
        result.add(ProcessedOrderItem(itemsSummary, 1, totalAmount))
    }
    return result
}
