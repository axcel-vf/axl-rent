package com.axlrent.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import com.axlrent.ui.theme.*
import com.axlrent.viewmodel.AppViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: AppViewModel,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val isLoggedIn by viewModel.isUserLoggedIn.collectAsState()

    // Pulse animation for the camera circle
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    // Loading bar animation
    var loadingProgress by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = loadingProgress,
        animationSpec = tween(2000, easing = EaseInOutCubic),
        label = "loading"
    )

    // Fade in animation
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(800),
        label = "fadeIn"
    )

    LaunchedEffect(isLoggedIn) {
        visible = true
        delay(300)
        loadingProgress = 1f
        delay(2200)
        
        // Cek status login setelah animasi selesai
        if (isLoggedIn == true) {
            onNavigateToHome()
        } else if (isLoggedIn == false) {
            onNavigateToOnboarding()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1A2640),
                        DarkNavy
                    ),
                    center = Offset(0.3f, 0.3f),
                    radius = 1200f
                )
            )
            .alpha(alpha),
        contentAlignment = Alignment.Center
    ) {
        // Subtle circular decoration (top-right)
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0xFF1E293B),
                radius = 300.dp.toPx(),
                center = Offset(size.width * 0.8f, size.height * 0.15f),
                style = Stroke(width = 1.dp.toPx())
            )
            drawCircle(
                color = Color(0xFF1E293B),
                radius = 200.dp.toPx(),
                center = Offset(size.width * 0.8f, size.height * 0.15f),
                style = Stroke(width = 0.5.dp.toPx())
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Pulsating ring
            Box(contentAlignment = Alignment.Center) {
                // Outer pulse ring
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(pulseScale)
                        .alpha(pulseAlpha)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    TextGray.copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                // Inner circle with border
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1A2640)),
                    contentAlignment = Alignment.Center
                ) {
                    // Circle border
                    Canvas(modifier = Modifier.size(88.dp)) {
                        drawCircle(
                            color = Color(0xFF334155),
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                    }
                    Icon(
                        imageVector = Icons.Outlined.CameraAlt,
                        contentDescription = "Camera",
                        modifier = Modifier.size(36.dp),
                        tint = TextGrayLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // AXL RENT text
            Text(
                text = "A X L   R E N T",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                letterSpacing = 6.sp
            )
        }

        // Bottom section
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tagline
            Text(
                text = "Sewa Kamera Jadi Lebih Mudah",
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic,
                color = TextGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Loading bar
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(DarkSurface)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(OrangePrimary, OrangeLight)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "I N I T I A L I Z I N G   G E A R",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = TextGrayDark,
                letterSpacing = 3.sp
            )
        }
    }
}

private val EaseInOutCubic = CubicBezierEasing(0.65f, 0f, 0.35f, 1f)
