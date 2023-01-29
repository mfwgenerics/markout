import io.koalaql.markout.markout
import org.junit.Test
import kotlin.io.path.Path
import kotlin.io.path.deleteIfExists
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlin.test.assertEquals

class ApplyModeTests {
    @Test
    fun `untracked files are never overwritten`() {
        val untrackedContents = "test generated file"

        val rootDir = Path("./test-data/untracked")
            .apply {
                resolve("untracked.txt").writeText(untrackedContents)
                resolve(".markout").deleteIfExists()
            }

        repeat(3) { ix ->
            try {
                markout(rootDir) {
                    file("untracked.txt", "changed contents")
                }

                assert(false) { "failed to fail on run #${ix+1}" }
            } catch (ex: IllegalStateException) {
                assertEquals("test-data/untracked/untracked.txt already exists", ex.message)
            }

            assertEquals(
                untrackedContents,
                rootDir.resolve("untracked.txt").readText(),
                message = "run #${ix+1}:"
            )
        }
    }
}