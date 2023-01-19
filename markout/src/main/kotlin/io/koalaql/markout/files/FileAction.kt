package io.koalaql.markout.files

import java.nio.file.Path

sealed interface FileAction {
    fun perform(path: Path)
}