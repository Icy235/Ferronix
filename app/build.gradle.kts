import java.util.Properties

plugins {
    alias(libs.plugins.android.application)

}

android {
    namespace = "com.calculate.ferronix"
    compileSdk = 35
    android.buildFeatures.buildConfig = true
    defaultConfig {
        applicationId = "com.calculate.ferronix"
        minSdk = 24
        targetSdk = 35
        versionCode = 15
        versionName = "1.3.5"

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
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    // Читаем api.key из local.properties и добавляем в BuildConfig
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        val properties = Properties().apply {
            localPropertiesFile.inputStream().use { load(it) } // Загружаем свойства
        }
        val apiKey = properties.getProperty("api.key", "")
        val botToken = properties.getProperty("bot.token", "")
        val chatId = properties.getProperty("chat.id", "")

        if (apiKey.isNotEmpty()) {
            buildTypes.forEach {
                it.buildConfigField("String", "API_KEY", "\"$apiKey\"")
                it.buildConfigField("String", "BOT_TOKEN", "\"$botToken\"")
                it.buildConfigField("String", "CHAT_ID", "\"$chatId\"")
            }
        } else {
            throw IllegalArgumentException("API_KEY is missing in local.properties")
        }
    } else {
        throw IllegalStateException("local.properties file not found.")
    }
}

dependencies {

    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.material3)

    // AppMetrica SDK.
    implementation(libs.analytics)

    // PDF Reader
    implementation("com.github.DImuthuUpe:AndroidPdfViewer:3.1.0-beta.1")
    implementation(libs.firebase.storage)
    implementation(libs.okhttp)

   //Точки для листания
    implementation("com.github.tommybuonomo:dotsindicator:3.0.3")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
