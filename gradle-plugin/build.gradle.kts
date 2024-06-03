import com.android.build.gradle.internal.tasks.factory.dependsOn

val GROUP: String by project
val VERSION_NAME: String by project
val POM_NAME: String by project
val POM_DESCRIPTION: String by project

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    alias(libs.plugins.gradle.plugin.publish)
}

group = GROUP

version = VERSION_NAME

dependencies {
    compileOnly(gradleApi())
    compileOnly(kotlin("stdlib"))
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.android.gradle.plugin)

    implementation(project(":bytemask-core"))
}

tasks.getByName<Test>("test") { useJUnitPlatform() }

gradlePlugin {
    website.set("https://github.com/PatilShreyas/bytemask")
    vcsUrl.set("https://github.com/PatilShreyas/bytemask.git")
    plugins {
        create("reportGenPlugin") {
            id = "dev.shreyaspatil.bytemask.plugin"
            displayName = POM_NAME
            description = POM_DESCRIPTION
            implementationClass = "dev.shreyaspatil.bytemask.plugin.BytemaskPlugin"
            tags.set(listOf("android", "kotlin", "security"))
        }
    }
}

val generateVersionClassTask =
    tasks.register("generateVersionClass") {
        doLast {
            val version =
                project.findProperty("VERSION_NAME")?.toString() ?: error("VERSION_NAME is not set")
            val file =
                File(
                    project.projectDir,
                    "build/generated/src/main/kotlin/dev/shreyaspatil/bytemask/plugin/Version.kt"
                )
            file.parentFile.mkdirs()
            file.writeText(
                """
                package dev.shreyaspatil.bytemask.plugin
                
                object PluginConfig {
                    const val VERSION = "$version"
                }
                """
                    .trimIndent()
            )
        }
    }

tasks.compileKotlin.dependsOn(generateVersionClassTask)

sourceSets { main { java { srcDir("build/generated/src/main/kotlin") } } }
