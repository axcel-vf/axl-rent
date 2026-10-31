package com.axlrent.ui.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
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
import com.axlrent.data.Camera
import com.axlrent.data.SampleData
import com.axlrent.ui.theme.*
import com.axlrent.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    cameraId: String,
    viewModel: AppViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToPayment: () -> Unit
) {
    val cameras by viewModel.cameras.collectAsState()
    val camera = cameras.find { it.id == cameraId } ?: SampleData.cameras.find { it.id == cameraId } ?: Camera()
    var pickupMethod by remember { mutableStateOf("store") }
    val context = LocalContext.current

    val durationDays by viewModel.selectedDurationDays.collectAsState()
    val startDt by viewModel.selectedStartDate.collectAsState()
    val endDt by viewModel.selectedEndDate.collectAsState()

    val cameraRentCost = camera.pricePerDay * durationDays
    val insuranceCost = 50000L
    val totalAmount = cameraRentCost + insuranceCost

    val showDatePicker = { isStartDate: Boolean ->
        val currentCalendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedCal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                val formattedDate = SimpleDateFormat("d MMM", Locale.getDefault()).format(selectedCal.time)
                val formattedDateFull = SimpleDateFormat("d MMM, yyyy", Locale.getDefault()).format(selectedCal.time)
                
                if (isStartDate) {
                    viewModel.selectedStartDate.value = formattedDate
                } else {
                    viewModel.selectedEndDate.value = formattedDateFull
                }
                
                // Recalculate duration
                try {
                    val startStr = viewModel.selectedStartDate.value
                    var endStr = viewModel.selectedEndDate.value
                    if (endStr.contains(",")) {
                        // is full format
                    } else {
                        endStr = "$endStr, $year"
                    }
                    val startD = SimpleDateFormat("d MMM, yyyy", Locale.getDefault()).parse("$startStr, $year")
                    val endD = SimpleDateFormat("d MMM, yyyy", Locale.getDefault()).parse(endStr)
                    if (startD != null && endD != null) {
                        val diffMs = endD.time - startD.time
                        val diffDays = (diffMs / (1000 * 60 * 60 * 24)) + 1
                        viewModel.selectedDurationDays.value = if (diffDays > 0) diffDays else 1L
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            currentCalendar.get(Calendar.YEAR),
            currentCalendar.get(Calendar.MONTH),
            currentCalendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    Scaffold(
        containerColor = DarkNavy,
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextWhite) } },
                title = { Text("Axl Rent", fontWeight = FontWeight.Bold, fontSize = 17.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkNavy, titleContentColor = TextWhite)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = DarkSurface, tonalElevation = 0.dp) {
                listOf("HOME" to Icons.Outlined.Home, "HISTORY" to Icons.Outlined.History, "BOOKING" to Icons.Outlined.ShoppingCart, "PROFILE" to Icons.Outlined.Person).forEachIndexed { i, (l, ic) ->
                    NavigationBarItem(selected = i == 2, onClick = {}, icon = { Icon(ic, l) }, label = { Text(l, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = OrangePrimary, selectedTextColor = OrangePrimary, unselectedIconColor = TextGrayDark, unselectedTextColor = TextGrayDark, indicatorColor = OrangeSubtle))
                }
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
            // Product header
            Box(Modifier.fillMaxWidth().padding(horizontal = 20.dp).clip(RoundedCornerShape(16.dp)).background(Brush.horizontalGradient(listOf(DarkCard, DarkSurface)))) {
                Column(Modifier.padding(16.dp)) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkCard),
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
                                null,
                                Modifier.size(60.dp),
                                tint = TextGrayDark.copy(0.3f)
                            )
                        }
                    }
                    Surface(shape = RoundedCornerShape(6.dp), color = OrangePrimary) {
                        Text("PREMIUM KIT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextWhite, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(camera.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                }
            }

            Spacer(Modifier.height(20.dp))

            // Date selection
            Surface(Modifier.fillMaxWidth().padding(horizontal = 20.dp), shape = RoundedCornerShape(14.dp), color = DarkSurface, border = BorderStroke(1.dp, DarkCardBorder)) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.CalendarMonth, null, Modifier.size(18.dp), tint = TextGray)
                        Spacer(Modifier.width(8.dp))
                        Text("Select Rental Dates", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        DateBox("START DATE", startDt, { showDatePicker(true) }, Modifier.weight(1f))
                        DateBox("END DATE", endDt, { showDatePicker(false) }, Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(12.dp))
                    Surface(Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), color = DarkCard) {
                        Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Info, null, Modifier.size(16.dp), tint = TextGray)
                                Spacer(Modifier.width(6.dp))
                                Text("Total duration:", fontSize = 13.sp, color = TextGray)
                            }
                            Text("$durationDays Days", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Pickup method
            Text("Pickup Method", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextWhite, modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(Modifier.height(12.dp))

            PickupOption("Ambil di Studio", "Studio Axl (09:00 - 18:00) • Gratis", Icons.Outlined.Store, pickupMethod == "store") { pickupMethod = "store" }
            Spacer(modifier = Modifier.height(8.dp))
            PickupOption("Kirim ke Rumah", "Dikirim ke alamat Anda • Rp 25.000", Icons.Outlined.LocalShipping, pickupMethod == "delivery") { pickupMethod = "delivery" }
            if (pickupMethod == "delivery") {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Pengiriman akan dikoordinasikan oleh admin via WhatsApp setelah booking dikonfirmasi",
                    fontSize = 12.sp,
                    color = OrangePrimary,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            // Booking Summary
            Surface(Modifier.fillMaxWidth().padding(horizontal = 20.dp), shape = RoundedCornerShape(14.dp), color = DarkSurface, border = BorderStroke(1.dp, DarkCardBorder)) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Receipt, null, Modifier.size(18.dp), tint = TextGray)
                        Spacer(Modifier.width(8.dp))
                        Text("Ringkasan Pemesanan", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                    }
                    Spacer(Modifier.height(16.dp))
                    SummaryRow("${camera.name} ($durationDays hari)", formatRupiah(cameraRentCost))
                    SummaryRow("Asuransi Kerusakan", formatRupiah(insuranceCost))
                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = DarkCardBorder)
                    Spacer(Modifier.height(12.dp))
                    Text("TOTAL PEMBAYARAN", fontSize = 10.sp, color = TextGray, letterSpacing = 1.sp)
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(formatRupiah(totalAmount), fontSize = 26.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                        Spacer(Modifier.width(8.dp))
                        Text("Termasuk PPN", fontSize = 12.sp, color = TextGray)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Confirm button
            Button(
                onClick = {
                    viewModel.createTemporaryBooking(
                        camera = camera,
                        startDate = startDt,
                        endDate = endDt,
                        totalPrice = totalAmount
                    )
                    onNavigateToPayment()
                },
                Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Text("Konfirmasi Booking", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = TextWhite)
                Spacer(Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = TextWhite)
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Lock, null, Modifier.size(14.dp), tint = TextGray)
                Spacer(Modifier.width(4.dp))
                Text("Secure 256-bit Encrypted Checkout", fontSize = 12.sp, color = TextGray)
            }

            Spacer(Modifier.height(16.dp))
            // Cancellation policy
            Surface(Modifier.fillMaxWidth().padding(horizontal = 20.dp), shape = RoundedCornerShape(14.dp), color = DarkSurface, border = BorderStroke(1.dp, DarkCardBorder)) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Filled.Info, null, Modifier.size(20.dp), tint = BluePremium)
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("Cancellation Policy", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                        Text("Full refund if cancelled 48h before pickup.\nA 15% re-stocking fee applies thereafter.", fontSize = 12.sp, color = TextGray, lineHeight = 18.sp)
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DateBox(label: String, date: String, onClick: () -> Unit, modifier: Modifier) {
    Surface(modifier.clickable { onClick() }, shape = RoundedCornerShape(10.dp), color = DarkCard, border = BorderStroke(1.dp, DarkCardBorder)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(label, fontSize = 9.sp, color = TextGray, letterSpacing = 1.sp)
                Text(date, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
            }
            Icon(Icons.Outlined.CalendarMonth, null, Modifier.size(18.dp), tint = TextGray)
        }
    }
}

@Composable
private fun PickupOption(title: String, desc: String, icon: androidx.compose.ui.graphics.vector.ImageVector, selected: Boolean, onClick: () -> Unit) {
    Surface(Modifier.fillMaxWidth().padding(horizontal = 20.dp).clickable { onClick() }, shape = RoundedCornerShape(14.dp), color = DarkSurface,
        border = BorderStroke(1.dp, if (selected) OrangePrimary else DarkCardBorder)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(if (selected) OrangeSubtle else DarkCard), contentAlignment = Alignment.Center) {
                Icon(icon, null, Modifier.size(20.dp), tint = if (selected) OrangePrimary else TextGray)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextWhite)
                Text(desc, fontSize = 12.sp, color = TextGray)
            }
            RadioButton(selected = selected, onClick = onClick, colors = RadioButtonDefaults.colors(selectedColor = OrangePrimary, unselectedColor = TextGray))
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 13.sp, color = TextGray)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextWhite)
    }
}
