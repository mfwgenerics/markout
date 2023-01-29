import io.koalaql.markout.markout
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import kotlin.io.path.*
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

    @JvmField
    @Rule
    val tempFolder = TemporaryFolder()

    @Test
    fun `files created, removed and overwritten`() {
        val rootDir = Path(tempFolder.root.path)

        // fresh state
        markout(rootDir) {
            file("modify-me.txt", "unmodified")
            file("delete-me.txt", "undeleted")

            directory("nested") {
                file("modify-me.txt", "unmodified")
                file("delete-me.txt", "undeleted")
            }
        }

        rootDir
            .apply {
                assertEquals(resolve(".markout").readText(), "modify-me.txt\ndelete-me.txt\nnested\n")
                assert(resolve("create-me.txt").notExists())
                assertEquals(resolve("modify-me.txt").readText(), "unmodified")
                assertEquals(resolve("delete-me.txt").readText(), "undeleted")

                resolve("nested").apply {
                    assert(exists() && isDirectory())

                    assertEquals(resolve(".markout").readText(), "modify-me.txt\ndelete-me.txt\n")
                    assert(resolve("create-me.txt").notExists())
                    assertEquals(resolve("modify-me.txt").readText(), "unmodified")
                    assertEquals(resolve("delete-me.txt").readText(), "undeleted")
                }
            }

        markout(rootDir) {
            file("create-me.txt", "created")
            file("modify-me.txt", "modified")

            directory("nested") {
                file("create-me.txt", "created")
                file("modify-me.txt", "modified")
            }
        }

        rootDir
            .apply {
                assertEquals(resolve(".markout").readText(), "create-me.txt\nmodify-me.txt\nnested\n")
                assertEquals(resolve("create-me.txt").readText(), "created")
                assertEquals(resolve("modify-me.txt").readText(), "modified")
                assert(resolve("delete-me.txt").notExists())

                resolve("nested").apply {
                    assert(exists() && isDirectory())

                    assertEquals(resolve(".markout").readText(), "create-me.txt\nmodify-me.txt\n")
                    assertEquals(resolve("create-me.txt").readText(), "created")
                    assertEquals(resolve("modify-me.txt").readText(), "modified")
                    assert(resolve("delete-me.txt").notExists())
                }
            }
    }
}