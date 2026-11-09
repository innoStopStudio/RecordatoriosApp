
import java.util.Properties
import java.io.FileInputStream

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

val propiedadesLocales = Properties().apply {
    val archivo = rootProject.file("local.properties")

    if (archivo.exists()) {
        FileInputStream(archivo).use { load(it) }
    }
}

val supabaseUrl =
    propiedadesLocales.getProperty("SUPABASE_URL", "")

val supabasePublishableKey =
    propiedadesLocales.getProperty("SUPABASE_PUBLISHABLE_KEY", "")