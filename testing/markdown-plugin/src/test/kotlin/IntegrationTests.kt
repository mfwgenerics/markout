import io.koalaql.markout.md.markdown
import kotlin.test.Test
import kotlin.test.assertEquals

class IntegrationTests {
    @Test
    fun `integrates with kapshot`() {
        assertEquals(
            """
                ```kotlin
                h1("test")

                1 + 2
                ```

                # test
            """.trimIndent(),
            markdown {
                val block = code {
                    h1("test")

                    1 + 2
                }

                assertEquals(3, block.invoke())
            }
        )
    }
}