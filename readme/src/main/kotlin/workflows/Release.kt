package workflows

import io.koalaql.markout.Markout
import io.koalaql.markout.workflow
import it.krzeminski.githubactions.actions.actions.CheckoutV3
import it.krzeminski.githubactions.actions.actions.SetupJavaV3
import it.krzeminski.githubactions.actions.gradle.WrapperValidationActionV1
import it.krzeminski.githubactions.domain.RunnerType
import it.krzeminski.githubactions.domain.triggers.PullRequest
import it.krzeminski.githubactions.domain.triggers.Push
import it.krzeminski.githubactions.domain.triggers.Release
import it.krzeminski.githubactions.dsl.expressions.Contexts
import it.krzeminski.githubactions.dsl.expressions.expr

fun Markout.releaseYml() = workflow("release2",
    name = "Publish plugins and dependencies",
    on = listOf(Push(), Release(
        mapOf(
            "types" to listOf("published"),
            "branches" to listOf("main")
        ),
    )),
) {
    /*

    name: Publish plugins and dependencies
on:
  release:
    types: [published]
    branches:
      - main
jobs:
  staging_repository:
    runs-on: ubuntu-latest
    name: Create staging repository
    outputs:
      repository_id: ${{ steps.create.outputs.repository_id }}
    steps:
      - id: create
        uses: nexus-actions/create-nexus-staging-repo@main
        with:
          username: ${{ secrets.SONATYPE_USERNAME }}
          password: ${{ secrets.SONATYPE_PASSWORD }}
          staging_profile_id: ${{ secrets.SONATYPE_PROFILE_ID }}
          description: ${{ github.repository }}/${{ github.workflow }}#${{ github.run_number }}
          base_url: https://s01.oss.sonatype.org/service/local/
     */

    val staging = job("staging_repository", runsOn = RunnerType.UbuntuLatest) {
        uses(CheckoutV3())
        uses(WrapperValidationActionV1())
        uses(
            SetupJavaV3(
            javaVersion = "19",
            distribution = SetupJavaV3.Distribution.Temurin
        )
        )
        run("./gradlew check")
    }

    job("publish",
        runsOn = RunnerType.UbuntuLatest,
        needs = listOf(staging)
    ) {
        uses(CheckoutV3())
        uses(
            SetupJavaV3(
                javaVersion = "19",
                distribution = SetupJavaV3.Distribution.Temurin
            )
        )

        run(
            name = "Publish to Maven Central",
            command = "./gradlew publish",
            env = linkedMapOf(
                "REPOSITORY_ID" to expr { "needs.staging_repository.outputs.repository_id" },
                "SONATYPE_USERNAME" to expr { secrets.getValue("SONATYPE_USERNAME") },
                "SONATYPE_PASSWORD" to expr { secrets.getValue("SONATYPE_PASSWORD") },
                "GPG_PRIVATE_KEY" to expr { secrets.getValue("GPG_PRIVATE_KEY") },
                "GPG_PRIVATE_PASSWORD" to expr { secrets.getValue("GPG_PRIVATE_PASSWORD") }
            )
        )

        run(
            name = "Publish to Maven Central",
            command = "./gradlew :markout-plugin:publishPlugins",
            env = linkedMapOf(
                "GRADLE_PUBLISH_KEY"  to expr { secrets.getValue("GRADLE_PUBLISH_KEY") },
                "GRADLE_PUBLISH_SECRET" to expr { secrets.getValue("GRADLE_PUBLISH_SECRET") }
            )
        )
    }
}
