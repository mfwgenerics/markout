package workflows

import io.koalaql.markout.Markout
import io.koalaql.markout.workflow
import it.krzeminski.githubactions.domain.triggers.PullRequest
import it.krzeminski.githubactions.domain.triggers.Push

fun Markout.checkWorkflow() = workflow("check2",
    name = "Build and check",
    on = listOf(Push(), PullRequest()),
) {

}