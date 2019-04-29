import com.android.build.gradle.AndroidConfig
import com.android.builder.signing.DefaultSigningConfig
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

/**
 * Custom task that updates the signing config on the fly
 */
open class UpdateSigningConfigTask : DefaultTask() {
    lateinit var storeFile: File
    lateinit var keyAlias: String
    lateinit var keyPassword: String
    lateinit var storePassword: String

    @TaskAction
    fun updateSigningConfig() {
        var releaseSigningConfig: DefaultSigningConfig? = null
        val androidConfig = this.project.properties["android"] as AndroidConfig
        for (signingConfig in androidConfig.signingConfigs) {
            if (signingConfig.name == "release") {
                releaseSigningConfig = signingConfig as DefaultSigningConfig
                break
            }
        }
        releaseSigningConfig?.storeFile = storeFile
        releaseSigningConfig?.keyAlias = keyAlias
        releaseSigningConfig?.keyPassword = keyPassword
        releaseSigningConfig?.storePassword = storePassword
    }
}
