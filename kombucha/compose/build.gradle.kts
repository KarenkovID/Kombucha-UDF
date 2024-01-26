plugins {
    alias(libs.plugins.kombucha.android.library)
    alias(libs.plugins.kombucha.jetpackCompose.library)
}

android {
    namespace = "com.github.ikarenkov.kobucha.compose"
}

dependencies {
    implementation(libs.essenty.instanceKeeper)
    implementation(libs.androidx.lifecycle.viewmodel)

    implementation(projects.kombucha.core)
}