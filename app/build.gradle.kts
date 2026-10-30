import com.android.build.api.dsl.ViewBinding

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    //id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    //id("com.google.gms.google-services")
    id("com.google.devtools.ksp")
    //kotlin("kapt")
}

android {
    namespace = "com.gfreeman.takeme"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.gfreeman.takeme"
        minSdk = 28
        targetSdk = 37
        versionCode = 100
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug{
            isDebuggable = true
            isMinifyEnabled = false
            android.buildFeatures.buildConfig = true
            buildConfigField("Boolean", "IS_DEBUGGABLE", "true")
        }
        release {
            isMinifyEnabled = false
            isDebuggable = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
//    kotlinOptions {
//        jvmTarget = "1.8"
//    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.fragment)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    //implementation(libs.play.services.maps)
    implementation(libs.retrofit)
    implementation(libs.converter.gson.v230)
    implementation(libs.gson)
    implementation(libs.play.services.location)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.lottie)
    //implementation(libs.credentials)
    //implementation(libs.firebase.auth.ktx)
    //implementation(libs.googleid)
    //kapt("androidx.room:room-compiler:2.6.1")
    ksp(libs.room.compiler)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.common)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}