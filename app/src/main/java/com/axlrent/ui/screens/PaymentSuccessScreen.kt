package com.axlrent.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axlrent.ui.theme.*
import com.axlrent.viewmodel.AppViewModel
import kotlinx.coroutines.delay

@Composable
fun PaymentSuccessScreen(
    viewModel: AppViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToHistory: () -> Unit
) {
    // Entrance animation
    var visible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.3f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f),
        label = "scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(600),
        label = "alpha"
    )

    // Confetti shimmer
    val infiniteTransition = rememberInfiniteTransition(label = "confetti")
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer"
    )

    LaunchedEffect(Unit) {
        delay(100)
        visible = true
        delay(2500)
        onNavigateToHistory()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy)
    ) {
        // Confetti dots scattered
        val confettiPositions = remember {
            listOf(
                0.1f to 0.3f, 0.85f to 0.25f, 0.15f to 0.22f,
                0.9f to 0.35f, 0.5f to 0.2f, 0.3f to 0.38f,
                0.7f to 0.4f, 0.05f to 0.35f, 0.95f to 0.28f,
                0.4f to 0.25f, 0.6f to 0.32f, 0.2f to 0.42f
            )
        }

        confettiPositions.forEachIndexed { index, (x, y) ->
            val colors = listOf(OrangePrimary, OrangeLight, GreenActive, TextGrayLight)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(x = (x * 400).dp, y = (y * 800).dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(if (index % 3 == 0) 6.dp else 4.dp)
                        .alpha(shimmerAlpha * (if (index % 2 == 0) 1f else 0.6f))
                        .clip(if (index % 2 == 0) CircleShape else RoundedCornerShape(1.dp))
                        .background(colors[index % colors.size])
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Brand name
            Text(
                text = "Axl Rent",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = OrangePrimary
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Success icon with ring
            Box(
                modifier = Modifier
                    .scale(scale)
                    .alpha(alpha),
                contentAlignment = Alignment.Center
            ) {
                // Outer ring glow
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    OrangePrimary.copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                // Inner ring
                androidx.compose.foundation.Canvas(
                    modifier = Modifier.size(100.dp)
                ) {
                    drawCircle(
                        color = DarkCardBorder,
                        style = Stroke(width = 3.dp.toPx())
                    )
                }
                // Check icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    DarkSurface,
                                    DarkCard
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = "Success",
                        modifier = Modifier.size(40.dp),
                        tint = OrangePrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Success text
            Text(
                text = "Pembayaran Berhasil!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                modifier = Modifier.alpha(alpha)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Terima kasih telah mempercayai kami. Pesanan\nAnda akan segera diproses oleh tim kami.",
                fontSize = 14.sp,
                color = TextGray,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.alpha(alpha)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Transaction details card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(alpha),
                shape = RoundedCornerShape(16.dp),
                color = DarkSurface,
                border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                    brush = Brush.linearGradient(
                        colors = listOf(DarkCardBorder, DarkCardBorder)
                    )
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ID TRANSAKSI",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextGray,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "AXL-9283741",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GreenActive.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "LUNAS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GreenActive,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = DarkCardBorder)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Product info
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkCard),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.CameraAlt,
                                null,
                                modifier = Modifier.size(24.dp),
                                tint = TextGrayDark.copy(alpha = 0.5f)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Sony Alpha A7 IV Kit",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextWhite
                            )
                            Text(
                                text = "3 Hari • Jabodetabek • Rp 1.250.000",
                                fontSize = 12.sp,
                                color = TextGray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // CTA buttons
            Button(
                onClick = onNavigateToHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Text(
                    text = "Kembali ke Beranda",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextWhite
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onNavigateToHistory,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                border = ButtonDefaults.outlinedButtonBorder(enabled = true),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = OrangeSubtle
                )
            ) {
                Text(
                    text = "Cek Riwayat Pesanan",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OrangePrimary
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
