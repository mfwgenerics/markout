plugins {
    id("conventions")

    id("java-gradle-plugin")
    id("com.gradle.plugin-publish") version "1.1.0"
}

dependencies {
    implementation(kotlin("gradle-plugin-api"))
}

pluginBundle {
    website = "https://github.com/mfwgenerics/markout"
    vcsUrl = "https://github.com/mfwgenerics/markout.git"
    tags = listOf("kotlin", "markout", "markdown", "jvm", "documentation")
}

gradlePlugin {
    plugins {
        create("markoutPlugin") {
            id = "io.koalaql.markout"
            displayName = "Markout Plugin"
            description = "Plugin Support for Markout: an executable documentation platform and Markdown DSL for Kotlin"
            implementationClass = "io.koalaql.markout.GradlePlugin"
        }
    }
}