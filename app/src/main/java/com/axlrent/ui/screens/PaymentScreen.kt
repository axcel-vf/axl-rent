package com.axlrent.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.runtime.collectAsState
import com.axlrent.data.Order
import com.axlrent.viewmodel.AppViewModel

import com.axlrent.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    viewModel: AppViewModel,
    onNavigateBack: () -> Unit,
    onPaymentSuccess: () -> Unit
) {
    var selectedMethod by remember { mutableStateOf("QRIS") }
    var timeLeft by remember { mutableIntStateOf(900) } // 15 min

    val bookingState by viewModel.bookingState.collectAsState()
    val booking = bookingState ?: Order()

    LaunchedEffect(Unit) {
        while (timeLeft > 0) { delay(1000); timeLeft-- }
    }
    val mins = timeLeft / 60
    val secs = timeLeft % 60

    Scaffold(
        containerColor = DarkNavy,
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextWhite) } },
                title = { Text("Payment", fontWeight = FontWeight.Bold, fontSize = 17.sp) },
                actions = { Row(Modifier.padding(end = 16.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Outlined.Lock, null, Modifier.size(14.dp), tint = TextGray); Spacer(Modifier.width(4.dp)); Text("SECURE CHECKOUT", fontSize = 10.sp, color = TextGray, letterSpacing = 1.sp) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkNavy, titleContentColor = TextWhite)
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
            // Payment methods
            Text("Pilih Metode Pembayaran", fontSize = 14.sp, color = TextGray, modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("QRIS", "GOPAY").forEach { m ->
                    PayMethodBox(m, selectedMethod == m, Modifier.weight(1f)) { selectedMethod = m }
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("OVO", "DANA").forEach { m ->
                    PayMethodBox(m, selectedMethod == m, Modifier.weight(1f)) { selectedMethod = m }
                }
            }

            Spacer(Modifier.height(24.dp))
            // QR Code area
            Surface(Modifier.fillMaxWidth().padding(horizontal = 20.dp), shape = RoundedCornerShape(16.dp), color = DarkSurface, border = BorderStroke(1.dp, DarkCardBorder)) {
                Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    // QR placeholder
                    Box(Modifier.size(180.dp).clip(RoundedCornerShape(12.dp)).background(Brush.verticalGradient(listOf(DarkCard, DarkNavy))), contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.QrCodeScanner, null, Modifier.size(80.dp), tint = TextGrayDark.copy(0.5f))
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("Scan QRIS Untuk Bayar", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                    Spacer(Modifier.height(4.dp))
                    Text("Pindai kode di atas melalui aplikasi e-\nwallet atau perbankan Anda.", fontSize = 13.sp, color = TextGray, textAlign = TextAlign.Center, lineHeight = 20.sp)
                    Spacer(Modifier.height(16.dp))
                    Surface(shape = RoundedCornerShape(20.dp), color = DarkCard, border = BorderStroke(1.dp, DarkCardBorder)) {
                        Row(Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(8.dp).clip(androidx.compose.foundation.shape.CircleShape).background(YellowWaiting))
                            Spacer(Modifier.width(8.dp))
                            Text("WAITING FOR PAYMENT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextGray, letterSpacing = 1.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            // Security badges
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                SecurityBadge("Verified Secure", Icons.Outlined.VerifiedUser)
                Spacer(Modifier.width(16.dp))
                SecurityBadge("256-bit SSL", Icons.Outlined.Lock)
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                SecurityBadge("Fraud Protection", Icons.Outlined.Shield)
            }

            Spacer(Modifier.height(24.dp))
            // Order summary card
            Surface(Modifier.fillMaxWidth().padding(horizontal = 20.dp), shape = RoundedCornerShape(14.dp), color = DarkSurface, border = BorderStroke(1.dp, DarkCardBorder)) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(48.dp).clip(RoundedCornerShape(10.dp)).background(DarkCard), contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.CameraAlt, null, Modifier.size(24.dp), tint = TextGrayDark.copy(0.5f))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(booking.camera.brand.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = OrangePrimary, letterSpacing = 1.sp)
                            Text(booking.camera.name.ifEmpty { "Kamera" }, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                            Text("3 Hari Penyewaan", fontSize = 12.sp, color = TextGray)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = DarkCardBorder)
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Biaya Sewa", fontSize = 13.sp, color = TextGray)
                        Text(formatRupiah(booking.camera.pricePerDay * 3), fontSize = 13.sp, color = TextWhite)
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Asuransi & Layanan", fontSize = 13.sp, color = TextGray)
                        Text(formatRupiah(booking.totalPrice - (booking.camera.pricePerDay * 3)), fontSize = 13.sp, color = TextWhite)
                    }
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider(color = DarkCardBorder)
                    Spacer(Modifier.height(8.dp))
                    Text("TOTAL BAYAR", fontSize = 10.sp, color = TextGray, letterSpacing = 1.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(formatRupiah(booking.totalPrice), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        Spacer(Modifier.weight(1f))
                        Icon(Icons.Outlined.CheckCircle, null, Modifier.size(24.dp), tint = GreenActive)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            // Pay button
            Button(
                onClick = {
                    viewModel.confirmPayment(
                        onSuccess = onPaymentSuccess,
                        onFailure = { /* Handle error silently or log */ }
                    )
                },
                Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Icon(Icons.Outlined.CreditCard, null, tint = TextWhite, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Bayar Sekarang", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = TextWhite)
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Icon(Icons.Outlined.Lock, null, Modifier.size(12.dp), tint = TextGray)
                Spacer(Modifier.width(4.dp))
                Text("Pembayaran diproses secara aman", fontSize = 11.sp, color = TextGray)
            }
            Spacer(Modifier.height(8.dp))
            Text("Dengan melanjutkan, Anda menyetujui Syarat\n& Ketentuan penyewaan Axl Rent.", fontSize = 11.sp, color = TextGray, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun PayMethodBox(name: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Surface(modifier.clickable { onClick() }, shape = RoundedCornerShape(12.dp), color = if (selected) OrangePrimary.copy(0.15f) else DarkSurface,
        border = BorderStroke(1.5.dp, if (selected) OrangePrimary else DarkCardBorder)) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            val icon = when (name) { "QRIS" -> Icons.Outlined.QrCodeScanner; else -> Icons.Outlined.AccountBalanceWallet }
            Icon(icon, null, Modifier.size(28.dp), tint = if (selected) OrangePrimary else TextGray)
            Spacer(Modifier.height(6.dp))
            Text(name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = if (selected) OrangePrimary else TextGray)
        }
    }
}

@Composable
private fun SecurityBadge(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, Modifier.size(14.dp), tint = TextGray)
        Spacer(Modifier.width(4.dp))
        Text(text, fontSize = 12.sp, color = TextGray)
    }
}
