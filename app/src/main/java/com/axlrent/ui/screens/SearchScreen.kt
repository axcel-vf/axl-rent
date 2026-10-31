package com.axlrent.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: AppViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCamera: (String) -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val cameras by viewModel.cameras.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var selectedBrand by remember { mutableStateOf("All") }
    var selectedPrice by remember { mutableStateOf("All") }
    var selectedType by remember { mutableStateOf("All") }
    var selectedAvailable by remember { mutableStateOf("All") }

    val brands = remember(cameras) { listOf("All") + cameras.map { it.brand }.distinct().sorted() }
    val types = remember(cameras) { listOf("All") + cameras.map { it.category }.distinct().sorted() }
    val priceOptions = listOf("All", "Termurah", "Termahal")
    val availabilityOptions = listOf("All", "Available Only", "Rented Only")

    val filteredCameras = remember(cameras, searchQuery, selectedBrand, selectedPrice, selectedType, selectedAvailable) {
        cameras.filter { camera ->
            val matchesQuery = searchQuery.isEmpty() ||
                    camera.name.contains(searchQuery, true) ||
                    camera.brand.contains(searchQuery, true)
            val matchesBrand = selectedBrand == "All" || camera.brand.equals(selectedBrand, true)
            val matchesType = selectedType == "All" || camera.category.equals(selectedType, true)
            val matchesAvailable = when (selectedAvailable) {
                "Available Only" -> camera.isAvailable
                "Rented Only" -> !camera.isAvailable
                else -> true
            }
            matchesQuery && matchesBrand && matchesType && matchesAvailable
        }.let { list ->
            when (selectedPrice) {
                "Termurah" -> list.sortedBy { it.pricePerDay }
                "Termahal" -> list.sortedByDescending { it.pricePerDay }
                else -> list
            }
        }
    }

    Scaffold(
        containerColor = DarkNavy,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.CameraAlt, null, tint = OrangePrimary, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Axl Rent", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(onClick = {
                            Toast.makeText(context, "Tidak ada notifikasi baru", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Outlined.Notifications, null, tint = TextGray)
                        }
                        IconButton(
                            onClick = onNavigateToProfile,
                            modifier = Modifier
                                .size(34.dp)
                                .background(DarkSurface, CircleShape)
                        ) {
                            Icon(
                                Icons.Outlined.Person,
                                null,
                                tint = OrangePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkNavy, titleContentColor = TextWhite)
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = searchQuery, onValueChange = { searchQuery = it },
                placeholder = { Text("Cari kamera, lensa, atau gear...", color = TextGrayDark) },
                leadingIcon = { Icon(Icons.Outlined.Search, null, tint = TextGray) },
                trailingIcon = { IconButton(onClick = {}) { Icon(Icons.Outlined.FilterList, null, tint = TextGray) } },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OrangePrimary, unfocusedBorderColor = DarkCardBorder,
                    focusedContainerColor = DarkSurface, unfocusedContainerColor = DarkSurface,
                    cursorColor = OrangePrimary, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite
                ),
                singleLine = true
            )
            Spacer(Modifier.height(14.dp))
            
            // Filter chips
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterDropdownChip("Brand", selectedBrand, brands) { selectedBrand = it }
                FilterDropdownChip("Price", selectedPrice, priceOptions) { selectedPrice = it }
                FilterDropdownChip("Type", selectedType, types) { selectedType = it }
                FilterDropdownChip("Available", selectedAvailable, availabilityOptions) { selectedAvailable = it }
                
                if (selectedBrand != "All" || selectedPrice != "All" || selectedType != "All" || selectedAvailable != "All") {
                    IconButton(
                        onClick = {
                            selectedBrand = "All"
                            selectedPrice = "All"
                            selectedType = "All"
                            selectedAvailable = "All"
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .background(OrangePrimary.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear Filters", tint = OrangePrimary, modifier = Modifier.size(16.dp))
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
            Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Row { Text("Gear for you", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextWhite); Spacer(Modifier.width(6.dp)); Text("(${filteredCameras.size})", fontSize = 14.sp, color = TextGray) }
                Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Outlined.FilterList, null, Modifier.size(16.dp), tint = TextGray); Spacer(Modifier.width(4.dp)); Text("Popular", fontSize = 13.sp, color = TextGray) }
            }
            Spacer(Modifier.height(16.dp))
            
            if (filteredCameras.isEmpty()) {
                Box(Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.Search, null, Modifier.size(48.dp), tint = TextGrayDark)
                        Spacer(Modifier.height(8.dp))
                        Text("Tidak ada perlengkapan yang cocok", fontSize = 14.sp, color = TextGray)
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredCameras) { cam ->
                        SearchCard(
                            camera = cam,
                            onClick = { onNavigateToCamera(cam.id) },
                            onCartClick = {
                                viewModel.toggleFavorite(cam)
                                if (!cam.isFavorite) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Berhasil ditambahkan ke Keranjang")
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterDropdownChip(
    label: String,
    selectedValue: String,
    options: List<String>,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val isSelected = selectedValue != "All"

    Box {
        ElevatedButton(
            onClick = { expanded = true },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = if (isSelected) OrangePrimary else DarkSurface,
                contentColor = if (isSelected) TextWhite else TextGray
            ),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
            elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 2.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(if (isSelected) "$label: $selectedValue" else label, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.width(4.dp))
                Icon(Icons.Filled.KeyboardArrowDown, null, Modifier.size(16.dp), tint = if (isSelected) TextWhite else TextGray)
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(DarkSurface).border(1.dp, DarkCardBorder, RoundedCornerShape(10.dp))
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = if (selectedValue == option) OrangePrimary else TextWhite, fontSize = 14.sp) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun SearchCard(
    camera: Camera,
    onClick: () -> Unit,
    onCartClick: () -> Unit
) {
    Card(Modifier.fillMaxWidth().clickable { onClick() }, shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = DarkSurface), border = BorderStroke(1.dp, DarkCardBorder.copy(alpha = 0.5f))) {
        Column {
            Box(Modifier.fillMaxWidth().height(140.dp).background(Brush.verticalGradient(listOf(DarkCard, DarkSurface))), contentAlignment = Alignment.Center) {
                val imageModel = camera.localImageRes ?: camera.imageUrl.takeIf { it.isNotEmpty() }
                if (imageModel != null) {
                    AsyncImage(
                        model = imageModel,
                        contentDescription = camera.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(Icons.Outlined.CameraAlt, null, Modifier.size(40.dp), tint = TextGrayDark.copy(alpha = 0.3f))
                }
                Surface(Modifier.align(Alignment.TopEnd).padding(8.dp), shape = RoundedCornerShape(6.dp), color = if (camera.isAvailable) GreenActive.copy(0.2f) else RedError.copy(0.2f)) {
                    Text(if (camera.isAvailable) "AVAILABLE" else "RENTED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (camera.isAvailable) GreenActive else RedError, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                }
            }
            Column(Modifier.padding(12.dp)) {
                Text(camera.brand, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                Text(camera.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextWhite, maxLines = 1)
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column { Text("Rp ${camera.pricePerDay / 1000}k", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextWhite); Text("per day", fontSize = 10.sp, color = TextGray) }
                    Surface(
                        onClick = onCartClick,
                        shape = RoundedCornerShape(8.dp),
                        color = if (camera.isFavorite) OrangePrimary.copy(alpha = 0.2f) else DarkCard,
                        border = BorderStroke(1.dp, if (camera.isFavorite) OrangePrimary else DarkCardBorder)
                    ) {
                        Icon(
                            imageVector = if (camera.isFavorite) Icons.Filled.ShoppingCart else Icons.Outlined.AddShoppingCart,
                            contentDescription = "Add to Cart",
                            modifier = Modifier.padding(8.dp).size(18.dp),
                            tint = if (camera.isFavorite) OrangePrimary else TextGray
                        )
                    }
                }
            }
        }
    }
}
