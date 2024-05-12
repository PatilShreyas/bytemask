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
package dev.shreyaspatil.bytemask.plugin.codegen

import dev.shreyaspatil.bytemask.plugin.util.normalize
import java.io.File
import java.util.Properties

class PropertyAndValuesProvider(private val propFiles: List<File>) {
    fun getAsMap(): Map<String, String> {
        return propFiles
            .map { Properties().apply { runCatching { load(it.inputStream()) } } }
            .filter { it.isNotEmpty() }
            .flatMap { it.entries }
            .associate { it.key.toString().normalize() to it.value.toString() }
    }
}
