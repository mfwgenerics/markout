import io.koalaql.markout.MarkoutDsl

interface Outputs {
    @MarkoutDsl
    fun secret(name: String): String =
        "\${{ secrets.$name }}"
}

@MarkoutDsl
interface JobBuilder: YamlBuilder {

}

@MarkoutDsl
interface JobsBuilder {
    @MarkoutDsl
    operator fun String.minus(block: context(Outputs) JobBuilder.() -> Unit)
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
            override fun String.minus(block: context(Outputs) JobBuilder.() -> Unit) {
                with(outer) {
                    this@minus - {
                        block(object : Outputs {
                        }, JobBuilderImpl(this))
                    }
                }
            }
        }.build()
    }
}