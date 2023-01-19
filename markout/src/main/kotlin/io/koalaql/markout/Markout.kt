package io.koalaql.markout

import io.koalaql.markout.files.*
import io.koalaql.markout.output.Output
import io.koalaql.markout.output.OutputDirectory
import io.koalaql.markout.output.OutputFile
import java.io.InputStream
import java.io.OutputStream
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.readText

@MarkoutDsl
interface Markout {
    @MarkoutDsl
    fun directory(name: String, builder: Markout.() -> Unit)
    @MarkoutDsl
    fun file(name: String, contents: String)
}

fun buildOutput(builder: Markout.() -> Unit): OutputDirectory = OutputDirectory {
    val entries = linkedMapOf<String, Output>()

    object : Markout {
        override fun directory(name: String, builder: Markout.() -> Unit) {
            entries[name] = buildOutput(builder)
        }

        override fun file(name: String, contents: String) {
            entries[name] = OutputFile { out ->
                out.writer().use { it.append(contents) }
            }
        }
    }.builder()

    entries
}

val METADATA_FILE_NAME = Path(".markout")

private fun validMetadataPath(dir: Path, path: String): Path? {
    if (path.isBlank()) return null

    return dir
        .resolve(path)
        .normalize()
        .takeIf { it.parent == dir }
}

/* order is important here: metadata path should be the last to be deleted to allow crash recovery */
fun metadataPaths(dir: Path): Sequence<Path> =
    try {
        val metadata = dir.resolve(METADATA_FILE_NAME)

        metadata
            .readText()
            .splitToSequence("\n")
            .mapNotNull { validMetadataPath(dir, it) }
            .plusElement(metadata) /* plusElement rather than plus bc Path : Iterable<Path> */
    } catch (ex: NoSuchFileException) {
        emptySequence()
    }

enum class DiffType {
    MISMATCH,
    UNTRACKED,
    EXPECTED,
    UNEXPECTED
}

data class Diff(
    val type: DiffType,
    val path: Path
) {
    override fun toString(): String =
        "${"$type".lowercase()}\t$path"
}

class StreamMatcher(
    private val input: InputStream
): OutputStream() {
    private var matches = true

    fun matched(): Boolean {
        return matches && input.read() == -1
    }

    override fun write(byte: Int) {
        matches = matches && input.read() == byte and 0xFF
    }
}

val MODE_ENV_VAR = "MARKOUT_MODE"

private fun executionModeProperty(): ExecutionMode {
    return when (val value = System.getenv(MODE_ENV_VAR)) {
        null, "", "apply" -> ExecutionMode.APPLY
        "expect" -> ExecutionMode.EXPECT
        else -> error("unexpected value `$value` for property $MODE_ENV_VAR")
    }
}

fun actionableFiles(output: Output, dir: Path): ActionableFiles {
    val tracked = linkedSetOf<Path>()
    val paths = linkedMapOf<Path, FileAction>()

    fun track(dir: Path) {
        metadataPaths(dir).forEach { path ->
            if (Files.isDirectory(path)) {
                track(path)
            }

            tracked.add(path)
        }
    }

    fun write(output: Output, path: Path) {
        when (output) {
            is OutputDirectory -> {
                paths[path] = DeclareDirectory(path in tracked)

                val entries = output.entries()

                /* write metadata first for graceful crash recovery */
                val metadataPath = path.resolve(METADATA_FILE_NAME)

                paths[metadataPath] = WriteMetadata(entries.keys)

                entries.forEach { (name, output) ->
                    write(output, path.resolve(name))
                }
            }
            is OutputFile -> {
                paths[path] = WriteToFile(
                    tracked = paths.containsKey(path),
                    output
                )
            }
        }
    }

    track(dir)

    tracked.forEach { path ->
        paths[path] = DeleteFile
    }

    write(output, dir)

    return ActionableFiles(paths)
}

fun markout(
    path: Path,
    mode: ExecutionMode = executionModeProperty(),
    builder: Markout.() -> Unit
) {
    val output = buildOutput(builder)

    val normalized = path.normalize()

    val actions = actionableFiles(output, normalized)

    when (mode) {
        ExecutionMode.APPLY -> actions.perform()
        ExecutionMode.EXPECT -> {
            val diffs = actions.expect()

            if (diffs.isNotEmpty()) error(diffs.joinToString("\n"))
        }
    }
}
