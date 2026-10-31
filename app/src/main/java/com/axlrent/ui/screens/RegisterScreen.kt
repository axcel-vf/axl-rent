package com.axlrent.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.axlrent.ui.theme.*
import com.axlrent.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: AppViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var agreeTerms by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var ktpUri by remember { mutableStateOf<Uri?>(null) }
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> ktpUri = uri }
    )

    val authLoading by viewModel.authLoading.collectAsState()
    val authError by viewModel.authError.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.clearAuthError()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextWhite
                    )
                }
                Text(
                    text = "Axl Rent",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextWhite
                )
            }

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                // Title
                Text(
                    text = "Buat Akun Baru",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Mulai perjalanan kreatif Anda dengan peralatan\nsinematik terbaik.",
                    fontSize = 14.sp,
                    color = TextGray,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Name field
                FieldLabel("NAMA LENGKAP")
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    placeholder = { Text("John Doe", color = TextGrayDark) },
                    leadingIcon = {
                        Icon(Icons.Outlined.Person, null, tint = TextGray)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Email field
                FieldLabel("EMAIL")
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("name@example.com", color = TextGrayDark) },
                    leadingIcon = {
                        Icon(Icons.Outlined.Email, null, tint = TextGray)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Phone field
                FieldLabel("NOMOR TELEPON")
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    placeholder = { Text("+62 812 3456 7890", color = TextGrayDark) },
                    leadingIcon = {
                        Icon(Icons.Outlined.Phone, null, tint = TextGray)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Password field
                FieldLabel("KATA SANDI")
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("••••••••", color = TextGrayDark) },
                    leadingIcon = {
                        Icon(Icons.Outlined.Lock, null, tint = TextGray)
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Outlined.VisibilityOff
                                else Icons.Outlined.Visibility,
                                contentDescription = null,
                                tint = TextGray
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors(),
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None
                    else PasswordVisualTransformation()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // KTP Upload section
                FieldLabel("VERIFIKASI IDENTITAS (KTP)")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(
                            width = 1.dp,
                            color = OrangePrimary.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .background(DarkCard)
                        .clickable { galleryLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (ktpUri != null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(OrangeSubtle),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Outlined.Check,
                                    contentDescription = null,
                                    tint = OrangePrimary,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "KTP Berhasil Dipilih!",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextWhite
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Klik kembali untuk mengubah foto KTP.",
                                fontSize = 12.sp,
                                color = TextGray
                            )
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(OrangeSubtle),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Outlined.CameraAlt,
                                    contentDescription = null,
                                    tint = OrangePrimary,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Ambil Foto atau Upload KTP",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextWhite
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Pastikan seluruh bagian KTP terlihat jelas\ndan tidak buram.",
                                fontSize = 12.sp,
                                color = TextGray,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Terms checkbox
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Checkbox(
                        checked = agreeTerms,
                        onCheckedChange = { agreeTerms = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = OrangePrimary,
                            uncheckedColor = TextGray
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Saya setuju dengan Syarat & Ketentuan serta Kebijakan Privasi yang berlaku di Axl Rent.",
                        fontSize = 13.sp,
                        color = TextGray,
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Error message
                authError?.let { error ->
                    Text(
                        text = error,
                        color = Color.Red,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                }

                // Register button
                Button(
                    onClick = {
                        viewModel.registerUser(
                            context = context,
                            email = email,
                            password = password,
                            name = fullName,
                            phone = phone,
                            ktpUri = ktpUri,
                            onSuccess = {
                                android.widget.Toast.makeText(
                                    context,
                                    "Registrasi Berhasil! Silakan masuk menggunakan email dan password Anda.",
                                    android.widget.Toast.LENGTH_LONG
                                ).show()
                                onRegisterSuccess()
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    enabled = agreeTerms && !authLoading
                ) {
                    if (authLoading) {
                        CircularProgressIndicator(color = TextWhite, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = "Daftar Sekarang",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextWhite
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Login link
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("Sudah punya akun?  ", fontSize = 14.sp, color = TextGray)
                    Text(
                        text = "Masuk",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = OrangePrimary,
                        modifier = Modifier.clickable { onNavigateBack() }
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextGray,
        letterSpacing = 1.sp
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = OrangePrimary,
    unfocusedBorderColor = DarkCardBorder,
    focusedContainerColor = DarkNavy,
    unfocusedContainerColor = DarkNavy,
    cursorColor = OrangePrimary,
    focusedTextColor = TextWhite,
    unfocusedTextColor = TextWhite
)
