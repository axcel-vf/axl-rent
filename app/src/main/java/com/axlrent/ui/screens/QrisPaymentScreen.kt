package com.axlrent.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.axlrent.data.Order
import com.axlrent.ui.theme.*
import com.axlrent.viewmodel.AppViewModel
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrisPaymentPage(
    viewModel: AppViewModel,
    onNavigateBack: () -> Unit,
    onPaymentSuccess: () -> Unit
) {
    val context = LocalContext.current
    val bookingState by viewModel.bookingState.collectAsState()
    val booking = bookingState ?: Order()

    // 15-minute countdown timer (900 seconds)
    var timeLeftSeconds by remember { mutableIntStateOf(900) }
    var isPaymentProcessing by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (timeLeftSeconds > 0) {
            delay(1000)
            timeLeftSeconds--
        }
    }

    val minutes = timeLeftSeconds / 60
    val seconds = timeLeftSeconds % 60
    val timerString = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

    val invoiceNo = if (booking.id.isNotEmpty()) booking.id else "INV/AXL-${(1000..9999).random()}"
    val totalAmount = if (booking.totalPrice > 0) booking.totalPrice else 350000L

    Scaffold(
        containerColor = DarkNavy,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = TextWhite)
                    }
                },
                title = {
                    Text("Pembayaran QRIS", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkNavy,
                    titleContentColor = TextWhite
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // --- 1. Info Card: Nama Rental, Invoice & Total Tagihan ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    border = BorderStroke(1.dp, DarkCardBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Axl Rent", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = OrangePrimary)
                                Text("Penyewaan Kamera & Peralatan", fontSize = 11.sp, color = TextGray)
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = DarkCard,
                                border = BorderStroke(1.dp, DarkCardBorder)
                            ) {
                                Text(
                                    text = "#$invoiceNo",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextWhite,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = DarkCardBorder)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total Tagihan", fontSize = 13.sp, color = TextGray)
                            Text(
                                text = formatQrisRupiah(totalAmount),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- 2. Countdown Timer Banner ---
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = YellowWaiting.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, YellowWaiting.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Timer, contentDescription = null, tint = YellowWaiting, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Sisa Waktu Pembayaran: $timerString",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = YellowWaiting
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // --- 3. QR Code Display Card ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Header QRIS Badge
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("QRIS", fontWeight = FontWeight.Black, fontSize = 22.sp, color = Color(0xFFED1C24), letterSpacing = 2.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("National QR Code", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // QR Code Image
                        Box(
                            modifier = Modifier
                                .size(220.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = "https://api.qrserver.com/v1/create-qr-code/?size=250x250&data=AXLRENT_QRIS_PAYMENT_$invoiceNo",
                                contentDescription = "QRIS Code",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text("NMB: AXL RENT INDONESIA", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black)
                        Text("NMID: ID102003948271", fontSize = 11.sp, color = Color.DarkGray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Instruction Text
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurface,
                    border = BorderStroke(1.dp, DarkCardBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Info, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Silakan screenshot halaman ini dan bayar menggunakan e-wallet atau m-banking pilihan Anda.",
                            fontSize = 12.sp,
                            color = TextGray,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- 4. Tombol "Saya Sudah Bayar" ---
                Button(
                    onClick = {
                        isPaymentProcessing = true
                        // Execute payment simulation logic:
                        viewModel.confirmPayment(
                            onSuccess = {
                                isPaymentProcessing = false
                                viewModel.initialHistoryTab.value = 2 // Tab Aktif
                                showSuccessDialog = true
                            },
                            onFailure = {
                                isPaymentProcessing = false
                                viewModel.initialHistoryTab.value = 2
                                showSuccessDialog = true
                            }
                        )
                    },
                    enabled = !isPaymentProcessing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    if (isPaymentProcessing) {
                        CircularProgressIndicator(color = TextWhite, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = TextWhite)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Saya Sudah Bayar", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            // --- 5. Pop-up Animation Dialog Pembayaran Berhasil ---
            if (showSuccessDialog) {
                Dialog(onDismissRequest = {}) {
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = BorderStroke(1.5.dp, GreenActive.copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(GreenActive.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.CheckCircle,
                                    contentDescription = "Sukses",
                                    tint = GreenActive,
                                    modifier = Modifier.size(48.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Pembayaran Berhasil!",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Transaksi Anda telah dikonfirmasi.\nItem telah diperbarui ke status Aktif.",
                                fontSize = 13.sp,
                                color = TextGray,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )

                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = {
                                    showSuccessDialog = false
                                    onPaymentSuccess()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GreenActive)
                            ) {
                                Text("Lihat Riwayat Sewa", fontWeight = FontWeight.Bold, color = TextWhite, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatQrisRupiah(amount: Long): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    format.maximumFractionDigits = 0
    return format.format(amount)
}
