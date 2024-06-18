/**
 * Copyright 2024 Shreyas Patil
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.shreyaspatil.bytemask.plugin

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.DynamicFeatureAndroidComponentsExtension
import com.android.build.api.variant.GeneratesApk
import com.android.build.api.variant.Variant
import dev.shreyaspatil.bytemask.core.encryption.EncryptionSpec
import dev.shreyaspatil.bytemask.core.encryption.Sha256DigestableKey
import dev.shreyaspatil.bytemask.plugin.config.BytemaskConfig
import dev.shreyaspatil.bytemask.plugin.config.KeySource
import dev.shreyaspatil.bytemask.plugin.task.BytemaskCodegenTask
import dev.shreyaspatil.bytemask.plugin.util.capitalized
import java.io.File
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Provider

/** Bytemask Gradle Plugin. Applies on Android projects. Entry point of the plugin. */
class BytemaskPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        var pluginApplied = false

        val config = BytemaskConfig.create(target)

        target.pluginManager.withPlugin("com.android.application") {
            pluginApplied = true
            target.extensions.configure(ApplicationAndroidComponentsExtension::class.java) {
                onVariants { variant -> handleVariant(variant, target, config) }
            }
        }

        target.pluginManager.withPlugin("com.android.dynamic-feature") {
            pluginApplied = true
            target.extensions.configure(DynamicFeatureAndroidComponentsExtension::class.java) {
                onVariants { variant -> handleVariant(variant, target, config) }
            }
        }

        target.afterEvaluate {
            if (!pluginApplied) {
                project.logger.error(
                    "The bytemask Gradle plugin needs to be applied on a project with either:" +
                        "com.android.application, com.android.dynamic-feature, com.android.library"
                )
            }
        }

        // Add the module dependencies
        target.dependencies.add(
            "implementation",
            "dev.shreyaspatil.bytemask:bytemask-android:${PluginConfig.VERSION}"
        )
    }

    private fun <T> handleVariant(variant: T, project: Project, config: BytemaskConfig) where
    T : Variant,
    T : GeneratesApk {
        val codegenTask =
            project.tasks.register(
                "bytemask${variant.name.capitalized()}",
                BytemaskCodegenTask::class.java
            ) {
                val propertyFileName = config.defaultPropertiesFileName.get()

                bytemaskPropFiles.set(
                    getBytemaskPropertyFiles(
                        propertyFileName = propertyFileName,
                        buildType = variant.buildType.orEmpty(),
                        flavorNames = variant.productFlavors.map { it.second },
                        root = project.projectDir
                    )
                )
                applicationId.set(variant.applicationId)
                className.set(config.className)

                val task = this
                val configForVariant = config.findConfigForVariant(variant.name)
                if (configForVariant != null) {
                    task.enableEncryption.set(configForVariant.enableEncryption.get())
                    task.encryptionSpec.set(configForVariant.encryptionSpec.get())
                    if (configForVariant.enableEncryption.get()) {
                        val keySource = configForVariant.encryptionKeySource.orNull
                        val encryptionKey =
                            getEncryptionKey(
                                keySource = keySource,
                                project = project,
                                variant = variant
                            )
                        task.encryptionKey.set(encryptionKey)
                    }
                } else {
                    // Set default encryption spec
                    task.enableEncryption.set(false)
                    task.encryptionSpec.set(
                        EncryptionSpec(algorithm = "AES", transformation = "AES/CBC/PKCS5Padding")
                    )
                }
            }
        variant.sources.java?.addGeneratedSourceDirectory(
            taskProvider = codegenTask,
            wiredWith = BytemaskCodegenTask::outputDirectory
        )
    }

    private fun <T> getEncryptionKey(
        keySource: KeySource?,
        project: Project,
        variant: T
    ): Provider<Sha256DigestableKey?> where T : Variant =
        project.provider {
            when (keySource) {
                is KeySource.SigningConfig ->
                    getAppSigningKeyForVariant(
                        project = project,
                        keySource = keySource,
                        variant = variant
                    )
                is KeySource.Key -> Sha256DigestableKey(value = keySource.encryptionKey)
                null ->
                    getAppSigningKeyForVariant(
                        project = project,
                        keySource = KeySource.SigningConfig(variant.name),
                        variant = variant
                    )
            }
        }

    /** Returns the signing key for the variant. It will be used as an encryption key. */
    private fun <T> getAppSigningKeyForVariant(
        project: Project,
        keySource: KeySource.SigningConfig,
        variant: T
    ): Sha256DigestableKey where T : Variant {
        val signingConfig =
            project.extensions
                .getByType(CommonExtension::class.java)
                .signingConfigs
                .findByName(keySource.name)
        return VariantSigningKeyProvider(signingConfig, variant.name).get()
    }

    companion object {

        /** Returns property files for Bytemask. */
        fun getBytemaskPropertyFiles(
            propertyFileName: String,
            buildType: String,
            flavorNames: List<String>,
            root: File
        ): List<File> {
            return getBytemaskPropFileLocations(propertyFileName, buildType, flavorNames).map {
                root.resolve(it)
            }
        }

        /**
         * Returns possible locations of property file. It will be used to search for property file
         * in the project.
         */
        fun getBytemaskPropFileLocations(
            propertyFileName: String,
            buildType: String,
            flavorNames: List<String>
        ): List<String> {
            val fileLocations: MutableList<String> = ArrayList()
            val flavorName =
                flavorNames.stream().reduce("") { a, b ->
                    a + if (a.isEmpty()) b else b.capitalized()
                }
            fileLocations.add("")
            fileLocations.add("src/$flavorName/$buildType")
            fileLocations.add("src/$buildType/$flavorName")
            fileLocations.add("src/$flavorName")
            fileLocations.add("src/$buildType")
            fileLocations.add("src/" + flavorName + buildType.capitalized())
            fileLocations.add("src/$buildType")
            var fileLocation = "src"
            for (flavor in flavorNames) {
                fileLocation += "/$flavor"
                fileLocations.add(fileLocation)
                fileLocations.add("$fileLocation/$buildType")
                fileLocations.add(fileLocation + buildType.capitalized())
            }
            return fileLocations
                .distinct()
                .sortedBy { path -> path.count { it == '/' } }
                .map { location: String ->
                    if (location.isEmpty()) location + propertyFileName
                    else "$location/$propertyFileName"
                }
        }
    }
}
