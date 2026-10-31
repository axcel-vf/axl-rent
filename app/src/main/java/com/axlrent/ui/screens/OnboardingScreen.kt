package com.axlrent.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axlrent.ui.theme.*
import kotlinx.coroutines.launch
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext

data class OnboardingPage(
    val icon: ImageVector,
    val badge: String,
    val title: String,
    val highlight: String,
    val description: String
)

@Composable
fun OnboardingScreen(onNavigateToLogin: () -> Unit) {
    val pages = listOf(
        OnboardingPage(
            icon = Icons.Outlined.CameraAlt,
            badge = "Premium Gear",
            title = "Akses Gear",
            highlight = "Profesional",
            description = "Temukan berbagai pilihan kamera DSLR, Mirrorless, dan perlengkapan sinematografi terbaik untuk karya visual Anda."
        ),
        OnboardingPage(
            icon = Icons.Outlined.LocalShipping,
            badge = "Fast Delivery",
            title = "Pengiriman",
            highlight = "Cepat & Aman",
            description = "Nikmati layanan pengiriman yang cepat dan aman langsung ke lokasi Anda, atau ambil langsung di studio kami."
        ),
        OnboardingPage(
            icon = Icons.Outlined.Shield,
            badge = "Full Protection",
            title = "Proteksi",
            highlight = "Kerusakan",
            description = "Setiap penyewaan dilengkapi asuransi kerusakan sehingga Anda bisa berkarya dengan tenang tanpa khawatir."
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var showTermsDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }

    // Dialog Syarat & Ketentuan
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = {
                Text("Syarat & Ketentuan Sewa", fontWeight = FontWeight.Bold, color = TextWhite)
            },
            text = {
                Text(
                    "1. Wajib menunjukkan KTP/Kartu Identitas asli saat pengambilan unit.\n" +
                    "2. Jaminan sewa berupa identitas atau deposit sesuai ketentuan.\n" +
                    "3. Pengembalian unit harus tepat waktu sesuai durasi sewa.\n" +
                    "4. Kerusakan fisik akibat kelalaian ditanggung oleh penyewa (dilindungi asuransi opsional).",
                    color = TextGray,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showTermsDialog = false }) {
                    Text("Paham & Setuju", color = OrangePrimary, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = DarkCard,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Dialog Bantuan / Customer Support
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = {
                Text("Pusat Bantuan Axl Rent", fontWeight = FontWeight.Bold, color = TextWhite)
            },
            text = {
                Text(
                    "Butuh bantuan atau ingin bertanya seputar sewa kamera? Tim Customer Service kami siap melayani Anda via WhatsApp.",
                    color = TextGray,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showHelpDialog = false
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("https://wa.me/6289505764951?text=Halo%20Admin%20Axl%20Rent,%20saya%20butuh%20bantuan%20sewa%20kamera.")
                        }
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Hubungi via WhatsApp", color = TextWhite, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text("Tutup", color = TextGray)
                }
            },
            containerColor = DarkCard,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top bar with Skip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo
                Text(
                    text = "Axl ",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Light,
                    color = TextWhite,
                )
                Text(
                    text = "Rent",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangePrimary,
                )

                Text(
                    text = "Lewati",
                    color = TextGray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }

            // Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                OnboardingPageContent(pages[page])
            }

            // Bottom section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 12.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // CTA Button
                Button(
                    onClick = {
                        if (pagerState.currentPage < pages.size - 1) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            onNavigateToLogin()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = OrangePrimary
                    )
                ) {
                    Text(
                        text = "Lanjutkan",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = TextWhite
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Page indicator dots
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pages.size) { index ->
                        val isSelected = pagerState.currentPage == index
                        val color by animateColorAsState(
                            targetValue = if (isSelected) OrangePrimary else DarkCardBorder,
                            label = "dotColor"
                        )
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(
                                    width = if (isSelected) 24.dp else 8.dp,
                                    height = 8.dp
                                )
                                .clip(CircleShape)
                                .background(color)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Footer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { showTermsDialog = true }) {
                        Text("Syarat & Ketentuan", color = TextGray, fontSize = 12.sp)
                    }
                    Text("  •  ", color = TextGray, fontSize = 12.sp)
                    TextButton(onClick = { showHelpDialog = true }) {
                        Text("Bantuan", color = TextGray, fontSize = 12.sp)
                    }
                }

                Text(
                    text = "© 2024 Axl Rent Studio. Professional Gear Partner.",
                    fontSize = 11.sp,
                    color = TextGrayDark,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Image placeholder area with gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2A1A0A),
                            DarkSurface
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Camera icon as visual
            Icon(
                imageVector = page.icon,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = OrangePrimary.copy(alpha = 0.6f)
            )

            // Badge
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(14.dp),
                shape = RoundedCornerShape(20.dp),
                color = DarkSurface.copy(alpha = 0.9f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = page.icon,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = OrangePrimary
                    )
                    Text(
                        text = page.badge,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextWhite
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Title
        Text(
            text = page.title,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite,
            textAlign = TextAlign.Center
        )
        Text(
            text = page.highlight,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = OrangePrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Description
        Text(
            text = page.description,
            fontSize = 14.sp,
            color = TextGray,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

