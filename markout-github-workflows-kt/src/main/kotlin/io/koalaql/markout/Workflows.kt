package io.koalaql.markout

import io.github.typesafegithub.workflows.domain.Concurrency
import io.github.typesafegithub.workflows.domain.triggers.Trigger
import io.github.typesafegithub.workflows.dsl.WorkflowBuilder
import io.github.typesafegithub.workflows.dsl.workflow as workflowImpl
import io.github.typesafegithub.workflows.yaml.toYaml
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