plugins {
  id("androidlab.android.application")
  id("androidlab.android.application.compose")
//  id("androidlab.android.application.jacoco")
  kotlin("kapt")
//  id("jacoco")
//  id("dagger.hilt.android.plugin")
//  id("androidlab.spotless")
}

android {
  defaultConfig {
    applicationId = "com.github.xandeer.androidlab"
    versionCode = 1
    versionName = "0.0.1" // X.Y.Z; X = Major, Y = minor, Z = Patch level

    // Custom test runner to set up Hilt dependency graph
    testInstrumentationRunner = "com.github.xandeer.androidlab.core.testing.AlTestRunner"
    vectorDrawables {
      useSupportLibrary = true
    }
  }

  buildTypes {
    val debug by getting {
      applicationIdSuffix = ".debug"
    }
    val release by getting {
      isMinifyEnabled = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
    val staging by creating {
      initWith(debug)
      signingConfig = signingConfigs.getByName("debug")
      matchingFallbacks.add("debug")
      applicationIdSuffix = ".staging"
    }
    val benchmark by creating {
      initWith(staging) // Usually should be `initWith(release)`. Connecting to demo backend.
      matchingFallbacks.add("debug") // Making some settings below to align closer to release.
      signingConfig = signingConfigs.getByName("debug")
      proguardFiles("benchmark-rules.pro") // Only use benchmark proguard rules
      isMinifyEnabled = false //  FIXME enabling minification breaks access to demo backend.
      isDebuggable = true
      applicationIdSuffix = ".benchmark"
    }
  }
  packagingOptions {
    resources {
      excludes.add("/META-INF/{AL2.0,LGPL2.1}")
    }
  }
  testOptions {
    unitTests {
      isIncludeAndroidResources = true
    }
  }
  namespace = "com.github.xandeer.androidlab"
}

dependencies {

  implementation(project(":feature-fab"))

  testImplementation(project(":core-testing"))
  androidTestImplementation(project(":core-testing"))
//  androidTestImplementation(project(":core-datastore-test"))
//  androidTestImplementation(project(":core-data-test"))
//  androidTestImplementation(project(":core-network"))

  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.core.ktx)
//  implementation(libs.androidx.compose.material3.windowSizeClass)
//  implementation(libs.androidx.window.manager)

//  implementation(libs.androidx.profileinstaller)

  implementation(libs.material3)
  implementation(libs.androidx.compose.material.iconsExtended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.navigation.compose)
//  implementation(libs.coil.kt)
//  implementation(libs.coil.kt.svg)

//  implementation(libs.hilt.android)
//  kapt(libs.hilt.compiler)
//  kaptAndroidTest(libs.hilt.compiler)

  // androidx.test is forcing JUnit, 4.12. This forces it to use 4.13
  configurations.configureEach {
    resolutionStrategy {
      force(libs.junit4)
      // Temporary workaround for https://issuetracker.google.com/174733673
      force("org.objenesis:objenesis:2.6")
    }
  }
}