import java.io.FileInputStream
import java.util.Properties
android.buildFeatures.buildConfig true
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}


fun getGeminiApiKey(): String {
    val properties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        properties.load(FileInputStream(localPropertiesFile))
    }
    return properties.getProperty("GEMINI_API_KEY") ?: ""
}


android {
    namespace = "com.example.futurefit"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.futurefit"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Secure API Key access
        buildConfigField("String", "GEMINI_API_KEY", "\"${getGeminiApiKey()}\"")
    }
    buildFeatures {
        buildConfig = true
    }


    buildTypes {
        release {
            isMinifyEnabled = false
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
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
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // working with json file
    implementation("com.google.code.gson:gson:2.8.8")


    //gemini ai
    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")



    dependencies {
        // Import the BoM for the Firebase platform
        implementation(platform("com.google.firebase:firebase-bom:33.13.0"))

        // Add the dependency for the Firebase Authentication library
        // When using the BoM, you don't specify versions in Firebase library dependencies
        implementation("com.google.firebase:firebase-auth")

        // Also add the dependency for the Google Play services library and specify its version
        implementation("com.google.android.gms:play-services-auth:21.3.0")

        implementation("com.google.android.gms:play-services-auth:20.7.0")


        implementation("com.github.bumptech.glide:glide:4.16.0")


        implementation ("com.google.android.material:material:1.10.0")

    }

    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("com.google.android.flexbox:flexbox:3.0.0")

    // Example for Google Sign-In
    implementation("com.google.android.gms:play-services-auth:21.1.0")
    // Example for Location
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation ("com.cloudinary:cloudinary-android:2.2.0")
    implementation ("com.github.bumptech.glide:glide:4.15.1")


    implementation ("com.afollestad.material-dialogs:core:3.3.0")
    implementation ("com.afollestad.material-dialogs:input:3.3.0")

    implementation ("com.afollestad.material-dialogs:core:3.3.0")



}
