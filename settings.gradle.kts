pluginManagement {
    includeBuild("build-logic")
}

includeBuild("markout-plugin")

include("markout")
include("markout-docusaurus")
include("markout-github-workflows-kt")
include("readme")
