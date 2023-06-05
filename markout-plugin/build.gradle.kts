plugins {
    id("publish-plugin")
}

gradlePlugin {
    website.set("https://github.com/mfwgenerics/markout")
    vcsUrl.set("https://github.com/mfwgenerics/markout.git")

    plugins {
        create("markoutPlugin") {
            id = "io.koalaql.markout"
            displayName = "Markout Plugin"
            description = "Plugin Support for Markout: an executable documentation platform and Markdown DSL for Kotlin"
            implementationClass = "io.koalaql.markout.GradlePlugin"

            tags.set(listOf("kotlin", "markout", "markdown", "jvm", "documentation"))
        }
    }
}