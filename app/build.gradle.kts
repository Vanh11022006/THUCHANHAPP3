plugins {
    id("com.android.application") version "9.3.3"
}

android {
    namespace = "vn.khohang"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "vn.khohang"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
