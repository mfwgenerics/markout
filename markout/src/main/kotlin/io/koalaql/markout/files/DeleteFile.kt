package io.koalaql.markout.files

import java.nio.file.Files
import java.nio.file.Path

object DeleteFile: FileAction {
    override fun perform(path: Path) {
        Files.deleteIfExists(path)
    }
}