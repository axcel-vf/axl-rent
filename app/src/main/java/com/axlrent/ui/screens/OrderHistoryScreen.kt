package com.axlrent.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axlrent.data.Order
import com.axlrent.data.OrderStatus
import com.axlrent.data.SampleData
import com.axlrent.ui.theme.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.axlrent.viewmodel.AppViewModel
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    viewModel: AppViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val initTab by viewModel.initialHistoryTab.collectAsState()
    var selectedTab by remember(initTab) { mutableIntStateOf(initTab) }
    val tabs = listOf("Semua", "Menunggu", "Aktif", "Terlambat", "Selesai", "Dibatalkan")

    val orders by viewModel.orders.collectAsState()
    val ordersLoading by viewModel.ordersLoading.collectAsState()

    val filteredOrders = when (selectedTab) {
        1 -> orders.filter { it.status == OrderStatus.WAITING }
        2 -> orders.filter { it.status == OrderStatus.ACTIVE }
        3 -> orders.filter { it.status == OrderStatus.LATE }
        4 -> orders.filter { it.status == OrderStatus.COMPLETED }
        5 -> orders.filter { it.status == OrderStatus.CANCELLED }
        else -> orders
    }

    Scaffold(
        containerColor = DarkNavy,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Riwayat Sewa",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkNavy,
                    titleContentColor = TextWhite
                )
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(4.dp))

            Text(
                text = "Kelola sewa kamera aktif dan riwayat penyewaan Anda.",
                fontSize = 13.sp,
                color = TextGray,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(Modifier.height(16.dp))

            // Scrollable Tab Bar
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 20.dp,
                containerColor = DarkNavy,
                contentColor = OrangePrimary,
                divider = {},
                indicator = { tabPositions ->
                    if (selectedTab in tabPositions.indices) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            height = 3.dp,
                            color = OrangePrimary
                        )
                    }
                }
            ) {
                tabs.forEachIndexed { index, tabTitle ->
                    val isSelected = selectedTab == index
                    Tab(
                        selected = isSelected,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = tabTitle,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) OrangePrimary else TextGray
                            )
                        }
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            if (ordersLoading && orders.isEmpty()) {
                Box(Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = OrangePrimary)
                }
            } else if (filteredOrders.isEmpty()) {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("Belum ada transaksi di kategori ini.", color = TextGray, fontSize = 14.sp)
                }
            } else {
                // Order cards
                filteredOrders.forEach { order ->
                    OrderCard(order, onApprove = {
                        viewModel.initialHistoryTab.value = 0
                        viewModel.approveOrder(order.id)
                    })
                    Spacer(Modifier.height(16.dp))
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun OrderCard(order: Order, onApprove: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, DarkCardBorder.copy(alpha = 0.5f))
    ) {
        Column {
            // Image area with status badge
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(DarkCard, DarkSurface)
                        )
                    )
            ) {
                val imageModel = order.camera.localImageRes ?: order.camera.imageUrl.takeIf { it.isNotEmpty() }
                if (imageModel != null) {
                    AsyncImage(
                        model = imageModel,
                        contentDescription = order.camera.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        Icons.Outlined.CameraAlt,
                        order.camera.name,
                        Modifier
                            .size(64.dp)
                            .align(Alignment.Center),
                        tint = TextGrayDark.copy(alpha = 0.3f)
                    )
                }

                // Status badge with customized colors per requirement
                val statusPair: Pair<String, Color> = when (order.status) {
                    OrderStatus.WAITING -> Pair("Menunggu", YellowWaiting)
                    OrderStatus.ACTIVE -> Pair("Aktif", GreenActive)
                    OrderStatus.LATE -> Pair("Terlambat", Color(0xFFFF334B))
                    OrderStatus.COMPLETED -> Pair("Selesai", BluePremium)
                    OrderStatus.CANCELLED -> Pair("Dibatalkan", Color(0xFFB71C1C))
                }
                val statusText = statusPair.first
                val statusColor = statusPair.second

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Row(
                        Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                        )
                        Text(
                            statusText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = statusColor
                        )
                    }
                }
            }

            // Info section
            Column(Modifier.padding(16.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            order.camera.name,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            "#${order.id}",
                            fontSize = 12.sp,
                            color = TextGray
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            formatRupiah(order.camera.pricePerDay),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimary
                        )
                        Text(
                            "/ day",
                            fontSize = 11.sp,
                            color = TextGray
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Date row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.CalendarMonth, null,
                        Modifier.size(16.dp),
                        tint = TextGray
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "${order.startDate} - ${order.endDate}",
                        fontSize = 13.sp,
                        color = TextGray
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Action button based on status
                when (order.status) {
                    OrderStatus.WAITING -> {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {},
                                Modifier
                                    .fillMaxWidth()
                                    .height(44.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = ButtonDefaults.outlinedButtonBorder(enabled = true)
                            ) {
                                Text(
                                    "Menunggu Konfirmasi Admin",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp,
                                    color = YellowWaiting
                                )
                            }
                            Button(
                                onClick = onApprove,
                                Modifier
                                    .fillMaxWidth()
                                    .height(40.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GreenActive.copy(alpha = 0.15f))
                            ) {
                                Text(
                                    "Setujui (Demo Admin)",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = GreenActive
                                )
                            }
                        }
                    }
                    OrderStatus.ACTIVE -> {
                        Button(
                            onClick = {},
                            Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = OrangePrimary
                            )
                        ) {
                            Text(
                                "Perpanjang Sewa",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = TextWhite
                            )
                        }
                    }
                    OrderStatus.LATE -> {
                        Button(
                            onClick = {},
                            Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF334B)
                            )
                        ) {
                            Text(
                                "Kembalikan Unit Sekarang",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = TextWhite
                            )
                        }
                    }
                    OrderStatus.COMPLETED -> {
                        OutlinedButton(
                            onClick = {},
                            Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = ButtonDefaults.outlinedButtonBorder(enabled = true),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = DarkCard
                            )
                        ) {
                            Text(
                                if (order.id == "AXL-9710") "Tulis Ulasan" else "Sewa Lagi",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = TextGrayLight
                            )
                        }
                    }
                    OrderStatus.CANCELLED -> {
                        OutlinedButton(
                            onClick = {},
                            Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFB71C1C).copy(alpha = 0.5f))
                        ) {
                            Text(
                                "Sewa Kembali",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Color(0xFFB71C1C)
                            )
                        }
                    }
                }
            }
        }
    }
}
