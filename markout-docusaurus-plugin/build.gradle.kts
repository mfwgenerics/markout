plugins {
    id("publish-plugin")
}

dependencies {
    implementation("com.github.node-gradle:gradle-node-plugin:3.5.1")
    implementation("io.koalaql:markout-markdown-plugin:${project.version}")
}

gradlePlugin {
    website.set("https://github.com/mfwgenerics/markout")
    vcsUrl.set("https://github.com/mfwgenerics/markout.git")

    plugins {
        create("markoutPlugin") {
            id = "io.koalaql.markout-docusaurus"
            displayName = "Markout Docusaurus Plugin"
            description = "Plugin Support for Markout Powered Docusaurus Sites"
            implementationClass = "io.koalaql.markout.docusaurus.GradlePlugin"

            tags.set(listOf("kotlin", "markout", "markdown", "jvm", "documentation"))
        }
    }
}