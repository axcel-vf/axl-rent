package com.axlrent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axlrent.navigation.NavGraph
import com.axlrent.ui.theme.AxlRentTheme
import com.axlrent.ui.theme.DarkNavy
import com.axlrent.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AxlRentTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkNavy
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        val navController = rememberNavController()
                        val appViewModel: AppViewModel = viewModel()

                        // Menampilkan NavGraph utama secara bersih tanpa gangguan overlay kamera
                        NavGraph(navController = navController, appViewModel = appViewModel)
                    }
                }
            }
        }
    }
}
