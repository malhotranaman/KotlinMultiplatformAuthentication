package com.example.myapp

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

actual class BiometricAuth {
    // Reference to the bridge implementation
    private val biometricAuthBridge = BiometricAuthBridge()

    actual suspend fun isBiometricAvailable(): Boolean {
        return biometricAuthBridge.isBiometricAvailable()
    }

    actual suspend fun authenticate(
        title: String,
        subtitle: String,
        cancelText: String
    ): BiometricResult = suspendCancellableCoroutine { continuation ->
        biometricAuthBridge.authenticate(
            title = title,
            subtitle = subtitle,
            cancelText = cancelText
        ) { resultString ->
            val result = when (resultString) {
                "SUCCESS" -> BiometricResult.SUCCESS
                "USER_CANCELED" -> BiometricResult.USER_CANCELED
                "NOT_AVAILABLE" -> BiometricResult.NOT_AVAILABLE
                else -> BiometricResult.ERROR
            }
            continuation.resume(result)
        }
    }

    fun getBiometricType(): String {
        return biometricAuthBridge.getBiometricType()
    }
}