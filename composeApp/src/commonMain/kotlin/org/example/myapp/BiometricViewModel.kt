// Location: /shared/src/commonMain/kotlin/com/example/myapp/BiometricViewModel.kt
package com.example.myapp

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Shared ViewModel that handles biometric authentication logic
 */
class BiometricViewModel {
    private val biometricAuth = createBiometricAuth()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val _state = MutableStateFlow(BiometricState())
    val state: StateFlow<BiometricState> = _state.asStateFlow()

    /**
     * Initialize the BiometricAuth with Android context
     * This is only needed for Android
     */
    fun initializeBiometricAuth(context: Any) {
        // Cast will only succeed on Android
        runCatching {
            val androidAuth = biometricAuth as com.example.myapp.BiometricAuth
            val androidContext = context as android.content.Context
            androidAuth.initialize(androidContext)
        }
    }

    /**
     * Check if biometric authentication is available on the device
     */
    fun checkBiometricAvailability() {
        coroutineScope.launch {
            val isAvailable = biometricAuth.isBiometricAvailable()
            _state.update { it.copy(isBiometricAvailable = isAvailable) }
        }
    }

    /**
     * Authenticate the user using biometric authentication
     */
    fun authenticate() {
        _state.update { it.copy(isAuthenticating = true, authResult = null) }

        coroutineScope.launch {
            val result = biometricAuth.authenticate(
                title = "Biometric Authentication",
                subtitle = "Please authenticate to continue",
                cancelText = "Cancel"
            )

            _state.update {
                it.copy(
                    isAuthenticating = false,
                    authResult = result
                )
            }
        }
    }

    /**
     * State for biometric authentication
     */
    data class BiometricState(
        val isBiometricAvailable: Boolean? = null,
        val isAuthenticating: Boolean = false,
        val authResult: BiometricResult? = null
    )
}