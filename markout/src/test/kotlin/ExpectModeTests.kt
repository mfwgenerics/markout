import io.koalaql.markout.ExecutionMode
import io.koalaql.markout.markout
import kotlin.io.path.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ExpectModeTests {
    private inline fun expectFailure(
        expected: String,
        block: () -> Unit
    ) {
        try {
            block()
            assert(false) { "expect mode shouldn't have succeeded!" }
        } catch (ex: IllegalStateException) {
            assertEquals(expected, ex.message)
        }
    }

    @Test
    fun `missing directory`() {
        expectFailure("expected\ttest-data/missing-dir/case1") {
            markout(
                Path("./test-data/missing-dir"),
                ExecutionMode.EXPECT
            ) {
                directory("case1") {
                    file("test.txt", "TEST CONTENT")
                }
            }
        }
    }

    @Test
    fun `one present one missing`() {
        expectFailure(
            """
            unexpected	test-data/missing-present/case2/untracked.txt
            expected	test-data/missing-present/case2/test.txt
            """.trimIndent()
        ) {
            markout(
                Path("./test-data/missing-present"),
                ExecutionMode.EXPECT
            ) {
                directory("case2") {
                    file("test.txt", "TEST CONTENT")
                }
            }
        }
    }

    @Test
    fun `missing one file`() {
        expectFailure("unexpected\ttest-data/tree/file0.txt") {
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
        expectFailure(
            """
            mismatch	test-data/tree/dir/nested/file2.txt
            mismatch	test-data/tree/dir/file1.txt
            mismatch	test-data/tree/file0.txt
            """.trimIndent(),
        ) {
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
        }
    }

    @Test
    fun `treating file as directory, directory as file`() {
        expectFailure(
            """
            mismatch	test-data/tree/dir
            mismatch	test-data/tree/file0.txt
            """.trimIndent()
        ) {
            markout(
                Path("./test-data/tree"),
                ExecutionMode.EXPECT
            ) {
                file("dir", "")

                directory("file0.txt") { }
            }
        }
    }

    @Test
    fun `unicode smoke test`() {
        markout(
            Path("./test-data/unicode"),
            ExecutionMode.EXPECT
        ) {
            file("unicode.txt", "\uD83D\uDE80\uD83C\uDF3B\uD83C\uDF55\uD83C\uDF89\uD83D\uDE97\uD83C\uDF0A\uD83C\uDF3A\uD83C\uDFB8\uD83D\uDD25\uD83C\uDF08")
        }
    }
}