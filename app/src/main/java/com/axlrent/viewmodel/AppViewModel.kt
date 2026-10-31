package com.axlrent.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.axlrent.data.Camera
import com.axlrent.data.Order
import com.axlrent.data.OrderStatus
import com.axlrent.data.SampleData
import com.axlrent.data.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.UUID

class AppViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Authentication States
    private val _currentUserProfile = MutableStateFlow<UserProfile?>(null)
    val currentUserProfile: StateFlow<UserProfile?> = _currentUserProfile.asStateFlow()

    private val _isUserLoggedIn = MutableStateFlow<Boolean?>(null) // null = loading/unknown, true = logged in, false = not logged in
    val isUserLoggedIn: StateFlow<Boolean?> = _isUserLoggedIn.asStateFlow()

    // Admin Role State
    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    private val _authLoading = MutableStateFlow(false)
    val authLoading: StateFlow<Boolean> = _authLoading.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    // KTP Upload State
    private val _uploadingKtp = MutableStateFlow(false)
    val uploadingKtp: StateFlow<Boolean> = _uploadingKtp.asStateFlow()

    // Cameras State
    private val _cameras = MutableStateFlow<List<Camera>>(emptyList())
    val cameras: StateFlow<List<Camera>> = _cameras.asStateFlow()

    private val _camerasLoading = MutableStateFlow(false)
    val camerasLoading: StateFlow<Boolean> = _camerasLoading.asStateFlow()

    // Orders/History State (user view)
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _ordersLoading = MutableStateFlow(false)
    val ordersLoading: StateFlow<Boolean> = _ordersLoading.asStateFlow()

    // Admin: All Orders across all users
    private val _allOrders = MutableStateFlow<List<Order>>(emptyList())
    val allOrders: StateFlow<List<Order>> = _allOrders.asStateFlow()

    // Booking Temporary State
    private val _bookingState = MutableStateFlow<Order?>(null)
    val bookingState: StateFlow<Order?> = _bookingState.asStateFlow()

    // Profile Avatar State — local URI for instant display
    val profileImageUri = MutableStateFlow<Uri?>(null)
    private val _profileBitmap = MutableStateFlow<Bitmap?>(null)
    val profileBitmap: StateFlow<Bitmap?> = _profileBitmap.asStateFlow()

    // Selected rental date states for booking flow
    val selectedStartDate = MutableStateFlow("20 Nov")
    val selectedEndDate = MutableStateFlow("23 Nov, 2026")
    val selectedDurationDays = MutableStateFlow(3L)

    init {
        // Check for existing session
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            _isUserLoggedIn.value = true
            fetchUserProfile(firebaseUser.uid)
            // If admin email, restore admin state and load all orders
            if (firebaseUser.email == ADMIN_EMAIL) {
                _isAdmin.value = true
                loadAllOrders()
            } else {
                loadOrders(firebaseUser.uid)
            }
        } else {
            _isUserLoggedIn.value = false
        }
        // Seed database if empty and load camera list
        seedCamerasIfEmpty()
    }

    companion object {
        const val ADMIN_EMAIL = "admin@gmail.com"
    }

    fun clearAuthError() {
        _authError.value = null
    }

    // --- Authentication Operations ---

    fun loginUser(email: String, password: String, onSuccess: (isAdmin: Boolean) -> Unit) {
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        if (trimmedEmail.isBlank() || trimmedPassword.isBlank()) {
            _authError.value = "Email dan password tidak boleh kosong"
            return
        }

        _authLoading.value = true
        _authError.value = null

        auth.signInWithEmailAndPassword(trimmedEmail, trimmedPassword)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid ?: ""
                val isAdminLogin = trimmedEmail == ADMIN_EMAIL

                if (isAdminLogin) {
                    // Set admin role in Firestore and local state
                    _isAdmin.value = true
                    val adminProfile = UserProfile(
                        uid = uid,
                        name = "Admin Axl Rent",
                        email = trimmedEmail,
                        phone = "",
                        role = "admin"
                    )
                    firestore.collection("users").document(uid)
                        .set(adminProfile)
                        .addOnCompleteListener {
                            _currentUserProfile.value = adminProfile
                        }
                    loadAllOrders()
                } else {
                    _isAdmin.value = false
                    fetchUserProfile(uid)
                    loadOrders(uid)
                }

                _isUserLoggedIn.value = true
                _authLoading.value = false
                onSuccess(isAdminLogin)
            }
            .addOnFailureListener { exception ->
                // Jika akun admin belum terdaftar, buat otomatis lalu login ulang
                val isAdminLogin = trimmedEmail == ADMIN_EMAIL
                val isUserNotFound = exception is com.google.firebase.auth.FirebaseAuthInvalidUserException

                if (isAdminLogin && isUserNotFound) {
                    // Auto-register akun admin ke Firebase Auth
                    auth.createUserWithEmailAndPassword(trimmedEmail, trimmedPassword)
                        .addOnSuccessListener { createResult ->
                            val uid = createResult.user?.uid ?: ""
                            val adminProfile = UserProfile(
                                uid = uid,
                                name = "Admin Axl Rent",
                                email = trimmedEmail,
                                phone = "",
                                role = "admin"
                            )
                            firestore.collection("users").document(uid).set(adminProfile)
                                .addOnCompleteListener {
                                    _currentUserProfile.value = adminProfile
                                }
                            _isAdmin.value = true
                            _isUserLoggedIn.value = true
                            _authLoading.value = false
                            loadAllOrders()
                            onSuccess(true)
                        }
                        .addOnFailureListener { createError ->
                            _authLoading.value = false
                            _authError.value = "Gagal membuat akun admin: ${createError.localizedMessage}"
                        }
                } else {
                    _authLoading.value = false
                    val errorMessage = when (exception) {
                        is com.google.firebase.auth.FirebaseAuthInvalidUserException -> "Akun tidak ditemukan. Silakan daftar terlebih dahulu."
                        is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> "Email atau password salah."
                        else -> exception.localizedMessage ?: "Login gagal. Periksa koneksi internet Anda."
                    }
                    _authError.value = errorMessage
                }
            }
    }

    fun loginWithGoogle(
        idToken: String,
        displayName: String?,
        email: String?,
        onSuccess: () -> Unit
    ) {
        _authLoading.value = true
        _authError.value = null

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                val uid = authResult.user?.uid ?: ""
                val isNewUser = authResult.additionalUserInfo?.isNewUser == true

                if (isNewUser) {
                    // Create Firestore profile for first-time Google sign-in
                    val userProfile = UserProfile(
                        uid = uid,
                        name = displayName ?: "Fotografer",
                        email = email ?: "",
                        phone = "",
                        role = "User"
                    )
                    firestore.collection("users").document(uid).set(userProfile)
                        .addOnSuccessListener {
                            _currentUserProfile.value = userProfile
                            _isUserLoggedIn.value = true
                            _authLoading.value = false
                            onSuccess()
                        }
                        .addOnFailureListener {
                            fetchUserProfile(uid)
                            loadOrders(uid)
                            _isUserLoggedIn.value = true
                            _authLoading.value = false
                            onSuccess()
                        }
                } else {
                    fetchUserProfile(uid)
                    loadOrders(uid)
                    _isUserLoggedIn.value = true
                    _authLoading.value = false
                    onSuccess()
                }
            }
            .addOnFailureListener { exception ->
                _authLoading.value = false
                _authError.value = "Login Google gagal: ${exception.localizedMessage}"
            }
    }

    fun registerUser(
        context: Context,
        email: String,
        password: String,
        name: String,
        phone: String,
        ktpUri: Uri?,
        onSuccess: () -> Unit
    ) {
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()
        val trimmedName = name.trim()
        val trimmedPhone = phone.trim()

        if (trimmedEmail.isBlank() || trimmedPassword.isBlank() || trimmedName.isBlank() || trimmedPhone.isBlank()) {
            _authError.value = "Semua kolom input wajib diisi"
            return
        }

        if (ktpUri == null) {
            _authError.value = "Harap unggah foto KTP Anda terlebih dahulu"
            return
        }

        _authLoading.value = true
        _authError.value = null

        viewModelScope.launch {
            // Convert and compress image in background thread to avoid UI freeze
            val base64Ktp = withContext(Dispatchers.IO) {
                convertUriToBase64AndCompress(context, ktpUri)
            }

            if (base64Ktp == null) {
                _authLoading.value = false
                _authError.value = "Gagal memproses foto KTP. Pastikan file gambar benar."
                return@launch
            }

            // Create user in Firebase Auth
            auth.createUserWithEmailAndPassword(trimmedEmail, trimmedPassword)
                .addOnSuccessListener { authResult ->
                    val uid = authResult.user?.uid ?: ""
                    val userProfile = UserProfile(
                        uid = uid,
                        name = trimmedName,
                        email = trimmedEmail,
                        phone = trimmedPhone,
                        ktpUrl = "data:image/jpeg;base64,$base64Ktp",
                        role = "User"
                    )

                    // Save profile details to Firestore
                    firestore.collection("users").document(uid).set(userProfile)
                        .addOnSuccessListener {
                            auth.signOut()
                            _currentUserProfile.value = null
                            _isUserLoggedIn.value = false
                            _authLoading.value = false
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            _authLoading.value = false
                            _authError.value = "Gagal menyimpan biodata: ${e.localizedMessage}"
                        }
                }
                .addOnFailureListener { exception ->
                    _authLoading.value = false
                    val errorMessage = when (exception) {
                        is com.google.firebase.auth.FirebaseAuthUserCollisionException -> "Email ini sudah terdaftar. Silakan gunakan email lain atau masuk."
                        is com.google.firebase.auth.FirebaseAuthWeakPasswordException -> "Kata sandi terlalu lemah. Minimal 6 karakter."
                        is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> "Format email tidak valid."
                        else -> exception.localizedMessage ?: "Registrasi gagal."
                    }
                    _authError.value = errorMessage
                }
        }
    }

    fun logoutUser(onSuccess: () -> Unit) {
        auth.signOut()
        _currentUserProfile.value = null
        _isUserLoggedIn.value = false
        _isAdmin.value = false
        _authError.value = null
        _orders.value = emptyList()
        _allOrders.value = emptyList()
        onSuccess()
    }

    private fun fetchUserProfile(uid: String) {
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val profile = document.toObject(UserProfile::class.java)
                    _currentUserProfile.value = profile
                    // Restore admin state from Firestore role field
                    if (profile?.role == "admin" || profile?.email == ADMIN_EMAIL) {
                        _isAdmin.value = true
                    }
                }
            }
    }

    // --- Firebase Storage Operations ---

    private fun convertUriToBase64AndCompress(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (originalBitmap == null) return null

            // Resize image to max 800px width/height to stay safely under Firestore's 1MB limit
            val maxDimension = 800
            val width = originalBitmap.width
            val height = originalBitmap.height
            val resizedBitmap = if (width > maxDimension || height > maxDimension) {
                val srcAspect = width.toFloat() / height.toFloat()
                val dstWidth: Int
                val dstHeight: Int
                if (srcAspect > 1) {
                    dstWidth = maxDimension
                    dstHeight = (maxDimension / srcAspect).toInt()
                } else {
                    dstHeight = maxDimension
                    dstWidth = (maxDimension * srcAspect).toInt()
                }
                Bitmap.createScaledBitmap(originalBitmap, dstWidth, dstHeight, true)
            } else {
                originalBitmap
            }

            val outputStream = ByteArrayOutputStream()
            // Compress to JPEG at 60% quality (great trade-off, output is usually ~40-80KB)
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
            val bytes = outputStream.toByteArray()
            Base64.encodeToString(bytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            null
        }
    }

    // --- Firestore Database Operations ---

    fun seedCamerasIfEmpty() {
        _camerasLoading.value = true
        firestore.collection("cameras").get()
            .addOnSuccessListener { snapshot ->
                val needsReseed = snapshot.isEmpty ||
                        snapshot.size() != SampleData.cameras.size ||
                        snapshot.documents.any { doc -> doc.getString("name").isNullOrEmpty() }

                if (needsReseed) {
                    val batch = firestore.batch()
                    // Delete old documents first to prevent stale documents
                    for (doc in snapshot.documents) {
                        batch.delete(doc.reference)
                    }
                    // Write new documents
                    for (camera in SampleData.cameras) {
                        val docRef = firestore.collection("cameras").document(camera.id)
                        batch.set(docRef, camera)
                    }
                    batch.commit()
                        .addOnSuccessListener {
                            loadCameras()
                        }
                        .addOnFailureListener {
                            _camerasLoading.value = false
                        }
                } else {
                    loadCameras()
                }
            }
            .addOnFailureListener {
                _camerasLoading.value = false
                // Fallback to local sample data if offline/firebase not configured yet
                _cameras.value = SampleData.cameras
            }
    }

    fun loadCameras() {
        _camerasLoading.value = true
        firestore.collection("cameras").get()
            .addOnSuccessListener { snapshot ->
                val cameraList = mutableListOf<Camera>()
                for (doc in snapshot.documents) {
                    doc.toObject(Camera::class.java)?.let { cameraList.add(it) }
                }
                _cameras.value = cameraList
                _camerasLoading.value = false
            }
            .addOnFailureListener {
                _camerasLoading.value = false
            }
    }

    fun loadOrders(userId: String) {
        _ordersLoading.value = true
        firestore.collection("orders")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val orderList = mutableListOf<Order>()
                for (doc in snapshot.documents) {
                    doc.toObject(Order::class.java)?.let { orderList.add(it) }
                }
                _orders.value = orderList
                _ordersLoading.value = false
            }
            .addOnFailureListener {
                _ordersLoading.value = false
            }
    }

    fun createTemporaryBooking(camera: Camera, startDate: String, endDate: String, totalPrice: Long) {
        val currentUid = auth.currentUser?.uid ?: ""
        val generatedId = "AXL-${UUID.randomUUID().toString().take(4).uppercase()}"
        _bookingState.value = Order(
            id = generatedId,
            userId = currentUid,
            camera = camera,
            startDate = startDate,
            endDate = endDate,
            status = OrderStatus.WAITING,
            totalPrice = totalPrice
        )
    }

    fun removeCartItemById(cameraId: String) {
        if (cameraId.isBlank()) return

        // 1. Only remove the specific paid item from local state Flow (set isFavorite = false for this ID only)
        val updatedCameras = _cameras.value.map { camera ->
            if (camera.id == cameraId) {
                camera.copy(isFavorite = false)
            } else {
                camera
            }
        }
        _cameras.value = updatedCameras

        // 2. Only update Firestore for this specific camera document
        firestore.collection("cameras").document(cameraId)
            .update("isFavorite", false)
            .addOnFailureListener { /* Ignore error */ }

        // 3. Only delete specific document for this item ID in user cart subcollections if present
        val uid = auth.currentUser?.uid
        if (uid != null) {
            firestore.collection("users").document(uid).collection("cart").document(cameraId).delete()
            firestore.collection("carts").document(uid).collection("items").document(cameraId).delete()
        }
    }

    fun confirmPayment(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val booking = _bookingState.value
        if (booking == null) {
            onFailure("Data pemesanan tidak ditemukan")
            return
        }

        // Change status to ACTIVE when paid successfully
        val finalOrder = booking.copy(status = OrderStatus.ACTIVE)
        
        // Optimistically add to local state
        _orders.value = listOf(finalOrder) + _orders.value.filter { it.id != finalOrder.id }

        // Remove ONLY the item that was checked out/paid for, keeping other cart items intact
        if (finalOrder.camera.id.isNotBlank()) {
            removeCartItemById(finalOrder.camera.id)
        }

        firestore.collection("orders").document(finalOrder.id).set(finalOrder)
            .addOnSuccessListener {
                loadOrders(finalOrder.userId)
                _bookingState.value = null
                onSuccess()
            }
            .addOnFailureListener { e ->
                _bookingState.value = null
                onSuccess()
            }
    }

    fun toggleFavorite(camera: Camera) {
        val updatedCameras = _cameras.value.map {
            if (it.id == camera.id) it.copy(isFavorite = !it.isFavorite) else it
        }
        _cameras.value = updatedCameras

        firestore.collection("cameras").document(camera.id)
            .update("isFavorite", !camera.isFavorite)
    }

    // Controls which tab is default when navigating to History
    val initialHistoryTab = MutableStateFlow(0)

    fun approveOrder(orderId: String) {
        _orders.value = _orders.value.map { order ->
            if (order.id == orderId) order.copy(status = OrderStatus.ACTIVE) else order
        }
        firestore.collection("orders").document(orderId)
            .update("status", "ACTIVE")
            .addOnFailureListener { /* Ignore; local state already updated */ }
    }

    fun updateAvatar(context: Context, uri: Uri, onSuccess: () -> Unit) {
        // Immediately set the local URI for instant visual feedback in the UI
        profileImageUri.value = uri

        val contentResolver = context.contentResolver
        val bytes = try {
            contentResolver.openInputStream(uri)?.use { it.readBytes() }
        } catch (e: Exception) {
            null
        }

        if (bytes == null) {
            android.widget.Toast.makeText(context, "Gagal membaca file gambar", android.widget.Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    val originalBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return@withContext null
                    val maxDimension = 800
                    val width = originalBitmap.width
                    val height = originalBitmap.height
                    val resizedBitmap = if (width > maxDimension || height > maxDimension) {
                        val srcAspect = width.toFloat() / height.toFloat()
                        val dstWidth: Int
                        val dstHeight: Int
                        if (srcAspect > 1) {
                            dstWidth = maxDimension
                            dstHeight = (maxDimension / srcAspect).toInt()
                        } else {
                            dstHeight = maxDimension
                            dstWidth = (maxDimension * srcAspect).toInt()
                        }
                        Bitmap.createScaledBitmap(originalBitmap, dstWidth, dstHeight, true)
                    } else {
                        originalBitmap
                    }

                    val outputStream = ByteArrayOutputStream()
                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
                    val compressedBytes = outputStream.toByteArray()
                    val base64 = Base64.encodeToString(compressedBytes, Base64.NO_WRAP)
                    Pair(base64, resizedBitmap)
                } catch (e: Exception) {
                    null
                }
            }

            if (result != null) {
                val (base64Avatar, bitmap) = result
                _profileBitmap.value = bitmap

                val currentProfile = _currentUserProfile.value ?: return@launch
                val updatedProfile = currentProfile.copy(avatarUrl = "data:image/jpeg;base64,$base64Avatar")

                firestore.collection("users").document(currentProfile.uid).set(updatedProfile)
                    .addOnSuccessListener {
                        _currentUserProfile.value = updatedProfile
                        onSuccess()
                    }
                    .addOnFailureListener {
                        android.widget.Toast.makeText(context, "Gagal mengunggah foto ke database", android.widget.Toast.LENGTH_SHORT).show()
                    }
            } else {
                android.widget.Toast.makeText(context, "Gagal memproses gambar", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Decode base64 avatarUrl to Bitmap for display when restoring from Firestore
    fun decodeAvatarFromProfile() {
        val avatarUrl = _currentUserProfile.value?.avatarUrl ?: return
        if (avatarUrl.isBlank() || _profileBitmap.value != null) return

        viewModelScope.launch {
            val bitmap = withContext(Dispatchers.IO) {
                try {
                    val base64Data = avatarUrl.substringAfter("base64,")
                    val decodedBytes = Base64.decode(base64Data, Base64.NO_WRAP)
                    BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                } catch (e: Exception) {
                    null
                }
            }
            if (bitmap != null) {
                _profileBitmap.value = bitmap
            }
        }
    }

    fun changePassword(currentPassword: String, newPassword: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val user = auth.currentUser
        if (user != null && !user.email.isNullOrEmpty()) {
            val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(user.email!!, currentPassword)
            user.reauthenticate(credential)
                .addOnSuccessListener {
                    user.updatePassword(newPassword)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { e -> onFailure(e.localizedMessage ?: "Gagal mengubah kata sandi") }
                }
                .addOnFailureListener { e ->
                    onFailure("Kata sandi saat ini salah: ${e.localizedMessage}")
                }
        } else if (user != null) {
            user.updatePassword(newPassword)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { e -> onFailure(e.localizedMessage ?: "Gagal mengubah kata sandi") }
        } else {
            onFailure("User tidak terautentikasi")
        }
    }

    fun updateUserProfile(updatedProfile: UserProfile, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val user = auth.currentUser
        if (user == null) {
            onFailure("User tidak terautentikasi")
            return
        }
        val uid = user.uid
        val finalProfile = updatedProfile.copy(uid = uid)

        viewModelScope.launch {
            if (finalProfile.email != user.email && finalProfile.email.isNotBlank()) {
                user.updateEmail(finalProfile.email)
                    .addOnCompleteListener { task ->
                        firestore.collection("users").document(uid).set(finalProfile)
                            .addOnSuccessListener {
                                _currentUserProfile.value = finalProfile
                                onSuccess()
                            }
                            .addOnFailureListener { e ->
                                onFailure(e.localizedMessage ?: "Gagal memperbarui profil di database")
                            }
                    }
            } else {
                firestore.collection("users").document(uid).set(finalProfile)
                    .addOnSuccessListener {
                        _currentUserProfile.value = finalProfile
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        onFailure(e.localizedMessage ?: "Gagal memperbarui profil di database")
                    }
            }
        }
    }

    fun deleteAccount(password: String? = null, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val user = auth.currentUser
        if (user != null) {
            val uid = user.uid

            val performDeletion = {
                // Delete user document in Firestore first
                firestore.collection("users").document(uid).delete()
                    .addOnCompleteListener {
                        // Proceed to delete Auth account
                        user.delete()
                            .addOnSuccessListener {
                                _currentUserProfile.value = null
                                _isUserLoggedIn.value = false
                                _orders.value = emptyList()
                                onSuccess()
                            }
                            .addOnFailureListener { e ->
                                onFailure(e.localizedMessage ?: "Gagal menghapus akun autentikasi. Sesi mungkin telah berakhir, silakan login ulang.")
                            }
                    }
            }

            if (!password.isNullOrBlank() && !user.email.isNullOrEmpty()) {
                val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(user.email!!, password)
                user.reauthenticate(credential)
                    .addOnSuccessListener {
                        performDeletion()
                    }
                    .addOnFailureListener { e ->
                        onFailure("Kata sandi verifikasi salah: ${e.localizedMessage}")
                    }
            } else {
                performDeletion()
            }
        } else {
            onFailure("User tidak terautentikasi")
        }
    }

    // --- Admin Operations ---

    /** Load ALL orders across all users (admin view) */
    fun loadAllOrders() {
        _ordersLoading.value = true
        firestore.collection("orders")
            .get()
            .addOnSuccessListener { snapshot ->
                val orderList = mutableListOf<Order>()
                for (doc in snapshot.documents) {
                    doc.toObject(Order::class.java)?.let { orderList.add(it) }
                }
                _allOrders.value = orderList
                _ordersLoading.value = false
            }
            .addOnFailureListener {
                _ordersLoading.value = false
            }
    }

    /** Admin: Konfirmasi pembayaran → ubah status WAITING → ACTIVE */
    fun adminConfirmOrder(orderId: String) {
        _allOrders.value = _allOrders.value.map { order ->
            if (order.id == orderId) order.copy(status = OrderStatus.ACTIVE) else order
        }
        firestore.collection("orders").document(orderId)
            .update("status", "ACTIVE")
            .addOnFailureListener { loadAllOrders() }
    }

    /** Admin: Selesaikan sewa → ubah status ACTIVE/LATE → COMPLETED */
    fun adminCompleteOrder(orderId: String) {
        _allOrders.value = _allOrders.value.map { order ->
            if (order.id == orderId) order.copy(status = OrderStatus.COMPLETED) else order
        }
        firestore.collection("orders").document(orderId)
            .update("status", "COMPLETED")
            .addOnFailureListener { loadAllOrders() }
    }

    /** Admin: Tambah kamera baru ke inventori */
    fun adminAddCamera(camera: Camera, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val newId = "CAM-${UUID.randomUUID().toString().take(6).uppercase()}"
        val newCamera = camera.copy(id = newId)
        firestore.collection("cameras").document(newId)
            .set(newCamera)
            .addOnSuccessListener {
                _cameras.value = _cameras.value + newCamera
                onSuccess()
            }
            .addOnFailureListener { e -> onFailure(e.localizedMessage ?: "Gagal menambahkan barang") }
    }

    /** Admin: Update kamera yang sudah ada */
    fun adminUpdateCamera(camera: Camera, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        firestore.collection("cameras").document(camera.id)
            .set(camera)
            .addOnSuccessListener {
                _cameras.value = _cameras.value.map { if (it.id == camera.id) camera else it }
                onSuccess()
            }
            .addOnFailureListener { e -> onFailure(e.localizedMessage ?: "Gagal memperbarui barang") }
    }

    /** Admin: Hapus kamera dari inventori */
    fun adminDeleteCamera(cameraId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        firestore.collection("cameras").document(cameraId)
            .delete()
            .addOnSuccessListener {
                _cameras.value = _cameras.value.filter { it.id != cameraId }
                onSuccess()
            }
            .addOnFailureListener { e -> onFailure(e.localizedMessage ?: "Gagal menghapus barang") }
    }
}
