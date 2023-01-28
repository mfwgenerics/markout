package workflows

import jobs
import yaml

val RELEASE_WORKFLOW = yaml {
    "name" - "Publish plugins and dependencies"
    "on" - {
        "release" - {
            "types" - "[published]"
            "branches" - {
                li { raw("main") }
            }
        }
    }

    jobs {
        val repo = "staging_repository" - {
            "runs-on" - "ubuntu-latest"
            "name" - "Create staging repository"
            "outputs" - {
                "repository_id" - "\${{ steps.create.outputs.repository_id }}"
            }

            "steps" - {
                li {
                    "id" - "create"
                    "uses" - "nexus-actions/create-nexus-staging-repo@main"
                    "with" - {
                        "username" - secret("SONATYPE_USERNAME")
                        "password" - secret("SONATYPE_PASSWORD")
                        "staging_profile_id" - secret("SONATYPE_PROFILE_ID")
                        "description" - "${github("repository")}/${github("workflow")}#${github("run_number")}"
                        "base_url" - "https://s01.oss.sonatype.org/service/local/"
                    }
                }
            }
        }

        "publish" - {
            "runs-on" - "ubuntu-latest"
            "name" - "Publish"
            "needs" - "staging_repository"
            "steps" - {
                li {
                    "name" - "Checkout"
                    "uses" - "actions/checkout@v2"
                }

                li {
                    "name" - "Configure JDK"
                    "uses" - "actions/setup-java@v1"
                    "with" - {
                        "java-version" - "19"
                    }
                }

                li {
                    "name" - "Publish to Maven Central"
                    "run" - "./gradlew publish"
                    "env" - {
                        "REPOSITORY_ID" - repo.outputs["repository_id"]
                        "SONATYPE_USERNAME" - secret("SONATYPE_USERNAME")
                        "SONATYPE_PASSWORD" - secret("SONATYPE_PASSWORD")
                        "GPG_PRIVATE_KEY" - secret("GPG_PRIVATE_KEY")
                        "GPG_PRIVATE_PASSWORD" - secret("GPG_PRIVATE_PASSWORD")
                    }
                }

                li {
                    "name" - "Publish Gradle plugin"
                    "env" - {
                        "GRADLE_PUBLISH_KEY" - secret("GRADLE_PUBLISH_KEY")
                        "GRADLE_PUBLISH_SECRET" - secret("GRADLE_PUBLISH_SECRET")
                    }
                    "run" - "./gradlew :markout-plugin:publishPlugins"
                }
            }
        }
    }
}