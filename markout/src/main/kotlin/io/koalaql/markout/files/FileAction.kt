package io.koalaql.markout.files

import io.koalaql.markout.Diff
import java.nio.file.Path

sealed interface FileAction {
    fun perform(path: Path)
    fun expect(path: Path, out: MutableList<Diff>): Boolean
}