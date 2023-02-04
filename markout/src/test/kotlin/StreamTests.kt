import io.koalaql.markout.stream.StreamMatcher
import io.koalaql.markout.stream.StreamMode
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertEquals

class StreamTests {
    @JvmField
    @Rule
    val temp = TemporaryFolder()

    @Test
    fun `stream checks files`() {
        val rootDir = Path(temp.root.path)

        rootDir.apply {
            val match = resolve("match.txt")

            match.writeText("this should match")

            assert(
                with(StreamMatcher(Files.newByteChannel(match, StandardOpenOption.READ))) {
                    use { write("this should match".toByteArray()) }
                    matched()
                }
            )

            assert(
                with(StreamMatcher(Files.newByteChannel(match, StandardOpenOption.READ))) {
                    use { write("this shouldn't match".toByteArray()) }
                    !matched()
                }
            )
        }
    }

    @Test
    fun `stream overwrites files`() {
        val rootDir = Path(temp.root.path)

        rootDir.apply {
            val overwrite0 = resolve("overwrite0.txt")

            overwrite0.writeText("this should be overwritten")

            assert(
                with(StreamMatcher(
                    Files.newByteChannel(overwrite0, StandardOpenOption.READ, StandardOpenOption.WRITE),
                    StreamMode.OVERWRITE
                )) {
                    use { write("overwritten!".toByteArray()) }
                    !matched()
                }
            )

            assertEquals(
                "overwritten!",
                overwrite0.readText()
            )

            assert(
                with(StreamMatcher(
                    Files.newByteChannel(overwrite0, StandardOpenOption.READ, StandardOpenOption.WRITE),
                    StreamMode.OVERWRITE
                )) {
                    use { write("overwritten with much longer text".toByteArray()) }
                    !matched()
                }
            )

            assertEquals(
                "overwritten with much longer text",
                overwrite0.readText()
            )

            assert(
                with(StreamMatcher(
                    /* leave out WRITE option since overwrite shouldn't actually write in match */
                    Files.newByteChannel(overwrite0, StandardOpenOption.READ),
                    StreamMode.OVERWRITE
                )) {
                    use { write("overwritten with much longer text".toByteArray()) }
                    matched()
                }
            )
        }
    }
}