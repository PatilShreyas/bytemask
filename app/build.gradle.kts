plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("dev.shreyaspatil.bytemask.plugin") version "1.0.0-alpha01"
}

android {
    namespace = "dev.shreyaspatil.bytemask.example"
    compileSdk = 34

    defaultConfig {
        applicationId = "dev.shreyaspatil.bytemask.example"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
    }

    buildTypes {
        debug { applicationIdSuffix = ".debug" }
        release {
            applicationIdSuffix = ".release"
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions { jvmTarget = "1.8" }
    buildFeatures { viewBinding { enable = true } }
    packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
    signingConfigs {
        named("debug") { storeFile = rootProject.file("debug.keystore") }
        create("release") { initWith(getByName("debug")) }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-ktx:1.8.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

bytemaskConfig {
    // UNCOMMENT THIS: To set the generated class name as "MyAppConfig"
    // className.set("MyAppConfig")

    // UNCOMMENT THIS: To pick secrets from the `secrets.properties`
    // defaultPropertiesFileName.set("secrets.properties")

    // For debug variant, enable encryption with a debug SHA-256 key.
    configure("debug") { enableEncryption.set(true) }

    configure("release") { enableEncryption.set(true) }
}
