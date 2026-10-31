package com.axlrent.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.axlrent.ui.screens.*
import com.axlrent.ui.theme.*
import com.axlrent.viewmodel.AppViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavGraph(
    navController: NavHostController,
    appViewModel: AppViewModel
) {
    // Cek session aktif: Jika sudah login sebagai admin, langsung ke ADMIN_DASHBOARD
    val currentUser = FirebaseAuth.getInstance().currentUser
    val startDestination = when {
        currentUser == null -> Routes.SPLASH
        currentUser.email == AppViewModel.ADMIN_EMAIL -> Routes.ADMIN_DASHBOARD
        else -> Routes.HOME
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val rootRoutes = listOf(
        Routes.HOME,
        Routes.ORDER_HISTORY,
        Routes.WISHLIST,
        Routes.PROFILE
    )

    val showBottomBar = currentRoute in rootRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                GlobalBottomNavBar(
                    currentRoute = currentRoute,
                    onTabSelected = { route ->
                        if (route == Routes.HOME) {
                            navController.navigate(route) {
                                popUpTo(Routes.HOME) { inclusive = true }
                                launchSingleTop = true
                            }
                        } else {
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Routes.SPLASH) {
                SplashScreen(
                    viewModel = appViewModel,
                    onNavigateToOnboarding = {
                        navController.navigate(Routes.ONBOARDING) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.ONBOARDING) {
                OnboardingScreen(
                    onNavigateToLogin = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.ONBOARDING) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.LOGIN) {
                LoginScreen(
                    viewModel = appViewModel,
                    onLoginSuccess = { isAdmin ->
                        if (isAdmin) {
                            navController.navigate(Routes.ADMIN_DASHBOARD) {
                                popUpTo(0) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Routes.HOME) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                            }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(Routes.REGISTER)
                    }
                )
            }

            composable(Routes.REGISTER) {
                RegisterScreen(
                    viewModel = appViewModel,
                    onRegisterSuccess = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.REGISTER) { inclusive = true }
                        }
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Routes.HOME) {
                HomeScreen(
                    viewModel = appViewModel,
                    onNavigateToSearch = {
                        navController.navigate(Routes.SEARCH)
                    },
                    onNavigateToCamera = { cameraId ->
                        navController.navigate(Routes.cameraDetail(cameraId))
                    },
                    onNavigateToHistory = {
                        navController.navigate(Routes.ORDER_HISTORY)
                    },
                    onNavigateToProfile = {
                        navController.navigate(Routes.PROFILE) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable(Routes.SEARCH) {
                SearchScreen(
                    viewModel = appViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToCamera = { cameraId ->
                        navController.navigate(Routes.cameraDetail(cameraId))
                    },
                    onNavigateToProfile = {
                        navController.navigate(Routes.PROFILE) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable(
                route = Routes.CAMERA_DETAIL,
                arguments = listOf(navArgument("cameraId") { type = NavType.StringType })
            ) { backStackEntry ->
                val cameraId = backStackEntry.arguments?.getString("cameraId") ?: ""
                CameraDetailScreen(
                    cameraId = cameraId,
                    viewModel = appViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToBooking = {
                        navController.navigate(Routes.booking(cameraId))
                    }
                )
            }

            composable(
                route = Routes.BOOKING,
                arguments = listOf(navArgument("cameraId") { type = NavType.StringType })
            ) { backStackEntry ->
                val cameraId = backStackEntry.arguments?.getString("cameraId") ?: ""
                BookingScreen(
                    cameraId = cameraId,
                    viewModel = appViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToPayment = {
                        navController.navigate(Routes.QRIS_PAYMENT)
                    }
                )
            }

            composable(Routes.QRIS_PAYMENT) {
                QrisPaymentPage(
                    viewModel = appViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onPaymentSuccess = {
                        navController.navigate(Routes.ORDER_HISTORY) {
                            popUpTo(Routes.HOME)
                        }
                    }
                )
            }

            composable(Routes.PAYMENT) {
                QrisPaymentPage(
                    viewModel = appViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onPaymentSuccess = {
                        navController.navigate(Routes.ORDER_HISTORY) {
                            popUpTo(Routes.HOME)
                        }
                    }
                )
            }

            composable(Routes.PAYMENT_SUCCESS) {
                PaymentSuccessScreen(
                    viewModel = appViewModel,
                    onNavigateToHome = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    },
                    onNavigateToHistory = {
                        navController.navigate(Routes.ORDER_HISTORY) {
                            popUpTo(Routes.HOME)
                        }
                    }
                )
            }

            composable(Routes.ORDER_HISTORY) {
                OrderHistoryScreen(
                    viewModel = appViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToHome = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    },
                    onNavigateToProfile = {
                        navController.navigate(Routes.PROFILE)
                    }
                )
            }

            composable(Routes.WISHLIST) {
                WishlistScreen(
                    viewModel = appViewModel,
                    onNavigateToCamera = { cameraId ->
                        navController.navigate(Routes.cameraDetail(cameraId))
                    },
                    onNavigateToBooking = { cameraId ->
                        navController.navigate(Routes.booking(cameraId))
                    },
                    onNavigateToHome = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable(Routes.PROFILE) {
                ProfileScreen(
                    viewModel = appViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToHome = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    },
                    onNavigateToHistory = {
                        navController.navigate(Routes.ORDER_HISTORY) {
                            popUpTo(Routes.HOME)
                        }
                    },
                    onNavigateToCart = {
                        navController.navigate(Routes.WISHLIST) {
                            popUpTo(Routes.HOME)
                        }
                    },
                    onNavigateToHelpCenter = {
                        navController.navigate(Routes.HELP_CENTER)
                    },
                    onLogout = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.HELP_CENTER) {
                HelpCenterScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Admin Dashboard — route guard
            composable(Routes.ADMIN_DASHBOARD) {
                val isAdmin by appViewModel.isAdmin.collectAsState()
                val isUserLoggedIn by appViewModel.isUserLoggedIn.collectAsState()

                LaunchedEffect(isAdmin, isUserLoggedIn) {
                    if (isUserLoggedIn == false) {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    } else if (isAdmin == false) {
                        navController.navigate(Routes.HOME) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }

                if (isAdmin && isUserLoggedIn == true) {
                    AdminDashboardScreen(
                        viewModel = appViewModel,
                        onLogout = {
                            appViewModel.logoutUser {
                                navController.navigate(Routes.LOGIN) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GlobalBottomNavBar(
    currentRoute: String?,
    onTabSelected: (String) -> Unit
) {
    NavigationBar(
        containerColor = DarkSurface,
        tonalElevation = 0.dp
    ) {
        val items = listOf(
            NavigationItem("Home", Routes.HOME, Icons.Filled.Home, Icons.Outlined.Home),
            NavigationItem("History", Routes.ORDER_HISTORY, Icons.Filled.History, Icons.Outlined.History),
            NavigationItem("Keranjang", Routes.WISHLIST, Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart),
            NavigationItem("Profile", Routes.PROFILE, Icons.Filled.Person, Icons.Outlined.Person)
        )
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(item.route) },
                icon = {
                    Icon(
                        if (isSelected) item.filledIcon else item.outlinedIcon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = OrangePrimary,
                    selectedTextColor = OrangePrimary,
                    unselectedIconColor = TextGrayDark,
                    unselectedTextColor = TextGrayDark,
                    indicatorColor = OrangeSubtle
                )
            )
        }
    }
}

data class NavigationItem(
    val label: String,
    val route: String,
    val filledIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val outlinedIcon: androidx.compose.ui.graphics.vector.ImageVector
)
