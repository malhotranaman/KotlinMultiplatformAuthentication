package com.example.myapp

// This is just a marker interface to help with the bridging
expect class BiometricAuthBridge() {
    fun isBiometricAvailable(): Boolean
    fun authenticate(title: String, subtitle: String, cancelText: String, completion: (String) -> Unit)
    fun getBiometricType(): String
}