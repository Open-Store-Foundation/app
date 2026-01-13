import org.gradle.api.tasks.Copy
import org.gradle.internal.extensions.stdlib.capitalized
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack

plugins {
    kotlin("multiplatform")
}

project.afterEvaluate {
    project.tasks.withType<KotlinWebpack>().forEach { webpackTask ->
        if (!webpackTask.name.contains("Webpack")) {
            return@forEach
        }

        val mode = webpackTask.mode.code.capitalized()
        val extensionTaskName = "assemble${mode}Extension"

        println("Configuring extension task: $extensionTaskName")

        project.tasks.register<Copy>(extensionTaskName) {
            dependsOn(webpackTask)
            group = "build"
            description = "Assembles the Browser Extension (${webpackTask.name})"

            from(webpackTask.outputDirectory)

            from(project.layout.buildDirectory.dir("processedResources/js/main")) {
                include("**/*.js")
                include("**/*.html")
                include("**/*.json")
                include("**/*.xml")
                include("**/*.ttf")
                include("composeResources")
            }

            into(project.layout.buildDirectory.dir("extension/unpacked${mode}"))
        }
    }
}