import io.koalaql.markout.markout
import io.koalaql.markout.md.markdown
import kotlin.io.path.Path

fun main() = markout(Path("..")) {
    markdown("README") {
        t("Markout is a library for generating markdown files and directories from Kotlin")
    }
}