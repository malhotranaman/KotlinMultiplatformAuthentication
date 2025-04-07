// Location: /shared/src/commonMain/kotlin/com/example/myapp/Platform.kt
package com.example.myapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform