import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

// Assinatura de release: keystore.properties na raiz, com
// storeFile / storePassword / keyAlias / keyPassword.
//
// Sem ele o build de release FALHA, de propósito. A chave de debug do Android é
// pública (senha "android"), então um release assinado com ela pode ser
// atualizado por qualquer pessoa — e o estrago só apareceria depois de
// publicado. Melhor não compilar do que compilar algo indefensável.
val keystorePropsFile = rootProject.file("keystore.properties")
val keystoreProps = Properties().apply {
    if (keystorePropsFile.exists()) keystorePropsFile.inputStream().use { load(it) }
}
val temChaveDeRelease = keystorePropsFile.exists()

gradle.taskGraph.whenReady {
    val vaiGerarRelease = allTasks.any { it.name.contains("Release") && it.name.startsWith("assemble") }
    if (vaiGerarRelease && !temChaveDeRelease) {
        throw GradleException(
            "Build de release sem keystore.properties na raiz do projeto.\n" +
                "Restaure o arquivo e o .jks a partir do seu backup. Assinar o release com a " +
                "chave de debug deixaria qualquer pessoa publicar atualizações por cima do app."
        )
    }
}

android {
    namespace = "br.com.hugolumazzini.havalapkstore"
    compileSdk = 36

    defaultConfig {
        applicationId = "br.com.hugolumazzini.havalapkstore"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }

    signingConfigs {
        if (keystorePropsFile.exists()) {
            create("release") {
                storeFile = rootProject.file(keystoreProps.getProperty("storeFile"))
                storePassword = keystoreProps.getProperty("storePassword")
                keyAlias = keystoreProps.getProperty("keyAlias")
                keyPassword = keystoreProps.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            // Sem fallback para a chave de debug: ver a checagem no topo do arquivo.
            if (temChaveDeRelease) signingConfig = signingConfigs.getByName("release")
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packaging {
        resources.excludes += setOf("/META-INF/{AL2.0,LGPL2.1}")
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.okhttp)
    implementation(libs.kotlinx.serialization.json)
}
