// Location: /shared/src/iosMain/kotlin/com/example/myapp/Platform.kt
package com.example.myapp

import platform.UIKit.UIDevice

class IOSPlatform : Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()