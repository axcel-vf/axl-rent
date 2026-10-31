package com.axlrent.ui.screens

import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.axlrent.data.SampleData
import com.axlrent.data.Camera
import com.axlrent.ui.theme.*
import com.axlrent.viewmodel.AppViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraDetailScreen(
    cameraId: String,
    viewModel: AppViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToBooking: () -> Unit
) {
    val cameras by viewModel.cameras.collectAsState()
    val camera = cameras.find { it.id == cameraId } ?: SampleData.cameras.find { it.id == cameraId } ?: Camera()
    val review = SampleData.reviews[0]

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showReviewsDialog by remember { mutableStateOf(false) }

    val calendarDays = remember {
        listOf(
            CalendarDay(1, "28", "Nov", 2026, true),
            CalendarDay(2, "29", "Nov", 2026, true),
            CalendarDay(3, "30", "Nov", 2026, true),
            CalendarDay(4, "1", "Des", 2026, false),
            CalendarDay(5, "2", "Des", 2026, false),
            CalendarDay(6, "3", "Des", 2026, false),
            CalendarDay(7, "4", "Des", 2026, false),
            CalendarDay(8, "5", "Des", 2026, false),
            CalendarDay(9, "6", "Des", 2026, false),
            CalendarDay(10, "7", "Des", 2026, false),
            CalendarDay(11, "8", "Des", 2026, false),
            CalendarDay(12, "9", "Des", 2026, false),
            CalendarDay(13, "10", "Des", 2026, false),
            CalendarDay(14, "11", "Des", 2026, false)
        )
    }

    var selectedStartDay by remember { mutableStateOf<CalendarDay?>(calendarDays[5]) } // Des 3
    var selectedEndDay by remember { mutableStateOf<CalendarDay?>(calendarDays[8]) }   // Des 6

    val durationDays = remember(selectedStartDay, selectedEndDay) {
        if (selectedStartDay != null && selectedEndDay != null) {
            (selectedEndDay!!.id - selectedStartDay!!.id + 1).toLong()
        } else if (selectedStartDay != null) {
            1L
        } else {
            0L
        }
    }

    val onDayClick = { day: CalendarDay ->
        if (!day.isGray) {
            if (selectedStartDay == null || (selectedStartDay != null && selectedEndDay != null)) {
                selectedStartDay = day
                selectedEndDay = null
            } else {
                if (day.id < selectedStartDay!!.id) {
                    selectedStartDay = day
                    selectedEndDay = null
                } else {
                    selectedEndDay = day
                }
            }
        }
    }

    val onSewaClick = {
        val startStr = selectedStartDay?.let { "${it.display} ${it.month}" } ?: "20 Nov"
        val endStr = selectedEndDay?.let { "${it.display} ${it.month}, 2026" } ?: "${selectedStartDay?.display ?: "20"} ${selectedStartDay?.month ?: "Nov"}, 2026"
        
        viewModel.selectedStartDate.value = startStr
        viewModel.selectedEndDate.value = endStr
        viewModel.selectedDurationDays.value = durationDays
        
        onNavigateToBooking()
    }

    Scaffold(
        containerColor = DarkNavy,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextWhite) } },
                title = { Text("Axl Rent", fontWeight = FontWeight.Bold, fontSize = 17.sp) },
                actions = {
                    IconButton(onClick = {
                        viewModel.toggleFavorite(camera)
                        if (!camera.isFavorite) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Berhasil ditambahkan ke Keranjang")
                            }
                        }
                    }) {
                        Icon(
                            imageVector = if (camera.isFavorite) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
                            contentDescription = "Cart",
                            tint = if (camera.isFavorite) OrangePrimary else TextWhite
                        )
                    }
                    IconButton(onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "Sewa ${camera.name} di AXL Rent hanya Rp ${camera.pricePerDay}/hari! Hubungi kami sekarang.")
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        context.startActivity(shareIntent)
                    }) { Icon(Icons.Outlined.Share, null, tint = TextWhite) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkNavy, titleContentColor = TextWhite)
            )
        },
        bottomBar = {
            Surface(color = DarkSurface, tonalElevation = 8.dp) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp).navigationBarsPadding(), verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = onSewaClick, Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)) {
                        Text("Sewa Sekarang", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextWhite)
                    }
                }
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
            // Image area
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(Brush.verticalGradient(listOf(DarkCard, DarkNavy))),
                contentAlignment = Alignment.Center
            ) {
                val imageModel = camera.localImageRes ?: camera.imageUrl.takeIf { it.isNotEmpty() }
                if (imageModel != null) {
                    AsyncImage(
                        model = imageModel,
                        contentDescription = camera.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        Icons.Outlined.CameraAlt,
                        camera.name,
                        Modifier.size(80.dp),
                        tint = TextGrayDark.copy(0.3f)
                    )
                }
            }

            Column(Modifier.padding(horizontal = 20.dp)) {
                Spacer(Modifier.height(12.dp))
                // Badges
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(shape = RoundedCornerShape(6.dp), color = OrangePrimary) {
                        Text("PREMIUM GEAR", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextWhite, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, null, tint = OrangePrimary, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("${camera.rating} (${camera.reviewCount} Reviews)", fontSize = 13.sp, color = TextGray)
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text(camera.name, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                Spacer(Modifier.height(8.dp))
                Text(camera.fullDescription.ifEmpty { camera.description }, fontSize = 14.sp, color = TextGray, lineHeight = 22.sp)
                Spacer(Modifier.height(20.dp))

                // Price card
                Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), color = DarkSurface, border = BorderStroke(1.dp, DarkCardBorder)) {
                    Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("PRICE PER DAY", fontSize = 10.sp, color = TextGray, letterSpacing = 1.sp)
                            Spacer(Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(formatRupiah(camera.pricePerDay), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                                Text(" /hari", fontSize = 13.sp, color = TextGray)
                            }
                        }
                        Surface(shape = RoundedCornerShape(8.dp), color = GreenActive.copy(0.15f), border = BorderStroke(1.dp, GreenActive.copy(0.3f))) {
                            Row(Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Shield, null, Modifier.size(14.dp), tint = GreenActive)
                                Spacer(Modifier.width(4.dp))
                                Text("Insured Gear", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = GreenActive)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
                // Specs
                Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), color = DarkSurface, border = BorderStroke(1.dp, DarkCardBorder)) {
                    Column(Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Settings, null, Modifier.size(18.dp), tint = TextGray)
                            Spacer(Modifier.width(8.dp))
                            Text("Spesifikasi", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        }
                        Spacer(Modifier.height(16.dp))
                        SpecItem("Sensor", camera.sensor.ifEmpty { "Full-Frame" }, Icons.Outlined.Memory)
                        Spacer(Modifier.height(12.dp))
                        SpecItem("Resolusi", camera.resolution.ifEmpty { "4K Video" }, Icons.Outlined.HighQuality)
                        Spacer(Modifier.height(12.dp))
                        SpecItem("Lensa", camera.lens.ifEmpty { "Interchangeable" }, Icons.Outlined.Camera)
                    }
                }

                Spacer(Modifier.height(24.dp))
                // Review section
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Rating & Reviews", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    Text("View All >", fontSize = 13.sp, color = OrangePrimary, modifier = Modifier.clickable { showReviewsDialog = true })
                }
                Spacer(Modifier.height(12.dp))
                Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), color = DarkSurface, border = BorderStroke(1.dp, DarkCardBorder)) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(36.dp).clip(CircleShape).background(DarkCard), contentAlignment = Alignment.Center) {
                                Icon(Icons.Outlined.Person, null, Modifier.size(20.dp), tint = TextGray)
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(review.userName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                                Row { repeat(5) { Icon(Icons.Filled.Star, null, Modifier.size(12.dp), tint = OrangePrimary) } }
                            }
                            Text(review.date, fontSize = 12.sp, color = TextGray)
                        }
                        Spacer(Modifier.height(10.dp))
                        Text(review.comment, fontSize = 13.sp, color = TextGray, lineHeight = 20.sp)
                    }
                }

                Spacer(Modifier.height(24.dp))
                // Calendar section
                Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), color = DarkSurface, border = BorderStroke(1.dp, DarkCardBorder)) {
                    Column(Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.CalendarMonth, null, Modifier.size(20.dp), tint = OrangePrimary)
                            Spacer(Modifier.width(8.dp))
                            Text("Pilih Tanggal", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        }
                        Spacer(Modifier.height(16.dp))
                        // Mini calendar header
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            listOf("SU", "MO", "TU", "WE", "TH", "FR", "SA").forEach {
                                Text(it, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextGray, modifier = Modifier.width(36.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        // Sample dates row 1
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            calendarDays.subList(0, 7).forEach { day ->
                                val isSelected = day == selectedStartDay || day == selectedEndDay
                                val isInRange = selectedStartDay != null && selectedEndDay != null && day.id > selectedStartDay!!.id && day.id < selectedEndDay!!.id
                                val bg = when {
                                    isSelected -> OrangePrimary
                                    isInRange -> OrangePrimary.copy(0.3f)
                                    else -> androidx.compose.ui.graphics.Color.Transparent
                                }
                                Box(
                                    Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(bg)
                                        .clickable(enabled = !day.isGray) { onDayClick(day) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        day.display,
                                        fontSize = 14.sp,
                                        color = if (day.isGray) TextGrayDark else TextWhite,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        // Sample dates row 2
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            calendarDays.subList(7, 14).forEach { day ->
                                val isSelected = day == selectedStartDay || day == selectedEndDay
                                val isInRange = selectedStartDay != null && selectedEndDay != null && day.id > selectedStartDay!!.id && day.id < selectedEndDay!!.id
                                val bg = when {
                                    isSelected -> OrangePrimary
                                    isInRange -> OrangePrimary.copy(0.3f)
                                    else -> androidx.compose.ui.graphics.Color.Transparent
                                }
                                Box(
                                    Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(bg)
                                        .clickable(enabled = !day.isGray) { onDayClick(day) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        day.display,
                                        fontSize = 14.sp,
                                        color = if (day.isGray) TextGrayDark else TextWhite,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider(color = DarkCardBorder)
                        Spacer(Modifier.height(12.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column { Text("Durasi Sewa:", fontSize = 13.sp, color = TextGray) }
                            Text("$durationDays Hari", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        }
                        Spacer(Modifier.height(4.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Status & Stok:", fontSize = 13.sp, color = TextGray)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(8.dp).clip(CircleShape).background(if (camera.stock > 0) GreenActive else RedError))
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    if (camera.stock > 0) "Tersedia (${camera.stock} Unit)" else "Stok Habis",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (camera.stock > 0) GreenActive else RedError
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }

    if (showReviewsDialog) {
        AlertDialog(
            onDismissRequest = { showReviewsDialog = false },
            title = { Text("Semua Ulasan", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SampleData.reviews.forEach { r ->
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(36.dp).clip(CircleShape).background(DarkCard), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Outlined.Person, null, Modifier.size(20.dp), tint = TextGray)
                                }
                                Spacer(Modifier.width(10.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(r.userName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                                    Row { repeat(r.rating.toInt()) { Icon(Icons.Filled.Star, null, Modifier.size(12.dp), tint = OrangePrimary) } }
                                }
                                Text(r.date, fontSize = 12.sp, color = TextGray)
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(r.comment, fontSize = 13.sp, color = TextGray, lineHeight = 18.sp)
                            Spacer(Modifier.height(8.dp))
                            HorizontalDivider(color = DarkCardBorder.copy(alpha = 0.5f))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showReviewsDialog = false }) {
                    Text("Tutup", color = OrangePrimary)
                }
            },
            containerColor = DarkNavy
        )
    }
}

@Composable
private fun SpecItem(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), color = DarkCard, border = BorderStroke(1.dp, DarkCardBorder.copy(0.5f))) {
        Column(Modifier.padding(14.dp)) {
            Icon(icon, null, Modifier.size(20.dp), tint = OrangePrimary)
            Spacer(Modifier.height(6.dp))
            Text(label, fontSize = 11.sp, color = TextGray)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
        }
    }
}

data class CalendarDay(
    val id: Int,
    val display: String,
    val month: String,
    val year: Int,
    val isGray: Boolean
)
