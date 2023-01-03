package io.koalaql.markout

import io.koalaql.markout.output.Output
import io.koalaql.markout.output.OutputDirectory
import io.koalaql.markout.output.OutputFile
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.io.path.writeText


private const val METADATA_PATH = ".markout"

fun isEmpty(dir: Path) =
    Files.newDirectoryStream(dir).use { directory -> !directory.iterator().hasNext() }

/* order is important here: metadata path should be the last to be deleted to allow crash recovery */
fun metadataPaths(dir: Path): Sequence<Path> =
    try {
        val metadata = dir.resolve(METADATA_PATH)

        metadata
            .readText()
            .splitToSequence("\n")
            .filter { it.isNotBlank() }
            .map { dir.resolve(it) } /* TODO prevent escaping to parent directory */
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
            path.resolve(METADATA_PATH).writeText(entries.keys.joinToString(
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
    cleanDirectory(Path("inner"))

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