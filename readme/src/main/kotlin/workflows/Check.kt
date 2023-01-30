package workflows

import io.koalaql.markout.Markout
import io.koalaql.markout.workflow
import it.krzeminski.githubactions.actions.actions.CheckoutV3
import it.krzeminski.githubactions.actions.actions.SetupJavaV3
import it.krzeminski.githubactions.actions.gradle.WrapperValidationActionV1
import it.krzeminski.githubactions.domain.RunnerType
import it.krzeminski.githubactions.domain.triggers.PullRequest
import it.krzeminski.githubactions.domain.triggers.Push

fun Markout.checkWorkflow() = workflow("check",
    name = "Build and check",
    on = listOf(Push(), PullRequest()),
) {
    job("build", runsOn = RunnerType.UbuntuLatest) {
        uses(CheckoutV3())
        uses(WrapperValidationActionV1())
        uses(SetupJavaV3(
            javaVersion = "19",
            distribution = SetupJavaV3.Distribution.Temurin
        ))
        run("./gradlew check")
    }
}