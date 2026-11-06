plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.plugin.compose)
    alias(libs.plugins.org.jetbrains.dokka)
    id("kotlin-parcelize")
    id("com.vanniktech.maven.publish") version "0.37.0"
}

android {
    namespace = "com.pakohan.coverflow"
    compileSdk = 37

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

mavenPublishing {
    signAllPublications()
    publishToMavenCentral()
    val version = (findProperty("version") as String?) ?: error("Env variable not found")
    coordinates(
        "io.github.pakohan",
        "coverflow",
        version,
    )

    pom {
        name.set("CoverFlow")
        description.set("A Jetpack Compose implementation of CoverFlow")
        inceptionYear.set("2024")
        url.set("https://github.com/pakohan/compose-coverflow")
        licenses {
            license {
                name = "MIT License"
                url = "https://github.com/pakohan/compose-coverflow/blob/main/LICENSE"
            }
        }
        developers {
            developer {
                id = "pakohan"
                name = "Patrick Kohan"
                email = "patrick.kohan@gmail.com"
            }
        }
        scm {
            connection = "scm:git:git://github.com:pakohan/compose-coverflow.git"
            developerConnection = "scm:git:ssh://github.com:pakohan/compose-coverflow.git"
            url = "https://github.com/pakohan/compose-coverflow"
        }
    }
}

dependencies {
    // No Compose BOM here: it would be published with the library and force its versions onto all
    // Compose artifacts of consuming apps. Explicit versions only set minimums for what we use.
    // Modifier is part of the public API, so consumers need compose-ui on their classpath.
    api(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
