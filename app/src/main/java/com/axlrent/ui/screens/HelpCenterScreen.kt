package com.axlrent.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.QuestionAnswer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axlrent.ui.theme.*

data class FaqItem(
    val question: String,
    val answer: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpCenterScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var expandedIndex by remember { mutableStateOf<Int?>(0) }

    val faqList = remember {
        listOf(
            FaqItem(
                question = "Bagaimana cara menyewa kamera di Axl Rent?",
                answer = "Pilih kamera yang ingin Anda sewa dari halaman Utama atau Pencarian, tentukan tanggal & durasi penyewaan, lalu selesaikan pembayaran online via QRIS atau E-Wallet."
            ),
            FaqItem(
                question = "Apa saja dokumen yang dibutuhkan untuk verifikasi?",
                answer = "Anda hanya perlu mengunggah foto KTP asli dan mengkonfirmasi nomor HP aktif saat pendaftaran akun. Tidak ada dokumen fisik lain yang disita."
            ),
            FaqItem(
                question = "Bagaimana cara pengambilan dan pengembalian unit?",
                answer = "Unit dapat diambil secara langsung di store resmi Axl Rent atau dikirimkan via kurir instan (GoSend/GrabExpress) setelah pembayaran terkonfirmasi LUNAS."
            ),
            FaqItem(
                question = "Bagaimana jika pengembalian kamera terlambat?",
                answer = "Keterlambatan pengembalian melebihi waktu toleransi (2 jam) akan dikenakan denda harian sesuai tarif sewa kamera tersebut."
            ),
            FaqItem(
                question = "Apakah kamera yang disewa dilindungi asuransi?",
                answer = "Ya, setiap unit sewaan terlindungi oleh Axl Protection. Kerusakan karena pemakaian wajar ditanggung asuransi, sedangkan kerusakan akibat kelalaian berat akan disesuaikan dengan syarat garansi."
            )
        )
    }

    Scaffold(
        containerColor = DarkNavy,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Pusat Bantuan & CS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = TextWhite
                        )
                    }
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(12.dp))

            // Header Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = DarkSurface,
                border = BorderStroke(1.dp, DarkCardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(OrangeSubtle),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.HelpOutline,
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = OrangePrimary
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Ada pertanyaan?",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Temukan jawaban cepat atau hubungi Customer Service kami 24/7.",
                            fontSize = 12.sp,
                            color = TextGray,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // FAQ Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.QuestionAnswer,
                    contentDescription = null,
                    tint = OrangePrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Pertanyaan Sering Diajukan (FAQ)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }

            Spacer(Modifier.height(12.dp))

            // Accordion FAQ List
            faqList.forEachIndexed { index, item ->
                val isExpanded = expandedIndex == index

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable {
                            expandedIndex = if (isExpanded) null else index
                        },
                    shape = RoundedCornerShape(14.dp),
                    color = if (isExpanded) DarkCard else DarkSurface,
                    border = BorderStroke(
                        1.dp,
                        if (isExpanded) OrangePrimary.copy(alpha = 0.5f) else DarkCardBorder.copy(alpha = 0.5f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.question,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isExpanded) OrangePrimary else TextWhite,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(Modifier.width(8.dp))
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = if (isExpanded) "Ciutkan" else "Buka",
                                tint = if (isExpanded) OrangePrimary else TextGray
                            )
                        }

                        AnimatedVisibility(
                            visible = isExpanded,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column {
                                Spacer(Modifier.height(12.dp))
                                HorizontalDivider(color = DarkCardBorder.copy(alpha = 0.4f))
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    text = item.answer,
                                    fontSize = 13.sp,
                                    color = TextGray,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            // Direct Contact Card with WhatsApp Button
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = DarkSurface,
                border = BorderStroke(1.dp, GreenActive.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Masih Butuh Bantuan?",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Tim Customer Service kami siap melayani Anda secara langsung melalui WhatsApp.",
                        fontSize = 12.sp,
                        color = TextGray,
                        lineHeight = 18.sp
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val waUrl = "https://api.whatsapp.com/send?phone=6281234567890&text=Halo%20Admin%20Axl%20Rent,%20saya%20butuh%20bantuan"
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(waUrl))
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF25D366)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Outlined.Chat,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = "Hubungi CS via WhatsApp",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}
