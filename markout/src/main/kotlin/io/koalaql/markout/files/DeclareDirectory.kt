package io.koalaql.markout.files

import java.nio.file.Files
import java.nio.file.Path

data class DeclareDirectory(
    private val prerequisite: FileAction
): FileAction {
    override fun perform(path: Path) {
        prerequisite.perform(path)
        if (!Files.isDirectory(path)) Files.createDirectory(path)
    }
}