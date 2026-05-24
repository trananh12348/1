package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.entity.OrderEntity
import com.example.ui.util.SoundHelper
import com.example.ui.viewmodel.FoodViewModel
import java.net.URLEncoder

@Composable
fun QrPaymentDialog(
    order: OrderEntity,
    viewModel: FoodViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val useCustomQr by viewModel.useCustomQr.collectAsState()
    val customQrPath by viewModel.customQrPath.collectAsState()

    // Mã nội dung chuyển khoản là dạng food + số ngẫu nhiên (<10000) đã được lưu trong OrderEntity
    val transferMemo = "food" + order.memoCode.toString()

    // Mã hóa các tham số để tạo link ảnh QR an toàn từ API VietQR công khai
    val qrUrl = remember(order) {
        try {
            val encodedName = URLEncoder.encode(order.accountOwner, "UTF-8")
            "https://img.vietqr.io/image/${order.bankName}-${order.bankAccount}-compact.png?amount=${order.totalAmount.toInt()}&addInfo=${transferMemo}&accountName=${encodedName}"
        } catch (e: Exception) {
            ""
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 6.dp,
            modifier = modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(20.dp)
            ) {
                // Tiêu đề & Nút Đóng
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Thanh Toán VietQR",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Đóng")
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 12.dp))

                // Số tiền cần chuyển khoản (Cực kì rõ ràng)
                Text(
                    text = "TỔNG SỐ TIỀN THÀNH TOÁN",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formatVND(order.totalAmount),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Ảnh QR Code VietQR tải bằng Coil
                val displayImage = if (useCustomQr && customQrPath.isNotEmpty()) customQrPath else qrUrl
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (displayImage.isNotEmpty()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(displayImage)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Mã QR",
                            modifier = Modifier.fillMaxSize(),
                            onLoading = {
                                // Hiển thị vòng xoay đang tải
                            },
                            onError = {
                                // Nếu không có mạng, hiển thị logo dự phòng
                            }
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Lỗi tạo QR",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }

                if (useCustomQr && customQrPath.isNotEmpty()) {
                    Text(
                        text = "Sử dụng ảnh QR riêng của cửa hàng",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Ô thông tin mã nội dung chuyển khoản ngẫu nhiên (<10000)
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "NỘI DUNG CHUYỂN KHOẢN (BẮT BUỘC)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = transferMemo,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(transferMemo))
                                    Toast.makeText(context, "Đã sao chép nội dung: $transferMemo", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Sao chép",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Text(
                            text = "Khách quét mã sẽ tự động điền số này. Nếu điền tay cần nhập đúng chính xác.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Thông tin tài khoản dự phòng
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Ngân hàng:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                        Text(text = order.bankName, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Số tài khoản:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                        Text(text = order.bankAccount, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Chủ tài khoản:", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                        Text(text = order.accountOwner, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Nút hoàn tất giao dịch
                Button(
                    onClick = {
                        SoundHelper.playClickSound()
                        viewModel.markOrderAsPaid(order)
                        onDismiss()
                        Toast.makeText(context, "Đã ghi nhận thanh toán đơn #${order.id}", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Đã nhận tiền chuyển khoản xong",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
