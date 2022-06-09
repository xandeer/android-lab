plugins {
  `kotlin-dsl`
}

group = "com.github.xandeer.android.lab.buildlogic"

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
  implementation(libs.android.gradlePlugin)
  implementation(libs.kotlin.gradlePlugin)
//  implementation(libs.spotless.gradlePlugin)
}

gradlePlugin {
  plugins {
    register("androidApplicationCompose") {
      id = "androidlab.android.application.compose"
      implementationClass = "AndroidApplicationComposeConventionPlugin"
    }
    register("androidApplication") {
      id = "androidlab.android.application"
      implementationClass = "AndroidApplicationConventionPlugin"
    }
//    register("androidApplicationJacoco") {
//      id = "androidlab.android.application.jacoco"
//      implementationClass = "AndroidApplicationJacocoConventionPlugin"
//    }
    register("androidLibraryCompose") {
      id = "androidlab.android.library.compose"
      implementationClass = "AndroidLibraryComposeConventionPlugin"
    }
    register("androidLibrary") {
      id = "androidlab.android.library"
      implementationClass = "AndroidLibraryConventionPlugin"
    }
    register("androidFeature") {
      id = "androidlab.android.feature"
      implementationClass = "AndroidFeatureConventionPlugin"
    }
//    register("androidLibraryJacoco") {
//      id = "androidlab.android.library.jacoco"
//      implementationClass = "AndroidLibraryJacocoConventionPlugin"
//    }
    register("androidTest") {
      id = "androidlab.android.test"
      implementationClass = "AndroidTestConventionPlugin"
    }
//    register("spotless") {
//      id = "androidlab.spotless"
//      implementationClass = "SpotlessConventionPlugin"
//    }
  }
}
