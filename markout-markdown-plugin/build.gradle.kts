plugins {
    id("publish-plugin")
}

dependencies {
    implementation("io.koalaql:kapshot-plugin-gradle:0.1.1")
    implementation("io.koalaql:markout-plugin:${project.version}")
}

pluginBundle {
    website = "https://github.com/mfwgenerics/markout"
    vcsUrl = "https://github.com/mfwgenerics/markout.git"
    tags = listOf("kotlin", "markout", "markdown", "jvm", "documentation")
}

gradlePlugin {
    plugins {
        create("markoutPlugin") {
            id = "io.koalaql.markout-markdown"
            displayName = "Markout Markdown Plugin"
            description = "Plugin Support for Markout Markdown Generation and Code Capture"
            implementationClass = "io.koalaql.markout.markdown.GradlePlugin"
        }
    }
}