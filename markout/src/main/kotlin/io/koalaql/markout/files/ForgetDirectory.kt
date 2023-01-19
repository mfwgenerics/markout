package io.koalaql.markout.files

import java.nio.file.Files
import java.nio.file.Path

object ForgetDirectory: FileAction {
    private fun isEmpty(dir: Path) =
        Files.newDirectoryStream(dir).use { directory -> !directory.iterator().hasNext() }

    override fun perform(path: Path) {
        if (isEmpty(path)) Files.delete(path)
    }
}