package com.axlrent.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.axlrent.ui.theme.*
import com.axlrent.viewmodel.AppViewModel
import com.axlrent.data.Camera

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistScreen(
    viewModel: AppViewModel,
    onNavigateToCamera: (String) -> Unit,
    onNavigateToBooking: (String) -> Unit,
    onNavigateToHome: () -> Unit
) {
    val cameras by viewModel.cameras.collectAsState()
    val favoriteCameras = cameras.filter { it.isFavorite }

    Scaffold(
        containerColor = DarkNavy,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Keranjang Saya",
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
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Spacer(Modifier.height(4.dp))

            if (favoriteCameras.isEmpty()) {
                // Empty state layout
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(horizontal = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            modifier = Modifier.size(80.dp),
                            shape = CircleShape,
                            color = OrangeSubtle
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Outlined.ShoppingCart,
                                    null,
                                    modifier = Modifier.size(36.dp),
                                    tint = OrangePrimary
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(
                            text = "Keranjang Kosong",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Belum ada perlengkapan kamera di keranjang Anda. Jelajahi katalog kami dan tambahkan perlengkapan.",
                            fontSize = 13.sp,
                            color = TextGray,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                        
                        Spacer(modifier = Modifier.height(28.dp))
                        
                        Button(
                            onClick = onNavigateToHome,
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = "Jelajahi Kamera",
                                color = TextWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            } else {
                // Subtitle section
                Text(
                    "Periksa kembali perlengkapan kamera pilihan Anda sebelum melakukan penyewaan.",
                    fontSize = 13.sp,
                    color = TextGray,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(Modifier.height(16.dp))

                // List layout with CartCard
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(favoriteCameras, key = { it.id }) { camera ->
                        CartCard(
                            camera = camera,
                            onCardClick = { onNavigateToCamera(camera.id) },
                            onDeleteClick = { viewModel.toggleFavorite(camera) },
                            onSewaClick = { onNavigateToBooking(camera.id) }
                        )
                    }
                }

                // Bottom Checkout Bar
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = DarkSurface,
                    border = BorderStroke(1.dp, DarkCardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val totalCartPrice = favoriteCameras.sumOf { it.pricePerDay } * 3
                        Column {
                            Text("Est. Total (3 Hari)", fontSize = 11.sp, color = TextGray)
                            Text(
                                text = formatRupiah(totalCartPrice),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = OrangePrimary
                            )
                        }
                        Button(
                            onClick = {
                                val firstCamera = favoriteCameras.firstOrNull()
                                if (firstCamera != null) {
                                    viewModel.createTemporaryBooking(
                                        camera = firstCamera,
                                        startDate = viewModel.selectedStartDate.value,
                                        endDate = viewModel.selectedEndDate.value,
                                        totalPrice = totalCartPrice
                                    )
                                    onNavigateToBooking(firstCamera.id)
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
                        ) {
                            Icon(Icons.Outlined.ShoppingCartCheckout, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Checkout (${favoriteCameras.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartCard(
    camera: Camera,
    onCardClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onSewaClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clickable { onCardClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = BorderStroke(1.dp, DarkCardBorder.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: Image
            Box(
                modifier = Modifier
                    .size(90.dp)
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
                        contentDescription = camera.name,
                        modifier = Modifier.size(36.dp),
                        tint = TextGrayDark.copy(alpha = 0.4f)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Middle side: Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = camera.brand.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangePrimary,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = camera.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = "Rating",
                        tint = OrangePrimary,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = camera.rating.toString(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextWhite
                    )
                    Text(
                        text = "(${camera.reviewCount})",
                        fontSize = 11.sp,
                        color = TextGray
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = formatRupiah(camera.pricePerDay),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = " /hari",
                        fontSize = 11.sp,
                        color = TextGray
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Right side: Actions
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.height(90.dp)
            ) {
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Hapus dari Keranjang",
                        tint = RedError,
                        modifier = Modifier.size(22.dp)
                    )
                }
                
                Surface(
                    onClick = onSewaClick,
                    shape = RoundedCornerShape(8.dp),
                    color = OrangePrimary
                ) {
                    Text(
                        text = "Sewa",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextWhite,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}
