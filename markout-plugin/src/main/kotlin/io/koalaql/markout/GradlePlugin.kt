package io.koalaql.markout

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.JavaExec
import java.util.concurrent.Callable

class GradlePlugin: Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        target.extensions.create("markout", MarkoutConfig::class.java)

        val checkTask = tasks.register("markoutCheck", JavaExec::class.java) {
            it.group = "markout"

            it.description = "Check that Markout generated files are up-to-date."

            it.classpath = project
                .files()
                .from(Callable { project
                    .extensions
                    .getByType(JavaPluginExtension::class.java)
                    .sourceSets.getByName("main")
                    .runtimeClasspath
                })

            it.environment("MARKOUT_MODE", "expect")

            it.mainClass.set("MainKt")
        }

        tasks.register("markout", JavaExec::class.java) {
            it.group = "markout"
            it.description = "Generate and clean Markout files."

            it.classpath = project
                .files()
                .from(Callable { project
                    .extensions
                    .getByType(JavaPluginExtension::class.java)
                    .sourceSets.getByName("main")
                    .runtimeClasspath
                })

            it.environment("MARKOUT_MODE", "apply")

            it.mainClass.set("MainKt")
        }

        tasks.named("check").configure {
            it.dependsOn(checkTask)
        }
    }
}