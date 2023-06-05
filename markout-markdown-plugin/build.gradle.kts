plugins {
    id("publish-plugin")
}

dependencies {
    implementation(libs.kapshot.plugin.gradle)
    implementation("io.koalaql:markout-plugin:${project.version}")
}

gradlePlugin {
    website.set("https://github.com/mfwgenerics/markout")
    vcsUrl.set("https://github.com/mfwgenerics/markout.git")

    plugins {
        create("markoutPlugin") {
            id = "io.koalaql.markout-markdown"
            displayName = "Markout Markdown Plugin"
            description = "Plugin Support for Markout Markdown Generation and Code Capture"
            implementationClass = "io.koalaql.markout.markdown.GradlePlugin"

            tags.set(listOf("kotlin", "markout", "markdown", "jvm", "documentation"))
        }
    }
}