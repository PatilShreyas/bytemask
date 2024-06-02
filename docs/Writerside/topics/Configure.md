# Configure Plugin

In the module where plugin is applied, you can configure the plugin with following options:

## Configuration option

| Parameter                   | Description                                      | Default value       |
|-----------------------------|--------------------------------------------------|---------------------|
| `defaultPropertiesFileName` | The `.properties` file to read the strings from. | bytemask.properties |
| `className`                 | Class name for the generated class               | BytemaskConfig      |

### Variant configuration option

| Parameter             | Description                                                                                                                                                                                                          | Default value                                                  |
|-----------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------|
| `enableEncryption`    | Whether to enable encryption for the current variant or not.                                                                                                                                                         | `false`                                                        |
| `encryptionKeySource` | The source of encryption key. Possible sources: <br>**1. Signing Info:** Reads the signing info (SHA-256) directly from the signing config specified in the project <br>**2. Key**: Encrypts with the specified key. | -                                                              |
| `encryptionSpec`      | The specification for encryption.                                                                                                                                                                                    | Algorithm = "AES" <br> Transformation = "AES/CBC/PKCS5Padding" |

## Usage Examples

### Configure custom file name and class name

```Kotlin
bytemaskConfig {
    // Strings to read from
    defaultPropertiesFileName.set("bytemask.properties")
    
    // Class name for the generated class
    className.set("BytemaskConfig")
}
```

### Configure for the "release" variant with signing config

```Kotlin
android {
    signingConfigs { 
        create("release") { 
            // Configure signing
        } 
    }
}
bytemaskConfig {
    // ...
    configure("release") {
        enableEncryption.set(true)
        
        // This will pick signing info directly from the `android.signingConfigs`.
        encryptionKeySource.set(KeySource.SigningConfig(name = "release"))
    }
}
```

### Provide a custom encryption key directly without depending on signing config info

```Kotlin
bytemaskConfig {
    // ...
    configure("release") {
        encryptionKeySource.set(KeySource.Key("encryption_key_here"))
    }
}
```

### Providing encryption specification

```Kotlin
bytemaskConfig {
    // ...
    configure("release") {
        encryptionSpec.set(EncryptionSpec(algorithm = "AES", transformation = "AES/GCM/NoPadding"))
    }
}
```

### Configure for multiple flavours

```Kotlin
android {
    flavorDimensions += "version"
    productFlavors {
        create("free") { ... }
        create("paid") { ... }
    }
}

bytemaskConfig {
    configure("freeDebug") {}
    configure("freeRelease") {}
    configure("paidDebug") {}
    configure("paidRelease") {}
}
```
