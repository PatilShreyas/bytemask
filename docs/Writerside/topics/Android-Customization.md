# Android Customization

## Initialization

Bytemask automatically gets initialized via App startup library at the time of launch.
So there is no need to initialize it explicitly. But if you want to control the initialization, follow these steps:

### 1. Disable the initializer

In the app's manifest, remove the initializer like this:

```XML
<provider
    android:name="androidx.startup.InitializationProvider"
    android:authorities="${applicationId}.androidx-startup"
    android:exported="false"
    tools:node="merge">

    <meta-data
        android:name="dev.shreyaspatil.bytemask.android.initializer.BytemaskInitializer"
        android:value="androidx.startup"
        tools:node="remove" />
</provider>
```

### 2. Initialize manually

#### 2.1 Initializing with default config (App signing config)

```Kotlin
class MyApp: Application() {
    override fun onCreate() {
        //...
        AndroidBytemask.init(context = this)
    }
}
```

#### 2.2 Initializing with custom encryption specification

If you've provided [custom encryption specification](Configure.md "Providing custom encryption key") for the encryption, you can configure the same key 
for decryption.

```Kotlin
class CustomEncryptionKeyProvider : EncryptionKeyProvider {
    override fun get(): Sha256DigestableKey() {
        return Sha256DigestableKey("encryption_key_here")
    }
}

class MyApp : Application() {
    override fun onCreate() {
        //...
        Bytemask.init(CustomEncryptionKeyProvider())
    }
}
```