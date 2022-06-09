plugins {
  id("androidlab.android.library")
  kotlin("kapt")
//  id("androidlab.spotless")
}

android {
  namespace = "com.github.xandeer.androidlab.core.testing"
}

dependencies {
//  implementation(project(":core-common"))
//  implementation(project(":core-data"))
//  implementation(project(":core-model"))

  implementation(libs.hilt.android)
  kapt(libs.hilt.compiler)

  api(libs.junit4)
  api(libs.androidx.test.core)
  api(libs.kotlinx.coroutines.test)
  api(libs.turbine)

  api(libs.androidx.test.espresso.core)
  api(libs.androidx.test.runner)
  api(libs.androidx.test.rules)
  api(libs.androidx.compose.ui.test)
  api(libs.hilt.android.testing)

  debugApi(libs.androidx.compose.ui.testManifest)
}
