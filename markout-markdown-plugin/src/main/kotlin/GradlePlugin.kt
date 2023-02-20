package io.koalaql.markout.markdown

import io.koalaql.markout_markdown_plugin.BuildConfig
import org.gradle.api.Plugin
import org.gradle.api.Project

class GradlePlugin: Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        dependencies.add("api", "io.koalaql:markout-markdown:${BuildConfig.VERSION}")
        dependencies.add("api", "io.koalaql:markout-docusaurus:${BuildConfig.VERSION}")

        with(plugins) {
            apply("com.github.node-gradle.node")
            apply("io.koalaql.markout")
            apply("io.koalaql.kapshot-plugin")
        }

        Unit
    }
}