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
package dev.shreyaspatil.bytemask.plugin.config

import dev.shreyaspatil.bytemask.core.encryption.EncryptionSpec
import dev.shreyaspatil.bytemask.plugin.config.impl.BytemaskConfigImpl
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.provider.Property

/** Configuration for Bytemask plugin. */
interface BytemaskConfig {
    /** Default properties file name to retrieve properties and values from. */
    val defaultPropertiesFileName: Property<String>

    /** The class name of the generated class. */
    val className: Property<String>

    /** Configurations for each variant. */
    val variantConfigs: Map<String, ByteMaskVariantConfig>

    /** Configures Bytemask for a variant. */
    fun configure(variant: String, config: Action<ByteMaskVariantConfig>)

    /** Returns the configuration for the variant. */
    fun findConfigForVariant(variant: String): ByteMaskVariantConfig?

    companion object {
        fun create(project: Project): BytemaskConfig =
            project.extensions.create("bytemaskConfig", BytemaskConfigImpl::class.java).apply {
                defaultPropertiesFileName.convention("bytemask.properties")
                className.convention("BytemaskConfig")
            }
    }
}

/** Configuration of Bytemask for variant. */
interface ByteMaskVariantConfig {
    /** Enable encryption for this variant. */
    val enableEncryption: Property<Boolean>

    /** Encryption spec for this variant. */
    val encryptionSpec: Property<EncryptionSpec>

    /** Source of the key for encryption. */
    val encryptionKeySource: Property<KeySource>
}

/** Represents the source of the key. */
sealed class KeySource {
    /**
     * Represents the signing configuration.
     *
     * It means key will be retrieved from the declared signing configs for the module.
     *
     * @param name Name of the signing config.
     */
    class SigningConfig(val name: String) : KeySource()

    /**
     * Represents the key.
     *
     * It means key will [encryptionKey].
     */
    class Key(val encryptionKey: String) : KeySource()
}
