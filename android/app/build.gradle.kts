plugins {
    id("com.android.application")
}

// 버전 번호는 GitHub Actions 실행 번호로 자동 증가 (업데이트 설치가 되려면 매번 커져야 함)
val vCode = (project.findProperty("versionCode") as String?)?.toIntOrNull() ?: 1
// 서명 키: GitHub 비밀값(Secrets)에서 받아 온다. 없으면 디버그 키로 빌드(이 경우 업데이트 설치가 안 될 수 있음)
val ksFile = System.getenv("AG_KEYSTORE_FILE")

android {
    namespace = "com.afterglow.protocol"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.afterglow.protocol"
        minSdk = 24
        targetSdk = 34
        versionCode = vCode
        versionName = "1.0.$vCode"
    }

    signingConfigs {
        if (ksFile != null && file(ksFile).exists()) {
            create("release") {
                storeFile = file(ksFile)
                storePassword = System.getenv("AG_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("AG_KEY_ALIAS")
                keyPassword = System.getenv("AG_KEYSTORE_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.findByName("release") ?: signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    // 게임 HTML 안의 음악(base64)을 압축하지 않아도 되도록 기본값 사용
}

dependencies {
    implementation("androidx.webkit:webkit:1.11.0")
}
