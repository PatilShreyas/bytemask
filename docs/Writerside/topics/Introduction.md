# Introduction

Bytemask is an Android Gradle Plugin that ***masks*** your secret strings for the app in the source code making it
difficult to extract from reverse engineering.

## Understand the flow

### 1. Plugin - Encrypt strings at the compile time
The plugin is customizable. The default implementation of plugin encrypts strings with the public app signing info 
(SHA-256) and generated the config class with the encrypted value in the byte array format in the app's source.

### 2. App - Decrypt strings in the runtime 

At runtime, the app retrieves values from the configuration class. These values are decrypted using the SHA-256 hash 
of the app's signing certificate. This hash is fetched in the runtime using the `PackageManager` API in Android.

> This security measure helps prevent tampering. If someone tries to reverse engineer the app and rebuild it with 
> their own code (_and a different signing key_), the app will crash at runtime. This is because the configuration keys 
> are **encrypted using the app's original signing key**, and an **invalid key in the modified APK** will be a cause
> to fail the decryption.

See the flow for better understanding:

![Bytemask Flow.svg](Flow.svg)

