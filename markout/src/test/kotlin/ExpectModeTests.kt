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
                "missing\ttest-data/missing-dir/case1",
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
                missing	test-data/missing-present/case2/test.txt
                present	test-data/missing-present/case2/untracked.txt
                """.trimIndent(),
                ex.message
            )
        }
    }
}