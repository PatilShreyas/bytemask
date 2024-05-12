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
package dev.shreyaspatil.bytemask.core.encryption

/**
 * A interface for a value that can be either plain or encrypted.
 *
 * @property value The string value of the Value.
 */
sealed interface Value {
    val value: String

    /**
     * A data class representing a plain (unencrypted) value.
     *
     * @property value The string value of the plain value.
     */
    data class Plain(override val value: String) : Value

    /**
     * A data class representing an encrypted value.
     *
     * @property value The string value of the encrypted value.
     */
    data class Encrypted(override val value: String) : Value
}

/**
 * Converts a String to a Plain Value.
 *
 * @return A Plain Value with this string as the value.
 */
fun String.asPlainValue() = Value.Plain(this)

/**
 * Converts a String to an Encrypted Value.
 *
 * @return An Encrypted Value with this string as the value.
 */
fun String.asEncryptedValue() = Value.Encrypted(this)
