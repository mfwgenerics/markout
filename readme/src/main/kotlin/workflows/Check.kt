package workflows

import io.koalaql.markout.Markout
import io.koalaql.markout.workflow
import io.github.typesafegithub.workflows.actions.actions.CheckoutV3
import io.github.typesafegithub.workflows.actions.actions.SetupJavaV3
import io.github.typesafegithub.workflows.actions.gradle.WrapperValidationActionV1
import io.github.typesafegithub.workflows.domain.RunnerType
import io.github.typesafegithub.workflows.domain.triggers.PullRequest
import io.github.typesafegithub.workflows.domain.triggers.Push

fun Markout.checkYml() = workflow("check",
    name = "Build and check",
    on = listOf(Push(), PullRequest()),
) {
    job(id = "build", runsOn = RunnerType.UbuntuLatest) {
        uses(action = CheckoutV3())
        uses(action = WrapperValidationActionV1())
        uses(action = SetupJavaV3(
            javaVersion = "19",
            distribution = SetupJavaV3.Distribution.Temurin
        ))
        run(command = "./gradlew check")
    }
}