package com.axlrent.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.axlrent.data.Camera
import com.axlrent.data.Order
import com.axlrent.data.OrderStatus
import com.axlrent.data.SampleData
import com.axlrent.ui.theme.*
import com.axlrent.viewmodel.AppViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: AppViewModel,
    onLogout: () -> Unit
) {
    val allOrders by viewModel.allOrders.collectAsState()
    val cameras by viewModel.cameras.collectAsState()
    val ordersLoading by viewModel.ordersLoading.collectAsState()

    // Derived stats
    val waitingOrders = allOrders.filter { it.status == OrderStatus.WAITING }
    val activeOrders = allOrders.filter { it.status == OrderStatus.ACTIVE || it.status == OrderStatus.LATE }
    val completedOrders = allOrders.filter { it.status == OrderStatus.COMPLETED }
    val totalRevenue = completedOrders.sumOf { it.totalPrice }
    val totalStockUnits = cameras.sumOf { it.stock }

    // UI state
    var selectedOrderTab by remember { mutableIntStateOf(0) }
    var selectedCategoryFilter by remember { mutableStateOf("Semua") }
    var showAddCameraDialog by remember { mutableStateOf(false) }
    var cameraToEdit by remember { mutableStateOf<Camera?>(null) }
    var cameraToDelete by remember { mutableStateOf<Camera?>(null) }
    var showLogoutConfirm by remember { mutableStateOf(false) }
    var snackMessage by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(snackMessage) {
        snackMessage?.let {
            snackbarHostState.showSnackbar(it)
            snackMessage = null
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadAllOrders()
    }

    Scaffold(
        containerColor = DarkNavy,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(listOf(Color(0xFF1E293B), Color(0xFF0F172A)))
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(OrangePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.AdminPanelSettings,
                            contentDescription = null,
                            tint = TextWhite,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Dashboard Admin",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = TextWhite
                            )
                            Spacer(Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = OrangePrimary.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    "PRO",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OrangePrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text("Axl Rent · Manajemen Toko & Stok", fontSize = 12.sp, color = TextGray)
                    }
                    IconButton(
                        onClick = { showLogoutConfirm = true },
                        modifier = Modifier
                            .size(38.dp)
                            .background(DarkSurface, CircleShape)
                    ) {
                        Icon(Icons.Outlined.Logout, "Logout", tint = RedError, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(18.dp))

            // ==========================================
            // PANEL 1: Ringkasan Toko (2x2 Grid)
            // ==========================================
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Ringkasan Performa Toko",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Text(
                    "Realtime",
                    fontSize = 11.sp,
                    color = GreenActive,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(Modifier.height(12.dp))

            // Grid Row 1
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AdminStatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.TrendingUp,
                    iconColor = GreenActive,
                    label = "Total Omset Sewa",
                    value = formatAdminRupiah(totalRevenue),
                    subLabel = "${completedOrders.size} sewa selesai"
                )
                AdminStatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.HourglassEmpty,
                    iconColor = YellowWaiting,
                    label = "Perlu Konfirmasi",
                    value = "${waitingOrders.size} Pesanan",
                    subLabel = "Menunggu persetujuan"
                )
            }

            Spacer(Modifier.height(12.dp))

            // Grid Row 2
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AdminStatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.AssignmentTurnedIn,
                    iconColor = BluePremium,
                    label = "Sewa Berjalan",
                    value = "${activeOrders.size} Unit",
                    subLabel = "Sedang di tangan penyewa"
                )
                AdminStatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.Inventory2,
                    iconColor = OrangePrimary,
                    label = "Total Stok Armada",
                    value = "$totalStockUnits Unit",
                    subLabel = "${cameras.size} Model Kamera"
                )
            }

            Spacer(Modifier.height(26.dp))
            HorizontalDivider(color = DarkCardBorder, modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(Modifier.height(20.dp))

            // ==========================================
            // PANEL 2: Manajemen Pesanan
            // ==========================================
            Text(
                "Manajemen Pesanan Masuk",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(12.dp))

            // Tab Bar
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf(
                    "Menunggu (${waitingOrders.size})",
                    "Sedang Aktif (${activeOrders.size})"
                ).forEachIndexed { idx, label ->
                    val isSelected = selectedOrderTab == idx
                    Surface(
                        onClick = { selectedOrderTab = idx },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) OrangePrimary else DarkSurface,
                        border = if (!isSelected) BorderStroke(1.dp, DarkCardBorder) else null
                    ) {
                        Text(
                            label,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) TextWhite else TextGray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 11.dp, horizontal = 6.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            if (ordersLoading) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = OrangePrimary)
                }
            } else {
                val displayOrders = if (selectedOrderTab == 0) waitingOrders else activeOrders
                if (displayOrders.isEmpty()) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (selectedOrderTab == 0) "Tidak ada pesanan menunggu verifikasi." else "Tidak ada unit yang sedang disewa saat ini.",
                            color = TextGray,
                            fontSize = 13.sp
                        )
                    }
                } else {
                    displayOrders.forEach { order ->
                        AdminOrderCard(
                            order = order,
                            onConfirm = {
                                viewModel.adminConfirmOrder(order.id)
                                snackMessage = "Pesanan #${order.id} dikonfirmasi menjadi Aktif"
                            },
                            onComplete = {
                                viewModel.adminCompleteOrder(order.id)
                                snackMessage = "Sewa #${order.id} berhasil diselesaikan!"
                            }
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = DarkCardBorder, modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(Modifier.height(20.dp))

            // ==========================================
            // PANEL 3: Inventori & Manajemen Stok Kamera
            // ==========================================
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Inventori & Kontrol Stok",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        "${cameras.size} item katalog · $totalStockUnits total unit fisik",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }
                Button(
                    onClick = { showAddCameraDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Filled.Add, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Tambah", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(14.dp))

            // Filter Kategori Chips
            val filterCategories = listOf("Semua") + SampleData.categories
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filterCategories) { category ->
                    val isSelected = selectedCategoryFilter == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategoryFilter = category },
                        label = {
                            Text(
                                category,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = OrangePrimary,
                            selectedLabelColor = TextWhite,
                            containerColor = DarkSurface,
                            labelColor = TextGray
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = DarkCardBorder,
                            selectedBorderColor = OrangePrimary
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            val filteredCameras = if (selectedCategoryFilter == "Semua") {
                cameras
            } else {
                cameras.filter { it.category.equals(selectedCategoryFilter, ignoreCase = true) }
            }

            if (filteredCameras.isEmpty()) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tidak ada barang di kategori ini.", color = TextGray, fontSize = 13.sp)
                }
            } else {
                filteredCameras.forEach { camera ->
                    AdminInventoryCard(
                        camera = camera,
                        onIncreaseStock = {
                            viewModel.adminUpdateStock(camera.id, camera.stock + 1)
                            snackMessage = "Stok '${camera.name}' ditambah menjadi ${camera.stock + 1}"
                        },
                        onDecreaseStock = {
                            if (camera.stock > 0) {
                                viewModel.adminUpdateStock(camera.id, camera.stock - 1)
                                snackMessage = "Stok '${camera.name}' dikurangi menjadi ${camera.stock - 1}"
                            }
                        },
                        onEdit = { cameraToEdit = camera },
                        onDelete = { cameraToDelete = camera }
                    )
                    Spacer(Modifier.height(10.dp))
                }
            }

            Spacer(Modifier.height(36.dp))
        }
    }

    // ==========================================
    // Dialogs
    // ==========================================

    // Dialog Konfirmasi Logout
    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            containerColor = DarkSurface,
            title = { Text("Logout Admin?", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = { Text("Anda akan keluar dari sesi administrator.", color = TextGray, fontSize = 14.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirm = false
                        viewModel.logoutUser(onLogout)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedError),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Ya, Logout", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) {
                    Text("Batal", color = TextGray)
                }
            }
        )
    }

    // Dialog Tambah Barang
    if (showAddCameraDialog) {
        CameraFormDialog(
            title = "Tambah Unit Kamera Baru",
            initialCamera = null,
            onDismiss = { showAddCameraDialog = false },
            onConfirm = { camera ->
                viewModel.adminAddCamera(camera,
                    onSuccess = {
                        showAddCameraDialog = false
                        snackMessage = "Barang '${camera.name}' berhasil ditambahkan!"
                    },
                    onFailure = { err -> snackMessage = "Gagal: $err" }
                )
            }
        )
    }

    // Dialog Edit Barang
    cameraToEdit?.let { cam ->
        CameraFormDialog(
            title = "Edit Data & Stok Kamera",
            initialCamera = cam,
            onDismiss = { cameraToEdit = null },
            onConfirm = { updated ->
                viewModel.adminUpdateCamera(updated.copy(id = cam.id),
                    onSuccess = {
                        cameraToEdit = null
                        snackMessage = "Data '${updated.name}' berhasil diperbarui!"
                    },
                    onFailure = { err -> snackMessage = "Gagal: $err" }
                )
            }
        )
    }

    // Dialog Hapus Barang
    cameraToDelete?.let { cam ->
        AlertDialog(
            onDismissRequest = { cameraToDelete = null },
            containerColor = DarkSurface,
            title = { Text("Hapus Barang?", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Yakin ingin menghapus unit \"${cam.name}\" dari katalog toko?\nTindakan ini tidak dapat dibatalkan.",
                    color = TextGray,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.adminDeleteCamera(cam.id,
                            onSuccess = {
                                cameraToDelete = null
                                snackMessage = "'${cam.name}' dihapus dari inventori"
                            },
                            onFailure = { err ->
                                cameraToDelete = null
                                snackMessage = "Gagal: $err"
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedError),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Hapus", fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { cameraToDelete = null },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Batal", color = TextGray)
                }
            }
        )
    }
}

@Composable
private fun AdminStatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconColor: Color,
    label: String,
    value: String,
    subLabel: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, DarkCardBorder)
    ) {
        Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.Start) {
            Box(
                Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(10.dp))
            Text(
                value,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextWhite,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextGrayLight,
                maxLines = 1
            )
            Text(
                subLabel,
                fontSize = 10.sp,
                color = TextGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun AdminOrderCard(
    order: Order,
    onConfirm: () -> Unit,
    onComplete: () -> Unit
) {
    val statusColor = when (order.status) {
        OrderStatus.WAITING -> YellowWaiting
        OrderStatus.ACTIVE -> GreenActive
        OrderStatus.LATE -> Color(0xFFFF334B)
        else -> TextGray
    }
    val statusLabel = when (order.status) {
        OrderStatus.WAITING -> "Menunggu Konfirmasi"
        OrderStatus.ACTIVE -> "Sedang Disewa"
        OrderStatus.LATE -> "Terlambat"
        OrderStatus.COMPLETED -> "Selesai"
        else -> "-"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.35f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        order.camera.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "Order #${order.id}",
                        fontSize = 11.sp,
                        color = TextGray,
                        fontWeight = FontWeight.Medium
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Row(
                        Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
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
                            statusLabel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.CalendarMonth, null, Modifier.size(15.dp), tint = TextGray)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "${order.startDate} - ${order.endDate}",
                        fontSize = 12.sp,
                        color = TextGrayLight
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Payments, null, Modifier.size(15.dp), tint = OrangePrimary)
                    Spacer(Modifier.width(4.dp))
                    Text(
                        formatAdminRupiah(order.totalPrice),
                        fontSize = 13.sp,
                        color = OrangePrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            when (order.status) {
                OrderStatus.WAITING -> Button(
                    onClick = onConfirm,
                    Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenActive)
                ) {
                    Icon(Icons.Filled.CheckCircle, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Konfirmasi & Izinkan Sewa", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                OrderStatus.ACTIVE, OrderStatus.LATE -> Button(
                    onClick = onComplete,
                    Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePremium)
                ) {
                    Icon(Icons.Filled.AssignmentTurnedIn, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Tandai Unit Kembali & Selesai", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun AdminInventoryCard(
    camera: Camera,
    onIncreaseStock: () -> Unit,
    onDecreaseStock: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, DarkCardBorder)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Camera Thumbnail
                val localRes = camera.localImageRes
                Box(
                    Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkNavy),
                    contentAlignment = Alignment.Center
                ) {
                    if (localRes != null) {
                        Image(
                            painter = painterResource(localRes),
                            contentDescription = camera.name,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp),
                            contentScale = ContentScale.Fit
                        )
                    } else if (camera.imageUrl.isNotBlank()) {
                        AsyncImage(
                            model = camera.imageUrl,
                            contentDescription = camera.name,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Icon(
                            Icons.Outlined.CameraAlt,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = OrangePrimary.copy(alpha = 0.6f)
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        camera.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "${camera.brand} · ${camera.category}",
                        fontSize = 11.sp,
                        color = TextGray
                    )
                    Text(
                        formatAdminRupiah(camera.pricePerDay) + " / hari",
                        fontSize = 12.sp,
                        color = OrangePrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Edit & Delete Actions
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .size(34.dp)
                            .background(DarkNavy, RoundedCornerShape(8.dp))
                    ) {
                        Icon(Icons.Outlined.Edit, "Edit", tint = BluePremium, modifier = Modifier.size(16.dp))
                    }
                    Spacer(Modifier.width(6.dp))
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(34.dp)
                            .background(DarkNavy, RoundedCornerShape(8.dp))
                    ) {
                        Icon(Icons.Outlined.Delete, "Hapus", tint = RedError, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // ==========================================
            // KONTROL STOK KAMERA (Permintaan Dosen)
            // ==========================================
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkNavy)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stock Badge
                val (stockLabel, stockBg, stockColor) = when {
                    camera.stock > 2 -> Triple("Stok Tersedia (${camera.stock} Unit)", GreenActive.copy(alpha = 0.15f), GreenActive)
                    camera.stock in 1..2 -> Triple("Stok Menipis (${camera.stock} Unit)", YellowWaiting.copy(alpha = 0.15f), YellowWaiting)
                    else -> Triple("Stok Habis (0 Unit)", RedError.copy(alpha = 0.15f), RedError)
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = stockBg
                ) {
                    Text(
                        stockLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = stockColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Quick Increment/Decrement Buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onDecreaseStock,
                        enabled = camera.stock > 0,
                        modifier = Modifier
                            .size(28.dp)
                            .background(DarkSurface, RoundedCornerShape(6.dp))
                    ) {
                        Icon(
                            Icons.Filled.Remove,
                            contentDescription = "Kurang",
                            modifier = Modifier.size(14.dp),
                            tint = if (camera.stock > 0) TextWhite else TextGray
                        )
                    }

                    Text(
                        "${camera.stock}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    IconButton(
                        onClick = onIncreaseStock,
                        modifier = Modifier
                            .size(28.dp)
                            .background(DarkSurface, RoundedCornerShape(6.dp))
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Tambah",
                            modifier = Modifier.size(14.dp),
                            tint = OrangePrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CameraFormDialog(
    title: String,
    initialCamera: Camera?,
    onDismiss: () -> Unit,
    onConfirm: (Camera) -> Unit
) {
    var name by remember { mutableStateOf(initialCamera?.name ?: "") }
    var brand by remember { mutableStateOf(initialCamera?.brand ?: "") }
    var category by remember { mutableStateOf(initialCamera?.category ?: "DSLR") }
    var stockText by remember { mutableStateOf((initialCamera?.stock ?: 5).toString()) }
    var pricePerDay by remember {
        mutableStateOf(if ((initialCamera?.pricePerDay ?: 0L) > 0) initialCamera!!.pricePerDay.toString() else "")
    }
    var description by remember { mutableStateOf(initialCamera?.description ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Column(Modifier.padding(24.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TextWhite)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Filled.Close, "Tutup", tint = TextGray, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(Modifier.height(16.dp))

                @Composable
                fun FormField(
                    label: String,
                    value: String,
                    placeholder: String = "",
                    multiline: Boolean = false,
                    onValueChange: (String) -> Unit
                ) {
                    Text(label, fontSize = 12.sp, color = TextGrayLight, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(4.dp))
                    OutlinedTextField(
                        value = value,
                        onValueChange = onValueChange,
                        placeholder = { if (placeholder.isNotBlank()) Text(placeholder, color = TextGray, fontSize = 13.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = !multiline,
                        minLines = if (multiline) 2 else 1,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedContainerColor = DarkNavy,
                            unfocusedContainerColor = DarkNavy,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            cursorColor = OrangePrimary
                        )
                    )
                    Spacer(Modifier.height(12.dp))
                }

                FormField("Nama Barang / Kamera", name, "Cth: Sony Alpha A7 IV") { name = it }
                FormField("Merek (Brand)", brand, "Cth: SONY, CANON, FUJIFILM") { brand = it }
                FormField("Kategori", category, "DSLR, Mirrorless, Action Cam, Lensa, dll") { category = it }

                // STOK UNIT
                Text("Jumlah Stok Fisik (Unit)", fontSize = 12.sp, color = TextGrayLight, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = stockText,
                    onValueChange = { if (it.all { c -> c.isDigit() }) stockText = it },
                    placeholder = { Text("5", color = TextGray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = {
                        Icon(Icons.Filled.Inventory2, null, Modifier.size(18.dp), tint = OrangePrimary)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimary,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedContainerColor = DarkNavy,
                        unfocusedContainerColor = DarkNavy,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        cursorColor = OrangePrimary
                    )
                )
                Spacer(Modifier.height(12.dp))

                // HARGA SEWA
                Text("Harga Sewa / Hari (Rp)", fontSize = 12.sp, color = TextGrayLight, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = pricePerDay,
                    onValueChange = { if (it.all { c -> c.isDigit() }) pricePerDay = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = {
                        Text("Rp", fontSize = 14.sp, color = TextGray, modifier = Modifier.padding(start = 12.dp))
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimary,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedContainerColor = DarkNavy,
                        unfocusedContainerColor = DarkNavy,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        cursorColor = OrangePrimary
                    )
                )

                Spacer(Modifier.height(12.dp))
                FormField("Deskripsi / Spesifikasi Singkat", description, multiline = true) { description = it }

                Spacer(Modifier.height(16.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(
                        onClick = onDismiss,
                        Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Batal", color = TextGray)
                    }
                    Button(
                        onClick = {
                            if (name.isBlank()) return@Button
                            val price = pricePerDay.toLongOrNull() ?: 0L
                            val stockVal = stockText.toIntOrNull() ?: 1
                            val camera = (initialCamera ?: Camera()).copy(
                                name = name.trim(),
                                brand = brand.trim().uppercase(),
                                category = category.trim(),
                                description = description.trim(),
                                pricePerDay = price,
                                stock = stockVal,
                                isAvailable = stockVal > 0
                            )
                            onConfirm(camera)
                        },
                        Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                    ) {
                        Text("Simpan", fontWeight = FontWeight.Bold, color = TextWhite)
                    }
                }
            }
        }
    }
}

private fun formatAdminRupiah(amount: Long): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    format.maximumFractionDigits = 0
    return format.format(amount)
}
