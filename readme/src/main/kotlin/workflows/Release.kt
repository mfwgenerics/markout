package workflows

import io.koalaql.markout.Markout
import io.koalaql.markout.workflow
import io.github.typesafegithub.workflows.actions.actions.CheckoutV3
import io.github.typesafegithub.workflows.actions.actions.SetupJavaV3
import io.github.typesafegithub.workflows.actions.gradle.WrapperValidationActionV1
import io.github.typesafegithub.workflows.domain.JobOutputs
import io.github.typesafegithub.workflows.domain.RunnerType
import io.github.typesafegithub.workflows.domain.actions.Action
import io.github.typesafegithub.workflows.domain.actions.RegularAction
import io.github.typesafegithub.workflows.domain.triggers.PullRequest
import io.github.typesafegithub.workflows.domain.triggers.Push
import io.github.typesafegithub.workflows.domain.triggers.Release
import io.github.typesafegithub.workflows.dsl.expressions.Contexts
import io.github.typesafegithub.workflows.dsl.expressions.expr

class CreateNexusStagingRepo(
    val username: String,
    val password: String,
    val stagingProfileId: String,
    val description: String,
    val baseUrl: String
) : RegularAction<CreateNexusStagingRepo.Outputs>(
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

    class Outputs(private val stepId: String) : Action.Outputs(stepId) {
        val repositoryId: String = "steps.$stepId.outputs.repository_id"
    }
}

class ReleaseNexusStagingRepo(
    val username: String,
    val password: String,
    val stagingRepoId: String,
    val baseUrl: String
): RegularAction<Action.Outputs>(
    "nexus-actions",
    "release-nexus-staging-repo",
    "main"
) {
    override fun toYamlArguments() = linkedMapOf(
        "username" to username,
        "password" to password,
        "staging_repository_id" to stagingRepoId,
        "base_url" to baseUrl
    )

    override fun buildOutputObject(stepId: String) = Outputs(stepId)

    class Outputs(private val stepId: String) : Action.Outputs(stepId) {
        val repositoryId: String = "steps.$stepId.outputs.repository_id"
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
    val staging = job(id = "staging_repository",
        name = "Create staging repository",
        runsOn = RunnerType.UbuntuLatest,
        outputs = object : JobOutputs() {
            var repository_id by output()
        }
    ) {
        val step = uses(action = CreateNexusStagingRepo(
            username = expr { secrets.getValue("SONATYPE_USERNAME") },
            password =  expr { secrets.getValue("SONATYPE_PASSWORD") },
            stagingProfileId = expr { secrets.getValue("SONATYPE_PROFILE_ID") },
            description = "${expr { github.repository }}/${expr { github.workflow }}#${expr { github.run_number }}",
            baseUrl = "https://s01.oss.sonatype.org/service/local/"
        ))

        jobOutputs.repository_id = step.outputs.repositoryId
    }

    job(id = "publish",
        runsOn = RunnerType.UbuntuLatest,
        needs = listOf(staging)
    ) {
        uses(action = CheckoutV3())
        uses(
            action = SetupJavaV3(
                javaVersion = "19",
                distribution = SetupJavaV3.Distribution.Temurin
            )
        )

        run(
            name = "Publish Plugins and Libraries",
            command = "./gradlew publish",
            env = linkedMapOf(
                "REPOSITORY_ID" to expr { staging.outputs.repository_id },
                "SONATYPE_USERNAME" to expr { secrets.getValue("SONATYPE_USERNAME") },
                "SONATYPE_PASSWORD" to expr { secrets.getValue("SONATYPE_PASSWORD") },
                "GPG_PRIVATE_KEY" to expr { secrets.getValue("GPG_PRIVATE_KEY") },
                "GPG_PRIVATE_PASSWORD" to expr { secrets.getValue("GPG_PRIVATE_PASSWORD") },
                "GRADLE_PUBLISH_KEY"  to expr { secrets.getValue("GRADLE_PUBLISH_KEY") },
                "GRADLE_PUBLISH_SECRET" to expr { secrets.getValue("GRADLE_PUBLISH_SECRET") }
            )
        )

        uses(action = ReleaseNexusStagingRepo(
            username = expr { secrets.getValue("SONATYPE_USERNAME") },
            password =  expr { secrets.getValue("SONATYPE_PASSWORD") },
            stagingRepoId = expr { staging.outputs.repository_id },
            baseUrl = "https://s01.oss.sonatype.org/service/local/"
        ))
    }
}
