import io.koalaql.markout.MarkoutDsl

interface Outputs {
    @MarkoutDsl
    fun secret(name: String): String =
        "\${{ secrets.$name }}"

    @MarkoutDsl
    fun github(name: String): String =
        "\${{ github.$name }}"
}

@MarkoutDsl
interface JobBuilder: YamlBuilder {

}

interface WorkflowJobOutputs {
    operator fun get(name: String): String
}

interface WorkflowJob {
    val outputs: WorkflowJobOutputs
}

@MarkoutDsl
interface JobsBuilder {
    @MarkoutDsl
    operator fun String.minus(block: context(Outputs) JobBuilder.() -> Unit): WorkflowJob
}

private class JobBuilderImpl(
    private val yaml: YamlBuilder
): JobBuilder, YamlBuilder by yaml {

}

@MarkoutDsl
fun YamlBuilder.jobs(build: JobsBuilder.() -> Unit) {
    "jobs" - {
        val outer = this

        object : JobsBuilder {
            override fun String.minus(block: context(Outputs) JobBuilder.() -> Unit): WorkflowJob {
                val jobName = this

                with(outer) {
                    this@minus - {
                        block(object : Outputs {
                        }, JobBuilderImpl(this))
                    }
                }

                return object : WorkflowJob {
                    override val outputs: WorkflowJobOutputs = object : WorkflowJobOutputs {
                        override fun get(name: String): String {
                            return "\${{ needs.$jobName.outputs.$name }}"
                        }
                    }
                }
            }
        }.build()
    }
}