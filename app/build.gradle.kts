import android.databinding.tool.ext.capitalizeUS
import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.navigation.args)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
    alias(libs.plugins.google.firebase.firebase.perf)
}

android {
    namespace = "com.lfsolutions.retail"
    compileSdk = 34

    signingConfigs {
        create("releaseConfig") {
            keyAlias = "retail"
            keyPassword = "retail"
            storeFile = file("retail-key")
            storePassword = "retail"
        }
    }

    defaultConfig {
        applicationId = "com.lfsolutions.retail"
        minSdk = 24
        targetSdk = 34
        versionCode = 103
        versionName = "0.2.103"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("releaseConfig")
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
        viewBinding = true
        buildConfig = true
    }

    applicationVariants.all {
        val variant = this
        variant.outputs
            .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach { output ->
                val outputFileName = "Retail_${
                    variant.buildType.name.capitalizeUS()
                }_VC${variant.versionCode}_VN${variant.versionName}_VD${getDate()}.apk"
                output.outputFileName = outputFileName
            }
    }
}

fun getDate(): String? {
    val date = Date()
    val formattedDate = SimpleDateFormat("yyyyMMdd-HHmm").format(date)
    return formattedDate
}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.fragment.ktx)
    implementation(project(":MonthYearPicker"))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.perf)
    implementation(libs.firebase.analytics)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.signature.pad)
    implementation(libs.coroutines)
    implementation(libs.square.retrofit)
    implementation(libs.square.gson)
    implementation(libs.square.scaler)
    implementation(libs.square.okhttp)
    implementation(libs.square.okhttp.logging.interceptor)
    implementation(libs.bumptech.glide)
    implementation(libs.calculator)
    implementation(libs.printer)
}