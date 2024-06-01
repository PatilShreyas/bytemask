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

import com.android.build.api.dsl.ApkSigningConfig
import dev.shreyaspatil.bytemask.core.EncryptionKeyProvider
import dev.shreyaspatil.bytemask.core.encryption.Sha256DigestableKey
import java.security.KeyStore
import java.security.MessageDigest
import java.security.cert.Certificate
import java.security.cert.X509Certificate

/** Provides [EncryptionKeyProvider] for a variant. */
class VariantSigningKeyProvider(
    private val config: ApkSigningConfig?,
    private val variantName: String,
) : EncryptionKeyProvider {

    override fun get(): Sha256DigestableKey {
        checkNotNull(config) { errorMessage("Signing config not found for variant") }
        checkNotNull(config.storeFile) { errorMessage("Keystore file not found") }
        checkNotNull(config.storePassword) { errorMessage("Keystore password not found") }
        checkNotNull(config.keyAlias) { errorMessage("Key alias not found") }
        checkNotNull(config.keyPassword) { errorMessage("Key password not found") }

        val ks = KeyStore.getInstance("JKS")
        ks.load(config.storeFile!!.inputStream(), config.storePassword?.toCharArray())

        val entry: KeyStore.PrivateKeyEntry =
            ks.getEntry(
                config.keyAlias,
                KeyStore.PasswordProtection(config.keyPassword?.toCharArray())
            ) as KeyStore.PrivateKeyEntry

        val certificate = entry.certificate as X509Certificate
        return Sha256DigestableKey(getFingerprint(certificate, "SHA-256"))
    }

    /** Returns the [Certificate] fingerprint as returned by `keytool`. */
    private fun getFingerprint(cert: Certificate, hashAlgorithm: String): String {
        val digest: MessageDigest = MessageDigest.getInstance(hashAlgorithm)
        return formatSha256(digest.digest(cert.encoded))
    }

    /** Formats the SHA-256 [ByteArray] to a [String]. */
    private fun formatSha256(value: ByteArray): String {
        val sb = StringBuilder()
        val len = value.size
        for (i in 0 until len) {
            val num = value[i].toInt() and 0xff
            if (num < 0x10) {
                sb.append('0')
            }
            sb.append(num.toString(16))
        }
        return sb.toString().uppercase()
    }

    private val errorSuffix =
        "Error occurred while configuring Bytemask for variant '$variantName':"

    private fun errorMessage(message: String): String {
        return "$errorSuffix $message"
    }
}
