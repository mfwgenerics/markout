pluginManagement {
    includeBuild("build-logic")
}

includeBuild("markout-plugin")
includeBuild("markout-docusaurus-plugin")

include("markout")
include("markout-markdown")
include("markout-docusaurus")
include("markout-github-workflows-kt")
include("readme")
