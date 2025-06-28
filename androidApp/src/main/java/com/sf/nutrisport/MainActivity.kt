package com.sf.nutrisport

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import com.nutrisport.navigation.SetupNavGraph
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue

import com.nutrisport.data.domain.CustomerRepository
import com.sf.nutrisport.navigation.Screen
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            )
        )
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val customerRepository = koinInject<CustomerRepository>()
                    var appReady by remember { mutableStateOf(false) }
                    var isUserAuthenticated = remember{customerRepository.getCurrentUserId()!=null}

                    var startDestination = remember {
                        if (isUserAuthenticated) Screen.HomeGraph
                        else Screen.Auth
                    }
                    LaunchedEffect(Unit) {
                        GoogleAuthProvider.create(credentials = GoogleAuthCredentials(serverId = Constants.WEB_CLIENT_ID))
                        appReady = true
                    }
                    AnimatedVisibility(visible = appReady) {
                        SetupNavGraph(startDestination)
                    }

                }
            }
        }
    }
}
