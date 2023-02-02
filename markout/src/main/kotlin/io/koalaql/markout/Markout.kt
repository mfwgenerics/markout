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
import kotlin.io.path.*

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
fun metadataPaths(dir: Path): Sequence<Path> {
    if (!Files.isDirectory(dir)) return emptySequence()

    return try {
        val metadata = dir.resolve(METADATA_FILE_NAME)

        metadata
            .readText()
            .splitToSequence("\n")
            .mapNotNull { validMetadataPath(dir, it) }
            .plusElement(metadata) /* plusElement rather than plus bc Path : Iterable<Path> */
    } catch (ex: NoSuchFileException) {
        emptySequence()
    }
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


val MODE_ENV_VAR = "MARKOUT_MODE"
val PATH_ENV_VAR = "MARKOUT_PATH"

private fun executionModeProperty(): ExecutionMode {
    return when (val value = System.getenv(MODE_ENV_VAR)) {
        null, "", "apply" -> ExecutionMode.APPLY
        "expect" -> ExecutionMode.EXPECT
        else -> error("unexpected value `$value` for property $MODE_ENV_VAR")
    }
}

fun actionableFiles(output: OutputDirectory, dir: Path): ActionableFiles {
    val paths = linkedMapOf<Path, FileAction>()

    fun markDeletions(path: Path) {
        metadataPaths(path).forEach {
            markDeletions(it)
        }

        paths[path] = DeleteFile
    }

    fun reconcile(output: OutputDirectory, dir: Path) {
        val entries = output.entries().toMutableMap()
        val remaining = entries.toMutableMap()

        metadataPaths(dir).forEach { path ->
            when (val entry = remaining.remove(path.name)) {
                is OutputDirectory -> {
                    paths[path] = DeclareDirectory

                    reconcile(entry, path)
                }
                else -> {
                    markDeletions(path)

                    if (entry is OutputFile) {
                        paths[path] = WriteToFile(entry)
                    }
                }
            }
        }

        remaining.iterator().let {
            it.forEach { (name, _) ->
                val path = dir.resolve(name)

                if (Files.exists(path) && !Files.isDirectory(path)) {
                    it.remove()
                    entries.remove(name)

                    paths[path] = AlreadyExistsError
                }
            }
        }

        val metadataPath = dir.resolve(METADATA_FILE_NAME)

        paths[metadataPath] = WriteMetadata(entries.keys)

        remaining.forEach { (name, entry) ->
            val path = dir.resolve(name)

            when (entry) {
                is OutputDirectory -> {
                    paths[path] = DeclareDirectory

                    reconcile(entry, path)
                }
                is OutputFile -> {
                    paths[path] = WriteToFile(entry)
                }
            }
        }
    }

    reconcile(output, dir)

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
        ExecutionMode.APPLY -> {
            actions.perform().forEach { println(it) }
        }
        ExecutionMode.EXPECT -> {
            val diffs = actions.expect()

            if (diffs.isNotEmpty()) error(diffs.joinToString("\n"))
        }
    }
}

fun markout(
    mode: ExecutionMode = executionModeProperty(),
    builder: Markout.() -> Unit
) = markout(
    checkNotNull(System.getenv(PATH_ENV_VAR)
        ?.takeIf { it.isNotBlank() }
        ?.let { Path(it) }) {
        "Missing path. Specify a path explicitly or set the $PATH_ENV_VAR environment variable"
    },
    mode,
    builder
)