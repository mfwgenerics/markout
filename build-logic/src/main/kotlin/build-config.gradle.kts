plugins {
    id("conventions")
    id("com.github.gmazzo.buildconfig")
}

buildConfig {
    buildConfigField("String", "VERSION", "\"${project.version}\"")
}