package io.koalaql.markout

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.DefaultTask
import org.gradle.api.plugins.ApplicationPlugin

class GradlePlugin: Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        plugins.apply(ApplicationPlugin::class.java)

        target.extensions.create("markout", MarkoutConfig::class.java)

        val checkTask = tasks.register("markoutCheck", DefaultTask::class.java) {
            it.description =
                "Check that generated files are up-to-date. " +
                "This task will succeed if running the markout task would result in no changes. " +
                "This task does not update or change files"
            it.group = "markout"
        }

        tasks.named("check").configure {
            it.dependsOn(checkTask)
        }
        Unit
    }
}