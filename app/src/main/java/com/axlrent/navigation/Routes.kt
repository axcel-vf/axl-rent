package com.axlrent.navigation

/**
 * Navigation route constants for the Axl Rent app.
 */
object Routes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val SEARCH = "search"
    const val CAMERA_DETAIL = "camera_detail/{cameraId}"
    const val BOOKING = "booking/{cameraId}"
    const val PAYMENT = "payment"
    const val PAYMENT_SUCCESS = "payment_success"
    const val ORDER_HISTORY = "order_history"
    const val PROFILE = "profile"
    const val WISHLIST = "wishlist"
    const val HELP_CENTER = "help_center"
    const val ADMIN_DASHBOARD = "admin_dashboard"
    const val QRIS_PAYMENT = "qris_payment"

    fun cameraDetail(cameraId: String) = "camera_detail/$cameraId"
    fun booking(cameraId: String) = "booking/$cameraId"
}
