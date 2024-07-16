# Getting Started

The Gradle plugin can be only applied to the following Android modules:

- Application (`com.android.application`)
- Dynamic Feature Module (`com.android.dynamic-feature`)

> Library modules (`com.android.library`) can be used in more than one app, so libraries are not applicable for this 
> use case.
{style="note"}

## Apply plugin

Apply the plugin in the app module.

### Using Plugin DSL

```Kotlin
plugins {
    id("dev.shreyaspatil.bytemask.plugin") version "1.0.0-beta01"
}
```

### OR using Legacy plugin application

```Kotlin
buildscript {
  repositories {
    maven {
      url = uri("https://plugins.gradle.org/m2/")
    }
  }
  dependencies {
    classpath("dev.shreyaspatil.bytemask:gradle-plugin:1.0.0-beta01")
  }
}

apply(plugin = "dev.shreyaspatil.bytemask.plugin")
```
