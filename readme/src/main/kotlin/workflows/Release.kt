package workflows

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

    "jobs" - {
        "staging_repository" - {
            "runs-on" - "ubuntu-latest"
            "name" - "Create staging repository"
            "outputs" - {
                "repository_id" - "\${{ steps.create.outputs.repository_id }}"
            }

            "steps" - {
                li {
                    raw("""
                                    id: create
                                    uses: nexus-actions/create-nexus-staging-repo@main
                                    with:
                                      username: ${'$'}{{ secrets.SONATYPE_USERNAME }}
                                      password: ${'$'}{{ secrets.SONATYPE_PASSWORD }}
                                      staging_profile_id: ${'$'}{{ secrets.SONATYPE_PROFILE_ID }}
                                      description: ${'$'}{{ github.repository }}/${'$'}{{ github.workflow }}#${'$'}{{ github.run_number }}
                                      base_url: https://s01.oss.sonatype.org/service/local/
                                """.trimIndent())
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
                        "REPOSITORY_ID" - "\${{ needs.staging_repository.outputs.repository_id }}"
                        "SONATYPE_USERNAME" - "\${{ secrets.SONATYPE_USERNAME }}"
                        "SONATYPE_PASSWORD" - "\${{ secrets.SONATYPE_PASSWORD }}"
                        "GPG_PRIVATE_KEY" - "\${{ secrets.GPG_PRIVATE_KEY }}"
                        "GPG_PRIVATE_PASSWORD" - "\${{ secrets.GPG_PRIVATE_PASSWORD }}"
                    }
                }

                li {
                    "name" - "Publish Gradle plugin"
                    "env" - {
                        "GRADLE_PUBLISH_KEY" - "\${{ secrets.GRADLE_PUBLISH_KEY }}"
                        "GRADLE_PUBLISH_SECRET" - "\${{ secrets.GRADLE_PUBLISH_SECRET }}"
                    }
                    "run" - "./gradlew :markout-plugin:publishPlugins"
                }
            }
        }
    }
}