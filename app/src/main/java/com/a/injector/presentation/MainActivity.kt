package com.a.injector.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.a.injector.domain.repository.DirectoryRepository
import com.a.injector.domain.repository.SuperuserRepository
import com.a.injector.domain.repository.ShizukuRepository
import com.a.injector.presentation.navigation.NavigationScreen
import com.a.injector.presentation.theme.ui.AInjectorTheme
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by inject()
    private val directoryRepository: DirectoryRepository by inject()

    private val shizukuRepository: ShizukuRepository by inject()
    private val superuserRepository: SuperuserRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        directoryRepository.initialize()

        lifecycleScope.launch {
            shizukuRepository.check()
            superuserRepository.check()
        }

        enableEdgeToEdge()
        installSplashScreen().setKeepOnScreenCondition { viewModel.isSplashScreenVisible.value }
        setContent {
            AInjectorTheme {
                NavigationScreen()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            shizukuRepository.check()
            superuserRepository.check()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        shizukuRepository.destroy()
    }
}