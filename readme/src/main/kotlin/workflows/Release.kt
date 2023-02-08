package workflows

import io.koalaql.markout.Markout
import io.koalaql.markout.workflow
import it.krzeminski.githubactions.actions.ActionWithOutputs
import it.krzeminski.githubactions.actions.actions.CheckoutV3
import it.krzeminski.githubactions.actions.actions.SetupJavaV3
import it.krzeminski.githubactions.actions.gradle.WrapperValidationActionV1
import it.krzeminski.githubactions.domain.JobOutputs
import it.krzeminski.githubactions.domain.RunnerType
import it.krzeminski.githubactions.domain.triggers.PullRequest
import it.krzeminski.githubactions.domain.triggers.Push
import it.krzeminski.githubactions.domain.triggers.Release
import it.krzeminski.githubactions.dsl.expressions.Contexts
import it.krzeminski.githubactions.dsl.expressions.expr

class CreateNexusStagingRepo(
    val username: String,
    val password: String,
    val stagingProfileId: String,
    val description: String,
    val baseUrl: String
) : ActionWithOutputs<CreateNexusStagingRepo.Outputs>(
    "nexus-actions",
    "create-nexus-staging-repo",
    "main"
) {
    override fun toYamlArguments() = linkedMapOf(
        "username" to username,
        "password" to password,
        "staging_profile_id" to stagingProfileId,
        "description" to description,
        "base_url" to baseUrl
    )

    override fun buildOutputObject(stepId: String) = Outputs(stepId)

    class Outputs(private val stepId: String) {
        val repositoryId: String = "steps.$stepId.outputs.repository_id"

        operator fun get(outputName: String) = "steps.$stepId.outputs.$outputName"
    }
}

fun Markout.releaseYml() = workflow("release",
    name = "Publish plugins and dependencies",
    on = listOf(Release(
        mapOf(
            "types" to listOf("published"),
            "branches" to listOf("main")
        ),
    )),
) {
    val staging = job("staging_repository",
        name = "Create staging repository",
        runsOn = RunnerType.UbuntuLatest,
        outputs = object : JobOutputs() {
            var repository_id by output()
        }
    ) {
        val step = uses(CreateNexusStagingRepo(
            username = expr { secrets.getValue("SONATYPE_USERNAME") },
            password =  expr { secrets.getValue("SONATYPE_PASSWORD") },
            stagingProfileId = expr { secrets.getValue("SONATYPE_PROFILE_ID") },
            description = "${expr { github.repository }}/${expr { github.workflow }}#${expr { github.run_number }}",
            baseUrl = "https://s01.oss.sonatype.org/service/local/"
        ))

        jobOutputs.repository_id = step.outputs.repositoryId
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
                "REPOSITORY_ID" to expr { staging.outputs.repository_id },
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
