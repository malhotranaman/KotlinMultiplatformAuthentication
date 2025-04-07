// Location: /shared/src/androidMain/kotlin/com/example/myapp/Platform.kt
package com.example.myapp

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()