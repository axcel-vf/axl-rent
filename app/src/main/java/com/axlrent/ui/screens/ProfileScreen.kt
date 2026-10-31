package com.axlrent.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axlrent.ui.theme.*
import androidx.compose.runtime.collectAsState
import com.axlrent.data.OrderStatus
import com.axlrent.data.UserProfile
import com.axlrent.viewmodel.AppViewModel
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import android.content.Intent
import androidx.compose.ui.graphics.asImageBitmap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: AppViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToCart: () -> Unit = {},
    onNavigateToHelpCenter: () -> Unit = {},
    onLogout: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    var pushNotificationsEnabled by remember { mutableStateOf(true) }
    var emailUpdatesEnabled by remember { mutableStateOf(false) }
    var privateAccountEnabled by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val userProfile by viewModel.currentUserProfile.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val avatarUri by viewModel.profileImageUri.collectAsState()
    val avatarBitmap by viewModel.profileBitmap.collectAsState()

    var isEditingName by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(userProfile?.name ?: "Fotografer") }

    // Settings sub-dialogs
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showNotificationSettingsDialog by remember { mutableStateOf(false) }
    var showPaymentMethodsDialog by remember { mutableStateOf(false) }
    var showHelpCenterDialog by remember { mutableStateOf(false) }
    var showPrivacyPolicyDialog by remember { mutableStateOf(false) }

    // Forms fields
    var editName by remember { mutableStateOf("") }
    var editEmail by remember { mutableStateOf("") }
    var editPhone by remember { mutableStateOf("") }
    var editBio by remember { mutableStateOf("") }

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var deleteAccountPassword by remember { mutableStateOf("") }
    var deleteAccountPasswordVisible by remember { mutableStateOf(false) }

    // Notifications sub-states
    var notifStatusSewa by remember { mutableStateOf(true) }
    var notifPromo by remember { mutableStateOf(true) }
    var notifEmail by remember { mutableStateOf(false) }

    // Payment methods
    var selectedPaymentMethod by remember { mutableStateOf("Visa (.... 4321)") }

    // Sync with profile changes
    LaunchedEffect(userProfile) {
        userProfile?.name?.let { editedName = it }
    }

    // Decode base64 avatar from Firestore on first load
    LaunchedEffect(userProfile?.avatarUrl) {
        viewModel.decodeAvatarFromProfile()
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.updateAvatar(context, it) {
                Toast.makeText(context, "Foto profil berhasil diperbarui!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Logout confirmation dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    "Konfirmasi Keluar",
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            },
            text = {
                Text(
                    "Apakah Anda yakin ingin keluar dari akun?",
                    color = TextGray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logoutUser(onSuccess = onLogout)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Text("Keluar", color = TextWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal", color = TextGray)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Settings dialog (Interactive List with professional options)
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = {
                Text(
                    "Pengaturan Akun",
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 1. Edit Profil Lengkap
                    TextButton(
                        onClick = {
                            showSettingsDialog = false
                            editName = userProfile?.name ?: ""
                            editEmail = userProfile?.email ?: ""
                            editPhone = userProfile?.phone ?: ""
                            editBio = userProfile?.bio ?: ""
                            showEditProfileDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.Edit, null, tint = OrangePrimary, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(12.dp))
                            Text("Edit Profil Lengkap", color = TextWhite, fontWeight = FontWeight.Medium)
                        }
                    }
                    HorizontalDivider(color = DarkCardBorder.copy(alpha = 0.3f))

                    // 2. Ubah Kata Sandi & Keamanan
                    TextButton(
                        onClick = {
                            showSettingsDialog = false
                            currentPassword = ""
                            newPassword = ""
                            confirmPassword = ""
                            currentPasswordVisible = false
                            newPasswordVisible = false
                            confirmPasswordVisible = false
                            showChangePasswordDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.Lock, null, tint = OrangePrimary, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(12.dp))
                            Text("Ubah Kata Sandi & Keamanan", color = TextWhite, fontWeight = FontWeight.Medium)
                        }
                    }
                    HorizontalDivider(color = DarkCardBorder.copy(alpha = 0.3f))

                    // 3. Pengaturan Notifikasi
                    TextButton(
                        onClick = {
                            showSettingsDialog = false
                            showNotificationSettingsDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.Notifications, null, tint = OrangePrimary, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(12.dp))
                            Text("Pengaturan Notifikasi", color = TextWhite, fontWeight = FontWeight.Medium)
                        }
                    }
                    HorizontalDivider(color = DarkCardBorder.copy(alpha = 0.3f))

                    // 4. Metode Pembayaran
                    TextButton(
                        onClick = {
                            showSettingsDialog = false
                            showPaymentMethodsDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.Payment, null, tint = OrangePrimary, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(12.dp))
                            Text("Metode Pembayaran", color = TextWhite, fontWeight = FontWeight.Medium)
                        }
                    }
                    HorizontalDivider(color = DarkCardBorder.copy(alpha = 0.3f))

                    // 5. Kebijakan Privasi
                    TextButton(
                        onClick = {
                            showSettingsDialog = false
                            showPrivacyPolicyDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.PrivacyTip, null, tint = OrangePrimary, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(12.dp))
                            Text("Kebijakan Privasi & Ketentuan", color = TextWhite, fontWeight = FontWeight.Medium)
                        }
                    }
                    HorizontalDivider(color = DarkCardBorder.copy(alpha = 0.3f))

                    // 7. Tombol Keluar Akun di paling bawah dengan warna teks merah
                    TextButton(
                        onClick = {
                            showSettingsDialog = false
                            showLogoutDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Logout, null, tint = Color.Red, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(12.dp))
                            Text("Keluar Akun", color = Color.Red, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text("Tutup", color = TextGray)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // 1. Edit Profil Lengkap Dialog
    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = {
                Text(
                    "Edit Profil Lengkap",
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Nama Lengkap", color = TextGray) },
                        textStyle = LocalTextStyle.current.copy(color = TextWhite),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = DarkCardBorder.copy(alpha = 0.5f),
                            cursorColor = OrangePrimary
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        label = { Text("Email", color = TextGray) },
                        textStyle = LocalTextStyle.current.copy(color = TextWhite),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = DarkCardBorder.copy(alpha = 0.5f),
                            cursorColor = OrangePrimary
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Nomor Telepon", color = TextGray) },
                        textStyle = LocalTextStyle.current.copy(color = TextWhite),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = DarkCardBorder.copy(alpha = 0.5f),
                            cursorColor = OrangePrimary
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editBio,
                        onValueChange = { editBio = it },
                        label = { Text("Bio / Keterangan", color = TextGray) },
                        textStyle = LocalTextStyle.current.copy(color = TextWhite),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = DarkCardBorder.copy(alpha = 0.5f),
                            cursorColor = OrangePrimary
                        ),
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editName.isBlank() || editEmail.isBlank() || editPhone.isBlank()) {
                            Toast.makeText(context, "Nama, Email, dan Telepon wajib diisi", Toast.LENGTH_SHORT).show()
                        } else {
                            val currentProfile = userProfile ?: UserProfile()
                            val updatedProfile = currentProfile.copy(
                                name = editName,
                                email = editEmail,
                                phone = editPhone,
                                bio = editBio
                            )
                            viewModel.updateUserProfile(
                                updatedProfile,
                                onSuccess = {
                                    showEditProfileDialog = false
                                    Toast.makeText(context, "Profil berhasil disimpan!", Toast.LENGTH_SHORT).show()
                                },
                                onFailure = { error ->
                                    Toast.makeText(context, "Gagal menyimpan: $error", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Text("Simpan", color = TextWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Batal", color = TextGray)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // 2. Ubah Kata Sandi & Keamanan Dialog
    if (showChangePasswordDialog) {
        AlertDialog(
            onDismissRequest = {
                currentPassword = ""
                newPassword = ""
                confirmPassword = ""
                showChangePasswordDialog = false
            },
            title = {
                Text(
                    "Kata Sandi & Keamanan",
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // a. Kata Sandi Saat Ini
                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text("Kata Sandi Saat Ini", color = TextGray) },
                        textStyle = LocalTextStyle.current.copy(color = TextWhite),
                        visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                                Icon(
                                    imageVector = if (currentPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (currentPasswordVisible) "Sembunyikan Kata Sandi" else "Tampilkan Kata Sandi",
                                    tint = TextGray
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = DarkCardBorder.copy(alpha = 0.5f),
                            cursorColor = OrangePrimary
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // b. Kata Sandi Baru
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("Kata Sandi Baru", color = TextGray) },
                        textStyle = LocalTextStyle.current.copy(color = TextWhite),
                        visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                Icon(
                                    imageVector = if (newPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (newPasswordVisible) "Sembunyikan Kata Sandi" else "Tampilkan Kata Sandi",
                                    tint = TextGray
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = DarkCardBorder.copy(alpha = 0.5f),
                            cursorColor = OrangePrimary
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // c. Konfirmasi Kata Sandi Baru
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Konfirmasi Kata Sandi Baru", color = TextGray) },
                        textStyle = LocalTextStyle.current.copy(color = TextWhite),
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (confirmPasswordVisible) "Sembunyikan Kata Sandi" else "Tampilkan Kata Sandi",
                                    tint = TextGray
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = DarkCardBorder.copy(alpha = 0.5f),
                            cursorColor = OrangePrimary
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    HorizontalDivider(color = DarkCardBorder.copy(alpha = 0.3f))

                    // Tombol Hapus Akun Permanen
                    TextButton(
                        onClick = {
                            showChangePasswordDialog = false
                            showDeleteAccountDialog = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Outlined.DeleteForever, null, tint = Color.Red, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Hapus Akun Permanen", color = Color.Red, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (currentPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
                            Toast.makeText(context, "Semua kolom kata sandi wajib diisi", Toast.LENGTH_SHORT).show()
                        } else if (newPassword != confirmPassword) {
                            Toast.makeText(context, "Konfirmasi kata sandi tidak cocok", Toast.LENGTH_SHORT).show()
                        } else if (newPassword.length < 6) {
                            Toast.makeText(context, "Kata sandi baru minimal 6 karakter", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.changePassword(
                                currentPassword = currentPassword,
                                newPassword = newPassword,
                                onSuccess = {
                                    currentPassword = ""
                                    newPassword = ""
                                    confirmPassword = ""
                                    showChangePasswordDialog = false
                                    Toast.makeText(context, "Kata sandi berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                                },
                                onFailure = { error ->
                                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                                }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Text("Simpan", color = TextWhite)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        currentPassword = ""
                        newPassword = ""
                        confirmPassword = ""
                        showChangePasswordDialog = false
                    }
                ) {
                    Text("Batal", color = TextGray)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // 3. Pengaturan Notifikasi Dialog
    if (showNotificationSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationSettingsDialog = false },
            title = {
                Text(
                    "Pengaturan Notifikasi",
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text("Status Penyewaan", color = TextWhite, fontWeight = FontWeight.Medium)
                            Text("Notifikasi perubahan status order & pengembalian", color = TextGray, fontSize = 11.sp)
                        }
                        Switch(
                            checked = notifStatusSewa,
                            onCheckedChange = { notifStatusSewa = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = OrangePrimary,
                                checkedTrackColor = OrangeSubtle
                            )
                        )
                    }
                    HorizontalDivider(color = DarkCardBorder.copy(alpha = 0.3f))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text("Promo & Penawaran", color = TextWhite, fontWeight = FontWeight.Medium)
                            Text("Info diskon sewa kamera dan penawaran khusus", color = TextGray, fontSize = 11.sp)
                        }
                        Switch(
                            checked = notifPromo,
                            onCheckedChange = { notifPromo = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = OrangePrimary,
                                checkedTrackColor = OrangeSubtle
                            )
                        )
                    }
                    HorizontalDivider(color = DarkCardBorder.copy(alpha = 0.3f))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text("Laporan & Pembaruan Email", color = TextWhite, fontWeight = FontWeight.Medium)
                            Text("Tips fotografi mingguan dan ketersediaan stok baru", color = TextGray, fontSize = 11.sp)
                        }
                        Switch(
                            checked = notifEmail,
                            onCheckedChange = { notifEmail = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = OrangePrimary,
                                checkedTrackColor = OrangeSubtle
                            )
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showNotificationSettingsDialog = false
                        Toast.makeText(context, "Preferensi notifikasi disimpan!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Text("Selesai", color = TextWhite)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // 4. Metode Pembayaran Dialog
    if (showPaymentMethodsDialog) {
        AlertDialog(
            onDismissRequest = { showPaymentMethodsDialog = false },
            title = {
                Text(
                    "Metode Pembayaran",
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Pilih atau Kelola Metode Pembayaran Utama Anda:", color = TextGray, fontSize = 13.sp)
                    
                    val methods = listOf(
                        "Visa (.... 4321)",
                        "GoPay (0812****8899)",
                        "OVO (0812****8899)",
                        "ShopeePay (0812****8899)"
                    )
                    
                    methods.forEach { method ->
                        val isSelected = selectedPaymentMethod == method
                        Surface(
                            onClick = { selectedPaymentMethod = method },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) OrangeSubtle.copy(alpha = 0.2f) else DarkSurface,
                            border = BorderStroke(1.dp, if (isSelected) OrangePrimary else DarkCardBorder.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                Modifier.fillMaxWidth().padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(method, color = TextWhite, fontWeight = FontWeight.Medium)
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedPaymentMethod = method },
                                    colors = RadioButtonDefaults.colors(selectedColor = OrangePrimary)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    Button(
                        onClick = {
                            Toast.makeText(context, "Tambah metode pembayaran baru dalam pengembangan", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("+ Tambah Kartu / E-Wallet Baru", color = TextWhite)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPaymentMethodsDialog = false }) {
                    Text("Tutup", color = TextGray)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // 5. Pusat Bantuan & Laporkan Masalah Dialog
    if (showHelpCenterDialog) {
        AlertDialog(
            onDismissRequest = { showHelpCenterDialog = false },
            title = {
                Text(
                    "Pusat Bantuan & CS",
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text("Pertanyaan Sering Diajukan (FAQ):", color = OrangePrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column {
                            Text("Q: Bagaimana cara menyewa kamera?", color = TextWhite, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("A: Cari kamera yang Anda inginkan di halaman utama, klik sewa, tentukan tanggal, lalu lakukan pembayaran online.", color = TextGray, fontSize = 12.sp)
                        }
                        HorizontalDivider(color = DarkCardBorder.copy(alpha = 0.3f))
                        Column {
                            Text("Q: Apakah ada jaminan yang harus diserahkan?", color = TextWhite, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("A: Anda hanya perlu mengunggah foto KTP asli pada saat registrasi akun untuk proses verifikasi.", color = TextGray, fontSize = 12.sp)
                        }
                        HorizontalDivider(color = DarkCardBorder.copy(alpha = 0.3f))
                        Column {
                            Text("Q: Bagaimana jika kamera mengalami kendala/kerusakan?", color = TextWhite, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Text("A: Segera hubungi customer support via WhatsApp. Kerusakan fisik karena kelalaian akan dikenakan biaya sesuai tingkat kerusakan.", color = TextGray, fontSize = 12.sp)
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Text("Masih butuh bantuan? Hubungi CS kami:", color = TextWhite, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text("📱 +62 895-0576-4951 (Senin-Sabtu 08:00-21:00)", color = TextGray, fontSize = 12.sp)
                    
                    Spacer(Modifier.height(4.dp))
                    Button(
                        onClick = {
                            try {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://wa.me/6289505764951")
                                )
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Gagal membuka WhatsApp. Nomor CS: +62 895-0576-4951", Toast.LENGTH_LONG).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenActive),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Hubungi WhatsApp CS", color = TextWhite)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showHelpCenterDialog = false }) {
                    Text("Tutup", color = TextGray)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // 6. Kebijakan Privasi & Ketentuan Layanan Dialog
    if (showPrivacyPolicyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyPolicyDialog = false },
            title = {
                Text(
                    "Kebijakan & Ketentuan",
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .heightIn(max = 300.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("1. Pengumpulan Informasi", fontWeight = FontWeight.Bold, color = OrangePrimary, fontSize = 14.sp)
                    Text("Kami mengumpulkan data pribadi berupa nama lengkap, alamat email, nomor telepon, biodata, serta foto identitas KTP yang diunggah saat pendaftaran untuk melakukan verifikasi identitas penyewa kamera demi keamanan bersama.", color = TextGray, fontSize = 12.sp)

                    Text("2. Tanggung Jawab Penyewa", fontWeight = FontWeight.Bold, color = OrangePrimary, fontSize = 14.sp)
                    Text("Penyewa bertanggung jawab penuh atas keutuhan dan fungsionalitas kamera yang disewa sejak waktu pengambilan hingga pengembalian. Segala bentuk kelalaian yang mengakibatkan kerusakan fisik atau kehilangan akan dibebankan kepada penyewa.", color = TextGray, fontSize = 12.sp)

                    Text("3. Pembayaran dan Denda", fontWeight = FontWeight.Bold, color = OrangePrimary, fontSize = 14.sp)
                    Text("Pembayaran dilakukan secara penuh sebelum pengambilan kamera. Pengembalian yang melebihi batas waktu yang telah ditentukan (terlambat) akan dikenakan denda harian sesuai tarif kamera yang disewa.", color = TextGray, fontSize = 12.sp)

                    Text("4. Keamanan Informasi", fontWeight = FontWeight.Bold, color = OrangePrimary, fontSize = 14.sp)
                    Text("Data identitas dan transaksi Anda disimpan dengan aman dan terenkripsi. Kami berkomitmen untuk tidak pernah menyebarluaskan data Anda kepada pihak ketiga tanpa izin resmi dari pengguna.", color = TextGray, fontSize = 12.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPrivacyPolicyDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Text("Saya Mengerti", color = TextWhite)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Delete Account Dialog
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = {
                deleteAccountPassword = ""
                showDeleteAccountDialog = false
            },
            title = {
                Text(
                    "Hapus Akun Permanen",
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Tindakan ini tidak bisa dibatalkan. Seluruh data profil, keranjang, dan riwayat transaksi Anda akan terhapus secara permanen.",
                        color = TextGray,
                        fontSize = 13.sp
                    )

                    OutlinedTextField(
                        value = deleteAccountPassword,
                        onValueChange = { deleteAccountPassword = it },
                        label = { Text("Masukkan Kata Sandi Anda", color = TextGray) },
                        textStyle = LocalTextStyle.current.copy(color = TextWhite),
                        visualTransformation = if (deleteAccountPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { deleteAccountPasswordVisible = !deleteAccountPasswordVisible }) {
                                Icon(
                                    imageVector = if (deleteAccountPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (deleteAccountPasswordVisible) "Sembunyikan Kata Sandi" else "Tampilkan Kata Sandi",
                                    tint = TextGray
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Red,
                            unfocusedBorderColor = DarkCardBorder.copy(alpha = 0.5f),
                            cursorColor = Color.Red
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (deleteAccountPassword.isBlank()) {
                            Toast.makeText(context, "Silakan masukkan kata sandi Anda untuk konfirmasi", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.deleteAccount(
                                password = deleteAccountPassword,
                                onSuccess = {
                                    deleteAccountPassword = ""
                                    showDeleteAccountDialog = false
                                    Toast.makeText(context, "Akun Anda berhasil dihapus.", Toast.LENGTH_LONG).show()
                                    onLogout()
                                },
                                onFailure = { errorMsg ->
                                    Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                                }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Hapus Akun Permanen", color = TextWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    deleteAccountPassword = ""
                    showDeleteAccountDialog = false
                }) {
                    Text("Batal", color = TextGray)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Scaffold(
        containerColor = DarkNavy,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Profil Saya",
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
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(24.dp))

            // Profile avatar
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.clickable { imagePickerLauncher.launch("image/*") }
            ) {
                Box(
                    Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(DarkSurface, DarkCard)
                            )
                        )
                        .border(2.dp, DarkCardBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        // Priority 1: Local content:// URI — instant preview after pick
                        avatarUri != null -> {
                            AsyncImage(
                                model = avatarUri,
                                contentDescription = "Foto Profil",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        // Priority 2: Decoded bitmap from base64 in Firestore — persistence
                        avatarBitmap != null -> {
                            Image(
                                bitmap = avatarBitmap!!.asImageBitmap(),
                                contentDescription = "Foto Profil",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        // Priority 3: Default icon
                        else -> {
                            Icon(
                                Icons.Outlined.Person,
                                "Profile",
                                Modifier.size(48.dp),
                                tint = TextGray
                            )
                        }
                    }
                }
                // Camera icon overlay
                Box(
                    Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(OrangePrimary)
                        .border(2.dp, DarkNavy, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.CameraAlt, null,
                        Modifier.size(14.dp),
                        tint = TextWhite
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Name (Inline Editing)
            if (isEditingName) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 4.dp)
                ) {
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        textStyle = LocalTextStyle.current.copy(
                            color = TextWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangePrimary,
                            unfocusedBorderColor = DarkCardBorder.copy(alpha = 0.5f),
                            cursorColor = OrangePrimary,
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Box(
                        Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.Red.copy(alpha = 0.2f))
                            .clickable {
                                editedName = userProfile?.name ?: "Fotografer"
                                isEditingName = false
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Close,
                            contentDescription = "Batal",
                            Modifier.size(18.dp),
                            tint = Color.Red
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Box(
                        Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(GreenActive.copy(alpha = 0.2f))
                            .clickable {
                                if (editedName.isNotBlank()) {
                                    val currentProfile = userProfile ?: UserProfile()
                                    val updatedProfile = currentProfile.copy(name = editedName)
                                    viewModel.updateUserProfile(
                                        updatedProfile,
                                        onSuccess = {
                                            isEditingName = false
                                            Toast.makeText(context, "Nama berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                                        },
                                        onFailure = { error ->
                                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                } else {
                                    Toast.makeText(context, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Check,
                            contentDescription = "Simpan",
                            Modifier.size(18.dp),
                            tint = GreenActive
                        )
                    }
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .clickable { isEditingName = true }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        userProfile?.name ?: "Fotografer",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Spacer(Modifier.width(8.dp))
                    Box(
                        Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(OrangePrimary)
                            .border(1.5.dp, DarkNavy, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Edit,
                            contentDescription = "Edit Nama",
                            Modifier.size(12.dp),
                            tint = TextWhite
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Premium badge
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = BluePremium.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, BluePremium.copy(alpha = 0.3f))
            ) {
                Row(
                    Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Outlined.Verified, null,
                        Modifier.size(16.dp),
                        tint = BluePremium
                    )
                    Text(
                        "PREMIUM MEMBER",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BluePremium,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Stats cards
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Active rentals stat
                Surface(
                    Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = DarkSurface,
                    border = BorderStroke(1.dp, DarkCardBorder)
                ) {
                    Column(
                        Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val activeCount = orders.filter { it.status == OrderStatus.ACTIVE }.size
                        Text(
                            activeCount.toString(),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimary
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Sewa Aktif",
                            fontSize = 12.sp,
                            color = TextGray
                        )
                    }
                }

                // Total rentals stat (dynamic from order history)
                Surface(
                    Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    color = DarkSurface,
                    border = BorderStroke(1.dp, DarkCardBorder)
                ) {
                    Column(
                        Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            orders.size.toString(),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Total Sewa",
                            fontSize = 12.sp,
                            color = TextGray
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Menu items
            ProfileMenuItem(
                icon = Icons.Outlined.ShoppingCart,
                title = "Keranjang Saya",
                subtitle = "Periksa barang di keranjang belanja Anda",
                onClick = { onNavigateToCart() }
            )
            ProfileMenuItem(
                icon = Icons.Outlined.History,
                title = "Riwayat Sewa",
                subtitle = "Lihat riwayat dan transaksi aktif Anda",
                onClick = { onNavigateToHistory() }
            )
            ProfileMenuItem(
                icon = Icons.Outlined.Settings,
                title = "Pengaturan",
                subtitle = "Akun dan keamanan",
                onClick = { showSettingsDialog = true }
            )
            ProfileMenuItem(
                icon = Icons.AutoMirrored.Outlined.HelpOutline,
                title = "Pusat Bantuan",
                subtitle = "FAQ dan dukungan langsung",
                onClick = { onNavigateToHelpCenter() }
            )

            Spacer(Modifier.height(16.dp))

            // Logout button
            Surface(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(14.dp),
                color = DarkSurface,
                border = BorderStroke(1.dp, DarkCardBorder)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Logout, null,
                        Modifier.size(20.dp),
                        tint = OrangePrimary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Logout",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OrangePrimary
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Upgrade banner
            Surface(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.Transparent
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF1A2740),
                                    Color(0xFF0F2030)
                                )
                            )
                        )
                        .border(1.dp, DarkCardBorder.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            "Upgrade your plan",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Get 20% off on all cinema\nrigs this month.",
                            fontSize = 13.sp,
                            color = TextGray,
                            lineHeight = 20.sp
                        )
                        Spacer(Modifier.height(12.dp))
                        Surface(
                            onClick = {
                                Toast.makeText(context, "Fitur Premium Plan dalam tahap pengembangan (Demo UAS)", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = OrangePrimary
                        ) {
                            Text(
                                "VIEW OFFERS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        color = DarkSurface,
        border = BorderStroke(1.dp, DarkCardBorder.copy(alpha = 0.5f))
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(OrangeSubtle),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon, null,
                    Modifier.size(22.dp),
                    tint = OrangePrimary
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextWhite
                )
                Text(
                    subtitle,
                    fontSize = 12.sp,
                    color = TextGray
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                null,
                Modifier.size(20.dp),
                tint = TextGray
            )
        }
    }
}
