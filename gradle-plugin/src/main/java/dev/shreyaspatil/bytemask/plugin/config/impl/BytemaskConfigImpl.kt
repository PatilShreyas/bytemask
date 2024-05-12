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

import dev.shreyaspatil.bytemask.plugin.config.ByteMaskVariantConfig
import dev.shreyaspatil.bytemask.plugin.config.BytemaskConfig
import javax.inject.Inject
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory

internal abstract class BytemaskConfigImpl
@Inject
constructor(private val objectFactory: ObjectFactory) : BytemaskConfig {
    override val variantConfigs: MutableMap<String, ByteMaskVariantConfig> = mutableMapOf()

    override fun configure(variant: String, config: Action<ByteMaskVariantConfig>) {
        val variantConfig =
            variantConfigs.getOrPut(variant) {
                objectFactory.newInstance(ByteMaskVariantConfigImpl::class.java)
            }
        config.execute(variantConfig)
    }

    override fun findConfigForVariant(variant: String): ByteMaskVariantConfig? {
        return variantConfigs[variant]
    }
}
