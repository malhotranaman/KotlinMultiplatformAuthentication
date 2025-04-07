// Location: /shared/src/androidMain/kotlin/com/example/myapp/BiometricAuth.kt
package com.example.myapp

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

actual class BiometricAuth {
    private var context: Context? = null

    /**
     * Initialize with Android context
     * This should be called before any other methods
     */
    fun initialize(context: Context) {
        this.context = context.applicationContext
    }

    actual suspend fun isBiometricAvailable(): Boolean {
        val context = context ?: return false
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) ==
                BiometricManager.BIOMETRIC_SUCCESS
    }

    actual suspend fun authenticate(
        title: String,
        subtitle: String,
        cancelText: String
    ): BiometricResult = suspendCancellableCoroutine { continuation ->
        val context = context ?: run {
            continuation.resume(BiometricResult.ERROR)
            return@suspendCancellableCoroutine
        }

        // We need a FragmentActivity to show the BiometricPrompt
        if (context !is FragmentActivity) {
            val currentActivity = getFragmentActivity(context)
            if (currentActivity == null) {
                continuation.resume(BiometricResult.ERROR)
                return@suspendCancellableCoroutine
            }
        }

        val activity = when (context) {
            is FragmentActivity -> context
            else -> getFragmentActivity(context) ?: run {
                continuation.resume(BiometricResult.ERROR)
                return@suspendCancellableCoroutine
            }
        }

        val executor = ContextCompat.getMainExecutor(context)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                continuation.resume(BiometricResult.SUCCESS)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                val resultCode = when (errorCode) {
                    BiometricPrompt.ERROR_USER_CANCELED,
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON -> BiometricResult.USER_CANCELED
                    BiometricPrompt.ERROR_HW_NOT_PRESENT,
                    BiometricPrompt.ERROR_NO_BIOMETRICS,
                    BiometricPrompt.ERROR_HW_UNAVAILABLE -> BiometricResult.NOT_AVAILABLE
                    else -> BiometricResult.ERROR
                }
                continuation.resume(resultCode)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                // This method is called when authentication fails, but it's not final
                // We don't resolve the continuation here, as onAuthenticationError will be called
            }
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText(cancelText)
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()

        val biometricPrompt = BiometricPrompt(activity, executor, callback)
        biometricPrompt.authenticate(promptInfo)

        continuation.invokeOnCancellation {
            // Handle cancellation if needed
        }
    }

    /**
     * Utility method to get the current FragmentActivity
     * This is a simplified version - in a real app you'd use a more robust method
     */
    private fun getFragmentActivity(context: Context): FragmentActivity? {
        // In a real app, you would have a way to access the current activity
        // This is just a placeholder - you should replace with your app's specific logic
        return null
    }
}