package io.koalaql.markout.files

import java.nio.file.Path

object NoAction: FileAction {
    override fun perform(path: Path) { }
}