package com.axlrent.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.axlrent.data.Camera
import com.axlrent.data.Order
import com.axlrent.data.OrderStatus
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

    // UI state
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddCameraDialog by remember { mutableStateOf(false) }
    var cameraToEdit by remember { mutableStateOf<Camera?>(null) }
    var cameraToDelete by remember { mutableStateOf<Camera?>(null) }
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
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(OrangePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.AdminPanelSettings, null, tint = TextWhite, modifier = Modifier.size(22.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Dashboard Admin", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextWhite)
                        Text("Axl Rent · Pengelola Toko", fontSize = 12.sp, color = TextGray)
                    }
                    IconButton(onClick = { viewModel.logoutUser(onLogout) }) {
                        Icon(Icons.Outlined.Logout, "Logout", tint = RedError)
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
            Spacer(Modifier.height(20.dp))

            // === PANEL 1: Ringkasan Statistik ===
            Text(
                "Ringkasan Toko",
                fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextGrayLight,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(12.dp))

            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminStatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.TrendingUp,
                    iconColor = GreenActive,
                    label = "Pendapatan",
                    value = formatAdminRupiah(totalRevenue)
                )
                AdminStatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.HourglassEmpty,
                    iconColor = YellowWaiting,
                    label = "Menunggu",
                    value = "${waitingOrders.size} Pesanan"
                )
                AdminStatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.DirectionsBike,
                    iconColor = BluePremium,
                    label = "Aktif",
                    value = "${activeOrders.size} Disewa"
                )
            }

            Spacer(Modifier.height(28.dp))
            HorizontalDivider(color = DarkCardBorder, modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(Modifier.height(20.dp))

            // === PANEL 2: Manajemen Pesanan ===
            Text(
                "Manajemen Pesanan",
                fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextGrayLight,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(12.dp))

            Row(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Menunggu (${waitingOrders.size})", "Aktif (${activeOrders.size})").forEachIndexed { idx, label ->
                    val isSelected = selectedTab == idx
                    Surface(
                        onClick = { selectedTab = idx },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) OrangePrimary else DarkSurface,
                        border = if (!isSelected) BorderStroke(1.dp, DarkCardBorder) else null
                    ) {
                        Text(
                            label,
                            fontSize = 12.sp, fontWeight = FontWeight.Medium,
                            color = if (isSelected) TextWhite else TextGray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                            maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            if (ordersLoading) {
                Box(Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = OrangePrimary)
                }
            } else {
                val displayOrders = if (selectedTab == 0) waitingOrders else activeOrders
                if (displayOrders.isEmpty()) {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("Tidak ada pesanan di kategori ini.", color = TextGray, fontSize = 14.sp)
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
                                snackMessage = "Sewa #${order.id} diselesaikan"
                            }
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = DarkCardBorder, modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(Modifier.height(20.dp))

            // === PANEL 3: Inventori ===
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Inventori Barang (${cameras.size})", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextGrayLight)
                Button(
                    onClick = { showAddCameraDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Filled.Add, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Tambah", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(Modifier.height(12.dp))

            if (cameras.isEmpty()) {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("Belum ada barang di inventori.", color = TextGray, fontSize = 14.sp)
                }
            } else {
                cameras.forEach { camera ->
                    AdminInventoryCard(camera = camera, onEdit = { cameraToEdit = camera }, onDelete = { cameraToDelete = camera })
                    Spacer(Modifier.height(8.dp))
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }

    // === Dialogs ===
    if (showAddCameraDialog) {
        CameraFormDialog(
            title = "Tambah Barang Baru",
            initialCamera = null,
            onDismiss = { showAddCameraDialog = false },
            onConfirm = { camera ->
                viewModel.adminAddCamera(camera,
                    onSuccess = { showAddCameraDialog = false; snackMessage = "Barang '${camera.name}' ditambahkan" },
                    onFailure = { err -> snackMessage = "Gagal: $err" }
                )
            }
        )
    }

    cameraToEdit?.let { cam ->
        CameraFormDialog(
            title = "Edit Barang",
            initialCamera = cam,
            onDismiss = { cameraToEdit = null },
            onConfirm = { updated ->
                viewModel.adminUpdateCamera(updated.copy(id = cam.id),
                    onSuccess = { cameraToEdit = null; snackMessage = "Barang '${updated.name}' diperbarui" },
                    onFailure = { err -> snackMessage = "Gagal: $err" }
                )
            }
        )
    }

    cameraToDelete?.let { cam ->
        AlertDialog(
            onDismissRequest = { cameraToDelete = null },
            containerColor = DarkSurface,
            title = { Text("Hapus Barang?", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = { Text("Yakin ingin menghapus \"${cam.name}\"?\nTindakan ini tidak dapat dibatalkan.", color = TextGray, fontSize = 14.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.adminDeleteCamera(cam.id,
                            onSuccess = { cameraToDelete = null; snackMessage = "'${cam.name}' dihapus dari inventori" },
                            onFailure = { err -> cameraToDelete = null; snackMessage = "Gagal: $err" }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedError)
                ) { Text("Hapus", fontWeight = FontWeight.SemiBold) }
            },
            dismissButton = {
                OutlinedButton(onClick = { cameraToDelete = null }) { Text("Batal", color = TextGray) }
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
    value: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, DarkCardBorder.copy(alpha = 0.5f))
    ) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.Start) {
            Box(
                Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextWhite, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(label, fontSize = 10.sp, color = TextGray, maxLines = 1)
        }
    }
}

@Composable
private fun AdminOrderCard(order: Order, onConfirm: () -> Unit, onComplete: () -> Unit) {
    val statusColor = when (order.status) {
        OrderStatus.WAITING -> YellowWaiting
        OrderStatus.ACTIVE -> GreenActive
        OrderStatus.LATE -> Color(0xFFFF334B)
        else -> TextGray
    }
    val statusLabel = when (order.status) {
        OrderStatus.WAITING -> "Menunggu"
        OrderStatus.ACTIVE -> "Aktif"
        OrderStatus.LATE -> "Terlambat"
        else -> "-"
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.3f))
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(order.camera.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextWhite, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("#${order.id}", fontSize = 12.sp, color = TextGray)
                }
                Surface(shape = RoundedCornerShape(8.dp), color = statusColor.copy(alpha = 0.15f)) {
                    Row(Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(Modifier.size(6.dp).clip(CircleShape).background(statusColor))
                        Text(statusLabel, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = statusColor)
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.CalendarMonth, null, Modifier.size(14.dp), tint = TextGray)
                    Spacer(Modifier.width(4.dp))
                    Text("${order.startDate} - ${order.endDate}", fontSize = 12.sp, color = TextGray)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Payments, null, Modifier.size(14.dp), tint = OrangePrimary)
                    Spacer(Modifier.width(4.dp))
                    Text(formatAdminRupiah(order.totalPrice), fontSize = 12.sp, color = OrangePrimary, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(Modifier.height(10.dp))
            when (order.status) {
                OrderStatus.WAITING -> Button(
                    onClick = onConfirm, Modifier.fillMaxWidth().height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenActive)
                ) {
                    Icon(Icons.Filled.CheckCircle, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Konfirmasi Pembayaran", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
                OrderStatus.ACTIVE, OrderStatus.LATE -> Button(
                    onClick = onComplete, Modifier.fillMaxWidth().height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePremium)
                ) {
                    Icon(Icons.Filled.AssignmentTurnedIn, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Selesaikan Sewa", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun AdminInventoryCard(camera: Camera, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, DarkCardBorder.copy(alpha = 0.5f))
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(52.dp).clip(RoundedCornerShape(10.dp)).background(DarkCard), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.CameraAlt, null, Modifier.size(28.dp), tint = OrangePrimary.copy(alpha = 0.7f))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(camera.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextWhite, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(camera.category, fontSize = 11.sp, color = TextGray)
                Text(formatAdminRupiah(camera.pricePerDay) + "/hari", fontSize = 12.sp, color = OrangePrimary, fontWeight = FontWeight.SemiBold)
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onEdit, modifier = Modifier.size(34.dp)) {
                    Icon(Icons.Outlined.Edit, "Edit", tint = BluePremium, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(34.dp)) {
                    Icon(Icons.Outlined.Delete, "Hapus", tint = RedError, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
private fun CameraFormDialog(title: String, initialCamera: Camera?, onDismiss: () -> Unit, onConfirm: (Camera) -> Unit) {
    var name by remember { mutableStateOf(initialCamera?.name ?: "") }
    var brand by remember { mutableStateOf(initialCamera?.brand ?: "") }
    var category by remember { mutableStateOf(initialCamera?.category ?: "") }
    var pricePerDay by remember { mutableStateOf(if ((initialCamera?.pricePerDay ?: 0L) > 0) initialCamera!!.pricePerDay.toString() else "") }
    var description by remember { mutableStateOf(initialCamera?.description ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
        ) {
            Column(Modifier.padding(24.dp)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextWhite)
                Spacer(Modifier.height(16.dp))

                @Composable
                fun FormField(label: String, value: String, multiline: Boolean = false, onValueChange: (String) -> Unit) {
                    Text(label, fontSize = 12.sp, color = TextGray, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(4.dp))
                    OutlinedTextField(
                        value = value, onValueChange = onValueChange,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = !multiline,
                        minLines = if (multiline) 2 else 1,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangePrimary, unfocusedBorderColor = DarkCardBorder,
                            focusedContainerColor = DarkNavy, unfocusedContainerColor = DarkNavy,
                            focusedTextColor = TextWhite, unfocusedTextColor = TextWhite, cursorColor = OrangePrimary
                        )
                    )
                    Spacer(Modifier.height(12.dp))
                }

                FormField("Nama Barang", name) { name = it }
                FormField("Merek", brand) { brand = it }
                FormField("Kategori (cth: DSLR, Lensa)", category) { category = it }
                FormField("Deskripsi", description, multiline = true) { description = it }

                Text("Harga / Hari (Rp)", fontSize = 12.sp, color = TextGray, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = pricePerDay,
                    onValueChange = { if (it.all { c -> c.isDigit() }) pricePerDay = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = { Text("Rp", fontSize = 14.sp, color = TextGray, modifier = Modifier.padding(start = 12.dp)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimary, unfocusedBorderColor = DarkCardBorder,
                        focusedContainerColor = DarkNavy, unfocusedContainerColor = DarkNavy,
                        focusedTextColor = TextWhite, unfocusedTextColor = TextWhite, cursorColor = OrangePrimary
                    )
                )
                Spacer(Modifier.height(24.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, Modifier.weight(1f), shape = RoundedCornerShape(10.dp)) {
                        Text("Batal", color = TextGray)
                    }
                    Button(
                        onClick = {
                            if (name.isBlank()) return@Button
                            val price = pricePerDay.toLongOrNull() ?: 0L
                            val camera = (initialCamera ?: Camera()).copy(
                                name = name.trim(), brand = brand.trim(),
                                category = category.trim(), description = description.trim(),
                                pricePerDay = price, isAvailable = true
                            )
                            onConfirm(camera)
                        },
                        Modifier.weight(1f), shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                    ) { Text("Simpan", fontWeight = FontWeight.SemiBold, color = TextWhite) }
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
