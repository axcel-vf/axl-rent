package com.axlrent.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.graphics.asImageBitmap
import com.axlrent.data.SampleData
import com.axlrent.data.Camera
import com.axlrent.ui.theme.*
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.runtime.collectAsState
import com.axlrent.viewmodel.AppViewModel
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    onNavigateToSearch: () -> Unit,
    onNavigateToCamera: (String) -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("DSLR") }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val userProfile by viewModel.currentUserProfile.collectAsState()
    val cameras by viewModel.cameras.collectAsState()
    val camerasLoading by viewModel.camerasLoading.collectAsState()
    val avatarUri by viewModel.profileImageUri.collectAsState()
    val avatarBitmap by viewModel.profileBitmap.collectAsState()

    // Decode base64 avatar from profile if available
    LaunchedEffect(userProfile?.avatarUrl) {
        viewModel.decodeAvatarFromProfile()
    }

    val userGreetingName = userProfile?.name ?: "Fotografer"

    Scaffold(
        containerColor = DarkNavy,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Status bar spacing
            Spacer(modifier = Modifier.statusBarsPadding())

            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.CameraAlt,
                        contentDescription = null,
                        tint = OrangePrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Axl Rent",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        Toast.makeText(context, "Tidak ada notifikasi baru", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Outlined.Notifications, null, tint = TextGray)
                    }
                    // Profile Avatar (syncs with user profile picture)
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(DarkSurface)
                            .border(1.5.dp, OrangePrimary, CircleShape)
                            .clickable { onNavigateToProfile() },
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            avatarUri != null -> {
                                AsyncImage(
                                    model = avatarUri,
                                    contentDescription = "Foto Profil",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            avatarBitmap != null -> {
                                Image(
                                    bitmap = avatarBitmap!!.asImageBitmap(),
                                    contentDescription = "Foto Profil",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            !userProfile?.avatarUrl.isNullOrEmpty() && (userProfile?.avatarUrl?.startsWith("http") == true) -> {
                                AsyncImage(
                                    model = userProfile?.avatarUrl,
                                    contentDescription = "Foto Profil",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            else -> {
                                Icon(
                                    Icons.Outlined.Person,
                                    contentDescription = "Profil",
                                    tint = OrangePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Greeting
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    text = "Halo, $userGreetingName!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextGrayLight
                )
                Text(
                    text = "Siap untuk karya masterpiece hari ini?",
                    fontSize = 13.sp,
                    color = TextGray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clickable { onNavigateToSearch() },
                shape = RoundedCornerShape(14.dp),
                color = DarkSurface,
                border = BorderStroke(1.dp, DarkCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.Search,
                        null,
                        tint = TextGrayDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Cari perlengkapan...",
                        fontSize = 14.sp,
                        color = TextGrayDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Promo banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(140.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF1A2740),
                                Color(0xFF0F2030)
                            )
                        )
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Column for Details
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = OrangePrimary
                        ) {
                            Text(
                                text = "PROMO TERBATAS",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                letterSpacing = 0.5.sp
                            )
                        }
                        Column {
                            Text(
                                text = "Hemat 30%",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Text(
                                text = "Lensa G-Master",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextWhite
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GreenActive
                        ) {
                            Text(
                                text = "Sewa Sekarang",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextWhite,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Right Side: Promo Image Illustration
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = "https://images.unsplash.com/photo-1617005082133-548c4dd27f35?q=80&w=600&auto=format&fit=crop",
                            contentDescription = "Lensa G-Master Promo",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Categories
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Kategori",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Text(
                    text = "Lihat Semua",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = OrangePrimary,
                    modifier = Modifier.clickable { onNavigateToSearch() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            val categoryIcons = mapOf(
                "DSLR" to Icons.Outlined.CameraAlt,
                "Mirrorless" to Icons.Outlined.Camera,
                "Action Cam" to Icons.Outlined.Videocam,
                "Lensa" to Icons.Outlined.Camera,
                "Lighting" to Icons.Outlined.FlashlightOn,
                "Tripod" to Icons.Outlined.CameraAlt
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(SampleData.categories) { category ->
                    val isSelected = selectedCategory == category
                    Surface(
                        onClick = { selectedCategory = category },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) OrangePrimary else DarkSurface,
                        border = if (!isSelected) BorderStroke(1.dp, DarkCardBorder) else null
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                categoryIcons[category] ?: Icons.Outlined.CameraAlt,
                                null,
                                tint = if (isSelected) TextWhite else TextGray,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = category,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isSelected) TextWhite else TextGray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Products list
            Text(
                text = "Pilihan Teratas",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (camerasLoading && cameras.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = OrangePrimary)
                }
            } else {
                val filteredCameras = cameras.filter { it.category.lowercase() == selectedCategory.lowercase() }
                if (filteredCameras.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tidak ada kamera untuk kategori ini",
                            color = TextGray,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    filteredCameras.forEach { camera ->
                        ProductCard(
                            camera = camera,
                            onClick = { onNavigateToCamera(camera.id) },
                            onFavoriteClick = {
                                viewModel.toggleFavorite(camera)
                                if (!camera.isFavorite) {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Berhasil ditambahkan ke Keranjang")
                                    }
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ProductCard(
    camera: Camera,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, DarkCardBorder.copy(alpha = 0.5f))
    ) {
        Column {
            // Image area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                DarkCard,
                                DarkSurface
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                val imageModel = camera.localImageRes ?: camera.imageUrl.takeIf { it.isNotEmpty() }
                if (imageModel != null) {
                    AsyncImage(
                        model = imageModel,
                        contentDescription = camera.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    )
                } else {
                    // Fallback ikon placeholder
                    Icon(
                        Icons.Outlined.CameraAlt,
                        contentDescription = camera.name,
                        modifier = Modifier.size(64.dp),
                        tint = TextGrayDark.copy(alpha = 0.4f)
                    )
                }

                // Wishlist heart -> Keranjang Belanja
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = if (camera.isFavorite) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
                        contentDescription = "Keranjang",
                        tint = if (camera.isFavorite) OrangePrimary else TextGray
                    )
                }

                // Rating badge
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = DarkNavy.copy(alpha = 0.85f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Filled.Star,
                            null,
                            tint = OrangePrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = camera.rating.toString(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextWhite
                        )
                    }
                }
            }

            // Info section
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = camera.name,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = camera.description,
                    fontSize = 13.sp,
                    color = TextGray
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = formatRupiah(camera.pricePerDay),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimary
                        )
                        Text(
                            text = " /hari",
                            fontSize = 12.sp,
                            color = TextGray
                        )
                    }
                    Surface(
                        onClick = onClick,
                        shape = RoundedCornerShape(10.dp),
                        color = OrangePrimary
                    ) {
                        Text(
                            text = "Sewa",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextWhite,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

// BottomNavBar removed since it's global now

fun formatRupiah(amount: Long): String {
    val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    return format.format(amount).replace("Rp", "Rp ").replace(",00", "")
}
