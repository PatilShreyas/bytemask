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
package dev.shreyaspatil.bytemask.plugin.util

/** Capitalizes the first character of the string. Example: "hello" -> "Hello" */
fun String.capitalized(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

/**
 * Converts the string to camelCase. Example:
 * 1. "hello world" -> "helloWorld"
 * 2. "hello_world" -> "helloWorld"
 * 3. "helloWorld" -> "helloWorld"
 * 4. "HelloWorld" -> "helloWorld"
 */
fun String.camelCase(): String {
    return this.split(" ", "_")
        .mapIndexed { index, word ->
            if (index == 0) word.lowercase() else word.lowercase().capitalized()
        }
        .joinToString("")
}

/**
 * Cleans the string by replacing special characters with underscore.
 *
 * Example:
 * 1. "Hello!World" -> "Hello_World"
 * 2. "Hey-There" -> "Hey_There"
 */
fun String.normalize(): String {
    return replace("[^A-Za-z0-9 _]".toRegex(), "_")
}

/**
 * Removes special characters from the string.
 *
 * Example:
 * 1. "Hello!World" -> "HelloWorld"
 * 2. "Hey-There" -> "HeyThere"
 */
fun String.removeSpecialChars(): String {
    return replace("[^A-Za-z0-9 ]".toRegex(), "")
}
