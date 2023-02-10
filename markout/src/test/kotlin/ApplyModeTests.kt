import io.koalaql.markout.markout
import io.koalaql.markout.name.UntrackedName
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import kotlin.io.path.*
import kotlin.test.assertEquals

class ApplyModeTests {
    @JvmField
    @Rule
    val temp = TemporaryFolder()

    @Test
    fun `existing files are never overwritten by tracked write`() {
        val untrackedContents = "test generated file"

        val rootDir = Path(temp.root.path)
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
                assertEquals("${temp.root.path}/untracked.txt already exists", ex.message)
            }

            assertEquals(
                untrackedContents,
                rootDir.resolve("untracked.txt").readText(),
                message = "run #${ix+1}:"
            )
        }
    }

    @Test
    fun `explicit untracked files can be overwritten`() {
        val untrackedContents = "test generated file"

        val rootDir = Path(temp.root.path)
            .apply {
                resolve("untracked.txt").writeText(untrackedContents)
                resolve(".markout").deleteIfExists()
            }

        markout(rootDir) {
            file(UntrackedName("untracked.txt"), "changed contents")
        }

        assertEquals(
            "changed contents",
            rootDir.resolve("untracked.txt").readText()
        )
    }

    @Test
    fun `untracked files are created but not removed`() {
        val rootDir = Path(temp.root.path)

        markout(rootDir) {
            file("tracked.txt", "I won't exist soon")
            file(UntrackedName("untracked.txt"), "I will still exist")
        }

        markout(rootDir) { }

        assertEquals(
            "I will still exist",
            rootDir.resolve("untracked.txt").readText()
        )

        assert(rootDir.resolve("tracked.txt").notExists())
    }

    @Test
    fun `files created, removed and overwritten`() {
        val rootDir = Path(temp.root.path)

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

    @Test
    fun `can use existing directory`() {
        val rootDir = Path(temp.root.path).apply {
            resolve("existing-dir").createDirectory()
        }

        markout(rootDir) {
            directory("existing-dir") {
                file("new-file.txt", "successfully created")
            }
        }

        rootDir.apply {
            assertEquals(resolve(".markout").readText(), "existing-dir\n")

            resolve("existing-dir").apply {
                assertEquals(resolve(".markout").readText(), "new-file.txt\n")

                assert(isDirectory())
                assertEquals("successfully created", resolve("new-file.txt").readText())
            }
        }
    }

    @Test
    fun `directories are cleaned up`() {
        val rootDir = Path(temp.root.path).apply {
        }

        markout(rootDir) {
            directory("existing-dir") {
                file("new-file.txt", "successfully created")
            }
        }

        rootDir.apply {
            assertEquals(resolve(".markout").readText(), "existing-dir\n")

            resolve("existing-dir").apply {
                assertEquals(resolve(".markout").readText(), "new-file.txt\n")

                assert(isDirectory())
                assertEquals("successfully created", resolve("new-file.txt").readText())
            }
        }

        markout(rootDir) { }

        rootDir.apply {
            resolve("existing-dir").apply {
                assert(notExists())
            }
        }
    }
}