plugins {
  id("androidlab.android.library")
  id("androidlab.android.feature")
  id("androidlab.android.library.compose")
//  id("androidlab.android.library.jacoco")
//  id("dagger.hilt.android.plugin")
//  id("androidlab.spotless")
}

android {
  namespace = "com.github.xandeer.androidlab.fab"
}

dependencies {
  implementation(libs.androidx.compose.material.iconsExtended)
  implementation(libs.androidx.compose.material3)
}