package io.koalaql.markout

import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.JavaExec
import java.util.concurrent.Callable
import io.koalaql.markout_plugin.BuildConfig
import org.gradle.api.*
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile

open class MarkoutExecTask: JavaExec() {
    @Internal
    val markoutBuildDir = project
        .buildDir
        .toPath()
        .resolve("markout")

    @OutputFile
    val outputPath = project.buildDir.toPath().resolve("markout/paths.txt")

    override fun exec() {
        markoutBuildDir.toFile().apply {
            deleteRecursively()
            mkdir()
        }

        super.exec()
    }
}

class GradlePlugin: Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        dependencies.add("api", "io.koalaql:markout:${BuildConfig.VERSION}")

        target.extensions.create("markout", MarkoutConfig::class.java)

        val configureTask = tasks.register("markoutConfigure", DefaultTask::class.java) {
            it.group = "markout"

            it.doLast {
                val ext = target.extensions.getByType(MarkoutConfig::class.java)

                checkNotNull(ext.mainClass) { "markout.mainClass was not set" }
            }
        }

        fun execTask(name: String, builder: (MarkoutExecTask) -> Unit) = tasks
            .register(name, MarkoutExecTask::class.java) {
                val ext = target.extensions.getByType(MarkoutConfig::class.java)

                it.group = "markout"

                it.classpath = project
                    .files()
                    .from(Callable { project
                        .extensions
                        .getByType(JavaPluginExtension::class.java)
                        .sourceSets.getByName("main")
                        .runtimeClasspath
                    })

                it.environment("MARKOUT_BUILD_DIR", "${it.markoutBuildDir}")
                it.environment("MARKOUT_PATH", (ext.rootDir ?: rootDir).absolutePath)

                if (ext.mainClass != null) it.mainClass.set(ext.mainClass)

                builder(it)

                it.dependsOn(configureTask)
            }

        val checkTask = execTask("markoutCheck") {
            it.description = "Check that Markout generated files are up-to-date."
            it.environment("MARKOUT_MODE", "expect")
        }

        execTask("markout") {
            it.description = "Generate and clean Markout files."
            it.environment("MARKOUT_MODE", "apply")
        }

        tasks
            .matching { it.name == "check" }
            .configureEach { it.dependsOn(checkTask) }
    }
}