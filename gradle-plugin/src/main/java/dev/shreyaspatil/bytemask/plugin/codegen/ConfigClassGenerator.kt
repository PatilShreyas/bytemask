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

import dev.shreyaspatil.bytemask.core.encryption.EncryptionSpec
import dev.shreyaspatil.bytemask.core.encryption.Sha256DigestableKey
import dev.shreyaspatil.bytemask.core.encryption.asPlainValue
import dev.shreyaspatil.bytemask.core.encryption.encrypt
import dev.shreyaspatil.bytemask.plugin.util.camelCase
import dev.shreyaspatil.bytemask.plugin.util.normalize
import java.io.File

/** Generates a Kotlin class file with properties and values. */
internal class ConfigClassGenerator(
    private val applicationId: String,
    private val className: String,
    private val propertyAndValuesProvider: PropertyAndValuesProvider,
    private val outputDir: File,
    private val encryptionDetail: EncryptionDetail?
) {
    fun generate(): File {
        val className = className.normalize()
        val declarationProperties = getDeclarationProperties()
        val helperMethods = getHelperMethods()

        return createClassKtFile(className).also {
            it.writeText(
                """
            |package $applicationId
            |
            |object $className {
            |$declarationProperties    
            |$helperMethods
            |}
            """
                    .trimMargin()
            )
        }
    }

    /**
     * Creates a Kotlin file with the given [className].
     *
     * @param className Name of the class.
     */
    private fun createClassKtFile(className: String) =
        applicationId
            .replace(".", "/")
            .let { packagePath -> outputDir.resolve(packagePath).also { it.mkdirs() } }
            .resolve("$className.kt")

    private fun getHelperMethods() = buildString {
        if (encryptionDetail == null) return@buildString

        val (encryptionSpec, _) = encryptionDetail

        // The reason for introducing separate function `buildString` here is because as a part of
        // code optimization by R8, it converts bytes to String already in the code which exposes
        // the string representation of the bytes because `String()` is an inline function in Kotlin
        // SDK. So making a non-inline function here helps in preventing this exposure of encrypted
        // strings.
        appendLine(
            """
            |   // Encryption Algorithm: ${encryptionSpec.algorithm}
            |   private val _bytemaskEncryptionAlgorithm = ${encryptionSpec.algorithm.asByteArrayDeclaration()}
            |   // Encryption Transformation: ${encryptionSpec.transformation}
            |   private val _bytemaskEncryptionTransformation = ${encryptionSpec.transformation.asByteArrayDeclaration()}
            |   
            |   private val encryptionSpec by lazy { 
            |       dev.shreyaspatil.bytemask.core.encryption.EncryptionSpec(
            |           algorithm = buildString(_bytemaskEncryptionAlgorithm),
            |           transformation = buildString(_bytemaskEncryptionTransformation)
            |       )
            |   }
            |   
            |   private fun unmask(bytes: ByteArray): Lazy<String> = lazy {
            |       dev.shreyaspatil.bytemask.core.Bytemask.getInstance().unmask(encryptionSpec, buildString(bytes))
            |   }
            |   
            |   private fun buildString(bytes: ByteArray): String = java.lang.String(bytes, Charsets.UTF_8) as String
            """
                .trimMargin()
        )
    }

    /**
     * Generates a property declaration with encryption if [encryptionDetail] is not null.
     * Otherwise, it generates a lazy property.
     *
     * ```kt
     * // 1. With Encryption
     * @JvmStatic
     * @get:JvmName("property")
     * val property by unmask(byteArrayOf(1, 2, 3, 4, 5))
     *
     * // 2. Without Encryption
     * @JvmStatic
     * @get:JvmName("property")
     * val property by lazy { "value" }
     * ```
     */
    private fun getDeclarationProperties() = buildString {
        propertyAndValuesProvider.getAsMap().forEach { (property, value) ->
            appendLine(
                """
                        |
                        |   /**
                        |    * $value
                        |    */
                        |   @JvmStatic
                        |   @get:JvmName("${property.camelCase()}")
                        |   ${propertyDeclaration(property, value)}
                        """
                    .trimMargin()
            )
        }
    }

    /**
     * Generates a property declaration with encryption if [encryptionDetail] is not null.
     * Otherwise, it generates a lazy property.
     *
     * ```kt
     * // 1. With Encryption
     * val property by unmask(byteArrayOf(1, 2, 3, 4, 5))
     *
     * // 2. Without Encryption
     * val property by lazy { "value" }
     * ```
     */
    private fun propertyDeclaration(property: String, value: String): String {
        return if (encryptionDetail != null) {
            """
            |   val $property by unmask(${encryptAsBytes(value, encryptionDetail)})
            """
                .trimIndent()
        } else {
            """
            |   val $property by lazy { "$value" }
            """
                .trimIndent()
        }
    }

    /**
     * Encrypts the [value] as bytes and returns it as a byte array declaration.
     *
     * ```kt
     * byteArrayOf(1, 2, 3, 4, 5)
     * ```
     */
    private fun encryptAsBytes(value: String, encryptionDetail: EncryptionDetail) =
        value
            .asPlainValue()
            .encrypt(encryptionDetail.encryptionSpec, encryptionDetail.encryptionKey)
            .value
            .asByteArrayDeclaration()

    /**
     * Converts the string to a byte array declaration.
     *
     * ```kt
     * byteArrayOf(1, 2, 3, 4, 5)
     * ```
     */
    private fun String.asByteArrayDeclaration() =
        "byteArrayOf(${encodeToByteArray().joinToString(", ")})"

    data class EncryptionDetail(
        val encryptionSpec: EncryptionSpec,
        val encryptionKey: Sha256DigestableKey
    )
}
