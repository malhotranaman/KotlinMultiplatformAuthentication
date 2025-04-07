package com.example.myapp

import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.stringWithCString

// This is how we bridge to the Objective-C compatible Swift class
actual class BiometricAuthBridge {
    // Reference to the ObjC/Swift class
    private val nativeBridge = BiometricAuthIOS()

    actual fun isBiometricAvailable(): Boolean {
        return nativeBridge.isBiometricAvailable()
    }

    actual fun authenticate(title: String, subtitle: String, cancelText: String, completion: (String) -> Unit) {
        nativeBridge.authenticate(
            title = title,
            subtitle = subtitle,
            cancelText = cancelText
        ) { result ->
            completion(result)
        }
    }

    actual fun getBiometricType(): String {
        return nativeBridge.getBiometricType()
    }
}