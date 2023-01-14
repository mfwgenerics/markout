import io.koalaql.markout.ExecutionMode
import io.koalaql.markout.markout
import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertEquals

class ExpectModeTests {
    @Test
    fun `missing directory`() {
        try {
            markout(
                Path("./test-data/missing-dir"),
                ExecutionMode.EXPECT
            ) {
                directory("case1") {
                    file("test.txt", "TEST CONTENT")
                }
            }
        } catch (ex: IllegalStateException) {
            assertEquals(
                "expected\ttest-data/missing-dir/case1",
                ex.message
            )
        }
    }

    @Test
    fun `one present one missing`() {
        try {
            markout(
                Path("./test-data/missing-present"),
                ExecutionMode.EXPECT
            ) {
                directory("case2") {
                    file("test.txt", "TEST CONTENT")
                }
            }
        } catch (ex: IllegalStateException) {
            assertEquals(
                """
                expected	test-data/missing-present/case2/test.txt
                unexpected	test-data/missing-present/case2/untracked.txt
                """.trimIndent(),
                ex.message
            )
        }
    }

    @Test
    fun `missing one file`() {
        try {
            markout(
                Path("./test-data/tree"),
                ExecutionMode.EXPECT
            ) {
                directory("dir") {
                    directory("nested") {
                        file("empty.txt", "")
                        file("file2.txt", "content...")
                        file("file3.txt", "content!")
                    }

                    file("file1.txt", "content\n\n")
                }
            }
        } catch (ex: IllegalStateException) {
            assertEquals("unexpected\ttest-data/tree/file0.txt", ex.message)
        }
    }

    @Test
    fun `matches perfectly`() {
        markout(
            Path("./test-data/tree"),
            ExecutionMode.EXPECT
        ) {
            directory("dir") {
                directory("nested") {
                    file("empty.txt", "")
                    file("file2.txt", "content...")
                    file("file3.txt", "content!")
                }

                file("file1.txt", "content\n\n")
            }

            file("file0.txt", "file0")
        }
    }

    @Test
    fun `contents mismatch`() {
        try {
            markout(
                Path("./test-data/tree"),
                ExecutionMode.EXPECT
            ) {
                directory("dir") {
                    directory("nested") {
                        file("empty.txt", "")
                        file("file2.txt", "content doesn't match")
                        file("file3.txt", "content!")
                    }

                    file("file1.txt", "content")
                }

                file("file0.txt", "")
            }
        } catch (ex: IllegalStateException) {
            assertEquals(
                """
                mismatch	test-data/tree/dir/nested/file2.txt
                mismatch	test-data/tree/dir/file1.txt
                mismatch	test-data/tree/file0.txt
                """.trimIndent(),
                ex.message
            )
        }
    }

    @Test
    fun `treating file as directory, directory as file`() {
        try {
            markout(
                Path("./test-data/tree"),
                ExecutionMode.EXPECT
            ) {
                file("dir", "")

                directory("file0.txt") { }
            }
        } catch (ex: IllegalStateException) {
            assertEquals(
                """
                mismatch	test-data/tree/dir
                mismatch	test-data/tree/file0.txt
                """.trimIndent(),
                ex.message
            )
        }
    }
}