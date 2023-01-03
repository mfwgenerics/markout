package io.koalaql.markout

import io.koalaql.markout.output.Output
import io.koalaql.markout.output.OutputDirectory
import io.koalaql.markout.output.OutputFile
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.io.path.writeText


private val METADATA_FILE_NAME = Path(".markout")

fun isEmpty(dir: Path) =
    Files.newDirectoryStream(dir).use { directory -> !directory.iterator().hasNext() }

fun validMetadataPath(dir: Path, path: String): Path? {
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

fun cleanDirectory(dir: Path) {
    metadataPaths(dir).forEach { path ->
        if (Files.isDirectory(path)) {
            cleanDirectory(path)

            if (isEmpty(path)) Files.delete(path)
        } else {
            Files.deleteIfExists(path)
        }
    }
}

fun Output.write(path: Path) {
    when (this) {
        is OutputDirectory -> {
            if (!Files.isDirectory(path)) {
                Files.createDirectory(path)
            }

            val entries = entries()

            /* write metadata first for graceful crash recovery */
            path.resolve(METADATA_FILE_NAME).writeText(entries.keys.joinToString(
                separator = "\n",
                postfix = "\n"
            ))

            entries.forEach { (name, output) ->
                output.write(path.resolve(name))
            }
        }
        is OutputFile -> {
            check (Files.notExists(path)) {
                "$path already exists as a user created file"
            }

            writeTo(Files.newOutputStream(path))
        }
    }
}

fun main() {
    cleanDirectory(Path(".."))

    markout {
        directory("docs") {
            file("EXAMPLE.md", """
                This is an example
            """.trimIndent())
        }

        file("README.md", """
            Markout is a library for generating markdown directories from Kotlin
            
            [Example](docs/EXAMPLE.md)
        """.trimIndent())
    }.write(Path(".."))
}