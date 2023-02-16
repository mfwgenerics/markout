plugins {
    id("publish-plugin")
}

dependencies {
    implementation("com.github.node-gradle:gradle-node-plugin:3.5.1")
    implementation("io.koalaql:kapshot-plugin-gradle:0.1.1")
    implementation("io.koalaql:markout-plugin")
}

pluginBundle {
    website = "https://github.com/mfwgenerics/markout"
    vcsUrl = "https://github.com/mfwgenerics/markout.git"
    tags = listOf("kotlin", "markout", "markdown", "jvm", "documentation")
}

gradlePlugin {
    plugins {
        create("markoutPlugin") {
            id = "io.koalaql.markout-docusaurus"
            displayName = "Markout Docusaurus Plugin"
            description = "Plugin Support for Markout Powered Docusaurus Sites"
            implementationClass = "io.koalaql.markout.docusaurus.GradlePlugin"
        }
    }
}