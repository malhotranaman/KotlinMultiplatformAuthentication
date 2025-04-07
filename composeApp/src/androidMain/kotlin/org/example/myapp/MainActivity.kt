// Location: /androidApp/src/main/java/com/example/myapp/android/MainActivity.kt
package com.example.myapp.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapp.BiometricResult
import com.example.myapp.BiometricViewModel

class MainActivity : ComponentActivity() {
    private val viewModel = BiometricViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the biometricAuth with Android context
        viewModel.initializeBiometricAuth(this)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BiometricScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun BiometricScreen(viewModel: BiometricViewModel) {
    // Check biometric availability when the screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.checkBiometricAvailability()
    }

    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Secure Authentication",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Use biometrics to securely access your account",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        when (state.isBiometricAvailable) {
            null -> {
                CircularProgressIndicator()
            }
            true -> {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Fingerprint",
                    modifier = Modifier.size(72.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.authenticate() },
                    enabled = !state.isAuthenticating,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(50.dp)
                ) {
                    Text("Authenticate with Biometrics")
                }

                Spacer(modifier = Modifier.height(24.dp))

                state.authResult?.let { result ->
                    val (text, color) = when (result) {
                        BiometricResult.SUCCESS -> "Authentication successful!" to MaterialTheme.colorScheme.primary
                        BiometricResult.ERROR -> "Authentication failed" to MaterialTheme.colorScheme.error
                        BiometricResult.NOT_AVAILABLE -> "Biometric auth not available" to MaterialTheme.colorScheme.error
                        BiometricResult.USER_CANCELED -> "Authentication canceled" to MaterialTheme.colorScheme.tertiary
                    }

                    Text(
                        text = text,
                        color = color,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            false -> {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Lock",
                    modifier = Modifier.size(72.dp),
                    tint = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Biometric authentication is not available on this device",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Please use alternative authentication methods",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}