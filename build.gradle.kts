// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    // El plugin 'kotlin-compose' es para Kotlin 2.0+. Lo eliminamos para usar Kotlin 1.9.22.
    // alias(libs.plugins.kotlin.compose) apply false 
}
