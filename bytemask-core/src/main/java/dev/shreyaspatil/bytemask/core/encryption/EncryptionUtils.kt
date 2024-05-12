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

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
/** This object provides utility functions for AES encryption and decryption. */
object EncryptionUtils {
    /**
     * Encrypts a string using AES encryption.
     *
     * @param key The encryption key as a string. It should be hashed with SHA-256.
     * @param value The string to encrypt.
     * @return The encrypted string.
     */
    fun encrypt(
        detail: EncryptionSpec,
        key: Sha256DigestableKey,
        plain: Value.Plain
    ): Value.Encrypted {
        // Generate a random Initialization Vector (IV)
        val iv = ByteArray(16)
        SecureRandom().nextBytes(iv)

        // Generate a SecretKeySpec from the SHA-256 hashed key
        val secretKeySpec = SecretKeySpec(key.digest(), detail.algorithm)

        // Create an IvParameterSpec from the IV
        val ivParameterSpec = IvParameterSpec(iv)

        // Get an instance of the Cipher
        val cipher = Cipher.getInstance(detail.transformation)

        // Initialize the Cipher in ENCRYPT_MODE with the SecretKeySpec and IvParameterSpec
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)

        // Encrypt the value and return it as a Base64 encoded string
        val encryptedValue = cipher.doFinal(plain.value.toByteArray())
        return Base64.Default.encode(iv + encryptedValue).asEncryptedValue()
    }

    /**
     * Decrypts an AES encrypted string.
     *
     * @param key The encryption key as a string. It should be hashed with SHA-256.
     * @param encrypted The encrypted string to decrypt.
     * @return The decrypted string.
     */
    fun decrypt(
        detail: EncryptionSpec,
        key: Sha256DigestableKey,
        encrypted: Value.Encrypted
    ): Value.Plain {
        // Decode the encrypted string from Base64 to ByteArray
        val decoded = Base64.Default.decode(encrypted.value)

        // Extract the Initialization Vector (IV) from the decoded ByteArray
        val iv = decoded.copyOfRange(0, 16)

        // Extract the encrypted value from the decoded ByteArray
        val encryptedValue = decoded.copyOfRange(16, decoded.size)

        // Generate a SecretKeySpec from the SHA-256 hashed key
        val secretKeySpec = SecretKeySpec(key.digest(), detail.algorithm)

        // Create an IvParameterSpec from the IV
        val ivParameterSpec = IvParameterSpec(iv)

        // Get an instance of the Cipher
        val cipher = Cipher.getInstance(detail.transformation)

        // Initialize the Cipher in DECRYPT_MODE with the SecretKeySpec and IvParameterSpec
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)

        // Decrypt the encrypted value and return it as a string
        return cipher.doFinal(encryptedValue).toString(Charsets.UTF_8).asPlainValue()
    }
}

/**
 * Encrypts the value.
 *
 * This function uses the EncryptionUtils.encrypt function to encrypt the value.
 *
 * @param key The encryption key as a Sha256DigestableKey.
 * @return The encrypted value as a Value.Encrypted.
 */
inline fun Value.Plain.encrypt(detail: EncryptionSpec, key: Sha256DigestableKey) =
    EncryptionUtils.encrypt(detail, key, this)

/**
 * Decrypts the value.
 *
 * This function uses the EncryptionUtils.decrypt function to decrypt the value.
 *
 * @param key The encryption key as a Sha256DigestableKey.
 * @return The decrypted value as a Value.Plain.
 */
inline fun Value.Encrypted.decrypt(detail: EncryptionSpec, key: Sha256DigestableKey) =
    EncryptionUtils.decrypt(detail, key, this)
