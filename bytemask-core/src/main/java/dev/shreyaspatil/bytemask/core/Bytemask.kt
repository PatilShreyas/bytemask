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
package dev.shreyaspatil.bytemask.core

import dev.shreyaspatil.bytemask.core.encryption.EncryptionSpec
import dev.shreyaspatil.bytemask.core.encryption.asEncryptedValue
import dev.shreyaspatil.bytemask.core.encryption.asPlainValue
import dev.shreyaspatil.bytemask.core.encryption.decrypt
import dev.shreyaspatil.bytemask.core.encryption.encrypt

/**
 * ByteMask is a class that provides functionality for masking and unmasking strings. It uses an
 * [EncryptionKeyProvider] to get the key for encryption and decryption.
 *
 * @property encryptionKeyProvider used to get the key for encryption and decryption.
 */
class Bytemask private constructor(private val encryptionKeyProvider: EncryptionKeyProvider) {

    /**
     * Masks a string by encrypting it.
     *
     * @param encryptionSpec The details of the encryption, such as the algorithm and mode.
     * @param value The string to be masked.
     * @return The masked string.
     */
    fun mask(encryptionSpec: EncryptionSpec, value: String): String {
        return value
            .asPlainValue()
            .encrypt(detail = encryptionSpec, key = encryptionKeyProvider.get())
            .value
    }

    /**
     * Unmasks a string by decrypting it.
     *
     * @param encryptionSpec The details of the encryption, such as the algorithm and mode.
     * @param value The string to be unmasked.
     * @return The unmasked string.
     */
    fun unmask(encryptionSpec: EncryptionSpec, value: String): String {
        return value
            .asEncryptedValue()
            .decrypt(detail = encryptionSpec, key = encryptionKeyProvider.get())
            .value
    }

    companion object {
        @Volatile private var INSTANCE: Bytemask? = null

        /**
         * Initializes the ByteMask.
         *
         * @param encryptionKeyProvider An instance of [EncryptionKeyProvider] used to get the key
         *   for encryption and decryption.
         */
        fun init(encryptionKeyProvider: EncryptionKeyProvider) {
            if (INSTANCE != null) return

            synchronized(this) {
                if (INSTANCE == null) {
                    val byteMask = Bytemask(encryptionKeyProvider = encryptionKeyProvider)
                    INSTANCE = byteMask
                }
            }
        }

        /**
         * Returns the instance of ByteMask.
         *
         * @return The singleton instance of ByteMask.
         * @throws IllegalStateException If the ByteMask instance has not been initialized.
         */
        fun getInstance(): Bytemask {
            return INSTANCE
                ?: synchronized(this) {
                    INSTANCE ?: throw IllegalStateException("ByteMask is not initialized.")
                }
        }
    }
}
