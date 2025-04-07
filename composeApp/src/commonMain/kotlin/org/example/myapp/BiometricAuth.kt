// Location: /shared/src/commonMain/kotlin/com/example/myapp/BiometricAuth.kt
package com.example.myapp

/**
 * Result of a biometric authentication attempt
 */
enum class BiometricResult {
    SUCCESS,
    ERROR,
    NOT_AVAILABLE,
    USER_CANCELED
}

/**
 * Interface for platform-specific biometric authentication
 */
expect class BiometricAuth() {
    /**
     * Checks if biometric authentication is available on the device
     * @return true if biometric authentication is available
     */
    suspend fun isBiometricAvailable(): Boolean

    /**
     * Authenticates the user using biometric authentication
     * @param title The title to display in the authentication dialog
     * @param subtitle The subtitle to display in the authentication dialog
     * @param cancelText The text for the cancel button
     * @return BiometricResult indicating the result of the authentication
     */
    suspend fun authenticate(
        title: String,
        subtitle: String,
        cancelText: String
    ): BiometricResult
}

/**
 * Helper factory to create an instance of BiometricAuth
 */
fun createBiometricAuth(): BiometricAuth = BiometricAuth()