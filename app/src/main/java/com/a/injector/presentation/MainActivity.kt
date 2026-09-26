@file:Suppress("LocalContextConfigurationRead")

package com.a.injector.presentation

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.a.injector.data.system.ShizukuCommandService
import com.a.injector.data.system.SuperuserCommandService
import com.a.injector.domain.repository.DirectoryRepository
import com.a.injector.domain.repository.NavigationRepository
import com.a.injector.domain.repository.PermissionRepository
import com.a.injector.presentation.loading.SignMethod
import com.a.injector.presentation.mainnavigation.MainNavigationRoute
import com.a.injector.presentation.mainnavigation.NavigationScreen
import com.a.injector.presentation.theme.ui.AInjectorTheme
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.handleDeeplinks
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val supabaseClient: SupabaseClient by inject()

    private val viewModel: MainViewModel by inject()

    private val directoryRepository: DirectoryRepository by inject()
    private val permissionRepository: PermissionRepository by inject()
    private val shizukuCommandService: ShizukuCommandService by inject()
    private val superuserCommandService: SuperuserCommandService by inject()
    private val navigationRepository: NavigationRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        directoryRepository.initialize()

        lifecycleScope.launch {
            supabaseClient.realtime.connect()
            permissionRepository.checkPermission()
            shizukuCommandService.check()
            superuserCommandService.check()
        }

        enableEdgeToEdge()
        installSplashScreen()
        setContent {
            AInjectorTheme {
                val context = LocalContext.current
                val state by viewModel.state.collectAsStateWithLifecycle()

                val localizedContext = remember(state.language) {
                    Locale.setDefault(state.language)

                    val configuration = Configuration(context.resources.configuration)
                    configuration.setLocale(state.language)
                    context.createConfigurationContext(configuration)
                }

                CompositionLocalProvider(
                    LocalContext provides localizedContext,
                    LocalConfiguration provides localizedContext.resources.configuration,
                    LocalActivityResultRegistryOwner provides (context as ComponentActivity)
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize(),
                        color = MaterialTheme.colorScheme.surface,
                        content = { NavigationScreen() }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        supabaseClient.handleDeeplinks(intent)

        lifecycleScope.launch {
            navigationRepository.replaceTo(MainNavigationRoute.LoadingScreen(SignMethod.EmailLink))
        }
    }

    override fun onResume() {
        super.onResume()
        supabaseClient.handleDeeplinks(intent)

        lifecycleScope.launch {
            permissionRepository.checkPermission()
            shizukuCommandService.check()
            superuserCommandService.check()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        lifecycleScope.launch {
            supabaseClient.realtime.disconnect()
            shizukuCommandService.destroy()
        }
    }
}