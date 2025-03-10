import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.org.jetbrains.kotlin.plugin.compose)
    alias(libs.plugins.org.jetbrains.dokka)
    id("kotlin-parcelize")
    id("com.vanniktech.maven.publish") version "0.31.0"
}

android {
    namespace = "com.pakohan.coverflow"
    compileSdk = 34

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
}

mavenPublishing {
    signAllPublications()
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
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
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.material3.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
