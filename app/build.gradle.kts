plugins {
    alias(libs.plugins.android.application)

}

android {
    namespace = "com.calculate.ferronix"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.calculate.ferronix"
        minSdk = 24
        targetSdk = 35
        versionCode = 8
        versionName = "1.1.0"




        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true // Удаляет неиспользуемые ресурсы
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    implementation(libs.material3)


// AppMetrica SDK.
    implementation("io.appmetrica.analytics:analytics:7.7.0")



    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}