package io.koalaql.markout

import it.krzeminski.githubactions.domain.Concurrency
import it.krzeminski.githubactions.domain.triggers.Trigger
import it.krzeminski.githubactions.dsl.WorkflowBuilder
import it.krzeminski.githubactions.dsl.workflow as workflowImpl
import it.krzeminski.githubactions.yaml.toYaml
import kotlin.io.path.Path

fun Markout.workflow(
    filename: String,
    name: String,
    on: List<Trigger>,
    env: LinkedHashMap<String, String> = linkedMapOf(),
    concurrency: Concurrency? = null,
    customArguments: Map<String, Any> = mapOf(),
    block: WorkflowBuilder.() -> Unit,
) {
    val extendedFn = if (
        filename.endsWith(".yml", ignoreCase = true) ||
        filename.endsWith(".yaml", ignoreCase = true)
    ) {
        filename
    } else {
        "$filename.yml"
    }

    val wf = workflowImpl(
        name = name,
        on = on,
        env = env,
        concurrency = concurrency,
        yamlConsistencyJobCondition = null,
        _customArguments = customArguments,
        block = block
    )

    file(extendedFn, wf.toYaml(addConsistencyCheck = false))
}