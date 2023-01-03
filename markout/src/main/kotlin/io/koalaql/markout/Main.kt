package io.koalaql.markout

import io.koalaql.markout.output.Output
import io.koalaql.markout.output.OutputDirectory
import io.koalaql.markout.output.OutputFile
import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.Path

fun deleteTree(path: Path) {
    Files.walkFileTree(path, object : SimpleFileVisitor<Path>() {
        override fun visitFile(file: Path, attrs: BasicFileAttributes?): FileVisitResult {
            Files.delete(file)
            return FileVisitResult.CONTINUE
        }

        override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
            Files.delete(dir)
            return FileVisitResult.CONTINUE
        }
    })
}

fun Output.write(path: Path) {
    fun attempt() {
        when (this) {
            is OutputDirectory -> {
                Files.createDirectory(path)

                entries().forEach { (name, output) ->
                    output.write(path.resolve(name))
                }
            }
            is OutputFile -> writeTo(Files.newOutputStream(path))
        }
    }

    try {
        attempt()
    } catch (ex: IOException) {
        deleteTree(path)
        attempt()
    }
}

fun main() {
    OutputDirectory {
        mapOf(
            "test-dir" to OutputDirectory {
                mapOf(
                    "file.txt" to OutputFile { out ->
                        out.writer().use {
                            it.append("test content")
                        }
                    }
                )
            }
        )
    }.write(Path("inner"))

    println("Test")
}