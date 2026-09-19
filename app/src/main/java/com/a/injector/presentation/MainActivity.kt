package com.a.injector.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.a.injector.data.system.ShizukuCommandService
import com.a.injector.data.system.SuperuserCommandService
import com.a.injector.domain.repository.DirectoryRepository
import com.a.injector.presentation.navigation.NavigationScreen
import com.a.injector.presentation.theme.ui.AInjectorTheme
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val directoryRepository: DirectoryRepository by inject()

    private val shizukuCommandService: ShizukuCommandService by inject()
    private val superuserCommandService: SuperuserCommandService by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        directoryRepository.initialize()

        lifecycleScope.launch {
            shizukuCommandService.check()
            superuserCommandService.check()
        }

        enableEdgeToEdge()
        installSplashScreen()
        setContent {
            AInjectorTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface,
                    content = { NavigationScreen() }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            shizukuCommandService.check()
            superuserCommandService.check()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.launch {
            shizukuCommandService.destroy()
        }
    }
}