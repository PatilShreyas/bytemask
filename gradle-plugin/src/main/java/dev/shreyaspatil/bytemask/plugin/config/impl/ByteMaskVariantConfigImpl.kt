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
package dev.shreyaspatil.bytemask.plugin.config.impl

import dev.shreyaspatil.bytemask.core.encryption.EncryptionSpec
import dev.shreyaspatil.bytemask.plugin.config.ByteMaskVariantConfig
import dev.shreyaspatil.bytemask.plugin.config.KeySource
import javax.inject.Inject
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.property

internal abstract class ByteMaskVariantConfigImpl
@Inject
constructor(objectFactory: ObjectFactory) : ByteMaskVariantConfig {

    override val enableEncryption: Property<Boolean> =
        objectFactory.property<Boolean>().convention(false)

    override val encryptionSpec: Property<EncryptionSpec> =
        objectFactory
            .property<EncryptionSpec>()
            .convention(EncryptionSpec(algorithm = "AES", transformation = "AES/CBC/PKCS5Padding"))

    override val encryptionKeySource: Property<KeySource> = objectFactory.property<KeySource>()
}
