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
package dev.shreyaspatil.bytemask.android.impl

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.Build
import androidx.annotation.RequiresApi
import dev.shreyaspatil.bytemask.core.EncryptionKeyProvider
import dev.shreyaspatil.bytemask.core.encryption.Sha256DigestableKey
import java.security.MessageDigest

/**
 * A class that provides the SHA-256 digest of the app's signing key.
 *
 * This class implements the AppSigningKeyInfoProvider interface.
 *
 * @property context The application context.
 */
internal class AndroidAppSigningSha256AsEncryptionKeyProvider(
    private val context: Context
) : EncryptionKeyProvider {

    private val sha256 =
        Sha256DigestableKey(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                getSha256SignatureApi28Impl()
            } else {
                getSha256SignaturePreApi28Impl()
            }
                .uppercase()
        )

    /**
     * Returns the SHA-256 digest of the app's signing key.
     *
     * This function checks the Android version and uses the appropriate method to retrieve the
     * app's signing key. The signing key is then hashed using SHA-256 and returned as a
     * Sha256DigestableKey.
     *
     * @return The SHA-256 digest of the app's signing key as a Sha256DigestableKey.
     */
    override fun get(): Sha256DigestableKey {
        return sha256
    }

    /**
     * Retrieves the SHA-256 digest of the app's signing key for Android versions below API 28.
     *
     * @return The SHA-256 digest of the app's signing key as a string.
     */
    @Suppress("DEPRECATION")
    private fun getSha256SignaturePreApi28Impl(): String {
        val packageInfo =
            context.packageManager.getPackageInfo(
                context.packageName, PackageManager.GET_SIGNATURES
            )
        val signatures = packageInfo.signatures
        return retrieveSha256(signatures)
    }

    /**
     * Retrieves the SHA-256 digest of the app's signing key for Android versions API 28 and above.
     *
     * This function uses the GET_SIGNING_CERTIFICATES flag, which is available from API 28.
     *
     * @return The SHA-256 digest of the app's signing key as a string.
     */
    @RequiresApi(Build.VERSION_CODES.P)
    private fun getSha256SignatureApi28Impl(): String {
        val packageInfo =
            context.packageManager.getPackageInfo(
                context.packageName, PackageManager.GET_SIGNING_CERTIFICATES
            )
        val signatures = packageInfo.signingInfo.apkContentsSigners
        return retrieveSha256(signatures)
    }

    /**
     * Retrieves the SHA-256 digest of the given signatures.
     *
     * This function hashes each signature using SHA-256 and concatenates the results into a string.
     *
     * @param signatures The signatures to hash.
     * @return The SHA-256 digest of the signatures as a string.
     */
    private fun retrieveSha256(signatures: Array<Signature>): String {
        val md = MessageDigest.getInstance("SHA-256")
        for (signature in signatures) {
            md.update(signature.toByteArray())
        }
        val digest = md.digest()
        val hexString = StringBuilder()
        for (byte in digest) {
            hexString.append(String.format("%02X", byte))
        }
        return hexString.toString()
    }
}
