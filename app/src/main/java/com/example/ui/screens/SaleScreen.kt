package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import com.example.data.entity.MenuItemEntity
import com.example.ui.util.SoundHelper
import com.example.ui.viewmodel.FoodViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext

// Thao tác định dạng tiền tệ Việt Nam (VND)
fun formatVND(amount: Double): String {
    return String.format("%,.0fđ", amount)
}

// Hàm lấy Emoji đại diện cho món ăn
fun getEmojiForFood(name: String, category: String): String {
    val lower = name.lowercase()
    return when {
        lower.contains("phở") || lower.contains("bún") || lower.contains("mỳ") || lower.contains("mì") -> "🍲"
        lower.contains("bánh mì") || lower.contains("bánh mỳ") -> "🥪"
        lower.contains("cơm") -> "🍛"
        lower.contains("cà phê") || lower.contains("cafe") -> "☕"
        lower.contains("trà") || lower.contains("nước") || lower.contains("vải") -> "🍹"
        category.contains("uống") || category.contains("nước") -> "🥤"
        category.contains("nhẹ") || category.contains("vặt") -> "🍟"
        else -> "🍱"
    }
}

// Màu nền tượng trưng cho món ăn để tăng mĩ thuật UI
fun getFoodColor(name: String): Color {
    val lower = name.lowercase()
    return when {
        lower.contains("phở") || lower.contains("bún") || lower.contains("mỳ") || lower.contains("mì") -> Color(0xFFFFF3E0) // Cam nhạt
        lower.contains("bánh mì") || lower.contains("bánh mỳ") -> Color(0xFFE8F5E9) // Xanh lục nhạt
        lower.contains("cơm") -> Color(0xFFFFFDE7) // Vàng nhạt
        lower.contains("cà phê") || lower.contains("cafe") -> Color(0xFFEFEBE9) // Nâu đất nhạt
        else -> Color(0xFFE3F2FD) // Xanh dương nhạt
    }
}

@Composable
fun SaleScreen(viewModel: FoodViewModel, modifier: Modifier = Modifier) {
    val menuItems by viewModel.menuItems.collectAsState()
    val cart by viewModel.cart.collectAsState()

    var selectedCategory by remember { mutableStateOf("Tất cả") }
    val categories = remember(menuItems) {
        listOf("Tất cả") + menuItems.map { it.category }.distinct()
    }

    val filteredItems = remember(menuItems, selectedCategory) {
        if (selectedCategory == "Tất cả") {
            menuItems
        } else {
            menuItems.filter { it.category == selectedCategory }
        }
    }

    val cartItemCount = cart.values.sum()
    val cartTotalPrice = cart.map { it.key.price * it.value }.sum()

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Bộ lọc thể loại món ăn (Category chips Scrollable)
            ScrollableTabRow(
                selectedTabIndex = categories.indexOf(selectedCategory).coerceAtLeast(0),
                edgePadding = 16.dp,
                divider = {},
                indicator = {},
                containerColor = Color.Transparent,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                categories.forEach { category ->
                    val selected = (category == selectedCategory)
                    FilterChip(
                        selected = selected,
                        onClick = {
                            SoundHelper.playClickSound()
                            selectedCategory = category
                        },
                        label = { Text(text = category, style = MaterialTheme.typography.labelLarge) },
                        modifier = Modifier.padding(horizontal = 4.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            }

            if (filteredItems.isEmpty()) {
                // Trạng thái menu rỗng
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestaurantMenu,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Chưa có món ăn nào trong danh mục này",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Hãy thêm món ăn mới tại tab Cấu Hình để bắt đầu bán hàng!",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                // Danh sách món ăn kiểu Grid Responsive quyến rũ
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 120.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredItems, key = { it.id }) { item ->
                        val qty = cart[item] ?: 0
                        FoodItemCard(
                            item = item,
                            quantityInCart = qty,
                            onAdd = {
                                SoundHelper.playClickSound()
                                viewModel.addToCart(item)
                            },
                            onDecrease = {
                                SoundHelper.playClickSound()
                                viewModel.decreaseQuantity(item)
                            }
                        )
                    }
                }
            }
        }

        // Thanh giỏ hàng nổi phía dưới (Bottom Cart Bar)
        val context = LocalContext.current
        AnimatedVisibility(
            visible = cartItemCount > 0,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                // Khoảng cách an toàn để không đè thanh điều hướng hệ thống
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 72.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cartItemCount.toString(),
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Tạm tính",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                            Text(
                                text = formatVND(cartTotalPrice),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Button(
                        onClick = {
                            SoundHelper.playClickSound()
                            viewModel.placeOrder()
                            Toast.makeText(context, "Đã gửi đơn thành công sang bếp chế biến!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Tạo Đơn",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FoodItemCard(
    item: MenuItemEntity,
    quantityInCart: Int,
    onAdd: () -> Unit,
    onDecrease: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E) // Matte black background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Phần ảnh Minh họa (Ảnh tự tải lên từ máy hoặc Emoji đầy màu sắc)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(Color(0xFF2C2C2E)), // Dark matte segments for emoji background
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
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = getEmojiForFood(item.name, item.category),
                        fontSize = 44.sp
                    )
                }

                // Nhãn Thể loại nhỏ góc trên
                Surface(
                    shape = RoundedCornerShape(topStart = 0.dp, bottomEnd = 12.dp, topEnd = 0.dp, bottomStart = 0.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = item.category,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Huy hiệu số lượng trong giỏ hàng nếu đã chọn
                if (quantityInCart > 0) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "x$quantityInCart",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Phần tiêu đề & Giá bán món cơm/đồ uống
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = item.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White, // White text on matte black
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(2.dp))
                
                Text(
                    text = formatVND(item.price),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFB300) // Beautiful glowing gold price text on dark backdrop
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Khu vực nút Thêm / Bớt sản phẩm
                if (quantityInCart > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onDecrease,
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFF3A3A3C), CircleShape) // Dark gray decrease button
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Giảm",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Text(
                            text = quantityInCart.toString(),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        IconButton(
                            onClick = onAdd,
                            modifier = Modifier
                                .size(36.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Tăng",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = onAdd,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary, // Highly visible core action button
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Chọn mua",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
