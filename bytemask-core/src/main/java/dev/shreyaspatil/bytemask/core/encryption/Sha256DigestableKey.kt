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

import java.io.Serializable
import java.security.MessageDigest

/**
 * A value class that represents a key that can be digested using SHA-256.
 *
 * @property value The string value of the key.
 */
@JvmInline
value class Sha256DigestableKey(private val value: String) : Serializable {

    /**
     * Digests the key using SHA-256.
     *
     * @return The SHA-256 hash of the key as a ByteArray.
     */
    fun digest(): ByteArray {
        // Get an instance of the MessageDigest with SHA-256
        val digest = MessageDigest.getInstance("SHA-256")

        // Return the SHA-256 hash of the string
        return digest.digest(value.toByteArray(Charsets.UTF_8))
    }
}
