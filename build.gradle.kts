tasks.register("check") {
    dependsOn(gradle.includedBuilds.map { it.task(":check") })
}

tasks.register("publish") {
    dependsOn(listOf(
        gradle.includedBuild("markout-plugin"),
        gradle.includedBuild("markout-docusaurus-plugin")
    ).map { it.task(":publishPlugins") })

    dependsOn(listOf(
        gradle.includedBuild("markout"),
        gradle.includedBuild("markout-markdown"),
        gradle.includedBuild("markout-docusaurus")
    ).map { it.task(":publish") })
}