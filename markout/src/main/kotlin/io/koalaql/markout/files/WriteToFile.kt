package io.koalaql.markout.files

import io.koalaql.markout.output.OutputFile
import java.nio.file.Files
import java.nio.file.Path

data class WriteToFile(
    private val overwrite: Boolean,
    private val source: OutputFile
): FileAction {
    override fun perform(path: Path) {
        check (overwrite || Files.notExists(path)) {
            "$path already exists"
        }

        source.writeTo(Files.newOutputStream(path))
    }
}
